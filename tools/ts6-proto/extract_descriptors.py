#!/usr/bin/env python3
"""Recover embedded protobuf FileDescriptorProtos from a C++ protobuf binary.

Generated C++ protobuf code embeds each .proto file's serialized
FileDescriptorProto verbatim (descriptor_table_protodef_*). Those blobs start
with field 1 (name), i.e. tag 0x0A, a varint length, then a path ending in
".proto". We locate those anchors, walk the wire format to find where the
message ends, then parse and collect them into a FileDescriptorSet.
"""
import sys
from google.protobuf import descriptor_pb2

# FileDescriptorProto field numbers that actually exist (1..13).
VALID_FIELDS = set(range(1, 14))


def read_varint(buf, i):
    val = 0
    shift = 0
    while i < len(buf):
        b = buf[i]
        val |= (b & 0x7F) << shift
        i += 1
        if not (b & 0x80):
            return val, i
        shift += 7
        if shift > 63:
            return None, i
    return None, i


def message_end(buf, start):
    """Walk top-level records from `start`, return offset just past the last
    record that still looks like a FileDescriptorProto field."""
    i = start
    while i < len(buf):
        tag, j = read_varint(buf, i)
        if tag is None:
            break
        field, wire = tag >> 3, tag & 7
        if field not in VALID_FIELDS:
            break
        if wire == 2:
            ln, j = read_varint(buf, j)
            if ln is None or j + ln > len(buf):
                break
            j += ln
        elif wire == 0:
            v, j = read_varint(buf, j)
            if v is None:
                break
        elif wire == 5:
            j += 4
        elif wire == 1:
            j += 8
        else:
            break
        i = j
    return i


def main(path, out):
    data = open(path, 'rb').read()
    fds = descriptor_pb2.FileDescriptorSet()
    seen = set()
    pos = 0
    while True:
        idx = data.find(b'.proto', pos)
        if idx == -1:
            break
        pos = idx + 1
        # Walk backwards over the filename to the length varint and 0x0A tag.
        j = idx + 6
        k = idx
        while k > 0 and 0x20 <= data[k - 1] < 0x7F:
            k -= 1
        # k = start of the printable run; the name may start anywhere in it.
        for name_start in range(k, idx + 1):
            name_len = j - name_start
            if name_len <= 0 or name_len > 255:
                continue
            # length varint is 1 byte for <128, else 2
            if name_len < 128:
                hdr = bytes([0x0A, name_len])
            else:
                hdr = bytes([0x0A, (name_len & 0x7F) | 0x80, name_len >> 7])
            hs = name_start - len(hdr)
            if hs < 0 or data[hs:name_start] != hdr:
                continue
            end = message_end(data, j)
            blob = data[hs:end]
            fd = descriptor_pb2.FileDescriptorProto()
            try:
                fd.ParseFromString(blob)
            except Exception:
                continue
            if fd.SerializeToString() != blob:
                continue
            if not fd.name or fd.name in seen:
                continue
            seen.add(fd.name)
            fds.file.add().CopyFrom(fd)
            break

    open(out, 'wb').write(fds.SerializeToString())
    print(f"recovered {len(fds.file)} .proto files -> {out}")
    for f in sorted(fds.file, key=lambda f: f.name):
        print(f"  {f.name:45s} pkg={f.package or '-':35s} "
              f"msgs={len(f.message_type):3d} enums={len(f.enum_type):2d} "
              f"svcs={len(f.service)}")


if __name__ == '__main__':
    main(sys.argv[1], sys.argv[2])

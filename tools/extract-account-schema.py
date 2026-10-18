#!/usr/bin/env python3
"""Extract selected public-client protobuf descriptors; never execute the client.

Requires the protobuf Python package. Inputs are public distribution binaries,
not user profile databases or captured account traffic.
"""
import argparse
import hashlib
import json
import re
from pathlib import Path
from google.protobuf import descriptor_pb2, text_format

NAMES = {
    'myteamspeak_common.proto', 'myteamspeak_login.proto',
    'synchronization_common.proto', 'myteamspeak_synchronization.proto',
    'Sync_Serializing.proto', 'Account_Serializing.proto',
}


def extract(source, destination):
    data = source.read_bytes()
    destination.mkdir(parents=True, exist_ok=False)
    records = []

    def varint(pos):
        value = 0
        for shift in range(0, 70, 7):
            byte = data[pos]
            pos += 1
            value |= (byte & 127) << shift
            if byte < 128:
                return value, pos
        raise ValueError('Invalid descriptor varint')

    for match in re.finditer(rb'\x0a[\x01-\x7f][A-Za-z0-9_]+\.proto', data):
        start = match.start()
        name = match.group()[2:].decode('ascii')
        if name not in NAMES:
            continue
        pos = start
        try:
            while pos - start < 262144:
                tag, next_pos = varint(pos)
                field, wire = tag >> 3, tag & 7
                if not field or field > 15:
                    break
                pos = next_pos
                if wire == 2:
                    size, pos = varint(pos)
                    pos += size
                elif wire == 0:
                    _, pos = varint(pos)
                elif wire == 1:
                    pos += 8
                elif wire == 5:
                    pos += 4
                else:
                    raise ValueError('Unsupported descriptor wire type')
            else:
                raise ValueError('Descriptor exceeds bound')
            raw = data[start:pos]
            descriptor = descriptor_pb2.FileDescriptorProto.FromString(raw)
            if descriptor.name != name or not (descriptor.message_type or descriptor.service):
                continue
            (destination / (name + '.descriptor.pb')).write_bytes(raw)
            (destination / (name + '.txt')).write_text(text_format.MessageToString(descriptor))
            records.append({'name': name, 'sourceOffset': hex(start), 'length': len(raw),
                            'sha256': hashlib.sha256(raw).hexdigest()})
        except (ValueError, IndexError):
            continue
    manifest = {'sourceSha256': hashlib.sha256(data).hexdigest(), 'records': records,
                'limitations': 'Static embedded schema extraction; no observed requests or crypto behavior.'}
    (destination / 'manifest.json').write_text(json.dumps(manifest, indent=2) + '\n')
    if set(record['name'] for record in records) != NAMES:
        raise SystemExit('One or more expected schemas were not recovered; inspect the manifest.')
    print(json.dumps(manifest, indent=2))


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('binary', type=Path)
    parser.add_argument('new_output_directory', type=Path)
    args = parser.parse_args()
    extract(args.binary, args.new_output_directory)

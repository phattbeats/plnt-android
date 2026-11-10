#!/usr/bin/env python3
"""Locate a string in a stripped ELF and list what references it.

    python3 find.py tsserver client_protocol_format

Prints every occurrence of the literal, the C strings around it, any absolute
pointers to it (via R_X86_64_RELATIVE relocations, which is how vtables and
typeinfo reference data in a PIE), and every rip-relative reference from .text.

No binutils required — the TS6 server container has none, and neither does the
sandbox this was written in.
"""
import struct
import sys

from elf import ELF, find_all


def rip_refs(e, target, section='.text'):
    """Addresses of disp32 fields in `section` whose rip-relative target is `target`.

    This scans every byte offset rather than decoding instructions, so it can
    report a false positive when a disp32-shaped byte sequence happens to land
    inside an immediate. Disassemble the hits to confirm.
    """
    s = e.sec(section)
    base_off, base_addr, size = s['offset'], s['addr'], s['size']
    blob = e.data[base_off:base_off + size]
    hits = []
    for i in range(0, size - 4):
        disp = struct.unpack_from('<i', blob, i)[0]
        if disp and base_addr + i + 4 + disp == target:
            hits.append(base_addr + i)
    return hits


def rel_refs(e, target):
    """Addresses holding an absolute pointer to `target` (relative relocations)."""
    out = []
    for name in ('.rela.dyn', '.rela.plt'):
        s = e.sec(name)
        if s is None:
            continue
        blob = e.sec_bytes(s)
        for i in range(len(blob) // 24):
            off, info, add = struct.unpack_from('<QQq', blob, i * 24)
            if add == target and (info & 0xffffffff) == 8:
                out.append(off)
    return out


def neighbours(e, addr, count=8):
    d = e.data
    off, _ = e.addr_to_off(addr)
    out = []
    p = off
    for _ in range(count):
        p = d.rfind(b'\0', 0, p - 1) if p > 0 else 0
        if p <= 0:
            break
        out.insert(0, (e.off_to_addr(p + 1)[0], d[p + 1:d.find(b'\0', p + 1)]))
        p += 1
    out.append((addr, d[off:d.find(b'\0', off)]))
    p = d.find(b'\0', off) + 1
    for _ in range(count):
        end = d.find(b'\0', p)
        out.append((e.off_to_addr(p)[0], d[p:end]))
        p = end + 1
    return out


def main():
    if len(sys.argv) != 3:
        sys.exit(__doc__)
    e = ELF(sys.argv[1])
    needle = sys.argv[2].encode()
    hits = find_all(e.data, needle)
    if not hits:
        print('not found')
        return
    for off in hits:
        addr, sec = e.off_to_addr(off)
        if addr is None:
            continue
        print(f'== 0x{addr:x} [{sec}]')
        for a, s in neighbours(e, addr, 4):
            print(('  >> ' if a == addr else '     ') + f'0x{a:x}  {s[:110]!r}')
        for r in rel_refs(e, addr):
            print(f'     abs pointer at 0x{r:x}')
        for r in rip_refs(e, addr):
            print(f'     rip-relative ref at 0x{r:x}')


if __name__ == '__main__':
    main()

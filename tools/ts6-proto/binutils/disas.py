#!/usr/bin/env python3
"""Disassemble one function of a stripped ELF, annotating string references.

    python3 disas.py tsserver 0xbd8720            # whole containing function
    python3 disas.py tsserver 0xbf6cea 0x80 0x80  # window around an address

Function boundaries come from `.eh_frame` FDEs, so this works on a fully
stripped binary with no symbol table. Needs `pip install capstone`.
"""
import bisect
import re
import struct
import sys

import capstone

from elf import ELF

MD = capstone.Cs(capstone.CS_ARCH_X86, capstone.CS_MODE_64)


def _uleb(b, i):
    r = s = 0
    while True:
        c = b[i]
        i += 1
        r |= (c & 0x7f) << s
        if not c & 0x80:
            return r, i
        s += 7


def _sleb(b, i):
    r = s = 0
    while True:
        c = b[i]
        i += 1
        r |= (c & 0x7f) << s
        s += 7
        if not c & 0x80:
            return (r - (1 << s) if c & 0x40 else r), i


_FIXED = {0x02: ('<H', 2), 0x03: ('<I', 4), 0x04: ('<Q', 8),
          0x0a: ('<h', 2), 0x0b: ('<i', 4), 0x0c: ('<q', 8), 0x00: ('<Q', 8)}


def _read_enc(b, i, enc, pc):
    fmt = enc & 0x0f
    if fmt == 0x01:
        v, i = _uleb(b, i)
    elif fmt == 0x09:
        v, i = _sleb(b, i)
    else:
        f, n = _FIXED[fmt]
        v = struct.unpack_from(f, b, i)[0]
        i += n
    return (v + pc if enc & 0x70 == 0x10 else v), i


def functions(e):
    """[(start, length)] for every FDE in .eh_frame, sorted by start."""
    s = e.sec('.eh_frame')
    b, base = e.sec_bytes(s), s['addr']
    out, cies, i, n = [], {}, 0, len(e.sec_bytes(s))
    while i + 4 <= n:
        start = i
        ln = struct.unpack_from('<I', b, i)[0]
        i += 4
        if ln == 0:
            break
        if ln == 0xffffffff:
            ln = struct.unpack_from('<Q', b, i)[0]
            i += 8
        end = i + ln
        cie_id = struct.unpack_from('<I', b, i)[0]
        j = i + 4
        if cie_id == 0:
            ver = b[j]
            j += 1
            aug_end = b.find(b'\0', j)
            aug = b[j:aug_end].decode()
            j = aug_end + 1
            _, j = _uleb(b, j)
            _, j = _sleb(b, j)
            j = j + 1 if ver == 1 else _uleb(b, j)[1]
            fde_enc = 0
            if aug.startswith('z'):
                _, j = _uleb(b, j)
                k = j
                for c in aug[1:]:
                    if c == 'R':
                        fde_enc = b[k]
                        k += 1
                    elif c == 'P':
                        penc = b[k]
                        k += 1
                        _, k = _read_enc(b, k, penc, base + k)
                    elif c == 'L':
                        k += 1
            cies[start] = fde_enc
        else:
            enc = cies.get(i - cie_id, 0x1b)
            pc_begin, j2 = _read_enc(b, j, enc, base + j)
            pc_range, _ = _read_enc(b, j2, enc & 0x0f, 0)
            out.append((pc_begin, pc_range))
        i = end
    out.sort()
    return out


class Disassembler:
    def __init__(self, path):
        self.e = ELF(path)
        self.funcs = functions(self.e)
        self.starts = [f[0] for f in self.funcs]

    def func_of(self, addr):
        k = bisect.bisect_right(self.starts, addr) - 1
        if k < 0:
            return None
        start, ln = self.funcs[k]
        return (start, ln) if start <= addr < start + ln else None

    def string_at(self, addr, maxlen=200):
        d = self.e.data
        off, sec = self.e.addr_to_off(addr)
        if off is None or sec not in ('.rodata', '.data', '.data.rel.ro'):
            return None
        end = d.find(b'\0', off, off + maxlen)
        if end < 0 or end - off < 2:
            return None
        try:
            t = d[off:end].decode('ascii')
        except UnicodeDecodeError:
            return None
        return t if all(32 <= ord(c) < 127 for c in t) else None

    def lines(self, addr):
        f = self.func_of(addr)
        start, ln = f if f else (addr, 200)
        off, _ = self.e.addr_to_off(start)
        out = []
        for ins in MD.disasm(self.e.data[off:off + ln], start):
            line = f'0x{ins.address:x}  {ins.mnemonic:<10s} {ins.op_str}'
            m = re.search(r'rip ([+-]) (0x[0-9a-f]+)', ins.op_str)
            if m:
                delta = int(m.group(2), 16) * (1 if m.group(1) == '+' else -1)
                tgt = ins.address + ins.size + delta
                sv = self.string_at(tgt)
                line += f'   ; 0x{tgt:x}' + (f'  "{sv}"' if sv else '')
            out.append(line)
        return out


def main():
    if len(sys.argv) < 3:
        sys.exit(__doc__)
    dis = Disassembler(sys.argv[1])
    addr = int(sys.argv[2], 0)
    before = int(sys.argv[3], 0) if len(sys.argv) > 3 else None
    after = int(sys.argv[4], 0) if len(sys.argv) > 4 else 0x180
    f = dis.func_of(addr)
    print(f'# 0x{addr:x} in ' + (f'function 0x{f[0]:x} (len {f[1]})' if f else 'no known function'))
    for line in dis.lines(addr):
        a = int(line.split()[0], 16)
        if before is not None and not (addr - before <= a <= addr + after):
            continue
        print(('>>' if a == addr else '  ') + line)


if __name__ == '__main__':
    main()

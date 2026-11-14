#!/usr/bin/env python3
"""Minimal ELF64 reader + string/xref helpers for tsserver analysis (no binutils here)."""
import struct, sys, re

class ELF:
    def __init__(self, path):
        self.data = open(path, 'rb').read()
        d = self.data
        assert d[:4] == b'\x7fELF', 'not ELF'
        assert d[4] == 2, 'not ELF64'
        (self.e_type, self.e_machine, _v, self.e_entry, self.e_phoff, self.e_shoff,
         self.e_flags, self.e_ehsize, self.e_phentsize, self.e_phnum,
         self.e_shentsize, self.e_shnum, self.e_shstrndx) = struct.unpack_from('<HHIQQQIHHHHHH', d, 16)
        self.sections = []
        for i in range(self.e_shnum):
            off = self.e_shoff + i * self.e_shentsize
            (name, typ, flags, addr, offset, size, link, info, align, entsize) = \
                struct.unpack_from('<IIQQQQIIQQ', d, off)
            self.sections.append(dict(name_off=name, type=typ, flags=flags, addr=addr,
                                      offset=offset, size=size, link=link, info=info,
                                      align=align, entsize=entsize, idx=i))
        shstr = self.sections[self.e_shstrndx]
        strtab = d[shstr['offset']:shstr['offset'] + shstr['size']]
        for s in self.sections:
            end = strtab.find(b'\0', s['name_off'])
            s['name'] = strtab[s['name_off']:end].decode()

    def sec(self, name):
        for s in self.sections:
            if s['name'] == name:
                return s
        return None

    def sec_bytes(self, s):
        if s['type'] == 8:  # NOBITS
            return b''
        return self.data[s['offset']:s['offset'] + s['size']]

    def addr_to_off(self, addr):
        for s in self.sections:
            if s['type'] != 8 and s['addr'] and s['addr'] <= addr < s['addr'] + s['size']:
                return s['offset'] + (addr - s['addr']), s['name']
        return None, None

    def off_to_addr(self, off):
        for s in self.sections:
            if s['type'] != 8 and s['addr'] and s['offset'] <= off < s['offset'] + s['size']:
                return s['addr'] + (off - s['offset']), s['name']
        return None, None

    def cstr(self, addr, maxlen=400):
        off, sec = self.addr_to_off(addr)
        if off is None:
            return None
        end = self.data.find(b'\0', off, off + maxlen)
        if end < 0:
            return None
        try:
            return self.data[off:end].decode('utf-8')
        except UnicodeDecodeError:
            return None


PRINTABLE = re.compile(rb'[\x20-\x7e]{4,}')

def strings_in(data, base_off=0, minlen=4):
    for m in re.finditer(rb'[\x20-\x7e]{%d,}' % minlen, data):
        yield base_off + m.start(), m.group().decode('ascii')


def find_all(data, needle):
    out, i = [], 0
    while True:
        i = data.find(needle, i)
        if i < 0:
            break
        out.append(i)
        i += 1
    return out


if __name__ == '__main__':
    e = ELF(sys.argv[1])
    print('sections:')
    for s in e.sections:
        print(f"  {s['name']:24s} type={s['type']:<3d} addr=0x{s['addr']:012x} off=0x{s['offset']:08x} size={s['size']}")

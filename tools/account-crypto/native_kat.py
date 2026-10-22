"""Emulate ONLY selected crypto functions from the public artifact; no syscalls/network.
All inputs are synthetic. Unresolved imports/instructions fail closed.
"""
import sys,struct,hashlib,json,os
from pathlib import Path
from elftools.elf.elffile import ELFFile
from unicorn import Uc,UC_ARCH_X86,UC_MODE_64,UC_HOOK_CODE
from unicorn.x86_const import *
P=Path(os.environ['TS6_BINARY'])
assert hashlib.sha256(P.read_bytes()).hexdigest()=='1189672c2c290997a419100b0f29f4da95d0df9d1e744b07a764eba1e8a0944f'
u=Uc(UC_ARCH_X86,UC_MODE_64);b=P.read_bytes()
with P.open('rb') as f:
 e=ELFFile(f);loads=[s for s in e.iter_segments() if s['p_type']=='PT_LOAD'];end=max(s['p_vaddr']+s['p_memsz'] for s in loads);u.mem_map(0,(end+4095)&~4095)
 for s in loads:u.mem_write(s['p_vaddr'],s.data())
 symbols=e.get_section_by_name('.dynsym');imports={};stub=0x60000000;u.mem_map(stub,0x100000)
 for s in e.iter_sections():
  if s['sh_type']!='SHT_RELA':continue
  for r in s.iter_relocations():
   typ=r['r_info_type'];a=r['r_offset'];v=r['r_addend']
   if typ==8:u.mem_write(a,struct.pack('<Q',v))
   elif typ in (1,6,7):
    sym=symbols.get_symbol(r['r_info_sym']);name=sym.name
    if sym['st_value']:value=sym['st_value']+v
    else:value=stub+len(imports)*16;imports[value]=name
    u.mem_write(a,struct.pack('<Q',value))
u.mem_map(0x70000000,0x200000);u.mem_map(0x71000000,0x1000000);heap=0x71000000
regs=[UC_X86_REG_RDI,UC_X86_REG_RSI,UC_X86_REG_RDX,UC_X86_REG_RCX,UC_X86_REG_R8,UC_X86_REG_R9]
def alloc(n):
 global heap
 a=heap;heap+=(n+31)&~15
 if heap>=0x72000000:raise RuntimeError('bounded heap exceeded')
 return a
def ret(v=0):
 sp=u.reg_read(UC_X86_REG_RSP);pc=struct.unpack('<Q',u.mem_read(sp,8))[0];u.reg_write(UC_X86_REG_RAX,v);u.reg_write(UC_X86_REG_RSP,sp+8);u.reg_write(UC_X86_REG_RIP,pc)
def hook(uc,a,size,data):
 if a not in imports:return
 name=imports[a];args=[u.reg_read(r) for r in regs]
 if name in ('malloc','calloc'):
  n=args[0] if name=='malloc' else args[0]*args[1];ret(alloc(n))
 elif name in ('free',):ret()
 elif name in ('memcpy','memmove'):
  u.mem_write(args[0],bytes(u.mem_read(args[1],args[2])));ret(args[0])
 elif name in ('memcmp','bcmp'):
  left=bytes(u.mem_read(args[0],args[2]));right=bytes(u.mem_read(args[1],args[2]));ret((left>right)-(left<right) & ((1<<64)-1))
 elif name in ('memset',):u.mem_write(args[0],bytes([args[1]&255])*args[2]);ret(args[0])
 else:raise RuntimeError('unsupported import '+name)
u.hook_add(UC_HOOK_CODE,hook,begin=stub,end=stub+0xfffff)
def call(addr,args):
 sp=0x701ff000;u.reg_write(UC_X86_REG_RSP,sp-8);u.mem_write(sp-8,struct.pack('<Q',0x600ff000))
 for r,v in zip(regs,args):u.reg_write(r,v)
 for i,v in enumerate(args[6:]):u.mem_write(sp+i*8,struct.pack('<Q',v))
 u.emu_start(addr,0x600ff000,timeout=120000000,count=200000000)
 assert u.reg_read(UC_X86_REG_RIP)==0x600ff000,hex(u.reg_read(UC_X86_REG_RIP))
 return u.reg_read(UC_X86_REG_RAX)
def buf(data):
 a=alloc(len(data)+16);u.mem_write(a,data);return a
if __name__=='__main__':
 out=[]
 for n in (32,48):
  password=b'Synthetic-Only-4350!';salt=b'person@example.invalidts3Login'+password;pw=buf(password);sa=buf(salt);dest=alloc(64)
  rounds=int(sys.argv[1]) if len(sys.argv)>1 else 1
  ok=call(0x47594a0,[pw,len(password),sa,len(salt),n,rounds,dest]);actual=bytes(u.mem_read(dest,n));expected=hashlib.pbkdf2_hmac('sha512',password,salt,rounds,n)
  assert ok&255==1 and actual==expected,(ok,actual.hex(),expected.hex());out.append({'primitive':'PBKDF2-HMAC-SHA512','rounds':rounds,'bytes':n,'output':actual.hex(),'matched':True})
 print(json.dumps({'source_sha256':hashlib.sha256(b).hexdigest(),'scope':'selected native crypto routine under Unicorn; allocator/memory imports only; no process initialization, accounts or network','checks':out},indent=2))

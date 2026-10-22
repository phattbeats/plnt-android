"""Synthetic known-answer tests of native wrappers; no client initialization/network."""
import json,struct
from Crypto.Cipher import AES
import native_kat as n

def ptr(a):return struct.unpack('<Q',n.u.mem_read(a,8))[0]
key=bytes(range(32));plain=bytes(range(65))
obj=n.alloc(32);n.call(0x4758b50,[obj]);ctx=ptr(obj)
assert n.call(0x4758c20,[ctx,n.buf(key)])&255==1
checks=[]
for iv in (bytes(range(16)),b'\xff'+bytes(15),bytes([255])*16):
 out=n.alloc(16);ivp=n.buf(iv)
 n.call(0x4758c90,[out,ctx,n.buf(plain),ivp,len(plain)])
 actual=bytes(n.u.mem_read(ptr(out),len(plain)))
 ecb=AES.new(key,AES.MODE_ECB);expected=b'';counter=iv
 for j in range(0,len(plain),16):
  block=ecb.encrypt(counter)
  expected+=bytes(x^y for x,y in zip(plain[j:j+16],block))
  counter=((int.from_bytes(counter,'big')+1)%(1<<128)).to_bytes(16,'big')
  counter=((int.from_bytes(counter,'little')+1)%(1<<128)).to_bytes(16,'little')
  counter=((int.from_bytes(counter,'big')-1)%(1<<128)).to_bytes(16,'big')
 assert actual==expected,(iv.hex(),actual.hex(),expected.hex())
 checks.append({'primitive':'AES-256 CTR native BE+1/LE+1/BE-1 update','iv':iv.hex(),'bytes':len(plain),'ciphertext':actual.hex(),'matched':True})
print(json.dumps({'source_sha256':n.hashlib.sha256(n.b).hexdigest(),'scope':'selected native wrapper under Unicorn; synthetic only','checks':checks},indent=2))
# GCM wrapper uses a 12-byte nonce, 16-byte tag, and zero bytes of AAD.
obj=n.alloc(32);n.call(0x4758de0,[obj]);ctx=ptr(obj)
assert n.call(0x4758eb0,[ctx,n.buf(key)])&255==1
nonce=bytes(range(12));out=n.alloc(16)
n.call(0x4758f20,[out,ctx,n.buf(plain),n.buf(nonce),len(plain)])
actual=bytes(n.u.mem_read(ptr(out),len(plain)));tag=bytes(n.u.mem_read(ptr(out+8),16))
expected,expected_tag=AES.new(key,AES.MODE_GCM,nonce=nonce).encrypt_and_digest(plain)
assert (actual,tag)==(expected,expected_tag)
print(json.dumps({'primitive':'AES-256-GCM','nonce':nonce.hex(),'aad':'','ciphertext':actual.hex(),'tag':tag.hex(),'matched':True},indent=2))

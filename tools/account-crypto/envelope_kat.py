import struct,json
from Crypto.Cipher import AES
import native_kat as n
def string(data):return n.buf(struct.pack('<QQQ',((len(data)+16)&~15)|1,len(data),n.buf(data)))
def value(a):
 h=bytes(n.u.mem_read(a,24));l=struct.unpack('<Q',h[8:16])[0] if h[0]&1 else h[0]>>1
 p=struct.unpack('<Q',h[16:24])[0] if h[0]&1 else a+1
 return bytes(n.u.mem_read(p,l)) if l else b''
if __name__=='__main__':
 key=bytes(range(32));nonce=bytes(range(12));plain=b'Synthetic item envelope 4350: no account data'
 cipher,tag=AES.new(key,AES.MODE_GCM,nonce=nonce).encrypt_and_digest(plain)
 obj=n.alloc(64);n.u.mem_write(obj+0x30,struct.pack('<III',32,16,12))
 checks=[]
 for label,blob,expected in [('valid',tag+nonce+cipher,plain),('bad-tag',bytes([tag[0]^1])+tag[1:]+nonce+cipher,b'')]:
  out=n.alloc(24);n.call(0x2a82760,[out,obj,string(blob),string(key)]);actual=value(out)
  assert actual==expected,(label,actual.hex())
  checks.append({'case':label,'matched':True,'plaintext_returned':bool(actual)})
 print(json.dumps({'source_sha256':n.hashlib.sha256(n.b).hexdigest(),'envelope':'tag[16] || nonce[12] || ciphertext','key':key.hex(),'nonce':nonce.hex(),'plaintext':plain.hex(),'ciphertext':cipher.hex(),'tag':tag.hex(),'checks':checks,'scope':'synthetic native envelope unwrap, no account/network'},indent=2))

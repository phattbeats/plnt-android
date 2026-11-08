from pathlib import Path
import hashlib,base64,json
from Crypto.Cipher import AES
p=b'SyntheticPassword!';m=b'person@example.invalid'
l=base64.b64encode(hashlib.pbkdf2_hmac('sha512',p,m+b'ts3Login'+p,10000,48));e=hashlib.pbkdf2_hmac('sha512',p,m+b'ts3Encryption'+p,10000,32)
def vi(v):
 b=b''
 while v>127:b+=bytes([(v&127)|128]);v>>=7
 return b+bytes([v])
def bs(n,v):
 if isinstance(v,str):v=v.encode()
 return vi(n*8+2)+vi(len(v))+v
def num(n,v):return vi(n*8)+vi(v)
def ctr(key,iv,plain):
 aes=AES.new(key,AES.MODE_ECB);out=b'';c=iv
 for i in range(0,len(plain),16):
  out+=bytes(a^b for a,b in zip(plain[i:i+16],aes.encrypt(c)))
  c=((int.from_bytes(c,'big')+1)%(1<<128)).to_bytes(16,'big')
  c=((int.from_bytes(c,'little')+1)%(1<<128)).to_bytes(16,'little')
  c=((int.from_bytes(c,'big')-1)%(1<<128)).to_bytes(16,'big')
 return out
master=bytes(range(32));nonce=bytes(range(12))
cipher,tag=AES.new(e,AES.MODE_GCM,nonce=nonce).encrypt_and_digest(master)
blob=b'\x02'+tag+nonce+cipher
login=bs(1,blob)+bs(2,'synthetic-session')+bs(4,'synthetic-account')+num(5,200)
identity=bs(2,'identity-id')+num(6,1)+bs(17,bs(1,'synthetic-private-key'))
bookmark=bs(2,'bookmark-id')+num(6,0)+bs(16,bs(1,'Synthetic server')+bs(2,'voice.example.invalid')+num(3,9987)+bs(4,'Synthetic nickname')+bs(6,'identity-id')+bs(7,'synthetic-server-password'))
def detail(uuid,plain):
 iv=bytes(range(16));encrypted=iv+ctr(master,iv,plain)+hashlib.sha512(plain).digest()
 return bs(1,uuid)+bs(3,encrypted)
groups=bs(2,num(1,0)+bs(3,detail('bookmark-id',bookmark)))+bs(2,num(1,1)+bs(3,detail('identity-id',identity)))
vals={'loginPassword':l.decode(),'encryptionPassword':e.hex(),'loginReply':login.hex(),'syncReply':(num(1,301)+groups).hex(),'missingIdentityReply':(num(1,301)+bs(2,num(1,0)+bs(3,detail('bookmark-id',bookmark)))).hex()}
out=Path(__file__).resolve().parents[2] / 'app/app/src/test/kotlin/com/plnt/client/account/AccountFixtures.kt'
out.write_text('// Synthetic fixtures independently generated using Python hashlib and PyCryptodome.\npackage com.plnt.client.account\n\ninternal object AccountFixtures {\n'+''.join('    const val '+k+' = "'+v+'"\n' for k,v in vals.items())+'}\n')

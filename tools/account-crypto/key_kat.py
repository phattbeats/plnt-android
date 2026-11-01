import json,hashlib,struct
from Crypto.Cipher import AES
import native_kat as n
from envelope_kat import string,value
master=bytes(range(32));nonce=bytes(range(12));password=b'Synthetic-login-hash';uuid=b'synthetic-account-uuid';encpassword=bytes(range(32,64))
checks=[]
for version in (2,1):
 wrapkey=encpassword if version==2 else hashlib.pbkdf2_hmac('sha512',password,hashlib.sha512(uuid+b'ts3PassPhrase'+password).digest(),10000,32)
 cipher,tag=AES.new(wrapkey,AES.MODE_GCM,nonce=nonce).encrypt_and_digest(master)
 obj=n.alloc(64);n.u.mem_write(obj,struct.pack('<Q',0x4a77d98));n.u.mem_write(obj+0x30,struct.pack('<III',32,16,12))
 derived=n.alloc(24)
 n.call(0x2a812b0,[derived,0,string(password),string(encpassword),string(uuid),obj,version])
 assert value(derived)==wrapkey,(version,value(derived).hex(),wrapkey.hex())
 state=n.alloc(24);blob=bytes([version])+tag+nonce+cipher
 n.call(0x2a82760,[state,obj,string(blob[1:]),derived])
 assert value(state)==master,(version,value(state).hex())
 checks.append({'version':version,'matched':True,'wrap_key':wrapkey.hex(),'blob':blob.hex()})
print(json.dumps({'source_sha256':n.hashlib.sha256(n.b).hexdigest(),'scope':'synthetic native version-specific KDF plus GCM unwrap; dispatch/header checked statically; no RNG constructor, client, or network','checks':checks},indent=2))

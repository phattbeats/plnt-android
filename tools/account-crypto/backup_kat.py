import json,struct,base64
from Crypto.Cipher import AES
import native_kat as n
from envelope_kat import string,value
key=bytes(range(32));master=bytes(range(32,64));iv=bytes(range(16));counter=iv;cipher=b'';aes=AES.new(key,AES.MODE_ECB)
for i in range(0,len(master),16):
 cipher+=bytes(x^y for x,y in zip(master[i:i+16],aes.encrypt(counter)))
 counter=((int.from_bytes(counter,'big')+1)%(1<<128)).to_bytes(16,'big')
 counter=((int.from_bytes(counter,'little')+1)%(1<<128)).to_bytes(16,'little')
 counter=((int.from_bytes(counter,'big')-1)%(1<<128)).to_bytes(16,'big')
obj=n.alloc(64);n.u.mem_write(obj,struct.pack('<Q',0x4a77d98));n.u.mem_write(obj+0x30,struct.pack('<III',32,16,12));n.call(0x4758b50,[obj+0x20]);state=n.alloc(48);n.u.mem_write(state,struct.pack('<Q',obj))
status=n.call(0x2a80da0,[state,string(base64.b64encode(key)),string(iv+cipher)])
assert status&255==1 and value(state+16)==master
print(json.dumps({'scope':'synthetic legacy recovery helper with explicit context; no RNG initialization, account or network','backup_encoding':'standard base64 of 32 bytes','wrap':'IV[16] || AES-256-CTR-native(master-key)','matched':True},indent=2))

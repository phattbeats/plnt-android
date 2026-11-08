import struct,json,hashlib
from Crypto.Cipher import AES
import native_kat as n
from envelope_kat import string,value
from google.protobuf import descriptor_pool,message_factory
pool=descriptor_pool.DescriptorPool()
for offset,length in [(0x114e190,1329),(0x1150e30,4551)]:pool.AddSerializedFile(n.b[offset:offset+length])
Item=message_factory.GetMessageClass(pool.FindMessageTypeByName('com.teamspeak.sync.proto.Item_Data'))
item=Item(item_uuid='synthetic-bookmark',item_type=0)
item.bookmark.name='Synthetic bookmark';item.bookmark.address='voice.example.invalid';item.bookmark.port=9987;item.bookmark.identity='synthetic-identity'
plain=item.SerializeToString()
identity=Item(item_uuid='synthetic-identity',item_type=1)
identity.identity.unique_identity='SYNTHETIC-NOT-A-PRIVATE-KEY';identity.identity.id_name='Synthetic identity'
for record,case in [(item,16),(identity,17)]:
 data=record.SerializeToString();native_item=n.alloc(0x60)
 n.call(0x46cfdf0,[native_item,0]);ok=n.call(0x43027a0,[native_item,n.buf(data),len(data)])
 assert ok&255==1 and struct.unpack('<I',n.u.mem_read(native_item+0x58,4))[0]==case
key=bytes(range(32));iv=bytes(range(16))
ecb=AES.new(key,AES.MODE_ECB);cipher=b'';counter=iv
for i in range(0,len(plain),16):
 cipher+=bytes(x^y for x,y in zip(plain[i:i+16],ecb.encrypt(counter)))
 counter=((int.from_bytes(counter,'big')+1)%(1<<128)).to_bytes(16,'big')
 counter=((int.from_bytes(counter,'little')+1)%(1<<128)).to_bytes(16,'little')
 counter=((int.from_bytes(counter,'big')-1)%(1<<128)).to_bytes(16,'big')
obj=n.alloc(64);n.u.mem_write(obj,struct.pack('<Q',0x4a77d98));n.u.mem_write(obj+0x30,struct.pack('<III',32,16,12))
cptr=n.alloc(16);n.call(0x4758b50,[cptr]);ctx=struct.unpack('<Q',n.u.mem_read(cptr,8))[0];assert n.call(0x4758c20,[ctx,n.buf(key)])&255==1
n.u.mem_write(obj+0x20,struct.pack('<Q',ctx));shared=n.buf(struct.pack('<QQ',obj,0))
checks=[]
blob=iv+cipher+hashlib.sha512(plain).digest()
for label,data,expected in [('valid',blob,plain),('bad-digest',blob[:-1]+bytes([blob[-1]^1]),b'')]:
 out=n.alloc(24);n.call(0x2a80410,[out,shared,string(data)]);actual=value(out)
 assert actual==expected,(label,actual.hex());checks.append({'case':label,'matched':True,'plaintext_returned':bool(actual)})
print(json.dumps({'envelope':'IV[16] || AES-256-CTR-native(plaintext) || SHA512(plaintext)[64]','checks':checks,'protobuf_mapping':'recovered Item_Data bookmark=16 and identity=17 accepted by native parser','scope':'synthetic native sync-item decrypt, no account/network'},indent=2))

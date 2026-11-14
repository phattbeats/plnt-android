# ts6-proto — recover the TeamSpeak 6 protocol schema from a server binary

The TS6 server is written in C++ against protobuf, and generated C++ protobuf code embeds each
`.proto` file's serialized `FileDescriptorProto` verbatim in the binary. These two scripts pull
those descriptors out and turn them back into `.proto` source.

The output is TeamSpeak's schema, so it is **not** checked in here. Run this against your own
server to regenerate it. See `../../TS6_PROTOCOL.md` for what the recovered schema tells us.

## Usage

Copy the server binary out of the container, then extract and render:

```sh
docker cp teamspeak6-server:/opt/tsserver/tsserver ./tsserver
python3 extract_descriptors.py ./tsserver teamspeak6.descriptorset.pb
python3 render_proto.py teamspeak6.descriptorset.pb ./proto_out
```

Requires `protobuf` for Python (`pip install protobuf`). Tested against protobuf 6.33.

`teamspeak6.descriptorset.pb` is a standard `FileDescriptorSet`, which is what code generators
consume directly — feed it to `prost-build` for the Rust core, or to `protoc` for anything else,
without needing the rendered `.proto` files at all.

## How it works

`extract_descriptors.py` scans for the anchor every `FileDescriptorProto` starts with — field 1
(`name`), i.e. tag `0x0A`, a length varint, then a path ending in `.proto`. From that offset it
walks the wire format record by record, stopping at the first tag that is not a valid
`FileDescriptorProto` field number, then parses the candidate slice. A blob is only accepted if
re-serializing it reproduces the original bytes exactly, which rules out false positives.

`render_proto.py` walks the resulting descriptors and prints `.proto` syntax — messages, nested
messages, enums, `oneof`s, repeated/optional labels and field numbers.

Expect ~49 files, including `client_commands.proto` (the `ClientCommandRequest` /
`ClientCommandResponse` envelope), `client/streaming.proto`, `client/chat.proto`,
`events/stream_events.proto` and the `shared/*.proto` type definitions.

## binutils/ — reading the code, not just the schema

The schema says what the messages are; it says nothing about how they are framed. `binutils/`
holds the three scripts used for that (see `../../TS6_FRAMING.md` for the findings):

```sh
python3 binutils/elf.py   ./tsserver                            # section map
python3 binutils/find.py  ./tsserver client_protocol_format     # string + xrefs
python3 binutils/disas.py ./tsserver 0xbd8720                   # disassemble a function
```

The server binary is stripped and the container it ships in has no binutils, so `elf.py` is a
self-contained ELF64 reader (sections, address↔offset, relocations) and `disas.py` recovers
function boundaries from `.eh_frame` FDEs rather than a symbol table. `disas.py` needs
`pip install capstone`; the other two are pure stdlib.

Addresses in `TS6_FRAMING.md` are for one specific build and will move. Re-find them by string:
`find.py` gives the xref, `disas.py` gives the function around it.

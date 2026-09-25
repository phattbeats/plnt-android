#!/usr/bin/env python3
"""Render a FileDescriptorSet back into .proto source files."""
import os
import sys
from google.protobuf import descriptor_pb2

FD = descriptor_pb2.FieldDescriptorProto
TYPES = {
    FD.TYPE_DOUBLE: 'double', FD.TYPE_FLOAT: 'float', FD.TYPE_INT64: 'int64',
    FD.TYPE_UINT64: 'uint64', FD.TYPE_INT32: 'int32', FD.TYPE_FIXED64: 'fixed64',
    FD.TYPE_FIXED32: 'fixed32', FD.TYPE_BOOL: 'bool', FD.TYPE_STRING: 'string',
    FD.TYPE_BYTES: 'bytes', FD.TYPE_UINT32: 'uint32', FD.TYPE_SFIXED32: 'sfixed32',
    FD.TYPE_SFIXED64: 'sfixed64', FD.TYPE_SINT32: 'sint32', FD.TYPE_SINT64: 'sint64',
}


def type_name(f):
    if f.type in (FD.TYPE_MESSAGE, FD.TYPE_ENUM, FD.TYPE_GROUP):
        return f.type_name.lstrip('.')
    return TYPES.get(f.type, f'/*type{f.type}*/')


def render_field(f, indent, in_oneof=False):
    label = ''
    if f.label == FD.LABEL_REPEATED:
        label = 'repeated '
    elif f.label == FD.LABEL_OPTIONAL and not in_oneof:
        label = 'optional '
    opts = []
    if f.HasField('default_value'):
        opts.append(f'default = {f.default_value}')
    tail = f' [{", ".join(opts)}]' if opts else ''
    return f'{indent}{label}{type_name(f)} {f.name} = {f.number}{tail};'


def render_enum(e, indent):
    out = [f'{indent}enum {e.name} {{']
    for v in e.value:
        out.append(f'{indent}  {v.name} = {v.number};')
    out.append(f'{indent}}}')
    return out


def render_message(m, indent):
    if m.options.map_entry:
        return []
    out = [f'{indent}message {m.name} {{']
    for n in m.nested_type:
        out += render_message(n, indent + '  ')
    for e in m.enum_type:
        out += render_enum(e, indent + '  ')
    oneof_done = set()
    for f in m.field:
        if f.HasField('oneof_index'):
            oi = f.oneof_index
            if oi in oneof_done:
                continue
            oneof_done.add(oi)
            out.append(f'{indent}  oneof {m.oneof_decl[oi].name} {{')
            for g in m.field:
                if g.HasField('oneof_index') and g.oneof_index == oi:
                    out.append(render_field(g, indent + '    ', in_oneof=True))
            out.append(f'{indent}  }}')
        else:
            out.append(render_field(f, indent + '  '))
    out.append(f'{indent}}}')
    return out


def render_file(fd):
    out = [f'// recovered from tsserver binary', f'syntax = "{fd.syntax or "proto2"}";']
    if fd.package:
        out.append(f'package {fd.package};')
    for dep in fd.dependency:
        out.append(f'import "{dep}";')
    out.append('')
    for e in fd.enum_type:
        out += render_enum(e, '')
    for m in fd.message_type:
        out += render_message(m, '')
    for s in fd.service:
        out.append(f'service {s.name} {{')
        for meth in s.method:
            out.append(f'  rpc {meth.name}({meth.input_type.lstrip(".")}) '
                       f'returns ({meth.output_type.lstrip(".")});')
        out.append('}')
    return '\n'.join(out) + '\n'


def main(setpath, outdir):
    fds = descriptor_pb2.FileDescriptorSet()
    fds.ParseFromString(open(setpath, 'rb').read())
    for fd in fds.file:
        if fd.name.startswith('google/protobuf/'):
            continue
        dest = os.path.join(outdir, fd.name)
        os.makedirs(os.path.dirname(dest), exist_ok=True)
        with open(dest, 'w') as fh:
            fh.write(render_file(fd))
    print(f'wrote {len(fds.file)} files under {outdir}')


if __name__ == '__main__':
    main(sys.argv[1], sys.argv[2])

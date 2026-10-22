2a80da0: push r14
2a80da2: push rbx
2a80da3: sub rsp, 0x38
2a80da7: mov r14, rdx
2a80daa: mov rbx, rdi
2a80dad: movzx eax, byte ptr [rsi]
2a80db0: test al, 1
2a80db2: je 0x2a80dbf
2a80db4: mov rax, qword ptr [rsi + 8]
2a80db8: test rax, rax
2a80dbb: jne 0x2a80dc6
2a80dbd: jmp 0x2a80e00
2a80dbf: shr eax, 1
2a80dc1: test rax, rax
2a80dc4: je 0x2a80e00
2a80dc6: movzx eax, byte ptr [r14]
2a80dca: test al, 1
2a80dcc: je 0x2a80df9
2a80dce: mov rax, qword ptr [r14 + 8]
2a80dd2: test rax, rax
2a80dd5: je 0x2a80e00
2a80dd7: lea rdi, [rsp + 8]
2a80ddc: call 0x2a81990
2a80de1: movzx eax, byte ptr [rsp + 8]
2a80de6: test al, 1
2a80de8: je 0x2a80e0a
2a80dea: mov rax, qword ptr [rsp + 0x10]
2a80def: test rax, rax
2a80df2: jne 0x2a80e15
2a80df4: jmp 0x2a80ebe
2a80df9: shr eax, 1
2a80dfb: test rax, rax
2a80dfe: jne 0x2a80dd7
2a80e00: xor eax, eax
2a80e02: add rsp, 0x38
2a80e06: pop rbx
2a80e07: pop r14
2a80e09: ret
2a80e0a: shr eax, 1
2a80e0c: test rax, rax
2a80e0f: je 0x2a80ebe
2a80e15: mov rsi, qword ptr [rbx]
2a80e18: test rsi, rsi
2a80e1b: jne 0x2a80e4a
2a80e1d: movzx eax, byte ptr [rip + 0x2126b7c]
2a80e24: movsx esi, al
2a80e27: lea rdi, [rsp + 0x20]
2a80e2c: call 0x2a7f8e0
2a80e31: mov rsi, qword ptr [rsp + 0x20]
2a80e36: mov rdi, qword ptr [rbx]
2a80e39: mov qword ptr [rbx], rsi
2a80e3c: test rdi, rdi
2a80e3f: je 0x2a80e4a
2a80e41: mov rax, qword ptr [rdi]
2a80e44: call qword ptr [rax + 8]
2a80e47: mov rsi, qword ptr [rbx]
2a80e4a: mov rax, qword ptr [rsi]
2a80e4d: lea rdi, [rsp + 0x20]
2a80e52: lea rcx, [rsp + 8]
2a80e57: mov rdx, r14
2a80e5a: call qword ptr [rax + 0x80]
2a80e60: lea r14, [rbx + 0x10]
2a80e64: test byte ptr [rbx + 0x10], 1
2a80e68: je 0x2a80e7b
2a80e6a: mov rsi, qword ptr [rbx + 0x10]
2a80e6e: mov rdi, qword ptr [rbx + 0x20]
2a80e72: and rsi, 0xfffffffffffffffe
2a80e76: call 0x498cfc8
2a80e7b: mov rax, qword ptr [rsp + 0x30]
2a80e80: mov qword ptr [r14 + 0x10], rax
2a80e84: movups xmm0, xmmword ptr [rsp + 0x20]
2a80e89: movups xmmword ptr [r14], xmm0
2a80e8d: movzx eax, byte ptr [rbx + 0x10]
2a80e91: test al, 1
2a80e93: je 0x2a80eb7
2a80e95: mov rax, qword ptr [rbx + 0x18]
2a80e99: test rax, rax
2a80e9c: je 0x2a80ebe
2a80e9e: mov rdi, qword ptr [rbx]
2a80ea1: mov rax, qword ptr [rdi]
2a80ea4: mov rsi, r14
2a80ea7: call qword ptr [rax + 0x70]
2a80eaa: test byte ptr [rsp + 8], 1
2a80eaf: je 0x2a80e02
2a80eb5: jmp 0x2a80ecb
2a80eb7: shr eax, 1
2a80eb9: test rax, rax
2a80ebc: jne 0x2a80e9e
2a80ebe: xor eax, eax
2a80ec0: test byte ptr [rsp + 8], 1
2a80ec5: je 0x2a80e02
2a80ecb: mov rsi, qword ptr [rsp + 8]
2a80ed0: mov rdi, qword ptr [rsp + 0x18]
2a80ed5: and rsi, 0xfffffffffffffffe
2a80ed9: mov ebx, eax
2a80edb: call 0x498cfc8
2a80ee0: mov eax, ebx
2a80ee2: add rsp, 0x38
2a80ee6: pop rbx
2a80ee7: pop r14
2a80ee9: ret
2a80eea: nop word ptr [rax + rax]

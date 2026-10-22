4758c90: push rbp
4758c91: push r15
4758c93: push r14
4758c95: push r13
4758c97: push r12
4758c99: push rbx
4758c9a: sub rsp, 0x38
4758c9e: mov qword ptr [rsp + 0x10], rdx
4758ca3: cmp qword ptr [rsi + 8], 0
4758ca8: je 0x4758d91
4758cae: mov r14, r8
4758cb1: mov r15, rcx
4758cb4: mov r13, rsi
4758cb7: mov qword ptr [rsp + 8], rdi
4758cbc: mov rdi, r8
4758cbf: call 0x4a07956
4758cc4: mov rbp, rax
4758cc7: mov rdi, rax
4758cca: xor esi, esi
4758ccc: mov rdx, r14
4758ccf: call 0x4a07ba0
4758cd4: xorps xmm0, xmm0
4758cd7: movaps xmmword ptr [rsp + 0x20], xmm0
4758cdc: mov qword ptr [rsp + 0x18], 0
4758ce5: test r14, r14
4758ce8: je 0x4758d87
4758cee: xor r12d, r12d
4758cf1: jmp 0x4758d09
4758cf3: nop word ptr cs:[rax + rax]
4758d00: add r12, 0x10
4758d04: test r14, r14
4758d07: je 0x4758d87
4758d09: cmp r14, 0x10
4758d0d: mov ebx, 0x10
4758d12: cmovb rbx, r14
4758d16: mov rdi, qword ptr [r13 + 8]
4758d1a: mov rax, qword ptr [rsp + 0x10]
4758d1f: lea r9, [rax + r12]
4758d23: lea rax, [r12 + rbp]
4758d27: mov qword ptr [rsp], rax
4758d2b: mov rsi, rbx
4758d2e: lea rdx, [rsp + 0x18]
4758d33: mov rcx, r15
4758d36: lea r8, [rsp + 0x20]
4758d3b: call 0x3c03510
4758d40: test eax, eax
4758d42: jne 0x4758d9a
4758d44: sub r14, rbx
4758d47: xor eax, eax
4758d49: nop dword ptr [rax]
4758d50: inc byte ptr [r15 + rax]
4758d54: jne 0x4758d60
4758d56: cmp rax, 0xf
4758d5a: lea rax, [rax + 1]
4758d5e: jb 0x4758d50
4758d60: mov eax, 0x10
4758d65: nop word ptr cs:[rax + rax]
4758d70: mov ecx, eax
4758d72: sub byte ptr [r15 + rcx - 1], 1
4758d78: jae 0x4758d00
4758d7a: cmp eax, 1
4758d7d: lea eax, [rax - 1]
4758d80: ja 0x4758d70
4758d82: jmp 0x4758d00
4758d87: mov rdi, qword ptr [rsp + 8]
4758d8c: mov qword ptr [rdi], rbp
4758d8f: jmp 0x4758db1
4758d91: mov qword ptr [rdi], 0
4758d98: jmp 0x4758db1
4758d9a: mov rbx, qword ptr [rsp + 8]
4758d9f: mov qword ptr [rbx], 0
4758da6: mov rdi, rbp
4758da9: call 0x498cfd0
4758dae: mov rdi, rbx
4758db1: mov rax, rdi
4758db4: add rsp, 0x38
4758db8: pop rbx
4758db9: pop r12
4758dbb: pop r13
4758dbd: pop r14
4758dbf: pop r15
4758dc1: pop rbp
4758dc2: ret
4758dc3: mov rbx, rax
4758dc6: mov rdi, rbp
4758dc9: call 0x498cfd0
4758dce: mov rdi, rbx
4758dd1: call 0x4a07a10
4758dd6: int3
4758dd7: int3
4758dd8: int3
4758dd9: int3
4758dda: int3
4758ddb: int3
4758ddc: int3
4758ddd: int3
4758dde: int3
4758ddf: int3

2a7fe30: push r15
2a7fe32: push r14
2a7fe34: push r13
2a7fe36: push r12
2a7fe38: push rbx
2a7fe39: sub rsp, 0x60
2a7fe3d: mov r15, rdx
2a7fe40: mov r14, rsi
2a7fe43: mov rbx, rdi
2a7fe46: call 0x49e5390
2a7fe4b: lea r12, [rsp + 0x40]
2a7fe50: mov rdi, r12
2a7fe53: mov rsi, rax
2a7fe56: call 0x49ea9d0
2a7fe5b: lea r13, [rsp + 0x48]
2a7fe60: mov rdi, r13
2a7fe63: mov rsi, r15
2a7fe66: mov rdx, r12
2a7fe69: call 0x2a7f780
2a7fe6e: lea rsi, [rip - 0x22c7458]
2a7fe75: mov edx, 0xd
2a7fe7a: mov rdi, r13
2a7fe7d: call 0x49ac7b0
2a7fe82: mov rcx, qword ptr [rax + 0x10]
2a7fe86: mov qword ptr [rsp + 0x10], rcx
2a7fe8b: movups xmm0, xmmword ptr [rax]
2a7fe8e: movaps xmmword ptr [rsp], xmm0
2a7fe92: xorps xmm0, xmm0
2a7fe95: movups xmmword ptr [rax], xmm0
2a7fe98: mov qword ptr [rax + 0x10], 0
2a7fea0: movzx edx, byte ptr [r14]
2a7fea4: mov eax, edx
2a7fea6: lea rsi, [r14 + 1]
2a7feaa: shr edx, 1
2a7feac: test al, 1
2a7feae: cmovne rsi, qword ptr [r14 + 0x10]
2a7feb3: cmovne rdx, qword ptr [r14 + 8]
2a7feb8: mov rdi, rsp
2a7febb: call 0x49ac7b0
2a7fec0: mov rcx, qword ptr [rax + 0x10]
2a7fec4: mov qword ptr [rsp + 0x30], rcx
2a7fec9: movups xmm0, xmmword ptr [rax]
2a7fecc: movaps xmmword ptr [rsp + 0x20], xmm0
2a7fed1: xorps xmm0, xmm0
2a7fed4: movups xmmword ptr [rax], xmm0
2a7fed7: mov qword ptr [rax + 0x10], 0
2a7fedf: lea rdx, [rsp + 0x20]
2a7fee4: mov rdi, rbx
2a7fee7: mov rsi, r14
2a7feea: mov ecx, 0x2710
2a7feef: call 0x2a81950
2a7fef4: test byte ptr [rsp + 0x20], 1
2a7fef9: jne 0x2a7ff36
2a7fefb: test byte ptr [rsp], 1
2a7feff: jne 0x2a7ff4f
2a7ff01: test byte ptr [rsp + 0x48], 1
2a7ff06: je 0x2a7ff1b
2a7ff08: mov rsi, qword ptr [rsp + 0x48]
2a7ff0d: mov rdi, qword ptr [rsp + 0x58]
2a7ff12: and rsi, 0xfffffffffffffffe
2a7ff16: call 0x498cfc8
2a7ff1b: lea rdi, [rsp + 0x40]
2a7ff20: call 0x49ea9f0
2a7ff25: mov rax, rbx
2a7ff28: add rsp, 0x60
2a7ff2c: pop rbx
2a7ff2d: pop r12
2a7ff2f: pop r13
2a7ff31: pop r14
2a7ff33: pop r15
2a7ff35: ret
2a7ff36: mov rsi, qword ptr [rsp + 0x20]
2a7ff3b: mov rdi, qword ptr [rsp + 0x30]
2a7ff40: and rsi, 0xfffffffffffffffe
2a7ff44: call 0x498cfc8
2a7ff49: test byte ptr [rsp], 1
2a7ff4d: je 0x2a7ff01
2a7ff4f: mov rsi, qword ptr [rsp]
2a7ff53: mov rdi, qword ptr [rsp + 0x10]
2a7ff58: and rsi, 0xfffffffffffffffe
2a7ff5c: call 0x498cfc8
2a7ff61: test byte ptr [rsp + 0x48], 1
2a7ff66: jne 0x2a7ff08
2a7ff68: jmp 0x2a7ff1b
2a7ff6a: mov rdi, rax
2a7ff6d: call 0x18f83c0
2a7ff72: nop word ptr cs:[rax + rax]

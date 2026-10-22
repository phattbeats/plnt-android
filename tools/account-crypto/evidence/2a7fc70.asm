2a7fc70: push r15
2a7fc72: push r14
2a7fc74: push r13
2a7fc76: push r12
2a7fc78: push rbx
2a7fc79: sub rsp, 0x70
2a7fc7d: mov r15, rdx
2a7fc80: mov r14, rsi
2a7fc83: mov rbx, rdi
2a7fc86: call 0x49e5390
2a7fc8b: lea r12, [rsp + 0x38]
2a7fc90: mov rdi, r12
2a7fc93: mov rsi, rax
2a7fc96: call 0x49ea9d0
2a7fc9b: lea r13, [rsp + 0x58]
2a7fca0: mov rdi, r13
2a7fca3: mov rsi, r15
2a7fca6: mov rdx, r12
2a7fca9: call 0x2a7f780
2a7fcae: lea rsi, [rip - 0x2304be4]
2a7fcb5: mov edx, 8
2a7fcba: mov rdi, r13
2a7fcbd: call 0x49ac7b0
2a7fcc2: mov rcx, qword ptr [rax + 0x10]
2a7fcc6: mov qword ptr [rsp + 0x10], rcx
2a7fccb: movups xmm0, xmmword ptr [rax]
2a7fcce: movaps xmmword ptr [rsp], xmm0
2a7fcd2: xorps xmm0, xmm0
2a7fcd5: movups xmmword ptr [rax], xmm0
2a7fcd8: mov qword ptr [rax + 0x10], 0
2a7fce0: movzx edx, byte ptr [r14]
2a7fce4: mov eax, edx
2a7fce6: lea rsi, [r14 + 1]
2a7fcea: shr edx, 1
2a7fcec: test al, 1
2a7fcee: cmovne rsi, qword ptr [r14 + 0x10]
2a7fcf3: cmovne rdx, qword ptr [r14 + 8]
2a7fcf8: mov rdi, rsp
2a7fcfb: call 0x49ac7b0
2a7fd00: mov rcx, qword ptr [rax + 0x10]
2a7fd04: mov qword ptr [rsp + 0x30], rcx
2a7fd09: movups xmm0, xmmword ptr [rax]
2a7fd0c: movaps xmmword ptr [rsp + 0x20], xmm0
2a7fd11: xorps xmm0, xmm0
2a7fd14: movups xmmword ptr [rax], xmm0
2a7fd17: mov qword ptr [rax + 0x10], 0
2a7fd1f: lea rdi, [rsp + 0x40]
2a7fd24: lea rdx, [rsp + 0x20]
2a7fd29: mov rsi, r14
2a7fd2c: mov ecx, 0x2710
2a7fd31: call 0x2a81930
2a7fd36: test byte ptr [rsp + 0x20], 1
2a7fd3b: jne 0x2a7fd9f
2a7fd3d: test byte ptr [rsp], 1
2a7fd41: jne 0x2a7fdb8
2a7fd43: test byte ptr [rsp + 0x58], 1
2a7fd48: je 0x2a7fd5d
2a7fd4a: mov rsi, qword ptr [rsp + 0x58]
2a7fd4f: mov rdi, qword ptr [rsp + 0x68]
2a7fd54: and rsi, 0xfffffffffffffffe
2a7fd58: call 0x498cfc8
2a7fd5d: lea rdi, [rsp + 0x38]
2a7fd62: call 0x49ea9f0
2a7fd67: lea rsi, [rsp + 0x40]
2a7fd6c: mov rdi, rbx
2a7fd6f: call 0x2a81970
2a7fd74: test byte ptr [rsp + 0x40], 1
2a7fd79: je 0x2a7fd8e
2a7fd7b: mov rsi, qword ptr [rsp + 0x40]
2a7fd80: mov rdi, qword ptr [rsp + 0x50]
2a7fd85: and rsi, 0xfffffffffffffffe
2a7fd89: call 0x498cfc8
2a7fd8e: mov rax, rbx
2a7fd91: add rsp, 0x70
2a7fd95: pop rbx
2a7fd96: pop r12
2a7fd98: pop r13
2a7fd9a: pop r14
2a7fd9c: pop r15
2a7fd9e: ret
2a7fd9f: mov rsi, qword ptr [rsp + 0x20]
2a7fda4: mov rdi, qword ptr [rsp + 0x30]
2a7fda9: and rsi, 0xfffffffffffffffe
2a7fdad: call 0x498cfc8
2a7fdb2: test byte ptr [rsp], 1
2a7fdb6: je 0x2a7fd43
2a7fdb8: mov rsi, qword ptr [rsp]
2a7fdbc: mov rdi, qword ptr [rsp + 0x10]
2a7fdc1: and rsi, 0xfffffffffffffffe
2a7fdc5: call 0x498cfc8
2a7fdca: test byte ptr [rsp + 0x58], 1
2a7fdcf: jne 0x2a7fd4a
2a7fdd5: jmp 0x2a7fd5d
2a7fdd7: mov rdi, rax
2a7fdda: call 0x18f83c0
2a7fddf: nop

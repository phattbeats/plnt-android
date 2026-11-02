2a82760: push rbp
2a82761: push r15
2a82763: push r14
2a82765: push r13
2a82767: push r12
2a82769: push rbx
2a8276a: sub rsp, 0x98
2a82771: mov r15, rdx
2a82774: mov r13, rsi
2a82777: movzx eax, byte ptr [rcx]
2a8277a: test al, 1
2a8277c: je 0x2a827a1
2a8277e: mov rax, qword ptr [rcx + 8]
2a82782: mov edx, dword ptr [r13 + 0x30]
2a82786: cmp rax, rdx
2a82789: je 0x2a827ac
2a8278b: xorps xmm0, xmm0
2a8278e: movups xmmword ptr [rdi], xmm0
2a82791: mov qword ptr [rdi + 0x10], 0
2a82799: mov rax, rdi
2a8279c: jmp 0x2a82a5e
2a827a1: shr eax, 1
2a827a3: mov edx, dword ptr [r13 + 0x30]
2a827a7: cmp rax, rdx
2a827aa: jne 0x2a8278b
2a827ac: movsxd rax, dword ptr [r13 + 0x34]
2a827b0: movzx edx, byte ptr [r15]
2a827b4: test dl, 1
2a827b7: je 0x2a827bf
2a827b9: mov r12, qword ptr [r15 + 8]
2a827bd: jmp 0x2a827c5
2a827bf: mov r12d, edx
2a827c2: shr r12d, 1
2a827c5: cmp r12, rax
2a827c8: mov rbp, rax
2a827cb: cmovb rbp, r12
2a827cf: cmp rbp, -9
2a827d3: jae 0x2a82b3f
2a827d9: mov qword ptr [rsp + 0x58], rax
2a827de: mov qword ptr [rsp + 0x18], rdi
2a827e3: mov rsi, qword ptr [r15 + 0x10]
2a827e7: cmp rbp, 0x16
2a827eb: mov qword ptr [rsp + 0x90], rcx
2a827f3: ja 0x2a82806
2a827f5: lea eax, [rbp*2]
2a827fc: mov byte ptr [rsp], al
2a827ff: lea rbx, [rsp + 1]
2a82804: jmp 0x2a82849
2a82806: mov rax, rbp
2a82809: and rax, 0xfffffffffffffff8
2a8280d: add rax, 8
2a82811: cmp rax, 0x18
2a82815: mov r14d, 0x1a
2a8281b: cmovne r14, rax
2a8281f: mov rdi, r14
2a82822: mov dword ptr [rsp + 0x54], edx
2a82826: mov rbx, rsi
2a82829: call 0x4a078f0
2a8282e: mov rsi, rbx
2a82831: mov edx, dword ptr [rsp + 0x54]
2a82835: mov rbx, rax
2a82838: inc r14
2a8283b: mov qword ptr [rsp], r14
2a8283f: mov qword ptr [rsp + 8], rbp
2a82844: mov qword ptr [rsp + 0x10], rax
2a82849: inc r15
2a8284c: test dl, 1
2a8284f: cmovne r15, rsi
2a82853: mov rdi, rbx
2a82856: mov rsi, r15
2a82859: mov rdx, rbp
2a8285c: call 0x4a07a30
2a82861: mov byte ptr [rbx + rbp], 0
2a82865: mov rbp, r12
2a82868: mov rbx, qword ptr [rsp + 0x58]
2a8286d: sub rbp, rbx
2a82870: jb 0x2a82b44
2a82876: movsxd r14, dword ptr [r13 + 0x38]
2a8287a: cmp rbp, r14
2a8287d: cmovae rbp, r14
2a82881: cmp rbp, -9
2a82885: jae 0x2a82b49
2a8288b: cmp rbp, 0x16
2a8288f: ja 0x2a828a3
2a82891: lea eax, [rbp*2]
2a82898: mov byte ptr [rsp + 0x38], al
2a8289c: lea r13, [rsp + 0x39]
2a828a1: jmp 0x2a828de
2a828a3: mov rax, rbp
2a828a6: and rax, 0xfffffffffffffff8
2a828aa: add rax, 8
2a828ae: cmp rax, 0x18
2a828b2: mov ebx, 0x1a
2a828b7: cmovne rbx, rax
2a828bb: mov rdi, rbx
2a828be: call 0x4a078f0
2a828c3: mov r13, rax
2a828c6: or rbx, 1
2a828ca: mov qword ptr [rsp + 0x38], rbx
2a828cf: mov qword ptr [rsp + 0x40], rbp
2a828d4: mov qword ptr [rsp + 0x48], rax
2a828d9: mov rbx, qword ptr [rsp + 0x58]
2a828de: mov rsi, rbx
2a828e1: add rsi, r15
2a828e4: mov rdi, r13
2a828e7: mov rdx, rbp
2a828ea: call 0x4a07a30
2a828ef: mov byte ptr [r13 + rbp], 0
2a828f5: lea eax, [rbx + r14]
2a828f9: movsxd r14, eax
2a828fc: sub r12, r14
2a828ff: jb 0x2a82b4e
2a82905: cmp r12, -9
2a82909: jae 0x2a82b53
2a8290f: cmp r12, 0x16
2a82913: ja 0x2a82924
2a82915: lea eax, [r12 + r12]
2a82919: mov byte ptr [rsp + 0x20], al
2a8291d: lea rbx, [rsp + 0x21]
2a82922: jmp 0x2a8295b
2a82924: mov rax, r12
2a82927: and rax, 0xfffffffffffffff8
2a8292b: add rax, 8
2a8292f: cmp rax, 0x18
2a82933: mov r13d, 0x1a
2a82939: cmovne r13, rax
2a8293d: mov rdi, r13
2a82940: call 0x4a078f0
2a82945: mov rbx, rax
2a82948: or r13, 1
2a8294c: mov qword ptr [rsp + 0x20], r13
2a82951: mov qword ptr [rsp + 0x28], r12
2a82956: mov qword ptr [rsp + 0x30], rax
2a8295b: add r15, r14
2a8295e: mov rdi, rbx
2a82961: mov rsi, r15
2a82964: mov rdx, r12
2a82967: call 0x4a07a30
2a8296c: mov byte ptr [rbx + r12], 0
2a82971: lea rdi, [rsp + 0x60]
2a82976: lea rcx, [rsp + 0x38]
2a8297b: lea r8, [rsp + 0x20]
2a82980: mov rdx, qword ptr [rsp + 0x90]
2a82988: mov r9d, 1
2a8298e: call 0x2a819b0
2a82993: movzx eax, byte ptr [rsp + 0x60]
2a82998: mov ebx, eax
2a8299a: mov r14, qword ptr [rsp + 0x68]
2a8299f: shr eax, 1
2a829a1: test bl, 1
2a829a4: cmovne rax, r14
2a829a8: test rax, rax
2a829ab: je 0x2a82a14
2a829ad: movzx eax, byte ptr [rsp + 0x78]
2a829b2: test al, 1
2a829b4: je 0x2a829c5
2a829b6: mov rdx, qword ptr [rsp + 0x80]
2a829be: test rdx, rdx
2a829c1: jne 0x2a829ce
2a829c3: jmp 0x2a82a14
2a829c5: mov edx, eax
2a829c7: shr edx, 1
2a829c9: test rdx, rdx
2a829cc: je 0x2a82a14
2a829ce: movzx ecx, byte ptr [rsp]
2a829d2: test cl, 1
2a829d5: je 0x2a829e3
2a829d7: mov rsi, qword ptr [rsp + 8]
2a829dc: cmp rsi, rdx
2a829df: je 0x2a829ec
2a829e1: jmp 0x2a82a14
2a829e3: mov esi, ecx
2a829e5: shr esi, 1
2a829e7: cmp rsi, rdx
2a829ea: jne 0x2a82a14
2a829ec: test cl, 1
2a829ef: je 0x2a82ae3
2a829f5: mov rdi, qword ptr [rsp + 0x10]
2a829fa: test al, 1
2a829fc: jne 0x2a82af0
2a82a02: lea rsi, [rsp + 0x79]
2a82a07: call 0x4a07bb0
2a82a0c: test eax, eax
2a82a0e: je 0x2a82b05
2a82a14: xorps xmm0, xmm0
2a82a17: mov rbx, qword ptr [rsp + 0x18]
2a82a1c: movups xmmword ptr [rbx], xmm0
2a82a1f: mov qword ptr [rbx + 0x10], 0
2a82a27: test byte ptr [rsp + 0x78], 1
2a82a2c: jne 0x2a82a70
2a82a2e: test byte ptr [rsp + 0x60], 1
2a82a33: jne 0x2a82a8d
2a82a35: test byte ptr [rsp + 0x20], 1
2a82a3a: jne 0x2a82aa7
2a82a3c: test byte ptr [rsp + 0x38], 1
2a82a41: jne 0x2a82ac1
2a82a43: test byte ptr [rsp], 1
2a82a47: je 0x2a82a5b
2a82a49: mov rsi, qword ptr [rsp]
2a82a4d: mov rdi, qword ptr [rsp + 0x10]
2a82a52: and rsi, 0xfffffffffffffffe
2a82a56: call 0x498cfc8
2a82a5b: mov rax, rbx
2a82a5e: add rsp, 0x98
2a82a65: pop rbx
2a82a66: pop r12
2a82a68: pop r13
2a82a6a: pop r14
2a82a6c: pop r15
2a82a6e: pop rbp
2a82a6f: ret
2a82a70: mov rsi, qword ptr [rsp + 0x78]
2a82a75: mov rdi, qword ptr [rsp + 0x88]
2a82a7d: and rsi, 0xfffffffffffffffe
2a82a81: call 0x498cfc8
2a82a86: test byte ptr [rsp + 0x60], 1
2a82a8b: je 0x2a82a35
2a82a8d: mov rsi, qword ptr [rsp + 0x60]
2a82a92: mov rdi, qword ptr [rsp + 0x70]
2a82a97: and rsi, 0xfffffffffffffffe
2a82a9b: call 0x498cfc8
2a82aa0: test byte ptr [rsp + 0x20], 1
2a82aa5: je 0x2a82a3c
2a82aa7: mov rsi, qword ptr [rsp + 0x20]
2a82aac: mov rdi, qword ptr [rsp + 0x30]
2a82ab1: and rsi, 0xfffffffffffffffe
2a82ab5: call 0x498cfc8
2a82aba: test byte ptr [rsp + 0x38], 1
2a82abf: je 0x2a82a43
2a82ac1: mov rsi, qword ptr [rsp + 0x38]
2a82ac6: mov rdi, qword ptr [rsp + 0x48]
2a82acb: and rsi, 0xfffffffffffffffe
2a82acf: call 0x498cfc8
2a82ad4: test byte ptr [rsp], 1
2a82ad8: jne 0x2a82a49
2a82ade: jmp 0x2a82a5b
2a82ae3: lea rdi, [rsp + 1]
2a82ae8: test al, 1
2a82aea: je 0x2a82a02
2a82af0: mov rsi, qword ptr [rsp + 0x88]
2a82af8: call 0x4a07bb0
2a82afd: test eax, eax
2a82aff: jne 0x2a82a14
2a82b05: test bl, 1
2a82b08: je 0x2a82b24
2a82b0a: mov rsi, qword ptr [rsp + 0x70]
2a82b0f: mov rbx, qword ptr [rsp + 0x18]
2a82b14: mov rdi, rbx
2a82b17: mov rdx, r14
2a82b1a: call 0x18f86e0
2a82b1f: jmp 0x2a82a27
2a82b24: mov rax, qword ptr [rsp + 0x70]
2a82b29: mov rbx, qword ptr [rsp + 0x18]
2a82b2e: mov qword ptr [rbx + 0x10], rax
2a82b32: movups xmm0, xmmword ptr [rsp + 0x60]
2a82b37: movups xmmword ptr [rbx], xmm0
2a82b3a: jmp 0x2a82a27
2a82b3f: call 0x18f8660
2a82b44: call 0x191b050
2a82b49: call 0x18f8660
2a82b4e: call 0x191b050
2a82b53: call 0x18f8660
2a82b58: mov rbx, rax
2a82b5b: lea rdi, [rsp + 0x60]
2a82b60: call 0x1a680c0
2a82b65: jmp 0x2a82b6a
2a82b67: mov rbx, rax
2a82b6a: test byte ptr [rsp + 0x20], 1
2a82b6f: jne 0x2a82b86
2a82b71: test byte ptr [rsp + 0x38], 1
2a82b76: jne 0x2a82bac
2a82b78: test byte ptr [rsp], 1
2a82b7c: jne 0x2a82bd0
2a82b7e: mov rdi, rbx
2a82b81: call 0x4a07a10
2a82b86: mov rsi, qword ptr [rsp + 0x20]
2a82b8b: mov rdi, qword ptr [rsp + 0x30]
2a82b90: and rsi, 0xfffffffffffffffe
2a82b94: call 0x498cfc8
2a82b99: test byte ptr [rsp + 0x38], 1
2a82b9e: je 0x2a82b78
2a82ba0: jmp 0x2a82bac
2a82ba2: mov rbx, rax
2a82ba5: test byte ptr [rsp + 0x38], 1
2a82baa: je 0x2a82b78
2a82bac: mov rsi, qword ptr [rsp + 0x38]
2a82bb1: mov rdi, qword ptr [rsp + 0x48]
2a82bb6: and rsi, 0xfffffffffffffffe
2a82bba: call 0x498cfc8
2a82bbf: test byte ptr [rsp], 1
2a82bc3: je 0x2a82b7e
2a82bc5: jmp 0x2a82bd0
2a82bc7: mov rbx, rax
2a82bca: test byte ptr [rsp], 1
2a82bce: je 0x2a82b7e
2a82bd0: mov rsi, qword ptr [rsp]
2a82bd4: mov rdi, qword ptr [rsp + 0x10]
2a82bd9: and rsi, 0xfffffffffffffffe
2a82bdd: call 0x498cfc8
2a82be2: mov rdi, rbx
2a82be5: call 0x4a07a10
2a82bea: nop word ptr [rax + rax]

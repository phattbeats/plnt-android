2a7f780: push rbp
2a7f781: push r15
2a7f783: push r14
2a7f785: push r13
2a7f787: push r12
2a7f789: push rbx
2a7f78a: push rax
2a7f78b: mov qword ptr [rsp], rdx
2a7f78f: mov r15, rsi
2a7f792: mov rbx, rdi
2a7f795: xorps xmm0, xmm0
2a7f798: movups xmmword ptr [rdi], xmm0
2a7f79b: mov qword ptr [rdi + 0x10], 0
2a7f7a3: movzx ebp, byte ptr [rsi]
2a7f7a6: test bpl, 1
2a7f7aa: je 0x2a7f7b2
2a7f7ac: mov r13, qword ptr [r15 + 8]
2a7f7b0: jmp 0x2a7f7b8
2a7f7b2: mov r13d, ebp
2a7f7b5: shr r13d, 1
2a7f7b8: cmp r13, -9
2a7f7bc: jae 0x2a7f883
2a7f7c2: cmp r13, 0x17
2a7f7c6: jb 0x2a7f80e
2a7f7c8: mov rax, r13
2a7f7cb: and rax, 0xfffffffffffffff8
2a7f7cf: add rax, 8
2a7f7d3: cmp rax, 0x18
2a7f7d7: mov r12d, 0x1a
2a7f7dd: cmovne r12, rax
2a7f7e1: mov rdi, r12
2a7f7e4: call 0x4a078f0
2a7f7e9: movzx ecx, byte ptr [rbx + 1]
2a7f7ed: mov byte ptr [rax], cl
2a7f7ef: or r12, 1
2a7f7f3: mov qword ptr [rbx], r12
2a7f7f6: mov qword ptr [rbx + 8], 0
2a7f7fe: mov qword ptr [rbx + 0x10], rax
2a7f802: test bpl, 1
2a7f806: je 0x2a7f829
2a7f808: mov r15, qword ptr [r15 + 0x10]
2a7f80c: jmp 0x2a7f82c
2a7f80e: test bpl, 1
2a7f812: je 0x2a7f81f
2a7f814: mov r15, qword ptr [r15 + 0x10]
2a7f818: test r13, r13
2a7f81b: jne 0x2a7f82c
2a7f81d: jmp 0x2a7f871
2a7f81f: inc r15
2a7f822: test r13, r13
2a7f825: jne 0x2a7f82c
2a7f827: jmp 0x2a7f871
2a7f829: inc r15
2a7f82c: xor ebp, ebp
2a7f82e: lea r12, [rip + 0x2260eab]
2a7f835: nop word ptr cs:[rax + rax]
2a7f840: movzx r14d, byte ptr [r15 + rbp]
2a7f845: mov rdi, qword ptr [rsp]
2a7f849: mov rsi, r12
2a7f84c: call 0x49eb1d0
2a7f851: mov rcx, qword ptr [rax]
2a7f854: movsx esi, r14b
2a7f858: mov rdi, rax
2a7f85b: call qword ptr [rcx + 0x28]
2a7f85e: movsx esi, al
2a7f861: mov rdi, rbx
2a7f864: call 0x49acac0
2a7f869: inc rbp
2a7f86c: cmp r13, rbp
2a7f86f: jne 0x2a7f840
2a7f871: mov rax, rbx
2a7f874: add rsp, 8
2a7f878: pop rbx
2a7f879: pop r12
2a7f87b: pop r13
2a7f87d: pop r14
2a7f87f: pop r15
2a7f881: pop rbp
2a7f882: ret
2a7f883: call 0x18f8660
2a7f888: mov rdi, rax
2a7f88b: call 0x18f83c0
2a7f890: mov rdi, rax
2a7f893: call 0x18f83c0
2a7f898: nop dword ptr [rax + rax]

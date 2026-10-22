2a812b0: push rbp
2a812b1: push r15
2a812b3: push r14
2a812b5: push r13
2a812b7: push r12
2a812b9: push rbx
2a812ba: sub rsp, 0x68
2a812be: mov rbx, rdi
2a812c1: movsx eax, byte ptr [rsp + 0xa0]
2a812c9: cmp eax, 2
2a812cc: je 0x2a812e6
2a812ce: cmp eax, 1
2a812d1: jne 0x2a81300
2a812d3: mov r15, rdx
2a812d6: movzx ebp, byte ptr [r8]
2a812da: test bpl, 1
2a812de: je 0x2a81313
2a812e0: mov r12, qword ptr [r8 + 8]
2a812e4: jmp 0x2a81319
2a812e6: test byte ptr [rcx], 1
2a812e9: je 0x2a81341
2a812eb: mov rdx, qword ptr [rcx + 8]
2a812ef: mov rsi, qword ptr [rcx + 0x10]
2a812f3: mov rdi, rbx
2a812f6: call 0x18f86e0
2a812fb: jmp 0x2a814aa
2a81300: xorps xmm0, xmm0
2a81303: movups xmmword ptr [rbx], xmm0
2a81306: mov qword ptr [rbx + 0x10], 0
2a8130e: jmp 0x2a814aa
2a81313: mov r12d, ebp
2a81316: shr r12d, 1
2a81319: lea r14, [r12 + 0xd]
2a8131e: cmp r14, -9
2a81322: jae 0x2a814bc
2a81328: mov qword ptr [rsp + 0x48], r9
2a8132d: mov rcx, qword ptr [r8 + 0x10]
2a81331: cmp r14, 0x16
2a81335: ja 0x2a81354
2a81337: add r14b, r14b
2a8133a: mov byte ptr [rsp + 8], r14b
2a8133f: jmp 0x2a8139f
2a81341: mov rax, qword ptr [rcx + 0x10]
2a81345: mov qword ptr [rbx + 0x10], rax
2a81349: movups xmm0, xmmword ptr [rcx]
2a8134c: movups xmmword ptr [rbx], xmm0
2a8134f: jmp 0x2a814aa
2a81354: mov qword ptr [rsp + 0x38], rcx
2a81359: mov qword ptr [rsp + 0x40], r8
2a8135e: mov rax, r14
2a81361: and rax, 0xfffffffffffffff8
2a81365: add rax, 8
2a81369: cmp rax, 0x18
2a8136d: mov r13d, 0x1a
2a81373: cmovne r13, rax
2a81377: mov rdi, r13
2a8137a: call 0x4a078f0
2a8137f: or r13, 1
2a81383: mov qword ptr [rsp + 8], r13
2a81388: mov qword ptr [rsp + 0x10], r14
2a8138d: mov qword ptr [rsp + 0x18], rax
2a81392: mov r14d, r13d
2a81395: mov r8, qword ptr [rsp + 0x40]
2a8139a: mov rcx, qword ptr [rsp + 0x38]
2a8139f: inc r8
2a813a2: test bpl, 1
2a813a6: cmovne r8, rcx
2a813aa: lea r13, [rsp + 9]
2a813af: test r14b, 1
2a813b3: cmovne r13, rax
2a813b7: mov rdi, r13
2a813ba: mov rsi, r8
2a813bd: mov rdx, r12
2a813c0: call 0x4a07a30
2a813c5: movabs rax, 0x5073736150337374
2a813cf: mov qword ptr [r13 + r12], rax
2a813d4: movabs rax, 0x6573617268507373
2a813de: mov qword ptr [r13 + r12 + 5], rax
2a813e3: mov byte ptr [r13 + r12 + 0xd], 0
2a813e9: movzx edx, byte ptr [r15]
2a813ed: mov eax, edx
2a813ef: lea rsi, [r15 + 1]
2a813f3: shr edx, 1
2a813f5: test al, 1
2a813f7: cmovne rsi, qword ptr [r15 + 0x10]
2a813fc: cmovne rdx, qword ptr [r15 + 8]
2a81401: lea rdi, [rsp + 8]
2a81406: call 0x49ac7b0
2a8140b: mov rcx, qword ptr [rax + 0x10]
2a8140f: mov qword ptr [rsp + 0x30], rcx
2a81414: movups xmm0, xmmword ptr [rax]
2a81417: movaps xmmword ptr [rsp + 0x20], xmm0
2a8141c: xorps xmm0, xmm0
2a8141f: movups xmmword ptr [rax], xmm0
2a81422: mov qword ptr [rax + 0x10], 0
2a8142a: mov r14, qword ptr [rsp + 0x48]
2a8142f: mov rax, qword ptr [r14]
2a81432: lea rdi, [rsp + 0x50]
2a81437: lea rdx, [rsp + 0x20]
2a8143c: mov rsi, r14
2a8143f: call qword ptr [rax + 0x18]
2a81442: test byte ptr [rsp + 0x20], 1
2a81447: je 0x2a8145c
2a81449: mov rsi, qword ptr [rsp + 0x20]
2a8144e: mov rdi, qword ptr [rsp + 0x30]
2a81453: and rsi, 0xfffffffffffffffe
2a81457: call 0x498cfc8
2a8145c: test byte ptr [rsp + 8], 1
2a81461: je 0x2a81476
2a81463: mov rsi, qword ptr [rsp + 8]
2a81468: mov rdi, qword ptr [rsp + 0x18]
2a8146d: and rsi, 0xfffffffffffffffe
2a81471: call 0x498cfc8
2a81476: mov rax, qword ptr [r14]
2a81479: lea rcx, [rsp + 0x50]
2a8147e: mov rdi, rbx
2a81481: mov rsi, r14
2a81484: mov rdx, r15
2a81487: mov r8d, 0x2710
2a8148d: call qword ptr [rax + 0x20]
2a81490: test byte ptr [rsp + 0x50], 1
2a81495: je 0x2a814aa
2a81497: mov rsi, qword ptr [rsp + 0x50]
2a8149c: mov rdi, qword ptr [rsp + 0x60]
2a814a1: and rsi, 0xfffffffffffffffe
2a814a5: call 0x498cfc8
2a814aa: mov rax, rbx
2a814ad: add rsp, 0x68
2a814b1: pop rbx
2a814b2: pop r12
2a814b4: pop r13
2a814b6: pop r14
2a814b8: pop r15
2a814ba: pop rbp
2a814bb: ret
2a814bc: call 0x18f8660
2a814c1: mov rdi, rax
2a814c4: call 0x18f83c0
2a814c9: nop dword ptr [rax]

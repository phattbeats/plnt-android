2a80410: push rbp
2a80411: push r15
2a80413: push r14
2a80415: push r13
2a80417: push r12
2a80419: push rbx
2a8041a: sub rsp, 0x78
2a8041e: mov rbx, rdi
2a80421: mov r15, qword ptr [rsi]
2a80424: test r15, r15
2a80427: je 0x2a8045e
2a80429: mov r12, rdx
2a8042c: movzx eax, byte ptr [rdx]
2a8042f: test al, 1
2a80431: je 0x2a80452
2a80433: mov r14, qword ptr [r12 + 8]
2a80438: cmp r14, 0x40
2a8043c: jbe 0x2a8045e
2a8043e: lea r13, [r14 - 0x40]
2a80442: test al, 1
2a80444: mov qword ptr [rsp + 0x40], rsi
2a80449: je 0x2a80471
2a8044b: mov r12, qword ptr [r12 + 0x10]
2a80450: jmp 0x2a80474
2a80452: mov r14d, eax
2a80455: shr r14d, 1
2a80458: cmp r14, 0x40
2a8045c: ja 0x2a8043e
2a8045e: xorps xmm0, xmm0
2a80461: movups xmmword ptr [rbx], xmm0
2a80464: mov qword ptr [rbx + 0x10], 0
2a8046c: jmp 0x2a80675
2a80471: inc r12
2a80474: mov edi, 0x48
2a80479: call 0x4a078f0
2a8047e: movups xmm0, xmmword ptr [r12 + r14 - 0x40]
2a80484: movups xmm1, xmmword ptr [r12 + r14 - 0x30]
2a8048a: movups xmm2, xmmword ptr [r12 + r14 - 0x20]
2a80490: movups xmm3, xmmword ptr [r12 + r14 - 0x10]
2a80496: movups xmmword ptr [rax], xmm0
2a80499: movups xmmword ptr [rax + 0x30], xmm3
2a8049d: movups xmmword ptr [rax + 0x20], xmm2
2a804a1: movups xmmword ptr [rax + 0x10], xmm1
2a804a5: mov qword ptr [rsp], rax
2a804a9: mov byte ptr [rax + 0x40], 0
2a804ad: cmp r13, 0x16
2a804b1: ja 0x2a804c6
2a804b3: lea eax, [r13*2]
2a804bb: mov byte ptr [rsp + 8], al
2a804bf: lea rbp, [rsp + 9]
2a804c4: jmp 0x2a804fd
2a804c6: mov rax, r13
2a804c9: and rax, 0xfffffffffffffff8
2a804cd: add rax, 8
2a804d1: cmp rax, 0x18
2a804d5: mov r14d, 0x1a
2a804db: cmovne r14, rax
2a804df: mov rdi, r14
2a804e2: call 0x4a078f0
2a804e7: mov rbp, rax
2a804ea: or r14, 1
2a804ee: mov qword ptr [rsp + 8], r14
2a804f3: mov qword ptr [rsp + 0x10], r13
2a804f8: mov qword ptr [rsp + 0x18], rax
2a804fd: mov rdi, rbp
2a80500: mov rsi, r12
2a80503: mov rdx, r13
2a80506: call 0x4a07a30
2a8050b: mov byte ptr [rbp + r13], 0
2a80511: movzx eax, byte ptr [rsp + 8]
2a80516: test al, 1
2a80518: je 0x2a80573
2a8051a: mov rax, qword ptr [rsp + 0x10]
2a8051f: test rax, rax
2a80522: je 0x2a8057a
2a80524: mov rax, qword ptr [r15]
2a80527: lea rdi, [rsp + 0x60]
2a8052c: lea rdx, [rsp + 8]
2a80531: mov rsi, r15
2a80534: call qword ptr [rax + 0x58]
2a80537: movups xmm0, xmmword ptr [rsp + 0x60]
2a8053c: movaps xmmword ptr [rsp + 0x20], xmm0
2a80541: mov rax, qword ptr [rsp + 0x70]
2a80546: mov qword ptr [rsp + 0x30], rax
2a8054b: mov word ptr [rsp + 0x60], 0
2a80552: movzx eax, byte ptr [rsp + 0x20]
2a80557: test al, 1
2a80559: je 0x2a8059c
2a8055b: mov rax, qword ptr [rsp + 0x28]
2a80560: mov rcx, qword ptr [rsp + 0x40]
2a80565: test rax, rax
2a80568: mov r15, qword ptr [rsp]
2a8056c: jne 0x2a805ac
2a8056e: jmp 0x2a80626
2a80573: shr eax, 1
2a80575: test rax, rax
2a80578: jne 0x2a80524
2a8057a: xorps xmm0, xmm0
2a8057d: movups xmmword ptr [rbx], xmm0
2a80580: mov qword ptr [rbx + 0x10], 0
2a80588: mov r15, qword ptr [rsp]
2a8058c: test byte ptr [rsp + 8], 1
2a80591: jne 0x2a80655
2a80597: jmp 0x2a80668
2a8059c: shr eax, 1
2a8059e: mov rcx, qword ptr [rsp + 0x40]
2a805a3: test rax, rax
2a805a6: mov r15, qword ptr [rsp]
2a805aa: je 0x2a80626
2a805ac: mov rsi, qword ptr [rcx]
2a805af: mov rax, qword ptr [rsi]
2a805b2: lea rdi, [rsp + 0x48]
2a805b7: lea rdx, [rsp + 0x20]
2a805bc: call qword ptr [rax + 0x18]
2a805bf: movzx ebp, byte ptr [rsp + 0x48]
2a805c4: test bpl, 1
2a805c8: je 0x2a805da
2a805ca: mov rax, qword ptr [rsp + 0x50]
2a805cf: mov r14b, 1
2a805d2: cmp rax, 0x40
2a805d6: je 0x2a805e7
2a805d8: jmp 0x2a8060c
2a805da: mov eax, ebp
2a805dc: shr eax, 1
2a805de: mov r14b, 1
2a805e1: cmp rax, 0x40
2a805e5: jne 0x2a8060c
2a805e7: test bpl, 1
2a805eb: je 0x2a805f4
2a805ed: mov rsi, qword ptr [rsp + 0x58]
2a805f2: jmp 0x2a805f9
2a805f4: lea rsi, [rsp + 0x49]
2a805f9: mov edx, 0x40
2a805fe: mov rdi, r15
2a80601: call 0x4a07bb0
2a80606: test eax, eax
2a80608: setne r14b
2a8060c: test bpl, 1
2a80610: jne 0x2a80687
2a80612: test byte ptr [rsp + 0x60], 1
2a80617: jne 0x2a806a5
2a8061d: test r14b, r14b
2a80620: je 0x2a806c1
2a80626: xorps xmm0, xmm0
2a80629: movups xmmword ptr [rbx], xmm0
2a8062c: mov qword ptr [rbx + 0x10], 0
2a80634: test byte ptr [rsp + 0x20], 1
2a80639: je 0x2a8064e
2a8063b: mov rsi, qword ptr [rsp + 0x20]
2a80640: mov rdi, qword ptr [rsp + 0x30]
2a80645: and rsi, 0xfffffffffffffffe
2a80649: call 0x498cfc8
2a8064e: test byte ptr [rsp + 8], 1
2a80653: je 0x2a80668
2a80655: mov rsi, qword ptr [rsp + 8]
2a8065a: mov rdi, qword ptr [rsp + 0x18]
2a8065f: and rsi, 0xfffffffffffffffe
2a80663: call 0x498cfc8
2a80668: mov esi, 0x48
2a8066d: mov rdi, r15
2a80670: call 0x498cfc8
2a80675: mov rax, rbx
2a80678: add rsp, 0x78
2a8067c: pop rbx
2a8067d: pop r12
2a8067f: pop r13
2a80681: pop r14
2a80683: pop r15
2a80685: pop rbp
2a80686: ret
2a80687: mov rsi, qword ptr [rsp + 0x48]
2a8068c: mov rdi, qword ptr [rsp + 0x58]
2a80691: and rsi, 0xfffffffffffffffe
2a80695: call 0x498cfc8
2a8069a: test byte ptr [rsp + 0x60], 1
2a8069f: je 0x2a8061d
2a806a5: mov rsi, qword ptr [rsp + 0x60]
2a806aa: mov rdi, qword ptr [rsp + 0x70]
2a806af: and rsi, 0xfffffffffffffffe
2a806b3: call 0x498cfc8
2a806b8: test r14b, r14b
2a806bb: jne 0x2a80626
2a806c1: mov rax, qword ptr [rsp + 0x30]
2a806c6: mov qword ptr [rbx + 0x10], rax
2a806ca: movaps xmm0, xmmword ptr [rsp + 0x20]
2a806cf: movups xmmword ptr [rbx], xmm0
2a806d2: test byte ptr [rsp + 8], 1
2a806d7: jne 0x2a80655
2a806dd: jmp 0x2a80668
2a806df: mov rbx, rax
2a806e2: mov esi, 0x48
2a806e7: mov rdi, qword ptr [rsp]
2a806eb: call 0x498cfc8
2a806f0: mov rdi, rbx
2a806f3: call 0x4a07a10
2a806f8: nop dword ptr [rax + rax]

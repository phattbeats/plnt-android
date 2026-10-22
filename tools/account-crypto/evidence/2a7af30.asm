2a7af30: push rbp
2a7af31: push r15
2a7af33: push r14
2a7af35: push r13
2a7af37: push r12
2a7af39: push rbx
2a7af3a: sub rsp, 0xb8
2a7af41: mov qword ptr [rsp + 0x68], r9
2a7af46: mov qword ptr [rsp + 0x88], rcx
2a7af4e: mov qword ptr [rsp + 0x18], rdx
2a7af53: mov qword ptr [rsp + 0x38], rdi
2a7af58: mov rax, qword ptr [rsi + 8]
2a7af5c: movups xmm0, xmmword ptr [rsi]
2a7af5f: test rax, rax
2a7af62: je 0x2a7af69
2a7af64: lock inc qword ptr [rax + 8]
2a7af69: mov rax, qword ptr [rsp + 0x38]
2a7af6e: mov r14, qword ptr [rax + 0x40]
2a7af72: movups xmmword ptr [rax + 0x38], xmm0
2a7af76: test r14, r14
2a7af79: je 0x2a7afa4
2a7af7b: mov rax, 0xffffffffffffffff
2a7af82: lock xadd qword ptr [r14 + 8], rax
2a7af88: test rax, rax
2a7af8b: jne 0x2a7afa4
2a7af8d: mov rax, qword ptr [r14]
2a7af90: mov rdi, r14
2a7af93: mov rbx, rsi
2a7af96: call qword ptr [rax + 0x10]
2a7af99: mov rdi, r14
2a7af9c: call 0x49a8780
2a7afa1: mov rsi, rbx
2a7afa4: mov rax, qword ptr [rsi]
2a7afa7: lea rcx, [rax + 0x18]
2a7afab: mov rsi, qword ptr [rax + 0x18]
2a7afaf: mov edx, esi
2a7afb1: add rsi, 7
2a7afb5: test dl, 1
2a7afb8: cmove rsi, rcx
2a7afbc: movsxd rcx, dword ptr [rax + 0x20]
2a7afc0: mov al, 1
2a7afc2: test rcx, rcx
2a7afc5: je 0x2a7b506
2a7afcb: lea rax, [rsi + rcx*8]
2a7afcf: mov qword ptr [rsp + 0x90], rax
2a7afd7: mov rax, qword ptr [rsp + 0x38]
2a7afdc: add rax, 0x18
2a7afe0: mov qword ptr [rsp + 0x98], rax
2a7afe8: jmp 0x2a7b00a
2a7afea: nop word ptr [rax + rax]
2a7aff0: mov rsi, qword ptr [rsp + 0xa0]
2a7aff8: add rsi, 8
2a7affc: cmp rsi, qword ptr [rsp + 0x90]
2a7b004: je 0x2a7b5c7
2a7b00a: mov qword ptr [rsp + 0xa0], rsi
2a7b012: mov rbx, qword ptr [rsi]
2a7b015: mov r13d, dword ptr [rbx + 0x38]
2a7b019: cmp byte ptr [rbx + 0x3c], 1
2a7b01d: jne 0x2a7b068
2a7b01f: mov edi, r13d
2a7b022: call 0x2a1b850
2a7b027: mov dword ptr [rsp + 0x40], eax
2a7b02b: mov rdx, qword ptr [rsp + 0x68]
2a7b030: mov rcx, qword ptr [rdx + 8]
2a7b034: mov qword ptr [rsp], rcx
2a7b038: cmp rcx, qword ptr [rdx + 0x10]
2a7b03c: mov rsi, rsp
2a7b03f: mov qword ptr [rsp + 0x20], rsi
2a7b044: lea rsi, [rsp + 0x40]
2a7b049: mov qword ptr [rsp + 0x28], rsi
2a7b04e: mov qword ptr [rsp + 0x30], rdx
2a7b053: jae 0x2a7b45e
2a7b059: mov dword ptr [rcx], eax
2a7b05b: add rcx, 4
2a7b05f: mov rax, qword ptr [rsp + 0x68]
2a7b064: mov qword ptr [rax + 8], rcx
2a7b068: mov edi, r13d
2a7b06b: call 0x2a1b850
2a7b070: mov dword ptr [rsp + 0x40], eax
2a7b074: mov rdx, qword ptr [rsp + 0x38]
2a7b079: mov rcx, qword ptr [rdx + 0x20]
2a7b07d: mov qword ptr [rsp], rcx
2a7b081: cmp rcx, qword ptr [rdx + 0x28]
2a7b085: mov rdx, rsp
2a7b088: mov qword ptr [rsp + 0x20], rdx
2a7b08d: lea rdx, [rsp + 0x40]
2a7b092: mov qword ptr [rsp + 0x28], rdx
2a7b097: mov rdx, qword ptr [rsp + 0x98]
2a7b09f: mov qword ptr [rsp + 0x30], rdx
2a7b0a4: jae 0x2a7b44b
2a7b0aa: mov dword ptr [rcx], eax
2a7b0ac: add rcx, 4
2a7b0b0: mov rax, qword ptr [rsp + 0x38]
2a7b0b5: mov qword ptr [rax + 0x20], rcx
2a7b0b9: lea rax, [rbx + 0x18]
2a7b0bd: mov rdx, qword ptr [rbx + 0x18]
2a7b0c1: mov ecx, edx
2a7b0c3: add rdx, 7
2a7b0c7: test cl, 1
2a7b0ca: cmove rdx, rax
2a7b0ce: mov qword ptr [rsp + 0xa8], rdx
2a7b0d6: movsxd rax, dword ptr [rbx + 0x20]
2a7b0da: mov qword ptr [rsp + 0x70], rax
2a7b0df: test rax, rax
2a7b0e2: je 0x2a7aff0
2a7b0e8: shl qword ptr [rsp + 0x70], 3
2a7b0ee: xor ebx, ebx
2a7b0f0: jmp 0x2a7b10f
2a7b0f2: nop word ptr cs:[rax + rax]
2a7b100: add rbx, 8
2a7b104: cmp qword ptr [rsp + 0x70], rbx
2a7b109: je 0x2a7aff0
2a7b10f: mov rax, qword ptr [rsp + 0xa8]
2a7b117: mov rax, qword ptr [rax + rbx]
2a7b11b: mov rax, qword ptr [rax + 0x28]
2a7b11f: and rax, 0xfffffffffffffffc
2a7b123: test byte ptr [rax], 1
2a7b126: je 0x2a7b140
2a7b128: mov rdx, qword ptr [rax + 8]
2a7b12c: mov rsi, qword ptr [rax + 0x10]
2a7b130: lea rdi, [rsp + 0x20]
2a7b135: call 0x18f86e0
2a7b13a: jmp 0x2a7b151
2a7b13c: nop dword ptr [rax]
2a7b140: mov rcx, qword ptr [rax + 0x10]
2a7b144: mov qword ptr [rsp + 0x30], rcx
2a7b149: movups xmm0, xmmword ptr [rax]
2a7b14c: movaps xmmword ptr [rsp + 0x20], xmm0
2a7b151: mov edi, 0x60
2a7b156: call 0x4a078f0
2a7b15b: mov r15, rax
2a7b15e: mov rdi, rax
2a7b161: xor esi, esi
2a7b163: call 0x46cfdf0
2a7b168: mov qword ptr [rsp + 0x78], r15
2a7b16d: cmp r13d, 4
2a7b171: jne 0x2a7b1b0
2a7b173: movzx edx, byte ptr [rsp + 0x20]
2a7b178: mov eax, edx
2a7b17a: shr edx, 1
2a7b17c: test al, 1
2a7b17e: mov rsi, qword ptr [rsp + 0x30]
2a7b183: lea rax, [rsp + 0x21]
2a7b188: cmove rsi, rax
2a7b18c: cmovne rdx, qword ptr [rsp + 0x28]
2a7b192: mov rdi, r15
2a7b195: call 0x43027a0
2a7b19a: test al, al
2a7b19c: jne 0x2a7b290
2a7b1a2: jmp 0x2a7b557
2a7b1a7: nop word ptr [rax + rax]
2a7b1b0: xorps xmm0, xmm0
2a7b1b3: movaps xmmword ptr [rsp], xmm0
2a7b1b7: mov qword ptr [rsp + 0x10], 0
2a7b1c0: mov rax, qword ptr [rsp + 0x38]
2a7b1c5: mov rsi, qword ptr [rax + 8]
2a7b1c9: lea rdi, [rsp + 0x40]
2a7b1ce: lea rdx, [rsp + 0x20]
2a7b1d3: call 0x2a803f0
2a7b1d8: test byte ptr [rsp], 1
2a7b1dc: je 0x2a7b1f0
2a7b1de: mov rsi, qword ptr [rsp]
2a7b1e2: mov rdi, qword ptr [rsp + 0x10]
2a7b1e7: and rsi, 0xfffffffffffffffe
2a7b1eb: call 0x498cfc8
2a7b1f0: movups xmm0, xmmword ptr [rsp + 0x40]
2a7b1f5: movaps xmmword ptr [rsp], xmm0
2a7b1f9: mov rax, qword ptr [rsp + 0x50]
2a7b1fe: mov qword ptr [rsp + 0x10], rax
2a7b203: mov word ptr [rsp + 0x40], 0
2a7b20a: movzx eax, byte ptr [rsp]
2a7b20e: test al, 1
2a7b210: je 0x2a7b230
2a7b212: mov rdx, qword ptr [rsp + 8]
2a7b217: test rdx, rdx
2a7b21a: jne 0x2a7b23d
2a7b21c: jmp 0x2a7b471
2a7b221: nop word ptr cs:[rax + rax]
2a7b230: mov edx, eax
2a7b232: shr edx, 1
2a7b234: test rdx, rdx
2a7b237: je 0x2a7b471
2a7b23d: lea rsi, [rsp + 1]
2a7b242: test al, 1
2a7b244: je 0x2a7b24b
2a7b246: mov rsi, qword ptr [rsp + 0x10]
2a7b24b: mov rdi, r15
2a7b24e: call 0x43027a0
2a7b253: mov ebp, eax
2a7b255: test byte ptr [rsp + 0x40], 1
2a7b25a: je 0x2a7b26f
2a7b25c: mov rsi, qword ptr [rsp + 0x40]
2a7b261: mov rdi, qword ptr [rsp + 0x50]
2a7b266: and rsi, 0xfffffffffffffffe
2a7b26a: call 0x498cfc8
2a7b26f: test bpl, bpl
2a7b272: je 0x2a7b471
2a7b278: test byte ptr [rsp], 1
2a7b27c: je 0x2a7b290
2a7b27e: mov rsi, qword ptr [rsp]
2a7b282: mov rdi, qword ptr [rsp + 0x10]
2a7b287: and rsi, 0xfffffffffffffffe
2a7b28b: call 0x498cfc8
2a7b290: mov qword ptr [rsp + 0xb0], rbx
2a7b298: mov edi, 0x20
2a7b29d: call 0x4a078f0
2a7b2a2: mov r12, rax
2a7b2a5: xorps xmm0, xmm0
2a7b2a8: movups xmmword ptr [rax + 8], xmm0
2a7b2ac: lea rax, [rip + 0x1ffc525]
2a7b2b3: mov qword ptr [r12], rax
2a7b2b7: mov edi, 0x60
2a7b2bc: call 0x4a078f0
2a7b2c1: mov r14, rax
2a7b2c4: mov ebx, r13d
2a7b2c7: mov rdi, rax
2a7b2ca: xor esi, esi
2a7b2cc: call 0x46cfdf0
2a7b2d1: mov r13, r12
2a7b2d4: add r13, 0x18
2a7b2d8: mov qword ptr [r13], r14
2a7b2dc: mov rdi, r14
2a7b2df: mov rsi, r15
2a7b2e2: call 0x46d0ae0
2a7b2e7: mov qword ptr [rsp], r13
2a7b2eb: mov qword ptr [rsp + 8], r12
2a7b2f0: mov rax, qword ptr [rsp + 0x18]
2a7b2f5: mov rbp, qword ptr [rax + 8]
2a7b2f9: mov r14, qword ptr [rax + 0x10]
2a7b2fd: cmp rbp, r14
2a7b300: jae 0x2a7b35d
2a7b302: mov qword ptr [rbp], r13
2a7b306: mov qword ptr [rbp + 8], r12
2a7b30a: add rbp, 0x10
2a7b30e: mov r12, rbp
2a7b311: mov r13d, ebx
2a7b314: mov rax, qword ptr [rsp + 0x18]
2a7b319: mov qword ptr [rax + 8], r12
2a7b31d: mov rdi, r15
2a7b320: call 0x46cff30
2a7b325: mov esi, 0x60
2a7b32a: mov rdi, r15
2a7b32d: call 0x498cfc8
2a7b332: test byte ptr [rsp + 0x20], 1
2a7b337: mov rbx, qword ptr [rsp + 0xb0]
2a7b33f: je 0x2a7b100
2a7b345: mov rsi, qword ptr [rsp + 0x20]
2a7b34a: mov rdi, qword ptr [rsp + 0x30]
2a7b34f: and rsi, 0xfffffffffffffffe
2a7b353: call 0x498cfc8
2a7b358: jmp 0x2a7b100
2a7b35d: mov rax, qword ptr [rsp + 0x18]
2a7b362: mov rdx, qword ptr [rax]
2a7b365: sub rbp, rdx
2a7b368: mov rsi, rbp
2a7b36b: sar rsi, 4
2a7b36f: lea rax, [rsi + 1]
2a7b373: mov rcx, rax
2a7b376: shr rcx, 0x3c
2a7b37a: jne 0x2a7b5d8
2a7b380: mov qword ptr [rsp + 0x58], rsi
2a7b385: mov qword ptr [rsp + 0x80], rdx
2a7b38d: sub r14, rdx
2a7b390: mov rcx, r14
2a7b393: sar rcx, 3
2a7b397: cmp rcx, rax
2a7b39a: cmovbe rcx, rax
2a7b39e: movabs rax, 0x7ffffffffffffff0
2a7b3a8: cmp r14, rax
2a7b3ab: movabs rax, 0xfffffffffffffff
2a7b3b5: cmovae rcx, rax
2a7b3b9: cmp rcx, rax
2a7b3bc: ja 0x2a7b5d3
2a7b3c2: shl rcx, 4
2a7b3c6: mov qword ptr [rsp + 0x60], rcx
2a7b3cb: mov rdi, rcx
2a7b3ce: call 0x4a078f0
2a7b3d3: lea rdi, [rax + rbp]
2a7b3d7: add qword ptr [rsp + 0x60], rax
2a7b3dc: mov qword ptr [rax + rbp], r13
2a7b3e0: mov qword ptr [rax + rbp + 8], r12
2a7b3e5: xorps xmm0, xmm0
2a7b3e8: movaps xmmword ptr [rsp], xmm0
2a7b3ec: lea r12, [rax + rbp]
2a7b3f0: add r12, 0x10
2a7b3f4: mov rax, qword ptr [rsp + 0x58]
2a7b3f9: shl rax, 4
2a7b3fd: sub rdi, rax
2a7b400: mov qword ptr [rsp + 0x58], rdi
2a7b405: mov r13, qword ptr [rsp + 0x80]
2a7b40d: mov rsi, r13
2a7b410: mov rdx, rbp
2a7b413: call 0x4a07ad0
2a7b418: mov rax, qword ptr [rsp + 0x18]
2a7b41d: mov rcx, qword ptr [rsp + 0x58]
2a7b422: mov qword ptr [rax], rcx
2a7b425: mov qword ptr [rax + 8], r12
2a7b429: mov rcx, qword ptr [rsp + 0x60]
2a7b42e: mov qword ptr [rax + 0x10], rcx
2a7b432: test r13, r13
2a7b435: je 0x2a7b311
2a7b43b: mov rdi, r13
2a7b43e: mov rsi, r14
2a7b441: call 0x498cfc8
2a7b446: jmp 0x2a7b311
2a7b44b: lea rdi, [rsp + 0x20]
2a7b450: call 0x2a7e360
2a7b455: mov rcx, qword ptr [rsp]
2a7b459: jmp 0x2a7b0b0
2a7b45e: lea rdi, [rsp + 0x20]
2a7b463: call 0x2a7e360
2a7b468: mov rcx, qword ptr [rsp]
2a7b46c: jmp 0x2a7b05f
2a7b471: mov dword ptr [rsp + 0x40], 4
2a7b479: mov rax, qword ptr [rsp + 0x88]
2a7b481: mov rdi, qword ptr [rax + 0x20]
2a7b485: test rdi, rdi
2a7b488: je 0x2a7b5ce
2a7b48e: mov rax, qword ptr [rdi]
2a7b491: lea rsi, [rsp + 0x40]
2a7b496: call qword ptr [rax + 0x30]
2a7b499: mov rax, qword ptr [rsp + 0x18]
2a7b49e: mov rbx, qword ptr [rax]
2a7b4a1: mov r15, qword ptr [rax + 8]
2a7b4a5: cmp rbx, r15
2a7b4a8: jne 0x2a7b529
2a7b4aa: mov rax, qword ptr [rsp + 0x18]
2a7b4af: mov qword ptr [rax + 8], rbx
2a7b4b3: test byte ptr [rsp], 1
2a7b4b7: je 0x2a7b4cb
2a7b4b9: mov rsi, qword ptr [rsp]
2a7b4bd: mov rdi, qword ptr [rsp + 0x10]
2a7b4c2: and rsi, 0xfffffffffffffffe
2a7b4c6: call 0x498cfc8
2a7b4cb: mov rbx, qword ptr [rsp + 0x78]
2a7b4d0: test rbx, rbx
2a7b4d3: je 0x2a7b4ea
2a7b4d5: mov rdi, rbx
2a7b4d8: call 0x46cff30
2a7b4dd: mov esi, 0x60
2a7b4e2: mov rdi, rbx
2a7b4e5: call 0x498cfc8
2a7b4ea: test byte ptr [rsp + 0x20], 1
2a7b4ef: je 0x2a7b504
2a7b4f1: mov rsi, qword ptr [rsp + 0x20]
2a7b4f6: mov rdi, qword ptr [rsp + 0x30]
2a7b4fb: and rsi, 0xfffffffffffffffe
2a7b4ff: call 0x498cfc8
2a7b504: xor eax, eax
2a7b506: add rsp, 0xb8
2a7b50d: pop rbx
2a7b50e: pop r12
2a7b510: pop r13
2a7b512: pop r14
2a7b514: pop r15
2a7b516: pop rbp
2a7b517: ret
2a7b518: nop dword ptr [rax + rax]
2a7b520: add r15, -0x10
2a7b524: cmp r15, rbx
2a7b527: je 0x2a7b4aa
2a7b529: mov r14, qword ptr [r15 - 8]
2a7b52d: test r14, r14
2a7b530: je 0x2a7b520
2a7b532: mov rax, 0xffffffffffffffff
2a7b539: lock xadd qword ptr [r14 + 8], rax
2a7b53f: test rax, rax
2a7b542: jne 0x2a7b520
2a7b544: mov rax, qword ptr [r14]
2a7b547: mov rdi, r14
2a7b54a: call qword ptr [rax + 0x10]
2a7b54d: mov rdi, r14
2a7b550: call 0x49a8780
2a7b555: jmp 0x2a7b520
2a7b557: mov rax, qword ptr [rsp + 0x18]
2a7b55c: mov rbx, qword ptr [rax]
2a7b55f: mov r15, qword ptr [rax + 8]
2a7b563: cmp rbx, r15
2a7b566: jne 0x2a7b599
2a7b568: mov rax, qword ptr [rsp + 0x18]
2a7b56d: mov qword ptr [rax + 8], rbx
2a7b571: mov rbx, qword ptr [rsp + 0x78]
2a7b576: test rbx, rbx
2a7b579: jne 0x2a7b4d5
2a7b57f: jmp 0x2a7b4ea
2a7b584: nop word ptr cs:[rax + rax]
2a7b590: add r15, -0x10
2a7b594: cmp r15, rbx
2a7b597: je 0x2a7b568
2a7b599: mov r14, qword ptr [r15 - 8]
2a7b59d: test r14, r14
2a7b5a0: je 0x2a7b590
2a7b5a2: mov rax, 0xffffffffffffffff
2a7b5a9: lock xadd qword ptr [r14 + 8], rax
2a7b5af: test rax, rax
2a7b5b2: jne 0x2a7b590
2a7b5b4: mov rax, qword ptr [r14]
2a7b5b7: mov rdi, r14
2a7b5ba: call qword ptr [rax + 0x10]
2a7b5bd: mov rdi, r14
2a7b5c0: call 0x49a8780
2a7b5c5: jmp 0x2a7b590
2a7b5c7: mov al, 1
2a7b5c9: jmp 0x2a7b506
2a7b5ce: call 0x198f9c0
2a7b5d3: call 0x1906330
2a7b5d8: call 0x2a6dca0
2a7b5dd: jmp 0x2a7b5df
2a7b5df: mov rbx, rax
2a7b5e2: mov rdi, rsp
2a7b5e5: call 0x2a6d660
2a7b5ea: jmp 0x2a7b650
2a7b5ec: mov rbx, rax
2a7b5ef: test byte ptr [rsp], 1
2a7b5f3: je 0x2a7b650
2a7b5f5: mov rsi, qword ptr [rsp]
2a7b5f9: mov rdi, qword ptr [rsp + 0x10]
2a7b5fe: and rsi, 0xfffffffffffffffe
2a7b602: jmp 0x2a7b64b
2a7b604: mov rdi, rax
2a7b607: call 0x18f83c0
2a7b60c: mov rdi, rax
2a7b60f: call 0x18f83c0
2a7b614: mov rbx, rax
2a7b617: jmp 0x2a7b63b
2a7b619: mov rbx, rax
2a7b61c: jmp 0x2a7b650
2a7b61e: mov rbx, rax
2a7b621: mov rdi, r13
2a7b624: call 0x2a6db30
2a7b629: jmp 0x2a7b63b
2a7b62b: mov rbx, rax
2a7b62e: mov esi, 0x60
2a7b633: mov rdi, r14
2a7b636: call 0x498cfc8
2a7b63b: mov rdi, r12
2a7b63e: call 0x49a86e0
2a7b643: mov esi, 0x20
2a7b648: mov rdi, r12
2a7b64b: call 0x498cfc8
2a7b650: lea rdi, [rsp + 0x78]
2a7b655: call 0x2a6d760
2a7b65a: jmp 0x2a7b671
2a7b65c: mov rbx, rax
2a7b65f: mov esi, 0x60
2a7b664: mov rdi, r15
2a7b667: call 0x498cfc8
2a7b66c: jmp 0x2a7b671
2a7b66e: mov rbx, rax
2a7b671: test byte ptr [rsp + 0x20], 1
2a7b676: je 0x2a7b68b
2a7b678: mov rsi, qword ptr [rsp + 0x20]
2a7b67d: mov rdi, qword ptr [rsp + 0x30]
2a7b682: and rsi, 0xfffffffffffffffe
2a7b686: call 0x498cfc8
2a7b68b: mov rdi, rbx
2a7b68e: call 0x4a07a10
2a7b693: nop word ptr cs:[rax + rax]

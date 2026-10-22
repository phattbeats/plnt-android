2a21720: push rbp
2a21721: push r15
2a21723: push r14
2a21725: push r13
2a21727: push r12
2a21729: push rbx
2a2172a: sub rsp, 0x138
2a21731: mov r14, rdx
2a21734: mov r15, rsi
2a21737: mov rbx, rdi
2a2173a: add rdi, 0x20
2a2173e: mov qword ptr [rsp + 0x40], rdi
2a21743: mov byte ptr [rsp + 0x48], 1
2a21748: call 0x49b5980
2a2174d: cmp qword ptr [rbx + 0x1a8], 0
2a21755: jne 0x2a217d1
2a21757: mov edi, 0x1c8
2a2175c: call 0x4a078f0
2a21761: mov r12, rax
2a21764: mov edx, 0x1c8
2a21769: mov rdi, rax
2a2176c: xor esi, esi
2a2176e: call 0x4a07ba0
2a21773: mov rdi, r12
2a21776: call 0x49b6070
2a2177b: lea rdi, [r12 + 0x90]
2a21783: xor esi, esi
2a21785: call 0x46d2ec0
2a2178a: mov byte ptr [r12 + 0x1c0], 0
2a21793: mov r13, qword ptr [rbx + 0x1a8]
2a2179a: mov qword ptr [rbx + 0x1a8], r12
2a217a1: test r13, r13
2a217a4: je 0x2a217d1
2a217a6: lea rdi, [r13 + 0x90]
2a217ad: call 0x46d3460
2a217b2: lea rdi, [r13 + 0x58]
2a217b6: call 0x49b4af0
2a217bb: lea rdi, [r13 + 0x28]
2a217bf: call 0x49b4af0
2a217c4: mov esi, 0x1c8
2a217c9: mov rdi, r13
2a217cc: call 0x498cfc8
2a217d1: xorps xmm0, xmm0
2a217d4: movaps xmmword ptr [rsp + 0x20], xmm0
2a217d9: mov qword ptr [rsp + 0x30], 0
2a217e2: lea rax, [rip + 0x202a4f7]
2a217e9: mov qword ptr [rsp + 0x128], rax
2a217f1: lea rax, [rsp + 0x20]
2a217f6: mov qword ptr [rsp + 0x130], rax
2a217fe: mov rdi, qword ptr [rbx + 0xd8]
2a21805: mov rax, qword ptr [rdi]
2a21808: call qword ptr [rax + 0xe8]
2a2180e: test al, al
2a21810: je 0x2a2183f
2a21812: mov rdi, qword ptr [rbx + 0xd8]
2a21819: mov rax, qword ptr [rdi]
2a2181c: lea rdx, [rsp + 0x128]
2a21824: mov rsi, r15
2a21827: call qword ptr [rax + 0x100]
2a2182d: mov ebp, 6
2a21832: test al, al
2a21834: jne 0x2a218ed
2a2183a: jmp 0x2a21b33
2a2183f: mov rax, qword ptr [r15]
2a21842: mov rdi, r15
2a21845: call qword ptr [rax + 0x10]
2a21848: mov r12, rax
2a2184b: mov rax, qword ptr [r15]
2a2184e: mov rdi, r15
2a21851: call qword ptr [rax + 0x18]
2a21854: mov r15, rax
2a21857: cmp rax, -9
2a2185b: jae 0x2a21bd8
2a21861: cmp r15, 0x16
2a21865: ja 0x2a21877
2a21867: mov eax, r15d
2a2186a: add al, r15b
2a2186d: mov byte ptr [rsp], al
2a21870: lea r13, [rsp + 1]
2a21875: jmp 0x2a218ac
2a21877: mov rax, r15
2a2187a: and rax, 0xfffffffffffffff8
2a2187e: add rax, 8
2a21882: cmp rax, 0x18
2a21886: mov ebp, 0x1a
2a2188b: cmovne rbp, rax
2a2188f: mov rdi, rbp
2a21892: call 0x4a078f0
2a21897: mov r13, rax
2a2189a: or rbp, 1
2a2189e: mov qword ptr [rsp], rbp
2a218a2: mov qword ptr [rsp + 8], r15
2a218a7: mov qword ptr [rsp + 0x10], rax
2a218ac: mov rdi, r13
2a218af: mov rsi, r12
2a218b2: mov rdx, r15
2a218b5: call 0x4a07a30
2a218ba: mov byte ptr [r13 + r15], 0
2a218c0: test byte ptr [rsp + 0x20], 1
2a218c5: je 0x2a218da
2a218c7: mov rsi, qword ptr [rsp + 0x20]
2a218cc: mov rdi, qword ptr [rsp + 0x30]
2a218d1: and rsi, 0xfffffffffffffffe
2a218d5: call 0x498cfc8
2a218da: mov rax, qword ptr [rsp + 0x10]
2a218df: mov qword ptr [rsp + 0x30], rax
2a218e4: movups xmm0, xmmword ptr [rsp]
2a218e8: movaps xmmword ptr [rsp + 0x20], xmm0
2a218ed: mov rdi, qword ptr [rbx + 0x1a8]
2a218f4: lea rsi, [rsp + 0x20]
2a218f9: call 0x2a70f20
2a218fe: mov ebp, eax
2a21900: test eax, eax
2a21902: jne 0x2a21b33
2a21908: xorps xmm0, xmm0
2a2190b: movaps xmmword ptr [rsp], xmm0
2a2190f: mov qword ptr [rsp + 0x10], 0
2a21918: movaps xmmword ptr [rsp + 0x90], xmm0
2a21920: mov qword ptr [rsp + 0xa0], 0
2a2192c: movaps xmmword ptr [rsp + 0x70], xmm0
2a21931: mov qword ptr [rsp + 0x80], 0
2a2193d: movaps xmmword ptr [rsp + 0x50], xmm0
2a21942: mov qword ptr [rsp + 0x60], 0
2a2194b: mov rdi, qword ptr [rbx + 0x1a8]
2a21952: mov rsi, rsp
2a21955: call 0x2a72230
2a2195a: mov ebp, 6
2a2195f: test eax, eax
2a21961: jne 0x2a21aff
2a21967: mov rdi, qword ptr [rbx + 0x1a8]
2a2196e: lea rsi, [rsp + 0x90]
2a21976: call 0x2a711d0
2a2197b: test eax, eax
2a2197d: jne 0x2a21aff
2a21983: mov rdi, qword ptr [rbx + 0x1a8]
2a2198a: lea rsi, [rsp + 0x50]
2a2198f: call 0x2a71520
2a21994: test eax, eax
2a21996: jne 0x2a21aff
2a2199c: mov rdi, qword ptr [rbx + 0x1a8]
2a219a3: lea rsi, [rsp + 0x70]
2a219a8: call 0x2a71390
2a219ad: mov rdi, qword ptr [rbx + 0x10]
2a219b1: mov rsi, rsp
2a219b4: lea rdx, [rsp + 0x90]
2a219bc: lea rcx, [rsp + 0x70]
2a219c1: lea r8, [rsp + 0x50]
2a219c6: call 0x2a80ff0
2a219cb: lea rsi, [rsp + 0x40]
2a219d0: mov rdi, rbx
2a219d3: mov edx, eax
2a219d5: call 0x2a21c20
2a219da: mov rdi, qword ptr [rbx + 0x1a8]
2a219e1: mov rax, qword ptr [rbx + 0x18]
2a219e5: movaps xmm0, xmmword ptr [rbx + 0x10]
2a219e9: movaps xmmword ptr [rsp + 0x110], xmm0
2a219f1: test rax, rax
2a219f4: je 0x2a219fb
2a219f6: lock inc qword ptr [rax + 8]
2a219fb: lea rsi, [rsp + 0x110]
2a21a03: call 0x2a752c0
2a21a08: lea rdi, [rsp + 0x110]
2a21a10: call 0x2a49ff0
2a21a15: lea rdx, [rbx + 0x240]
2a21a1c: mov rax, qword ptr [r14 + 8]
2a21a20: movups xmm0, xmmword ptr [r14]
2a21a24: movaps xmmword ptr [rsp + 0x100], xmm0
2a21a2c: test rax, rax
2a21a2f: je 0x2a21a36
2a21a31: lock inc qword ptr [rax + 8]
2a21a36: xorps xmm0, xmm0
2a21a39: movaps xmmword ptr [rsp + 0xf0], xmm0
2a21a41: movaps xmmword ptr [rsp + 0xd0], xmm0
2a21a49: mov qword ptr [rsp + 0xe0], 0
2a21a55: movaps xmmword ptr [rsp + 0xb0], xmm0
2a21a5d: mov qword ptr [rsp + 0xc0], 0
2a21a69: lea rax, [rsp + 0xb0]
2a21a71: lea rsi, [rsp + 0x40]
2a21a76: lea rcx, [rsp + 0x100]
2a21a7e: lea r8, [rsp + 0xf0]
2a21a86: lea r9, [rsp + 0xd0]
2a21a8e: mov rdi, rbx
2a21a91: push rax
2a21a92: push 0
2a21a94: call 0x2a220b0
2a21a99: add rsp, 0x10
2a21a9d: mov ebp, eax
2a21a9f: test byte ptr [rsp + 0xb0], 1
2a21aa7: je 0x2a21ac2
2a21aa9: mov rsi, qword ptr [rsp + 0xb0]
2a21ab1: mov rdi, qword ptr [rsp + 0xc0]
2a21ab9: and rsi, 0xfffffffffffffffe
2a21abd: call 0x498cfc8
2a21ac2: test byte ptr [rsp + 0xd0], 1
2a21aca: je 0x2a21ae5
2a21acc: mov rsi, qword ptr [rsp + 0xd0]
2a21ad4: mov rdi, qword ptr [rsp + 0xe0]
2a21adc: and rsi, 0xfffffffffffffffe
2a21ae0: call 0x498cfc8
2a21ae5: lea rdi, [rsp + 0xf0]
2a21aed: call 0x2a4a020
2a21af2: lea rdi, [rsp + 0x100]
2a21afa: call 0x24ea860
2a21aff: test byte ptr [rsp + 0x50], 1
2a21b04: jne 0x2a21b72
2a21b06: test byte ptr [rsp + 0x70], 1
2a21b0b: jne 0x2a21b8c
2a21b0d: test byte ptr [rsp + 0x90], 1
2a21b15: jne 0x2a21bb0
2a21b1b: test byte ptr [rsp], 1
2a21b1f: je 0x2a21b33
2a21b21: mov rsi, qword ptr [rsp]
2a21b25: mov rdi, qword ptr [rsp + 0x10]
2a21b2a: and rsi, 0xfffffffffffffffe
2a21b2e: call 0x498cfc8
2a21b33: test byte ptr [rsp + 0x20], 1
2a21b38: je 0x2a21b4d
2a21b3a: mov rsi, qword ptr [rsp + 0x20]
2a21b3f: mov rdi, qword ptr [rsp + 0x30]
2a21b44: and rsi, 0xfffffffffffffffe
2a21b48: call 0x498cfc8
2a21b4d: cmp byte ptr [rsp + 0x48], 1
2a21b52: jne 0x2a21b5e
2a21b54: mov rdi, qword ptr [rsp + 0x40]
2a21b59: call 0x49b59b0
2a21b5e: mov eax, ebp
2a21b60: add rsp, 0x138
2a21b67: pop rbx
2a21b68: pop r12
2a21b6a: pop r13
2a21b6c: pop r14
2a21b6e: pop r15
2a21b70: pop rbp
2a21b71: ret
2a21b72: mov rsi, qword ptr [rsp + 0x50]
2a21b77: mov rdi, qword ptr [rsp + 0x60]
2a21b7c: and rsi, 0xfffffffffffffffe
2a21b80: call 0x498cfc8
2a21b85: test byte ptr [rsp + 0x70], 1
2a21b8a: je 0x2a21b0d
2a21b8c: mov rsi, qword ptr [rsp + 0x70]
2a21b91: mov rdi, qword ptr [rsp + 0x80]
2a21b99: and rsi, 0xfffffffffffffffe
2a21b9d: call 0x498cfc8
2a21ba2: test byte ptr [rsp + 0x90], 1
2a21baa: je 0x2a21b1b
2a21bb0: mov rsi, qword ptr [rsp + 0x90]
2a21bb8: mov rdi, qword ptr [rsp + 0xa0]
2a21bc0: and rsi, 0xfffffffffffffffe
2a21bc4: call 0x498cfc8
2a21bc9: test byte ptr [rsp], 1
2a21bcd: jne 0x2a21b21
2a21bd3: jmp 0x2a21b33
2a21bd8: call 0x18f8660
2a21bdd: mov rbx, rax
2a21be0: lea rdi, [r12 + 0x58]
2a21be5: call 0x49b4af0
2a21bea: mov rdi, r12
2a21bed: add rdi, 0x28
2a21bf1: call 0x49b4af0
2a21bf6: jmp 0x2a21bfb
2a21bf8: mov rbx, rax
2a21bfb: mov esi, 0x1c8
2a21c00: mov rdi, r12
2a21c03: call 0x498cfc8
2a21c08: mov rdi, rbx
2a21c0b: call 0x18f83c0
2a21c10: mov rdi, rax
2a21c13: call 0x18f83c0
2a21c18: nop dword ptr [rax + rax]

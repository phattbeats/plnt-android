2a238a0: push rbp
2a238a1: push r15
2a238a3: push r14
2a238a5: push r13
2a238a7: push r12
2a238a9: push rbx
2a238aa: sub rsp, 0xc8
2a238b1: mov qword ptr [rsp + 0x98], r8
2a238b9: mov qword ptr [rsp + 0x80], rcx
2a238c1: mov r13, rdx
2a238c4: mov r12, rsi
2a238c7: mov qword ptr [rsp], rdi
2a238cb: mov rax, qword ptr [rsi]
2a238ce: mov rdi, rsi
2a238d1: call qword ptr [rax + 0x18]
2a238d4: mov ebp, 6
2a238d9: test rax, rax
2a238dc: je 0x2a23fc2
2a238e2: mov rax, qword ptr [r13]
2a238e6: mov rdi, r13
2a238e9: call qword ptr [rax + 0x18]
2a238ec: test rax, rax
2a238ef: je 0x2a23fc2
2a238f5: xorps xmm0, xmm0
2a238f8: movaps xmmword ptr [rsp + 0x60], xmm0
2a238fd: mov qword ptr [rsp + 0x70], 0
2a23906: mov rax, qword ptr [rsp]
2a2390a: mov rdi, qword ptr [rax + 0x1a8]
2a23911: lea rsi, [rsp + 0x60]
2a23916: call 0x2a710f0
2a2391b: test eax, eax
2a2391d: je 0x2a23fd6
2a23923: mov rdi, qword ptr [rsp]
2a23927: mov rax, qword ptr [rdi]
2a2392a: call qword ptr [rax + 0x40]
2a2392d: mov rax, qword ptr [rsp]
2a23931: lea rdi, [rax + 0x20]
2a23935: mov qword ptr [rsp + 0x88], rdi
2a2393d: mov byte ptr [rsp + 0x90], 1
2a23945: call 0x49b5980
2a2394a: mov rax, qword ptr [rsp]
2a2394e: mov rbp, qword ptr [rax + 0x1a8]
2a23955: test rbp, rbp
2a23958: jne 0x2a239e4
2a2395e: mov edi, 0x1c8
2a23963: call 0x4a078f0
2a23968: mov rbp, rax
2a2396b: mov edx, 0x1c8
2a23970: mov rdi, rax
2a23973: xor esi, esi
2a23975: call 0x4a07ba0
2a2397a: mov rdi, rbp
2a2397d: call 0x49b6070
2a23982: lea rdi, [rbp + 0x90]
2a23989: xor esi, esi
2a2398b: call 0x46d2ec0
2a23990: mov byte ptr [rbp + 0x1c0], 0
2a23997: mov rax, qword ptr [rsp]
2a2399b: mov r14, qword ptr [rax + 0x1a8]
2a239a2: mov qword ptr [rax + 0x1a8], rbp
2a239a9: test r14, r14
2a239ac: je 0x2a239e4
2a239ae: lea rdi, [r14 + 0x90]
2a239b5: call 0x46d3460
2a239ba: lea rdi, [r14 + 0x58]
2a239be: call 0x49b4af0
2a239c3: lea rdi, [r14 + 0x28]
2a239c7: call 0x49b4af0
2a239cc: mov esi, 0x1c8
2a239d1: mov rdi, r14
2a239d4: call 0x498cfc8
2a239d9: mov rax, qword ptr [rsp]
2a239dd: mov rbp, qword ptr [rax + 0x1a8]
2a239e4: mov rax, qword ptr [r12]
2a239e8: mov rdi, r12
2a239eb: call qword ptr [rax + 0x10]
2a239ee: mov qword ptr [rsp + 0x58], rax
2a239f3: mov rax, qword ptr [r12]
2a239f7: mov rdi, r12
2a239fa: call qword ptr [rax + 0x18]
2a239fd: mov r14, rax
2a23a00: cmp rax, -0xa
2a23a04: ja 0x2a2403a
2a23a0a: cmp r14, 0x16
2a23a0e: ja 0x2a23a21
2a23a10: mov eax, r14d
2a23a13: add al, r14b
2a23a16: mov byte ptr [rsp + 8], al
2a23a1a: lea rbx, [rsp + 9]
2a23a1f: jmp 0x2a23a58
2a23a21: mov rax, r14
2a23a24: and rax, 0xfffffffffffffff8
2a23a28: add rax, 8
2a23a2c: cmp rax, 0x18
2a23a30: mov r15d, 0x1a
2a23a36: cmovne r15, rax
2a23a3a: mov rdi, r15
2a23a3d: call 0x4a078f0
2a23a42: mov rbx, rax
2a23a45: or r15, 1
2a23a49: mov qword ptr [rsp + 8], r15
2a23a4e: mov qword ptr [rsp + 0x10], r14
2a23a53: mov qword ptr [rsp + 0x18], rax
2a23a58: mov rsi, qword ptr [rsp + 0x58]
2a23a5d: mov rdi, rbx
2a23a60: mov rdx, r14
2a23a63: call 0x4a07a30
2a23a68: mov byte ptr [rbx + r14], 0
2a23a6d: lea rsi, [rsp + 8]
2a23a72: mov rdi, rbp
2a23a75: call 0x2a728f0
2a23a7a: test byte ptr [rsp + 8], 1
2a23a7f: je 0x2a23a94
2a23a81: mov rsi, qword ptr [rsp + 8]
2a23a86: mov rdi, qword ptr [rsp + 0x18]
2a23a8b: and rsi, 0xfffffffffffffffe
2a23a8f: call 0x498cfc8
2a23a94: mov rax, qword ptr [rsp]
2a23a98: mov rax, qword ptr [rax + 0x1a8]
2a23a9f: mov qword ptr [rsp + 0x58], rax
2a23aa4: mov rax, qword ptr [r13]
2a23aa8: mov rdi, r13
2a23aab: call qword ptr [rax + 0x10]
2a23aae: mov r15, rax
2a23ab1: mov rax, qword ptr [r13]
2a23ab5: mov rdi, r13
2a23ab8: call qword ptr [rax + 0x18]
2a23abb: mov r14, rax
2a23abe: cmp rax, -0xa
2a23ac2: ja 0x2a2403a
2a23ac8: cmp r14, 0x16
2a23acc: ja 0x2a23adf
2a23ace: mov eax, r14d
2a23ad1: add al, r14b
2a23ad4: mov byte ptr [rsp + 0x20], al
2a23ad8: lea rbx, [rsp + 0x21]
2a23add: jmp 0x2a23b15
2a23adf: mov rax, r14
2a23ae2: and rax, 0xfffffffffffffff8
2a23ae6: add rax, 8
2a23aea: cmp rax, 0x18
2a23aee: mov ebp, 0x1a
2a23af3: cmovne rbp, rax
2a23af7: mov rdi, rbp
2a23afa: call 0x4a078f0
2a23aff: mov rbx, rax
2a23b02: or rbp, 1
2a23b06: mov qword ptr [rsp + 0x20], rbp
2a23b0b: mov qword ptr [rsp + 0x28], r14
2a23b10: mov qword ptr [rsp + 0x30], rax
2a23b15: mov rdi, rbx
2a23b18: mov rsi, r15
2a23b1b: mov rdx, r14
2a23b1e: call 0x4a07a30
2a23b23: mov byte ptr [rbx + r14], 0
2a23b28: mov rax, qword ptr [r12]
2a23b2c: mov rdi, r12
2a23b2f: call qword ptr [rax + 0x10]
2a23b32: mov r15, rax
2a23b35: mov rax, qword ptr [r12]
2a23b39: mov rdi, r12
2a23b3c: call qword ptr [rax + 0x18]
2a23b3f: mov r14, rax
2a23b42: cmp rax, -0xa
2a23b46: ja 0x2a2403a
2a23b4c: cmp r14, 0x16
2a23b50: ja 0x2a23b63
2a23b52: mov eax, r14d
2a23b55: add al, r14b
2a23b58: mov byte ptr [rsp + 0x40], al
2a23b5c: lea rbx, [rsp + 0x41]
2a23b61: jmp 0x2a23b99
2a23b63: mov rax, r14
2a23b66: and rax, 0xfffffffffffffff8
2a23b6a: add rax, 8
2a23b6e: cmp rax, 0x18
2a23b72: mov ebp, 0x1a
2a23b77: cmovne rbp, rax
2a23b7b: mov rdi, rbp
2a23b7e: call 0x4a078f0
2a23b83: mov rbx, rax
2a23b86: or rbp, 1
2a23b8a: mov qword ptr [rsp + 0x40], rbp
2a23b8f: mov qword ptr [rsp + 0x48], r14
2a23b94: mov qword ptr [rsp + 0x50], rax
2a23b99: mov rdi, rbx
2a23b9c: mov rsi, r15
2a23b9f: mov rdx, r14
2a23ba2: call 0x4a07a30
2a23ba7: mov byte ptr [rbx + r14], 0
2a23bac: lea rbx, [rsp + 8]
2a23bb1: lea rsi, [rsp + 0x20]
2a23bb6: lea rdx, [rsp + 0x40]
2a23bbb: mov rdi, rbx
2a23bbe: call 0x2a7fc70
2a23bc3: mov rdi, qword ptr [rsp + 0x58]
2a23bc8: mov rsi, rbx
2a23bcb: call 0x2a72970
2a23bd0: test byte ptr [rsp + 8], 1
2a23bd5: jne 0x2a23c3e
2a23bd7: test byte ptr [rsp + 0x40], 1
2a23bdc: jne 0x2a23c58
2a23bde: test byte ptr [rsp + 0x20], 1
2a23be3: je 0x2a23bf8
2a23be5: mov rsi, qword ptr [rsp + 0x20]
2a23bea: mov rdi, qword ptr [rsp + 0x30]
2a23bef: and rsi, 0xfffffffffffffffe
2a23bf3: call 0x498cfc8
2a23bf8: mov rax, qword ptr [rsp]
2a23bfc: mov rbp, qword ptr [rax + 0x1a8]
2a23c03: mov rax, qword ptr [r13]
2a23c07: mov rdi, r13
2a23c0a: call qword ptr [rax + 0x10]
2a23c0d: mov r15, rax
2a23c10: mov rax, qword ptr [r13]
2a23c14: mov rdi, r13
2a23c17: call qword ptr [rax + 0x18]
2a23c1a: mov r14, rax
2a23c1d: cmp rax, -0xa
2a23c21: ja 0x2a2403a
2a23c27: cmp r14, 0x16
2a23c2b: ja 0x2a23c78
2a23c2d: mov eax, r14d
2a23c30: add al, r14b
2a23c33: mov byte ptr [rsp + 0x20], al
2a23c37: lea rbx, [rsp + 0x21]
2a23c3c: jmp 0x2a23caf
2a23c3e: mov rsi, qword ptr [rsp + 8]
2a23c43: mov rdi, qword ptr [rsp + 0x18]
2a23c48: and rsi, 0xfffffffffffffffe
2a23c4c: call 0x498cfc8
2a23c51: test byte ptr [rsp + 0x40], 1
2a23c56: je 0x2a23bde
2a23c58: mov rsi, qword ptr [rsp + 0x40]
2a23c5d: mov rdi, qword ptr [rsp + 0x50]
2a23c62: and rsi, 0xfffffffffffffffe
2a23c66: call 0x498cfc8
2a23c6b: test byte ptr [rsp + 0x20], 1
2a23c70: jne 0x2a23be5
2a23c76: jmp 0x2a23bf8
2a23c78: mov rax, r14
2a23c7b: and rax, 0xfffffffffffffff8
2a23c7f: add rax, 8
2a23c83: cmp rax, 0x18
2a23c87: mov r13d, 0x1a
2a23c8d: cmovne r13, rax
2a23c91: mov rdi, r13
2a23c94: call 0x4a078f0
2a23c99: mov rbx, rax
2a23c9c: or r13, 1
2a23ca0: mov qword ptr [rsp + 0x20], r13
2a23ca5: mov qword ptr [rsp + 0x28], r14
2a23caa: mov qword ptr [rsp + 0x30], rax
2a23caf: mov rdi, rbx
2a23cb2: mov rsi, r15
2a23cb5: mov rdx, r14
2a23cb8: call 0x4a07a30
2a23cbd: mov byte ptr [rbx + r14], 0
2a23cc2: mov rax, qword ptr [r12]
2a23cc6: mov rdi, r12
2a23cc9: call qword ptr [rax + 0x10]
2a23ccc: mov r15, rax
2a23ccf: mov rax, qword ptr [r12]
2a23cd3: mov rdi, r12
2a23cd6: call qword ptr [rax + 0x18]
2a23cd9: mov r14, rax
2a23cdc: cmp rax, -0xa
2a23ce0: ja 0x2a2403a
2a23ce6: cmp r14, 0x16
2a23cea: ja 0x2a23cfd
2a23cec: mov eax, r14d
2a23cef: add al, r14b
2a23cf2: mov byte ptr [rsp + 0x40], al
2a23cf6: lea rbx, [rsp + 0x41]
2a23cfb: jmp 0x2a23d34
2a23cfd: mov rax, r14
2a23d00: and rax, 0xfffffffffffffff8
2a23d04: add rax, 8
2a23d08: cmp rax, 0x18
2a23d0c: mov r12d, 0x1a
2a23d12: cmovne r12, rax
2a23d16: mov rdi, r12
2a23d19: call 0x4a078f0
2a23d1e: mov rbx, rax
2a23d21: or r12, 1
2a23d25: mov qword ptr [rsp + 0x40], r12
2a23d2a: mov qword ptr [rsp + 0x48], r14
2a23d2f: mov qword ptr [rsp + 0x50], rax
2a23d34: mov rdi, rbx
2a23d37: mov rsi, r15
2a23d3a: mov rdx, r14
2a23d3d: call 0x4a07a30
2a23d42: mov byte ptr [rbx + r14], 0
2a23d47: lea rbx, [rsp + 8]
2a23d4c: lea r13, [rsp + 0x20]
2a23d51: lea rdx, [rsp + 0x40]
2a23d56: mov rdi, rbx
2a23d59: mov rsi, r13
2a23d5c: call 0x2a7fe30
2a23d61: mov rdi, rbp
2a23d64: mov rsi, rbx
2a23d67: call 0x2a72a70
2a23d6c: test byte ptr [rsp + 8], 1
2a23d71: jne 0x2a23e1b
2a23d77: test byte ptr [rsp + 0x40], 1
2a23d7c: jne 0x2a23e39
2a23d82: test byte ptr [rsp + 0x20], 1
2a23d87: je 0x2a23d9c
2a23d89: mov rsi, qword ptr [rsp + 0x20]
2a23d8e: mov rdi, qword ptr [rsp + 0x30]
2a23d93: and rsi, 0xfffffffffffffffe
2a23d97: call 0x498cfc8
2a23d9c: mov eax, 0xffffffff
2a23da1: mov rcx, qword ptr [rsp]
2a23da5: xchg dword ptr [rcx + 0x20c], eax
2a23dab: mov rax, qword ptr [rsp + 0x98]
2a23db3: mov r12, qword ptr [rax + 8]
2a23db7: movups xmm0, xmmword ptr [rax]
2a23dba: movaps xmmword ptr [rsp + 0xb0], xmm0
2a23dc2: test r12, r12
2a23dc5: je 0x2a23dcd
2a23dc7: lock inc qword ptr [r12 + 8]
2a23dcd: xorps xmm0, xmm0
2a23dd0: movaps xmmword ptr [rsp + 0xa0], xmm0
2a23dd8: mov rdi, qword ptr [rsp + 0x80]
2a23de0: mov rax, qword ptr [rdi]
2a23de3: call qword ptr [rax + 0x10]
2a23de6: mov r15, rax
2a23de9: mov rdi, qword ptr [rsp + 0x80]
2a23df1: mov rax, qword ptr [rdi]
2a23df4: call qword ptr [rax + 0x18]
2a23df7: mov r14, rax
2a23dfa: cmp rax, -9
2a23dfe: jae 0x2a2403a
2a23e04: cmp r14, 0x16
2a23e08: ja 0x2a23e5c
2a23e0a: mov eax, r14d
2a23e0d: add al, r14b
2a23e10: mov byte ptr [rsp + 8], al
2a23e14: lea rbx, [rsp + 9]
2a23e19: jmp 0x2a23e92
2a23e1b: mov rsi, qword ptr [rsp + 8]
2a23e20: mov rdi, qword ptr [rsp + 0x18]
2a23e25: and rsi, 0xfffffffffffffffe
2a23e29: call 0x498cfc8
2a23e2e: test byte ptr [rsp + 0x40], 1
2a23e33: je 0x2a23d82
2a23e39: mov rsi, qword ptr [rsp + 0x40]
2a23e3e: mov rdi, qword ptr [rsp + 0x50]
2a23e43: and rsi, 0xfffffffffffffffe
2a23e47: call 0x498cfc8
2a23e4c: test byte ptr [rsp + 0x20], 1
2a23e51: jne 0x2a23d89
2a23e57: jmp 0x2a23d9c
2a23e5c: mov rax, r14
2a23e5f: and rax, 0xfffffffffffffff8
2a23e63: add rax, 8
2a23e67: cmp rax, 0x18
2a23e6b: mov ebp, 0x1a
2a23e70: cmovne rbp, rax
2a23e74: mov rdi, rbp
2a23e77: call 0x4a078f0
2a23e7c: mov rbx, rax
2a23e7f: or rbp, 1
2a23e83: mov qword ptr [rsp + 8], rbp
2a23e88: mov qword ptr [rsp + 0x10], r14
2a23e8d: mov qword ptr [rsp + 0x18], rax
2a23e92: mov rdi, rbx
2a23e95: mov rsi, r15
2a23e98: mov rdx, r14
2a23e9b: call 0x4a07a30
2a23ea0: mov byte ptr [rbx + r14], 0
2a23ea5: mov rdi, qword ptr [rsp]
2a23ea9: lea rdx, [rdi + 0x240]
2a23eb0: xorps xmm0, xmm0
2a23eb3: movaps xmmword ptr [rsp + 0x20], xmm0
2a23eb8: mov qword ptr [rsp + 0x30], 0
2a23ec1: lea rsi, [rsp + 0x88]
2a23ec9: lea rcx, [rsp + 0xb0]
2a23ed1: lea r8, [rsp + 0xa0]
2a23ed9: lea r9, [rsp + 8]
2a23ede: push r13
2a23ee0: push 0
2a23ee2: call 0x2a220b0
2a23ee7: add rsp, 0x10
2a23eeb: mov ebp, eax
2a23eed: test byte ptr [rsp + 0x20], 1
2a23ef2: jne 0x2a23f0a
2a23ef4: test byte ptr [rsp + 8], 1
2a23ef9: jne 0x2a23f24
2a23efb: mov rbx, qword ptr [rsp + 0xa8]
2a23f03: test rbx, rbx
2a23f06: jne 0x2a23f44
2a23f08: jmp 0x2a23f67
2a23f0a: mov rsi, qword ptr [rsp + 0x20]
2a23f0f: mov rdi, qword ptr [rsp + 0x30]
2a23f14: and rsi, 0xfffffffffffffffe
2a23f18: call 0x498cfc8
2a23f1d: test byte ptr [rsp + 8], 1
2a23f22: je 0x2a23efb
2a23f24: mov rsi, qword ptr [rsp + 8]
2a23f29: mov rdi, qword ptr [rsp + 0x18]
2a23f2e: and rsi, 0xfffffffffffffffe
2a23f32: call 0x498cfc8
2a23f37: mov rbx, qword ptr [rsp + 0xa8]
2a23f3f: test rbx, rbx
2a23f42: je 0x2a23f67
2a23f44: mov rax, 0xffffffffffffffff
2a23f4b: lock xadd qword ptr [rbx + 8], rax
2a23f51: test rax, rax
2a23f54: jne 0x2a23f67
2a23f56: mov rax, qword ptr [rbx]
2a23f59: mov rdi, rbx
2a23f5c: call qword ptr [rax + 0x10]
2a23f5f: mov rdi, rbx
2a23f62: call 0x49a8780
2a23f67: test r12, r12
2a23f6a: je 0x2a23f91
2a23f6c: mov rax, 0xffffffffffffffff
2a23f73: lock xadd qword ptr [r12 + 8], rax
2a23f7a: test rax, rax
2a23f7d: jne 0x2a23f91
2a23f7f: mov rax, qword ptr [r12]
2a23f83: mov rdi, r12
2a23f86: call qword ptr [rax + 0x10]
2a23f89: mov rdi, r12
2a23f8c: call 0x49a8780
2a23f91: cmp byte ptr [rsp + 0x90], 1
2a23f99: jne 0x2a23fa8
2a23f9b: mov rdi, qword ptr [rsp + 0x88]
2a23fa3: call 0x49b59b0
2a23fa8: test byte ptr [rsp + 0x60], 1
2a23fad: je 0x2a23fc2
2a23faf: mov rsi, qword ptr [rsp + 0x60]
2a23fb4: mov rdi, qword ptr [rsp + 0x70]
2a23fb9: and rsi, 0xfffffffffffffffe
2a23fbd: call 0x498cfc8
2a23fc2: mov eax, ebp
2a23fc4: add rsp, 0xc8
2a23fcb: pop rbx
2a23fcc: pop r12
2a23fce: pop r13
2a23fd0: pop r14
2a23fd2: pop r15
2a23fd4: pop rbp
2a23fd5: ret
2a23fd6: mov rax, qword ptr [r12]
2a23fda: mov rdi, r12
2a23fdd: call qword ptr [rax + 0x10]
2a23fe0: mov r14, rax
2a23fe3: mov rax, qword ptr [r12]
2a23fe7: mov rdi, r12
2a23fea: call qword ptr [rax + 0x18]
2a23fed: mov r15, rax
2a23ff0: cmp rax, -1
2a23ff4: je 0x2a2403f
2a23ff6: movzx ebx, byte ptr [rsp + 0x60]
2a23ffb: mov eax, ebx
2a23ffd: shr ebx, 1
2a23fff: test al, 1
2a24001: cmovne rbx, qword ptr [rsp + 0x68]
2a24007: lea rdi, [rsp + 0x61]
2a2400c: cmovne rdi, qword ptr [rsp + 0x70]
2a24012: cmp r15, rbx
2a24015: mov rdx, rbx
2a24018: cmovb rdx, r15
2a2401c: mov rsi, r14
2a2401f: call 0x4a07bb0
2a24024: test eax, eax
2a24026: jne 0x2a23923
2a2402c: cmp rbx, r15
2a2402f: je 0x2a2392d
2a24035: jmp 0x2a23923
2a2403a: call 0x18f8660
2a2403f: call 0x191b050
2a24044: mov rbx, rax
2a24047: lea rdi, [rbp + 0x58]
2a2404b: call 0x49b4af0
2a24050: mov rdi, rbp
2a24053: add rdi, 0x28
2a24057: call 0x49b4af0
2a2405c: jmp 0x2a24061
2a2405e: mov rbx, rax
2a24061: mov esi, 0x1c8
2a24066: mov rdi, rbp
2a24069: call 0x498cfc8
2a2406e: mov rdi, rbx
2a24071: call 0x18f83c0
2a24076: mov rdi, rax
2a24079: call 0x18f83c0
2a2407e: nop

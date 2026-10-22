2a80ff0: push rbp
2a80ff1: push r15
2a80ff3: push r14
2a80ff5: push r13
2a80ff7: push r12
2a80ff9: push rbx
2a80ffa: sub rsp, 0x68
2a80ffe: mov rbx, rdi
2a81001: lea rbp, [rsi + 1]
2a81005: test byte ptr [rsi], 1
2a81008: mov r14, rbp
2a8100b: je 0x2a81011
2a8100d: mov r14, qword ptr [rsi + 0x10]
2a81011: movzx edi, byte ptr [r14]
2a81015: test dil, dil
2a81018: je 0x2a810f1
2a8101e: mov eax, 1
2a81023: cmp dil, 2
2a81027: jg 0x2a81284
2a8102d: movzx eax, byte ptr [rip + 0x212696c]
2a81034: mov r9d, 2
2a8103a: cmp dil, al
2a8103d: jl 0x2a81053
2a8103f: movzx eax, byte ptr [rip + 0x212695a]
2a81046: xor edi, edi
2a81048: cmp byte ptr [r14], al
2a8104b: setg dil
2a8104f: lea r9d, [rdi + rdi*2]
2a81053: movsx eax, byte ptr [r14]
2a81057: cmp eax, 2
2a8105a: jl 0x2a81076
2a8105c: movzx edi, byte ptr [rcx]
2a8105f: test dil, 1
2a81063: je 0x2a81274
2a81069: mov rdi, qword ptr [rcx + 8]
2a8106d: test rdi, rdi
2a81070: je 0x2a8127f
2a81076: mov dword ptr [rsp + 0x1c], r9d
2a8107b: mov r13, rdx
2a8107e: mov r12, rcx
2a81081: mov r15, r8
2a81084: mov qword ptr [rsp + 0x10], rsi
2a81089: lea rdi, [rsp + 0x38]
2a8108e: mov esi, eax
2a81090: call 0x2a7f8e0
2a81095: mov r9, qword ptr [rsp + 0x38]
2a8109a: mov rdi, qword ptr [rbx]
2a8109d: mov qword ptr [rbx], r9
2a810a0: test rdi, rdi
2a810a3: je 0x2a810ae
2a810a5: mov rax, qword ptr [rdi]
2a810a8: call qword ptr [rax + 8]
2a810ab: mov r9, qword ptr [rbx]
2a810ae: movsx eax, byte ptr [r14]
2a810b2: mov dword ptr [rsp], eax
2a810b5: lea rdi, [rsp + 0x38]
2a810ba: mov rdx, r13
2a810bd: mov rcx, r12
2a810c0: mov r8, r15
2a810c3: call 0x2a812b0
2a810c8: movzx ecx, byte ptr [rsp + 0x38]
2a810cd: test cl, 1
2a810d0: je 0x2a810fb
2a810d2: mov rdx, qword ptr [rsp + 0x40]
2a810d7: mov rax, qword ptr [rsp + 0x10]
2a810dc: test rdx, rdx
2a810df: je 0x2a81109
2a810e1: movzx r14d, byte ptr [rax]
2a810e5: test r14b, 1
2a810e9: je 0x2a8111c
2a810eb: mov rcx, qword ptr [rax + 8]
2a810ef: jmp 0x2a81121
2a810f1: mov eax, 5
2a810f6: jmp 0x2a81284
2a810fb: mov edx, ecx
2a810fd: shr edx, 1
2a810ff: mov rax, qword ptr [rsp + 0x10]
2a81104: test rdx, rdx
2a81107: jne 0x2a810e1
2a81109: mov eax, 5
2a8110e: test cl, 1
2a81111: jne 0x2a81246
2a81117: jmp 0x2a81284
2a8111c: mov ecx, r14d
2a8111f: shr ecx, 1
2a81121: test rcx, rcx
2a81124: je 0x2a81293
2a8112a: lea r15, [rcx - 1]
2a8112e: cmp r15, -9
2a81132: jae 0x2a81298
2a81138: mov rdx, qword ptr [rax + 0x10]
2a8113c: cmp rcx, 0x17
2a81140: ja 0x2a81151
2a81142: lea eax, [r15 + r15]
2a81146: mov byte ptr [rsp + 0x20], al
2a8114a: lea r12, [rsp + 0x21]
2a8114f: jmp 0x2a81192
2a81151: mov qword ptr [rsp + 0x10], rdx
2a81156: mov rax, r15
2a81159: and rax, 0xfffffffffffffff8
2a8115d: add rax, 8
2a81161: cmp rax, 0x18
2a81165: mov r13d, 0x1a
2a8116b: cmovne r13, rax
2a8116f: mov rdi, r13
2a81172: call 0x4a078f0
2a81177: mov r12, rax
2a8117a: or r13, 1
2a8117e: mov qword ptr [rsp + 0x20], r13
2a81183: mov qword ptr [rsp + 0x28], r15
2a81188: mov qword ptr [rsp + 0x30], rax
2a8118d: mov rdx, qword ptr [rsp + 0x10]
2a81192: test r14b, 1
2a81196: cmovne rbp, rdx
2a8119a: inc rbp
2a8119d: mov rdi, r12
2a811a0: mov rsi, rbp
2a811a3: mov rdx, r15
2a811a6: call 0x4a07a30
2a811ab: mov byte ptr [r12 + r15], 0
2a811b0: mov rsi, qword ptr [rbx]
2a811b3: mov rax, qword ptr [rsi]
2a811b6: lea rdi, [rsp + 0x50]
2a811bb: lea rdx, [rsp + 0x20]
2a811c0: lea rcx, [rsp + 0x38]
2a811c5: call qword ptr [rax + 0x40]
2a811c8: lea r14, [rbx + 0x10]
2a811cc: test byte ptr [rbx + 0x10], 1
2a811d0: je 0x2a811e3
2a811d2: mov rsi, qword ptr [rbx + 0x10]
2a811d6: mov rdi, qword ptr [rbx + 0x20]
2a811da: and rsi, 0xfffffffffffffffe
2a811de: call 0x498cfc8
2a811e3: mov rax, qword ptr [rsp + 0x60]
2a811e8: mov qword ptr [r14 + 0x10], rax
2a811ec: movups xmm0, xmmword ptr [rsp + 0x50]
2a811f1: movups xmmword ptr [r14], xmm0
2a811f5: movzx eax, byte ptr [rbx + 0x10]
2a811f9: test al, 1
2a811fb: je 0x2a8125f
2a811fd: mov rax, qword ptr [rbx + 0x18]
2a81201: test rax, rax
2a81204: je 0x2a81266
2a81206: mov rdi, qword ptr [rbx]
2a81209: mov rax, qword ptr [rdi]
2a8120c: mov rsi, r14
2a8120f: call qword ptr [rax + 0x70]
2a81212: test al, al
2a81214: mov eax, 5
2a81219: cmovne eax, dword ptr [rsp + 0x1c]
2a8121e: test byte ptr [rsp + 0x20], 1
2a81223: je 0x2a8123c
2a81225: mov rsi, qword ptr [rsp + 0x20]
2a8122a: mov rdi, qword ptr [rsp + 0x30]
2a8122f: and rsi, 0xfffffffffffffffe
2a81233: mov ebx, eax
2a81235: call 0x498cfc8
2a8123a: mov eax, ebx
2a8123c: movzx ecx, byte ptr [rsp + 0x38]
2a81241: test cl, 1
2a81244: je 0x2a81284
2a81246: mov rsi, qword ptr [rsp + 0x38]
2a8124b: mov rdi, qword ptr [rsp + 0x48]
2a81250: and rsi, 0xfffffffffffffffe
2a81254: mov ebx, eax
2a81256: call 0x498cfc8
2a8125b: mov eax, ebx
2a8125d: jmp 0x2a81284
2a8125f: shr eax, 1
2a81261: test rax, rax
2a81264: jne 0x2a81206
2a81266: mov eax, 6
2a8126b: test byte ptr [rsp + 0x20], 1
2a81270: jne 0x2a81225
2a81272: jmp 0x2a8123c
2a81274: shr edi, 1
2a81276: test rdi, rdi
2a81279: jne 0x2a81076
2a8127f: mov eax, 4
2a81284: add rsp, 0x68
2a81288: pop rbx
2a81289: pop r12
2a8128b: pop r13
2a8128d: pop r14
2a8128f: pop r15
2a81291: pop rbp
2a81292: ret
2a81293: call 0x191b050
2a81298: call 0x18f8660
2a8129d: mov rdi, rax
2a812a0: call 0x18f83c0
2a812a5: nop word ptr cs:[rax + rax]

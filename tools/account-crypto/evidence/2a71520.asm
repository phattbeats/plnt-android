2a71520: push r15
2a71522: push r14
2a71524: push rbx
2a71525: sub rsp, 0x10
2a71529: mov r14, rsi
2a7152c: mov rbx, rdi
2a7152f: mov qword ptr [rsp], rdi
2a71533: mov byte ptr [rsp + 8], 1
2a71538: call 0x49b61a0
2a7153d: mov rdi, rbx
2a71540: mov ebx, 1
2a71545: test byte ptr [rdi + 0xa0], 8
2a7154c: je 0x2a715ba
2a7154e: mov rax, qword ptr [rdi + 0xd0]
2a71555: and rax, 0xfffffffffffffffc
2a71559: xor ebx, ebx
2a7155b: cmp r14, rax
2a7155e: je 0x2a715ba
2a71560: movzx edx, byte ptr [rax]
2a71563: test byte ptr [r14], 1
2a71567: je 0x2a7158e
2a71569: mov ecx, edx
2a7156b: lea rsi, [rax + 1]
2a7156f: shr edx, 1
2a71571: test cl, 1
2a71574: cmovne rsi, qword ptr [rax + 0x10]
2a71579: cmovne rdx, qword ptr [rax + 8]
2a7157e: mov r15, rdi
2a71581: mov rdi, r14
2a71584: call 0x19125a0
2a71589: mov rdi, r15
2a7158c: jmp 0x2a715ba
2a7158e: test dl, 1
2a71591: je 0x2a715ab
2a71593: mov rdx, qword ptr [rax + 8]
2a71597: mov rsi, qword ptr [rax + 0x10]
2a7159b: mov r15, rdi
2a7159e: mov rdi, r14
2a715a1: call 0x19126b0
2a715a6: mov rdi, r15
2a715a9: jmp 0x2a715ba
2a715ab: mov rcx, qword ptr [rax + 0x10]
2a715af: mov qword ptr [r14 + 0x10], rcx
2a715b3: movups xmm0, xmmword ptr [rax]
2a715b6: movups xmmword ptr [r14], xmm0
2a715ba: call 0x49b6260
2a715bf: mov eax, ebx
2a715c1: add rsp, 0x10
2a715c5: pop rbx
2a715c6: pop r14
2a715c8: pop r15
2a715ca: ret
2a715cb: mov rbx, rax
2a715ce: mov rdi, rsp
2a715d1: call 0x1a04c00
2a715d6: mov rdi, rbx
2a715d9: call 0x4a07a10
2a715de: mov rdi, rax
2a715e1: call 0x18f83c0
2a715e6: nop word ptr cs:[rax + rax]

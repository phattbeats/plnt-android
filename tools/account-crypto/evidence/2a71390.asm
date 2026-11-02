2a71390: push rbp
2a71391: push r14
2a71393: push rbx
2a71394: sub rsp, 0x10
2a71398: mov rbx, rsi
2a7139b: mov r14, rdi
2a7139e: mov qword ptr [rsp], rdi
2a713a2: mov byte ptr [rsp + 8], 1
2a713a7: call 0x49b61a0
2a713ac: mov ebp, 1
2a713b1: test byte ptr [r14 + 0xa0], 0x20
2a713b9: je 0x2a71436
2a713bb: mov rax, qword ptr [r14 + 0xe0]
2a713c2: and rax, 0xfffffffffffffffc
2a713c6: cmp rbx, rax
2a713c9: je 0x2a713f0
2a713cb: movzx edx, byte ptr [rax]
2a713ce: test byte ptr [rbx], 1
2a713d1: je 0x2a713fd
2a713d3: mov ecx, edx
2a713d5: lea rsi, [rax + 1]
2a713d9: shr edx, 1
2a713db: test cl, 1
2a713de: cmovne rsi, qword ptr [rax + 0x10]
2a713e3: cmovne rdx, qword ptr [rax + 8]
2a713e8: mov rdi, rbx
2a713eb: call 0x19125a0
2a713f0: movzx eax, byte ptr [rbx]
2a713f3: test al, 1
2a713f5: je 0x2a71429
2a713f7: mov rax, qword ptr [rbx + 8]
2a713fb: jmp 0x2a7142b
2a713fd: test dl, 1
2a71400: je 0x2a71414
2a71402: mov rdx, qword ptr [rax + 8]
2a71406: mov rsi, qword ptr [rax + 0x10]
2a7140a: mov rdi, rbx
2a7140d: call 0x19126b0
2a71412: jmp 0x2a713f0
2a71414: mov rcx, qword ptr [rax + 0x10]
2a71418: mov qword ptr [rbx + 0x10], rcx
2a7141c: movups xmm0, xmmword ptr [rax]
2a7141f: movups xmmword ptr [rbx], xmm0
2a71422: movzx eax, byte ptr [rbx]
2a71425: test al, 1
2a71427: jne 0x2a713f7
2a71429: shr eax, 1
2a7142b: xor ecx, ecx
2a7142d: test rax, rax
2a71430: sete cl
2a71433: lea ebp, [rcx + rcx*4]
2a71436: mov rdi, r14
2a71439: call 0x49b6260
2a7143e: mov eax, ebp
2a71440: add rsp, 0x10
2a71444: pop rbx
2a71445: pop r14
2a71447: pop rbp
2a71448: ret
2a71449: mov rbx, rax
2a7144c: mov rdi, rsp
2a7144f: call 0x1a04c00
2a71454: mov rdi, rbx
2a71457: call 0x4a07a10
2a7145c: mov rdi, rax
2a7145f: call 0x18f83c0
2a71464: nop word ptr cs:[rax + rax]

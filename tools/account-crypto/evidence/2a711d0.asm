2a711d0: push rbp
2a711d1: push r14
2a711d3: push rbx
2a711d4: sub rsp, 0x10
2a711d8: mov rbx, rsi
2a711db: mov r14, rdi
2a711de: mov qword ptr [rsp], rdi
2a711e2: mov byte ptr [rsp + 8], 1
2a711e7: call 0x49b61a0
2a711ec: mov ebp, 1
2a711f1: test byte ptr [r14 + 0xa0], 4
2a711f9: je 0x2a71276
2a711fb: mov rax, qword ptr [r14 + 0xc8]
2a71202: and rax, 0xfffffffffffffffc
2a71206: cmp rbx, rax
2a71209: je 0x2a71230
2a7120b: movzx edx, byte ptr [rax]
2a7120e: test byte ptr [rbx], 1
2a71211: je 0x2a7123d
2a71213: mov ecx, edx
2a71215: lea rsi, [rax + 1]
2a71219: shr edx, 1
2a7121b: test cl, 1
2a7121e: cmovne rsi, qword ptr [rax + 0x10]
2a71223: cmovne rdx, qword ptr [rax + 8]
2a71228: mov rdi, rbx
2a7122b: call 0x19125a0
2a71230: movzx eax, byte ptr [rbx]
2a71233: test al, 1
2a71235: je 0x2a71269
2a71237: mov rax, qword ptr [rbx + 8]
2a7123b: jmp 0x2a7126b
2a7123d: test dl, 1
2a71240: je 0x2a71254
2a71242: mov rdx, qword ptr [rax + 8]
2a71246: mov rsi, qword ptr [rax + 0x10]
2a7124a: mov rdi, rbx
2a7124d: call 0x19126b0
2a71252: jmp 0x2a71230
2a71254: mov rcx, qword ptr [rax + 0x10]
2a71258: mov qword ptr [rbx + 0x10], rcx
2a7125c: movups xmm0, xmmword ptr [rax]
2a7125f: movups xmmword ptr [rbx], xmm0
2a71262: movzx eax, byte ptr [rbx]
2a71265: test al, 1
2a71267: jne 0x2a71237
2a71269: shr eax, 1
2a7126b: xor ecx, ecx
2a7126d: test rax, rax
2a71270: sete cl
2a71273: lea ebp, [rcx + rcx*4]
2a71276: mov rdi, r14
2a71279: call 0x49b6260
2a7127e: mov eax, ebp
2a71280: add rsp, 0x10
2a71284: pop rbx
2a71285: pop r14
2a71287: pop rbp
2a71288: ret
2a71289: mov rbx, rax
2a7128c: mov rdi, rsp
2a7128f: call 0x1a04c00
2a71294: mov rdi, rbx
2a71297: call 0x4a07a10
2a7129c: mov rdi, rax
2a7129f: call 0x18f83c0
2a712a4: nop word ptr cs:[rax + rax]

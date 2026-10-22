2a6fea0: push rax
2a6fea1: mov rcx, qword ptr [rdi]
2a6fea4: mov edx, dword ptr [rcx + 0x58]
2a6fea7: mov eax, 5
2a6feac: add edx, -0x10
2a6feaf: cmp edx, 9
2a6feb2: ja 0x2a6feda
2a6feb4: lea rdi, [rip - 0x21526db]
2a6febb: movsxd rdx, dword ptr [rdi + rdx*4]
2a6febf: add rdx, rdi
2a6fec2: jmp rdx
2a6fec4: mov rdi, qword ptr [rcx + 0x50]
2a6fec8: call 0x4302fc0
2a6fecd: mov ecx, eax
2a6fecf: mov eax, 2
2a6fed4: test cl, cl
2a6fed6: je 0x2a6feda
2a6fed8: xor eax, eax
2a6feda: pop rcx
2a6fedb: ret
2a6fedc: nop dword ptr [rax]

4758c20: push r15
4758c22: push r14
4758c24: push rbx
4758c25: mov rbx, rsi
4758c28: mov r14, rdi
4758c2b: mov edi, 0x120
4758c30: call 0x4a078f0
4758c35: mov r15, rax
4758c38: mov edx, 0x120
4758c3d: mov rdi, rax
4758c40: xor esi, esi
4758c42: call 0x4a07ba0
4758c47: mov rdi, qword ptr [r14 + 8]
4758c4b: mov qword ptr [r14 + 8], r15
4758c4f: test rdi, rdi
4758c52: je 0x4758c62
4758c54: mov esi, 0x120
4758c59: call 0x498cfc8
4758c5e: mov r15, qword ptr [r14 + 8]
4758c62: mov rdi, r15
4758c65: call 0x3c01360
4758c6a: mov rdi, qword ptr [r14 + 8]
4758c6e: mov rsi, rbx
4758c71: mov edx, 0x100
4758c76: call 0x3c013c0
4758c7b: test eax, eax
4758c7d: sete al
4758c80: pop rbx
4758c81: pop r14
4758c83: pop r15
4758c85: ret
4758c86: int3
4758c87: int3
4758c88: int3
4758c89: int3
4758c8a: int3
4758c8b: int3
4758c8c: int3
4758c8d: int3
4758c8e: int3
4758c8f: int3

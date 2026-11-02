4758f40: push rbp
4758f41: push r15
4758f43: push r14
4758f45: push r13
4758f47: push r12
4758f49: push rbx
4758f4a: sub rsp, 0x18
4758f4e: mov r14, rdi
4758f51: mov r15, qword ptr [rsi + 8]
4758f55: test r15, r15
4758f58: je 0x4758fe3
4758f5e: mov rbp, r8
4758f61: mov r13, rcx
4758f64: mov dword ptr [rsp + 0xc], r9d
4758f69: mov qword ptr [rsp + 0x10], rdx
4758f6e: mov rdi, r8
4758f71: call 0x4a07956
4758f76: mov rbx, rax
4758f79: mov rdi, rax
4758f7c: xor esi, esi
4758f7e: mov rdx, rbp
4758f81: call 0x4a07ba0
4758f86: mov edi, 0x10
4758f8b: call 0x4a07956
4758f90: mov r12, rax
4758f93: xorps xmm0, xmm0
4758f96: movups xmmword ptr [rax], xmm0
4758f99: sub rsp, 8
4758f9d: mov r8d, 0xc
4758fa3: mov rdi, r15
4758fa6: mov esi, dword ptr [rsp + 0x14]
4758faa: mov rdx, rbp
4758fad: mov rcx, r13
4758fb0: xor r9d, r9d
4758fb3: push rax
4758fb4: push 0x10
4758fb6: push rbx
4758fb7: push qword ptr [rsp + 0x30]
4758fbb: push 0
4758fbd: call 0x3c150b0
4758fc2: add rsp, 0x30
4758fc6: test eax, eax
4758fc8: je 0x4758fec
4758fca: xorps xmm0, xmm0
4758fcd: movups xmmword ptr [r14], xmm0
4758fd1: mov rdi, r12
4758fd4: call 0x498cfd0
4758fd9: mov rdi, rbx
4758fdc: call 0x498cfd0
4758fe1: jmp 0x4758ff3
4758fe3: xorps xmm0, xmm0
4758fe6: movups xmmword ptr [r14], xmm0
4758fea: jmp 0x4758ff3
4758fec: mov qword ptr [r14], rbx
4758fef: mov qword ptr [r14 + 8], r12
4758ff3: mov rax, r14
4758ff6: add rsp, 0x18
4758ffa: pop rbx
4758ffb: pop r12
4758ffd: pop r13
4758fff: pop r14
4759001: pop r15
4759003: pop rbp
4759004: ret
4759005: mov r14, rax
4759008: mov rdi, r12
475900b: call 0x498cfd0
4759010: mov rdi, rbx
4759013: call 0x498cfd0
4759018: mov rdi, r14
475901b: call 0x4a07a10
4759020: mov r14, rax
4759023: mov rdi, rbx
4759026: call 0x498cfd0
475902b: mov rdi, r14
475902e: call 0x4a07a10
4759033: int3
4759034: int3
4759035: int3
4759036: int3
4759037: int3
4759038: int3
4759039: int3
475903a: int3
475903b: int3
475903c: int3
475903d: int3
475903e: int3
475903f: int3

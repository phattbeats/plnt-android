2a72230: push r15
2a72232: push r14
2a72234: push rbx
2a72235: sub rsp, 0x10
2a72239: mov r14, rsi
2a7223c: mov rbx, rdi
2a7223f: mov qword ptr [rsp], rdi
2a72243: mov byte ptr [rsp + 8], 1
2a72248: call 0x49b61a0
2a7224d: mov rdi, rbx
2a72250: mov ebx, 1
2a72255: test byte ptr [rdi + 0xa2], 2
2a7225c: je 0x2a722ca
2a7225e: mov rax, qword ptr [rdi + 0x140]
2a72265: and rax, 0xfffffffffffffffc
2a72269: xor ebx, ebx
2a7226b: cmp r14, rax
2a7226e: je 0x2a722ca
2a72270: movzx edx, byte ptr [rax]
2a72273: test byte ptr [r14], 1
2a72277: je 0x2a7229e
2a72279: mov ecx, edx
2a7227b: lea rsi, [rax + 1]
2a7227f: shr edx, 1
2a72281: test cl, 1
2a72284: cmovne rsi, qword ptr [rax + 0x10]
2a72289: cmovne rdx, qword ptr [rax + 8]
2a7228e: mov r15, rdi
2a72291: mov rdi, r14
2a72294: call 0x19125a0
2a72299: mov rdi, r15
2a7229c: jmp 0x2a722ca
2a7229e: test dl, 1
2a722a1: je 0x2a722bb
2a722a3: mov rdx, qword ptr [rax + 8]
2a722a7: mov rsi, qword ptr [rax + 0x10]
2a722ab: mov r15, rdi
2a722ae: mov rdi, r14
2a722b1: call 0x19126b0
2a722b6: mov rdi, r15
2a722b9: jmp 0x2a722ca
2a722bb: mov rcx, qword ptr [rax + 0x10]
2a722bf: mov qword ptr [r14 + 0x10], rcx
2a722c3: movups xmm0, xmmword ptr [rax]
2a722c6: movups xmmword ptr [r14], xmm0
2a722ca: call 0x49b6260
2a722cf: mov eax, ebx
2a722d1: add rsp, 0x10
2a722d5: pop rbx
2a722d6: pop r14
2a722d8: pop r15
2a722da: ret
2a722db: mov rbx, rax
2a722de: mov rdi, rsp
2a722e1: call 0x1a04c00
2a722e6: mov rdi, rbx
2a722e9: call 0x4a07a10
2a722ee: mov rdi, rax
2a722f1: call 0x18f83c0
2a722f6: nop word ptr cs:[rax + rax]

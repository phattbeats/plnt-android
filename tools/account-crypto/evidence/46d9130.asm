46d9130: push r15
46d9132: push r14
46d9134: push r12
46d9136: push rbx
46d9137: sub rsp, 0xa8
46d913e: mov rbx, rdi
46d9141: movzx eax, byte ptr [rdi + 0xc0]
46d9148: cmp al, 1
46d914a: jne 0x46d91a6
46d914c: mov r15, qword ptr [rbx + 0x50]
46d9150: mov edi, 0x28
46d9155: call 0x4a078f0
46d915a: mov r14, rax
46d915d: movups xmm0, xmmword ptr [rip - 0x40f3e98]
46d9164: movups xmmword ptr [rax], xmm0
46d9167: movups xmm0, xmmword ptr [rip - 0x40f3e92]
46d916e: movups xmmword ptr [rax + 0x10], xmm0
46d9172: mov dword ptr [rax + 0x1f], 0x676e696c
46d9179: mov byte ptr [rax + 0x23], 0
46d917d: mov dword ptr [r15 + 8], 4
46d9185: lea rdi, [r15 + 0x10]
46d9189: test byte ptr [r15 + 0x10], 1
46d918e: je 0x46d9346
46d9194: mov edx, 0x23
46d9199: mov rsi, r14
46d919c: call 0x19125a0
46d91a1: jmp 0x46d9353
46d91a6: xorps xmm0, xmm0
46d91a9: movaps xmmword ptr [rsp + 0x20], xmm0
46d91ae: mov qword ptr [rsp + 0x30], 0
46d91b7: lea rax, [rip + 0x4ae842]
46d91be: mov qword ptr [rsp + 0x40], rax
46d91c3: lea rax, [rip + 0x8f6]
46d91ca: mov qword ptr [rsp + 0x48], rax
46d91cf: mov qword ptr [rsp + 0x50], 0
46d91d8: mov qword ptr [rsp + 0x58], rbx
46d91dd: lea r15, [rsp + 0x40]
46d91e2: mov qword ptr [rsp + 0x60], r15
46d91e7: movaps xmmword ptr [rsp], xmm0
46d91eb: mov qword ptr [rsp + 0x10], 0
46d91f4: mov edi, 0x408
46d91f9: call 0x4a078f0
46d91fe: mov byte ptr [rax], 0
46d9201: mov qword ptr [rsp], 0x409
46d9209: mov qword ptr [rsp + 8], 0
46d9212: mov qword ptr [rsp + 0x10], rax
46d9217: mov rax, qword ptr [rbx + 0x38]
46d921b: mov rax, qword ptr [rax + 8]
46d921f: movzx eax, word ptr [rax]
46d9222: cmp eax, 0x80
46d9227: jae 0x46d95be
46d922d: movsx edx, ax
46d9230: mov rdi, rsp
46d9233: mov esi, 1
46d9238: call 0x49ac620
46d923d: mov rax, qword ptr [rbx + 0x38]
46d9241: mov rax, qword ptr [rax + 8]
46d9245: movzx edx, word ptr [rax]
46d9248: mov rsi, rdx
46d924b: not rsi
46d924e: add rsi, rax
46d9251: mov rdi, rsp
46d9254: call 0x49ac7b0
46d9259: mov rdi, qword ptr [rbx + 0x40]
46d925d: call 0x4301bb0
46d9262: test al, al
46d9264: je 0x46d92f5
46d926a: mov rdi, qword ptr [rbx + 0x40]
46d926e: mov rsi, rsp
46d9271: call 0x4302e10
46d9276: test al, al
46d9278: je 0x46d92f5
46d927a: lea r14, [rbx + 0x88]
46d9281: mov rdi, r14
46d9284: call 0x49b5980
46d9289: cmp byte ptr [rbx + 0x59], 1
46d928d: jne 0x46d950d
46d9293: mov rdi, r14
46d9296: call 0x49b59b0
46d929b: mov r12, qword ptr [rbx + 0x50]
46d929f: mov edi, 0x1a
46d92a4: call 0x4a078f0
46d92a9: mov r14, rax
46d92ac: movups xmm0, xmmword ptr [rip - 0x405d2f7]
46d92b3: movups xmmword ptr [rax], xmm0
46d92b6: movabs rax, 0x6c656e6e61686320
46d92c0: mov qword ptr [r14 + 0xf], rax
46d92c4: mov byte ptr [r14 + 0x17], 0
46d92c9: mov dword ptr [r12 + 8], 2
46d92d2: lea rdi, [r12 + 0x10]
46d92d7: test byte ptr [r12 + 0x10], 1
46d92dd: je 0x46d9573
46d92e3: mov edx, 0x17
46d92e8: mov rsi, r14
46d92eb: call 0x19125a0
46d92f0: jmp 0x46d9580
46d92f5: mov r12, qword ptr [rbx + 0x50]
46d92f9: mov edi, 0x28
46d92fe: call 0x4a078f0
46d9303: mov r14, rax
46d9306: movups xmm0, xmmword ptr [rip - 0x40bad6c]
46d930d: movups xmmword ptr [rax + 0x10], xmm0
46d9311: movups xmm0, xmmword ptr [rip - 0x40bad87]
46d9318: movups xmmword ptr [rax], xmm0
46d931b: mov word ptr [rax + 0x20], 0x72
46d9321: mov dword ptr [r12 + 8], 3
46d932a: lea rdi, [r12 + 0x10]
46d932f: test byte ptr [r12 + 0x10], 1
46d9335: je 0x46d93b0
46d9337: mov edx, 0x21
46d933c: mov rsi, r14
46d933f: call 0x19125a0
46d9344: jmp 0x46d93bd
46d9346: mov edx, 0x23
46d934b: mov rsi, r14
46d934e: call 0x19126b0
46d9353: mov esi, 0x28
46d9358: mov rdi, r14
46d935b: call 0x498cfc8
46d9360: mov rdi, qword ptr [rbx]
46d9363: lea rax, [rip + 0x4ae456]
46d936a: mov qword ptr [rsp + 0x40], rax
46d936f: mov qword ptr [rsp + 0x48], rbx
46d9374: lea r14, [rsp + 0x40]
46d9379: mov qword ptr [rsp + 0x60], r14
46d937e: mov rax, qword ptr [rdi]
46d9381: mov rsi, r14
46d9384: call qword ptr [rax + 0x70]
46d9387: mov rdi, qword ptr [rsp + 0x60]
46d938c: cmp rdi, r14
46d938f: je 0x46d93a0
46d9391: mov eax, 0x28
46d9396: test rdi, rdi
46d9399: jne 0x46d93a5
46d939b: jmp 0x46d94e4
46d93a0: mov eax, 0x20
46d93a5: mov rcx, qword ptr [rdi]
46d93a8: call qword ptr [rcx + rax]
46d93ab: jmp 0x46d94e4
46d93b0: mov edx, 0x21
46d93b5: mov rsi, r14
46d93b8: call 0x19126b0
46d93bd: mov esi, 0x28
46d93c2: mov rdi, r14
46d93c5: call 0x498cfc8
46d93ca: mov rdi, qword ptr [rbx]
46d93cd: lea rax, [rip + 0x4ae46c]
46d93d4: mov qword ptr [rsp + 0x70], rax
46d93d9: mov qword ptr [rsp + 0x78], rbx
46d93de: lea r14, [rsp + 0x70]
46d93e3: mov qword ptr [rsp + 0x90], r14
46d93eb: mov rax, qword ptr [rdi]
46d93ee: mov rsi, r14
46d93f1: call qword ptr [rax + 0x70]
46d93f4: mov rdi, qword ptr [rsp + 0x90]
46d93fc: cmp rdi, r14
46d93ff: je 0x46d94f3
46d9405: mov eax, 0x28
46d940a: test rdi, rdi
46d940d: jne 0x46d94f8
46d9413: test byte ptr [rsp], 1
46d9417: je 0x46d942b
46d9419: mov rsi, qword ptr [rsp]
46d941d: mov rdi, qword ptr [rsp + 0x10]
46d9422: and rsi, 0xfffffffffffffffe
46d9426: call 0x498cfc8
46d942b: mov rdi, qword ptr [rsp + 0x60]
46d9430: cmp rdi, r15
46d9433: je 0x46d944e
46d9435: mov eax, 0x28
46d943a: test rdi, rdi
46d943d: jne 0x46d9453
46d943f: mov rbx, qword ptr [rsp + 0x20]
46d9444: test rbx, rbx
46d9447: jne 0x46d9467
46d9449: jmp 0x46d94e4
46d944e: mov eax, 0x20
46d9453: mov rcx, qword ptr [rdi]
46d9456: call qword ptr [rcx + rax]
46d9459: mov rbx, qword ptr [rsp + 0x20]
46d945e: test rbx, rbx
46d9461: je 0x46d94e4
46d9467: mov r14, qword ptr [rsp + 0x28]
46d946c: mov rdi, rbx
46d946f: cmp rbx, r14
46d9472: jne 0x46d9488
46d9474: jmp 0x46d94d2
46d9476: nop word ptr cs:[rax + rax]
46d9480: mov r14, r15
46d9483: cmp r15, rbx
46d9486: je 0x46d94cd
46d9488: test byte ptr [r14 - 0x18], 1
46d948d: je 0x46d94b0
46d948f: mov rsi, qword ptr [r14 - 0x18]
46d9493: mov rdi, qword ptr [r14 - 8]
46d9497: and rsi, 0xfffffffffffffffe
46d949b: call 0x498cfc8
46d94a0: lea r15, [r14 - 0x30]
46d94a4: test byte ptr [r15], 1
46d94a8: je 0x46d9480
46d94aa: jmp 0x46d94ba
46d94ac: nop dword ptr [rax]
46d94b0: lea r15, [r14 - 0x30]
46d94b4: test byte ptr [r15], 1
46d94b8: je 0x46d9480
46d94ba: mov rsi, qword ptr [r14 - 0x30]
46d94be: mov rdi, qword ptr [r14 - 0x20]
46d94c2: and rsi, 0xfffffffffffffffe
46d94c6: call 0x498cfc8
46d94cb: jmp 0x46d9480
46d94cd: mov rdi, qword ptr [rsp + 0x20]
46d94d2: mov qword ptr [rsp + 0x28], rbx
46d94d7: mov rsi, qword ptr [rsp + 0x30]
46d94dc: sub rsi, rdi
46d94df: call 0x498cfc8
46d94e4: add rsp, 0xa8
46d94eb: pop rbx
46d94ec: pop r12
46d94ee: pop r14
46d94f0: pop r15
46d94f2: ret
46d94f3: mov eax, 0x20
46d94f8: mov rcx, qword ptr [rdi]
46d94fb: call qword ptr [rcx + rax]
46d94fe: test byte ptr [rsp], 1
46d9502: je 0x46d942b
46d9508: jmp 0x46d9419
46d950d: mov rax, qword ptr [rbx + 0x78]
46d9511: mov rcx, qword ptr [rbx + 0x60]
46d9515: mov rdx, qword ptr [rbx + 0x68]
46d9519: sub rdx, rcx
46d951c: sar rdx, 3
46d9520: movabs rsi, 0xaaaaaaaaaaaaaaab
46d952a: imul rsi, rdx
46d952e: cmp rax, rsi
46d9531: jae 0x46d95bc
46d9537: mov rdi, qword ptr [rbx]
46d953a: lea rax, [rax + rax*2]
46d953e: lea rsi, [rcx + rax*8]
46d9542: mov rax, qword ptr [rdi]
46d9545: lea r8, [rip - 0x3816924]
46d954c: lea rdx, [rsp + 0x20]
46d9551: mov rcx, rsp
46d9554: lea r9, [rsp + 0x40]
46d9559: call qword ptr [rax + 0x18]
46d955c: mov rdi, r14
46d955f: call 0x49b59b0
46d9564: test byte ptr [rsp], 1
46d9568: jne 0x46d9419
46d956e: jmp 0x46d942b
46d9573: mov edx, 0x17
46d9578: mov rsi, r14
46d957b: call 0x19126b0
46d9580: mov esi, 0x1a
46d9585: mov rdi, r14
46d9588: call 0x498cfc8
46d958d: mov rdi, qword ptr [rbx]
46d9590: lea rax, [rip + 0x4ae329]
46d9597: mov qword ptr [rsp + 0x70], rax
46d959c: mov qword ptr [rsp + 0x78], rbx
46d95a1: lea r14, [rsp + 0x70]
46d95a6: mov qword ptr [rsp + 0x90], r14
46d95ae: mov rax, qword ptr [rdi]
46d95b1: mov rsi, r14
46d95b4: call qword ptr [rax + 0x70]
46d95b7: jmp 0x46d93f4
46d95bc: ud2
46d95be: mov edi, 8
46d95c3: call 0x498d0a0
46d95c8: lea rcx, [rip + 0x4ae409]
46d95cf: mov qword ptr [rax], rcx
46d95d2: lea rsi, [rip + 0x4ae3d7]
46d95d9: lea rdx, [rip + 0x2b1eb0]
46d95e0: mov rdi, rax
46d95e3: call 0x498d290
46d95e8: mov rbx, rax
46d95eb: mov rdi, r14
46d95ee: call 0x49b59b0
46d95f3: test byte ptr [rsp], 1
46d95f7: je 0x46d971c
46d95fd: jmp 0x46d9655
46d95ff: jmp 0x46d962c
46d9601: mov rbx, rax
46d9604: mov esi, 0x1a
46d9609: jmp 0x46d9699
46d960e: mov rbx, rax
46d9611: test byte ptr [rsp], 1
46d9615: je 0x46d971c
46d961b: jmp 0x46d9655
46d961d: mov rbx, rax
46d9620: test byte ptr [rsp], 1
46d9624: je 0x46d971c
46d962a: jmp 0x46d9655
46d962c: mov rbx, rax
46d962f: mov rdi, qword ptr [rsp + 0x90]
46d9637: mov eax, 0x20
46d963c: cmp rdi, r14
46d963f: je 0x46d967f
46d9641: mov eax, 0x28
46d9646: test rdi, rdi
46d9649: jne 0x46d967f
46d964b: test byte ptr [rsp], 1
46d964f: je 0x46d971c
46d9655: mov rsi, qword ptr [rsp]
46d9659: mov rdi, qword ptr [rsp + 0x10]
46d965e: and rsi, 0xfffffffffffffffe
46d9662: call 0x498cfc8
46d9667: mov rdi, qword ptr [rsp + 0x60]
46d966c: mov eax, 0x20
46d9671: cmp rdi, r15
46d9674: je 0x46d9735
46d967a: jmp 0x46d972b
46d967f: mov rcx, qword ptr [rdi]
46d9682: call qword ptr [rcx + rax]
46d9685: test byte ptr [rsp], 1
46d9689: je 0x46d971c
46d968f: jmp 0x46d9655
46d9691: mov rbx, rax
46d9694: mov esi, 0x28
46d9699: mov rdi, r14
46d969c: call 0x498cfc8
46d96a1: test byte ptr [rsp], 1
46d96a5: je 0x46d971c
46d96a7: jmp 0x46d9655
46d96a9: mov rbx, rax
46d96ac: test byte ptr [rsp], 1
46d96b0: je 0x46d971c
46d96b2: jmp 0x46d9655
46d96b4: mov rbx, rax
46d96b7: test byte ptr [rsp], 1
46d96bb: je 0x46d971c
46d96bd: jmp 0x46d9655
46d96bf: mov rbx, rax
46d96c2: mov rdi, qword ptr [rsp + 0x60]
46d96c7: mov eax, 0x20
46d96cc: cmp rdi, r14
46d96cf: je 0x46d96db
46d96d1: mov eax, 0x28
46d96d6: test rdi, rdi
46d96d9: je 0x46d9745
46d96db: mov rcx, qword ptr [rdi]
46d96de: call qword ptr [rcx + rax]
46d96e1: mov rdi, rbx
46d96e4: call 0x4a07a10
46d96e9: mov rbx, rax
46d96ec: mov esi, 0x28
46d96f1: mov rdi, r14
46d96f4: call 0x498cfc8
46d96f9: mov rdi, rbx
46d96fc: call 0x4a07a10
46d9701: mov rbx, rax
46d9704: test byte ptr [rsp], 1
46d9708: je 0x46d971c
46d970a: jmp 0x46d9655
46d970f: mov rbx, rax
46d9712: test byte ptr [rsp], 1
46d9716: jne 0x46d9655
46d971c: mov rdi, qword ptr [rsp + 0x60]
46d9721: mov eax, 0x20
46d9726: cmp rdi, r15
46d9729: je 0x46d9735
46d972b: mov eax, 0x28
46d9730: test rdi, rdi
46d9733: je 0x46d973b
46d9735: mov rcx, qword ptr [rdi]
46d9738: call qword ptr [rcx + rax]
46d973b: lea rdi, [rsp + 0x20]
46d9740: call 0x1921140
46d9745: mov rdi, rbx
46d9748: call 0x4a07a10
46d974d: nop dword ptr [rax]

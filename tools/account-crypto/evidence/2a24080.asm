2a24080: push rbp
2a24081: push r15
2a24083: push r14
2a24085: push r13
2a24087: push r12
2a24089: push rbx
2a2408a: sub rsp, 0xd8
2a24091: mov qword ptr [rsp + 0x88], r8
2a24099: mov rbx, rcx
2a2409c: mov r12, rdx
2a2409f: mov r15, rsi
2a240a2: mov qword ptr [rsp + 8], rdi
2a240a7: mov rax, qword ptr [rcx]
2a240aa: mov rdi, rcx
2a240ad: call qword ptr [rax + 0x10]
2a240b0: mov r13, rax
2a240b3: mov rax, qword ptr [rbx]
2a240b6: mov rdi, rbx
2a240b9: call qword ptr [rax + 0x18]
2a240bc: mov rbp, rax
2a240bf: cmp rax, -0xa
2a240c3: ja 0x2a24892
2a240c9: cmp rbp, 0x16
2a240cd: ja 0x2a240df
2a240cf: mov eax, ebp
2a240d1: add al, bpl
2a240d4: mov byte ptr [rsp + 0x40], al
2a240d8: lea r14, [rsp + 0x41]
2a240dd: jmp 0x2a24115
2a240df: mov rax, rbp
2a240e2: and rax, 0xfffffffffffffff8
2a240e6: add rax, 8
2a240ea: cmp rax, 0x18
2a240ee: mov ebx, 0x1a
2a240f3: cmovne rbx, rax
2a240f7: mov rdi, rbx
2a240fa: call 0x4a078f0
2a240ff: mov r14, rax
2a24102: or rbx, 1
2a24106: mov qword ptr [rsp + 0x40], rbx
2a2410b: mov qword ptr [rsp + 0x48], rbp
2a24110: mov qword ptr [rsp + 0x50], rax
2a24115: mov rdi, r14
2a24118: mov rsi, r13
2a2411b: mov rdx, rbp
2a2411e: call 0x4a07a30
2a24123: mov byte ptr [r14 + rbp], 0
2a24128: lea rdi, [rsp + 0x90]
2a24130: lea rsi, [rsp + 0x40]
2a24135: call 0x2a83d90
2a2413a: test byte ptr [rsp + 0x40], 1
2a2413f: je 0x2a24154
2a24141: mov rsi, qword ptr [rsp + 0x40]
2a24146: mov rdi, qword ptr [rsp + 0x50]
2a2414b: and rsi, 0xfffffffffffffffe
2a2414f: call 0x498cfc8
2a24154: mov rax, qword ptr [r15]
2a24157: mov rdi, r15
2a2415a: call qword ptr [rax + 0x18]
2a2415d: mov ebp, 6
2a24162: test rax, rax
2a24165: je 0x2a247ed
2a2416b: mov rax, qword ptr [r12]
2a2416f: mov rdi, r12
2a24172: call qword ptr [rax + 0x18]
2a24175: test rax, rax
2a24178: je 0x2a247ed
2a2417e: cmp byte ptr [rsp + 0xa8], 1
2a24186: jne 0x2a247ed
2a2418c: xorps xmm0, xmm0
2a2418f: movaps xmmword ptr [rsp + 0x40], xmm0
2a24194: mov qword ptr [rsp + 0x50], 0
2a2419d: mov rax, qword ptr [rsp + 8]
2a241a2: mov rdi, qword ptr [rax + 0x1a8]
2a241a9: lea rsi, [rsp + 0x40]
2a241ae: call 0x2a710f0
2a241b3: test eax, eax
2a241b5: je 0x2a2482e
2a241bb: mov rdi, qword ptr [rsp + 8]
2a241c0: mov rax, qword ptr [rdi]
2a241c3: call qword ptr [rax + 0x40]
2a241c6: mov rax, qword ptr [rsp + 8]
2a241cb: lea rdi, [rax + 0x20]
2a241cf: mov qword ptr [rsp + 0x78], rdi
2a241d4: mov byte ptr [rsp + 0x80], 1
2a241dc: call 0x49b5980
2a241e1: mov rax, qword ptr [rsp + 8]
2a241e6: mov r13, qword ptr [rax + 0x1a8]
2a241ed: test r13, r13
2a241f0: jne 0x2a2427f
2a241f6: mov edi, 0x1c8
2a241fb: call 0x4a078f0
2a24200: mov r13, rax
2a24203: mov edx, 0x1c8
2a24208: mov rdi, rax
2a2420b: xor esi, esi
2a2420d: call 0x4a07ba0
2a24212: mov rdi, r13
2a24215: call 0x49b6070
2a2421a: lea rdi, [r13 + 0x90]
2a24221: xor esi, esi
2a24223: call 0x46d2ec0
2a24228: mov byte ptr [r13 + 0x1c0], 0
2a24230: mov rax, qword ptr [rsp + 8]
2a24235: mov r14, qword ptr [rax + 0x1a8]
2a2423c: mov qword ptr [rax + 0x1a8], r13
2a24243: test r14, r14
2a24246: je 0x2a2427f
2a24248: lea rdi, [r14 + 0x90]
2a2424f: call 0x46d3460
2a24254: lea rdi, [r14 + 0x58]
2a24258: call 0x49b4af0
2a2425d: lea rdi, [r14 + 0x28]
2a24261: call 0x49b4af0
2a24266: mov esi, 0x1c8
2a2426b: mov rdi, r14
2a2426e: call 0x498cfc8
2a24273: mov rax, qword ptr [rsp + 8]
2a24278: mov r13, qword ptr [rax + 0x1a8]
2a2427f: mov rax, qword ptr [r15]
2a24282: mov rdi, r15
2a24285: call qword ptr [rax + 0x10]
2a24288: mov qword ptr [rsp + 0x70], rax
2a2428d: mov rax, qword ptr [r15]
2a24290: mov rdi, r15
2a24293: call qword ptr [rax + 0x18]
2a24296: mov r14, rax
2a24299: cmp rax, -0xa
2a2429d: ja 0x2a24892
2a242a3: cmp r14, 0x16
2a242a7: ja 0x2a242ba
2a242a9: mov eax, r14d
2a242ac: add al, r14b
2a242af: mov byte ptr [rsp + 0x10], al
2a242b3: lea rbx, [rsp + 0x11]
2a242b8: jmp 0x2a242f0
2a242ba: mov rax, r14
2a242bd: and rax, 0xfffffffffffffff8
2a242c1: add rax, 8
2a242c5: cmp rax, 0x18
2a242c9: mov ebp, 0x1a
2a242ce: cmovne rbp, rax
2a242d2: mov rdi, rbp
2a242d5: call 0x4a078f0
2a242da: mov rbx, rax
2a242dd: or rbp, 1
2a242e1: mov qword ptr [rsp + 0x10], rbp
2a242e6: mov qword ptr [rsp + 0x18], r14
2a242eb: mov qword ptr [rsp + 0x20], rax
2a242f0: mov rdi, rbx
2a242f3: mov rsi, qword ptr [rsp + 0x70]
2a242f8: mov rdx, r14
2a242fb: call 0x4a07a30
2a24300: mov byte ptr [rbx + r14], 0
2a24305: lea rsi, [rsp + 0x10]
2a2430a: mov rdi, r13
2a2430d: call 0x2a728f0
2a24312: test byte ptr [rsp + 0x10], 1
2a24317: je 0x2a2432c
2a24319: mov rsi, qword ptr [rsp + 0x10]
2a2431e: mov rdi, qword ptr [rsp + 0x20]
2a24323: and rsi, 0xfffffffffffffffe
2a24327: call 0x498cfc8
2a2432c: mov rax, qword ptr [rsp + 8]
2a24331: mov rax, qword ptr [rax + 0x1a8]
2a24338: mov qword ptr [rsp + 0x70], rax
2a2433d: mov rax, qword ptr [r12]
2a24341: mov rdi, r12
2a24344: call qword ptr [rax + 0x10]
2a24347: mov rbp, rax
2a2434a: mov rax, qword ptr [r12]
2a2434e: mov rdi, r12
2a24351: call qword ptr [rax + 0x18]
2a24354: mov r14, rax
2a24357: cmp rax, -0xa
2a2435b: ja 0x2a24892
2a24361: cmp r14, 0x16
2a24365: ja 0x2a24378
2a24367: mov eax, r14d
2a2436a: add al, r14b
2a2436d: mov byte ptr [rsp + 0x28], al
2a24371: lea rbx, [rsp + 0x29]
2a24376: jmp 0x2a243af
2a24378: mov rax, r14
2a2437b: and rax, 0xfffffffffffffff8
2a2437f: add rax, 8
2a24383: cmp rax, 0x18
2a24387: mov r13d, 0x1a
2a2438d: cmovne r13, rax
2a24391: mov rdi, r13
2a24394: call 0x4a078f0
2a24399: mov rbx, rax
2a2439c: or r13, 1
2a243a0: mov qword ptr [rsp + 0x28], r13
2a243a5: mov qword ptr [rsp + 0x30], r14
2a243aa: mov qword ptr [rsp + 0x38], rax
2a243af: mov rdi, rbx
2a243b2: mov rsi, rbp
2a243b5: mov rdx, r14
2a243b8: call 0x4a07a30
2a243bd: mov byte ptr [rbx + r14], 0
2a243c2: mov rax, qword ptr [r15]
2a243c5: mov rdi, r15
2a243c8: call qword ptr [rax + 0x10]
2a243cb: mov rbp, rax
2a243ce: mov rax, qword ptr [r15]
2a243d1: mov rdi, r15
2a243d4: call qword ptr [rax + 0x18]
2a243d7: mov r14, rax
2a243da: cmp rax, -0xa
2a243de: ja 0x2a24892
2a243e4: cmp r14, 0x16
2a243e8: ja 0x2a243fb
2a243ea: mov eax, r14d
2a243ed: add al, r14b
2a243f0: mov byte ptr [rsp + 0x58], al
2a243f4: lea rbx, [rsp + 0x59]
2a243f9: jmp 0x2a24432
2a243fb: mov rax, r14
2a243fe: and rax, 0xfffffffffffffff8
2a24402: add rax, 8
2a24406: cmp rax, 0x18
2a2440a: mov r13d, 0x1a
2a24410: cmovne r13, rax
2a24414: mov rdi, r13
2a24417: call 0x4a078f0
2a2441c: mov rbx, rax
2a2441f: or r13, 1
2a24423: mov qword ptr [rsp + 0x58], r13
2a24428: mov qword ptr [rsp + 0x60], r14
2a2442d: mov qword ptr [rsp + 0x68], rax
2a24432: mov rdi, rbx
2a24435: mov rsi, rbp
2a24438: mov rdx, r14
2a2443b: call 0x4a07a30
2a24440: mov byte ptr [rbx + r14], 0
2a24445: lea rbx, [rsp + 0x10]
2a2444a: lea rsi, [rsp + 0x28]
2a2444f: lea rdx, [rsp + 0x58]
2a24454: mov rdi, rbx
2a24457: call 0x2a7fc70
2a2445c: mov rdi, qword ptr [rsp + 0x70]
2a24461: mov rsi, rbx
2a24464: call 0x2a72970
2a24469: test byte ptr [rsp + 0x10], 1
2a2446e: jne 0x2a244d8
2a24470: test byte ptr [rsp + 0x58], 1
2a24475: jne 0x2a244f2
2a24477: test byte ptr [rsp + 0x28], 1
2a2447c: je 0x2a24491
2a2447e: mov rsi, qword ptr [rsp + 0x28]
2a24483: mov rdi, qword ptr [rsp + 0x38]
2a24488: and rsi, 0xfffffffffffffffe
2a2448c: call 0x498cfc8
2a24491: mov rax, qword ptr [rsp + 8]
2a24496: mov r13, qword ptr [rax + 0x1a8]
2a2449d: mov rax, qword ptr [r12]
2a244a1: mov rdi, r12
2a244a4: call qword ptr [rax + 0x10]
2a244a7: mov rbp, rax
2a244aa: mov rax, qword ptr [r12]
2a244ae: mov rdi, r12
2a244b1: call qword ptr [rax + 0x18]
2a244b4: mov r14, rax
2a244b7: cmp rax, -0xa
2a244bb: ja 0x2a24892
2a244c1: cmp r14, 0x16
2a244c5: ja 0x2a24515
2a244c7: mov eax, r14d
2a244ca: add al, r14b
2a244cd: mov byte ptr [rsp + 0x28], al
2a244d1: lea rbx, [rsp + 0x29]
2a244d6: jmp 0x2a2454c
2a244d8: mov rsi, qword ptr [rsp + 0x10]
2a244dd: mov rdi, qword ptr [rsp + 0x20]
2a244e2: and rsi, 0xfffffffffffffffe
2a244e6: call 0x498cfc8
2a244eb: test byte ptr [rsp + 0x58], 1
2a244f0: je 0x2a24477
2a244f2: mov rsi, qword ptr [rsp + 0x58]
2a244f7: mov rdi, qword ptr [rsp + 0x68]
2a244fc: and rsi, 0xfffffffffffffffe
2a24500: call 0x498cfc8
2a24505: test byte ptr [rsp + 0x28], 1
2a2450a: jne 0x2a2447e
2a24510: jmp 0x2a24491
2a24515: mov rax, r14
2a24518: and rax, 0xfffffffffffffff8
2a2451c: add rax, 8
2a24520: cmp rax, 0x18
2a24524: mov r12d, 0x1a
2a2452a: cmovne r12, rax
2a2452e: mov rdi, r12
2a24531: call 0x4a078f0
2a24536: mov rbx, rax
2a24539: or r12, 1
2a2453d: mov qword ptr [rsp + 0x28], r12
2a24542: mov qword ptr [rsp + 0x30], r14
2a24547: mov qword ptr [rsp + 0x38], rax
2a2454c: mov rdi, rbx
2a2454f: mov rsi, rbp
2a24552: mov rdx, r14
2a24555: call 0x4a07a30
2a2455a: mov byte ptr [rbx + r14], 0
2a2455f: mov rax, qword ptr [r15]
2a24562: mov rdi, r15
2a24565: call qword ptr [rax + 0x10]
2a24568: mov r12, rax
2a2456b: mov rax, qword ptr [r15]
2a2456e: mov rdi, r15
2a24571: call qword ptr [rax + 0x18]
2a24574: mov r14, rax
2a24577: cmp rax, -9
2a2457b: jae 0x2a24892
2a24581: cmp r14, 0x16
2a24585: ja 0x2a24598
2a24587: mov eax, r14d
2a2458a: add al, r14b
2a2458d: mov byte ptr [rsp + 0x58], al
2a24591: lea rbx, [rsp + 0x59]
2a24596: jmp 0x2a245cf
2a24598: mov rax, r14
2a2459b: and rax, 0xfffffffffffffff8
2a2459f: add rax, 8
2a245a3: cmp rax, 0x18
2a245a7: mov r15d, 0x1a
2a245ad: cmovne r15, rax
2a245b1: mov rdi, r15
2a245b4: call 0x4a078f0
2a245b9: mov rbx, rax
2a245bc: or r15, 1
2a245c0: mov qword ptr [rsp + 0x58], r15
2a245c5: mov qword ptr [rsp + 0x60], r14
2a245ca: mov qword ptr [rsp + 0x68], rax
2a245cf: mov rdi, rbx
2a245d2: mov rsi, r12
2a245d5: mov rdx, r14
2a245d8: call 0x4a07a30
2a245dd: mov byte ptr [rbx + r14], 0
2a245e2: lea rbx, [rsp + 0x10]
2a245e7: lea rsi, [rsp + 0x28]
2a245ec: lea rdx, [rsp + 0x58]
2a245f1: mov rdi, rbx
2a245f4: call 0x2a7fe30
2a245f9: mov rdi, r13
2a245fc: mov rsi, rbx
2a245ff: call 0x2a72a70
2a24604: test byte ptr [rsp + 0x10], 1
2a24609: jne 0x2a246f9
2a2460f: test byte ptr [rsp + 0x58], 1
2a24614: jne 0x2a24717
2a2461a: test byte ptr [rsp + 0x28], 1
2a2461f: je 0x2a24634
2a24621: mov rsi, qword ptr [rsp + 0x28]
2a24626: mov rdi, qword ptr [rsp + 0x38]
2a2462b: and rsi, 0xfffffffffffffffe
2a2462f: call 0x498cfc8
2a24634: mov eax, 0xffffffff
2a24639: mov rcx, qword ptr [rsp + 8]
2a2463e: xchg dword ptr [rcx + 0x20c], eax
2a24644: mov rax, qword ptr [rsp + 0x88]
2a2464c: mov r15, qword ptr [rax + 8]
2a24650: movups xmm0, xmmword ptr [rax]
2a24653: movaps xmmword ptr [rsp + 0xc0], xmm0
2a2465b: test r15, r15
2a2465e: je 0x2a24665
2a24660: lock inc qword ptr [r15 + 8]
2a24665: xorps xmm0, xmm0
2a24668: movaps xmmword ptr [rsp + 0xb0], xmm0
2a24670: movaps xmmword ptr [rsp + 0x10], xmm0
2a24675: mov qword ptr [rsp + 0x20], 0
2a2467e: cmp byte ptr [rsp + 0xa8], 0
2a24686: je 0x2a24890
2a2468c: lea rbx, [rsp + 0x28]
2a24691: lea rsi, [rsp + 0x90]
2a24699: mov rdi, rbx
2a2469c: call 0x2a84190
2a246a1: mov rdi, qword ptr [rsp + 8]
2a246a6: lea rdx, [rdi + 0x240]
2a246ad: lea rsi, [rsp + 0x78]
2a246b2: lea rcx, [rsp + 0xc0]
2a246ba: lea r8, [rsp + 0xb0]
2a246c2: lea r9, [rsp + 0x10]
2a246c7: push rbx
2a246c8: push 0
2a246ca: call 0x2a220b0
2a246cf: add rsp, 0x10
2a246d3: mov ebp, eax
2a246d5: test byte ptr [rsp + 0x28], 1
2a246da: jne 0x2a2473a
2a246dc: test byte ptr [rsp + 0x10], 1
2a246e1: jne 0x2a24754
2a246e3: mov rbx, qword ptr [rsp + 0xb8]
2a246eb: test rbx, rbx
2a246ee: jne 0x2a24774
2a246f4: jmp 0x2a24797
2a246f9: mov rsi, qword ptr [rsp + 0x10]
2a246fe: mov rdi, qword ptr [rsp + 0x20]
2a24703: and rsi, 0xfffffffffffffffe
2a24707: call 0x498cfc8
2a2470c: test byte ptr [rsp + 0x58], 1
2a24711: je 0x2a2461a
2a24717: mov rsi, qword ptr [rsp + 0x58]
2a2471c: mov rdi, qword ptr [rsp + 0x68]
2a24721: and rsi, 0xfffffffffffffffe
2a24725: call 0x498cfc8
2a2472a: test byte ptr [rsp + 0x28], 1
2a2472f: jne 0x2a24621
2a24735: jmp 0x2a24634
2a2473a: mov rsi, qword ptr [rsp + 0x28]
2a2473f: mov rdi, qword ptr [rsp + 0x38]
2a24744: and rsi, 0xfffffffffffffffe
2a24748: call 0x498cfc8
2a2474d: test byte ptr [rsp + 0x10], 1
2a24752: je 0x2a246e3
2a24754: mov rsi, qword ptr [rsp + 0x10]
2a24759: mov rdi, qword ptr [rsp + 0x20]
2a2475e: and rsi, 0xfffffffffffffffe
2a24762: call 0x498cfc8
2a24767: mov rbx, qword ptr [rsp + 0xb8]
2a2476f: test rbx, rbx
2a24772: je 0x2a24797
2a24774: mov rax, 0xffffffffffffffff
2a2477b: lock xadd qword ptr [rbx + 8], rax
2a24781: test rax, rax
2a24784: jne 0x2a24797
2a24786: mov rax, qword ptr [rbx]
2a24789: mov rdi, rbx
2a2478c: call qword ptr [rax + 0x10]
2a2478f: mov rdi, rbx
2a24792: call 0x49a8780
2a24797: test r15, r15
2a2479a: je 0x2a247bf
2a2479c: mov rax, 0xffffffffffffffff
2a247a3: lock xadd qword ptr [r15 + 8], rax
2a247a9: test rax, rax
2a247ac: jne 0x2a247bf
2a247ae: mov rax, qword ptr [r15]
2a247b1: mov rdi, r15
2a247b4: call qword ptr [rax + 0x10]
2a247b7: mov rdi, r15
2a247ba: call 0x49a8780
2a247bf: cmp byte ptr [rsp + 0x80], 1
2a247c7: jne 0x2a247d3
2a247c9: mov rdi, qword ptr [rsp + 0x78]
2a247ce: call 0x49b59b0
2a247d3: test byte ptr [rsp + 0x40], 1
2a247d8: je 0x2a247ed
2a247da: mov rsi, qword ptr [rsp + 0x40]
2a247df: mov rdi, qword ptr [rsp + 0x50]
2a247e4: and rsi, 0xfffffffffffffffe
2a247e8: call 0x498cfc8
2a247ed: cmp byte ptr [rsp + 0xa8], 1
2a247f5: jne 0x2a2481a
2a247f7: test byte ptr [rsp + 0x90], 1
2a247ff: je 0x2a2481a
2a24801: mov rsi, qword ptr [rsp + 0x90]
2a24809: mov rdi, qword ptr [rsp + 0xa0]
2a24811: and rsi, 0xfffffffffffffffe
2a24815: call 0x498cfc8
2a2481a: mov eax, ebp
2a2481c: add rsp, 0xd8
2a24823: pop rbx
2a24824: pop r12
2a24826: pop r13
2a24828: pop r14
2a2482a: pop r15
2a2482c: pop rbp
2a2482d: ret
2a2482e: mov rax, qword ptr [r15]
2a24831: mov rdi, r15
2a24834: call qword ptr [rax + 0x10]
2a24837: mov r14, rax
2a2483a: mov rax, qword ptr [r15]
2a2483d: mov rdi, r15
2a24840: call qword ptr [rax + 0x18]
2a24843: mov r13, rax
2a24846: cmp rax, -1
2a2484a: je 0x2a24897
2a2484c: movzx ebx, byte ptr [rsp + 0x40]
2a24851: mov eax, ebx
2a24853: shr ebx, 1
2a24855: test al, 1
2a24857: cmovne rbx, qword ptr [rsp + 0x48]
2a2485d: lea rdi, [rsp + 0x41]
2a24862: cmovne rdi, qword ptr [rsp + 0x50]
2a24868: cmp r13, rbx
2a2486b: mov rdx, rbx
2a2486e: cmovb rdx, r13
2a24872: mov rsi, r14
2a24875: call 0x4a07bb0
2a2487a: test eax, eax
2a2487c: jne 0x2a241bb
2a24882: cmp rbx, r13
2a24885: je 0x2a241c6
2a2488b: jmp 0x2a241bb
2a24890: ud2
2a24892: call 0x18f8660
2a24897: call 0x191b050
2a2489c: mov rbx, rax
2a2489f: lea rdi, [r13 + 0x58]
2a248a3: call 0x49b4af0
2a248a8: mov rdi, r13
2a248ab: add rdi, 0x28
2a248af: call 0x49b4af0
2a248b4: jmp 0x2a248b9
2a248b6: mov rbx, rax
2a248b9: mov esi, 0x1c8
2a248be: mov rdi, r13
2a248c1: call 0x498cfc8
2a248c6: mov rdi, rbx
2a248c9: call 0x18f83c0
2a248ce: mov rdi, rax
2a248d1: call 0x18f83c0
2a248d6: nop word ptr cs:[rax + rax]

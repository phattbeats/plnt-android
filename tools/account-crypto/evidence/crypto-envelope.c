// FUNCTION 02b83130 bounds 02b831ef

undefined8 * FUN_02b83130(undefined8 *param_1,long param_2,byte *param_3)

{
  byte bVar1;
  long *plVar2;
  char cVar3;
  long *plStack_28;
  undefined8 uStack_20;

  FUN_04858b50(&plStack_28);
  bVar1 = *param_3;
  if ((bVar1 & 1) == 0) {
    if ((uint)(bVar1 >> 1) != *(uint *)(param_2 + 0x30)) goto LAB_02b83199;
  }
  else if (*(ulong *)(param_3 + 8) != (ulong)*(uint *)(param_2 + 0x30)) goto LAB_02b83199;
  if ((bVar1 & 1) == 0) {
    param_3 = param_3 + 1;
  }
  else {
    param_3 = *(byte **)(param_3 + 0x10);
  }
  cVar3 = (**(code **)(*plStack_28 + 0x10))(plStack_28,param_3);
  if (cVar3 != '\0') {
    *param_1 = plStack_28;
    *(undefined1 *)(param_1 + 1) = 1;
    return param_1;
  }
LAB_02b83199:
  FUN_04858b50(&uStack_20);
  plVar2 = plStack_28;
  *param_1 = uStack_20;
  *(undefined1 *)(param_1 + 1) = 0;
  plStack_28 = (long *)0x0;
  if (plVar2 != (long *)0x0) {
    (**(code **)(*plVar2 + 8))();
  }
  return param_1;
}


// FUNCTION 02b82c00 bounds 02b82e3f

undefined8 * FUN_02b82c00(undefined8 *param_1,long *param_2,byte *param_3,long *param_4)

{
  byte bVar1;
  code *pcVar2;
  undefined4 uVar3;
  undefined4 uVar4;
  undefined4 uVar5;
  undefined1 *puVar6;
  ulong uVar7;
  undefined8 *puVar8;
  ulong uVar9;
  uint uVar10;
  byte *extraout_RDX;
  undefined8 *unaff_RBX;
  undefined8 unaff_RBP;
  ulong uVar11;
  undefined1 *puVar12;
  undefined1 *puVar13;
  undefined1 *unaff_R12;
  undefined1 *puVar14;
  bool bVar15;
  ulong unaff_R13;
  ulong unaff_R14;
  ulong unaff_R15;

  do {
    *(undefined8 *)((long)register0x00000020 + -8) = unaff_RBP;
    *(ulong *)((long)register0x00000020 + -0x10) = unaff_R15;
    *(ulong *)((long)register0x00000020 + -0x18) = unaff_R14;
    *(ulong *)((long)register0x00000020 + -0x20) = unaff_R13;
    *(undefined1 **)((long)register0x00000020 + -0x28) = unaff_R12;
    *(undefined8 **)((long)register0x00000020 + -0x30) = unaff_RBX;
    if ((*param_3 & 1) == 0) {
      if (*param_3 >> 1 == 0) goto LAB_02b82c79;
    }
    else if (*(long *)(param_3 + 8) == 0) {
LAB_02b82c79:
      *param_1 = 0;
      param_1[1] = 0;
      param_1[2] = 0;
      return param_1;
    }
    unaff_R12 = (undefined1 *)((long)register0x00000020 + -0x68);
    pcVar2 = *(code **)(*param_2 + 0x30);
    *(undefined8 *)((long)register0x00000020 + -0xa0) = 0x2b82c3a;
    (*pcVar2)(unaff_R12);
    *(undefined8 *)((long)register0x00000020 + -0xa0) = 0x2b82c4b;
    puVar6 = (undefined1 *)
             FUN_02b83440((undefined1 *)((long)register0x00000020 + -0x98),param_4,param_3,unaff_R12
                         );
    if (*(char *)((long)register0x00000020 + -0x80) == '\0') {
      *param_1 = 0;
      param_1[1] = 0;
      param_1[2] = 0;
      bVar1 = *(byte *)((long)register0x00000020 + -0x98);
      goto joined_r0x02b82c9e;
    }
    bVar1 = *(byte *)((long)register0x00000020 + -0x68);
    uVar10 = (uint)bVar1;
    if ((bVar1 & 1) == 0) {
      unaff_R14 = (ulong)(bVar1 >> 1);
      uVar9 = (ulong)*(byte *)((long)register0x00000020 + -0x98);
      if ((*(byte *)((long)register0x00000020 + -0x98) & 1) == 0) goto LAB_02b82c6a;
LAB_02b82cb8:
      unaff_R15 = *(ulong *)((long)register0x00000020 + -0x90);
    }
    else {
      unaff_R14 = *(ulong *)((long)register0x00000020 + -0x60);
      uVar9 = (ulong)*(byte *)((long)register0x00000020 + -0x98);
      if ((*(byte *)((long)register0x00000020 + -0x98) & 1) != 0) goto LAB_02b82cb8;
LAB_02b82c6a:
      unaff_R15 = uVar9 >> 1;
    }
    unaff_R13 = unaff_R15 + unaff_R14;
    if (unaff_R13 < 0xfffffffffffffff7) {
      puVar12 = *(undefined1 **)((long)register0x00000020 + -0x58);
      puVar13 = *(undefined1 **)((long)register0x00000020 + -0x88);
      *(undefined1 **)((long)register0x00000020 + -0x70) = puVar13;
      if (unaff_R13 < 0x17) {
        bVar15 = false;
        *(char *)((long)register0x00000020 + -0x50) = (char)unaff_R13 * '\x02';
      }
      else {
        *(undefined1 **)((long)register0x00000020 + -0x38) = puVar12;
        *(uint *)((long)register0x00000020 + -0x74) = (uint)bVar1;
        uVar7 = (unaff_R13 & 0xfffffffffffffff8) + 8;
        uVar11 = 0x1a;
        if (uVar7 != 0x18) {
          uVar11 = uVar7;
        }
        *(undefined8 *)((long)register0x00000020 + -0xa0) = 0x2b82d16;
        puVar6 = (undefined1 *)func_0x04b078f0(uVar11);
        *(ulong *)((long)register0x00000020 + -0x50) = uVar11 | 1;
        *(ulong *)((long)register0x00000020 + -0x48) = unaff_R13;
        *(undefined1 **)((long)register0x00000020 + -0x40) = puVar6;
        bVar15 = true;
        uVar10 = *(uint *)((long)register0x00000020 + -0x74);
        puVar13 = *(undefined1 **)((long)register0x00000020 + -0x70);
        puVar12 = *(undefined1 **)((long)register0x00000020 + -0x38);
      }
      puVar14 = (undefined1 *)((long)register0x00000020 + -0x97);
      if ((uVar9 & 1) != 0) {
        puVar14 = puVar13;
      }
      puVar13 = (undefined1 *)((long)register0x00000020 + -0x67);
      if ((uVar10 & 1) != 0) {
        puVar13 = puVar12;
      }
      puVar12 = (undefined1 *)((long)register0x00000020 + -0x4f);
      if (bVar15) {
        puVar12 = puVar6;
      }
      *(undefined8 *)((long)register0x00000020 + -0xa0) = 0x2b82d70;
      func_0x04b07a30(puVar12,puVar13,unaff_R14);
      *(undefined8 *)((long)register0x00000020 + -0xa0) = 0x2b82d81;
      func_0x04b07a30(puVar12 + unaff_R14,puVar14,unaff_R15);
      (puVar12 + unaff_R14)[unaff_R15] = 0;
      if ((uVar9 & 1) != 0) {
        *(undefined8 *)((long)register0x00000020 + -0xa0) = 0x2b82d9e;
        func_0x04a8cfc8(*(undefined8 *)((long)register0x00000020 + -0x70),
                        *(ulong *)((long)register0x00000020 + -0x98) & 0xfffffffffffffffe);
      }
      *(undefined8 *)((long)register0x00000020 + -0x88) =
           *(undefined8 *)((long)register0x00000020 + -0x40);
      *(undefined4 *)((long)register0x00000020 + -0x98) =
           *(undefined4 *)((long)register0x00000020 + -0x50);
      *(undefined4 *)((long)register0x00000020 + -0x94) =
           *(undefined4 *)((long)register0x00000020 + -0x4c);
      *(undefined4 *)((long)register0x00000020 + -0x90) =
           *(undefined4 *)((long)register0x00000020 + -0x48);
      *(undefined4 *)((long)register0x00000020 + -0x8c) =
           *(undefined4 *)((long)register0x00000020 + -0x44);
      if ((*(byte *)((long)register0x00000020 + -0x98) & 1) == 0) {
        param_1[2] = *(undefined8 *)((long)register0x00000020 + -0x88);
        uVar3 = *(undefined4 *)((long)register0x00000020 + -0x94);
        uVar4 = *(undefined4 *)((long)register0x00000020 + -0x90);
        uVar5 = *(undefined4 *)((long)register0x00000020 + -0x8c);
        *(undefined4 *)param_1 = *(undefined4 *)((long)register0x00000020 + -0x98);
        *(undefined4 *)((long)param_1 + 4) = uVar3;
        *(undefined4 *)(param_1 + 1) = uVar4;
        *(undefined4 *)((long)param_1 + 0xc) = uVar5;
        bVar1 = *(byte *)((long)register0x00000020 + -0x98);
      }
      else {
        *(undefined8 *)((long)register0x00000020 + -0xa0) = 0x2b82dc9;
        func_0x019f86e0(param_1,*(undefined8 *)((long)register0x00000020 + -0x88),
                        *(undefined8 *)((long)register0x00000020 + -0x90));
        bVar1 = *(byte *)((long)register0x00000020 + -0x98);
      }
joined_r0x02b82c9e:
      if ((bVar1 & 1) != 0) {
        *(undefined8 *)((long)register0x00000020 + -0xa0) = 0x2b82de1;
        func_0x04a8cfc8(*(undefined8 *)((long)register0x00000020 + -0x88),
                        *(ulong *)((long)register0x00000020 + -0x98) & 0xfffffffffffffffe);
      }
      if ((*(byte *)((long)register0x00000020 + -0x68) & 1) != 0) {
        *(undefined8 *)((long)register0x00000020 + -0xa0) = 0x2b82dfb;
        func_0x04a8cfc8(*(undefined8 *)((long)register0x00000020 + -0x58),
                        *(ulong *)((long)register0x00000020 + -0x68) & 0xfffffffffffffffe);
      }
      return param_1;
    }
    *(undefined8 *)((long)register0x00000020 + -0xa0) = 0x2b82e2a;
    puVar8 = (undefined8 *)func_0x019f8660();
    *(undefined8 *)((long)register0x00000020 + -0xa0) = 0x2b82e32;
    param_2 = param_4;
    func_0x019f83c0();
    param_4 = param_2 + 5;
    param_3 = extraout_RDX;
    unaff_RBX = param_1;
    register0x00000020 = (BADSPACEBASE *)((long)register0x00000020 + -0x98);
    param_1 = puVar8;
  } while( true );
}


// FUNCTION 02b82e60 bounds 02b8302f

/* WARNING: Type propagation algorithm not settling */

undefined8 * FUN_02b82e60(undefined8 *param_1,undefined8 param_2,byte *param_3,byte *param_4)

{
  byte bVar1;
  undefined4 uVar2;
  undefined4 uVar3;
  undefined4 uVar4;
  ulong uVar5;
  undefined1 *puVar6;
  undefined8 *puVar7;
  byte *pbVar8;
  byte *extraout_RDX;
  undefined8 *unaff_RBX;
  undefined8 unaff_RBP;
  ulong uVar9;
  byte *pbVar10;
  ulong unaff_R12;
  ulong uVar11;
  ulong unaff_R13;
  byte *unaff_R14;
  byte *unaff_R15;

  while( true ) {
    pbVar8 = param_4;
    *(undefined8 *)((long)register0x00000020 + -8) = unaff_RBP;
    *(byte **)((long)register0x00000020 + -0x10) = unaff_R15;
    *(byte **)((long)register0x00000020 + -0x18) = unaff_R14;
    *(ulong *)((long)register0x00000020 + -0x20) = unaff_R13;
    *(ulong *)((long)register0x00000020 + -0x28) = unaff_R12;
    *(undefined8 **)((long)register0x00000020 + -0x30) = unaff_RBX;
    bVar1 = *param_3;
    if ((bVar1 & 1) == 0) {
      unaff_R12 = (ulong)(bVar1 >> 1);
    }
    else {
      unaff_R12 = *(ulong *)(param_3 + 8);
    }
    if (unaff_R12 == 0) {
      *param_1 = 0;
      param_1[1] = 0;
      param_1[2] = 0;
      return param_1;
    }
    unaff_R13 = 0x10;
    if (unaff_R12 < 0x10) {
      unaff_R13 = unaff_R12;
    }
    if ((bVar1 & 1) == 0) {
      unaff_R15 = param_3 + 1;
    }
    else {
      unaff_R15 = *(byte **)(param_3 + 0x10);
    }
    *(char *)((long)register0x00000020 + -0x48) = (char)unaff_R13 * '\x02';
    *(undefined8 *)((long)register0x00000020 + -0x90) = 0x2b82edc;
    pbVar10 = unaff_R15;
    func_0x04b07a30((undefined1 *)((long)register0x00000020 + -0x47),unaff_R15,unaff_R13);
    *(undefined1 *)((long)register0x00000020 + (unaff_R13 - 0x47)) = 0;
    if (0xf < unaff_R12) break;
    *(undefined8 *)((long)register0x00000020 + -0x90) = 0x2b83027;
    puVar7 = (undefined8 *)func_0x01a1b050();
    *(undefined8 *)((long)register0x00000020 + -0x90) = 0x2b8302f;
    func_0x019f83c0();
    param_4 = pbVar10 + 0x28;
    param_3 = extraout_RDX;
    unaff_RBX = param_1;
    register0x00000020 = (BADSPACEBASE *)((long)register0x00000020 + -0x88);
    param_1 = puVar7;
    unaff_R14 = pbVar8;
  }
  uVar11 = unaff_R12 - 0x10;
  if (uVar11 < 0x17) {
    *(char *)((long)register0x00000020 + -0x80) = (char)uVar11 * '\x02';
    puVar6 = (undefined1 *)((long)register0x00000020 + -0x7f);
  }
  else {
    uVar5 = (uVar11 & 0xfffffffffffffff8) + 8;
    uVar9 = 0x1a;
    if (uVar5 != 0x18) {
      uVar9 = uVar5;
    }
    *(undefined8 *)((long)register0x00000020 + -0x90) = 0x2b82f25;
    puVar6 = (undefined1 *)func_0x04b078f0(uVar9);
    *(ulong *)((long)register0x00000020 + -0x80) = uVar9 | 1;
    *(ulong *)((long)register0x00000020 + -0x78) = uVar11;
    *(undefined1 **)((long)register0x00000020 + -0x70) = puVar6;
  }
  *(undefined8 *)((long)register0x00000020 + -0x90) = 0x2b82f4d;
  func_0x04b07a30(puVar6,unaff_R15 + 0x10,uVar11);
  puVar6[uVar11] = 0;
  *(undefined8 *)((long)register0x00000020 + -0x90) = 0x2b82f6a;
  FUN_02b83440((undefined1 *)((long)register0x00000020 + -0x68),pbVar8,
               (undefined1 *)((long)register0x00000020 + -0x80),
               (undefined1 *)((long)register0x00000020 + -0x48));
  if (*(char *)((long)register0x00000020 + -0x50) == '\0') {
    *param_1 = 0;
    param_1[1] = 0;
    param_1[2] = 0;
  }
  else {
    if ((*(byte *)((long)register0x00000020 + -0x68) & 1) == 0) {
      param_1[2] = *(undefined8 *)((long)register0x00000020 + -0x58);
      uVar2 = *(undefined4 *)((long)register0x00000020 + -100);
      uVar3 = *(undefined4 *)((long)register0x00000020 + -0x60);
      uVar4 = *(undefined4 *)((long)register0x00000020 + -0x5c);
      *(undefined4 *)param_1 = *(undefined4 *)((long)register0x00000020 + -0x68);
      *(undefined4 *)((long)param_1 + 4) = uVar2;
      *(undefined4 *)(param_1 + 1) = uVar3;
      *(undefined4 *)((long)param_1 + 0xc) = uVar4;
      bVar1 = *(byte *)((long)register0x00000020 + -0x68);
      goto joined_r0x02b82f9f;
    }
    *(undefined8 *)((long)register0x00000020 + -0x90) = 0x2b82f8a;
    func_0x019f86e0(param_1,*(undefined8 *)((long)register0x00000020 + -0x58),
                    *(undefined8 *)((long)register0x00000020 + -0x60));
  }
  bVar1 = *(byte *)((long)register0x00000020 + -0x68);
joined_r0x02b82f9f:
  if ((bVar1 & 1) == 0) {
    bVar1 = *(byte *)((long)register0x00000020 + -0x80);
  }
  else {
    *(undefined8 *)((long)register0x00000020 + -0x90) = 0x2b82fff;
    func_0x04a8cfc8(*(undefined8 *)((long)register0x00000020 + -0x58),
                    *(ulong *)((long)register0x00000020 + -0x68) & 0xfffffffffffffffe);
    bVar1 = *(byte *)((long)register0x00000020 + -0x80);
  }
  if ((bVar1 & 1) == 0) {
    bVar1 = *(byte *)((long)register0x00000020 + -0x48);
  }
  else {
    *(undefined8 *)((long)register0x00000020 + -0x90) = 0x2b83019;
    func_0x04a8cfc8(*(undefined8 *)((long)register0x00000020 + -0x70),
                    *(ulong *)((long)register0x00000020 + -0x80) & 0xfffffffffffffffe);
    bVar1 = *(byte *)((long)register0x00000020 + -0x48);
  }
  if ((bVar1 & 1) != 0) {
    *(undefined8 *)((long)register0x00000020 + -0x90) = 0x2b82fc2;
    func_0x04a8cfc8(*(undefined8 *)((long)register0x00000020 + -0x38),
                    *(ulong *)((long)register0x00000020 + -0x48) & 0xfffffffffffffffe);
  }
  return param_1;
}


// FUNCTION 04858ab0 bounds 04858aef

undefined8 FUN_04858ab0(undefined8 param_1,undefined8 param_2,undefined8 param_3,undefined8 param_4)

{
  int iVar1;
  undefined8 uStack_8;

  uStack_8 = 0;
  iVar1 = func_0x03d19790(param_3,param_4,&uStack_8,param_1,param_2);
  if (iVar1 == 0) {
    return uStack_8;
  }
  return 0;
}


// FUNCTION 03d16780 bounds 03d1679f

undefined8 FUN_03d16780(int param_1)

{
  if (param_1 - 3U < 7) {
    return *(undefined8 *)(&UNK_04c2f750 + (ulong)(param_1 - 3U) * 8);
  }
  return 0;
}


// FUNCTION 03d1edb0 bounds 03d1f1bf

int FUN_03d1edb0(undefined8 *param_1,undefined8 param_2,undefined8 param_3,undefined8 param_4,
                undefined8 param_5,uint param_6,undefined8 param_7,long param_8,undefined8 param_9,
                undefined8 param_10,undefined8 param_11,undefined8 param_12,undefined8 param_13,
                undefined8 param_14,undefined8 param_15,undefined8 param_16,undefined8 param_17,
                undefined8 param_18)

{
  char cVar1;
  char cVar2;
  uint uVar3;
  int iVar4;
  uint uVar5;
  ulong uVar6;
  uint uVar7;
  undefined8 unaff_RBX;
  byte bVar8;
  undefined8 unaff_RBP;
  undefined8 unaff_R12;
  undefined8 unaff_R13;
  uint uVar9;
  undefined8 unaff_R14;
  ulong uVar10;
  uint uVar11;
  undefined8 unaff_R15;
  uint unaff_retaddr;
  uint unaff_retaddr_00;
  undefined4 uStack_f0;
  uint uStack_ec;
  uint auStack_e8 [4];
  uint uStack_d8;
  uint uStack_d4;
  uint uStack_d0;
  uint uStack_cc;
  uint uStack_c8;
  uint uStack_c4;
  uint uStack_c0;
  uint uStack_bc;
  uint uStack_b8;
  uint uStack_b4;
  uint uStack_b0;
  uint uStack_ac;
  uint uStack_a8;
  uint uStack_a4;
  uint uStack_a0;
  uint uStack_9c;
  undefined8 uStack_98;
  undefined8 uStack_90;
  undefined8 uStack_88;
  undefined8 uStack_80;
  uint auStack_78 [4];
  uint uStack_68;
  uint uStack_64;
  uint uStack_60;
  uint uStack_5c;
  uint uStack_58;
  uint uStack_54;
  uint uStack_50;
  uint uStack_4c;
  uint uStack_48;
  uint uStack_44;
  uint uStack_40;
  uint uStack_3c;
  uint uStack_38;
  uint uStack_34;
  uint uVar12;
  uint uVar13;
  uint uVar14;
  uint uVar15;
  uint uVar16;

  uStack_ec = param_6;
  uStack_80 = param_5;
  uVar3 = func_0x03d171b0(*param_1);
  uStack_f0 = 0x1000000;
  iVar4 = func_0x03d16d00(param_1,param_2,param_3);
  if (iVar4 == 0) {
    uStack_88 = param_4;
    if ((uint)param_7 != 0) {
      uStack_90 = param_8;
      bVar8 = (byte)uVar3;
      uVar10 = (ulong)bVar8;
      uStack_98 = (ulong)(uVar3 & 0xe0);
      uVar7 = uVar3 & 0xfc;
      uVar11 = (uint)param_7;
      do {
        iVar4 = func_0x03d16f90(param_1,uStack_88,uStack_80);
        if ((((iVar4 != 0) || (iVar4 = func_0x03d16f90(param_1,&uStack_f0,4), iVar4 != 0)) ||
            (iVar4 = func_0x03d16fb0(param_1,auStack_e8), iVar4 != 0)) ||
           (iVar4 = func_0x03d17050(param_1), iVar4 != 0)) goto LAB_03d1f180;
        uStack_9c = uVar11;
        func_0x04b07ad0(auStack_78,auStack_e8,uVar10);
        uVar9 = uVar3 & 0xff;
        if (1 < uStack_ec) {
          uVar11 = 1;
          do {
            iVar4 = func_0x03d16f90(param_1,auStack_78,uVar10);
            if (((iVar4 != 0) || (iVar4 = func_0x03d16fb0(param_1,auStack_78), iVar4 != 0)) ||
               (iVar4 = func_0x03d17050(param_1), iVar4 != 0)) goto LAB_03d1f180;
            if (bVar8 != 0) {
              if (bVar8 < 4) {
                uVar6 = 0;
              }
              else {
                if (bVar8 < 0x20) {
                  uVar6 = 0;
                }
                else {
                  auStack_e8[0] = auStack_e8[0] ^ auStack_78[0];
                  auStack_e8[1] = auStack_e8[1] ^ auStack_78[1];
                  auStack_e8[2] = auStack_e8[2] ^ auStack_78[2];
                  auStack_e8[3] = auStack_e8[3] ^ auStack_78[3];
                  uStack_d8 = uStack_d8 ^ uStack_68;
                  uStack_d4 = uStack_d4 ^ uStack_64;
                  uStack_d0 = uStack_d0 ^ uStack_60;
                  uStack_cc = uStack_cc ^ uStack_5c;
                  uVar5 = (uint)uStack_98;
                  if (uVar5 != 0x20) {
                    uStack_c8 = uStack_c8 ^ uStack_58;
                    uStack_c4 = uStack_c4 ^ uStack_54;
                    uStack_c0 = uStack_c0 ^ uStack_50;
                    uStack_bc = uStack_bc ^ uStack_4c;
                    uStack_b8 = uStack_b8 ^ uStack_48;
                    uStack_b4 = uStack_b4 ^ uStack_44;
                    uStack_b0 = uStack_b0 ^ uStack_40;
                    uStack_ac = uStack_ac ^ uStack_3c;
                    if (uVar5 != 0x40) {
                      uVar12 = (uint)((ulong)unaff_RBX >> 0x20);
                      uVar13 = (uint)((ulong)unaff_R12 >> 0x20);
                      uVar14 = (uint)((ulong)unaff_R13 >> 0x20);
                      uStack_a8 = uStack_a8 ^ uStack_38;
                      uStack_a4 = uStack_a4 ^ uStack_34;
                      uStack_a0 = uStack_a0 ^ (uint)unaff_RBX;
                      uStack_9c = uStack_9c ^ uVar12;
                      uStack_98 = CONCAT44(uStack_98._4_4_ ^ uVar13,uVar5 ^ (uint)unaff_R12);
                      uStack_90 = CONCAT44(uStack_90._4_4_ ^ uVar14,
                                           (uint)uStack_90 ^ (uint)unaff_R13);
                      if (uVar5 != 0x60) {
                        uVar15 = (uint)((ulong)unaff_R14 >> 0x20);
                        uVar16 = (uint)((ulong)unaff_R15 >> 0x20);
                        uStack_88 = CONCAT44(uStack_88._4_4_ ^ uVar15,
                                             (uint)uStack_88 ^ (uint)unaff_R14);
                        uStack_80 = CONCAT44(uStack_80._4_4_ ^ uVar16,
                                             (uint)uStack_80 ^ (uint)unaff_R15);
                        auStack_78[0] = auStack_78[0] ^ (uint)unaff_RBP;
                        auStack_78[1] = auStack_78[1] ^ (uint)((ulong)unaff_RBP >> 0x20);
                        auStack_78[2] = auStack_78[2] ^ unaff_retaddr;
                        auStack_78[3] = auStack_78[3] ^ unaff_retaddr_00;
                        if (uVar5 != 0x80) {
                          uStack_68 = uStack_68 ^ (uint)param_7;
                          uStack_64 = uStack_64 ^ param_7._4_4_;
                          uStack_60 = uStack_60 ^ (uint)param_8;
                          uStack_5c = uStack_5c ^ param_8._4_4_;
                          uStack_58 = uStack_58 ^ (uint)param_9;
                          uStack_54 = uStack_54 ^ param_9._4_4_;
                          uStack_50 = uStack_50 ^ (uint)param_10;
                          uStack_4c = uStack_4c ^ param_10._4_4_;
                          if (uVar5 != 0xa0) {
                            uStack_48 = uStack_48 ^ (uint)param_11;
                            uStack_44 = uStack_44 ^ param_11._4_4_;
                            uStack_40 = uStack_40 ^ (uint)param_12;
                            uStack_3c = uStack_3c ^ param_12._4_4_;
                            uStack_38 = uStack_38 ^ (uint)param_13;
                            uStack_34 = uStack_34 ^ param_13._4_4_;
                            unaff_RBX = CONCAT44(uVar12 ^ param_14._4_4_,
                                                 (uint)unaff_RBX ^ (uint)param_14);
                            if (uVar5 != 0xc0) {
                              unaff_R12 = CONCAT44(uVar13 ^ param_15._4_4_,
                                                   (uint)unaff_R12 ^ (uint)param_15);
                              unaff_R13 = CONCAT44(uVar14 ^ param_16._4_4_,
                                                   (uint)unaff_R13 ^ (uint)param_16);
                              unaff_R14 = CONCAT44(uVar15 ^ param_17._4_4_,
                                                   (uint)unaff_R14 ^ (uint)param_17);
                              unaff_R15 = CONCAT44(uVar16 ^ param_18._4_4_,
                                                   (uint)unaff_R15 ^ (uint)param_18);
                            }
                          }
                        }
                      }
                    }
                  }
                  if (uVar5 == uVar9) goto LAB_03d1eef0;
                  uVar6 = uStack_98;
                  if ((uVar3 & 0x1c) == 0) goto LAB_03d1f110;
                }
                do {
                  *(uint *)((long)auStack_e8 + uVar6) =
                       *(uint *)((long)auStack_e8 + uVar6) ^ *(uint *)((long)auStack_78 + uVar6);
                  uVar6 = uVar6 + 4;
                } while (uVar7 != uVar6);
                uVar6 = (ulong)uVar7;
                if (uVar7 == uVar9) goto LAB_03d1eef0;
              }
LAB_03d1f110:
              do {
                *(byte *)((long)auStack_e8 + uVar6) =
                     *(byte *)((long)auStack_e8 + uVar6) ^ *(byte *)((long)auStack_78 + uVar6);
                uVar6 = uVar6 + 1;
              } while (uVar10 != uVar6);
            }
LAB_03d1eef0:
            uVar11 = uVar11 + 1;
          } while (uVar11 != uStack_ec);
        }
        uVar11 = uStack_9c;
        uVar6 = uVar10;
        if (uStack_9c < uVar9) {
          uVar6 = (ulong)uStack_9c;
        }
        func_0x04b07ad0(uStack_90,auStack_e8,uVar6);
        uVar9 = uStack_f0;
        cVar1 = uStack_f0._3_1_ + '\x01';
        uStack_f0 = CONCAT13(cVar1,(uint3)uStack_f0);
        if (cVar1 == '\0') {
          uStack_f0._2_1_ = SUB41(uVar9,2);
          cVar1 = uStack_f0._2_1_ + '\x01';
          uStack_f0._0_2_ = (ushort)uVar9;
          uStack_f0._0_3_ = CONCAT12(cVar1,(ushort)uStack_f0);
          uStack_f0 = (uint)(uint3)uStack_f0;
          if (cVar1 == '\0') {
            uStack_f0._1_1_ = SUB41(uVar9,1);
            cVar1 = uStack_f0._1_1_ + '\x01';
            uStack_f0._0_1_ = (char)uVar9;
            cVar2 = (char)uStack_f0;
            uStack_f0._0_2_ = CONCAT11(cVar1,(char)uStack_f0);
            uStack_f0 = (uint)(ushort)uStack_f0;
            if (cVar1 == '\0') {
              uStack_f0 = (uint)(byte)(cVar2 + 1);
            }
          }
        }
        uVar11 = uVar11 - (int)uVar6;
        uStack_90 = uStack_90 + uVar6;
      } while (uVar11 != 0);
    }
    iVar4 = 0;
LAB_03d1f180:
    func_0x03d1f350(auStack_e8,0x40);
    func_0x03d1f350(auStack_78,0x40);
  }
  return iVar4;
}


// FUNCTION 02b4a050 bounds 02b4a69f

ulong * FUN_02b4a050(ulong *param_1,undefined8 *param_2,byte *param_3,uint *param_4)

{
  code *pcVar1;
  undefined1 *puVar2;
  ulong uVar3;
  ulong uVar4;
  undefined1 *puVar5;
  long lVar6;
  ulong uVar7;
  byte *pbVar8;
  undefined1 **ppuVar9;
  ulong uVar10;
  undefined8 *puVar11;
  bool bVar12;
  undefined8 uStack_88;
  undefined8 uStack_80;
  undefined1 *apuStack_78 [2];
  undefined8 uStack_68;
  ulong uStack_60;
  ulong uStack_58;
  long lStack_48;
  undefined1 *puStack_40;
  undefined4 uStack_38;
  uint uStack_34;
  uint uStack_30;
  uint uStack_2c;
  long lStack_28;

  if ((*param_4 & 1) == 0) {
    lStack_28 = *(long *)(param_4 + 4);
    uStack_38 = *param_4;
    uStack_34 = param_4[1];
    uStack_30 = param_4[2];
    uStack_2c = param_4[3];
    uVar3 = (ulong)(byte)uStack_38;
    if ((uStack_38 & 1) != 0) goto LAB_02b4a0e0;
LAB_02b4a084:
    uVar4 = uVar3 >> 1;
  }
  else {
    func_0x019f86e0(&uStack_38,*(undefined8 *)(param_4 + 4),*(undefined8 *)(param_4 + 2));
    uVar3 = (ulong)(byte)uStack_38;
    if ((uStack_38 & 1) == 0) goto LAB_02b4a084;
LAB_02b4a0e0:
    uVar4 = CONCAT44(uStack_2c,uStack_30);
  }
  if (uVar4 != 0xc) {
    param_1[4] = 0;
    param_1[5] = 0;
    param_1[2] = 0;
    param_1[3] = 0;
    *param_1 = 0;
    param_1[1] = 0;
    *(undefined1 *)(param_1 + 6) = 0;
    if ((uVar3 & 1) == 0) {
      return param_1;
    }
    goto LAB_02b4a0a4;
  }
  lVar6 = lStack_28;
  if ((uVar3 & 1) == 0) {
    lVar6 = (long)&uStack_38 + 1;
  }
  bVar12 = (*param_3 & 1) != 0;
  pbVar8 = param_3 + 1;
  if (bVar12) {
    pbVar8 = *(byte **)(param_3 + 0x10);
  }
  uVar3 = (ulong)(*param_3 >> 1);
  if (bVar12) {
    uVar3 = *(ulong *)(param_3 + 8);
  }
  (**(code **)(*(long *)*param_2 + 0x20))(&lStack_48,(long *)*param_2,pbVar8,lVar6,uVar3);
  lVar6 = lStack_48;
  if (lStack_48 == 0 || puStack_40 == (undefined1 *)0x0) {
    param_1[4] = 0;
    param_1[5] = 0;
    param_1[2] = 0;
    param_1[3] = 0;
    *param_1 = 0;
    param_1[1] = 0;
    *(undefined1 *)(param_1 + 6) = 0;
    puVar2 = puStack_40;
  }
  else {
    if ((*param_3 & 1) == 0) {
      uVar3 = (ulong)(*param_3 >> 1);
    }
    else {
      uVar3 = *(ulong *)(param_3 + 8);
    }
    uStack_68 = 0;
    uStack_60 = 0;
    uStack_58 = 0;
    if (uVar3 == 0) {
      uVar4 = 0;
    }
    else {
      func_0x04aac620(&uStack_68,uVar3,0);
      uVar4 = uStack_68 & 0xff;
    }
    uVar7 = uVar4 >> 1;
    if ((uVar4 & 1) != 0) {
      uVar7 = uStack_60;
    }
    if (uVar3 - 1 < uVar7) {
      uVar4 = (long)&uStack_68 + 1;
      if (uVar3 == 1) {
        uVar7 = 0;
      }
      else {
        uVar7 = 0;
        do {
          uVar10 = uStack_60;
          if ((uStack_68 & 1) == 0) {
            uVar10 = (ulong)((byte)uStack_68 >> 1);
          }
          if (uVar10 < uVar7) goto LAB_02b4a631;
          uVar10 = uVar4;
          if ((uStack_68 & 1) != 0) {
            uVar10 = uStack_58;
          }
          *(undefined1 *)(uVar10 + uVar7) = *(undefined1 *)(lVar6 + uVar7);
          uVar10 = uStack_60;
          if ((uStack_68 & 1) == 0) {
            uVar10 = (ulong)((byte)uStack_68 >> 1);
          }
          if (uVar10 <= uVar7) goto LAB_02b4a631;
          uVar10 = uVar4;
          if ((uStack_68 & 1) != 0) {
            uVar10 = uStack_58;
          }
          *(undefined1 *)(uVar10 + 1 + uVar7) = *(undefined1 *)(lVar6 + 1 + uVar7);
          uVar7 = uVar7 + 2;
        } while ((uVar3 & 0xfffffffffffffffe) != uVar7);
        if ((uVar3 & 1) == 0) goto LAB_02b4a2db;
      }
      uVar3 = uStack_60;
      if ((uStack_68 & 1) == 0) {
        uVar3 = (ulong)((byte)uStack_68 >> 1);
      }
      if (uVar3 < uVar7) goto LAB_02b4a631;
      if ((uStack_68 & 1) != 0) {
        uVar4 = uStack_58;
      }
      *(undefined1 *)(uVar4 + uVar7) = *(undefined1 *)(lVar6 + uVar7);
    }
LAB_02b4a2db:
    puVar2 = puStack_40;
    uStack_88 = 0;
    uStack_80 = 0;
    apuStack_78[0] = (undefined1 *)0x0;
    func_0x04aac620(&uStack_88,0x10,0);
    if ((uStack_88 & 1) == 0) {
      if (0xf < (byte)uStack_88 >> 1) goto LAB_02b4a325;
    }
    else if (0xf < uStack_80) {
LAB_02b4a325:
      puVar5 = apuStack_78[0];
      if ((uStack_88 & 1) == 0) {
        puVar5 = (undefined1 *)((long)&uStack_88 + 1);
      }
      *puVar5 = *puVar2;
      uVar3 = (ulong)((byte)uStack_88 >> 1);
      uVar4 = uVar3;
      if ((uStack_88 & 1) != 0) {
        uVar4 = uStack_80;
      }
      if (uVar4 == 0) {
LAB_02b4a631:
                    /* WARNING: Does not return */
        pcVar1 = (code *)invalidInstructionException();
        (*pcVar1)();
      }
      bVar12 = (uStack_88 & 1) != 0;
      puVar5 = (undefined1 *)((long)&uStack_88 + 2);
      if (bVar12) {
        puVar5 = apuStack_78[0] + 1;
      }
      *puVar5 = puVar2[1];
      uVar4 = uStack_80;
      if (!bVar12) {
        uVar4 = uVar3;
      }
      if (uVar4 < 2) goto LAB_02b4a631;
      bVar12 = (uStack_88 & 1) != 0;
      puVar5 = (undefined1 *)((long)&uStack_88 + 3);
      if (bVar12) {
        puVar5 = apuStack_78[0] + 2;
      }
      *puVar5 = puVar2[2];
      uVar4 = uStack_80;
      if (!bVar12) {
        uVar4 = uVar3;
      }
      if (uVar4 < 3) goto LAB_02b4a631;
      bVar12 = (uStack_88 & 1) != 0;
      puVar5 = (undefined1 *)((long)&uStack_88 + 4);
      if (bVar12) {
        puVar5 = apuStack_78[0] + 3;
      }
      *puVar5 = puVar2[3];
      uVar4 = uStack_80;
      if (!bVar12) {
        uVar4 = uVar3;
      }
      if (uVar4 < 4) goto LAB_02b4a631;
      bVar12 = (uStack_88 & 1) != 0;
      puVar5 = (undefined1 *)((long)&uStack_88 + 5);
      if (bVar12) {
        puVar5 = apuStack_78[0] + 4;
      }
      *puVar5 = puVar2[4];
      uVar4 = uStack_80;
      if (!bVar12) {
        uVar4 = uVar3;
      }
      if (uVar4 < 5) goto LAB_02b4a631;
      bVar12 = (uStack_88 & 1) != 0;
      puVar5 = (undefined1 *)((long)&uStack_88 + 6);
      if (bVar12) {
        puVar5 = apuStack_78[0] + 5;
      }
      *puVar5 = puVar2[5];
      uVar4 = uStack_80;
      if (!bVar12) {
        uVar4 = uVar3;
      }
      if (uVar4 < 6) goto LAB_02b4a631;
      bVar12 = (uStack_88 & 1) != 0;
      puVar5 = (undefined1 *)((long)&uStack_88 + 7);
      if (bVar12) {
        puVar5 = apuStack_78[0] + 6;
      }
      *puVar5 = puVar2[6];
      uVar4 = uStack_80;
      if (!bVar12) {
        uVar4 = uVar3;
      }
      if (uVar4 < 7) goto LAB_02b4a631;
      bVar12 = (uStack_88 & 1) != 0;
      puVar11 = &uStack_80;
      if (bVar12) {
        puVar11 = (undefined8 *)(apuStack_78[0] + 7);
      }
      *(undefined1 *)puVar11 = puVar2[7];
      uVar4 = uStack_80;
      if (!bVar12) {
        uVar4 = uVar3;
      }
      if (uVar4 < 8) goto LAB_02b4a631;
      bVar12 = (uStack_88 & 1) != 0;
      puVar5 = (undefined1 *)((long)&uStack_80 + 1);
      if (bVar12) {
        puVar5 = apuStack_78[0] + 8;
      }
      *puVar5 = puVar2[8];
      uVar4 = uStack_80;
      if (!bVar12) {
        uVar4 = uVar3;
      }
      if (uVar4 < 9) goto LAB_02b4a631;
      bVar12 = (uStack_88 & 1) != 0;
      puVar5 = (undefined1 *)((long)&uStack_80 + 2);
      if (bVar12) {
        puVar5 = apuStack_78[0] + 9;
      }
      *puVar5 = puVar2[9];
      uVar4 = uStack_80;
      if (!bVar12) {
        uVar4 = uVar3;
      }
      if (uVar4 < 10) goto LAB_02b4a631;
      bVar12 = (uStack_88 & 1) != 0;
      puVar5 = (undefined1 *)((long)&uStack_80 + 3);
      if (bVar12) {
        puVar5 = apuStack_78[0] + 10;
      }
      *puVar5 = puVar2[10];
      uVar4 = uStack_80;
      if (!bVar12) {
        uVar4 = uVar3;
      }
      if (uVar4 < 0xb) goto LAB_02b4a631;
      bVar12 = (uStack_88 & 1) != 0;
      puVar5 = (undefined1 *)((long)&uStack_80 + 4);
      if (bVar12) {
        puVar5 = apuStack_78[0] + 0xb;
      }
      *puVar5 = puVar2[0xb];
      uVar4 = uStack_80;
      if (!bVar12) {
        uVar4 = uVar3;
      }
      if (uVar4 < 0xc) goto LAB_02b4a631;
      bVar12 = (uStack_88 & 1) != 0;
      puVar5 = (undefined1 *)((long)&uStack_80 + 5);
      if (bVar12) {
        puVar5 = apuStack_78[0] + 0xc;
      }
      *puVar5 = puVar2[0xc];
      uVar4 = uStack_80;
      if (!bVar12) {
        uVar4 = uVar3;
      }
      if (uVar4 < 0xd) goto LAB_02b4a631;
      bVar12 = (uStack_88 & 1) != 0;
      puVar5 = (undefined1 *)((long)&uStack_80 + 6);
      if (bVar12) {
        puVar5 = apuStack_78[0] + 0xd;
      }
      *puVar5 = puVar2[0xd];
      uVar4 = uStack_80;
      if (!bVar12) {
        uVar4 = uVar3;
      }
      if (uVar4 < 0xe) goto LAB_02b4a631;
      bVar12 = (uStack_88 & 1) != 0;
      puVar5 = (undefined1 *)((long)&uStack_80 + 7);
      if (bVar12) {
        puVar5 = apuStack_78[0] + 0xe;
      }
      *puVar5 = puVar2[0xe];
      if (bVar12) {
        uVar3 = uStack_80;
      }
      if (uVar3 < 0xf) goto LAB_02b4a631;
      ppuVar9 = (undefined1 **)(apuStack_78[0] + 0xf);
      if ((uStack_88 & 1) == 0) {
        ppuVar9 = apuStack_78;
      }
      *(undefined1 *)ppuVar9 = puVar2[0xf];
    }
    param_1[2] = uStack_58;
    *param_1 = uStack_68;
    param_1[1] = uStack_60;
    *(undefined4 *)(param_1 + 3) = (undefined4)uStack_88;
    *(undefined4 *)((long)param_1 + 0x1c) = uStack_88._4_4_;
    *(undefined4 *)(param_1 + 4) = (undefined4)uStack_80;
    *(undefined4 *)((long)param_1 + 0x24) = uStack_80._4_4_;
    param_1[5] = (ulong)apuStack_78[0];
    *(undefined1 *)(param_1 + 6) = 1;
    puVar2 = puStack_40;
  }
  puStack_40 = (undefined1 *)0x0;
  if (puVar2 != (undefined1 *)0x0) {
    puStack_40 = (undefined1 *)0x0;
    func_0x04a8cfd0();
  }
  lVar6 = lStack_48;
  lStack_48 = 0;
  if (lVar6 != 0) {
    func_0x04a8cfd0();
  }
  if ((uStack_38 & 1) == 0) {
    return param_1;
  }
LAB_02b4a0a4:
  func_0x04a8cfc8(lStack_28,CONCAT44(uStack_34,uStack_38) & 0xfffffffffffffffe);
  return param_1;
}


// FUNCTION 02b525e0 bounds 02b52c2f

ulong * FUN_02b525e0(ulong *param_1,undefined8 *param_2,byte *param_3,uint *param_4)

{
  code *pcVar1;
  undefined1 *puVar2;
  ulong uVar3;
  ulong uVar4;
  undefined1 *puVar5;
  long lVar6;
  ulong uVar7;
  byte *pbVar8;
  undefined1 **ppuVar9;
  ulong uVar10;
  undefined8 *puVar11;
  bool bVar12;
  undefined8 uStack_88;
  undefined8 uStack_80;
  undefined1 *apuStack_78 [2];
  undefined8 uStack_68;
  ulong uStack_60;
  ulong uStack_58;
  long lStack_48;
  undefined1 *puStack_40;
  undefined4 uStack_38;
  uint uStack_34;
  uint uStack_30;
  uint uStack_2c;
  long lStack_28;

  if ((*param_4 & 1) == 0) {
    lStack_28 = *(long *)(param_4 + 4);
    uStack_38 = *param_4;
    uStack_34 = param_4[1];
    uStack_30 = param_4[2];
    uStack_2c = param_4[3];
    uVar3 = (ulong)(byte)uStack_38;
    if ((uStack_38 & 1) != 0) goto LAB_02b52670;
LAB_02b52614:
    uVar4 = uVar3 >> 1;
  }
  else {
    func_0x019f86e0(&uStack_38,*(undefined8 *)(param_4 + 4),*(undefined8 *)(param_4 + 2));
    uVar3 = (ulong)(byte)uStack_38;
    if ((uStack_38 & 1) == 0) goto LAB_02b52614;
LAB_02b52670:
    uVar4 = CONCAT44(uStack_2c,uStack_30);
  }
  if (uVar4 != 0xc) {
    param_1[4] = 0;
    param_1[5] = 0;
    param_1[2] = 0;
    param_1[3] = 0;
    *param_1 = 0;
    param_1[1] = 0;
    *(undefined1 *)(param_1 + 6) = 0;
    if ((uVar3 & 1) == 0) {
      return param_1;
    }
    goto LAB_02b52634;
  }
  lVar6 = lStack_28;
  if ((uVar3 & 1) == 0) {
    lVar6 = (long)&uStack_38 + 1;
  }
  bVar12 = (*param_3 & 1) != 0;
  pbVar8 = param_3 + 1;
  if (bVar12) {
    pbVar8 = *(byte **)(param_3 + 0x10);
  }
  uVar3 = (ulong)(*param_3 >> 1);
  if (bVar12) {
    uVar3 = *(ulong *)(param_3 + 8);
  }
  (**(code **)(*(long *)*param_2 + 0x18))(&lStack_48,(long *)*param_2,pbVar8,lVar6,uVar3);
  lVar6 = lStack_48;
  if (lStack_48 == 0 || puStack_40 == (undefined1 *)0x0) {
    param_1[4] = 0;
    param_1[5] = 0;
    param_1[2] = 0;
    param_1[3] = 0;
    *param_1 = 0;
    param_1[1] = 0;
    *(undefined1 *)(param_1 + 6) = 0;
    puVar2 = puStack_40;
  }
  else {
    if ((*param_3 & 1) == 0) {
      uVar3 = (ulong)(*param_3 >> 1);
    }
    else {
      uVar3 = *(ulong *)(param_3 + 8);
    }
    uStack_68 = 0;
    uStack_60 = 0;
    uStack_58 = 0;
    if (uVar3 == 0) {
      uVar4 = 0;
    }
    else {
      func_0x04aac620(&uStack_68,uVar3,0);
      uVar4 = uStack_68 & 0xff;
    }
    uVar7 = uVar4 >> 1;
    if ((uVar4 & 1) != 0) {
      uVar7 = uStack_60;
    }
    if (uVar3 - 1 < uVar7) {
      uVar4 = (long)&uStack_68 + 1;
      if (uVar3 == 1) {
        uVar7 = 0;
      }
      else {
        uVar7 = 0;
        do {
          uVar10 = uStack_60;
          if ((uStack_68 & 1) == 0) {
            uVar10 = (ulong)((byte)uStack_68 >> 1);
          }
          if (uVar10 < uVar7) goto LAB_02b52bc1;
          uVar10 = uVar4;
          if ((uStack_68 & 1) != 0) {
            uVar10 = uStack_58;
          }
          *(undefined1 *)(uVar10 + uVar7) = *(undefined1 *)(lVar6 + uVar7);
          uVar10 = uStack_60;
          if ((uStack_68 & 1) == 0) {
            uVar10 = (ulong)((byte)uStack_68 >> 1);
          }
          if (uVar10 <= uVar7) goto LAB_02b52bc1;
          uVar10 = uVar4;
          if ((uStack_68 & 1) != 0) {
            uVar10 = uStack_58;
          }
          *(undefined1 *)(uVar10 + 1 + uVar7) = *(undefined1 *)(lVar6 + 1 + uVar7);
          uVar7 = uVar7 + 2;
        } while ((uVar3 & 0xfffffffffffffffe) != uVar7);
        if ((uVar3 & 1) == 0) goto LAB_02b5286b;
      }
      uVar3 = uStack_60;
      if ((uStack_68 & 1) == 0) {
        uVar3 = (ulong)((byte)uStack_68 >> 1);
      }
      if (uVar3 < uVar7) goto LAB_02b52bc1;
      if ((uStack_68 & 1) != 0) {
        uVar4 = uStack_58;
      }
      *(undefined1 *)(uVar4 + uVar7) = *(undefined1 *)(lVar6 + uVar7);
    }
LAB_02b5286b:
    puVar2 = puStack_40;
    uStack_88 = 0;
    uStack_80 = 0;
    apuStack_78[0] = (undefined1 *)0x0;
    func_0x04aac620(&uStack_88,0x10,0);
    if ((uStack_88 & 1) == 0) {
      if (0xf < (byte)uStack_88 >> 1) goto LAB_02b528b5;
    }
    else if (0xf < uStack_80) {
LAB_02b528b5:
      puVar5 = apuStack_78[0];
      if ((uStack_88 & 1) == 0) {
        puVar5 = (undefined1 *)((long)&uStack_88 + 1);
      }
      *puVar5 = *puVar2;
      uVar3 = (ulong)((byte)uStack_88 >> 1);
      uVar4 = uVar3;
      if ((uStack_88 & 1) != 0) {
        uVar4 = uStack_80;
      }
      if (uVar4 == 0) {
LAB_02b52bc1:
                    /* WARNING: Does not return */
        pcVar1 = (code *)invalidInstructionException();
        (*pcVar1)();
      }
      bVar12 = (uStack_88 & 1) != 0;
      puVar5 = (undefined1 *)((long)&uStack_88 + 2);
      if (bVar12) {
        puVar5 = apuStack_78[0] + 1;
      }
      *puVar5 = puVar2[1];
      uVar4 = uStack_80;
      if (!bVar12) {
        uVar4 = uVar3;
      }
      if (uVar4 < 2) goto LAB_02b52bc1;
      bVar12 = (uStack_88 & 1) != 0;
      puVar5 = (undefined1 *)((long)&uStack_88 + 3);
      if (bVar12) {
        puVar5 = apuStack_78[0] + 2;
      }
      *puVar5 = puVar2[2];
      uVar4 = uStack_80;
      if (!bVar12) {
        uVar4 = uVar3;
      }
      if (uVar4 < 3) goto LAB_02b52bc1;
      bVar12 = (uStack_88 & 1) != 0;
      puVar5 = (undefined1 *)((long)&uStack_88 + 4);
      if (bVar12) {
        puVar5 = apuStack_78[0] + 3;
      }
      *puVar5 = puVar2[3];
      uVar4 = uStack_80;
      if (!bVar12) {
        uVar4 = uVar3;
      }
      if (uVar4 < 4) goto LAB_02b52bc1;
      bVar12 = (uStack_88 & 1) != 0;
      puVar5 = (undefined1 *)((long)&uStack_88 + 5);
      if (bVar12) {
        puVar5 = apuStack_78[0] + 4;
      }
      *puVar5 = puVar2[4];
      uVar4 = uStack_80;
      if (!bVar12) {
        uVar4 = uVar3;
      }
      if (uVar4 < 5) goto LAB_02b52bc1;
      bVar12 = (uStack_88 & 1) != 0;
      puVar5 = (undefined1 *)((long)&uStack_88 + 6);
      if (bVar12) {
        puVar5 = apuStack_78[0] + 5;
      }
      *puVar5 = puVar2[5];
      uVar4 = uStack_80;
      if (!bVar12) {
        uVar4 = uVar3;
      }
      if (uVar4 < 6) goto LAB_02b52bc1;
      bVar12 = (uStack_88 & 1) != 0;
      puVar5 = (undefined1 *)((long)&uStack_88 + 7);
      if (bVar12) {
        puVar5 = apuStack_78[0] + 6;
      }
      *puVar5 = puVar2[6];
      uVar4 = uStack_80;
      if (!bVar12) {
        uVar4 = uVar3;
      }
      if (uVar4 < 7) goto LAB_02b52bc1;
      bVar12 = (uStack_88 & 1) != 0;
      puVar11 = &uStack_80;
      if (bVar12) {
        puVar11 = (undefined8 *)(apuStack_78[0] + 7);
      }
      *(undefined1 *)puVar11 = puVar2[7];
      uVar4 = uStack_80;
      if (!bVar12) {
        uVar4 = uVar3;
      }
      if (uVar4 < 8) goto LAB_02b52bc1;
      bVar12 = (uStack_88 & 1) != 0;
      puVar5 = (undefined1 *)((long)&uStack_80 + 1);
      if (bVar12) {
        puVar5 = apuStack_78[0] + 8;
      }
      *puVar5 = puVar2[8];
      uVar4 = uStack_80;
      if (!bVar12) {
        uVar4 = uVar3;
      }
      if (uVar4 < 9) goto LAB_02b52bc1;
      bVar12 = (uStack_88 & 1) != 0;
      puVar5 = (undefined1 *)((long)&uStack_80 + 2);
      if (bVar12) {
        puVar5 = apuStack_78[0] + 9;
      }
      *puVar5 = puVar2[9];
      uVar4 = uStack_80;
      if (!bVar12) {
        uVar4 = uVar3;
      }
      if (uVar4 < 10) goto LAB_02b52bc1;
      bVar12 = (uStack_88 & 1) != 0;
      puVar5 = (undefined1 *)((long)&uStack_80 + 3);
      if (bVar12) {
        puVar5 = apuStack_78[0] + 10;
      }
      *puVar5 = puVar2[10];
      uVar4 = uStack_80;
      if (!bVar12) {
        uVar4 = uVar3;
      }
      if (uVar4 < 0xb) goto LAB_02b52bc1;
      bVar12 = (uStack_88 & 1) != 0;
      puVar5 = (undefined1 *)((long)&uStack_80 + 4);
      if (bVar12) {
        puVar5 = apuStack_78[0] + 0xb;
      }
      *puVar5 = puVar2[0xb];
      uVar4 = uStack_80;
      if (!bVar12) {
        uVar4 = uVar3;
      }
      if (uVar4 < 0xc) goto LAB_02b52bc1;
      bVar12 = (uStack_88 & 1) != 0;
      puVar5 = (undefined1 *)((long)&uStack_80 + 5);
      if (bVar12) {
        puVar5 = apuStack_78[0] + 0xc;
      }
      *puVar5 = puVar2[0xc];
      uVar4 = uStack_80;
      if (!bVar12) {
        uVar4 = uVar3;
      }
      if (uVar4 < 0xd) goto LAB_02b52bc1;
      bVar12 = (uStack_88 & 1) != 0;
      puVar5 = (undefined1 *)((long)&uStack_80 + 6);
      if (bVar12) {
        puVar5 = apuStack_78[0] + 0xd;
      }
      *puVar5 = puVar2[0xd];
      uVar4 = uStack_80;
      if (!bVar12) {
        uVar4 = uVar3;
      }
      if (uVar4 < 0xe) goto LAB_02b52bc1;
      bVar12 = (uStack_88 & 1) != 0;
      puVar5 = (undefined1 *)((long)&uStack_80 + 7);
      if (bVar12) {
        puVar5 = apuStack_78[0] + 0xe;
      }
      *puVar5 = puVar2[0xe];
      if (bVar12) {
        uVar3 = uStack_80;
      }
      if (uVar3 < 0xf) goto LAB_02b52bc1;
      ppuVar9 = (undefined1 **)(apuStack_78[0] + 0xf);
      if ((uStack_88 & 1) == 0) {
        ppuVar9 = apuStack_78;
      }
      *(undefined1 *)ppuVar9 = puVar2[0xf];
    }
    param_1[2] = uStack_58;
    *param_1 = uStack_68;
    param_1[1] = uStack_60;
    *(undefined4 *)(param_1 + 3) = (undefined4)uStack_88;
    *(undefined4 *)((long)param_1 + 0x1c) = uStack_88._4_4_;
    *(undefined4 *)(param_1 + 4) = (undefined4)uStack_80;
    *(undefined4 *)((long)param_1 + 0x24) = uStack_80._4_4_;
    param_1[5] = (ulong)apuStack_78[0];
    *(undefined1 *)(param_1 + 6) = 1;
    puVar2 = puStack_40;
  }
  puStack_40 = (undefined1 *)0x0;
  if (puVar2 != (undefined1 *)0x0) {
    puStack_40 = (undefined1 *)0x0;
    func_0x04a8cfd0();
  }
  lVar6 = lStack_48;
  lStack_48 = 0;
  if (lVar6 != 0) {
    func_0x04a8cfd0();
  }
  if ((uStack_38 & 1) == 0) {
    return param_1;
  }
LAB_02b52634:
  func_0x04a8cfc8(lStack_28,CONCAT44(uStack_34,uStack_38) & 0xfffffffffffffffe);
  return param_1;
}

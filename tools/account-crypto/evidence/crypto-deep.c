// FUNCTION 048594a0 bounds 0485953f

undefined8
FUN_048594a0(undefined8 param_1,undefined8 param_2,undefined8 param_3,undefined8 param_4,
            undefined8 param_5,undefined4 param_6,undefined8 param_7)

{
  int iVar1;
  undefined8 uVar2;
  undefined1 auStack_48 [24];

  func_0x03d167a0(auStack_48);
  uVar2 = func_0x03d16780(8);
  iVar1 = func_0x03d168e0(auStack_48,uVar2,1);
  if ((iVar1 == 0) &&
     (iVar1 = func_0x03d1edb0(auStack_48,param_1,param_2,param_3,param_4,param_6,param_5,param_7),
     iVar1 == 0)) {
    func_0x03d167b0(auStack_48);
    return 1;
  }
  return 0;
}


// FUNCTION 02b81930 bounds 02b8194f

undefined8 FUN_02b81930(undefined8 param_1)

{
  FUN_02b83320();
  return param_1;
}


// FUNCTION 02b81950 bounds 02b8196f

undefined8 FUN_02b81950(undefined8 param_1)

{
  FUN_02b83320();
  return param_1;
}


// FUNCTION 02b831f0 bounds 02b8328f

undefined8 *
FUN_02b831f0(undefined8 *param_1,undefined8 param_2,undefined8 param_3,undefined8 param_4)

{
  long *plVar1;
  long *plStack_40;
  char cStack_38;
  undefined4 uStack_30;
  undefined4 uStack_2c;
  undefined4 uStack_28;
  undefined4 uStack_24;
  undefined8 uStack_20;

  func_0x02b83130(&plStack_40,param_2,param_4);
  if (cStack_38 == '\0') {
    *param_1 = 0;
    param_1[1] = 0;
    param_1[2] = 0;
    plVar1 = plStack_40;
  }
  else {
    func_0x02b82c00(&uStack_30,param_2,param_3,&plStack_40);
    param_1[2] = uStack_20;
    *(undefined4 *)param_1 = uStack_30;
    *(undefined4 *)((long)param_1 + 4) = uStack_2c;
    *(undefined4 *)(param_1 + 1) = uStack_28;
    *(undefined4 *)((long)param_1 + 0xc) = uStack_24;
    plVar1 = plStack_40;
  }
  if (plVar1 != (long *)0x0) {
    plStack_40 = (long *)0x0;
    (**(code **)(*plVar1 + 8))();
  }
  return param_1;
}


// FUNCTION 02b83290 bounds 02b8331f

undefined8 *
FUN_02b83290(undefined8 *param_1,undefined8 param_2,undefined8 param_3,undefined8 param_4)

{
  long *plVar1;
  long *plStack_38;
  char cStack_30;
  undefined4 uStack_28;
  undefined4 uStack_24;
  undefined4 uStack_20;
  undefined4 uStack_1c;
  undefined8 uStack_18;

  func_0x02b83130(&plStack_38,param_2,param_4);
  if (cStack_30 == '\0') {
    *param_1 = 0;
    param_1[1] = 0;
    param_1[2] = 0;
    plVar1 = plStack_38;
  }
  else {
    func_0x02b82e60(&uStack_28,param_2,param_3,&plStack_38);
    param_1[2] = uStack_18;
    *(undefined4 *)param_1 = uStack_28;
    *(undefined4 *)((long)param_1 + 4) = uStack_24;
    *(undefined4 *)(param_1 + 1) = uStack_20;
    *(undefined4 *)((long)param_1 + 0xc) = uStack_1c;
    plVar1 = plStack_38;
  }
  if (plVar1 != (long *)0x0) {
    plStack_38 = (long *)0x0;
    (**(code **)(*plVar1 + 8))();
  }
  return param_1;
}


// FUNCTION 02b830e0 bounds 02b8312f

undefined8 FUN_02b830e0(long param_1,byte *param_2)

{
  byte bVar1;
  undefined8 uVar2;

  bVar1 = *param_2;
  if ((bVar1 & 1) == 0) {
    if ((uint)(bVar1 >> 1) == *(uint *)(param_1 + 0x30)) goto LAB_02b83104;
  }
  else if (*(ulong *)(param_2 + 8) == (ulong)*(uint *)(param_1 + 0x30)) {
LAB_02b83104:
    if ((bVar1 & 1) == 0) {
      param_2 = param_2 + 1;
    }
    else {
      param_2 = *(byte **)(param_2 + 0x10);
    }
    uVar2 = (**(code **)(**(long **)(param_1 + 0x20) + 0x10))(*(long **)(param_1 + 0x20),param_2);
    return uVar2;
  }
  return 0;
}


// FUNCTION 02b82bf0 bounds 02b82bff

undefined8 * FUN_02b82bf0(undefined8 *param_1,long *param_2,byte *param_3)

{
  byte bVar1;
  code *pcVar2;
  undefined4 uVar3;
  undefined4 uVar4;
  undefined4 uVar5;
  undefined1 *puVar6;
  ulong uVar7;
  undefined8 *puVar8;
  long *plVar9;
  ulong uVar10;
  uint uVar11;
  byte *extraout_RDX;
  undefined8 *unaff_RBX;
  undefined1 *puVar12;
  undefined8 unaff_RBP;
  ulong uVar13;
  undefined1 *puVar14;
  undefined1 *puVar15;
  undefined1 *unaff_R12;
  undefined1 *puVar16;
  bool bVar17;
  ulong unaff_R13;
  ulong unaff_R14;
  ulong unaff_R15;

  plVar9 = param_2 + 4;
  puVar12 = (undefined1 *)register0x00000020;
  do {
    *(undefined8 *)(puVar12 + -8) = unaff_RBP;
    *(ulong *)(puVar12 + -0x10) = unaff_R15;
    *(ulong *)(puVar12 + -0x18) = unaff_R14;
    *(ulong *)(puVar12 + -0x20) = unaff_R13;
    *(undefined1 **)(puVar12 + -0x28) = unaff_R12;
    *(undefined8 **)(puVar12 + -0x30) = unaff_RBX;
    if ((*param_3 & 1) == 0) {
      if (*param_3 >> 1 == 0) goto code_r0x02b82c79;
    }
    else if (*(long *)(param_3 + 8) == 0) {
code_r0x02b82c79:
      *param_1 = 0;
      param_1[1] = 0;
      param_1[2] = 0;
      return param_1;
    }
    unaff_R12 = puVar12 + -0x68;
    pcVar2 = *(code **)(*param_2 + 0x30);
    *(undefined **)(puVar12 + -0xa0) = &UNK_02b82c3a;
    (*pcVar2)(unaff_R12);
    *(undefined **)(puVar12 + -0xa0) = &UNK_02b82c4b;
    puVar6 = (undefined1 *)FUN_02b83440(puVar12 + -0x98,plVar9,param_3,unaff_R12);
    if (puVar12[-0x80] == '\0') {
      *param_1 = 0;
      param_1[1] = 0;
      param_1[2] = 0;
      bVar1 = puVar12[-0x98];
      goto joined_r0x02b82c9e;
    }
    bVar1 = puVar12[-0x68];
    uVar11 = (uint)bVar1;
    if ((bVar1 & 1) == 0) {
      unaff_R14 = (ulong)(bVar1 >> 1);
      uVar10 = (ulong)(byte)puVar12[-0x98];
      if ((puVar12[-0x98] & 1) == 0) goto code_r0x02b82c6a;
code_r0x02b82cb8:
      unaff_R15 = *(ulong *)(puVar12 + -0x90);
    }
    else {
      unaff_R14 = *(ulong *)(puVar12 + -0x60);
      uVar10 = (ulong)(byte)puVar12[-0x98];
      if ((puVar12[-0x98] & 1) != 0) goto code_r0x02b82cb8;
code_r0x02b82c6a:
      unaff_R15 = uVar10 >> 1;
    }
    unaff_R13 = unaff_R15 + unaff_R14;
    if (unaff_R13 < 0xfffffffffffffff7) {
      puVar14 = *(undefined1 **)(puVar12 + -0x58);
      puVar15 = *(undefined1 **)(puVar12 + -0x88);
      *(undefined1 **)(puVar12 + -0x70) = puVar15;
      if (unaff_R13 < 0x17) {
        bVar17 = false;
        puVar12[-0x50] = (char)unaff_R13 * '\x02';
      }
      else {
        *(undefined1 **)(puVar12 + -0x38) = puVar14;
        *(uint *)(puVar12 + -0x74) = (uint)bVar1;
        uVar7 = (unaff_R13 & 0xfffffffffffffff8) + 8;
        uVar13 = 0x1a;
        if (uVar7 != 0x18) {
          uVar13 = uVar7;
        }
        *(undefined **)(puVar12 + -0xa0) = &UNK_02b82d16;
        puVar6 = (undefined1 *)func_0x04b078f0(uVar13);
        *(ulong *)(puVar12 + -0x50) = uVar13 | 1;
        *(ulong *)(puVar12 + -0x48) = unaff_R13;
        *(undefined1 **)(puVar12 + -0x40) = puVar6;
        bVar17 = true;
        uVar11 = *(uint *)(puVar12 + -0x74);
        puVar15 = *(undefined1 **)(puVar12 + -0x70);
        puVar14 = *(undefined1 **)(puVar12 + -0x38);
      }
      puVar16 = puVar12 + -0x97;
      if ((uVar10 & 1) != 0) {
        puVar16 = puVar15;
      }
      puVar15 = puVar12 + -0x67;
      if ((uVar11 & 1) != 0) {
        puVar15 = puVar14;
      }
      puVar14 = puVar12 + -0x4f;
      if (bVar17) {
        puVar14 = puVar6;
      }
      *(undefined **)(puVar12 + -0xa0) = &UNK_02b82d70;
      func_0x04b07a30(puVar14,puVar15,unaff_R14);
      *(undefined **)(puVar12 + -0xa0) = &UNK_02b82d81;
      func_0x04b07a30(puVar14 + unaff_R14,puVar16,unaff_R15);
      (puVar14 + unaff_R14)[unaff_R15] = 0;
      if ((uVar10 & 1) != 0) {
        *(undefined **)(puVar12 + -0xa0) = &UNK_02b82d9e;
        func_0x04a8cfc8(*(undefined8 *)(puVar12 + -0x70),
                        *(ulong *)(puVar12 + -0x98) & 0xfffffffffffffffe);
      }
      *(undefined8 *)(puVar12 + -0x88) = *(undefined8 *)(puVar12 + -0x40);
      *(undefined4 *)(puVar12 + -0x98) = *(undefined4 *)(puVar12 + -0x50);
      *(undefined4 *)(puVar12 + -0x94) = *(undefined4 *)(puVar12 + -0x4c);
      *(undefined4 *)(puVar12 + -0x90) = *(undefined4 *)(puVar12 + -0x48);
      *(undefined4 *)(puVar12 + -0x8c) = *(undefined4 *)(puVar12 + -0x44);
      if ((puVar12[-0x98] & 1) == 0) {
        param_1[2] = *(undefined8 *)(puVar12 + -0x88);
        uVar3 = *(undefined4 *)(puVar12 + -0x94);
        uVar4 = *(undefined4 *)(puVar12 + -0x90);
        uVar5 = *(undefined4 *)(puVar12 + -0x8c);
        *(undefined4 *)param_1 = *(undefined4 *)(puVar12 + -0x98);
        *(undefined4 *)((long)param_1 + 4) = uVar3;
        *(undefined4 *)(param_1 + 1) = uVar4;
        *(undefined4 *)((long)param_1 + 0xc) = uVar5;
        bVar1 = puVar12[-0x98];
      }
      else {
        *(undefined **)(puVar12 + -0xa0) = &UNK_02b82dc9;
        func_0x019f86e0(param_1,*(undefined8 *)(puVar12 + -0x88),*(undefined8 *)(puVar12 + -0x90));
        bVar1 = puVar12[-0x98];
      }
joined_r0x02b82c9e:
      if ((bVar1 & 1) != 0) {
        *(undefined **)(puVar12 + -0xa0) = &UNK_02b82de1;
        func_0x04a8cfc8(*(undefined8 *)(puVar12 + -0x88),
                        *(ulong *)(puVar12 + -0x98) & 0xfffffffffffffffe);
      }
      if ((puVar12[-0x68] & 1) != 0) {
        *(undefined **)(puVar12 + -0xa0) = &UNK_02b82dfb;
        func_0x04a8cfc8(*(undefined8 *)(puVar12 + -0x58),
                        *(ulong *)(puVar12 + -0x68) & 0xfffffffffffffffe);
      }
      return param_1;
    }
    *(undefined **)(puVar12 + -0xa0) = &UNK_02b82e2a;
    puVar8 = (undefined8 *)func_0x019f8660();
    *(undefined **)(puVar12 + -0xa0) = &UNK_02b82e32;
    param_2 = plVar9;
    func_0x019f83c0();
    plVar9 = param_2 + 5;
    param_3 = extraout_RDX;
    unaff_RBX = param_1;
    puVar12 = puVar12 + -0x98;
    param_1 = puVar8;
  } while( true );
}


// FUNCTION 02b82e50 bounds 02b82e5f

/* WARNING: Type propagation algorithm not settling */

undefined8 * FUN_02b82e50(undefined8 *param_1,long param_2,byte *param_3)

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
  undefined1 *puVar9;
  undefined8 unaff_RBP;
  ulong uVar10;
  byte *pbVar11;
  ulong unaff_R12;
  ulong uVar12;
  ulong unaff_R13;
  byte *unaff_R14;
  byte *unaff_R15;

  pbVar11 = (byte *)(param_2 + 0x20);
  puVar9 = (undefined1 *)register0x00000020;
  while( true ) {
    pbVar8 = pbVar11;
    *(undefined8 *)(puVar9 + -8) = unaff_RBP;
    *(byte **)(puVar9 + -0x10) = unaff_R15;
    *(byte **)(puVar9 + -0x18) = unaff_R14;
    *(ulong *)(puVar9 + -0x20) = unaff_R13;
    *(ulong *)(puVar9 + -0x28) = unaff_R12;
    *(undefined8 **)(puVar9 + -0x30) = unaff_RBX;
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
    puVar9[-0x48] = (char)unaff_R13 * '\x02';
    *(undefined **)(puVar9 + -0x90) = &UNK_02b82edc;
    pbVar11 = unaff_R15;
    func_0x04b07a30(puVar9 + -0x47,unaff_R15,unaff_R13);
    puVar9[unaff_R13 - 0x47] = 0;
    if (0xf < unaff_R12) break;
    *(undefined **)(puVar9 + -0x90) = &UNK_02b83027;
    puVar7 = (undefined8 *)func_0x01a1b050();
    *(undefined **)(puVar9 + -0x90) = &UNK_02b8302f;
    func_0x019f83c0();
    pbVar11 = pbVar11 + 0x28;
    param_3 = extraout_RDX;
    unaff_RBX = param_1;
    puVar9 = puVar9 + -0x88;
    param_1 = puVar7;
    unaff_R14 = pbVar8;
  }
  uVar12 = unaff_R12 - 0x10;
  if (uVar12 < 0x17) {
    puVar9[-0x80] = (char)uVar12 * '\x02';
    puVar6 = puVar9 + -0x7f;
  }
  else {
    uVar5 = (uVar12 & 0xfffffffffffffff8) + 8;
    uVar10 = 0x1a;
    if (uVar5 != 0x18) {
      uVar10 = uVar5;
    }
    *(undefined **)(puVar9 + -0x90) = &UNK_02b82f25;
    puVar6 = (undefined1 *)func_0x04b078f0(uVar10);
    *(ulong *)(puVar9 + -0x80) = uVar10 | 1;
    *(ulong *)(puVar9 + -0x78) = uVar12;
    *(undefined1 **)(puVar9 + -0x70) = puVar6;
  }
  *(undefined **)(puVar9 + -0x90) = &UNK_02b82f4d;
  func_0x04b07a30(puVar6,unaff_R15 + 0x10,uVar12);
  puVar6[uVar12] = 0;
  *(undefined **)(puVar9 + -0x90) = &UNK_02b82f6a;
  FUN_02b83440(puVar9 + -0x68,pbVar8,puVar9 + -0x80,puVar9 + -0x48);
  if (puVar9[-0x50] == '\0') {
    *param_1 = 0;
    param_1[1] = 0;
    param_1[2] = 0;
  }
  else {
    if ((puVar9[-0x68] & 1) == 0) {
      param_1[2] = *(undefined8 *)(puVar9 + -0x58);
      uVar2 = *(undefined4 *)(puVar9 + -100);
      uVar3 = *(undefined4 *)(puVar9 + -0x60);
      uVar4 = *(undefined4 *)(puVar9 + -0x5c);
      *(undefined4 *)param_1 = *(undefined4 *)(puVar9 + -0x68);
      *(undefined4 *)((long)param_1 + 4) = uVar2;
      *(undefined4 *)(param_1 + 1) = uVar3;
      *(undefined4 *)((long)param_1 + 0xc) = uVar4;
      bVar1 = puVar9[-0x68];
      goto joined_r0x02b82f9f;
    }
    *(undefined **)(puVar9 + -0x90) = &UNK_02b82f8a;
    func_0x019f86e0(param_1,*(undefined8 *)(puVar9 + -0x58),*(undefined8 *)(puVar9 + -0x60));
  }
  bVar1 = puVar9[-0x68];
joined_r0x02b82f9f:
  if ((bVar1 & 1) == 0) {
    bVar1 = puVar9[-0x80];
  }
  else {
    *(undefined **)(puVar9 + -0x90) = &UNK_02b82fff;
    func_0x04a8cfc8(*(undefined8 *)(puVar9 + -0x58),*(ulong *)(puVar9 + -0x68) & 0xfffffffffffffffe)
    ;
    bVar1 = puVar9[-0x80];
  }
  if ((bVar1 & 1) == 0) {
    bVar1 = puVar9[-0x48];
  }
  else {
    *(undefined **)(puVar9 + -0x90) = &UNK_02b83019;
    func_0x04a8cfc8(*(undefined8 *)(puVar9 + -0x70),*(ulong *)(puVar9 + -0x80) & 0xfffffffffffffffe)
    ;
    bVar1 = puVar9[-0x48];
  }
  if ((bVar1 & 1) != 0) {
    *(undefined **)(puVar9 + -0x90) = &UNK_02b82fc2;
    func_0x04a8cfc8(*(undefined8 *)(puVar9 + -0x38),*(ulong *)(puVar9 + -0x48) & 0xfffffffffffffffe)
    ;
  }
  return param_1;
}


// FUNCTION 02b220b0 bounds 02b2389f
Exception while decompiling 02b220b0: process: timeout

// FUNCTION 02b27dd0 bounds 02b2822f

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

int FUN_02b27dd0(long param_1,long param_2,long *param_3)

{
  code *pcVar1;
  char cVar2;
  int iVar3;
  int iVar4;
  undefined8 uVar5;
  long lVar6;
  ulong uVar7;
  long lVar8;
  long *plVar9;
  long *plVar10;
  long unaff_R12;
  ulong uVar11;
  ulong uVar12;
  ulong uStack_230;
  long lStack_228;
  undefined8 uStack_220;
  undefined8 uStack_210;
  undefined8 uStack_208;
  long *plStack_200;
  long *plStack_1f8;
  undefined4 uStack_1f0;
  undefined4 uStack_1ec;
  undefined4 uStack_1e8;
  undefined4 uStack_1e4;
  undefined4 uStack_1e0;
  undefined4 uStack_1dc;
  undefined4 uStack_1d8;
  undefined4 uStack_1d4;
  long *plStack_1d0;
  long *plStack_1c8;
  long *plStack_1b8;
  long lStack_1b0;
  long lStack_1a8;
  ulong uStack_1a0;
  undefined8 uStack_198;
  ulong uStack_190;
  long lStack_188;
  byte bStack_178;
  undefined1 uStack_177;
  undefined6 uStack_176;
  long *plStack_170;
  long *plStack_168;
  undefined8 uStack_160;
  ulong uStack_158;
  long lStack_150;
  undefined8 uStack_148;
  long *plStack_140;
  long lStack_138;
  undefined *puStack_130;
  undefined1 auStack_128 [8];
  long lStack_120;
  ulong uStack_110;
  long lStack_108;
  ulong uStack_100;
  byte bStack_f0;
  undefined7 uStack_ef;
  undefined8 uStack_e0;
  ulong uStack_d8;
  uint uStack_d0;
  undefined1 auStack_c8 [152];

  plVar10 = &uStack_198;
  if (*(char *)(param_2 + 8) == '\0') {
    uStack_1a0 = 0x2b27dfb;
    func_0x01ab8040(&uStack_148);
    uStack_1a0 = 0x2b27e11;
    uVar5 = func_0x01a53960(&lStack_138,&UNK_006d848b,100);
    uStack_1a0 = 0x2b27e1e;
    uVar5 = func_0x04aba9e0(uVar5,0x5cd);
    uStack_1a0 = 0x2b27e32;
    uVar5 = func_0x01a53960(uVar5,&UNK_006b7c79,2);
    uStack_1a0 = 0x2b27e43;
    func_0x01a53960(uVar5,&UNK_005a82fc,0);
    if ((uStack_d0 & 0x10) == 0) {
      uVar12 = 0;
      lVar6 = lStack_120;
      lVar8 = 0;
      if ((uStack_d0 & 8) != 0) goto LAB_02b27e91;
LAB_02b27ea0:
      lVar6 = lVar8;
      uStack_198 = CONCAT71(uStack_198._1_7_,(char)uVar12 * '\x02');
      unaff_R12 = (long)&uStack_198 + 1;
    }
    else {
      lVar6 = lStack_108;
      uStack_110 = uStack_d8;
      if (uStack_d8 < uStack_100) {
        uStack_d8 = uStack_100;
        uStack_110 = uStack_100;
      }
LAB_02b27e91:
      uVar12 = uStack_110 - lVar6;
      if ((long)uVar12 < 0) {
                    /* WARNING: Does not return */
        pcVar1 = (code *)invalidInstructionException();
        (*pcVar1)();
      }
      lVar8 = lVar6;
      if (uVar12 < 0x17) goto LAB_02b27ea0;
      uVar7 = (uVar12 & 0x7ffffffffffffff8) + 8;
      uVar11 = 0x1a;
      if (uVar7 != 0x18) {
        uVar11 = uVar7;
      }
      uStack_1a0 = 0x2b27ed8;
      unaff_R12 = func_0x04b078f0(uVar11);
      uStack_198 = uVar11 | 1;
      uStack_190 = uVar12;
      lStack_188 = unaff_R12;
    }
    uStack_1a0 = 0x2b27efb;
    func_0x04b07a30(unaff_R12,lVar6,uVar12);
    *(undefined1 *)(unaff_R12 + uVar12) = 0;
    bStack_178 = 0;
    uStack_177 = 0;
    uStack_1a0 = 0x2b27f18;
    func_0x02acb490(&uStack_198,0,&bStack_178,0);
    if ((bStack_178 & 1) != 0) {
      uStack_1a0 = 0x2b27f32;
      func_0x04a8cfc8(plStack_168,
                      CONCAT62(uStack_176,CONCAT11(uStack_177,bStack_178)) & 0xfffffffffffffffe);
    }
    if ((uStack_198 & 1) != 0) {
      uStack_1a0 = 0x2b27f4a;
      func_0x04a8cfc8(lStack_188,uStack_198 & 0xfffffffffffffffe);
    }
    uStack_148 = _UNK_04c9f9f0;
    *(undefined8 *)((long)&uStack_148 + *(long *)(_UNK_04c9f9f0 + -0x18)) = _UNK_04c9fa30;
    lStack_138 = _UNK_04c9fa38;
    puStack_130 = &UNK_04c9f668;
    if ((bStack_f0 & 1) != 0) {
      uStack_1a0 = 0x2b27fa2;
      func_0x04a8cfc8(uStack_e0,CONCAT71(uStack_ef,bStack_f0) & 0xfffffffffffffffe);
    }
    puStack_130 = &UNK_04c9f338;
    uStack_1a0 = 0x2b27fbc;
    func_0x04aea9f0(auStack_128);
    uStack_1a0 = 0x2b27fcd;
    func_0x04abbce8(&uStack_148,&UNK_04c9f9f8);
    uStack_1a0 = 0x2b27fda;
    func_0x04ab7708(auStack_c8);
  }
  uStack_148 = 0;
  plStack_140 = (long *)0x0;
  lStack_138 = 0;
  uStack_198 = 0;
  uStack_190 = 0;
  lStack_188 = 0;
  uStack_1a0 = 0x2b28009;
  iVar3 = func_0x02b711d0(*(undefined8 *)(param_1 + 0x1a8),&uStack_148);
  if (iVar3 != 0) goto LAB_02b281ca;
  uStack_1a0 = 0x2b28022;
  iVar3 = func_0x02b710f0(*(undefined8 *)(param_1 + 0x1a8));
  if (iVar3 != 0) goto LAB_02b281ca;
  uStack_1a0 = 0x2b28035;
  lVar6 = (**(code **)(*param_3 + 0x10))(param_3);
  uStack_1a0 = 0x2b28041;
  uVar12 = (**(code **)(*param_3 + 0x18))(param_3);
  if (uVar12 < 0xfffffffffffffff7) {
    if (uVar12 < 0x17) {
      uStack_160 = CONCAT71(uStack_160._1_7_,(char)uVar12 * '\x02');
      unaff_R12 = (long)&uStack_160 + 1;
    }
    else {
      uVar7 = (uVar12 & 0xfffffffffffffff8) + 8;
      uVar11 = 0x1a;
      if (uVar7 != 0x18) {
        uVar11 = uVar7;
      }
      uStack_1a0 = 0x2b28086;
      unaff_R12 = func_0x04b078f0(uVar11);
      uStack_160 = uVar11 | 1;
      uStack_158 = uVar12;
      lStack_150 = unaff_R12;
    }
    uStack_1a0 = 0x2b280aa;
    func_0x04b07a30(unaff_R12,lVar6,uVar12);
    *(undefined1 *)(unaff_R12 + uVar12) = 0;
    uStack_1a0 = 0x2b280c1;
    FUN_02b7fc70(&bStack_178,&uStack_160,&uStack_198);
    if ((uStack_160 & 1) != 0) {
      uStack_1a0 = 0x2b280db;
      func_0x04a8cfc8(lStack_150,uStack_160 & 0xfffffffffffffffe);
    }
    if ((uStack_148 & 1) == 0) {
      plVar9 = (long *)(uStack_148 >> 1 & 0x7f);
      if ((bStack_178 & 1) != 0) goto LAB_02b28111;
LAB_02b280f3:
      plVar10 = (long *)(ulong)(bStack_178 >> 1);
      if (plVar9 != plVar10) goto LAB_02b2814f;
LAB_02b28120:
      lVar6 = (long)&uStack_148 + 1;
      if ((uStack_148 & 1) != 0) {
        lVar6 = lStack_138;
      }
      if ((bStack_178 & 1) == 0) {
        plVar10 = (long *)&uStack_177;
        uStack_1a0 = 0x2b2814b;
        iVar3 = func_0x04b07bb0(lVar6);
      }
      else {
        uStack_1a0 = 0x2b2813b;
        plVar10 = plStack_168;
        iVar3 = func_0x04b07bb0(lVar6);
      }
      if (iVar3 != 0) goto LAB_02b2814f;
LAB_02b281ae:
      iVar3 = 0;
    }
    else {
      plVar9 = plStack_140;
      if ((bStack_178 & 1) == 0) goto LAB_02b280f3;
LAB_02b28111:
      plVar10 = plStack_170;
      if (plVar9 == plStack_170) goto LAB_02b28120;
LAB_02b2814f:
      lVar6 = (long)&uStack_148 + 1;
      uStack_1a0 = 0x2b28158;
      uVar12 = (**(code **)(*param_3 + 0x10))(param_3);
      uStack_1a0 = 0x2b28164;
      param_3 = (long *)(**(code **)(*param_3 + 0x18))(param_3);
      if (param_3 == (long *)0xffffffffffffffff) goto LAB_02b28217;
      plVar10 = (long *)(uStack_148 >> 1 & 0x7f);
      if ((uStack_148 & 1) != 0) {
        lVar6 = lStack_138;
        plVar10 = plStack_140;
      }
      plVar9 = plVar10;
      if (param_3 < plVar10) {
        plVar9 = param_3;
      }
      uStack_1a0 = 0x2b281a0;
      iVar4 = func_0x04b07bb0(lVar6,uVar12,plVar9);
      iVar3 = 7;
      if ((iVar4 == 0) && (plVar10 == param_3)) goto LAB_02b281ae;
    }
    if ((bStack_178 & 1) != 0) {
      uStack_1a0 = 0x2b281ca;
      func_0x04a8cfc8(plStack_168,
                      CONCAT62(uStack_176,CONCAT11(uStack_177,bStack_178)) & 0xfffffffffffffffe);
    }
LAB_02b281ca:
    if ((uStack_198 & 1) != 0) {
      uStack_1a0 = 0x2b281e2;
      func_0x04a8cfc8(lStack_188,uStack_198 & 0xfffffffffffffffe);
    }
    if ((uStack_148 & 1) != 0) {
      uStack_1a0 = 0x2b281fc;
      func_0x04a8cfc8(lStack_138,uStack_148 & 0xfffffffffffffffe);
    }
    return iVar3;
  }
  uStack_1a0 = 0x2b28217;
  func_0x019f8660();
LAB_02b28217:
  uStack_1a0 = 0x2b2821c;
  lVar8 = func_0x01a1b050();
  uStack_1a0 = 0x2b28224;
  func_0x019f83c0();
  if ((*(byte *)(lVar8 + 0x1b8) & 1) == 0) {
    if (*(byte *)(lVar8 + 0x1b8) >> 1 != 0) {
      return 8;
    }
  }
  else if (*(long *)(lVar8 + 0x1c0) != 0) {
    return 8;
  }
  uStack_230 = 0;
  lStack_228 = 0;
  uStack_220 = 0;
  plStack_1b8 = param_3;
  lStack_1b0 = unaff_R12;
  lStack_1a8 = lVar6;
  uStack_1a0 = uVar12;
  cVar2 = func_0x02b80c30(*(undefined8 *)(lVar8 + 0x10),lVar8 + 0x1b8,&uStack_230);
  iVar3 = 1;
  if (cVar2 != '\0') {
    if ((*(byte *)(lVar8 + 0x1b8) & 1) == 0) {
      if (*(byte *)(lVar8 + 0x1b8) >> 1 == 0) goto code_r0x02b284ed;
    }
    else if (*(long *)(lVar8 + 0x1c0) == 0) goto code_r0x02b284ed;
    if ((uStack_230 & 1) == 0) {
      if ((byte)uStack_230 >> 1 == 0) goto code_r0x02b284ed;
    }
    else if (lStack_228 == 0) goto code_r0x02b284ed;
    plVar9 = (long *)func_0x04b078f0(0x70);
    plVar9[1] = 0;
    plVar9[2] = 0;
    *plVar9 = (long)&UNK_04b760e0;
    func_0x04774750(plVar9 + 3,0);
    *(byte *)(plVar9 + 5) = *(byte *)(plVar9 + 5) | 4;
    uVar12 = plVar9[4];
    if ((uVar12 & 1) != 0) {
      uVar12 = *(ulong *)(uVar12 & 0xfffffffffffffffe);
    }
    plStack_200 = plVar9 + 3;
    plStack_1f8 = plVar9;
    func_0x042dca90(plVar9 + 8,&uStack_230,uVar12);
    *(undefined1 *)(plVar9 + 0xd) = 1;
    *(byte *)(plVar9 + 5) = *(byte *)(plVar9 + 5) | 0x80;
    uStack_208 = (long *)func_0x04b078f0(0x68);
    uStack_208[1] = 0;
    uStack_208[2] = 0;
    *uStack_208 = (long)&UNK_04b75090;
    uStack_210 = uStack_208 + 3;
    uStack_208[9] = 0;
    uStack_208[10] = 0;
    uStack_208[3] = 0;
    uStack_208[4] = 0;
    uStack_208[5] = 0;
    uStack_208[6] = 0;
    uStack_208[7] = 0;
    uStack_208[8] = 0;
    *(undefined4 *)(uStack_208 + 10) = 0x3f800000;
    uStack_208[0xb] = 0;
    uStack_208[0xc] = 0;
    uStack_1f0 = (undefined4)*plVar10;
    uStack_1ec = *(undefined4 *)((long)plVar10 + 4);
    uStack_1e8 = (undefined4)plVar10[1];
    uStack_1e4 = *(undefined4 *)((long)plVar10 + 0xc);
    if (plVar10[1] != 0) {
      LOCK();
      plVar10 = (long *)(plVar10[1] + 8);
      *plVar10 = *plVar10 + 1;
      UNLOCK();
    }
    func_0x02b1bbb0(uStack_210,&uStack_1f0);
    plVar10 = (long *)CONCAT44(uStack_1e4,uStack_1e8);
    if (plVar10 != (long *)0x0) {
      LOCK();
      plVar9 = plVar10 + 1;
      lVar6 = *plVar9;
      *plVar9 = *plVar9 + -1;
      UNLOCK();
      if (lVar6 == 0) {
        (**(code **)(*plVar10 + 0x10))(plVar10);
        func_0x04aa8780(plVar10);
      }
    }
    plStack_1d0 = plStack_200;
    plStack_1c8 = plStack_1f8;
    if (plStack_1f8 != (long *)0x0) {
      LOCK();
      plStack_1f8[1] = plStack_1f8[1] + 1;
      UNLOCK();
    }
    uStack_1e0 = (undefined4)uStack_210;
    uStack_1dc = uStack_210._4_4_;
    uStack_1d8 = (undefined4)uStack_208;
    uStack_1d4 = uStack_208._4_4_;
    if (uStack_208 != (long *)0x0) {
      LOCK();
      uStack_208[1] = uStack_208[1] + 1;
      UNLOCK();
    }
    func_0x02b2b520(lVar8,&plStack_1d0,lVar8 + 0x360,&uStack_1e0);
    plVar10 = (long *)CONCAT44(uStack_1d4,uStack_1d8);
    if (plVar10 != (long *)0x0) {
      LOCK();
      plVar9 = plVar10 + 1;
      lVar6 = *plVar9;
      *plVar9 = *plVar9 + -1;
      UNLOCK();
      if (lVar6 == 0) {
        (**(code **)(*plVar10 + 0x10))(plVar10);
        func_0x04aa8780(plVar10);
      }
    }
    plVar10 = plStack_1c8;
    if (plStack_1c8 != (long *)0x0) {
      LOCK();
      plVar9 = plStack_1c8 + 1;
      lVar6 = *plVar9;
      *plVar9 = *plVar9 + -1;
      UNLOCK();
      if (lVar6 == 0) {
        (**(code **)(*plStack_1c8 + 0x10))(plStack_1c8);
        func_0x04aa8780(plVar10);
      }
    }
    plVar10 = uStack_208;
    if (uStack_208 != (long *)0x0) {
      LOCK();
      plVar9 = uStack_208 + 1;
      lVar6 = *plVar9;
      *plVar9 = *plVar9 + -1;
      UNLOCK();
      if (lVar6 == 0) {
        (**(code **)(*uStack_208 + 0x10))(uStack_208);
        func_0x04aa8780(plVar10);
      }
    }
    plVar10 = plStack_1f8;
    if (plStack_1f8 != (long *)0x0) {
      LOCK();
      plVar9 = plStack_1f8 + 1;
      lVar6 = *plVar9;
      *plVar9 = *plVar9 + -1;
      UNLOCK();
      if (lVar6 == 0) {
        (**(code **)(*plStack_1f8 + 0x10))(plStack_1f8);
        func_0x04aa8780(plVar10);
      }
    }
    iVar3 = 0;
  }
code_r0x02b284ed:
  if ((uStack_230 & 1) != 0) {
    func_0x04a8cfc8(uStack_220,uStack_230 & 0xfffffffffffffffe);
  }
  return iVar3;
}


// FUNCTION 026421e0 bounds 026422df

byte * FUN_026421e0(byte *param_1,byte *param_2)

{
  byte bVar1;
  long lVar2;
  ulong uVar3;
  ulong uVar4;
  byte *pbVar5;
  byte *pbVar6;
  bool bVar7;

  param_1[0] = 0;
  param_1[1] = 0;
  param_1[2] = 0;
  param_1[3] = 0;
  param_1[4] = 0;
  param_1[5] = 0;
  param_1[6] = 0;
  param_1[7] = 0;
  param_1[8] = 0;
  param_1[9] = 0;
  param_1[10] = 0;
  param_1[0xb] = 0;
  param_1[0xc] = 0;
  param_1[0xd] = 0;
  param_1[0xe] = 0;
  param_1[0xf] = 0;
  param_1[0x10] = 0;
  param_1[0x11] = 0;
  param_1[0x12] = 0;
  param_1[0x13] = 0;
  param_1[0x14] = 0;
  param_1[0x15] = 0;
  param_1[0x16] = 0;
  param_1[0x17] = 0;
  if ((*param_2 & 1) == 0) {
    uVar3 = (ulong)(*param_2 >> 1);
  }
  else {
    uVar3 = *(ulong *)(param_2 + 8);
  }
  lVar2 = func_0x04858b30(uVar3);
  if (lVar2 == 0) {
    param_1[0] = 0;
    param_1[1] = 0;
  }
  else {
    func_0x04aac620(param_1,lVar2,0);
  }
  bVar7 = (*param_2 & 1) != 0;
  pbVar6 = param_2 + 1;
  if (bVar7) {
    pbVar6 = *(byte **)(param_2 + 0x10);
  }
  uVar3 = (ulong)(*param_2 >> 1);
  if (bVar7) {
    uVar3 = *(ulong *)(param_2 + 8);
  }
  bVar7 = (*param_1 & 1) == 0;
  pbVar5 = *(byte **)(param_1 + 0x10);
  if (bVar7) {
    pbVar5 = param_1 + 1;
  }
  uVar4 = (ulong)(*param_1 >> 1);
  if (!bVar7) {
    uVar4 = *(ulong *)(param_1 + 8);
  }
  uVar3 = func_0x04858ab0(pbVar6,uVar3,pbVar5,uVar4);
  bVar1 = *param_1;
  if ((bVar1 & 1) == 0) {
    lVar2 = uVar3 - (bVar1 >> 1);
    if (bVar1 >> 1 <= uVar3 && lVar2 != 0) goto LAB_02642291;
  }
  else {
    lVar2 = uVar3 - *(ulong *)(param_1 + 8);
    if (*(ulong *)(param_1 + 8) <= uVar3 && lVar2 != 0) {
LAB_02642291:
      func_0x04aac620(param_1,lVar2,0);
      return param_1;
    }
  }
  if ((bVar1 & 1) == 0) {
    *param_1 = (char)uVar3 * '\x02';
    (param_1 + 1)[uVar3] = 0;
  }
  else {
    *(ulong *)(param_1 + 8) = uVar3;
    *(undefined1 *)(*(long *)(param_1 + 0x10) + uVar3) = 0;
  }
  return param_1;
}


// FUNCTION 04858de0 bounds 04858e0f

void FUN_04858de0(undefined8 *param_1)

{
  undefined8 *puVar1;

  puVar1 = (undefined8 *)func_0x04b078f0(0x10);
  *puVar1 = &UNK_04c8af00;
  puVar1[1] = 0;
  *param_1 = puVar1;
  return;
}

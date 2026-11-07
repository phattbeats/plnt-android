// FUNCTION 047d84a0

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void FUN_047d84a0(undefined8 *param_1,undefined8 param_2,undefined8 param_3,undefined8 param_4,
                 long param_5)

{
  code *pcVar1;
  long lVar2;
  undefined8 uVar3;
  ulong uVar4;
  long lVar5;
  ulong uVar6;
  ulong uVar7;
  undefined8 uStack_170;
  ulong uStack_168;
  long lStack_160;
  byte bStack_158;
  undefined7 uStack_157;
  undefined4 uStack_150;
  undefined1 uStack_14c;
  undefined8 uStack_148;
  long alStack_140 [3];
  undefined *puStack_128;
  undefined1 auStack_120 [8];
  long lStack_118;
  ulong uStack_108;
  long lStack_100;
  ulong uStack_f8;
  byte bStack_e8;
  undefined7 uStack_e7;
  undefined8 uStack_d8;
  ulong uStack_d0;
  uint uStack_c8;
  undefined1 auStack_c0 [152];

  *param_1 = param_2;
  param_1[1] = param_3;
  param_1[2] = param_4;
  if (param_5 == 0) {
    param_1[3] = 0;
  }
  else {
    lVar2 = func_0x04a8bac0(param_5,&UNK_04c683e0,&UNK_04c6b478,0);
    param_1[3] = lVar2;
    if (lVar2 != 0) {
      return;
    }
  }
  func_0x01ab8040(alStack_140);
  uVar3 = func_0x01a53960(alStack_140 + 2,&UNK_0054d3ef,0x4c);
  uVar3 = func_0x04aba9e0(uVar3,0x22);
  uVar3 = func_0x01a53960(uVar3,&UNK_006b7c79,2);
  func_0x01a53960(uVar3,&UNK_005a82fc,0);
  if ((uStack_c8 & 0x10) == 0) {
    uVar7 = 0;
    lVar2 = lStack_118;
    lVar5 = 0;
    if ((uStack_c8 & 8) != 0) goto LAB_047d858c;
  }
  else {
    lVar2 = lStack_100;
    uStack_108 = uStack_d0;
    if (uStack_d0 < uStack_f8) {
      uStack_d0 = uStack_f8;
      uStack_108 = uStack_f8;
    }
LAB_047d858c:
    uVar7 = uStack_108 - lVar2;
    if ((long)uVar7 < 0) {
                    /* WARNING: Does not return */
      pcVar1 = (code *)invalidInstructionException();
      (*pcVar1)();
    }
    lVar5 = lVar2;
    if (0x16 < uVar7) {
      uVar4 = (uVar7 & 0x7ffffffffffffff8) + 8;
      uVar6 = 0x1a;
      if (uVar4 != 0x18) {
        uVar6 = uVar4;
      }
      lVar5 = func_0x04b078f0(uVar6);
      uStack_170 = uVar6 | 1;
      uStack_168 = uVar7;
      lStack_160 = lVar5;
      goto LAB_047d85ed;
    }
  }
  lVar2 = lVar5;
  uStack_170 = CONCAT71(uStack_170._1_7_,(char)uVar7 * '\x02');
  lVar5 = (long)&uStack_170 + 1;
LAB_047d85ed:
  func_0x04b07a30(lVar5,lVar2,uVar7);
  *(undefined1 *)(lVar5 + uVar7) = 0;
  bStack_158 = 0x16;
  uStack_157 = 0x75626f746f7250;
  uStack_150 = 0x43505266;
  uStack_14c = 0;
  func_0x02acb490(&uStack_170,0,&bStack_158,0);
  if ((bStack_158 & 1) != 0) {
    func_0x04a8cfc8(uStack_148,CONCAT71(uStack_157,bStack_158) & 0xfffffffffffffffe);
  }
  if ((uStack_170 & 1) != 0) {
    func_0x04a8cfc8(lStack_160,uStack_170 & 0xfffffffffffffffe);
  }
  alStack_140[0] = _UNK_04c9f9f0;
  *(undefined8 *)((long)alStack_140 + *(long *)(_UNK_04c9f9f0 + -0x18)) = _UNK_04c9fa30;
  alStack_140[2] = _UNK_04c9fa38;
  puStack_128 = &UNK_04c9f668;
  if ((bStack_e8 & 1) != 0) {
    func_0x04a8cfc8(uStack_d8,CONCAT71(uStack_e7,bStack_e8) & 0xfffffffffffffffe);
  }
  puStack_128 = &UNK_04c9f338;
  func_0x04aea9f0(auStack_120);
  func_0x04abbce8(alStack_140,&UNK_04c9f9f8);
  func_0x04ab7708(auStack_c0);
  return;
}


// FUNCTION 047d8780

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void FUN_047d8780(undefined8 *param_1,long *param_2,undefined8 *param_3,undefined8 *param_4)

{
  code *pcVar1;
  undefined8 uVar2;
  ulong uVar3;
  long lVar4;
  long lVar5;
  ulong uVar6;
  ulong uVar7;
  byte bStack_178;
  undefined7 uStack_177;
  undefined4 uStack_170;
  undefined1 uStack_16c;
  undefined8 uStack_168;
  undefined8 uStack_160;
  ulong uStack_158;
  long lStack_150;
  long alStack_148 [3];
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

  uVar2 = *param_3;
  *param_3 = 0;
  *param_1 = uVar2;
  param_1[0xc] = 0;
  param_1[0xd] = 0;
  param_1[0xe] = 0;
  param_1[0x11] = 0;
  param_1[0x12] = 0;
  param_1[0x13] = 0;
  param_1[0x14] = 0;
  param_1[0x15] = 0;
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
  func_0x04ab54a0(param_1 + 0x16);
  param_1[0x17] = 0;
  *(undefined1 *)(param_1 + 0x18) = 0;
  param_1[0x19] = 0;
  param_1[0x1a] = 0;
  param_1[0x1b] = 0;
  param_1[0x1c] = 0;
  param_1[0x1d] = 0;
  param_1[0x1e] = 0;
  param_1[0x1f] = 0;
  uVar2 = *param_4;
  *param_4 = 0;
  param_1[0x20] = uVar2;
  if (*param_2 != param_2[1]) goto LAB_047d8a59;
  func_0x01ab8040(alStack_148);
  uVar2 = func_0x01a53960(alStack_148 + 2,&UNK_0077bf6b,0x50);
  uVar2 = func_0x04aba9e0(uVar2,0x29);
  uVar2 = func_0x01a53960(uVar2,&UNK_006b7c79,2);
  func_0x01a53960(uVar2,&UNK_005a82fc,0);
  if ((uStack_d0 & 0x10) == 0) {
    uVar6 = 0;
    lVar5 = lStack_120;
    lVar4 = 0;
    if ((uStack_d0 & 8) != 0) goto LAB_047d88e4;
LAB_047d88f3:
    lVar5 = lVar4;
    uStack_160 = CONCAT71(uStack_160._1_7_,(char)uVar6 * '\x02');
    lVar4 = (long)&uStack_160 + 1;
  }
  else {
    lVar5 = lStack_108;
    uStack_110 = uStack_d8;
    if (uStack_d8 < uStack_100) {
      uStack_d8 = uStack_100;
      uStack_110 = uStack_100;
    }
LAB_047d88e4:
    uVar6 = uStack_110 - lVar5;
    if ((long)uVar6 < 0) {
                    /* WARNING: Does not return */
      pcVar1 = (code *)invalidInstructionException();
      (*pcVar1)();
    }
    lVar4 = lVar5;
    if (uVar6 < 0x17) goto LAB_047d88f3;
    uVar3 = (uVar6 & 0x7ffffffffffffff8) + 8;
    uVar7 = 0x1a;
    if (uVar3 != 0x18) {
      uVar7 = uVar3;
    }
    lVar4 = func_0x04b078f0(uVar7);
    uStack_160 = uVar7 | 1;
    uStack_158 = uVar6;
    lStack_150 = lVar4;
  }
  func_0x04b07a30(lVar4,lVar5,uVar6);
  *(undefined1 *)(lVar4 + uVar6) = 0;
  bStack_178 = 0x16;
  uStack_177 = 0x75626f746f7250;
  uStack_170 = 0x43505266;
  uStack_16c = 0;
  func_0x02acb490(&uStack_160,0,&bStack_178,0);
  if ((bStack_178 & 1) != 0) {
    func_0x04a8cfc8(uStack_168,CONCAT71(uStack_177,bStack_178) & 0xfffffffffffffffe);
  }
  if ((uStack_160 & 1) != 0) {
    func_0x04a8cfc8(lStack_150,uStack_160 & 0xfffffffffffffffe);
  }
  alStack_148[0] = _UNK_04c9f9f0;
  *(undefined8 *)((long)alStack_148 + *(long *)(_UNK_04c9f9f0 + -0x18)) = _UNK_04c9fa30;
  alStack_148[2] = _UNK_04c9fa38;
  puStack_130 = &UNK_04c9f668;
  if ((bStack_f0 & 1) != 0) {
    func_0x04a8cfc8(uStack_e0,CONCAT71(uStack_ef,bStack_f0) & 0xfffffffffffffffe);
  }
  puStack_130 = &UNK_04c9f338;
  func_0x04aea9f0(auStack_128);
  func_0x04abbce8(alStack_148,&UNK_04c9f9f8);
  func_0x04ab7708(auStack_c8);
LAB_047d8a59:
  *(undefined2 *)(param_1 + 0xb) = 0;
  func_0x04ab56d0(&bStack_178,param_1 + 0x16);
  uVar2 = CONCAT71(uStack_177,bStack_178);
  bStack_178 = 0;
  uStack_177 = 0;
  uStack_160 = 0;
  alStack_148[0] = param_1[0x17];
  param_1[0x17] = uVar2;
  func_0x04ab5890(alStack_148);
  func_0x04ab5890(&uStack_160);
  func_0x04ab5400(&bStack_178);
  param_1[0xf] = 0;
  param_1[0x10] = 0;
  if (param_1 + 0xc != param_2) {
    func_0x01aac2d0(param_1 + 0xc,*param_2,param_2[1],
                    (param_2[1] - *param_2 >> 3) * -0x5555555555555555);
  }
  return;
}


// FUNCTION 047d9ac0

/* WARNING: Possible PIC construction at 0x047d9b5b: Changing call to branch */
/* WARNING: Possible PIC construction at 0x047da40f: Changing call to branch */
/* WARNING: Possible PIC construction at 0x047da668: Changing call to branch */
/* WARNING: Possible PIC construction at 0x047da952: Changing call to branch */
/* WARNING: Possible PIC construction at 0x047d989c: Changing call to branch */
/* WARNING: Removing unreachable block (ram,0x047da66d) */
/* WARNING: Removing unreachable block (ram,0x047da674) */
/* WARNING: Removing unreachable block (ram,0x047d9b60) */
/* WARNING: Removing unreachable block (ram,0x047da957) */
/* WARNING: Removing unreachable block (ram,0x047daa53) */
/* WARNING: Removing unreachable block (ram,0x047da9a0) */
/* WARNING: Removing unreachable block (ram,0x047daa60) */
/* WARNING: Removing unreachable block (ram,0x047daaf6) */
/* WARNING: Removing unreachable block (ram,0x047daaea) */
/* WARNING: Removing unreachable block (ram,0x047daafb) */
/* WARNING: Removing unreachable block (ram,0x047daaf4) */
/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void FUN_047d9ac0(undefined8 *param_1)

{
  undefined4 *puVar1;
  byte bVar2;
  long *plVar3;
  code *pcVar4;
  undefined4 uVar5;
  undefined4 uVar6;
  undefined8 *puVar7;
  char cVar8;
  int iVar9;
  uint uVar10;
  undefined4 uVar11;
  undefined8 uVar12;
  long lVar13;
  ulong *puVar14;
  ulong uVar15;
  ulong uVar16;
  undefined8 *puVar17;
  undefined8 uVar18;
  undefined8 uVar19;
  long lVar20;
  ulong uVar21;
  undefined8 *unaff_RBX;
  undefined8 *puVar22;
  byte *pbVar23;
  long lVar24;
  undefined4 *puVar25;
  ulong unaff_R12;
  byte *unaff_R14;
  byte *pbVar26;
  byte *unaff_R15;
  bool bVar27;
  char acStack_2f8 [296];
  undefined8 uStack_1d0;
  undefined1 auStack_1c8 [8];
  byte *pbStack_1c0;
  undefined8 *puStack_1b8;
  undefined4 uStack_1a8;
  undefined4 uStack_1a4;
  undefined8 uStack_1a0;
  long lStack_198;
  undefined4 uStack_190;
  undefined4 uStack_18c;
  ulong uStack_188;
  long lStack_180;
  byte *pbStack_178;
  undefined8 uStack_170;
  undefined8 *puStack_168;
  uint uStack_15c;
  undefined8 uStack_158;
  undefined8 uStack_150;
  long lStack_148;
  undefined *puStack_140;
  undefined1 auStack_138 [8];
  long lStack_130;
  ulong uStack_120;
  long lStack_118;
  ulong uStack_110;
  byte bStack_100;
  undefined7 uStack_ff;
  undefined8 uStack_f0;
  ulong uStack_e8;
  uint uStack_e0;
  undefined1 auStack_d8 [160];
  long lStack_38;

  pbVar26 = auStack_1c8;
  puVar22 = (undefined8 *)auStack_1c8;
  uStack_1d0 = 0x47d9add;
  cVar8 = (**(code **)(*(long *)*param_1 + 0x30))();
  if (cVar8 == '\0') {
    uStack_1d0 = 0x47d9b68;
    cVar8 = (**(code **)(*(long *)*param_1 + 0x38))();
    if ((cVar8 != '\0') && (*(char *)((long)param_1 + 0x59) != '\0')) {
      lVar24 = param_1[10];
      uStack_1d0 = 0x47d9b81;
      puVar25 = (undefined4 *)func_0x04b078f0(0x1a);
      *(undefined8 *)((long)puVar25 + 0xf) = 0x6c656e6e61686320;
      uVar6 = _UNK_0077bfc8;
      uVar5 = _UNK_0077bfc4;
      uVar11 = _UNK_0077bfc0;
      *puVar25 = _DAT_0077bfbc;
      puVar25[1] = uVar11;
      puVar25[2] = uVar5;
      puVar25[3] = uVar6;
      *(undefined1 *)((long)puVar25 + 0x17) = 0;
      *(undefined4 *)(lVar24 + 8) = 2;
      if ((*(byte *)(lVar24 + 0x10) & 1) == 0) {
        uStack_1d0 = 0x47d9cbc;
        func_0x01a126b0(lVar24 + 0x10,puVar25,0x17);
      }
      else {
        uStack_1d0 = 0x47d9bc4;
        func_0x01a125a0(lVar24 + 0x10,puVar25,0x17);
      }
      uStack_1d0 = 0x47d9cc9;
      func_0x04a8cfc8(puVar25,0x1a);
SUB_047d9850:
      *(byte **)((long)register0x00000020 + -8) = unaff_R15;
      *(byte **)((long)register0x00000020 + -0x10) = unaff_R14;
      *(undefined8 **)((long)register0x00000020 + -0x18) = unaff_RBX;
      pcVar4 = *(code **)(*(long *)param_1[10] + 0x10);
      *(undefined8 *)((long)register0x00000020 + -0x170) = 0x47d9869;
      (*pcVar4)();
      *(undefined8 **)((long)register0x00000020 + -0x130) = param_1 + 0x11;
      *(undefined1 *)((long)register0x00000020 + -0x128) = 1;
      *(undefined8 *)((long)register0x00000020 + -0x170) = 0x47d987f;
      func_0x04ab5980();
      *(undefined1 *)(param_1 + 0xb) = 0;
      *(undefined8 *)((long)register0x00000020 + -0x170) = 0x47d9890;
      func_0x047d8d20(param_1,(undefined1 *)((long)register0x00000020 + -0x130));
      if (*(char *)((long)register0x00000020 + -0x128) != '\x01') {
        return;
      }
      lVar24 = *(long *)((long)register0x00000020 + -0x130);
      *(undefined8 *)((long)register0x00000020 + -0x170) = 0x47d98a1;
      goto SUB_04ab59b0;
    }
    uStack_1d0 = 0x47d9bd3;
    func_0x01ab8040(&uStack_158);
    uStack_1d0 = 0x47d9bec;
    uVar12 = func_0x01a53960(&lStack_148,&UNK_00511c29,0x15);
    pbVar23 = (byte *)param_1[0xf];
    lVar24 = param_1[0xc];
    puStack_168 = param_1;
    if ((byte *)((param_1[0xd] - lVar24 >> 3) * -0x5555555555555555) <= pbVar23) goto LAB_047da68c;
    bVar2 = *(byte *)(lVar24 + (long)pbVar23 * 0x18);
    if ((bVar2 & 1) == 0) {
      unaff_R15 = (byte *)(ulong)(bVar2 >> 1);
    }
    else {
      unaff_R15 = *(byte **)(lVar24 + 8 + (long)pbVar23 * 0x18);
    }
    unaff_R14 = unaff_R15 + 2;
    if ((byte *)0xfffffffffffffff6 < unaff_R14) {
      uStack_1d0 = 0x47da693;
      func_0x019f8660();
      goto LAB_047da693;
    }
    pbVar26 = *(byte **)(lVar24 + 0x10 + (long)pbVar23 * 0x18);
    uStack_170 = uVar12;
    if (unaff_R14 < (byte *)0x17) {
      bVar27 = false;
      uStack_1a8 = CONCAT31(uStack_1a8._1_3_,(char)unaff_R14 * '\x02');
      unaff_R14 = uStack_1a0;
    }
    else {
      lVar13 = ((ulong)unaff_R14 & 0xfffffffffffffff8) + 8;
      lVar20 = 0x1a;
      if (lVar13 != 0x18) {
        lVar20 = lVar13;
      }
      uStack_1d0 = 0x47d9e22;
      pbStack_178 = pbVar26;
      lStack_198 = func_0x04b078f0(lVar20);
      uStack_1a8 = (uint)lVar20 | 1;
      uStack_1a4 = (undefined4)((ulong)lVar20 >> 0x20);
      bVar27 = true;
      pbVar26 = pbStack_178;
    }
    pbVar23 = (byte *)((long)pbVar23 * 0x18 + lVar24 + 1);
    if ((bVar2 & 1) != 0) {
      pbVar23 = pbVar26;
    }
    lVar24 = (long)&uStack_1a8 + 1;
    if (bVar27) {
      lVar24 = lStack_198;
    }
    uStack_1d0 = 0x47d9e6b;
    uStack_1a0 = unaff_R14;
    func_0x04b07a30(lVar24,pbVar23,unaff_R15);
    (unaff_R15 + lVar24)[0] = 0x3a;
    (unaff_R15 + lVar24)[1] = 0x20;
    unaff_R15[lVar24 + 2] = 0;
    param_1 = puStack_168;
    uStack_1d0 = 0x47d9e8c;
    (**(code **)(*(long *)*puStack_168 + 0x40))(&uStack_190);
    uVar21 = (ulong)((byte)uStack_190 >> 1);
    lVar24 = (long)&uStack_190 + 1;
    if ((uStack_190 & 1) != 0) {
      uVar21 = uStack_188;
      lVar24 = lStack_180;
    }
    uStack_1d0 = 0x47d9eb2;
    puVar14 = (ulong *)func_0x04aac7b0(&uStack_1a8,lVar24,uVar21);
    puStack_1b8 = (undefined8 *)puVar14[2];
    auStack_1c8 = (undefined1  [8])*puVar14;
    pbStack_1c0 = (byte *)puVar14[1];
    *puVar14 = 0;
    puVar14[1] = 0;
    puVar14[2] = 0;
    puVar17 = (undefined8 *)(auStack_1c8 + 1);
    pbVar26 = (byte *)((ulong)auStack_1c8 >> 1 & 0x7f);
    puVar7 = puVar17;
    if (((ulong)auStack_1c8 & 1) != 0) {
      pbVar26 = pbStack_1c0;
      puVar7 = puStack_1b8;
    }
    uStack_1d0 = 0x47d9ef8;
    func_0x01a53960(uStack_170,puVar7,pbVar26);
    if (((ulong)auStack_1c8 & 1) == 0) {
      if ((uStack_190 & 1) != 0) goto LAB_047d9f75;
LAB_047d9f17:
      if ((uStack_1a8 & 1) != 0) goto LAB_047d9f1e;
LAB_047d9f8f:
      if ((uStack_e0 & 0x10) == 0) goto LAB_047d9f9a;
LAB_047d9f3c:
      if (uStack_e8 < uStack_110) {
        uStack_e8 = uStack_110;
      }
      pbVar26 = (byte *)(uStack_e8 - lStack_118);
      lVar24 = lStack_118;
joined_r0x047d9fb9:
      if ((long)pbVar26 < 0) goto LAB_047da68c;
      if (pbVar26 < (byte *)0x17) goto LAB_047d9fc9;
      uVar16 = ((ulong)pbVar26 & 0x7ffffffffffffff8) + 8;
      uVar21 = 0x1a;
      if (uVar16 != 0x18) {
        uVar21 = uVar16;
      }
      uStack_1d0 = 0x47da1de;
      puVar17 = (undefined8 *)func_0x04b078f0(uVar21);
      auStack_1c8 = (undefined1  [8])(uVar21 | 1);
      pbStack_1c0 = pbVar26;
      puStack_1b8 = puVar17;
    }
    else {
      uStack_1d0 = 0x47d9f10;
      func_0x04a8cfc8(puStack_1b8,(ulong)auStack_1c8 & 0xfffffffffffffffe);
      if ((uStack_190 & 1) == 0) goto LAB_047d9f17;
LAB_047d9f75:
      uStack_1d0 = 0x47d9f88;
      func_0x04a8cfc8(lStack_180,CONCAT44(uStack_18c,uStack_190) & 0xfffffffffffffffe);
      if ((uStack_1a8 & 1) == 0) goto LAB_047d9f8f;
LAB_047d9f1e:
      uStack_1d0 = 0x47d9f31;
      func_0x04a8cfc8(lStack_198,CONCAT44(uStack_1a4,uStack_1a8) & 0xfffffffffffffffe);
      if ((uStack_e0 & 0x10) != 0) goto LAB_047d9f3c;
LAB_047d9f9a:
      pbVar26 = (byte *)0x0;
      lVar24 = 0;
      if ((uStack_e0 & 8) != 0) {
        pbVar26 = (byte *)(uStack_120 - lStack_130);
        lVar24 = lStack_130;
        goto joined_r0x047d9fb9;
      }
LAB_047d9fc9:
      auStack_1c8[0] = (char)pbVar26 * '\x02';
    }
    uStack_1d0 = 0x47da201;
    func_0x04b07a30(puVar17,lVar24,pbVar26);
    *(byte *)((long)puVar17 + (long)pbVar26) = 0;
    uStack_1a8 = 0x6f725016;
    uStack_1a4 = 0x75626f74;
    uStack_1a0._0_4_ = 0x43505266;
    uStack_1a0._4_4_ = uStack_1a0._4_4_ & 0xffffff00;
    uStack_1d0 = 0x47da23b;
    func_0x02acb490(auStack_1c8,2,&uStack_1a8,0);
  }
  else {
    uStack_1d0 = 0x47d9aeb;
    iVar9 = (**(code **)(*(long *)*param_1 + 0x48))();
    if (iVar9 != 200) {
      uStack_1d0 = 0x47d9c49;
      func_0x01ab8040(&uStack_158);
      pbVar23 = &UNK_00647e4a;
      uStack_1d0 = 0x47d9c62;
      unaff_R15 = (byte *)func_0x01a53960(&lStack_148,&UNK_00647e4a,0xe);
      uVar21 = param_1[0xf];
      lVar24 = param_1[0xc];
      if ((ulong)((param_1[0xd] - lVar24 >> 3) * -0x5555555555555555) <= uVar21) goto LAB_047da68c;
      bVar2 = *(byte *)(lVar24 + uVar21 * 0x18);
      uStack_170 = CONCAT44(uStack_170._4_4_,iVar9);
      if ((bVar2 & 1) == 0) {
        unaff_R12 = (ulong)(bVar2 >> 1);
      }
      else {
        unaff_R12 = *(ulong *)(lVar24 + 8 + uVar21 * 0x18);
      }
      unaff_R14 = (byte *)(unaff_R12 + 0xc);
      if (unaff_R14 < (byte *)0xfffffffffffffff7) {
        lVar20 = *(long *)(lVar24 + 0x10 + uVar21 * 0x18);
        if (unaff_R14 < (byte *)0x17) {
          bVar27 = false;
          auStack_1c8[0] = (char)unaff_R14 * '\x02';
          puStack_168 = param_1;
          uVar10 = (uint)bVar2;
        }
        else {
          uVar15 = ((ulong)unaff_R14 & 0xfffffffffffffff8) + 8;
          uVar16 = 0x1a;
          if (uVar15 != 0x18) {
            uVar16 = uVar15;
          }
          uStack_1d0 = 0x47da007;
          pbStack_178 = unaff_R15;
          puStack_168 = param_1;
          uStack_15c = (uint)bVar2;
          lStack_38 = lVar20;
          puStack_1b8 = (undefined8 *)func_0x04b078f0(uVar16);
          auStack_1c8 = (undefined1  [8])(uVar16 | 1);
          bVar27 = true;
          lVar20 = lStack_38;
          unaff_R15 = pbStack_178;
          pbStack_1c0 = unaff_R14;
          uVar10 = uStack_15c;
        }
        lVar24 = uVar21 * 0x18 + lVar24 + 1;
        if ((uVar10 & 1) != 0) {
          lVar24 = lVar20;
        }
        unaff_RBX = (undefined8 *)(auStack_1c8 + 1);
        puVar17 = puStack_1b8;
        if (!bVar27) {
          puVar17 = unaff_RBX;
        }
        uStack_1d0 = 0x47da05c;
        func_0x04b07a30(puVar17,lVar24,unaff_R12);
        *(undefined8 *)((long)puVar17 + unaff_R12) = 0x646e6f7073657220;
        *(undefined4 *)((long)puVar17 + unaff_R12 + 8) = 0x203a6465;
        *(undefined1 *)((long)puVar17 + unaff_R12 + 0xc) = 0;
        pbVar23 = (byte *)((ulong)auStack_1c8 >> 1 & 0x7f);
        puVar17 = unaff_RBX;
        if (((ulong)auStack_1c8 & 1) != 0) {
          pbVar23 = pbStack_1c0;
          puVar17 = puStack_1b8;
        }
        uStack_1d0 = 0x47da09b;
        uVar12 = func_0x01a53960(unaff_R15,puVar17,pbVar23);
        param_1 = puStack_168;
        uStack_1d0 = 0x47da0ad;
        uVar11 = (**(code **)(*(long *)*puStack_168 + 0x48))();
        uStack_1d0 = 0x47da0b7;
        uVar12 = func_0x04aba9e0(uVar12,uVar11);
        uStack_1d0 = 0x47da0cb;
        uVar12 = func_0x01a53960(uVar12,&UNK_0052356e,1);
        uStack_1d0 = 0x47da0dd;
        (**(code **)(*(long *)*param_1 + 0x50))(&uStack_1a8);
        bVar27 = (uStack_1a8 & 1) != 0;
        lVar24 = (long)&uStack_1a8 + 1;
        if (bVar27) {
          lVar24 = lStack_198;
        }
        uVar21 = (ulong)((byte)uStack_1a8 >> 1);
        if (bVar27) {
          uVar21 = CONCAT44(uStack_1a0._4_4_,(undefined4)uStack_1a0);
        }
        uStack_1d0 = 0x47da101;
        func_0x01a53960(uVar12,lVar24,uVar21);
        if ((uStack_1a8 & 1) == 0) {
          if (((ulong)auStack_1c8 & 1) != 0) goto LAB_047da150;
LAB_047da121:
          if ((uStack_e0 & 0x10) == 0) goto LAB_047da12c;
LAB_047da16d:
          lVar24 = lStack_118;
          uStack_120 = uStack_e8;
          if (uStack_e8 < uStack_110) {
            uStack_e8 = uStack_110;
            uStack_120 = uStack_110;
          }
LAB_047da195:
          unaff_R15 = (byte *)(uStack_120 - lVar24);
          if ((long)unaff_R15 < 0) goto LAB_047da68c;
          lVar20 = lVar24;
          if (unaff_R15 < (byte *)0x17) goto LAB_047da1a8;
          uVar16 = ((ulong)unaff_R15 & 0x7ffffffffffffff8) + 8;
          uVar21 = 0x1a;
          if (uVar16 != 0x18) {
            uVar21 = uVar16;
          }
          uStack_1d0 = 0x47da26a;
          unaff_RBX = (undefined8 *)func_0x04b078f0(uVar21);
          unaff_R12 = uVar21 | 1;
          auStack_1c8 = (undefined1  [8])unaff_R12;
          pbStack_1c0 = unaff_R15;
          puStack_1b8 = unaff_RBX;
        }
        else {
          uStack_1d0 = 0x47da11b;
          func_0x04a8cfc8(lStack_198,CONCAT44(uStack_1a4,uStack_1a8) & 0xfffffffffffffffe);
          if (((ulong)auStack_1c8 & 1) == 0) goto LAB_047da121;
LAB_047da150:
          uStack_1d0 = 0x47da162;
          func_0x04a8cfc8(puStack_1b8,(ulong)auStack_1c8 & 0xfffffffffffffffe);
          if ((uStack_e0 & 0x10) != 0) goto LAB_047da16d;
LAB_047da12c:
          unaff_R15 = (byte *)0x0;
          lVar24 = lStack_130;
          lVar20 = 0;
          if ((uStack_e0 & 8) != 0) goto LAB_047da195;
LAB_047da1a8:
          lVar24 = lVar20;
          auStack_1c8[0] = (char)unaff_R15 * '\x02';
        }
        uStack_1d0 = 0x47da28d;
        func_0x04b07a30(unaff_RBX,lVar24,unaff_R15);
        *(byte *)((long)unaff_RBX + (long)unaff_R15) = 0;
        uStack_1a8 = 0x6f725016;
        uStack_1a4 = 0x75626f74;
        uStack_1a0._0_4_ = 0x43505266;
        uStack_1a0._4_4_ = uStack_1a0._4_4_ & 0xffffff00;
        uStack_1d0 = 0x47da2c7;
        func_0x02acb490(auStack_1c8,2,&uStack_1a8,0);
        if ((uStack_1a8 & 1) != 0) {
          uStack_1d0 = 0x47da2e1;
          func_0x04a8cfc8(lStack_198,CONCAT44(uStack_1a4,uStack_1a8) & 0xfffffffffffffffe);
        }
        if (((ulong)auStack_1c8 & 1) != 0) {
          uStack_1d0 = 0x47da2f9;
          func_0x04a8cfc8(puStack_1b8,(ulong)auStack_1c8 & 0xfffffffffffffffe);
        }
        uStack_158 = _UNK_04c9f9f0;
        *(undefined8 *)((long)&uStack_158 + *(long *)(_UNK_04c9f9f0 - 0x18)) = _UNK_04c9fa30;
        lStack_148 = _UNK_04c9fa38;
        puStack_140 = &UNK_04c9f668;
        if ((bStack_100 & 1) != 0) {
          uStack_1d0 = 0x47da357;
          func_0x04a8cfc8(uStack_f0,CONCAT71(uStack_ff,bStack_100) & 0xfffffffffffffffe);
        }
        puStack_140 = &UNK_04c9f338;
        uStack_1d0 = 0x47da377;
        func_0x04aea9f0(auStack_138);
        unaff_R14 = &UNK_04c9f9f8;
        uStack_1d0 = 0x47da388;
        pbVar23 = unaff_R14;
        func_0x04abbce8(&uStack_158);
        uStack_1d0 = 0x47da395;
        func_0x04ab7708(auStack_d8);
        if (param_1[0x20] == 0) goto LAB_047da61e;
        uStack_1a8 = 0;
        uStack_158 = 0;
        uStack_150 = 0;
        lStack_148 = 0;
        uStack_190 = 0;
        auStack_1c8._0_4_ = (undefined4)uStack_170;
        plVar3 = *(long **)(param_1[0x20] + 0x20);
        if (plVar3 == (long *)0x0) goto LAB_047da698;
        unaff_R14 = (byte *)&uStack_158;
        uStack_1d0 = 0x47da3f8;
        cVar8 = (**(code **)(*plVar3 + 0x30))(plVar3,auStack_1c8,&uStack_1a8,unaff_R14,&uStack_190);
        if (cVar8 == '\0') {
          if ((uStack_158 & 1) != 0) {
            uStack_1d0 = 0x47da47f;
            func_0x04a8cfc8(lStack_148,uStack_158 & 0xfffffffffffffffe);
          }
          goto LAB_047da61e;
        }
        if ((ulong)uStack_190 == 0) {
          lVar24 = param_1[10];
          *(uint *)(lVar24 + 8) = uStack_1a8;
          pbVar26 = (byte *)(lVar24 + 0x10);
          if (pbVar26 != unaff_R14) {
            if ((*pbVar26 & 1) == 0) {
              if ((uStack_158 & 1) == 0) {
                *(long *)(lVar24 + 0x20) = lStack_148;
                *(undefined4 *)pbVar26 = (undefined4)uStack_158;
                *(undefined4 *)(lVar24 + 0x14) = uStack_158._4_4_;
                *(undefined4 *)(lVar24 + 0x18) = (undefined4)uStack_150;
                *(undefined4 *)(lVar24 + 0x1c) = uStack_150._4_4_;
              }
              else {
                uStack_1d0 = 0x47da64f;
                func_0x01a126b0(pbVar26,lStack_148,uStack_150);
              }
            }
            else {
              uVar21 = uStack_158 >> 1 & 0x7f;
              lVar24 = (long)&uStack_158 + 1;
              if ((uStack_158 & 1) != 0) {
                uVar21 = uStack_150;
                lVar24 = lStack_148;
              }
              uStack_1d0 = 0x47da459;
              func_0x01a125a0(pbVar26,lVar24,uVar21);
            }
          }
          register0x00000020 = (BADSPACEBASE *)&uStack_1d0;
          uStack_1d0 = 0x47da66d;
          goto SUB_047d9850;
        }
        auStack_1c8 = (undefined1  [8])(ulong)uStack_190;
        puVar22 = &uStack_1d0;
        uStack_1d0 = 0x47da414;
      }
      else {
LAB_047da693:
        uStack_1d0 = 0x47da698;
        func_0x019f8660();
LAB_047da698:
        uStack_1d0 = 0x47da69d;
        uVar12 = func_0x01a8f9c0();
        uStack_1d0 = 0x47da777;
        func_0x01ab81b0(&uStack_158);
        uStack_1d0 = 0x47da77f;
        uVar12 = func_0x04b07a10(uVar12);
        pbVar26 = pbVar23;
        if ((uStack_158 & 1) != 0) {
          pbVar26 = (byte *)(uStack_158 & 0xfffffffffffffffe);
          uStack_1d0 = 0x47da79f;
          func_0x04a8cfc8(lStack_148);
        }
        uStack_1d0 = 0x47da7a7;
        unaff_RBX = (undefined8 *)func_0x04b07a10(uVar12);
        if (((ulong)auStack_1c8 & 1) != 0) {
          pbVar26 = (byte *)((ulong)auStack_1c8 & 0xfffffffffffffffe);
          uStack_1d0 = 0x47da7c2;
          func_0x04a8cfc8(puStack_1b8);
        }
        uStack_1d0 = 0x47da7cc;
        func_0x01ab81b0(&uStack_158);
        uStack_1d0 = 0x47da7d4;
        param_1 = unaff_RBX;
        func_0x04b07a10();
      }
      *(byte **)((long)puVar22 + -8) = unaff_R15;
      *(byte **)((long)puVar22 + -0x10) = unaff_R14;
      *(ulong *)((long)puVar22 + -0x18) = unaff_R12;
      *(undefined8 **)((long)puVar22 + -0x20) = unaff_RBX;
      if (*(char *)(param_1 + 0x18) != '\0') {
        return;
      }
      plVar3 = param_1 + 0x19;
      if (param_1[0x19] != 0) {
        *(undefined8 *)((long)puVar22 + -0x40) = 0x47da81d;
        func_0x04ab4af8(param_1 + 0x1a);
        *(undefined8 *)((long)puVar22 + -0x40) = 0x47da825;
        func_0x04ab62e0(plVar3);
      }
      LOCK();
      *(undefined1 *)(param_1 + 0x18) = 1;
      UNLOCK();
      uVar12 = *(undefined8 *)pbVar26;
      *(undefined8 *)((long)puVar22 + -0x40) = 0x47da83e;
      uVar18 = func_0x04b078f0(8);
      *(undefined8 *)((long)puVar22 + -0x40) = 0x47da849;
      func_0x04ab6620(uVar18);
      *(undefined8 *)((long)puVar22 + -0x30) = uVar18;
      *(undefined8 *)((long)puVar22 + -0x40) = 0x47da858;
      puVar17 = (undefined8 *)func_0x04b078f0(0x18);
      *(undefined8 *)((long)puVar22 + -0x30) = 0;
      *puVar17 = uVar18;
      puVar17[1] = param_1;
      puVar17[2] = uVar12;
      *(undefined8 **)((long)puVar22 + -0x28) = puVar17;
      *(undefined8 *)((long)puVar22 + -0x40) = 0x47da885;
      iVar9 = func_0x04b07f30((undefined1 *)((long)puVar22 + -0x38),0,&UNK_047db820,puVar17);
      if (iVar9 == 0) {
        if (*plVar3 == 0) {
          *plVar3 = *(long *)((long)puVar22 + -0x38);
          *(undefined8 *)((long)puVar22 + -0x38) = 0;
          *(undefined8 *)((long)puVar22 + -0x40) = 0x47da8a6;
          func_0x04ab62d0((undefined1 *)((long)puVar22 + -0x38));
          return;
        }
      }
      else {
        *(undefined8 *)((long)puVar22 + -0x40) = 0x47da8c0;
        func_0x04ab4980(iVar9,&UNK_0057483f);
      }
      *(undefined8 *)((long)puVar22 + -0x40) = 0x47da8c5;
      uVar19 = func_0x04a8b200();
      *(undefined8 *)((long)puVar22 + -0x40) = 0x47da8d2;
      func_0x047db900((undefined1 *)((long)puVar22 + -0x28));
      *(undefined8 *)((long)puVar22 + -0x40) = 0x47da8dc;
      func_0x01a60c20((undefined1 *)((long)puVar22 + -0x30));
      *(undefined8 *)((long)puVar22 + -0x40) = 0x47da8e4;
      uVar19 = func_0x019f83c0(uVar19);
      *(undefined8 *)((long)puVar22 + -0x40) = 0x47da8f1;
      func_0x01a60c20((undefined1 *)((long)puVar22 + -0x30));
      *(undefined8 *)((long)puVar22 + -0x40) = 0x47da8f9;
      uVar19 = func_0x019f83c0(uVar19);
      puVar25 = (undefined4 *)0x8;
      *(undefined8 *)((long)puVar22 + -0x40) = 0x47da909;
      func_0x04a8cfc8(uVar18);
      *(undefined8 *)((long)puVar22 + -0x40) = 0x47da911;
      lVar20 = func_0x019f83c0(uVar19);
      *(undefined8 *)((long)puVar22 + -0x40) = 0x47da919;
      func_0x019f83c0();
      *(undefined8 *)((long)puVar22 + -0x40) = uVar18;
      *(undefined8 **)((long)puVar22 + -0x48) = param_1;
      *(undefined8 *)((long)puVar22 + -0x50) = uVar12;
      *(undefined8 *)((long)puVar22 + -0x58) = uVar19;
      lVar24 = lVar20 + 0x88;
      *(long *)((long)puVar22 + -0xb0) = lVar24;
      *(undefined1 *)((long)puVar22 + -0xa8) = 1;
      *(undefined8 *)((long)puVar22 + -0xb8) = 0x47da949;
      func_0x04ab5980(lVar24);
      if (*(char *)(lVar20 + 0x59) == '\x01') {
        *(undefined8 *)((long)puVar22 + -0xb8) = 0x47da957;
      }
      else {
        lVar24 = *(long *)(lVar20 + 0x10);
        uVar21 = 0;
        if (*(long *)(lVar20 + 0x18) != lVar24) {
          uVar21 = (*(long *)(lVar20 + 0x18) - lVar24) * 0x10 - 1;
        }
        uVar16 = *(long *)(lVar20 + 0x30) + *(long *)(lVar20 + 0x28);
        if (uVar21 == uVar16) {
          *(undefined8 *)((long)puVar22 + -0xb8) = 0x47da9e6;
          func_0x047dbe40(lVar20 + 8);
          lVar24 = *(long *)(lVar20 + 0x10);
          uVar16 = *(long *)(lVar20 + 0x28) + *(long *)(lVar20 + 0x30);
        }
        lVar24 = *(long *)(lVar24 + (uVar16 >> 7) * 8);
        uVar21 = (ulong)(((uint)uVar16 & 0x7f) << 5);
        *(undefined8 *)(lVar24 + 0x10 + uVar21) = *(undefined8 *)(puVar25 + 4);
        uVar11 = puVar25[1];
        uVar5 = puVar25[2];
        uVar6 = puVar25[3];
        puVar1 = (undefined4 *)(lVar24 + uVar21);
        *puVar1 = *puVar25;
        puVar1[1] = uVar11;
        puVar1[2] = uVar5;
        puVar1[3] = uVar6;
        uVar12 = *(undefined8 *)(puVar25 + 6);
        *(undefined8 *)(puVar25 + 6) = 0;
        *(undefined8 *)(lVar24 + 0x18 + uVar21) = uVar12;
        *(long *)(lVar20 + 0x30) = *(long *)(lVar20 + 0x30) + 1;
        *(undefined8 *)((long)puVar22 + -0xb8) = 0x47daa34;
        func_0x047d8d20(lVar20,(undefined1 *)((long)puVar22 + -0xb0));
        if (*(char *)((long)puVar22 + -0xa8) != '\x01') {
          return;
        }
        lVar24 = *(long *)((long)puVar22 + -0xb0);
      }
SUB_04ab59b0:
      (*(code *)PTR_pthread_mutex_unlock_04d10da0)(lVar24);
      return;
    }
    unaff_R14 = (byte *)param_1[9];
    uStack_1d0 = 0x47d9b09;
    (**(code **)(*(long *)*param_1 + 0x68))(&uStack_158);
    uVar21 = uStack_158 >> 1 & 0x7f;
    lVar24 = (long)&uStack_158 + 1;
    if ((uStack_158 & 1) != 0) {
      uVar21 = uStack_150;
      lVar24 = lStack_148;
    }
    uStack_1d0 = 0x47d9b30;
    cVar8 = func_0x044027a0(unaff_R14,lVar24,uVar21);
    if ((uStack_158 & 1) != 0) {
      uStack_1d0 = 0x47d9b4f;
      func_0x04a8cfc8(lStack_148,uStack_158 & 0xfffffffffffffffe);
    }
    if (cVar8 != '\0') {
      register0x00000020 = (BADSPACEBASE *)&uStack_1d0;
      uStack_1d0 = 0x47d9b60;
      goto SUB_047d9850;
    }
    uStack_1d0 = 0x47d9cec;
    func_0x01ab8040(&uStack_158);
    uStack_1d0 = 0x47d9d05;
    uVar12 = func_0x01a53960(&lStack_148,&UNK_004f61a9,0x2a);
    uVar21 = param_1[0xf];
    lVar24 = param_1[0xc];
    if ((ulong)((param_1[0xd] - lVar24 >> 3) * -0x5555555555555555) <= uVar21) goto LAB_047da68c;
    bVar2 = *(byte *)(lVar24 + uVar21 * 0x18);
    bVar27 = (bVar2 & 1) != 0;
    lVar20 = lVar24 + uVar21 * 0x18 + 1;
    if (bVar27) {
      lVar20 = *(long *)(lVar24 + 0x10 + uVar21 * 0x18);
    }
    uVar16 = (ulong)(bVar2 >> 1);
    if (bVar27) {
      uVar16 = *(ulong *)(lVar24 + 8 + uVar21 * 0x18);
    }
    uStack_1d0 = 0x47d9d5b;
    func_0x01a53960(uVar12,lVar20,uVar16);
    if ((uStack_e0 & 0x10) == 0) {
      pbVar26 = (byte *)0x0;
      lVar24 = lStack_130;
      lVar20 = 0;
      if ((uStack_e0 & 8) != 0) goto LAB_047da4ac;
LAB_047da4bb:
      lVar24 = lVar20;
      auStack_1c8[0] = (char)pbVar26 * '\x02';
      puVar17 = (undefined8 *)(auStack_1c8 + 1);
    }
    else {
      lVar24 = lStack_118;
      uStack_120 = uStack_e8;
      if (uStack_e8 < uStack_110) {
        uStack_e8 = uStack_110;
        uStack_120 = uStack_110;
      }
LAB_047da4ac:
      pbVar26 = (byte *)(uStack_120 - lVar24);
      if ((long)pbVar26 < 0) {
LAB_047da68c:
                    /* WARNING: Does not return */
        pcVar4 = (code *)invalidInstructionException();
        (*pcVar4)();
      }
      lVar20 = lVar24;
      if (pbVar26 < (byte *)0x17) goto LAB_047da4bb;
      uVar16 = ((ulong)pbVar26 & 0x7ffffffffffffff8) + 8;
      uVar21 = 0x1a;
      if (uVar16 != 0x18) {
        uVar21 = uVar16;
      }
      uStack_1d0 = 0x47da4f3;
      puVar17 = (undefined8 *)func_0x04b078f0(uVar21);
      auStack_1c8 = (undefined1  [8])(uVar21 | 1);
      pbStack_1c0 = pbVar26;
      puStack_1b8 = puVar17;
    }
    uStack_1d0 = 0x47da516;
    func_0x04b07a30(puVar17,lVar24,pbVar26);
    *(byte *)((long)puVar17 + (long)pbVar26) = 0;
    uStack_1a8 = 0x6f725016;
    uStack_1a4 = 0x75626f74;
    uStack_1a0._0_4_ = 0x43505266;
    uStack_1a0._4_4_ = uStack_1a0._4_4_ & 0xffffff00;
    uStack_1d0 = 0x47da550;
    func_0x02acb490(auStack_1c8,2,&uStack_1a8,0);
  }
  if ((uStack_1a8 & 1) != 0) {
    uStack_1d0 = 0x47da56a;
    func_0x04a8cfc8(lStack_198,CONCAT44(uStack_1a4,uStack_1a8) & 0xfffffffffffffffe);
  }
  if (((ulong)auStack_1c8 & 1) != 0) {
    uStack_1d0 = 0x47da582;
    func_0x04a8cfc8(puStack_1b8,(ulong)auStack_1c8 & 0xfffffffffffffffe);
  }
  uStack_158 = _UNK_04c9f9f0;
  *(undefined8 *)((long)&uStack_158 + *(long *)(_UNK_04c9f9f0 - 0x18)) = _UNK_04c9fa30;
  lStack_148 = _UNK_04c9fa38;
  puStack_140 = &UNK_04c9f668;
  if ((bStack_100 & 1) != 0) {
    uStack_1d0 = 0x47da5e0;
    func_0x04a8cfc8(uStack_f0,CONCAT71(uStack_ff,bStack_100) & 0xfffffffffffffffe);
  }
  puStack_140 = &UNK_04c9f338;
  uStack_1d0 = 0x47da600;
  func_0x04aea9f0(auStack_138);
  uStack_1d0 = 0x47da611;
  func_0x04abbce8(&uStack_158,&UNK_04c9f9f8);
  uStack_1d0 = 0x47da61e;
  func_0x04ab7708(auStack_d8);
LAB_047da61e:
  uStack_1d0 = 0x47da626;
  func_0x047d9750(param_1);
  return;
}


// FUNCTION 047d9130

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void FUN_047d9130(undefined8 *param_1)

{
  byte bVar1;
  ushort uVar2;
  ushort *puVar3;
  undefined8 uVar4;
  long *plVar5;
  code *pcVar6;
  ulong uVar7;
  undefined4 uVar8;
  undefined4 uVar9;
  undefined4 uVar10;
  char cVar11;
  undefined8 *puVar12;
  undefined1 *puVar13;
  undefined4 *puVar14;
  long lVar15;
  undefined8 unaff_RBX;
  long lVar16;
  long *plVar17;
  byte *pbVar18;
  undefined8 unaff_R12;
  undefined8 unaff_R14;
  long lVar19;
  long *unaff_R15;

  do {
    *(long **)((long)register0x00000020 + -8) = unaff_R15;
    *(undefined8 *)((long)register0x00000020 + -0x10) = unaff_R14;
    *(undefined8 *)((long)register0x00000020 + -0x18) = unaff_R12;
    *(undefined8 *)((long)register0x00000020 + -0x20) = unaff_RBX;
    if (*(char *)(param_1 + 0x18) == '\x01') {
      lVar15 = param_1[10];
      *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d915a;
      puVar12 = (undefined8 *)func_0x04b078f0(0x28);
      uVar4 = _UNK_006e52d4;
      *puVar12 = _DAT_006e52cc;
      puVar12[1] = uVar4;
      uVar10 = _UNK_006e52e8;
      uVar9 = _UNK_006e52e4;
      uVar8 = _UNK_006e52e0;
      *(undefined4 *)(puVar12 + 2) = _DAT_006e52dc;
      *(undefined4 *)((long)puVar12 + 0x14) = uVar8;
      *(undefined4 *)(puVar12 + 3) = uVar9;
      *(undefined4 *)((long)puVar12 + 0x1c) = uVar10;
      *(undefined4 *)((long)puVar12 + 0x1f) = 0x676e696c;
      *(undefined1 *)((long)puVar12 + 0x23) = 0;
      *(undefined4 *)(lVar15 + 8) = 4;
      if ((*(byte *)(lVar15 + 0x10) & 1) == 0) {
        *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d9353;
        func_0x01a126b0(lVar15 + 0x10,puVar12,0x23);
      }
      else {
        *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d91a1;
        func_0x01a125a0(lVar15 + 0x10,puVar12,0x23);
      }
      *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d9360;
      func_0x04a8cfc8(puVar12,0x28);
      plVar5 = (long *)*param_1;
      *(undefined **)((long)register0x00000020 + -0x88) = &UNK_04c877c0;
      *(undefined8 **)((long)register0x00000020 + -0x80) = param_1;
      plVar17 = (long *)((long)register0x00000020 + -0x88);
      *(long **)((long)register0x00000020 + -0x68) = plVar17;
      pcVar6 = *(code **)(*plVar5 + 0x70);
      *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d9387;
      (*pcVar6)(plVar5,plVar17);
      plVar5 = *(long **)((long)register0x00000020 + -0x68);
      if (plVar5 == plVar17) {
        lVar15 = 0x20;
      }
      else {
        lVar15 = 0x28;
        if (plVar5 == (long *)0x0) {
          return;
        }
      }
      pcVar6 = *(code **)(*plVar5 + lVar15);
      *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d93ab;
      (*pcVar6)();
      return;
    }
    *(undefined8 *)((long)register0x00000020 + -0xa8) = 0;
    *(undefined8 *)((long)register0x00000020 + -0xa0) = 0;
    *(undefined8 *)((long)register0x00000020 + -0x98) = 0;
    *(undefined **)((long)register0x00000020 + -0x88) = &UNK_04c87a00;
    *(code **)((long)register0x00000020 + -0x80) = FUN_047d9ac0;
    *(undefined8 *)((long)register0x00000020 + -0x78) = 0;
    *(undefined8 **)((long)register0x00000020 + -0x70) = param_1;
    unaff_R15 = (long *)((long)register0x00000020 + -0x88);
    *(long **)((long)register0x00000020 + -0x68) = unaff_R15;
    *(undefined8 *)((long)register0x00000020 + -200) = 0;
    *(undefined8 *)((long)register0x00000020 + -0xc0) = 0;
    *(undefined8 *)((long)register0x00000020 + -0xb8) = 0;
    *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d91fe;
    puVar13 = (undefined1 *)func_0x04b078f0(0x408);
    *puVar13 = 0;
    *(undefined8 *)((long)register0x00000020 + -200) = 0x409;
    *(undefined8 *)((long)register0x00000020 + -0xc0) = 0;
    *(undefined1 **)((long)register0x00000020 + -0xb8) = puVar13;
    uVar2 = **(ushort **)(param_1[7] + 8);
    if (uVar2 < 0x80) {
      *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d923d;
      func_0x04aac620((undefined1 *)((long)register0x00000020 + -200),1,(int)(short)uVar2);
      puVar3 = *(ushort **)(param_1[7] + 8);
      uVar2 = *puVar3;
      *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d9259;
      func_0x04aac7b0((undefined1 *)((long)register0x00000020 + -200),~(ulong)uVar2 + (long)puVar3);
      uVar4 = param_1[8];
      *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d9262;
      cVar11 = func_0x04401bb0(uVar4);
      if (cVar11 == '\0') {
LAB_047d92f5:
        lVar15 = param_1[10];
        *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d9303;
        puVar14 = (undefined4 *)func_0x04b078f0(0x28);
        uVar4 = _UNK_0071e5a9;
        *(undefined8 *)(puVar14 + 4) = _DAT_0071e5a1;
        *(undefined8 *)(puVar14 + 6) = uVar4;
        uVar10 = _UNK_0071e59d;
        uVar9 = _UNK_0071e599;
        uVar8 = _UNK_0071e595;
        *puVar14 = _DAT_0071e591;
        puVar14[1] = uVar8;
        puVar14[2] = uVar9;
        puVar14[3] = uVar10;
        *(undefined2 *)(puVar14 + 8) = 0x72;
        *(undefined4 *)(lVar15 + 8) = 3;
        if ((*(byte *)(lVar15 + 0x10) & 1) == 0) {
          *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d93bd;
          func_0x01a126b0(lVar15 + 0x10,puVar14,0x21);
        }
        else {
          *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d9344;
          func_0x01a125a0(lVar15 + 0x10,puVar14,0x21);
        }
        *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d93ca;
        func_0x04a8cfc8(puVar14,0x28);
        plVar17 = (long *)*param_1;
        *(undefined **)((long)register0x00000020 + -0x58) = &UNK_04c87840;
        *(undefined8 **)((long)register0x00000020 + -0x50) = param_1;
        *(undefined1 **)((long)register0x00000020 + -0x38) =
             (undefined1 *)((long)register0x00000020 + -0x58);
        pcVar6 = *(code **)(*plVar17 + 0x70);
        *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d93f4;
        (*pcVar6)(plVar17,(undefined1 *)((long)register0x00000020 + -0x58));
LAB_047d93f4:
        plVar17 = *(long **)((long)register0x00000020 + -0x38);
        if (plVar17 == (long *)((long)register0x00000020 + -0x58)) {
          lVar15 = 0x20;
        }
        else {
          lVar15 = 0x28;
          if (plVar17 == (long *)0x0) {
            bVar1 = *(byte *)((long)register0x00000020 + -200);
            goto joined_r0x047d9502;
          }
        }
        pcVar6 = *(code **)(*plVar17 + lVar15);
        *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d94fe;
        (*pcVar6)();
        bVar1 = *(byte *)((long)register0x00000020 + -200);
      }
      else {
        uVar4 = param_1[8];
        *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d9276;
        cVar11 = func_0x04402e10(uVar4,(undefined1 *)((long)register0x00000020 + -200));
        if (cVar11 == '\0') goto LAB_047d92f5;
        puVar12 = param_1 + 0x11;
        *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d9289;
        func_0x04ab5980(puVar12);
        if (*(char *)((long)param_1 + 0x59) == '\x01') {
          *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d929b;
          func_0x04ab59b0(puVar12);
          lVar15 = param_1[10];
          *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d92a9;
          puVar14 = (undefined4 *)func_0x04b078f0(0x1a);
          uVar10 = _UNK_0077bfc8;
          uVar9 = _UNK_0077bfc4;
          uVar8 = _UNK_0077bfc0;
          *puVar14 = _DAT_0077bfbc;
          puVar14[1] = uVar8;
          puVar14[2] = uVar9;
          puVar14[3] = uVar10;
          *(undefined8 *)((long)puVar14 + 0xf) = 0x6c656e6e61686320;
          *(undefined1 *)((long)puVar14 + 0x17) = 0;
          *(undefined4 *)(lVar15 + 8) = 2;
          if ((*(byte *)(lVar15 + 0x10) & 1) == 0) {
            *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d9580;
            func_0x01a126b0(lVar15 + 0x10,puVar14,0x17);
          }
          else {
            *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d92f0;
            func_0x01a125a0(lVar15 + 0x10,puVar14,0x17);
          }
          *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d958d;
          func_0x04a8cfc8(puVar14,0x1a);
          plVar17 = (long *)*param_1;
          *(undefined **)((long)register0x00000020 + -0x58) = &UNK_04c878c0;
          *(undefined8 **)((long)register0x00000020 + -0x50) = param_1;
          *(undefined1 **)((long)register0x00000020 + -0x38) =
               (undefined1 *)((long)register0x00000020 + -0x58);
          pcVar6 = *(code **)(*plVar17 + 0x70);
          *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d95b7;
          (*pcVar6)(plVar17,(undefined1 *)((long)register0x00000020 + -0x58));
          goto LAB_047d93f4;
        }
        uVar7 = param_1[0xf];
        lVar15 = param_1[0xc];
        if ((ulong)((param_1[0xd] - lVar15 >> 3) * -0x5555555555555555) <= uVar7) {
                    /* WARNING: Does not return */
          pcVar6 = (code *)invalidInstructionException();
          (*pcVar6)();
        }
        plVar17 = (long *)*param_1;
        pcVar6 = *(code **)(*plVar17 + 0x18);
        *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d955c;
        (*pcVar6)(plVar17,lVar15 + uVar7 * 0x18,(undefined1 *)((long)register0x00000020 + -0xa8),
                  (undefined1 *)((long)register0x00000020 + -200),&UNK_00fc2c28,
                  (undefined1 *)((long)register0x00000020 + -0x88));
        *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d9564;
        func_0x04ab59b0(puVar12);
        bVar1 = *(byte *)((long)register0x00000020 + -200);
      }
joined_r0x047d9502:
      if ((bVar1 & 1) != 0) {
        *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d942b;
        func_0x04a8cfc8(*(undefined8 *)((long)register0x00000020 + -0xb8),
                        *(ulong *)((long)register0x00000020 + -200) & 0xfffffffffffffffe);
      }
      plVar17 = *(long **)((long)register0x00000020 + -0x68);
      if (plVar17 == unaff_R15) {
        lVar15 = 0x20;
      }
      else {
        lVar15 = 0x28;
        if (plVar17 == (long *)0x0) {
          lVar15 = *(long *)((long)register0x00000020 + -0xa8);
          goto joined_r0x047d9461;
        }
      }
      pcVar6 = *(code **)(*plVar17 + lVar15);
      *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d9459;
      (*pcVar6)();
      lVar15 = *(long *)((long)register0x00000020 + -0xa8);
joined_r0x047d9461:
      if (lVar15 == 0) {
        return;
      }
      lVar19 = *(long *)((long)register0x00000020 + -0xa0);
      lVar16 = lVar15;
      if (lVar15 != lVar19) {
        do {
          if ((*(byte *)(lVar19 + -0x18) & 1) == 0) {
            bVar1 = *(byte *)(lVar19 + -0x30);
          }
          else {
            uVar7 = *(ulong *)(lVar19 + -0x18);
            uVar4 = *(undefined8 *)(lVar19 + -8);
            *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d94a0;
            func_0x04a8cfc8(uVar4,uVar7 & 0xfffffffffffffffe);
            bVar1 = *(byte *)(lVar19 + -0x30);
          }
          if ((bVar1 & 1) != 0) {
            uVar7 = *(ulong *)(lVar19 + -0x30);
            uVar4 = *(undefined8 *)(lVar19 + -0x20);
            *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d94cb;
            func_0x04a8cfc8(uVar4,uVar7 & 0xfffffffffffffffe);
          }
          lVar19 = lVar19 + -0x30;
        } while (lVar19 != lVar15);
        lVar16 = *(long *)((long)register0x00000020 + -0xa8);
      }
      *(long *)((long)register0x00000020 + -0xa0) = lVar15;
      *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d94e4;
      func_0x04a8cfc8(lVar16,*(long *)((long)register0x00000020 + -0x98) - lVar16);
      return;
    }
    *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d95c8;
    puVar12 = (undefined8 *)func_0x04a8d0a0(8);
    *puVar12 = &UNK_04c879d8;
    *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d95e8;
    puVar12 = (undefined8 *)func_0x04a8d290(puVar12,&UNK_04c879b0,&SUB_04a8b490);
    *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d95f3;
    func_0x04ab59b0();
    if ((*(byte *)((long)register0x00000020 + -200) & 1) == 0) {
      plVar17 = *(long **)((long)register0x00000020 + -0x68);
      lVar15 = 0x20;
      if (plVar17 != unaff_R15) goto LAB_047d972b;
LAB_047d9735:
      pcVar6 = *(code **)(*plVar17 + lVar15);
      *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d973b;
      (*pcVar6)();
    }
    else {
      *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d9667;
      func_0x04a8cfc8(*(undefined8 *)((long)register0x00000020 + -0xb8));
      plVar17 = *(long **)((long)register0x00000020 + -0x68);
      lVar15 = 0x20;
      if (plVar17 == unaff_R15) goto LAB_047d9735;
LAB_047d972b:
      lVar15 = 0x28;
      if (plVar17 != (long *)0x0) goto LAB_047d9735;
    }
    *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d9745;
    func_0x01a21140((undefined1 *)((long)register0x00000020 + -0xa8));
    *(undefined8 *)((long)register0x00000020 + -0xd0) = 0x47d974d;
    param_1 = puVar12;
    func_0x04b07a10();
    *(undefined8 **)((long)register0x00000020 + -0xd0) = puVar12;
    lVar15 = 0;
    if ((ulong)param_1[0xf] < ((long)(param_1[0xd] - param_1[0xc]) >> 3) * -0x5555555555555555 - 1U)
    {
      lVar15 = param_1[0xf] + 1;
    }
    param_1[0xf] = lVar15;
    if (lVar15 == param_1[0x10]) {
      lVar15 = param_1[10];
      *(undefined1 *)((long)register0x00000020 + -0xe8) = 0x24;
      *(undefined4 *)((long)register0x00000020 + -0xe7) = _DAT_00574025;
      *(undefined4 *)((long)register0x00000020 + -0xe3) = _UNK_00574029;
      *(undefined4 *)((long)register0x00000020 + -0xdf) = _UNK_0057402d;
      *(undefined4 *)((long)register0x00000020 + -0xdb) = _UNK_00574031;
      *(undefined2 *)((long)register0x00000020 + -0xd7) = 0x6465;
      *(undefined1 *)((long)register0x00000020 + -0xd5) = 0;
      *(undefined4 *)(lVar15 + 8) = 1;
      pbVar18 = (byte *)(lVar15 + 0x10);
      if (pbVar18 != (byte *)((long)register0x00000020 + -0xe8)) {
        if ((*pbVar18 & 1) == 0) {
          *(undefined8 *)(lVar15 + 0x20) = *(undefined8 *)((long)register0x00000020 + -0xd8);
          uVar8 = *(undefined4 *)((long)register0x00000020 + -0xe4);
          uVar9 = *(undefined4 *)((long)register0x00000020 + -0xe0);
          uVar10 = *(undefined4 *)((long)register0x00000020 + -0xdc);
          *(undefined4 *)pbVar18 = *(undefined4 *)((long)register0x00000020 + -0xe8);
          *(undefined4 *)(lVar15 + 0x14) = uVar8;
          *(undefined4 *)(lVar15 + 0x18) = uVar9;
          *(undefined4 *)(lVar15 + 0x1c) = uVar10;
        }
        else {
          *(undefined8 *)((long)register0x00000020 + -0xf8) = 0x47d97dd;
          func_0x01a125a0(pbVar18,(undefined1 *)((long)register0x00000020 + -0xe7),0x12);
          if ((*(byte *)((long)register0x00000020 + -0xe8) & 1) != 0) {
            *(undefined8 *)((long)register0x00000020 + -0xf8) = 0x47d97f7;
            func_0x04a8cfc8(*(undefined8 *)((long)register0x00000020 + -0xd8),
                            *(ulong *)((long)register0x00000020 + -0xe8) & 0xfffffffffffffffe);
          }
        }
      }
      *(undefined8 *)((long)register0x00000020 + -0xf8) = 0x47d981f;
      func_0x047d9850(param_1);
      return;
    }
    unaff_RBX = *(undefined8 *)((long)register0x00000020 + -0xd0);
    register0x00000020 = (BADSPACEBASE *)((long)register0x00000020 + -200);
  } while( true );
}


// FUNCTION 0261e070

void FUN_0261e070(undefined8 *param_1,byte *param_2,byte *param_3,undefined8 *param_4)

{
  byte bVar1;
  code *pcVar2;
  undefined4 uVar3;
  undefined4 uVar4;
  undefined4 uVar5;
  undefined8 *puVar6;
  undefined8 uVar7;
  ulong uVar8;
  long lVar9;
  byte *pbVar10;
  byte *pbVar11;
  byte *pbVar12;
  long lVar13;
  bool bVar14;
  byte *pbStack_70;
  undefined8 *puStack_68;
  byte *pbStack_60;
  undefined8 *puStack_58;
  undefined8 *puStack_50;
  byte **ppbStack_48;
  byte **ppbStack_40;
  undefined1 uStack_38;

  puStack_58 = param_1 + 2;
  pbVar10 = (byte *)*param_1;
  lVar13 = param_1[2];
  lVar9 = lVar13 - (long)pbVar10;
  if ((ulong)((lVar9 >> 3) * -0x5555555555555555) < param_4) {
    if (pbVar10 != (byte *)0x0) {
      pbVar12 = (byte *)param_1[1];
      pbVar11 = pbVar10;
      puStack_68 = param_4;
      if (pbVar10 != pbVar12) {
        do {
          if ((pbVar12[-0x18] & 1) != 0) {
            func_0x04a8cfc8(*(undefined8 *)(pbVar12 + -8),
                            *(ulong *)(pbVar12 + -0x18) & 0xfffffffffffffffe);
          }
          pbVar12 = pbVar12 + -0x18;
        } while (pbVar12 != pbVar10);
        lVar9 = param_1[2] - (long)*param_1;
        pbVar11 = (byte *)*param_1;
      }
      param_1[1] = pbVar10;
      func_0x04a8cfc8(pbVar11,lVar9);
      *param_1 = 0;
      param_1[1] = 0;
      param_1[2] = 0;
      lVar13 = 0;
      param_4 = puStack_68;
    }
    if (param_4 < 0xaaaaaaaaaaaaaab) {
      uVar8 = (lVar13 >> 3) * 0x5555555555555556;
      if (param_4 < uVar8) {
        param_4 = (undefined8 *)uVar8;
      }
      if (0x555555555555554 < (ulong)((lVar13 >> 3) * -0x5555555555555555)) {
        param_4 = (undefined8 *)0xaaaaaaaaaaaaaaa;
      }
      if (param_4 < 0xaaaaaaaaaaaaaab) {
        pbStack_70 = (byte *)func_0x04b078f0((long)param_4 * 0x18);
        *param_1 = pbStack_70;
        param_1[1] = pbStack_70;
        param_1[2] = pbStack_70 + (long)param_4 * 0x18;
        ppbStack_48 = &pbStack_60;
        ppbStack_40 = &pbStack_70;
        uStack_38 = 0;
        pbStack_60 = pbStack_70;
        puStack_50 = puStack_58;
        for (; param_2 != param_3; param_2 = param_2 + 0x18) {
          if ((*param_2 & 1) == 0) {
            *(undefined8 *)(pbStack_70 + 0x10) = *(undefined8 *)(param_2 + 0x10);
            uVar3 = *(undefined4 *)(param_2 + 4);
            uVar4 = *(undefined4 *)(param_2 + 8);
            uVar5 = *(undefined4 *)(param_2 + 0xc);
            *(undefined4 *)pbStack_70 = *(undefined4 *)param_2;
            *(undefined4 *)(pbStack_70 + 4) = uVar3;
            *(undefined4 *)(pbStack_70 + 8) = uVar4;
            *(undefined4 *)(pbStack_70 + 0xc) = uVar5;
          }
          else {
            func_0x019f86e0(pbStack_70,*(undefined8 *)(param_2 + 0x10),*(undefined8 *)(param_2 + 8))
            ;
          }
          pbStack_70 = pbStack_70 + 0x18;
        }
        param_1[1] = pbStack_70;
        return;
      }
    }
    uVar7 = func_0x019f8920();
    func_0x019f8930(&puStack_50);
    *(undefined8 **)(param_2 + 8) = param_1;
    uVar7 = func_0x04b07a10(uVar7);
    func_0x019f8930(&puStack_50);
    param_1[1] = pbVar10;
    func_0x04b07a10(uVar7);
    pcVar2 = (code *)swi(3);
    (*pcVar2)();
    return;
  }
  pbVar12 = (byte *)param_1[1];
  lVar13 = (long)pbVar12 - (long)pbVar10;
  puStack_68 = param_1;
  if ((ulong)((lVar13 >> 3) * -0x5555555555555555) < param_4) {
    if (pbVar12 != pbVar10) {
      pbVar11 = param_2 + lVar13;
      do {
        if (param_2 != pbVar10) {
          bVar1 = *param_2;
          if ((*pbVar10 & 1) == 0) {
            if ((bVar1 & 1) == 0) {
              *(undefined8 *)(pbVar10 + 0x10) = *(undefined8 *)(param_2 + 0x10);
              uVar3 = *(undefined4 *)(param_2 + 4);
              uVar4 = *(undefined4 *)(param_2 + 8);
              uVar5 = *(undefined4 *)(param_2 + 0xc);
              *(undefined4 *)pbVar10 = *(undefined4 *)param_2;
              *(undefined4 *)(pbVar10 + 4) = uVar3;
              *(undefined4 *)(pbVar10 + 8) = uVar4;
              *(undefined4 *)(pbVar10 + 0xc) = uVar5;
            }
            else {
              func_0x01a126b0(pbVar10,*(undefined8 *)(param_2 + 0x10),*(undefined8 *)(param_2 + 8));
            }
          }
          else {
            bVar14 = (bVar1 & 1) != 0;
            pbVar12 = param_2 + 1;
            if (bVar14) {
              pbVar12 = *(byte **)(param_2 + 0x10);
            }
            uVar8 = (ulong)(bVar1 >> 1);
            if (bVar14) {
              uVar8 = *(ulong *)(param_2 + 8);
            }
            func_0x01a125a0(pbVar10,pbVar12,uVar8);
          }
        }
        param_2 = param_2 + 0x18;
        pbVar10 = pbVar10 + 0x18;
        lVar13 = lVar13 + -0x18;
      } while (lVar13 != 0);
      pbVar12 = (byte *)puStack_68[1];
      param_2 = pbVar11;
    }
    puVar6 = puStack_68;
    puStack_50 = puStack_58;
    ppbStack_48 = &pbStack_60;
    ppbStack_40 = &pbStack_70;
    uStack_38 = 0;
    pbStack_70 = pbVar12;
    pbStack_60 = pbVar12;
    if (param_2 != param_3) {
      do {
        if ((*param_2 & 1) == 0) {
          *(undefined8 *)(pbStack_70 + 0x10) = *(undefined8 *)(param_2 + 0x10);
          uVar3 = *(undefined4 *)(param_2 + 4);
          uVar4 = *(undefined4 *)(param_2 + 8);
          uVar5 = *(undefined4 *)(param_2 + 0xc);
          *(undefined4 *)pbStack_70 = *(undefined4 *)param_2;
          *(undefined4 *)(pbStack_70 + 4) = uVar3;
          *(undefined4 *)(pbStack_70 + 8) = uVar4;
          *(undefined4 *)(pbStack_70 + 0xc) = uVar5;
        }
        else {
          func_0x019f86e0(pbStack_70,*(undefined8 *)(param_2 + 0x10),*(undefined8 *)(param_2 + 8));
        }
        param_2 = param_2 + 0x18;
        pbStack_70 = pbStack_70 + 0x18;
      } while (param_2 != param_3);
      puVar6[1] = pbStack_70;
      return;
    }
    puStack_68[1] = pbVar12;
    return;
  }
  pbVar11 = pbVar10;
  if (param_2 == param_3) {
    if (pbVar10 == pbVar12) goto LAB_0261e3eb;
  }
  else {
    do {
      if (param_2 != pbVar11) {
        bVar1 = *param_2;
        if ((*pbVar11 & 1) == 0) {
          if ((bVar1 & 1) == 0) {
            *(undefined8 *)(pbVar11 + 0x10) = *(undefined8 *)(param_2 + 0x10);
            uVar3 = *(undefined4 *)(param_2 + 4);
            uVar4 = *(undefined4 *)(param_2 + 8);
            uVar5 = *(undefined4 *)(param_2 + 0xc);
            *(undefined4 *)pbVar11 = *(undefined4 *)param_2;
            *(undefined4 *)(pbVar11 + 4) = uVar3;
            *(undefined4 *)(pbVar11 + 8) = uVar4;
            *(undefined4 *)(pbVar11 + 0xc) = uVar5;
          }
          else {
            func_0x01a126b0(pbVar11,*(undefined8 *)(param_2 + 0x10),*(undefined8 *)(param_2 + 8));
          }
        }
        else {
          bVar14 = (bVar1 & 1) != 0;
          pbVar12 = param_2 + 1;
          if (bVar14) {
            pbVar12 = *(byte **)(param_2 + 0x10);
          }
          uVar8 = (ulong)(bVar1 >> 1);
          if (bVar14) {
            uVar8 = *(ulong *)(param_2 + 8);
          }
          func_0x01a125a0(pbVar11,pbVar12,uVar8);
        }
      }
      param_2 = param_2 + 0x18;
      pbVar11 = pbVar11 + 0x18;
      pbVar10 = pbVar10 + 0x18;
    } while (param_2 != param_3);
    pbVar12 = (byte *)param_1[1];
    if (pbVar11 == pbVar12) goto LAB_0261e3eb;
  }
  do {
    if ((pbVar12[-0x18] & 1) != 0) {
      func_0x04a8cfc8(*(undefined8 *)(pbVar12 + -8),*(ulong *)(pbVar12 + -0x18) & 0xfffffffffffffffe
                     );
    }
    pbVar12 = pbVar12 + -0x18;
  } while (pbVar12 != pbVar11);
LAB_0261e3eb:
  param_1[1] = pbVar10;
  return;
}


// FUNCTION 02b1fa70

long * FUN_02b1fa70(long *param_1,undefined8 *param_2)

{
  long lVar1;
  undefined4 uVar2;
  undefined4 uVar3;
  undefined4 uVar4;
  long lVar5;
  long lVar6;

  lVar1 = *param_1;
  if (lVar1 != 0) {
    lVar6 = param_1[1];
    lVar5 = lVar1;
    if (lVar1 != lVar6) {
      do {
        if ((*(byte *)(lVar6 + -0x18) & 1) != 0) {
          func_0x04a8cfc8(*(undefined8 *)(lVar6 + -8),*(ulong *)(lVar6 + -0x18) & 0xfffffffffffffffe
                         );
        }
        lVar6 = lVar6 + -0x18;
      } while (lVar6 != lVar1);
      lVar5 = *param_1;
    }
    param_1[1] = lVar1;
    func_0x04a8cfc8(lVar5,param_1[2] - lVar5);
    *param_1 = 0;
    param_1[1] = 0;
    param_1[2] = 0;
  }
  uVar2 = *(undefined4 *)((long)param_2 + 4);
  uVar3 = *(undefined4 *)(param_2 + 1);
  uVar4 = *(undefined4 *)((long)param_2 + 0xc);
  *(undefined4 *)param_1 = *(undefined4 *)param_2;
  *(undefined4 *)((long)param_1 + 4) = uVar2;
  *(undefined4 *)(param_1 + 1) = uVar3;
  *(undefined4 *)((long)param_1 + 0xc) = uVar4;
  param_1[2] = param_2[2];
  *param_2 = 0;
  param_2[1] = 0;
  param_2[2] = 0;
  lVar1 = param_1[3];
  if (lVar1 != 0) {
    lVar6 = param_1[4];
    lVar5 = lVar1;
    if (lVar1 != lVar6) {
      do {
        if ((*(byte *)(lVar6 + -0x18) & 1) != 0) {
          func_0x04a8cfc8(*(undefined8 *)(lVar6 + -8),*(ulong *)(lVar6 + -0x18) & 0xfffffffffffffffe
                         );
        }
        lVar6 = lVar6 + -0x18;
      } while (lVar6 != lVar1);
      lVar5 = param_1[3];
    }
    param_1[4] = lVar1;
    func_0x04a8cfc8(lVar5,param_1[5] - lVar5);
    param_1[3] = 0;
    param_1[4] = 0;
    param_1[5] = 0;
  }
  uVar2 = *(undefined4 *)((long)param_2 + 0x1c);
  uVar3 = *(undefined4 *)(param_2 + 4);
  uVar4 = *(undefined4 *)((long)param_2 + 0x24);
  *(undefined4 *)(param_1 + 3) = *(undefined4 *)(param_2 + 3);
  *(undefined4 *)((long)param_1 + 0x1c) = uVar2;
  *(undefined4 *)(param_1 + 4) = uVar3;
  *(undefined4 *)((long)param_1 + 0x24) = uVar4;
  param_1[5] = param_2[5];
  param_2[3] = 0;
  param_2[4] = 0;
  param_2[5] = 0;
  lVar1 = param_1[6];
  if (lVar1 != 0) {
    lVar6 = param_1[7];
    lVar5 = lVar1;
    if (lVar1 != lVar6) {
      do {
        if ((*(byte *)(lVar6 + -0x18) & 1) != 0) {
          func_0x04a8cfc8(*(undefined8 *)(lVar6 + -8),*(ulong *)(lVar6 + -0x18) & 0xfffffffffffffffe
                         );
        }
        lVar6 = lVar6 + -0x18;
      } while (lVar6 != lVar1);
      lVar5 = param_1[6];
    }
    param_1[7] = lVar1;
    func_0x04a8cfc8(lVar5,param_1[8] - lVar5);
    param_1[6] = 0;
    param_1[7] = 0;
    param_1[8] = 0;
  }
  uVar2 = *(undefined4 *)((long)param_2 + 0x34);
  uVar3 = *(undefined4 *)(param_2 + 7);
  uVar4 = *(undefined4 *)((long)param_2 + 0x3c);
  *(undefined4 *)(param_1 + 6) = *(undefined4 *)(param_2 + 6);
  *(undefined4 *)((long)param_1 + 0x34) = uVar2;
  *(undefined4 *)(param_1 + 7) = uVar3;
  *(undefined4 *)((long)param_1 + 0x3c) = uVar4;
  param_1[8] = param_2[8];
  param_2[6] = 0;
  param_2[7] = 0;
  param_2[8] = 0;
  lVar1 = param_1[9];
  if (lVar1 != 0) {
    lVar6 = param_1[10];
    lVar5 = lVar1;
    if (lVar1 != lVar6) {
      do {
        if ((*(byte *)(lVar6 + -0x18) & 1) != 0) {
          func_0x04a8cfc8(*(undefined8 *)(lVar6 + -8),*(ulong *)(lVar6 + -0x18) & 0xfffffffffffffffe
                         );
        }
        lVar6 = lVar6 + -0x18;
      } while (lVar6 != lVar1);
      lVar5 = param_1[9];
    }
    param_1[10] = lVar1;
    func_0x04a8cfc8(lVar5,param_1[0xb] - lVar5);
    param_1[9] = 0;
    param_1[10] = 0;
    param_1[0xb] = 0;
  }
  uVar2 = *(undefined4 *)((long)param_2 + 0x4c);
  uVar3 = *(undefined4 *)(param_2 + 10);
  uVar4 = *(undefined4 *)((long)param_2 + 0x54);
  *(undefined4 *)(param_1 + 9) = *(undefined4 *)(param_2 + 9);
  *(undefined4 *)((long)param_1 + 0x4c) = uVar2;
  *(undefined4 *)(param_1 + 10) = uVar3;
  *(undefined4 *)((long)param_1 + 0x54) = uVar4;
  param_1[0xb] = param_2[0xb];
  param_2[9] = 0;
  param_2[10] = 0;
  param_2[0xb] = 0;
  lVar1 = param_1[0xc];
  if (lVar1 != 0) {
    lVar6 = param_1[0xd];
    lVar5 = lVar1;
    if (lVar1 != lVar6) {
      do {
        if ((*(byte *)(lVar6 + -0x18) & 1) != 0) {
          func_0x04a8cfc8(*(undefined8 *)(lVar6 + -8),*(ulong *)(lVar6 + -0x18) & 0xfffffffffffffffe
                         );
        }
        lVar6 = lVar6 + -0x18;
      } while (lVar6 != lVar1);
      lVar5 = param_1[0xc];
    }
    param_1[0xd] = lVar1;
    func_0x04a8cfc8(lVar5,param_1[0xe] - lVar5);
    param_1[0xc] = 0;
    param_1[0xd] = 0;
    param_1[0xe] = 0;
  }
  uVar2 = *(undefined4 *)((long)param_2 + 100);
  uVar3 = *(undefined4 *)(param_2 + 0xd);
  uVar4 = *(undefined4 *)((long)param_2 + 0x6c);
  *(undefined4 *)(param_1 + 0xc) = *(undefined4 *)(param_2 + 0xc);
  *(undefined4 *)((long)param_1 + 100) = uVar2;
  *(undefined4 *)(param_1 + 0xd) = uVar3;
  *(undefined4 *)((long)param_1 + 0x6c) = uVar4;
  param_1[0xe] = param_2[0xe];
  param_2[0xc] = 0;
  param_2[0xd] = 0;
  param_2[0xe] = 0;
  lVar1 = param_1[0xf];
  if (lVar1 != 0) {
    lVar6 = param_1[0x10];
    lVar5 = lVar1;
    if (lVar1 != lVar6) {
      do {
        if ((*(byte *)(lVar6 + -0x18) & 1) != 0) {
          func_0x04a8cfc8(*(undefined8 *)(lVar6 + -8),*(ulong *)(lVar6 + -0x18) & 0xfffffffffffffffe
                         );
        }
        lVar6 = lVar6 + -0x18;
      } while (lVar6 != lVar1);
      lVar5 = param_1[0xf];
    }
    param_1[0x10] = lVar1;
    func_0x04a8cfc8(lVar5,param_1[0x11] - lVar5);
    param_1[0xf] = 0;
    param_1[0x10] = 0;
    param_1[0x11] = 0;
  }
  uVar2 = *(undefined4 *)((long)param_2 + 0x7c);
  uVar3 = *(undefined4 *)(param_2 + 0x10);
  uVar4 = *(undefined4 *)((long)param_2 + 0x84);
  *(undefined4 *)(param_1 + 0xf) = *(undefined4 *)(param_2 + 0xf);
  *(undefined4 *)((long)param_1 + 0x7c) = uVar2;
  *(undefined4 *)(param_1 + 0x10) = uVar3;
  *(undefined4 *)((long)param_1 + 0x84) = uVar4;
  param_1[0x11] = param_2[0x11];
  param_2[0xf] = 0;
  param_2[0x10] = 0;
  param_2[0x11] = 0;
  lVar1 = param_1[0x12];
  if (lVar1 != 0) {
    lVar6 = param_1[0x13];
    lVar5 = lVar1;
    if (lVar1 != lVar6) {
      do {
        if ((*(byte *)(lVar6 + -0x18) & 1) != 0) {
          func_0x04a8cfc8(*(undefined8 *)(lVar6 + -8),*(ulong *)(lVar6 + -0x18) & 0xfffffffffffffffe
                         );
        }
        lVar6 = lVar6 + -0x18;
      } while (lVar6 != lVar1);
      lVar5 = param_1[0x12];
    }
    param_1[0x13] = lVar1;
    func_0x04a8cfc8(lVar5,param_1[0x14] - lVar5);
    param_1[0x12] = 0;
    param_1[0x13] = 0;
    param_1[0x14] = 0;
  }
  uVar2 = *(undefined4 *)((long)param_2 + 0x94);
  uVar3 = *(undefined4 *)(param_2 + 0x13);
  uVar4 = *(undefined4 *)((long)param_2 + 0x9c);
  *(undefined4 *)(param_1 + 0x12) = *(undefined4 *)(param_2 + 0x12);
  *(undefined4 *)((long)param_1 + 0x94) = uVar2;
  *(undefined4 *)(param_1 + 0x13) = uVar3;
  *(undefined4 *)((long)param_1 + 0x9c) = uVar4;
  param_1[0x14] = param_2[0x14];
  param_2[0x12] = 0;
  param_2[0x13] = 0;
  param_2[0x14] = 0;
  lVar1 = param_1[0x15];
  if (lVar1 != 0) {
    lVar6 = param_1[0x16];
    lVar5 = lVar1;
    if (lVar1 != lVar6) {
      do {
        if ((*(byte *)(lVar6 + -0x18) & 1) != 0) {
          func_0x04a8cfc8(*(undefined8 *)(lVar6 + -8),*(ulong *)(lVar6 + -0x18) & 0xfffffffffffffffe
                         );
        }
        lVar6 = lVar6 + -0x18;
      } while (lVar6 != lVar1);
      lVar5 = param_1[0x15];
    }
    param_1[0x16] = lVar1;
    func_0x04a8cfc8(lVar5,param_1[0x17] - lVar5);
    param_1[0x15] = 0;
    param_1[0x16] = 0;
    param_1[0x17] = 0;
  }
  uVar2 = *(undefined4 *)((long)param_2 + 0xac);
  uVar3 = *(undefined4 *)(param_2 + 0x16);
  uVar4 = *(undefined4 *)((long)param_2 + 0xb4);
  *(undefined4 *)(param_1 + 0x15) = *(undefined4 *)(param_2 + 0x15);
  *(undefined4 *)((long)param_1 + 0xac) = uVar2;
  *(undefined4 *)(param_1 + 0x16) = uVar3;
  *(undefined4 *)((long)param_1 + 0xb4) = uVar4;
  param_1[0x17] = param_2[0x17];
  param_2[0x15] = 0;
  param_2[0x16] = 0;
  param_2[0x17] = 0;
  lVar1 = param_1[0x18];
  if (lVar1 != 0) {
    lVar6 = param_1[0x19];
    lVar5 = lVar1;
    if (lVar1 != lVar6) {
      do {
        if ((*(byte *)(lVar6 + -0x18) & 1) != 0) {
          func_0x04a8cfc8(*(undefined8 *)(lVar6 + -8),*(ulong *)(lVar6 + -0x18) & 0xfffffffffffffffe
                         );
        }
        lVar6 = lVar6 + -0x18;
      } while (lVar6 != lVar1);
      lVar5 = param_1[0x18];
    }
    param_1[0x19] = lVar1;
    func_0x04a8cfc8(lVar5,param_1[0x1a] - lVar5);
    param_1[0x18] = 0;
    param_1[0x19] = 0;
    param_1[0x1a] = 0;
  }
  uVar2 = *(undefined4 *)((long)param_2 + 0xc4);
  uVar3 = *(undefined4 *)(param_2 + 0x19);
  uVar4 = *(undefined4 *)((long)param_2 + 0xcc);
  *(undefined4 *)(param_1 + 0x18) = *(undefined4 *)(param_2 + 0x18);
  *(undefined4 *)((long)param_1 + 0xc4) = uVar2;
  *(undefined4 *)(param_1 + 0x19) = uVar3;
  *(undefined4 *)((long)param_1 + 0xcc) = uVar4;
  param_1[0x1a] = param_2[0x1a];
  param_2[0x18] = 0;
  param_2[0x19] = 0;
  param_2[0x1a] = 0;
  lVar1 = param_1[0x1b];
  if (lVar1 != 0) {
    lVar6 = param_1[0x1c];
    lVar5 = lVar1;
    if (lVar1 != lVar6) {
      do {
        if ((*(byte *)(lVar6 + -0x18) & 1) != 0) {
          func_0x04a8cfc8(*(undefined8 *)(lVar6 + -8),*(ulong *)(lVar6 + -0x18) & 0xfffffffffffffffe
                         );
        }
        lVar6 = lVar6 + -0x18;
      } while (lVar6 != lVar1);
      lVar5 = param_1[0x1b];
    }
    param_1[0x1c] = lVar1;
    func_0x04a8cfc8(lVar5,param_1[0x1d] - lVar5);
    param_1[0x1b] = 0;
    param_1[0x1c] = 0;
    param_1[0x1d] = 0;
  }
  uVar2 = *(undefined4 *)((long)param_2 + 0xdc);
  uVar3 = *(undefined4 *)(param_2 + 0x1c);
  uVar4 = *(undefined4 *)((long)param_2 + 0xe4);
  *(undefined4 *)(param_1 + 0x1b) = *(undefined4 *)(param_2 + 0x1b);
  *(undefined4 *)((long)param_1 + 0xdc) = uVar2;
  *(undefined4 *)(param_1 + 0x1c) = uVar3;
  *(undefined4 *)((long)param_1 + 0xe4) = uVar4;
  param_1[0x1d] = param_2[0x1d];
  param_2[0x1b] = 0;
  param_2[0x1c] = 0;
  param_2[0x1d] = 0;
  lVar1 = param_1[0x1e];
  if (lVar1 != 0) {
    lVar6 = param_1[0x1f];
    lVar5 = lVar1;
    if (lVar1 != lVar6) {
      do {
        if ((*(byte *)(lVar6 + -0x18) & 1) != 0) {
          func_0x04a8cfc8(*(undefined8 *)(lVar6 + -8),*(ulong *)(lVar6 + -0x18) & 0xfffffffffffffffe
                         );
        }
        lVar6 = lVar6 + -0x18;
      } while (lVar6 != lVar1);
      lVar5 = param_1[0x1e];
    }
    param_1[0x1f] = lVar1;
    func_0x04a8cfc8(lVar5,param_1[0x20] - lVar5);
    param_1[0x1e] = 0;
    param_1[0x1f] = 0;
    param_1[0x20] = 0;
  }
  lVar1 = param_2[0x1f];
  param_1[0x1e] = param_2[0x1e];
  param_1[0x1f] = lVar1;
  param_1[0x20] = param_2[0x20];
  param_2[0x1e] = 0;
  param_2[0x1f] = 0;
  param_2[0x20] = 0;
  return param_1;
}

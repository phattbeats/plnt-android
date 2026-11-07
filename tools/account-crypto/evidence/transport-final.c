// FUNCTION 02b207e0 bounds 02b214df

ulong * FUN_02b207e0(undefined8 *param_1,undefined8 param_2,undefined4 *param_3,long *param_4,
                    undefined8 param_5,undefined8 *param_6,undefined8 *param_7,undefined1 param_8)

{
  long *plVar1;
  long *plVar2;
  byte bVar3;
  ulong uVar4;
  code *pcVar5;
  undefined4 uVar6;
  undefined4 uVar7;
  undefined4 uVar8;
  int iVar9;
  long lVar10;
  ulong uVar11;
  undefined8 *puVar12;
  long *plVar13;
  undefined8 uVar14;
  long lVar15;
  long *plVar16;
  undefined8 uVar17;
  undefined8 *puVar18;
  ulong *puVar19;
  byte *pbVar20;
  ulong uVar21;
  ulong uVar22;
  ulong uVar23;
  ulong *puVar24;
  ulong *puVar25;
  ulong uVar26;
  ulong uVar27;
  bool bVar28;
  long *plStack_a8;
  undefined4 uStack_a0;
  undefined4 uStack_9c;
  long *plStack_98;
  long *plStack_90;
  long **pplStack_78;
  undefined8 uStack_60;
  undefined8 uStack_58;
  undefined8 uStack_50;
  undefined4 uStack_48;
  undefined4 uStack_44;
  undefined4 uStack_40;
  undefined4 uStack_3c;

  *param_1 = &UNK_04b73808;
  param_1[1] = &UNK_04b73ab0;
  uStack_58 = param_2;
  uStack_50 = param_5;
  puVar12 = (undefined8 *)func_0x04b078f0(0x70);
  puVar12[1] = 0;
  puVar12[2] = 0;
  *puVar12 = &UNK_04b75130;
  func_0x02b7f8a0(puVar12 + 3);
  param_1[2] = puVar12 + 3;
  param_1[3] = puVar12;
  param_1[4] = 0;
  param_1[5] = 0;
  param_1[6] = 0;
  param_1[7] = 0;
  param_1[8] = 0;
  param_1[9] = 0;
  lVar15 = *param_4;
  *param_4 = 0;
  param_1[10] = lVar15;
  plVar13 = (long *)func_0x04b078f0(8);
  lVar15 = *(long *)(lVar15 + 8);
  *plVar13 = lVar15;
  LOCK();
  plVar1 = (long *)(lVar15 + 0xd8);
  *plVar1 = *plVar1 + 1;
  UNLOCK();
  param_1[0xb] = plVar13;
  uVar17 = param_1[10];
  uVar14 = func_0x04b078f0(8);
  func_0x048400c0(uVar14,uVar17,10);
  param_1[0xc] = uVar14;
  *(undefined2 *)(param_1 + 0xd) = 0;
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
  param_1[0x18] = 0;
  lVar15 = *(long *)(param_3 + 2);
  uVar6 = param_3[1];
  uVar7 = param_3[2];
  uVar8 = param_3[3];
  *(undefined4 *)(param_1 + 0x19) = *param_3;
  *(undefined4 *)((long)param_1 + 0xcc) = uVar6;
  *(undefined4 *)(param_1 + 0x1a) = uVar7;
  *(undefined4 *)((long)param_1 + 0xd4) = uVar8;
  if (lVar15 != 0) {
    LOCK();
    plVar1 = (long *)(lVar15 + 8);
    *plVar1 = *plVar1 + 1;
    UNLOCK();
  }
  uVar17 = param_1[10];
  uVar14 = func_0x04b078f0(0x1f0);
  plStack_98 = (long *)param_1[2];
  plStack_90 = (long *)param_1[3];
  if (param_1[3] != 0) {
    LOCK();
    plVar1 = (long *)(param_1[3] + 8);
    *plVar1 = *plVar1 + 1;
    UNLOCK();
  }
  plStack_a8 = (long *)param_1[0x19];
  uStack_a0 = *(undefined4 *)(param_1 + 0x1a);
  uStack_9c = *(undefined4 *)((long)param_1 + 0xd4);
  if (param_1[0x1a] != 0) {
    LOCK();
    plVar1 = (long *)(param_1[0x1a] + 8);
    *plVar1 = *plVar1 + 1;
    UNLOCK();
  }
  func_0x02b58ff0(uVar14,uVar17,&plStack_98,param_1,&plStack_a8);
  param_1[0x1b] = uVar14;
  plVar1 = (long *)CONCAT44(uStack_9c,uStack_a0);
  if (plVar1 != (long *)0x0) {
    LOCK();
    plVar13 = plVar1 + 1;
    lVar15 = *plVar13;
    *plVar13 = *plVar13 + -1;
    UNLOCK();
    if (lVar15 == 0) {
      (**(code **)(*plVar1 + 0x10))(plVar1);
      func_0x04aa8780(plVar1);
    }
  }
  plVar1 = plStack_90;
  if (plStack_90 != (long *)0x0) {
    LOCK();
    plVar13 = plStack_90 + 1;
    lVar15 = *plVar13;
    *plVar13 = *plVar13 + -1;
    UNLOCK();
    if (lVar15 == 0) {
      (**(code **)(*plStack_90 + 0x10))(plStack_90);
      func_0x04aa8780(plVar1);
    }
  }
  plVar1 = param_1 + 1;
  param_1[0x20] = 0;
  param_1[0x21] = 0;
  param_1[0x1e] = 0;
  param_1[0x1f] = 0;
  param_1[0x1c] = 0;
  param_1[0x1d] = 0;
  uVar17 = *param_6;
  *param_6 = 0;
  param_1[0x22] = uVar17;
  param_1[0x23] = 0;
  param_1[0x24] = 0;
  param_1[0x27] = param_7[2];
  uVar17 = param_7[1];
  param_1[0x25] = *param_7;
  param_1[0x26] = uVar17;
  *param_7 = 0;
  param_7[1] = 0;
  param_7[2] = 0;
  param_1[0x2a] = param_7[5];
  uVar17 = param_7[4];
  param_1[0x28] = param_7[3];
  param_1[0x29] = uVar17;
  param_7[3] = 0;
  param_7[4] = 0;
  param_7[5] = 0;
  param_1[0x2d] = param_7[8];
  uVar17 = param_7[7];
  param_1[0x2b] = param_7[6];
  param_1[0x2c] = uVar17;
  param_7[6] = 0;
  param_7[7] = 0;
  param_7[8] = 0;
  param_1[0x30] = param_7[0xb];
  uVar6 = *(undefined4 *)((long)param_7 + 0x4c);
  uVar7 = *(undefined4 *)(param_7 + 10);
  uVar8 = *(undefined4 *)((long)param_7 + 0x54);
  *(undefined4 *)(param_1 + 0x2e) = *(undefined4 *)(param_7 + 9);
  *(undefined4 *)((long)param_1 + 0x174) = uVar6;
  *(undefined4 *)(param_1 + 0x2f) = uVar7;
  *(undefined4 *)((long)param_1 + 0x17c) = uVar8;
  param_7[9] = 0;
  param_7[10] = 0;
  param_7[0xb] = 0;
  uStack_48 = *(undefined4 *)(param_1 + 0x19);
  uStack_44 = *(undefined4 *)((long)param_1 + 0xcc);
  uStack_40 = *(undefined4 *)(param_1 + 0x1a);
  uStack_3c = *(undefined4 *)((long)param_1 + 0xd4);
  if (param_1[0x1a] != 0) {
    LOCK();
    plVar13 = (long *)(param_1[0x1a] + 8);
    *plVar13 = *plVar13 + 1;
    UNLOCK();
  }
  func_0x02bb72c0(param_1 + 0x31,&uStack_48,plVar1);
  plVar13 = (long *)CONCAT44(uStack_3c,uStack_40);
  if (plVar13 != (long *)0x0) {
    LOCK();
    plVar16 = plVar13 + 1;
    lVar15 = *plVar16;
    *plVar16 = *plVar16 + -1;
    UNLOCK();
    if (lVar15 == 0) {
      (**(code **)(*plVar13 + 0x10))(plVar13);
      func_0x04aa8780(plVar13);
    }
  }
  *(undefined1 *)(param_1 + 0x34) = param_8;
  *(undefined4 *)((long)param_1 + 0x1a4) = 0;
  lVar15 = func_0x04b078f0(0x1c8);
  func_0x04b07ba0(lVar15,0,0x1c8);
  func_0x04ab6070(lVar15);
  func_0x047d2ec0(lVar15 + 0x90,0);
  *(undefined1 *)(lVar15 + 0x1c0) = 0;
  param_1[0x35] = lVar15;
  param_1[0x36] = uStack_58;
  param_1[0x37] = 0;
  param_1[0x38] = 0;
  param_1[0x39] = 0;
  param_1[0x3a] = 0;
  param_1[0x3b] = 0;
  param_1[0x3c] = 0;
  param_1[0x3d] = &UNK_04b77590;
  *(undefined4 *)((long)param_1 + 0x204) = 0;
  *(undefined1 *)(param_1 + 0x41) = 0;
  param_1[0x3e] = 0;
  param_1[0x3f] = 0;
  *(undefined1 *)(param_1 + 0x40) = 0;
  *(undefined4 *)((long)param_1 + 0x20c) = 0xffffffff;
  param_1[0x42] = uStack_50;
  param_1[0x43] = 0;
  param_1[0x44] = 0;
  param_1[0x45] = 0;
  param_1[0x46] = 0;
  param_1[0x48] = &UNK_04b75180;
  param_1[0x49] = param_1;
  param_1[0x4c] = param_1 + 0x48;
  param_1[0x4e] = &UNK_04b75308;
  param_1[0x4f] = param_1;
  param_1[0x52] = param_1 + 0x4e;
  param_1[0x54] = &UNK_04b75490;
  param_1[0x55] = param_1;
  param_1[0x58] = param_1 + 0x54;
  param_1[0x5a] = &UNK_04b75510;
  param_1[0x5b] = param_1;
  param_1[0x5e] = param_1 + 0x5a;
  param_1[0x60] = &UNK_04b75698;
  param_1[0x61] = param_1;
  param_1[100] = param_1 + 0x60;
  param_1[0x66] = &UNK_04b75718;
  param_1[0x67] = param_1;
  param_1[0x6a] = param_1 + 0x66;
  param_1[0x6c] = &UNK_04b75798;
  param_1[0x6d] = param_1;
  param_1[0x70] = param_1 + 0x6c;
  param_1[0x72] = &UNK_04b75818;
  param_1[0x73] = param_1;
  param_1[0x76] = param_1 + 0x72;
  param_1[0x78] = &UNK_04b759a0;
  param_1[0x79] = param_1;
  param_1[0x7c] = param_1 + 0x78;
  param_1[0x7e] = &UNK_04b75a20;
  param_1[0x7f] = param_1;
  param_1[0x82] = param_1 + 0x7e;
  param_1[0x84] = &UNK_04b75aa0;
  param_1[0x85] = param_1;
  param_1[0x88] = param_1 + 0x84;
  param_1[0x8a] = &UNK_04b75b20;
  param_1[0x8b] = param_1;
  param_1[0x8e] = param_1 + 0x8a;
  param_1[0x90] = &UNK_04b75bb0;
  param_1[0x91] = param_1;
  param_1[0x94] = param_1 + 0x90;
  param_1[0x96] = &UNK_04b75c30;
  param_1[0x97] = param_1;
  param_1[0x9a] = param_1 + 0x96;
  param_1[0x9c] = &UNK_04b75cb0;
  param_1[0x9d] = param_1;
  param_1[0xa0] = param_1 + 0x9c;
  param_1[0xa2] = &UNK_04b75d30;
  param_1[0xa3] = param_1;
  param_1[0xa6] = param_1 + 0xa2;
  param_1[0xac] = 0;
  plVar13 = (long *)func_0x04b078f0(8);
  func_0x04ab6620(plVar13);
  plStack_98 = plVar13;
  plStack_a8 = (long *)func_0x04b078f0(0x10);
  plStack_98 = (long *)0x0;
  *plStack_a8 = (long)plVar13;
  plStack_a8[1] = (long)param_1;
  iVar9 = func_0x04b07f30(&uStack_60,0,&UNK_02b3e600,plStack_a8);
  if (iVar9 != 0) {
    func_0x04ab4980(iVar9,&UNK_0057483f);
LAB_02b213b2:
    uVar17 = func_0x04a8b200();
    lVar15 = 0x20;
    if ((pplStack_78 == (long **)plVar1) || (lVar15 = 0x28, pplStack_78 != (long **)0x0)) {
      (**(code **)((long)*pplStack_78 + lVar15))();
    }
    func_0x04a8cfc8(plVar13,0xf0);
    uVar17 = func_0x019f83c0(uVar17);
    func_0x04a8cfc8(plVar13,0x10);
    uVar17 = func_0x019f83c0(uVar17);
    plVar1 = plStack_98;
    plStack_98 = (long *)0x0;
    if (plVar1 != (long *)0x0) {
      (**(code **)(*plVar1 + 8))();
    }
    func_0x04a8cfc8(puVar12,8);
    uVar17 = func_0x019f83c0(uVar17);
    func_0x02b3e700(&plStack_a8);
    func_0x01a60c20(&plStack_98);
    uVar17 = func_0x019f83c0(uVar17);
    func_0x01a60c20(&plStack_98);
    uVar17 = func_0x019f83c0(uVar17);
    func_0x04a8cfc8(plVar13,8);
    uVar17 = func_0x019f83c0(uVar17);
    func_0x04aa86e0(puVar12);
    puVar24 = (ulong *)0x70;
    func_0x04a8cfc8(puVar12);
    lVar15 = func_0x019f83c0(uVar17);
    puVar19 = (ulong *)func_0x019f83c0();
    puVar25 = (ulong *)(lVar15 + 0x218);
    if (puVar25 != puVar24) {
      bVar3 = (byte)*puVar24;
      if ((*(byte *)puVar25 & 1) != 0) {
        bVar28 = (bVar3 & 1) != 0;
        pbVar20 = (byte *)((long)puVar24 + 1);
        if (bVar28) {
          pbVar20 = (byte *)puVar24[2];
        }
        uVar23 = (ulong)(bVar3 >> 1);
        if (bVar28) {
          uVar23 = puVar24[1];
        }
        uVar4 = *puVar25;
        uVar26 = uVar4 & 0xfffffffffffffffe;
        if (uVar23 < uVar26) {
          lVar10 = *(long *)(lVar15 + 0x228);
          *(ulong *)(lVar15 + 0x220) = uVar23;
          func_0x04b07a30(lVar10,pbVar20,uVar23);
          *(undefined1 *)(lVar10 + uVar23) = 0;
        }
        else {
          if (0xfffffffffffffff6 < uVar23) {
            func_0x019f8660();
            pcVar5 = (code *)swi(3);
            puVar19 = (ulong *)(*pcVar5)();
            return puVar19;
          }
          uVar21 = 0x16;
          if ((uVar4 & 1) != 0) {
            uVar21 = uVar26 - 1;
          }
          uVar27 = uVar21 * 2;
          if (uVar21 * 2 < uVar23) {
            uVar27 = uVar23;
          }
          uVar11 = 0xfffffffffffffff8;
          if (uVar21 < 0x7ffffffffffffff4) {
            uVar11 = (uVar27 & 0xfffffffffffffff8) + 8;
          }
          uVar21 = 0x1a;
          if (uVar11 != 0x18) {
            uVar21 = uVar11;
          }
          lVar10 = func_0x04b078f0(uVar21);
          uVar17 = *(undefined8 *)(lVar15 + 0x228);
          if (uVar23 != 0) {
            func_0x04b07ad0(lVar10,pbVar20,uVar23);
          }
          *(undefined1 *)(lVar10 + uVar23) = 0;
          if ((uVar4 & 1) != 0) {
            func_0x04a8cfc8(uVar17,uVar26);
          }
          *puVar25 = uVar21 | 1;
          *(ulong *)(lVar15 + 0x220) = uVar23;
          *(long *)(lVar15 + 0x228) = lVar10;
        }
        return puVar25;
      }
      if ((bVar3 & 1) != 0) {
        uVar23 = puVar24[1];
        uVar4 = puVar24[2];
        if (uVar23 < 0x17) {
          *(char *)puVar25 = (char)uVar23 * '\x02';
          func_0x04b07a30(lVar15 + 0x219,uVar4,uVar23);
          *(undefined1 *)(lVar15 + 0x219 + uVar23) = 0;
        }
        else {
          if (0xfffffffffffffff6 < uVar23) {
            func_0x019f8660();
            pcVar5 = (code *)swi(3);
            puVar19 = (ulong *)(*pcVar5)();
            return puVar19;
          }
          uVar26 = *puVar25;
          uVar27 = uVar26 & 0xfffffffffffffffe;
          uVar21 = 0x16;
          if ((*(byte *)puVar25 & 1) != 0) {
            uVar21 = uVar27 - 1;
          }
          uVar11 = uVar21 * 2;
          if (uVar21 * 2 < uVar23) {
            uVar11 = uVar23;
          }
          uVar22 = 0xfffffffffffffff8;
          if (uVar21 < 0x7ffffffffffffff4) {
            uVar22 = (uVar11 & 0xfffffffffffffff8) + 8;
          }
          uVar21 = 0x1a;
          if (uVar22 != 0x18) {
            uVar21 = uVar22;
          }
          lVar10 = func_0x04b078f0(uVar21);
          uVar17 = *(undefined8 *)(lVar15 + 0x228);
          func_0x04b07ad0(lVar10,uVar4,uVar23);
          *(undefined1 *)(lVar10 + uVar23) = 0;
          if ((uVar26 & 1) != 0) {
            func_0x04a8cfc8(uVar17,uVar27);
          }
          *puVar25 = uVar21 | 1;
          *(ulong *)(lVar15 + 0x220) = uVar23;
          *(long *)(lVar15 + 0x228) = lVar10;
        }
        return puVar25;
      }
      puVar19 = (ulong *)puVar24[2];
      *(ulong **)(lVar15 + 0x228) = puVar19;
      uVar23 = puVar24[1];
      *puVar25 = *puVar24;
      *(ulong *)(lVar15 + 0x220) = uVar23;
    }
    return puVar19;
  }
  if (param_1[9] != 0) goto LAB_02b213b2;
  param_1[9] = uStack_60;
  uStack_60 = 0;
  func_0x04ab62d0(&uStack_60);
  func_0x02b63890(param_1 + 0x3d);
  plVar16 = (long *)func_0x04b078f0(0x58);
  lVar15 = param_1[0x19];
  plVar13 = (long *)param_1[0x1a];
  if (plVar13 == (long *)0x0) {
    *plVar16 = (long)&UNK_04b77b28;
    plVar16[1] = 0;
    plVar16[2] = 0;
    plVar16[3] = 0;
    plVar16[4] = 0;
    plVar16[5] = 0;
    plVar16[6] = lVar15;
    plVar16[7] = 0;
    plVar16[8] = (long)&UNK_04b77590;
    plVar16[9] = 0;
    plVar16[10] = 0;
  }
  else {
    LOCK();
    plVar13[1] = plVar13[1] + 1;
    UNLOCK();
    *plVar16 = (long)&UNK_04b77b28;
    plVar16[1] = 0;
    plVar16[2] = 0;
    plVar16[3] = 0;
    plVar16[4] = 0;
    plVar16[5] = 0;
    plVar16[6] = lVar15;
    plVar16[7] = (long)plVar13;
    LOCK();
    plVar13[1] = plVar13[1] + 1;
    UNLOCK();
    plVar16[8] = (long)&UNK_04b77590;
    plVar16[9] = 0;
    plVar16[10] = 0;
    LOCK();
    plVar2 = plVar13 + 1;
    lVar15 = *plVar2;
    *plVar2 = *plVar2 + -1;
    UNLOCK();
    if (lVar15 == 0) {
      (**(code **)(*plVar13 + 0x10))(plVar13);
      func_0x04aa8780(plVar13);
    }
  }
  uVar17 = func_0x04b078f0(8);
  plStack_98 = plVar16;
  func_0x02b76bf0(uVar17,&plStack_98);
  plVar13 = plStack_98;
  plStack_98 = (long *)0x0;
  if (plVar13 != (long *)0x0) {
    (**(code **)(*plVar13 + 8))();
  }
  plVar13 = (long *)param_1[0x1f];
  param_1[0x1f] = uVar17;
  if (plVar13 != (long *)0x0) {
    plVar16 = (long *)*plVar13;
    *plVar13 = 0;
    if (plVar16 != (long *)0x0) {
      (**(code **)(*plVar16 + 8))();
    }
    func_0x04a8cfc8(plVar13,8);
  }
  puVar12 = (undefined8 *)func_0x04b078f0(0x10);
  *puVar12 = &UNK_04b7a2b0;
  uVar17 = func_0x02b78510();
  puVar12[1] = uVar17;
  puVar18 = (undefined8 *)func_0x04b078f0(8);
  *puVar18 = puVar12;
  plVar13 = (long *)param_1[0x20];
  param_1[0x20] = puVar18;
  if (plVar13 != (long *)0x0) {
    plVar16 = (long *)*plVar13;
    *plVar13 = 0;
    if (plVar16 != (long *)0x0) {
      (**(code **)(*plVar16 + 8))();
    }
    func_0x04a8cfc8(plVar13,8);
  }
  puVar12 = (undefined8 *)func_0x04b078f0(0x50);
  puVar12[8] = 0;
  puVar12[9] = 0;
  puVar12[6] = 0;
  puVar12[7] = 0;
  puVar12[4] = 0;
  puVar12[5] = 0;
  puVar12[2] = 0;
  puVar12[3] = 0;
  *puVar12 = 0;
  puVar12[1] = 0;
  lVar15 = param_1[0x21];
  param_1[0x21] = puVar12;
  if (lVar15 != 0) {
    plVar13 = *(long **)(lVar15 + 0x48);
    if (plVar13 == (long *)0x0) {
LAB_02b21061:
      bVar3 = *(byte *)(lVar15 + 8);
    }
    else {
      LOCK();
      plVar16 = plVar13 + 1;
      lVar10 = *plVar16;
      *plVar16 = *plVar16 + -1;
      UNLOCK();
      if (lVar10 != 0) goto LAB_02b21061;
      (**(code **)(*plVar13 + 0x10))(plVar13);
      func_0x04aa8780(plVar13);
      bVar3 = *(byte *)(lVar15 + 8);
    }
    if ((bVar3 & 1) != 0) {
      func_0x04a8cfc8(*(undefined8 *)(lVar15 + 0x18),*(ulong *)(lVar15 + 8) & 0xfffffffffffffffe);
    }
    func_0x04a8cfc8(lVar15,0x50);
  }
  uVar17 = func_0x028ba3a0();
  plStack_98 = (long *)func_0x04b078f0(0x10);
  *plStack_98 = (long)&UNK_04b75030;
  plStack_98[1] = (long)plVar1;
  func_0x028ba1c0(uVar17,&plStack_98);
  plVar1 = plStack_98;
  plStack_98 = (long *)0x0;
  if (plVar1 != (long *)0x0) {
    (**(code **)(*plVar1 + 8))();
  }
  uVar17 = param_1[10];
  lVar15 = func_0x04b078f0(0xf0);
  plStack_98 = (long *)&UNK_04b73b40;
  plStack_90 = param_1;
  pplStack_78 = &plStack_98;
  func_0x0470ba90(lVar15,uVar17,&plStack_98);
  *(undefined4 *)(lVar15 + 0xe4) = 0;
  if (pplStack_78 == &plStack_98) {
    puVar19 = (ulong *)0x20;
LAB_02b2114f:
    puVar19 = (ulong *)(**(code **)((long)*pplStack_78 + (long)puVar19))();
    lVar10 = param_1[0x46];
    param_1[0x46] = lVar15;
    if (lVar10 == 0) goto LAB_02b2113b;
LAB_02b21168:
    plVar1 = *(long **)(lVar10 + 0xd0);
    if (plVar1 == (long *)(lVar10 + 0xb0)) {
      lVar15 = 0x20;
LAB_02b2118e:
      (**(code **)(*plVar1 + lVar15))();
    }
    else {
      lVar15 = 0x28;
      if (plVar1 != (long *)0x0) goto LAB_02b2118e;
    }
    func_0x01d9db10(lVar10 + 0x28);
    func_0x04ab5a60(lVar10);
    puVar19 = (ulong *)func_0x04a8cfc8(lVar10,0xf0);
    lVar15 = param_1[0x22];
  }
  else {
    puVar19 = (ulong *)0x28;
    if (pplStack_78 != (long **)0x0) goto LAB_02b2114f;
    lVar10 = param_1[0x46];
    param_1[0x46] = lVar15;
    if (lVar10 != 0) goto LAB_02b21168;
LAB_02b2113b:
    lVar15 = param_1[0x22];
  }
  if (lVar15 == 0) {
    return puVar19;
  }
  func_0x02bc1c60(&plStack_a8);
  plStack_98 = (long *)&UNK_04b73d40;
  plStack_90 = param_1;
  pplStack_78 = &plStack_98;
  func_0x027cb9a0(&plStack_98,plStack_a8 + 2);
  if (pplStack_78 == &plStack_98) {
    lVar15 = 0x20;
LAB_02b2120a:
    (**(code **)((long)*pplStack_78 + lVar15))();
  }
  else {
    lVar15 = 0x28;
    if (pplStack_78 != (long **)0x0) goto LAB_02b2120a;
  }
  plStack_98 = (long *)&UNK_04b73dc0;
  plStack_90 = param_1;
  pplStack_78 = &plStack_98;
  func_0x027cb9a0(&plStack_98,plStack_a8 + 8);
  if (pplStack_78 == &plStack_98) {
    lVar15 = 0x20;
LAB_02b21251:
    (**(code **)((long)*pplStack_78 + lVar15))();
  }
  else {
    lVar15 = 0x28;
    if (pplStack_78 != (long **)0x0) goto LAB_02b21251;
  }
  plStack_98 = (long *)&UNK_04b73e40;
  plStack_90 = param_1;
  pplStack_78 = &plStack_98;
  func_0x027cb9a0(&plStack_98,plStack_a8 + 0xe);
  if (pplStack_78 == &plStack_98) {
    lVar15 = 0x20;
LAB_02b21298:
    (**(code **)((long)*pplStack_78 + lVar15))();
  }
  else {
    lVar15 = 0x28;
    if (pplStack_78 != (long **)0x0) goto LAB_02b21298;
  }
  plStack_98 = (long *)&UNK_04b73ec0;
  plStack_90 = param_1;
  pplStack_78 = &plStack_98;
  func_0x02b51280(&plStack_98,plStack_a8 + 0x14);
  if (pplStack_78 == &plStack_98) {
    lVar15 = 0x20;
LAB_02b212e0:
    (**(code **)((long)*pplStack_78 + lVar15))();
  }
  else {
    lVar15 = 0x28;
    if (pplStack_78 != (long **)0x0) goto LAB_02b212e0;
  }
  plVar13 = plStack_a8 + 0x1a;
  plVar1 = (long *)param_1[0xa6];
  if (plVar1 == (long *)0x0) {
    pplStack_78 = (long **)0x0;
  }
  else if (plVar1 == param_1 + 0xa2) {
    pplStack_78 = &plStack_98;
    (**(code **)(*plVar1 + 0x18))(plVar1,&plStack_98);
  }
  else {
    pplStack_78 = (long **)(**(code **)(*plVar1 + 0x10))();
  }
  func_0x025643d0(&plStack_98,plVar13);
  if (pplStack_78 == &plStack_98) {
    lVar15 = 0x20;
  }
  else {
    lVar15 = 0x28;
    if (pplStack_78 == (long **)0x0) goto LAB_02b2136e;
  }
  (**(code **)((long)*pplStack_78 + lVar15))();
LAB_02b2136e:
  puVar19 = (ulong *)(**(code **)(*(long *)param_1[0x22] + 0x10))((long *)param_1[0x22],&plStack_a8)
  ;
  plVar1 = plStack_a8;
  plStack_a8 = (long *)0x0;
  if (plVar1 != (long *)0x0) {
    puVar19 = (ulong *)(**(code **)(*plVar1 + 0x10))();
  }
  return puVar19;
}


// FUNCTION 047de6e0 bounds 047de83f

void FUN_047de6e0(long param_1,undefined8 param_2,undefined2 param_3)

{
  undefined8 uVar1;

  FUN_0480cd70();
  *(undefined8 *)(param_1 + 0x10) = 0;
  *(undefined8 *)(param_1 + 0x18) = 0;
  *(undefined8 *)(param_1 + 0x20) = 0;
  *(undefined8 *)(param_1 + 0x30) = 0;
  *(undefined8 *)(param_1 + 0x38) = 0;
  *(long *)(param_1 + 0x28) = param_1 + 0x30;
  uVar1 = func_0x04ab41f0();
  *(undefined8 *)(param_1 + 0x40) = 0;
  *(undefined8 *)(param_1 + 0x48) = 0;
  *(undefined8 *)(param_1 + 0x4c) = 0;
  *(undefined8 *)(param_1 + 0x54) = 0;
  *(undefined8 *)(param_1 + 0x60) = uVar1;
  *(undefined8 *)(param_1 + 0x68) = 0;
  *(undefined8 *)(param_1 + 0x70) = 0;
  *(undefined8 *)(param_1 + 0x78) = 0;
  *(undefined8 *)(param_1 + 0xa0) = 0;
  *(undefined8 *)(param_1 + 0xf0) = 0;
  *(undefined8 *)(param_1 + 0xf8) = 0;
  *(undefined8 *)(param_1 + 0xb0) = 0;
  *(undefined8 *)(param_1 + 0xb8) = 0;
  *(undefined8 *)(param_1 + 0xc0) = 0;
  *(undefined8 *)(param_1 + 200) = 0;
  *(undefined8 *)(param_1 + 0xd0) = 0;
  *(undefined8 *)(param_1 + 0xd8) = 0;
  *(undefined8 *)(param_1 + 0xe0) = 0;
  *(long *)(param_1 + 0xe8) = param_1 + 0xf0;
  *(undefined1 *)(param_1 + 0x120) = 0;
  *(undefined8 *)(param_1 + 0x109) = 0;
  *(undefined8 *)(param_1 + 0x111) = 0;
  *(undefined8 *)(param_1 + 0x100) = 0;
  *(undefined8 *)(param_1 + 0x108) = 0;
  *(undefined1 *)(param_1 + 0x118) = 1;
  *(undefined8 *)(param_1 + 0x150) = 0x300000;
  *(undefined2 *)(param_1 + 0x148) = param_3;
  func_0x019fa7c0(param_1 + 0x100,&UNK_008c1330);
  return;
}


// FUNCTION 047de460 bounds 047de57f

void FUN_047de460(long *param_1,long param_2)

{
  undefined4 uVar1;
  undefined4 uVar2;
  undefined4 uVar3;
  long lVar4;
  long *plVar5;
  long *plVar6;

  *param_1 = (long)(param_1 + 1);
  param_1[1] = 0;
  param_1[2] = 0;
  if (*(long *)(param_2 + 0x10) != 0) {
    plVar5 = (long *)func_0x025a7b00(param_1,*(undefined8 *)(param_2 + 8),param_1);
    param_1[1] = (long)plVar5;
    plVar5[2] = (long)(param_1 + 1);
    do {
      plVar6 = plVar5;
      plVar5 = (long *)*plVar6;
    } while (plVar5 != (long *)0x0);
    *param_1 = (long)plVar6;
    param_1[2] = *(long *)(param_2 + 0x10);
  }
  if ((*(byte *)(param_2 + 0x18) & 1) == 0) {
    param_1[5] = *(long *)(param_2 + 0x28);
    uVar1 = *(undefined4 *)(param_2 + 0x1c);
    uVar2 = *(undefined4 *)(param_2 + 0x20);
    uVar3 = *(undefined4 *)(param_2 + 0x24);
    *(undefined4 *)(param_1 + 3) = *(undefined4 *)(param_2 + 0x18);
    *(undefined4 *)((long)param_1 + 0x1c) = uVar1;
    *(undefined4 *)(param_1 + 4) = uVar2;
    *(undefined4 *)((long)param_1 + 0x24) = uVar3;
  }
  else {
    func_0x019f86e0(param_1 + 3,*(undefined8 *)(param_2 + 0x28),*(undefined8 *)(param_2 + 0x20));
  }
  *(undefined1 *)(param_1 + 6) = *(undefined1 *)(param_2 + 0x30);
  *(undefined1 *)(param_1 + 7) = 0;
  if (*(char *)(param_2 + 0x38) == '\x01') {
    if ((*(byte *)(param_2 + 0x40) & 1) == 0) {
      param_1[10] = *(long *)(param_2 + 0x50);
      lVar4 = *(long *)(param_2 + 0x48);
      param_1[8] = *(long *)(param_2 + 0x40);
      param_1[9] = lVar4;
    }
    else {
      func_0x019f86e0(param_1 + 8,*(undefined8 *)(param_2 + 0x50),*(undefined8 *)(param_2 + 0x48));
    }
    *(undefined2 *)(param_1 + 0xb) = *(undefined2 *)(param_2 + 0x58);
    *(undefined1 *)(param_1 + 7) = 1;
  }
  lVar4 = *(long *)(param_2 + 0x68);
  param_1[0xc] = *(long *)(param_2 + 0x60);
  param_1[0xd] = lVar4;
  return;
}

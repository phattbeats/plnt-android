// FUNCTION 02b1f020 bounds 02b1fa0f

/* WARNING: Type propagation algorithm not settling */
/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined8
FUN_02b1f020(undefined8 param_1,undefined8 param_2,undefined2 param_3,int param_4,long *param_5,
            ulong *param_6,char param_7,undefined1 param_8)

{
  long *plVar1;
  code *pcVar2;
  long *plVar3;
  undefined4 uVar4;
  undefined4 uVar5;
  undefined4 uVar6;
  long lVar7;
  undefined8 uVar8;
  int iVar9;
  long lStack_1c8;
  long *plStack_1c0;
  undefined8 uStack_1b8;
  undefined8 uStack_1b0;
  undefined4 *puStack_1a8;
  undefined1 auStack_1a0 [248];
  undefined4 uStack_a8;
  undefined4 uStack_a4;
  undefined4 uStack_a0;
  undefined4 uStack_9c;
  ulong uStack_98;
  ulong uStack_90;
  ulong uStack_88;
  ulong uStack_80;
  ulong uStack_78;
  ulong uStack_70;
  ulong uStack_68;
  ulong uStack_60;
  ulong uStack_58;
  uint uStack_50;
  undefined4 uStack_4c;
  undefined4 uStack_48;
  undefined4 uStack_44;
  ulong uStack_40;

  if ((((((*param_5 == param_5[1]) || (param_5[3] == param_5[4])) || (param_5[6] == param_5[7])) ||
       ((param_5[9] == param_5[10] || (param_5[0xf] == param_5[0x10])))) ||
      ((param_5[0x12] == param_5[0x13] ||
       ((param_5[0x15] == param_5[0x16] || (param_5[0x1b] == param_5[0x1c])))))) ||
     (param_5[0xc] == param_5[0xd])) {
    func_0x04b07ba0(&uStack_1b8,0,0x108);
    FUN_02b1fa70(param_5,&uStack_1b8);
    func_0x0261cf70(&uStack_1b8);
    puStack_1a8 = (undefined4 *)func_0x04b078f0(0x38);
    uVar8 = _UNK_00773ca1;
    uStack_1b8 = 0x39;
    uStack_1b0 = (long *)0x30;
    *(undefined8 *)(puStack_1a8 + 8) = _DAT_00773c99;
    *(undefined8 *)(puStack_1a8 + 10) = uVar8;
    uVar8 = _UNK_00773c91;
    *(undefined8 *)(puStack_1a8 + 4) = _DAT_00773c89;
    *(undefined8 *)(puStack_1a8 + 6) = uVar8;
    uVar6 = _UNK_00773c85;
    uVar5 = _UNK_00773c81;
    uVar4 = _UNK_00773c7d;
    *puStack_1a8 = _DAT_00773c79;
    puStack_1a8[1] = uVar4;
    puStack_1a8[2] = uVar5;
    puStack_1a8[3] = uVar6;
    *(undefined1 *)(puStack_1a8 + 0xc) = 0;
    FUN_0261e070(param_5,&uStack_1b8,auStack_1a0,1);
    if ((uStack_1b8 & 1) != 0) {
      func_0x04a8cfc8(puStack_1a8,uStack_1b8 & 0xfffffffffffffffe);
    }
    puStack_1a8 = (undefined4 *)func_0x04b078f0(0x30);
    uVar8 = _UNK_006b85f5;
    uStack_1b8 = 0x31;
    uStack_1b0 = (long *)0x29;
    *(ulong *)((long)puStack_1a8 + 0x19) = CONCAT17(UNK_006b85f4,_DAT_006b85ed);
    *(undefined8 *)((long)puStack_1a8 + 0x21) = uVar8;
    uVar8 = CONCAT71(_DAT_006b85ed,UNK_006b85ec);
    *(undefined8 *)(puStack_1a8 + 4) = _DAT_006b85e4;
    *(undefined8 *)(puStack_1a8 + 6) = uVar8;
    uVar6 = _UNK_006b85e0;
    uVar5 = _UNK_006b85dc;
    uVar4 = _UNK_006b85d8;
    *puStack_1a8 = _DAT_006b85d4;
    puStack_1a8[1] = uVar4;
    puStack_1a8[2] = uVar5;
    puStack_1a8[3] = uVar6;
    *(undefined1 *)((long)puStack_1a8 + 0x29) = 0;
    FUN_0261e070(param_5 + 3,&uStack_1b8,auStack_1a0,1);
    if ((uStack_1b8 & 1) != 0) {
      func_0x04a8cfc8(puStack_1a8,uStack_1b8 & 0xfffffffffffffffe);
    }
    puStack_1a8 = (undefined4 *)func_0x04b078f0(0x38);
    uVar8 = _UNK_005fdb17;
    uStack_1b8 = 0x39;
    uStack_1b0 = (long *)0x31;
    *(undefined8 *)(puStack_1a8 + 8) = _DAT_005fdb0f;
    *(undefined8 *)(puStack_1a8 + 10) = uVar8;
    uVar8 = _UNK_005fdb07;
    *(undefined8 *)(puStack_1a8 + 4) = _DAT_005fdaff;
    *(undefined8 *)(puStack_1a8 + 6) = uVar8;
    uVar6 = _UNK_005fdafb;
    uVar5 = _UNK_005fdaf7;
    uVar4 = _UNK_005fdaf3;
    *puStack_1a8 = _DAT_005fdaef;
    puStack_1a8[1] = uVar4;
    puStack_1a8[2] = uVar5;
    puStack_1a8[3] = uVar6;
    *(undefined2 *)(puStack_1a8 + 0xc) = 0x6e;
    FUN_0261e070(param_5 + 6,&uStack_1b8,auStack_1a0,1);
    if ((uStack_1b8 & 1) != 0) {
      func_0x04a8cfc8(puStack_1a8,uStack_1b8 & 0xfffffffffffffffe);
    }
    puStack_1a8 = (undefined4 *)func_0x04b078f0(0x28);
    uVar8 = _UNK_0067b68a;
    uStack_1b8 = 0x29;
    uStack_1b0 = (long *)0x26;
    *(undefined8 *)(puStack_1a8 + 4) = _DAT_0067b682;
    *(undefined8 *)(puStack_1a8 + 6) = uVar8;
    uVar6 = _UNK_0067b67e;
    uVar5 = _UNK_0067b67a;
    uVar4 = _UNK_0067b676;
    *puStack_1a8 = _DAT_0067b672;
    puStack_1a8[1] = uVar4;
    puStack_1a8[2] = uVar5;
    puStack_1a8[3] = uVar6;
    *(undefined8 *)((long)puStack_1a8 + 0x1e) = 0x726573752f6d6f63;
    *(undefined1 *)((long)puStack_1a8 + 0x26) = 0;
    FUN_0261e070(param_5 + 9,&uStack_1b8,auStack_1a0,1);
    if ((uStack_1b8 & 1) != 0) {
      func_0x04a8cfc8(puStack_1a8,uStack_1b8 & 0xfffffffffffffffe);
    }
    puStack_1a8 = (undefined4 *)func_0x04b078f0(0x28);
    uVar8 = _UNK_005099d6;
    uStack_1b8 = 0x29;
    uStack_1b0 = (long *)0x27;
    *(undefined8 *)(puStack_1a8 + 4) = _DAT_005099ce;
    *(undefined8 *)(puStack_1a8 + 6) = uVar8;
    uVar6 = _UNK_005099ca;
    uVar5 = _UNK_005099c6;
    uVar4 = _UNK_005099c2;
    *puStack_1a8 = _DAT_005099be;
    puStack_1a8[1] = uVar4;
    puStack_1a8[2] = uVar5;
    puStack_1a8[3] = uVar6;
    *(undefined8 *)((long)puStack_1a8 + 0x1f) = 0x6e6f6464612f6d6f;
    *(undefined1 *)((long)puStack_1a8 + 0x27) = 0;
    FUN_0261e070(param_5 + 0xf,&uStack_1b8,auStack_1a0,1);
    if ((uStack_1b8 & 1) != 0) {
      func_0x04a8cfc8(puStack_1a8,uStack_1b8 & 0xfffffffffffffffe);
    }
    puStack_1a8 = (undefined4 *)func_0x04b078f0(0x30);
    uVar8 = _UNK_00773ccf;
    uStack_1b8 = 0x31;
    uStack_1b0 = (long *)0x2d;
    *(ulong *)((long)puStack_1a8 + 0x1d) = CONCAT53(_UNK_00773cca,_DAT_00773cc7);
    *(undefined8 *)((long)puStack_1a8 + 0x25) = uVar8;
    uVar8 = CONCAT35(_DAT_00773cc7,_UNK_00773cc2);
    *(undefined8 *)(puStack_1a8 + 4) = _DAT_00773cba;
    *(undefined8 *)(puStack_1a8 + 6) = uVar8;
    uVar6 = _UNK_00773cb6;
    uVar5 = _UNK_00773cb2;
    uVar4 = _UNK_00773cae;
    *puStack_1a8 = _DAT_00773caa;
    puStack_1a8[1] = uVar4;
    puStack_1a8[2] = uVar5;
    puStack_1a8[3] = uVar6;
    *(undefined1 *)((long)puStack_1a8 + 0x2d) = 0;
    FUN_0261e070(param_5 + 0x12,&uStack_1b8,auStack_1a0,1);
    if ((uStack_1b8 & 1) != 0) {
      func_0x04a8cfc8(puStack_1a8,uStack_1b8 & 0xfffffffffffffffe);
    }
    puStack_1a8 = (undefined4 *)func_0x04b078f0(0x30);
    uVar8 = _UNK_0087b093;
    uStack_1b8 = 0x31;
    uStack_1b0 = (long *)0x2b;
    *(ulong *)((long)puStack_1a8 + 0x1b) = CONCAT35(_UNK_0087b090,_DAT_0087b08b);
    *(undefined8 *)((long)puStack_1a8 + 0x23) = uVar8;
    uVar8 = CONCAT53(_DAT_0087b08b,_UNK_0087b088);
    *(undefined8 *)(puStack_1a8 + 4) = _DAT_0087b080;
    *(undefined8 *)(puStack_1a8 + 6) = uVar8;
    uVar6 = _UNK_0087b07c;
    uVar5 = _UNK_0087b078;
    uVar4 = _UNK_0087b074;
    *puStack_1a8 = _DAT_0087b070;
    puStack_1a8[1] = uVar4;
    puStack_1a8[2] = uVar5;
    puStack_1a8[3] = uVar6;
    *(undefined1 *)((long)puStack_1a8 + 0x2b) = 0;
    FUN_0261e070(param_5 + 0x15,&uStack_1b8,auStack_1a0,1);
    if ((uStack_1b8 & 1) != 0) {
      func_0x04a8cfc8(puStack_1a8,uStack_1b8 & 0xfffffffffffffffe);
    }
    puStack_1a8 = (undefined4 *)func_0x04b078f0(0x30);
    uVar8 = _UNK_008d7a87;
    uStack_1b8 = 0x31;
    uStack_1b0 = (long *)0x28;
    *(undefined8 *)(puStack_1a8 + 4) = _DAT_008d7a7f;
    *(undefined8 *)(puStack_1a8 + 6) = uVar8;
    uVar6 = _UNK_008d7a7b;
    uVar5 = _UNK_008d7a77;
    uVar4 = _UNK_008d7a73;
    *puStack_1a8 = _DAT_008d7a6f;
    puStack_1a8[1] = uVar4;
    puStack_1a8[2] = uVar5;
    puStack_1a8[3] = uVar6;
    *(undefined8 *)(puStack_1a8 + 8) = 0x7461686373742f6d;
    *(undefined1 *)(puStack_1a8 + 10) = 0;
    FUN_0261e070(param_5 + 0x18,&uStack_1b8,auStack_1a0,1);
    if ((uStack_1b8 & 1) != 0) {
      func_0x04a8cfc8(puStack_1a8,uStack_1b8 & 0xfffffffffffffffe);
    }
    puStack_1a8 = (undefined4 *)func_0x04b078f0(0x30);
    uVar8 = _UNK_007cb7c9;
    uStack_1b8 = 0x31;
    uStack_1b0 = (long *)0x2c;
    *(ulong *)(puStack_1a8 + 7) = CONCAT44(_UNK_007cb7c5,_DAT_007cb7c1);
    *(undefined8 *)(puStack_1a8 + 9) = uVar8;
    uVar8 = CONCAT44(_DAT_007cb7c1,_UNK_007cb7bd);
    *(undefined8 *)(puStack_1a8 + 4) = _DAT_007cb7b5;
    *(undefined8 *)(puStack_1a8 + 6) = uVar8;
    uVar6 = _UNK_007cb7b1;
    uVar5 = _UNK_007cb7ad;
    uVar4 = _UNK_007cb7a9;
    *puStack_1a8 = _DAT_007cb7a5;
    puStack_1a8[1] = uVar4;
    puStack_1a8[2] = uVar5;
    puStack_1a8[3] = uVar6;
    *(undefined1 *)(puStack_1a8 + 0xb) = 0;
    FUN_0261e070(param_5 + 0xc,&uStack_1b8,auStack_1a0,1);
    if ((uStack_1b8 & 1) != 0) {
      func_0x04a8cfc8(puStack_1a8,uStack_1b8 & 0xfffffffffffffffe);
    }
    puStack_1a8 = (undefined4 *)func_0x04b078f0(0x40);
    uVar8 = _UNK_00661c7c;
    uStack_1b8 = 0x41;
    uStack_1b0 = (long *)0x39;
    *(ulong *)((long)puStack_1a8 + 0x29) = CONCAT17(UNK_00661c7b,_DAT_00661c74);
    *(undefined8 *)((long)puStack_1a8 + 0x31) = uVar8;
    uVar8 = CONCAT71(_DAT_00661c74,UNK_00661c73);
    *(undefined8 *)(puStack_1a8 + 8) = _DAT_00661c6b;
    *(undefined8 *)(puStack_1a8 + 10) = uVar8;
    uVar8 = _UNK_00661c63;
    *(undefined8 *)(puStack_1a8 + 4) = _DAT_00661c5b;
    *(undefined8 *)(puStack_1a8 + 6) = uVar8;
    uVar6 = _UNK_00661c57;
    uVar5 = _UNK_00661c53;
    uVar4 = _UNK_00661c4f;
    *puStack_1a8 = _DAT_00661c4b;
    puStack_1a8[1] = uVar4;
    puStack_1a8[2] = uVar5;
    puStack_1a8[3] = uVar6;
    *(undefined1 *)((long)puStack_1a8 + 0x39) = 0;
    FUN_0261e070(param_5 + 0x1b,&uStack_1b8,auStack_1a0,1);
    if ((uStack_1b8 & 1) != 0) {
      func_0x04a8cfc8(puStack_1a8,uStack_1b8 & 0xfffffffffffffffe);
    }
    puStack_1a8 = (undefined4 *)func_0x04b078f0(0x20);
    uVar8 = _UNK_00661c96;
    uStack_1b8 = 0x21;
    uStack_1b0 = (long *)0x19;
    *(ulong *)((long)puStack_1a8 + 9) = CONCAT17(UNK_00661c95,CONCAT43(_UNK_00661c91,_DAT_00661c8e))
    ;
    *(undefined8 *)((long)puStack_1a8 + 0x11) = uVar8;
    uVar6 = _UNK_00661c91;
    uVar5 = _UNK_00661c8d;
    uVar4 = _UNK_00661c89;
    *puStack_1a8 = _DAT_00661c85;
    puStack_1a8[1] = uVar4;
    puStack_1a8[2] = uVar5;
    puStack_1a8[3] = uVar6;
    *(undefined1 *)((long)puStack_1a8 + 0x19) = 0;
    FUN_0261e070(param_5 + 0x1e,&uStack_1b8,auStack_1a0,1);
    if ((uStack_1b8 & 1) != 0) {
      func_0x04a8cfc8(puStack_1a8,uStack_1b8 & 0xfffffffffffffffe);
    }
  }
  lVar7 = func_0x04b078f0(0x10);
  func_0x01a875d0(lVar7);
  lStack_1c8 = lVar7;
  func_0x044cec80(&uStack_1b8,lVar7,param_5,param_3);
  iVar9 = 0;
  if (0 < param_4) {
    iVar9 = param_4;
  }
  uVar8 = func_0x04b078f0(0x570);
  uStack_a8 = (undefined4)uStack_1b8;
  uStack_a4 = uStack_1b8._4_4_;
  uStack_a0 = (undefined4)uStack_1b0;
  uStack_9c = uStack_1b0._4_4_;
  if (uStack_1b0 != (long *)0x0) {
    LOCK();
    uStack_1b0[1] = uStack_1b0[1] + 1;
    UNLOCK();
  }
  if ((param_7 == '\0') || (param_5[0x1f] == param_5[0x1e])) {
    plStack_1c0 = (long *)0x0;
  }
  else {
    func_0x02bc0dd0(&plStack_1c0);
  }
  uStack_88 = param_6[2];
  uStack_98 = *param_6;
  uStack_90 = param_6[1];
  *param_6 = 0;
  param_6[1] = 0;
  param_6[2] = 0;
  uStack_70 = param_6[5];
  uStack_80 = param_6[3];
  uStack_78 = param_6[4];
  param_6[3] = 0;
  param_6[4] = 0;
  param_6[5] = 0;
  uStack_58 = param_6[8];
  uStack_68 = param_6[6];
  uStack_60 = param_6[7];
  param_6[6] = 0;
  param_6[7] = 0;
  param_6[8] = 0;
  uStack_40 = param_6[0xb];
  uStack_50 = (uint)param_6[9];
  uStack_4c = *(undefined4 *)((long)param_6 + 0x4c);
  uStack_48 = (undefined4)param_6[10];
  uStack_44 = *(undefined4 *)((long)param_6 + 0x54);
  param_6[9] = 0;
  param_6[10] = 0;
  param_6[0xb] = 0;
  func_0x02b207e0(uVar8,param_2,&uStack_a8,&lStack_1c8,iVar9,&plStack_1c0,&uStack_98,param_8);
  if ((uStack_50 & 1) != 0) {
    func_0x04a8cfc8(uStack_40,CONCAT44(uStack_4c,uStack_50) & 0xfffffffffffffffe);
  }
  if ((uStack_68 & 1) != 0) {
    func_0x04a8cfc8(uStack_58,uStack_68 & 0xfffffffffffffffe);
  }
  if ((uStack_80 & 1) != 0) {
    func_0x04a8cfc8(uStack_70,uStack_80 & 0xfffffffffffffffe);
  }
  plVar3 = plStack_1c0;
  if ((uStack_98 & 1) != 0) {
    func_0x04a8cfc8(uStack_88,uStack_98 & 0xfffffffffffffffe);
    plVar3 = plStack_1c0;
  }
  plStack_1c0 = (long *)0x0;
  if (plVar3 != (long *)0x0) {
    plStack_1c0 = (long *)0x0;
    (**(code **)(*plVar3 + 8))();
  }
  plVar3 = (long *)CONCAT44(uStack_9c,uStack_a0);
  if (plVar3 != (long *)0x0) {
    LOCK();
    plVar1 = plVar3 + 1;
    lVar7 = *plVar1;
    *plVar1 = *plVar1 + -1;
    UNLOCK();
    if (lVar7 == 0) {
      (**(code **)(*plVar3 + 0x10))(plVar3);
      func_0x04aa8780(plVar3);
    }
  }
  if (param_5[0x1b] != param_5[0x1c]) {
    func_0x02b214e0(uVar8);
    plVar3 = uStack_1b0;
    if (uStack_1b0 != (long *)0x0) {
      LOCK();
      plVar1 = uStack_1b0 + 1;
      lVar7 = *plVar1;
      *plVar1 = *plVar1 + -1;
      UNLOCK();
      if (lVar7 == 0) {
        (**(code **)(*uStack_1b0 + 0x10))(uStack_1b0);
        func_0x04aa8780(plVar3);
      }
    }
    lVar7 = lStack_1c8;
    lStack_1c8 = 0;
    if (lVar7 != 0) {
      func_0x01a8b610(&lStack_1c8);
    }
    return uVar8;
  }
                    /* WARNING: Does not return */
  pcVar2 = (code *)invalidInstructionException();
  (*pcVar2)();
}


// FUNCTION 0261e070 bounds 0261e46f

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


// FUNCTION 02b83d90 bounds 02b83fdf

undefined4 * FUN_02b83d90(undefined4 *param_1,byte *param_2)

{
  byte bVar1;
  code *pcVar2;
  byte bVar3;
  uint uVar4;
  ulong uVar5;
  char *pcVar6;
  ulong uVar7;
  byte *pbVar8;
  ulong uVar9;
  bool bVar10;
  undefined8 uStack_68;
  ulong uStack_60;
  char *pcStack_58;
  undefined8 uStack_48;
  undefined8 uStack_40;
  undefined8 uStack_38;

  uStack_68 = 0;
  uStack_60 = 0;
  pcStack_58 = (char *)0x0;
  bVar10 = (*param_2 & 1) != 0;
  pbVar8 = param_2 + 1;
  if (bVar10) {
    pbVar8 = *(byte **)(param_2 + 0x10);
  }
  uVar5 = (ulong)(*param_2 >> 1);
  if (bVar10) {
    uVar5 = *(ulong *)(param_2 + 8);
  }
  if (uVar5 == 0) {
    uStack_68._0_1_ = 0;
  }
  else {
    uVar9 = 0;
    do {
      bVar1 = pbVar8[uVar9];
      if ((0x2d < (ulong)bVar1) || ((0x200100002600U >> ((ulong)bVar1 & 0x3f) & 1) == 0)) {
        bVar3 = bVar1 - 0x20;
        if (0x19 < (byte)(bVar1 + 0x9f)) {
          bVar3 = bVar1;
        }
        if (9 < (byte)(bVar3 - 0x30) && 0x19 < (byte)(bVar3 + 0xbf)) {
          *(undefined1 *)param_1 = 0;
          *(undefined1 *)(param_1 + 6) = 0;
          uVar5 = uStack_68;
          goto joined_r0x02b83f7d;
        }
        func_0x04aacac0(&uStack_68,(int)(char)bVar3);
      }
      uVar9 = uVar9 + 1;
    } while (uVar5 != uVar9);
  }
  uVar5 = (ulong)((byte)uStack_68 >> 1);
  if (((byte)uStack_68 & 1) != 0) {
    uVar5 = uStack_60;
  }
  if (uVar5 == 0x10) {
    uStack_48 = 0;
    uStack_40 = 0;
    uStack_38 = 0;
    pcVar6 = (char *)((long)&uStack_68 + 1);
    if (((byte)uStack_68 & 1) != 0) {
      pcVar6 = pcStack_58;
    }
    func_0x04aacac0(&uStack_48,(int)*pcVar6);
    uVar5 = uStack_68 & 0xff;
    uVar9 = uStack_68 >> 1 & 0x7f;
    if ((uStack_68 & 1) != 0) {
      uVar9 = uStack_60;
    }
    if (1 < uVar9) {
      uVar9 = 1;
      do {
        uVar4 = (uint)uVar5;
        if ((uVar9 & 3) == 0) {
          func_0x04aacac0(&uStack_48,0x2d);
          uVar4 = (uint)(byte)uStack_68;
        }
        uVar5 = (ulong)(byte)((byte)uVar4 >> 1);
        if ((uVar4 & 1) != 0) {
          uVar5 = uStack_60;
        }
        if (uVar5 < uVar9) {
                    /* WARNING: Does not return */
          pcVar2 = (code *)invalidInstructionException();
          (*pcVar2)();
        }
        pcVar6 = (char *)((long)&uStack_68 + 1);
        if ((uVar4 & 1) != 0) {
          pcVar6 = pcStack_58;
        }
        func_0x04aacac0(&uStack_48,(int)pcVar6[uVar9]);
        uVar9 = uVar9 + 1;
        uVar5 = uStack_68 & 0xff;
        uVar7 = uStack_68 >> 1 & 0x7f;
        if ((uStack_68 & 1) != 0) {
          uVar7 = uStack_60;
        }
      } while (uVar9 < uVar7);
    }
    *(undefined8 *)(param_1 + 4) = uStack_38;
    *param_1 = (undefined4)uStack_48;
    param_1[1] = uStack_48._4_4_;
    param_1[2] = (undefined4)uStack_40;
    param_1[3] = uStack_40._4_4_;
    *(undefined1 *)(param_1 + 6) = 1;
joined_r0x02b83f7d:
    if ((uVar5 & 1) == 0) {
      return param_1;
    }
  }
  else {
    *(undefined1 *)param_1 = 0;
    *(undefined1 *)(param_1 + 6) = 0;
    if (((byte)uStack_68 & 1) == 0) {
      return param_1;
    }
  }
  func_0x04a8cfc8(pcStack_58,uStack_68 & 0xfffffffffffffffe);
  return param_1;
}


// FUNCTION 02b84190 bounds 02b8436f

ulong FUN_02b84190(ulong param_1,byte *param_2,undefined8 param_3,undefined8 param_4)

{
  ulong uVar1;
  byte bVar2;
  byte *pbVar3;
  undefined8 *in_RAX;
  long lVar4;
  undefined8 *puVar5;
  undefined8 uVar6;
  ulong uVar7;
  int iVar8;
  long lVar9;
  long extraout_RDX;
  uint uVar10;
  byte *pbVar11;
  ulong uVar12;
  bool bVar13;
  undefined1 auStack_70 [8];
  undefined8 uStack_68;
  undefined8 *puStack_60;
  undefined8 uStack_58;
  undefined8 uStack_50;
  long lStack_48;
  ulong uStack_38;

  bVar2 = *param_2;
  if ((bVar2 & 1) == 0) {
    uVar7 = (ulong)(bVar2 >> 1);
  }
  else {
    uVar7 = *(ulong *)(param_2 + 8);
  }
  uVar1 = uVar7 + 0xe;
  if (uVar1 < 0xfffffffffffffff7) {
    pbVar3 = *(byte **)(param_2 + 0x10);
    if (uVar1 < 0x17) {
      uVar12 = 0;
      auStack_70[0] = (char)uVar1 * '\x02';
    }
    else {
      lVar4 = (uVar1 & 0xfffffffffffffff8) + 8;
      lVar9 = 0x1a;
      if (lVar4 != 0x18) {
        lVar9 = lVar4;
      }
      uStack_38 = param_1;
      in_RAX = (undefined8 *)func_0x04b078f0(lVar9);
      auStack_70 = (undefined1  [8])(lVar9 + 1);
      uVar12 = (ulong)auStack_70 & 0xffffffff;
      param_1 = uStack_38;
      uStack_68 = uVar1;
      puStack_60 = in_RAX;
    }
    pbVar11 = param_2 + 1;
    if ((bVar2 & 1) != 0) {
      pbVar11 = pbVar3;
    }
    bVar13 = (uVar12 & 1) == 0;
    puVar5 = in_RAX;
    if (bVar13) {
      puVar5 = (undefined8 *)(auStack_70 + 1);
    }
    *(undefined8 *)((long)puVar5 + 6) = 0x79654b797265766f;
    *puVar5 = 0x766f636552337374;
    lVar9 = (long)&uStack_68 + 7;
    if (!bVar13) {
      lVar9 = (long)in_RAX + 0xe;
    }
    func_0x04b07a30(lVar9,pbVar11,uVar7);
    *(undefined1 *)(lVar9 + uVar7) = 0;
    uStack_58 = 0;
    uStack_50 = 0;
    lStack_48 = 0;
    func_0x04aac620(&uStack_58,0x40,0);
    uVar7 = (ulong)auStack_70 >> 1 & 0x7f;
    puVar5 = (undefined8 *)(auStack_70 + 1);
    if (((ulong)auStack_70 & 1) != 0) {
      uVar7 = uStack_68;
      puVar5 = puStack_60;
    }
    lVar9 = lStack_48;
    if ((uStack_58 & 1) == 0) {
      lVar9 = (long)&uStack_58 + 1;
    }
    FUN_048592b0(puVar5,uVar7,lVar9);
    FUN_02b81970(param_1,&uStack_58);
    if ((uStack_58 & 1) != 0) {
      func_0x04a8cfc8(lStack_48,uStack_58 & 0xfffffffffffffffe);
    }
    if (((ulong)auStack_70 & 1) != 0) {
      func_0x04a8cfc8(puStack_60,(ulong)auStack_70 & 0xfffffffffffffffe);
    }
    return param_1;
  }
  uVar6 = func_0x019f8660();
  iVar8 = (int)param_4;
  if ((uStack_58 & 1) != 0) goto LAB_02b84333;
  while (((ulong)auStack_70 & 1) == 0) {
    func_0x04b07a10(uVar6);
LAB_02b84333:
    func_0x04a8cfc8(lStack_48,uStack_58 & 0xfffffffffffffffe);
    iVar8 = (int)param_4;
  }
  uVar10 = auStack_70._0_4_ & 0xfffffffe;
  func_0x04a8cfc8(puStack_60);
  func_0x04b07a10();
  uVar7 = 3;
  if ((((int)uVar6 != 0) && (uVar10 != 0x74)) && (uVar10 != 0x67)) {
    if (uVar10 == 0) {
      if (extraout_RDX != 0) {
        uVar7 = 3;
        if (0 < iVar8) {
          uVar7 = (ulong)((uint)(extraout_RDX != iVar8) * 3);
        }
        return uVar7;
      }
      return 1;
    }
    uVar7 = 2;
  }
  return uVar7;
}


// FUNCTION 02b74690 bounds 02b748ff

undefined8 FUN_02b74690(long param_1)

{
  ulong *puVar1;
  byte bVar2;
  uint uVar3;
  int iVar4;
  int iVar5;
  int iVar6;
  ulong uVar7;
  undefined8 ******ppppppuVar8;
  undefined8 *****pppppuVar9;
  byte *pbVar10;
  undefined8 uVar11;
  undefined8 ******ppppppuVar12;
  undefined8 ******ppppppuVar13;
  ulong *puVar14;
  int iStack_4c;
  undefined8 *****pppppuStack_48;
  undefined8 *****pppppuStack_40;
  long lStack_38;

  func_0x04ab61a0();
  uVar3 = *(uint *)(param_1 + 0xa0);
  if ((uVar3 & 2) == 0) goto LAB_02b746b0;
  pbVar10 = (byte *)(*(ulong *)(param_1 + 0xc0) & 0xfffffffffffffffc);
  bVar2 = *pbVar10;
  if ((bVar2 & 1) == 0) {
    uVar7 = (ulong)(bVar2 >> 1);
  }
  else {
    uVar7 = *(ulong *)(pbVar10 + 8);
  }
  uVar11 = 0;
  if (((uVar3 & 4) == 0) || (uVar11 = 0, uVar7 == 0)) goto LAB_02b746b2;
  pbVar10 = (byte *)(*(ulong *)(param_1 + 200) & 0xfffffffffffffffc);
  bVar2 = *pbVar10;
  if ((bVar2 & 1) == 0) {
    if (bVar2 >> 1 != 0) goto LAB_02b7471c;
  }
  else if (*(long *)(pbVar10 + 8) != 0) {
LAB_02b7471c:
    if ((uVar3 & 0x20000) != 0 && (long)*(int *)(param_1 + 0x1b0) != 0) {
      pbVar10 = (byte *)(*(ulong *)(param_1 + 0x140) & 0xfffffffffffffffc);
      bVar2 = *pbVar10;
      if ((bVar2 & 1) == 0) {
        uVar7 = (ulong)(bVar2 >> 1);
      }
      else {
        uVar7 = *(ulong *)(pbVar10 + 8);
      }
      uVar11 = 0;
      if (((uVar3 & 0x80000) == 0) || (uVar7 == 0)) goto LAB_02b746b2;
      pbVar10 = (byte *)(*(ulong *)(param_1 + 0x150) & 0xfffffffffffffffc);
      bVar2 = *pbVar10;
      if ((bVar2 & 1) == 0) {
        uVar7 = (ulong)(bVar2 >> 1);
      }
      else {
        uVar7 = *(ulong *)(pbVar10 + 8);
      }
      if (uVar7 != 0) {
        pppppuStack_48 = &pppppuStack_40;
        pppppuStack_40 = (undefined8 ******)0x0;
        lStack_38 = 0;
        uVar7 = *(ulong *)(param_1 + 0x1a8);
        puVar14 = (ulong *)(uVar7 + 7);
        if ((uVar7 & 1) == 0) {
          puVar14 = (ulong *)(param_1 + 0x1a8);
        }
        puVar1 = puVar14 + *(int *)(param_1 + 0x1b0);
        do {
          iVar4 = func_0x02b1b880(*(undefined4 *)(*puVar14 + 0x18));
          ppppppuVar12 = &pppppuStack_40;
          ppppppuVar8 = (undefined8 ******)pppppuStack_40;
          while (ppppppuVar13 = ppppppuVar12, ppppppuVar8 != (undefined8 ******)0x0) {
            while (ppppppuVar12 = ppppppuVar8, *(int *)((long)ppppppuVar12 + 0x1c) <= iVar4) {
              if (iVar4 <= *(int *)((long)ppppppuVar12 + 0x1c)) goto LAB_02b747d2;
              ppppppuVar8 = (undefined8 ******)ppppppuVar12[1];
              if ((undefined8 ******)ppppppuVar12[1] == (undefined8 ******)0x0) {
                ppppppuVar13 = ppppppuVar12 + 1;
                goto LAB_02b74827;
              }
            }
            ppppppuVar8 = (undefined8 ******)*ppppppuVar12;
          }
LAB_02b74827:
          pppppuVar9 = (undefined8 *****)func_0x04b078f0(0x20);
          *(int *)((long)pppppuVar9 + 0x1c) = iVar4;
          *pppppuVar9 = (undefined8 ****)0x0;
          pppppuVar9[1] = (undefined8 ****)0x0;
          pppppuVar9[2] = ppppppuVar12;
          *ppppppuVar13 = pppppuVar9;
          if ((undefined8 ******)*pppppuStack_48 != (undefined8 ******)0x0) {
            pppppuStack_48 = (undefined8 *****)*pppppuStack_48;
          }
          func_0x01a4f620(pppppuStack_40,pppppuVar9);
          lStack_38 = lStack_38 + 1;
LAB_02b747d2:
          puVar14 = puVar14 + 1;
        } while (puVar14 != puVar1);
        iVar5 = func_0x02b1b7a0(0);
        iStack_4c = iVar5;
        iVar6 = func_0x02b1b7b0(0);
        uVar11 = CONCAT71((uint7)(uint3)((uint)iVar4 >> 8),1);
        if (iVar5 != iVar6) {
          do {
            iVar4 = func_0x02b1b7d0(iVar5);
            ppppppuVar12 = (undefined8 ******)pppppuStack_40;
            while( true ) {
              while( true ) {
                if (ppppppuVar12 == (undefined8 ******)0x0) goto LAB_02b748d0;
                if (*(int *)((long)ppppppuVar12 + 0x1c) <= iVar4) break;
                ppppppuVar12 = (undefined8 ******)*ppppppuVar12;
              }
              if (iVar4 <= *(int *)((long)ppppppuVar12 + 0x1c)) break;
              ppppppuVar12 = (undefined8 ******)ppppppuVar12[1];
            }
            if (ppppppuVar12 == &pppppuStack_40) {
LAB_02b748d0:
              uVar11 = 0;
              break;
            }
            func_0x02b1b7c0(&iStack_4c);
            iVar5 = iStack_4c;
          } while (iStack_4c != iVar6);
        }
        func_0x02b75620(&pppppuStack_48);
        goto LAB_02b746b2;
      }
    }
  }
LAB_02b746b0:
  uVar11 = 0;
LAB_02b746b2:
  func_0x04ab6260(param_1);
  return uVar11;
}

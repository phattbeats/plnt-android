// FUNCTION 02b33df0 bounds 02b346bf

/* WARNING: Type propagation algorithm not settling */
/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

ulong FUN_02b33df0(long param_1,long *param_2)

{
  long lVar1;
  code *pcVar2;
  int iVar3;
  ulong uVar4;
  long lVar5;
  ulong uVar6;
  ulong uVar7;
  undefined8 uVar8;
  long lVar9;
  undefined8 unaff_RBP;
  undefined7 *puVar10;
  long *unaff_R12;
  long *plVar11;
  long in_FS_OFFSET;
  undefined8 uStack_1f8;
  ulong uStack_1f0;
  long lStack_1e8;
  undefined8 uStack_1d8;
  long *plStack_1d0;
  long lStack_1c8;
  long *plStack_1b8;
  undefined8 uStack_1a0;
  ulong uStack_198;
  undefined8 uStack_190;
  undefined8 uStack_188;
  byte bStack_178;
  undefined7 uStack_177;
  ulong uStack_170;
  undefined7 *puStack_168;
  long *plStack_160;
  undefined8 *puStack_158;
  undefined8 *puStack_150;
  undefined8 uStack_148;
  ulong uStack_140;
  long lStack_138;
  undefined *puStack_130;
  long *plStack_128;
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

  lVar1 = param_1 + 0x20;
  func_0x04ab5980(lVar1);
  if (*(long *)(param_1 + 0x560) == 0) {
LAB_02b34259:
    uVar6 = CONCAT71((int7)((ulong)unaff_RBP >> 8),1);
    goto LAB_02b3425c;
  }
  uStack_198 = 0;
  uStack_190 = 0;
  uStack_188 = 0;
  uStack_1f8 = 0;
  uStack_1f0 = 0;
  lStack_1e8 = 0;
  iVar3 = func_0x02b710f0(*(undefined8 *)(param_1 + 0x1a8),&uStack_198);
  if ((iVar3 == 0) &&
     (iVar3 = func_0x02b711d0(*(undefined8 *)(param_1 + 0x1a8),&uStack_1f8), iVar3 == 0)) {
    uStack_1a0 = (**(code **)(*param_2 + 0x10))(param_2);
    uVar6 = (**(code **)(*param_2 + 0x18))(param_2);
    if (0xfffffffffffffff6 < uVar6) {
      func_0x019f8660();
      goto LAB_02b344f0;
    }
    if (uVar6 < 0x17) {
      uStack_148 = (long *)CONCAT71(uStack_148._1_7_,(char)uVar6 * '\x02');
      lVar9 = (long)&uStack_148 + 1;
    }
    else {
      uVar7 = (uVar6 & 0xfffffffffffffff8) + 8;
      uVar4 = 0x1a;
      if (uVar7 != 0x18) {
        uVar4 = uVar7;
      }
      lVar9 = func_0x04b078f0(uVar4);
      unaff_R12 = (long *)(uVar4 | 1);
      uStack_148 = unaff_R12;
      uStack_140 = uVar6;
      lStack_138 = lVar9;
    }
    func_0x04b07a30(lVar9,uStack_1a0,uVar6);
    *(undefined1 *)(lVar9 + uVar6) = 0;
    func_0x02b7fc70(&bStack_178,&uStack_148,&uStack_198);
    if (((ulong)uStack_148 & 1) != 0) {
      func_0x04a8cfc8(lStack_138,(ulong)uStack_148 & 0xfffffffffffffffe);
    }
    if ((uStack_1f8 & 1) == 0) {
      uVar6 = uStack_1f8 >> 1 & 0x7f;
      if ((bStack_178 & 1) != 0) goto LAB_02b3419b;
LAB_02b3417f:
      if (uVar6 != bStack_178 >> 1) goto LAB_02b341df;
LAB_02b341a8:
      lVar9 = lStack_1e8;
      if ((uStack_1f8 & 1) == 0) {
        lVar9 = (long)&uStack_1f8 + 1;
      }
      puVar10 = puStack_168;
      if ((bStack_178 & 1) == 0) {
        puVar10 = &uStack_177;
      }
      iVar3 = func_0x04b07bb0(lVar9,puVar10);
      if (iVar3 != 0) goto LAB_02b341df;
      func_0x02b72970(*(undefined8 *)(param_1 + 0x1a8),&bStack_178);
      uStack_1a0 = *(undefined8 *)(param_1 + 0x1a8);
      uVar8 = (**(code **)(*param_2 + 0x10))(param_2);
      param_2 = (long *)(**(code **)(*param_2 + 0x18))(param_2);
      if ((long *)0xfffffffffffffff6 < param_2) {
LAB_02b344f0:
        uVar8 = func_0x019f8660();
        uVar8 = func_0x019f83c0(uVar8);
        uVar8 = func_0x019f83c0(uVar8);
        func_0x02b43940(&plStack_160);
        lVar9 = 0x20;
        if ((plStack_1b8 == param_2) || (lVar9 = 0x28, plStack_1b8 != (long *)0x0)) {
          (**(code **)(*plStack_1b8 + lVar9))();
        }
        lVar9 = 0x20;
        if ((plStack_128 == unaff_R12) || (lVar9 = 0x28, plStack_128 != (long *)0x0)) {
          (**(code **)(*plStack_128 + lVar9))();
        }
        if ((bStack_178 & 1) != 0) {
          func_0x04a8cfc8(puStack_168,CONCAT71(uStack_177,bStack_178) & 0xfffffffffffffffe);
        }
        do {
          if ((uStack_1f8 & 1) != 0) {
            func_0x04a8cfc8(lStack_1e8,uStack_1f8 & 0xfffffffffffffffe);
          }
          if ((uStack_198 & 1) == 0) {
            func_0x04ab59b0(lVar1);
            uVar8 = func_0x04b07a10(uVar8);
            func_0x01ab81b0(&uStack_148);
          }
          else {
            func_0x04a8cfc8(uStack_188,uStack_198 & 0xfffffffffffffffe);
            func_0x04ab59b0(lVar1);
            uVar8 = func_0x04b07a10(uVar8);
          }
        } while( true );
      }
      if (param_2 < (long *)0x17) {
        uStack_1d8 = CONCAT71(uStack_1d8._1_7_,(char)param_2 * '\x02');
        lVar9 = (long)&uStack_1d8 + 1;
      }
      else {
        uVar4 = ((ulong)param_2 & 0xfffffffffffffff8) + 8;
        uVar6 = 0x1a;
        if (uVar4 != 0x18) {
          uVar6 = uVar4;
        }
        lVar9 = func_0x04b078f0(uVar6);
        uStack_1d8 = uVar6 | 1;
        plStack_1d0 = param_2;
        lStack_1c8 = lVar9;
      }
      func_0x04b07a30(lVar9,uVar8,param_2);
      *(undefined1 *)(lVar9 + (long)param_2) = 0;
      func_0x02b7fe30(&uStack_148,&uStack_1d8,&uStack_198);
      func_0x02b72a70(uStack_1a0,&uStack_148);
      if (((ulong)uStack_148 & 1) != 0) {
        func_0x04a8cfc8(lStack_138,(ulong)uStack_148 & 0xfffffffffffffffe);
      }
      if ((uStack_1d8 & 1) != 0) {
        func_0x04a8cfc8(lStack_1c8,uStack_1d8 & 0xfffffffffffffffe);
      }
      plStack_128 = (long *)0x0;
      func_0x0256db60(param_1 + 0x540,&uStack_148);
      lVar5 = *(long *)(param_1 + 0x50);
      if (plStack_128 == (long *)0x0) {
        plStack_1b8 = (long *)0x0;
      }
      else if (plStack_128 == &uStack_148) {
        plStack_1b8 = &uStack_1d8;
        (**(code **)(*plStack_128 + 0x18))(plStack_128,&uStack_1d8);
      }
      else {
        plStack_1b8 = plStack_128;
        plStack_128 = (long *)0x0;
      }
      if (*(long *)(in_FS_OFFSET + -0x1730) == 0) {
        uVar8 = 0;
      }
      else {
        uVar8 = *(undefined8 *)(*(long *)(in_FS_OFFSET + -0x1730) + 8);
      }
      plStack_160 = &uStack_1d8;
      puStack_150 = (undefined8 *)func_0x01d19ae0(uVar8,0x60,0x10);
      *puStack_150 = 0;
      puStack_150[1] = &UNK_02b439d0;
      *(undefined4 *)(puStack_150 + 2) = 0;
      puStack_158 = puStack_150;
      if (plStack_1b8 == (long *)0x0) {
        puStack_150[8] = 0;
      }
      else if (plStack_1b8 == &uStack_1d8) {
        puStack_150[8] = puStack_150 + 4;
        (**(code **)(*plStack_1b8 + 0x18))();
      }
      else {
        puStack_150[8] = plStack_1b8;
        plStack_1b8 = (long *)0x0;
      }
      func_0x01d197c0(*(undefined8 *)(lVar5 + 8),puStack_150,0);
      if (plStack_1b8 == &uStack_1d8) {
        lVar5 = 0x20;
LAB_02b344a9:
        (**(code **)(*plStack_1b8 + lVar5))();
      }
      else {
        lVar5 = 0x28;
        if (plStack_1b8 != (long *)0x0) goto LAB_02b344a9;
      }
      if (plStack_128 == &uStack_148) {
        lVar5 = 0x20;
LAB_02b344cd:
        (**(code **)(*plStack_128 + lVar5))();
      }
      else {
        lVar5 = 0x28;
        if (plStack_128 != (long *)0x0) goto LAB_02b344cd;
      }
      unaff_RBP = CONCAT71((int7)((ulong)lVar9 >> 8),1);
    }
    else {
      uVar6 = uStack_1f0;
      if ((bStack_178 & 1) == 0) goto LAB_02b3417f;
LAB_02b3419b:
      if (uVar6 == uStack_170) goto LAB_02b341a8;
LAB_02b341df:
      func_0x02b31dd0(param_1);
      unaff_RBP = 0;
    }
    if ((bStack_178 & 1) != 0) {
      func_0x04a8cfc8(puStack_168,CONCAT71(uStack_177,bStack_178) & 0xfffffffffffffffe);
    }
    if ((uStack_1f8 & 1) != 0) {
      func_0x04a8cfc8(lStack_1e8,uStack_1f8 & 0xfffffffffffffffe);
    }
    if ((uStack_198 & 1) != 0) {
      func_0x04a8cfc8(uStack_188,uStack_198 & 0xfffffffffffffffe);
    }
    if ((char)unaff_RBP != '\0') goto LAB_02b34259;
  }
  else {
    func_0x01ab8040(&uStack_148);
    func_0x01a53960(&lStack_138,&UNK_008223de,0x46);
    if ((uStack_d0 & 0x10) == 0) {
      plVar11 = (long *)0x0;
      lVar9 = lStack_120;
      lVar5 = 0;
      if ((uStack_d0 & 8) != 0) goto LAB_02b33ee2;
LAB_02b33ef1:
      lVar9 = lVar5;
      uStack_1d8 = CONCAT71(uStack_1d8._1_7_,(char)plVar11 * '\x02');
      lVar5 = (long)&uStack_1d8 + 1;
    }
    else {
      lVar9 = lStack_108;
      uStack_110 = uStack_d8;
      if (uStack_d8 < uStack_100) {
        uStack_d8 = uStack_100;
        uStack_110 = uStack_100;
      }
LAB_02b33ee2:
      plVar11 = (long *)(uStack_110 - lVar9);
      if ((long)plVar11 < 0) {
                    /* WARNING: Does not return */
        pcVar2 = (code *)invalidInstructionException();
        (*pcVar2)();
      }
      lVar5 = lVar9;
      if (plVar11 < (long *)0x17) goto LAB_02b33ef1;
      uVar4 = ((ulong)plVar11 & 0x7ffffffffffffff8) + 8;
      uVar6 = 0x1a;
      if (uVar4 != 0x18) {
        uVar6 = uVar4;
      }
      lVar5 = func_0x04b078f0(uVar6);
      uStack_1d8 = uVar6 | 1;
      plStack_1d0 = plVar11;
      lStack_1c8 = lVar5;
    }
    func_0x04b07a30(lVar5,lVar9,plVar11);
    *(undefined1 *)(lVar5 + (long)plVar11) = 0;
    plStack_160 = (long *)((ulong)plStack_160 & 0xffffffffffff0000);
    func_0x02acb490(&uStack_1d8,2,&plStack_160,0);
    if (((ulong)plStack_160 & 1) != 0) {
      func_0x04a8cfc8(puStack_150,(ulong)plStack_160 & 0xfffffffffffffffe);
    }
    if ((uStack_1d8 & 1) != 0) {
      func_0x04a8cfc8(lStack_1c8,uStack_1d8 & 0xfffffffffffffffe);
    }
    uStack_148 = _UNK_04c9f9f0;
    *(undefined8 *)((long)&uStack_148 + _UNK_04c9f9f0[-3]) = _UNK_04c9fa30;
    lStack_138 = _UNK_04c9fa38;
    puStack_130 = &UNK_04c9f668;
    if ((bStack_f0 & 1) != 0) {
      func_0x04a8cfc8(uStack_e0,CONCAT71(uStack_ef,bStack_f0) & 0xfffffffffffffffe);
    }
    puStack_130 = &UNK_04c9f338;
    func_0x04aea9f0(&plStack_128);
    func_0x04abbce8(&uStack_148,&UNK_04c9f9f8);
    func_0x04ab7708(auStack_c8);
    if ((uStack_1f8 & 1) != 0) {
      func_0x04a8cfc8(lStack_1e8,uStack_1f8 & 0xfffffffffffffffe);
    }
    if ((uStack_198 & 1) != 0) {
      func_0x04a8cfc8(uStack_188,uStack_198 & 0xfffffffffffffffe);
    }
  }
  uVar6 = 0;
LAB_02b3425c:
  func_0x04ab59b0(lVar1);
  return uVar6 & 0xffffffff;
}


// FUNCTION 02b7f8e0 bounds 02b7fc6f

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined8 * FUN_02b7f8e0(undefined8 *param_1,int param_2)

{
  code *pcVar1;
  undefined8 uVar2;
  ulong uVar3;
  long lVar4;
  long lVar5;
  ulong uVar6;
  ulong uVar7;
  undefined8 uStack_178;
  ulong uStack_170;
  long lStack_168;
  byte bStack_160;
  undefined1 uStack_15f;
  undefined6 uStack_15e;
  undefined8 uStack_150;
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

  if (param_2 == 2) {
    uVar2 = func_0x04b078f0(0x40);
    func_0x02b81f80(uVar2);
    goto LAB_02b7fae5;
  }
  if (param_2 == 1) {
    uVar2 = func_0x04b078f0(0x40);
    func_0x02b81f80(uVar2);
    goto LAB_02b7fae5;
  }
  func_0x01ab8040(alStack_148);
  func_0x01a53960(alStack_148 + 2,&UNK_006b86d4,0x1a);
  if ((uStack_d0 & 0x10) == 0) {
    uVar7 = 0;
    lVar5 = lStack_120;
    lVar4 = 0;
    if ((uStack_d0 & 8) != 0) goto LAB_02b7f999;
LAB_02b7f9a8:
    lVar5 = lVar4;
    uStack_178 = CONCAT71(uStack_178._1_7_,(char)uVar7 * '\x02');
    lVar4 = (long)&uStack_178 + 1;
  }
  else {
    lVar5 = lStack_108;
    uStack_110 = uStack_d8;
    if (uStack_d8 < uStack_100) {
      uStack_d8 = uStack_100;
      uStack_110 = uStack_100;
    }
LAB_02b7f999:
    uVar7 = uStack_110 - lVar5;
    if ((long)uVar7 < 0) {
                    /* WARNING: Does not return */
      pcVar1 = (code *)invalidInstructionException();
      (*pcVar1)();
    }
    lVar4 = lVar5;
    if (uVar7 < 0x17) goto LAB_02b7f9a8;
    uVar3 = (uVar7 & 0x7ffffffffffffff8) + 8;
    uVar6 = 0x1a;
    if (uVar3 != 0x18) {
      uVar6 = uVar3;
    }
    lVar4 = func_0x04b078f0(uVar6);
    uStack_178 = uVar6 | 1;
    uStack_170 = uVar7;
    lStack_168 = lVar4;
  }
  func_0x04b07a30(lVar4,lVar5,uVar7);
  *(undefined1 *)(lVar4 + uVar7) = 0;
  bStack_160 = 0;
  uStack_15f = 0;
  func_0x02acb490(&uStack_178,0,&bStack_160,0);
  if ((bStack_160 & 1) != 0) {
    func_0x04a8cfc8(uStack_150,
                    CONCAT62(uStack_15e,CONCAT11(uStack_15f,bStack_160)) & 0xfffffffffffffffe);
  }
  if ((uStack_178 & 1) != 0) {
    func_0x04a8cfc8(lStack_168,uStack_178 & 0xfffffffffffffffe);
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
  uVar2 = 0;
LAB_02b7fae5:
  *param_1 = uVar2;
  return param_1;
}


// FUNCTION 026c7bf0 bounds 026ca42f

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void FUN_026c7bf0(undefined8 param_1,undefined8 param_2)

{
  undefined8 uVar1;
  undefined4 uVar2;
  undefined4 uVar3;
  undefined4 uVar4;
  undefined8 uStack_610;
  undefined8 uStack_608;
  undefined8 uStack_600;
  byte bStack_5f8;
  undefined4 uStack_5f7;
  undefined4 uStack_5f3;
  undefined4 uStack_5ef;
  undefined4 uStack_5eb;
  undefined2 uStack_5e7;
  undefined1 uStack_5e5;
  undefined4 uStack_5e4;
  byte bStack_5e0;
  undefined4 uStack_5df;
  undefined4 uStack_5db;
  undefined4 uStack_5d7;
  undefined3 uStack_5d3;
  undefined1 uStack_5d0;
  undefined4 uStack_5cf;
  undefined3 uStack_5cb;
  byte bStack_5c8;
  undefined4 uStack_5c7;
  undefined4 uStack_5c3;
  undefined4 uStack_5bf;
  undefined4 uStack_5bb;
  undefined2 uStack_5b7;
  undefined1 uStack_5b5;
  undefined4 uStack_5b4;
  byte bStack_5b0;
  undefined4 uStack_5af;
  undefined4 uStack_5ab;
  undefined4 uStack_5a7;
  undefined4 uStack_5a3;
  undefined4 uStack_59f;
  undefined1 uStack_59b;
  undefined2 uStack_59a;
  byte bStack_598;
  undefined4 uStack_597;
  undefined4 uStack_593;
  undefined4 uStack_58f;
  undefined2 uStack_58b;
  undefined1 uStack_589;
  undefined1 uStack_588;
  undefined6 uStack_587;
  undefined1 uStack_581;
  byte bStack_580;
  undefined7 uStack_57f;
  undefined1 uStack_578;
  undefined7 uStack_577;
  undefined1 uStack_570;
  undefined7 uStack_56f;
  byte bStack_568;
  undefined7 uStack_567;
  undefined1 uStack_560;
  undefined7 uStack_55f;
  undefined1 uStack_558;
  undefined7 uStack_557;
  byte bStack_550;
  undefined7 uStack_54f;
  undefined1 uStack_548;
  undefined7 uStack_547;
  undefined1 uStack_540;
  undefined7 uStack_53f;
  byte bStack_538;
  undefined5 uStack_537;
  undefined2 uStack_532;
  undefined1 uStack_530;
  undefined5 uStack_52f;
  undefined1 uStack_52a;
  undefined8 uStack_528;
  byte bStack_520;
  undefined7 uStack_51f;
  undefined1 uStack_518;
  undefined4 uStack_517;
  undefined1 uStack_513;
  undefined8 uStack_510;
  byte bStack_508;
  undefined7 uStack_507;
  undefined1 uStack_500;
  undefined7 uStack_4ff;
  undefined1 uStack_4f8;
  undefined7 uStack_4f7;
  byte bStack_4f0;
  undefined4 uStack_4ef;
  undefined4 uStack_4eb;
  undefined4 uStack_4e7;
  undefined4 uStack_4e3;
  undefined2 uStack_4df;
  undefined1 uStack_4dd;
  undefined4 uStack_4dc;
  byte bStack_4d8;
  undefined4 uStack_4d7;
  undefined4 uStack_4d3;
  undefined4 uStack_4cf;
  undefined3 uStack_4cb;
  undefined1 uStack_4c8;
  undefined4 uStack_4c7;
  undefined3 uStack_4c3;
  byte bStack_4c0;
  undefined4 uStack_4bf;
  undefined4 uStack_4bb;
  undefined4 uStack_4b7;
  undefined3 uStack_4b3;
  undefined1 uStack_4b0;
  undefined4 uStack_4af;
  undefined3 uStack_4ab;
  byte bStack_4a8;
  undefined7 uStack_4a7;
  undefined4 uStack_4a0;
  undefined1 uStack_49c;
  undefined8 uStack_498;
  byte bStack_490;
  undefined7 uStack_48f;
  undefined1 uStack_488;
  undefined7 uStack_487;
  undefined1 uStack_480;
  undefined7 uStack_47f;
  byte bStack_478;
  undefined5 uStack_477;
  undefined2 uStack_472;
  undefined1 uStack_470;
  undefined5 uStack_46f;
  undefined1 uStack_46a;
  undefined8 uStack_468;
  byte bStack_460;
  undefined7 uStack_45f;
  undefined1 uStack_458;
  undefined4 uStack_457;
  undefined1 uStack_453;
  undefined8 uStack_450;
  byte bStack_448;
  undefined4 uStack_447;
  undefined4 uStack_443;
  undefined4 uStack_43f;
  undefined4 uStack_43b;
  undefined2 uStack_437;
  undefined1 uStack_435;
  undefined4 uStack_434;
  byte bStack_430;
  undefined4 uStack_42f;
  undefined4 uStack_42b;
  undefined4 uStack_427;
  undefined4 uStack_423;
  undefined2 uStack_41f;
  undefined1 uStack_41d;
  undefined4 uStack_41c;
  byte bStack_418;
  undefined6 uStack_417;
  undefined1 uStack_411;
  undefined1 uStack_410;
  undefined6 uStack_40f;
  undefined1 uStack_409;
  undefined8 uStack_408;
  byte bStack_400;
  undefined4 uStack_3ff;
  undefined4 uStack_3fb;
  undefined4 uStack_3f7;
  undefined4 uStack_3f3;
  undefined4 uStack_3ef;
  undefined1 uStack_3eb;
  undefined2 uStack_3ea;
  byte bStack_3e8;
  undefined4 uStack_3e7;
  undefined4 uStack_3e3;
  undefined4 uStack_3df;
  undefined2 uStack_3db;
  undefined1 uStack_3d9;
  undefined1 uStack_3d8;
  undefined6 uStack_3d7;
  undefined1 uStack_3d1;
  byte bStack_3d0;
  undefined4 uStack_3cf;
  undefined4 uStack_3cb;
  undefined4 uStack_3c7;
  undefined3 uStack_3c3;
  undefined1 uStack_3c0;
  undefined4 uStack_3bf;
  undefined3 uStack_3bb;
  byte bStack_3b8;
  undefined4 uStack_3b7;
  undefined4 uStack_3b3;
  undefined4 uStack_3af;
  undefined3 uStack_3ab;
  undefined1 uStack_3a8;
  undefined4 uStack_3a7;
  undefined3 uStack_3a3;
  byte bStack_3a0;
  undefined4 uStack_39f;
  undefined4 uStack_39b;
  undefined4 uStack_397;
  undefined3 uStack_393;
  undefined1 uStack_390;
  undefined4 uStack_38f;
  undefined3 uStack_38b;
  byte bStack_388;
  undefined5 uStack_387;
  undefined2 uStack_382;
  undefined1 uStack_380;
  undefined5 uStack_37f;
  undefined1 uStack_37a;
  undefined8 uStack_378;
  byte bStack_370;
  undefined4 uStack_36f;
  undefined4 uStack_36b;
  undefined4 uStack_367;
  undefined1 uStack_363;
  undefined2 uStack_362;
  undefined1 uStack_360;
  undefined5 uStack_35f;
  undefined1 uStack_35a;
  undefined1 uStack_359;
  byte bStack_358;
  undefined7 uStack_357;
  undefined1 uStack_350;
  undefined7 uStack_34f;
  undefined1 uStack_348;
  undefined7 uStack_347;
  byte bStack_340;
  undefined4 uStack_33f;
  undefined4 uStack_33b;
  undefined4 uStack_337;
  undefined4 uStack_333;
  undefined2 uStack_32f;
  undefined1 uStack_32d;
  undefined4 uStack_32c;
  byte bStack_328;
  undefined6 uStack_327;
  undefined1 uStack_321;
  undefined1 uStack_320;
  undefined6 uStack_31f;
  undefined1 uStack_319;
  undefined8 uStack_318;
  byte bStack_310;
  undefined7 uStack_30f;
  undefined1 uStack_308;
  undefined4 uStack_307;
  undefined1 uStack_303;
  undefined8 uStack_300;
  byte bStack_2f8;
  undefined7 uStack_2f7;
  undefined1 uStack_2f0;
  undefined4 uStack_2ef;
  undefined1 uStack_2eb;
  undefined8 uStack_2e8;
  byte bStack_2e0;
  undefined7 uStack_2df;
  undefined1 uStack_2d8;
  undefined4 uStack_2d7;
  undefined1 uStack_2d3;
  undefined8 uStack_2d0;
  byte bStack_2c8;
  undefined4 uStack_2c7;
  undefined4 uStack_2c3;
  undefined4 uStack_2bf;
  undefined4 uStack_2bb;
  undefined4 uStack_2b7;
  undefined1 uStack_2b3;
  undefined2 uStack_2b2;
  byte bStack_2b0;
  undefined4 uStack_2af;
  undefined4 uStack_2ab;
  undefined4 uStack_2a7;
  undefined4 uStack_2a3;
  undefined4 uStack_29f;
  undefined1 uStack_29b;
  undefined2 uStack_29a;
  byte bStack_298;
  undefined6 uStack_297;
  undefined1 uStack_291;
  undefined1 uStack_290;
  undefined6 uStack_28f;
  undefined1 uStack_289;
  undefined8 uStack_288;
  byte bStack_280;
  undefined5 uStack_27f;
  undefined2 uStack_27a;
  undefined1 uStack_278;
  undefined5 uStack_277;
  undefined1 uStack_272;
  undefined8 uStack_270;
  byte bStack_268;
  undefined7 uStack_267;
  undefined1 uStack_260;
  undefined4 uStack_25f;
  undefined1 uStack_25b;
  undefined8 uStack_258;
  byte bStack_250;
  undefined7 uStack_24f;
  undefined1 uStack_248;
  undefined7 uStack_247;
  undefined1 uStack_240;
  undefined7 uStack_23f;
  ulong auStack_238 [2];
  undefined4 *puStack_228;
  byte bStack_220;
  undefined4 uStack_21f;
  undefined4 uStack_21b;
  undefined4 uStack_217;
  undefined4 uStack_213;
  undefined2 uStack_20f;
  undefined5 uStack_20d;
  byte bStack_208;
  undefined4 uStack_207;
  undefined4 uStack_203;
  undefined4 uStack_1ff;
  undefined4 uStack_1fb;
  undefined2 uStack_1f7;
  undefined5 uStack_1f5;
  ulong auStack_1f0 [2];
  undefined8 *puStack_1e0;
  ulong auStack_1d8 [2];
  undefined8 *puStack_1c8;
  ulong auStack_1c0 [2];
  undefined4 *puStack_1b0;
  ulong auStack_1a8 [2];
  undefined8 *puStack_198;
  ulong auStack_190 [2];
  undefined4 *puStack_180;
  ulong auStack_178 [2];
  undefined4 *puStack_168;
  ulong auStack_160 [2];
  undefined8 *puStack_150;
  ulong auStack_148 [2];
  undefined8 *puStack_138;
  ulong auStack_130 [2];
  undefined8 *puStack_120;
  ulong auStack_118 [2];
  undefined4 *puStack_108;
  ulong auStack_100 [2];
  undefined4 *puStack_f0;
  ulong auStack_e8 [2];
  undefined8 *puStack_d8;
  ulong auStack_d0 [2];
  undefined4 *puStack_c0;
  ulong auStack_b8 [2];
  undefined4 *puStack_a8;
  byte bStack_a0;
  undefined4 uStack_9f;
  undefined4 uStack_9b;
  undefined4 uStack_97;
  undefined4 uStack_93;
  undefined2 uStack_8f;
  undefined5 uStack_8d;
  byte bStack_88;
  undefined4 uStack_87;
  undefined4 uStack_83;
  undefined4 uStack_7f;
  undefined4 uStack_7b;
  undefined1 uStack_77;
  undefined6 uStack_76;
  ulong auStack_70 [2];
  undefined8 *puStack_60;
  ulong auStack_58 [2];
  undefined4 *puStack_48;
  byte bStack_40;
  undefined4 uStack_3f;
  undefined4 uStack_3b;
  undefined4 uStack_37;
  undefined4 uStack_33;
  undefined1 uStack_2f;
  undefined6 uStack_2e;

  bStack_250 = 0x1e;
  uStack_24f = 0x6f636341746567;
  uStack_248 = 0x75;
  uStack_247 = 0x6574617453746e;
  uStack_240 = 0;
  uStack_608 = 0x11;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d6890(param_2,&bStack_250,&uStack_608,&uStack_610);
  if ((bStack_250 & 1) != 0) {
    func_0x04a8cfc8(CONCAT71(uStack_23f,uStack_240),
                    CONCAT71(uStack_24f,bStack_250) & 0xfffffffffffffffe);
  }
  bStack_268 = 0x18;
  uStack_267 = 0x63416e69676f6c;
  uStack_260 = 99;
  uStack_25f = 0x746e756f;
  uStack_25b = 0;
  uStack_608 = 0x19;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d6980(param_2,&bStack_268,&uStack_608,&uStack_610);
  if ((bStack_268 & 1) != 0) {
    func_0x04a8cfc8(uStack_258,CONCAT71(uStack_267,bStack_268) & 0xfffffffffffffffe);
  }
  bStack_280 = 0x1a;
  uStack_27f = 0x7461657263;
  uStack_27a = 0x4165;
  uStack_278 = 99;
  uStack_277 = 0x746e756f63;
  uStack_272 = 0;
  uStack_608 = 0x21;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d6a70(param_2,&bStack_280,&uStack_608,&uStack_610);
  if ((bStack_280 & 1) != 0) {
    func_0x04a8cfc8(uStack_270,
                    CONCAT26(uStack_27a,CONCAT51(uStack_27f,bStack_280)) & 0xfffffffffffffffe);
  }
  bStack_40 = 0x20;
  uStack_3f = _DAT_0058b631;
  uStack_3b = _UNK_0058b635;
  uStack_37 = _UNK_0058b639;
  uStack_33 = _UNK_0058b63d;
  uStack_2f = 0;
  uStack_608 = 0x29;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d6b60(param_2,&bStack_40,&uStack_608,&uStack_610);
  if ((bStack_40 & 1) != 0) {
    func_0x04a8cfc8(CONCAT62(uStack_2e,CONCAT11(uStack_2f,uStack_33._3_1_)),
                    CONCAT35((undefined3)uStack_3b,CONCAT41(uStack_3f,bStack_40)) &
                    0xfffffffffffffffe);
  }
  bStack_298 = 0x1c;
  uStack_297 = 0x65676e616863;
  uStack_291 = 0x50;
  uStack_290 = 0x61;
  uStack_28f = 0x64726f777373;
  uStack_289 = 0;
  uStack_608 = 0x31;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d6c50(param_2,&bStack_298,&uStack_608,&uStack_610);
  if ((bStack_298 & 1) != 0) {
    func_0x04a8cfc8(uStack_288,
                    CONCAT17(uStack_291,CONCAT61(uStack_297,bStack_298)) & 0xfffffffffffffffe);
  }
  bStack_2b0 = 0x28;
  uStack_2af = _DAT_00863c0c;
  uStack_2ab = _UNK_00863c10;
  uStack_2a7 = _UNK_00863c14;
  uStack_2a3 = _UNK_00863c18;
  uStack_29f = 0x72617461;
  uStack_29b = 0;
  uStack_608 = 0x41;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d6d40(param_2,&bStack_2b0,&uStack_608,&uStack_610);
  if ((bStack_2b0 & 1) != 0) {
    func_0x04a8cfc8(CONCAT26(uStack_29a,CONCAT15(uStack_29b,CONCAT41(uStack_29f,uStack_2a3._3_1_))),
                    CONCAT35((undefined3)uStack_2ab,CONCAT41(uStack_2af,bStack_2b0)) &
                    0xfffffffffffffffe);
  }
  puStack_48 = (undefined4 *)func_0x04b078f0(0x20);
  uVar4 = _UNK_006a1613;
  uVar3 = _UNK_006a160f;
  uVar2 = _UNK_006a160b;
  auStack_58[0] = 0x21;
  auStack_58[1] = 0x18;
  *puStack_48 = _DAT_006a1607;
  puStack_48[1] = uVar2;
  puStack_48[2] = uVar3;
  puStack_48[3] = uVar4;
  *(undefined8 *)(puStack_48 + 4) = 0x7372617461764174;
  *(undefined1 *)(puStack_48 + 6) = 0;
  uStack_608 = 0x49;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d6e30(param_2,auStack_58,&uStack_608,&uStack_610);
  if ((auStack_58[0] & 1) != 0) {
    func_0x04a8cfc8(puStack_48,auStack_58[0] & 0xfffffffffffffffe);
  }
  bStack_2c8 = 0x28;
  uStack_2c7 = _DAT_0062a00e;
  uStack_2c3 = _UNK_0062a012;
  uStack_2bf = _UNK_0062a016;
  uStack_2bb = _UNK_0062a01a;
  uStack_2b7 = 0x72617461;
  uStack_2b3 = 0;
  uStack_608 = 0x51;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d6f20(param_2,&bStack_2c8,&uStack_608,&uStack_610);
  if ((bStack_2c8 & 1) != 0) {
    func_0x04a8cfc8(CONCAT26(uStack_2b2,CONCAT15(uStack_2b3,CONCAT41(uStack_2b7,uStack_2bb._3_1_))),
                    CONCAT35((undefined3)uStack_2c3,CONCAT41(uStack_2c7,bStack_2c8)) &
                    0xfffffffffffffffe);
  }
  puStack_60 = (undefined8 *)func_0x04b078f0(0x28);
  uVar1 = _UNK_004d7756;
  auStack_70[0] = 0x29;
  auStack_70[1] = 0x27;
  *puStack_60 = _DAT_004d774e;
  puStack_60[1] = uVar1;
  uVar4 = _UNK_004d776a;
  uVar3 = _UNK_004d7766;
  uVar2 = _UNK_004d7762;
  *(undefined4 *)(puStack_60 + 2) = _DAT_004d775e;
  *(undefined4 *)((long)puStack_60 + 0x14) = uVar2;
  *(undefined4 *)(puStack_60 + 3) = uVar3;
  *(undefined4 *)((long)puStack_60 + 0x1c) = uVar4;
  *(undefined8 *)((long)puStack_60 + 0x1f) = 0x79747265706f7250;
  *(undefined1 *)((long)puStack_60 + 0x27) = 0;
  uStack_608 = 0x59;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d6f20(param_2,auStack_70,&uStack_608,&uStack_610);
  if ((auStack_70[0] & 1) != 0) {
    func_0x04a8cfc8(puStack_60,auStack_70[0] & 0xfffffffffffffffe);
  }
  bStack_2e0 = 0x18;
  uStack_2df = 0x4164616f6c7075;
  uStack_2d8 = 0x76;
  uStack_2d7 = 0x72617461;
  uStack_2d3 = 0;
  uStack_608 = 0x61;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d7010(param_2,&bStack_2e0,&uStack_608,&uStack_610);
  if ((bStack_2e0 & 1) != 0) {
    func_0x04a8cfc8(uStack_2d0,CONCAT71(uStack_2df,bStack_2e0) & 0xfffffffffffffffe);
  }
  bStack_2f8 = 0x18;
  uStack_2f7 = 0x416574656c6564;
  uStack_2f0 = 0x76;
  uStack_2ef = 0x72617461;
  uStack_2eb = 0;
  uStack_608 = 0x69;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d7100(param_2,&bStack_2f8,&uStack_608,&uStack_610);
  if ((bStack_2f8 & 1) != 0) {
    func_0x04a8cfc8(uStack_2e8,CONCAT71(uStack_2f7,bStack_2f8) & 0xfffffffffffffffe);
  }
  bStack_88 = 0x20;
  uStack_87 = _DAT_0082ae57;
  uStack_83 = _UNK_0082ae5b;
  uStack_7f = _UNK_0082ae5f;
  uStack_7b = _UNK_0082ae63;
  uStack_77 = 0;
  uStack_608 = 0x71;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d6b60(param_2,&bStack_88,&uStack_608,&uStack_610);
  if ((bStack_88 & 1) != 0) {
    func_0x04a8cfc8(CONCAT62(uStack_76,CONCAT11(uStack_77,uStack_7b._3_1_)),
                    CONCAT35((undefined3)uStack_83,CONCAT41(uStack_87,bStack_88)) &
                    0xfffffffffffffffe);
  }
  bStack_a0 = 0x22;
  uStack_9f = _DAT_008a071a;
  uStack_9b = _UNK_008a071e;
  uStack_97 = _UNK_008a0722;
  uStack_93 = _UNK_008a0726;
  uStack_8f = 0x6b;
  uStack_608 = 0xb1;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d71f0(param_2,&bStack_a0,&uStack_608,&uStack_610);
  if ((bStack_a0 & 1) != 0) {
    func_0x04a8cfc8(CONCAT53(uStack_8d,CONCAT21(uStack_8f,uStack_93._3_1_)),
                    CONCAT35((undefined3)uStack_9b,CONCAT41(uStack_9f,bStack_a0)) &
                    0xfffffffffffffffe);
  }
  bStack_310 = 0x18;
  uStack_30f = 0x6b636142657375;
  uStack_308 = 0x75;
  uStack_307 = 0x79654b70;
  uStack_303 = 0;
  uStack_608 = 0xa9;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d6b60(param_2,&bStack_310,&uStack_608,&uStack_610);
  if ((bStack_310 & 1) != 0) {
    func_0x04a8cfc8(uStack_300,CONCAT71(uStack_30f,bStack_310) & 0xfffffffffffffffe);
  }
  bStack_328 = 0x1c;
  uStack_327 = 0x65676e616863;
  uStack_321 = 0x55;
  uStack_320 = 0x73;
  uStack_31f = 0x656d616e7265;
  uStack_319 = 0;
  uStack_608 = 0x39;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d72e0(param_2,&bStack_328,&uStack_608,&uStack_610);
  if ((bStack_328 & 1) != 0) {
    func_0x04a8cfc8(uStack_318,
                    CONCAT17(uStack_321,CONCAT61(uStack_327,bStack_328)) & 0xfffffffffffffffe);
  }
  bStack_340 = 0x24;
  uStack_33f = _DAT_0064876c;
  uStack_33b = _UNK_00648770;
  uStack_337 = _UNK_00648774;
  uStack_333 = _UNK_00648778;
  uStack_32f = 0x656d;
  uStack_32d = 0;
  uStack_608 = 0x79;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d73d0(param_2,&bStack_340,&uStack_608,&uStack_610);
  if ((bStack_340 & 1) != 0) {
    func_0x04a8cfc8(CONCAT44(uStack_32c,CONCAT13(uStack_32d,CONCAT21(uStack_32f,uStack_333._3_1_))),
                    CONCAT35((undefined3)uStack_33b,CONCAT41(uStack_33f,bStack_340)) &
                    0xfffffffffffffffe);
  }
  bStack_358 = 0x1e;
  uStack_357 = 0x6f636341746567;
  uStack_350 = 0x75;
  uStack_34f = 0x6c69616d45746e;
  uStack_348 = 0;
  uStack_608 = 0x81;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d73d0(param_2,&bStack_358,&uStack_608,&uStack_610);
  if ((bStack_358 & 1) != 0) {
    func_0x04a8cfc8(CONCAT71(uStack_347,uStack_348),
                    CONCAT71(uStack_357,bStack_358) & 0xfffffffffffffffe);
  }
  puStack_a8 = (undefined4 *)func_0x04b078f0(0x1a);
  uVar4 = _UNK_006e5db9;
  uVar3 = _UNK_006e5db5;
  uVar2 = _UNK_006e5db1;
  auStack_b8[0] = 0x1b;
  auStack_b8[1] = 0x17;
  *puStack_a8 = _DAT_006e5dad;
  puStack_a8[1] = uVar2;
  puStack_a8[2] = uVar3;
  puStack_a8[3] = uVar4;
  *(undefined8 *)((long)puStack_a8 + 0xf) = 0x64496b616570736d;
  *(undefined1 *)((long)puStack_a8 + 0x17) = 0;
  uStack_608 = 0x91;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d73d0(param_2,auStack_b8,&uStack_608,&uStack_610);
  if ((auStack_b8[0] & 1) != 0) {
    func_0x04a8cfc8(puStack_a8,auStack_b8[0] & 0xfffffffffffffffe);
  }
  bStack_370 = 0x2a;
  uStack_36f = _DAT_0052d83c;
  uStack_36b = _UNK_0052d840;
  uStack_367 = _UNK_0052d844;
  uStack_363 = (undefined1)_UNK_0052d848;
  uStack_362 = 0x696d;
  uStack_360 = 0x73;
  uStack_35f = 0x736e6f6973;
  uStack_35a = 0;
  uStack_608 = 0x89;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d74c0(param_2,&bStack_370,&uStack_608,&uStack_610);
  if ((bStack_370 & 1) != 0) {
    func_0x04a8cfc8(CONCAT17(uStack_359,CONCAT16(uStack_35a,CONCAT51(uStack_35f,uStack_360))),
                    CONCAT35((undefined3)uStack_36b,CONCAT41(uStack_36f,bStack_370)) &
                    0xfffffffffffffffe);
  }
  puStack_c0 = (undefined4 *)func_0x04b078f0(0x20);
  uVar4 = _UNK_0080ae88;
  uVar3 = _UNK_0080ae84;
  uVar2 = _UNK_0080ae80;
  auStack_d0[0] = 0x21;
  auStack_d0[1] = 0x18;
  *puStack_c0 = _DAT_0080ae7c;
  puStack_c0[1] = uVar2;
  puStack_c0[2] = uVar3;
  puStack_c0[3] = uVar4;
  *(undefined8 *)(puStack_c0 + 4) = 0x64726f7773736150;
  *(undefined1 *)(puStack_c0 + 6) = 0;
  uStack_608 = 0x99;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d73d0(param_2,auStack_d0,&uStack_608,&uStack_610);
  if ((auStack_d0[0] & 1) != 0) {
    func_0x04a8cfc8(puStack_c0,auStack_d0[0] & 0xfffffffffffffffe);
  }
  bStack_388 = 0x1a;
  uStack_387 = 0x756f676f6c;
  uStack_382 = 0x4174;
  uStack_380 = 99;
  uStack_37f = 0x746e756f63;
  uStack_37a = 0;
  uStack_608 = 0xa1;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d6890(param_2,&bStack_388,&uStack_608,&uStack_610);
  if ((bStack_388 & 1) != 0) {
    func_0x04a8cfc8(uStack_378,
                    CONCAT26(uStack_382,CONCAT51(uStack_387,bStack_388)) & 0xfffffffffffffffe);
  }
  bStack_3a0 = 0x26;
  uStack_39f = _DAT_0062a023;
  uStack_39b = _UNK_0062a027;
  uStack_397 = _UNK_0062a02b;
  uStack_393 = (undefined3)_UNK_0062a02f;
  uStack_390 = 0x70;
  uStack_38f = 0x79654b;
  uStack_608 = 0xb9;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d75b0(param_2,&bStack_3a0,&uStack_608,&uStack_610);
  if ((bStack_3a0 & 1) != 0) {
    func_0x04a8cfc8(CONCAT35(uStack_38b,CONCAT41(uStack_38f,uStack_390)),
                    CONCAT35((undefined3)uStack_39b,CONCAT41(uStack_39f,bStack_3a0)) &
                    0xfffffffffffffffe);
  }
  bStack_3b8 = 0x26;
  uStack_3b7 = _DAT_004f6d3e;
  uStack_3b3 = _UNK_004f6d42;
  uStack_3af = _UNK_004f6d46;
  uStack_3ab = (undefined3)_UNK_004f6d4a;
  uStack_3a8 = 0x6f;
  uStack_3a7 = 0x746e75;
  uStack_608 = 0xc1;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d6b60(param_2,&bStack_3b8,&uStack_608,&uStack_610);
  if ((bStack_3b8 & 1) != 0) {
    func_0x04a8cfc8(CONCAT35(uStack_3a3,CONCAT41(uStack_3a7,uStack_3a8)),
                    CONCAT35((undefined3)uStack_3b3,CONCAT41(uStack_3b7,bStack_3b8)) &
                    0xfffffffffffffffe);
  }
  bStack_3d0 = 0x26;
  uStack_3cf = _DAT_0071eca4;
  uStack_3cb = _UNK_0071eca8;
  uStack_3c7 = _UNK_0071ecac;
  uStack_3c3 = (undefined3)_UNK_0071ecb0;
  uStack_3c0 = 0x74;
  uStack_3bf = 0x657461;
  uStack_608 = 0xc9;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d6890(param_2,&bStack_3d0,&uStack_608,&uStack_610);
  if ((bStack_3d0 & 1) != 0) {
    func_0x04a8cfc8(CONCAT35(uStack_3bb,CONCAT41(uStack_3bf,uStack_3c0)),
                    CONCAT35((undefined3)uStack_3cb,CONCAT41(uStack_3cf,bStack_3d0)) &
                    0xfffffffffffffffe);
  }
  bStack_3e8 = 0x2c;
  uStack_3e7 = _DAT_005e4fcc;
  uStack_3e3 = _UNK_005e4fd0;
  uStack_3df = _UNK_005e4fd4;
  uStack_3db = (undefined2)_UNK_005e4fd8;
  uStack_3d9 = 0x75;
  uStack_3d8 = 0x70;
  uStack_3d7 = 0x746572636553;
  uStack_3d1 = 0;
  uStack_608 = 0xd1;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d76a0(param_2,&bStack_3e8,&uStack_608,&uStack_610);
  if ((bStack_3e8 & 1) != 0) {
    func_0x04a8cfc8(CONCAT17(uStack_3d1,CONCAT61(uStack_3d7,uStack_3d8)),
                    CONCAT35((undefined3)uStack_3e3,CONCAT41(uStack_3e7,bStack_3e8)) &
                    0xfffffffffffffffe);
  }
  puStack_d8 = (undefined8 *)func_0x04b078f0(0x20);
  auStack_e8[0] = 0x21;
  auStack_e8[1] = 0x1b;
  uVar1 = CONCAT17(UNK_0077c5b3,CONCAT43(_DAT_0077c5af,_UNK_0077c5ac));
  *puStack_d8 = _DAT_0077c5a4;
  puStack_d8[1] = uVar1;
  uVar4 = _UNK_0077c5bb;
  uVar3 = _UNK_0077c5b7;
  uVar2 = _UNK_0077c5b3;
  *(undefined4 *)((long)puStack_d8 + 0xb) = _DAT_0077c5af;
  *(undefined4 *)((long)puStack_d8 + 0xf) = uVar2;
  *(undefined4 *)((long)puStack_d8 + 0x13) = uVar3;
  *(undefined4 *)((long)puStack_d8 + 0x17) = uVar4;
  *(undefined1 *)((long)puStack_d8 + 0x1b) = 0;
  uStack_608 = 0xd9;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d72e0(param_2,auStack_e8,&uStack_608,&uStack_610);
  if ((auStack_e8[0] & 1) != 0) {
    func_0x04a8cfc8(puStack_d8,auStack_e8[0] & 0xfffffffffffffffe);
  }
  puStack_f0 = (undefined4 *)func_0x04b078f0(0x1a);
  uVar4 = _UNK_0066a9b6;
  uVar3 = _UNK_0066a9b2;
  uVar2 = _UNK_0066a9ae;
  auStack_100[0] = 0x1b;
  auStack_100[1] = 0x17;
  *puStack_f0 = _DAT_0066a9aa;
  puStack_f0[1] = uVar2;
  puStack_f0[2] = uVar3;
  puStack_f0[3] = uVar4;
  *(undefined8 *)((long)puStack_f0 + 0xf) = 0x6570795468747541;
  *(undefined1 *)((long)puStack_f0 + 0x17) = 0;
  uStack_608 = 0xe1;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d7790(param_2,auStack_100,&uStack_608,&uStack_610);
  if ((auStack_100[0] & 1) != 0) {
    func_0x04a8cfc8(puStack_f0,auStack_100[0] & 0xfffffffffffffffe);
  }
  puStack_108 = (undefined4 *)func_0x04b078f0(0x1a);
  uVar4 = _UNK_004d7782;
  uVar3 = _UNK_004d777e;
  uVar2 = _UNK_004d777a;
  auStack_118[0] = 0x1b;
  auStack_118[1] = 0x17;
  *puStack_108 = _DAT_004d7776;
  puStack_108[1] = uVar2;
  puStack_108[2] = uVar3;
  puStack_108[3] = uVar4;
  *(undefined8 *)((long)puStack_108 + 0xf) = 0x6570795468747541;
  *(undefined1 *)((long)puStack_108 + 0x17) = 0;
  uStack_608 = 0xe9;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d7880(param_2,auStack_118,&uStack_608,&uStack_610);
  if ((auStack_118[0] & 1) != 0) {
    func_0x04a8cfc8(puStack_108,auStack_118[0] & 0xfffffffffffffffe);
  }
  bStack_400 = 0x28;
  uStack_3ff = _DAT_0082ae68;
  uStack_3fb = _UNK_0082ae6c;
  uStack_3f7 = _UNK_0082ae70;
  uStack_3f3 = _UNK_0082ae74;
  uStack_3ef = 0x65707954;
  uStack_3eb = 0;
  uStack_608 = 0xf1;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d7970(param_2,&bStack_400,&uStack_608,&uStack_610);
  if ((bStack_400 & 1) != 0) {
    func_0x04a8cfc8(CONCAT26(uStack_3ea,CONCAT15(uStack_3eb,CONCAT41(uStack_3ef,uStack_3f3._3_1_))),
                    CONCAT35((undefined3)uStack_3fb,CONCAT41(uStack_3ff,bStack_400)) &
                    0xfffffffffffffffe);
  }
  bStack_418 = 0x1c;
  uStack_417 = 0x636341746567;
  uStack_411 = 0x6f;
  uStack_410 = 0x75;
  uStack_40f = 0x61746144746e;
  uStack_409 = 0;
  uStack_608 = 0xf9;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d7a60(param_2,&bStack_418,&uStack_608,&uStack_610);
  if ((bStack_418 & 1) != 0) {
    func_0x04a8cfc8(uStack_408,
                    CONCAT17(uStack_411,CONCAT61(uStack_417,bStack_418)) & 0xfffffffffffffffe);
  }
  puStack_120 = (undefined8 *)func_0x04b078f0(0x20);
  auStack_130[0] = 0x21;
  auStack_130[1] = 0x1c;
  uVar1 = CONCAT44(_DAT_004f6d6d,_UNK_004f6d69);
  *puStack_120 = _DAT_004f6d61;
  puStack_120[1] = uVar1;
  uVar4 = _UNK_004f6d79;
  uVar3 = _UNK_004f6d75;
  uVar2 = _UNK_004f6d71;
  *(undefined4 *)((long)puStack_120 + 0xc) = _DAT_004f6d6d;
  *(undefined4 *)(puStack_120 + 2) = uVar2;
  *(undefined4 *)((long)puStack_120 + 0x14) = uVar3;
  *(undefined4 *)(puStack_120 + 3) = uVar4;
  *(undefined1 *)((long)puStack_120 + 0x1c) = 0;
  uStack_608 = 0x101;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d6980(param_2,auStack_130,&uStack_608,&uStack_610);
  if ((auStack_130[0] & 1) != 0) {
    func_0x04a8cfc8(puStack_120,auStack_130[0] & 0xfffffffffffffffe);
  }
  bStack_430 = 0x24;
  uStack_42f = _DAT_00863c21;
  uStack_42b = _UNK_00863c25;
  uStack_427 = _UNK_00863c29;
  uStack_423 = _UNK_00863c2d;
  uStack_41f = 0x7365;
  uStack_41d = 0;
  uStack_608 = 0x109;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d7b50(param_2,&bStack_430,&uStack_608,&uStack_610);
  if ((bStack_430 & 1) != 0) {
    func_0x04a8cfc8(CONCAT44(uStack_41c,CONCAT13(uStack_41d,CONCAT21(uStack_41f,uStack_423._3_1_))),
                    CONCAT35((undefined3)uStack_42b,CONCAT41(uStack_42f,bStack_430)) &
                    0xfffffffffffffffe);
  }
  puStack_138 = (undefined8 *)func_0x04b078f0(0x20);
  auStack_148[0] = 0x21;
  auStack_148[1] = 0x1b;
  uVar1 = CONCAT17(UNK_007b8c68,CONCAT43(_DAT_007b8c64,_UNK_007b8c61));
  *puStack_138 = _DAT_007b8c59;
  puStack_138[1] = uVar1;
  uVar4 = _UNK_007b8c70;
  uVar3 = _UNK_007b8c6c;
  uVar2 = _UNK_007b8c68;
  *(undefined4 *)((long)puStack_138 + 0xb) = _DAT_007b8c64;
  *(undefined4 *)((long)puStack_138 + 0xf) = uVar2;
  *(undefined4 *)((long)puStack_138 + 0x13) = uVar3;
  *(undefined4 *)((long)puStack_138 + 0x17) = uVar4;
  *(undefined1 *)((long)puStack_138 + 0x1b) = 0;
  uStack_608 = 0x111;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d7c40(param_2,auStack_148,&uStack_608,&uStack_610);
  if ((auStack_148[0] & 1) != 0) {
    func_0x04a8cfc8(puStack_138,auStack_148[0] & 0xfffffffffffffffe);
  }
  puStack_150 = (undefined8 *)func_0x04b078f0(0x28);
  uVar1 = _UNK_007034ec;
  auStack_160[0] = 0x29;
  auStack_160[1] = 0x21;
  *puStack_150 = _DAT_007034e4;
  puStack_150[1] = uVar1;
  uVar4 = _UNK_00703500;
  uVar3 = _UNK_007034fc;
  uVar2 = _UNK_007034f8;
  *(undefined4 *)(puStack_150 + 2) = _DAT_007034f4;
  *(undefined4 *)((long)puStack_150 + 0x14) = uVar2;
  *(undefined4 *)(puStack_150 + 3) = uVar3;
  *(undefined4 *)((long)puStack_150 + 0x1c) = uVar4;
  *(undefined2 *)(puStack_150 + 4) = 0x72;
  uStack_608 = 0x119;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d7d30(param_2,auStack_160,&uStack_608,&uStack_610);
  if ((auStack_160[0] & 1) != 0) {
    func_0x04a8cfc8(puStack_150,auStack_160[0] & 0xfffffffffffffffe);
  }
  puStack_168 = (undefined4 *)func_0x04b078f0(0x1a);
  uVar4 = _UNK_00844d14;
  uVar3 = _UNK_00844d10;
  uVar2 = _UNK_00844d0c;
  auStack_178[0] = 0x1b;
  auStack_178[1] = 0x17;
  *puStack_168 = _DAT_00844d08;
  puStack_168[1] = uVar2;
  puStack_168[2] = uVar3;
  puStack_168[3] = uVar4;
  *(undefined8 *)((long)puStack_168 + 0xf) = 0x7365646f43797265;
  *(undefined1 *)((long)puStack_168 + 0x17) = 0;
  uStack_608 = 0x121;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d7e20(param_2,auStack_178,&uStack_608,&uStack_610);
  if ((auStack_178[0] & 1) != 0) {
    func_0x04a8cfc8(puStack_168,auStack_178[0] & 0xfffffffffffffffe);
  }
  bStack_448 = 0x24;
  uStack_447 = _DAT_005a852e;
  uStack_443 = _UNK_005a8532;
  uStack_43f = _UNK_005a8536;
  uStack_43b = _UNK_005a853a;
  uStack_437 = 0x7375;
  uStack_435 = 0;
  uStack_608 = 0x129;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d7f10(param_2,&bStack_448,&uStack_608,&uStack_610);
  if ((bStack_448 & 1) != 0) {
    func_0x04a8cfc8(CONCAT44(uStack_434,CONCAT13(uStack_435,CONCAT21(uStack_437,uStack_43b._3_1_))),
                    CONCAT35((undefined3)uStack_443,CONCAT41(uStack_447,bStack_448)) &
                    0xfffffffffffffffe);
  }
  bStack_460 = 0x18;
  uStack_45f = 0x6b6f6f42746567;
  uStack_458 = 0x6d;
  uStack_457 = 0x736b7261;
  uStack_453 = 0;
  uStack_608 = 0x169;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d8000(param_2,&bStack_460,&uStack_608,&uStack_610);
  if ((bStack_460 & 1) != 0) {
    func_0x04a8cfc8(uStack_450,CONCAT71(uStack_45f,bStack_460) & 0xfffffffffffffffe);
  }
  bStack_478 = 0x1a;
  uStack_477 = 0x6449746567;
  uStack_472 = 0x6e65;
  uStack_470 = 0x74;
  uStack_46f = 0x7365697469;
  uStack_46a = 0;
  uStack_608 = 0x171;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d80f0(param_2,&bStack_478,&uStack_608,&uStack_610);
  if ((bStack_478 & 1) != 0) {
    func_0x04a8cfc8(uStack_468,
                    CONCAT26(uStack_472,CONCAT51(uStack_477,bStack_478)) & 0xfffffffffffffffe);
  }
  bStack_490 = 0x1e;
  uStack_48f = 0x73696857746567;
  uStack_488 = 0x70;
  uStack_487 = 0x737473696c7265;
  uStack_480 = 0;
  uStack_608 = 0x179;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d80f0(param_2,&bStack_490,&uStack_608,&uStack_610);
  if ((bStack_490 & 1) != 0) {
    func_0x04a8cfc8(CONCAT71(uStack_47f,uStack_480),
                    CONCAT71(uStack_48f,bStack_490) & 0xfffffffffffffffe);
  }
  bStack_4a8 = 0x16;
  uStack_4a7 = 0x746e6f43746567;
  uStack_4a0 = 0x73746361;
  uStack_49c = 0;
  uStack_608 = 0x181;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d80f0(param_2,&bStack_4a8,&uStack_608,&uStack_610);
  if ((bStack_4a8 & 1) != 0) {
    func_0x04a8cfc8(uStack_498,CONCAT71(uStack_4a7,bStack_4a8) & 0xfffffffffffffffe);
  }
  puStack_180 = (undefined4 *)func_0x04b078f0(0x20);
  uVar4 = _UNK_00844d2c;
  uVar3 = _UNK_00844d28;
  uVar2 = _UNK_00844d24;
  auStack_190[0] = 0x21;
  auStack_190[1] = 0x18;
  *puStack_180 = _DAT_00844d20;
  puStack_180[1] = uVar2;
  puStack_180[2] = uVar3;
  puStack_180[3] = uVar4;
  *(undefined8 *)(puStack_180 + 4) = 0x736e6f697463416d;
  *(undefined1 *)(puStack_180 + 6) = 0;
  uStack_608 = 0x189;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d81e0(param_2,auStack_190,&uStack_608,&uStack_610);
  if ((auStack_190[0] & 1) != 0) {
    func_0x04a8cfc8(puStack_180,auStack_190[0] & 0xfffffffffffffffe);
  }
  bStack_4c0 = 0x26;
  uStack_4bf = _DAT_004d779e;
  uStack_4bb = _UNK_004d77a2;
  uStack_4b7 = _UNK_004d77a6;
  uStack_4b3 = (undefined3)_UNK_004d77aa;
  uStack_4b0 = 0x74;
  uStack_4af = 0x657461;
  uStack_608 = 0x191;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d82d0(param_2,&bStack_4c0,&uStack_608,&uStack_610);
  if ((bStack_4c0 & 1) != 0) {
    func_0x04a8cfc8(CONCAT35(uStack_4ab,CONCAT41(uStack_4af,uStack_4b0)),
                    CONCAT35((undefined3)uStack_4bb,CONCAT41(uStack_4bf,bStack_4c0)) &
                    0xfffffffffffffffe);
  }
  bStack_4d8 = 0x26;
  uStack_4d7 = _DAT_00863c34;
  uStack_4d3 = _UNK_00863c38;
  uStack_4cf = _UNK_00863c3c;
  uStack_4cb = (undefined3)_UNK_00863c40;
  uStack_4c8 = 0x74;
  uStack_4c7 = 0x657461;
  uStack_608 = 0x199;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d83c0(param_2,&bStack_4d8,&uStack_608,&uStack_610);
  if ((bStack_4d8 & 1) != 0) {
    func_0x04a8cfc8(CONCAT35(uStack_4c3,CONCAT41(uStack_4c7,uStack_4c8)),
                    CONCAT35((undefined3)uStack_4d3,CONCAT41(uStack_4d7,bStack_4d8)) &
                    0xfffffffffffffffe);
  }
  bStack_4f0 = 0x24;
  uStack_4ef = _DAT_008c1f94;
  uStack_4eb = _UNK_008c1f98;
  uStack_4e7 = _UNK_008c1f9c;
  uStack_4e3 = _UNK_008c1fa0;
  uStack_4df = 0x6f66;
  uStack_4dd = 0;
  uStack_608 = 0x131;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d6d40(param_2,&bStack_4f0,&uStack_608,&uStack_610);
  if ((bStack_4f0 & 1) != 0) {
    func_0x04a8cfc8(CONCAT44(uStack_4dc,CONCAT13(uStack_4dd,CONCAT21(uStack_4df,uStack_4e3._3_1_))),
                    CONCAT35((undefined3)uStack_4eb,CONCAT41(uStack_4ef,bStack_4f0)) &
                    0xfffffffffffffffe);
  }
  bStack_508 = 0x1e;
  uStack_507 = 0x426d6565646572;
  uStack_500 = 0x61;
  uStack_4ff = 0x65646f43656764;
  uStack_4f8 = 0;
  uStack_608 = 0x139;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d6b60(param_2,&bStack_508,&uStack_608,&uStack_610);
  if ((bStack_508 & 1) != 0) {
    func_0x04a8cfc8(CONCAT71(uStack_4f7,uStack_4f8),
                    CONCAT71(uStack_507,bStack_508) & 0xfffffffffffffffe);
  }
  bStack_520 = 0x18;
  uStack_51f = 0x67646142746567;
  uStack_518 = 0x65;
  uStack_517 = 0x6f666e49;
  uStack_513 = 0;
  uStack_608 = 0x141;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d6d40(param_2,&bStack_520,&uStack_608,&uStack_610);
  if ((bStack_520 & 1) != 0) {
    func_0x04a8cfc8(uStack_510,CONCAT71(uStack_51f,bStack_520) & 0xfffffffffffffffe);
  }
  bStack_538 = 0x1a;
  uStack_537 = 0x7355746567;
  uStack_532 = 0x7265;
  uStack_530 = 0x42;
  uStack_52f = 0x7365676461;
  uStack_52a = 0;
  uStack_608 = 0x149;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d84b0(param_2,&bStack_538,&uStack_608,&uStack_610);
  if ((bStack_538 & 1) != 0) {
    func_0x04a8cfc8(uStack_528,
                    CONCAT26(uStack_532,CONCAT51(uStack_537,bStack_538)) & 0xfffffffffffffffe);
  }
  bStack_550 = 0x1e;
  uStack_54f = 0x67646142746567;
  uStack_548 = 0x65;
  uStack_547 = 0x72657355664f73;
  uStack_540 = 0;
  uStack_608 = 0x151;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d85a0(param_2,&bStack_550,&uStack_608,&uStack_610);
  if ((bStack_550 & 1) != 0) {
    func_0x04a8cfc8(CONCAT71(uStack_53f,uStack_540),
                    CONCAT71(uStack_54f,bStack_550) & 0xfffffffffffffffe);
  }
  bStack_568 = 0x1e;
  uStack_567 = 0x69746341746567;
  uStack_560 = 0x76;
  uStack_55f = 0x73656764614265;
  uStack_558 = 0;
  uStack_608 = 0x159;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d74c0(param_2,&bStack_568,&uStack_608,&uStack_610);
  if ((bStack_568 & 1) != 0) {
    func_0x04a8cfc8(CONCAT71(uStack_557,uStack_558),
                    CONCAT71(uStack_567,bStack_568) & 0xfffffffffffffffe);
  }
  bStack_580 = 0x1e;
  uStack_57f = 0x69746341746573;
  uStack_578 = 0x76;
  uStack_577 = 0x73656764614265;
  uStack_570 = 0;
  uStack_608 = 0x161;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d8690(param_2,&bStack_580,&uStack_608,&uStack_610);
  if ((bStack_580 & 1) != 0) {
    func_0x04a8cfc8(CONCAT71(uStack_56f,uStack_570),
                    CONCAT71(uStack_57f,bStack_580) & 0xfffffffffffffffe);
  }
  bStack_598 = 0x2c;
  uStack_597 = _DAT_00844d39;
  uStack_593 = _UNK_00844d3d;
  uStack_58f = _UNK_00844d41;
  uStack_58b = (undefined2)_UNK_00844d45;
  uStack_589 = 0x65;
  uStack_588 = 0x72;
  uStack_587 = 0x64616f6c7055;
  uStack_581 = 0;
  uStack_608 = 0x1a1;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d8780(param_2,&bStack_598,&uStack_608,&uStack_610);
  if ((bStack_598 & 1) != 0) {
    func_0x04a8cfc8(CONCAT17(uStack_581,CONCAT61(uStack_587,uStack_588)),
                    CONCAT35((undefined3)uStack_593,CONCAT41(uStack_597,bStack_598)) &
                    0xfffffffffffffffe);
  }
  puStack_198 = (undefined8 *)func_0x04b078f0(0x20);
  auStack_1a8[0] = 0x21;
  auStack_1a8[1] = 0x1b;
  uVar1 = CONCAT17(UNK_005c951c,CONCAT43(_DAT_005c9518,_UNK_005c9515));
  *puStack_198 = _DAT_005c950d;
  puStack_198[1] = uVar1;
  uVar4 = _UNK_005c9524;
  uVar3 = _UNK_005c9520;
  uVar2 = _UNK_005c951c;
  *(undefined4 *)((long)puStack_198 + 0xb) = _DAT_005c9518;
  *(undefined4 *)((long)puStack_198 + 0xf) = uVar2;
  *(undefined4 *)((long)puStack_198 + 0x13) = uVar3;
  *(undefined4 *)((long)puStack_198 + 0x17) = uVar4;
  *(undefined1 *)((long)puStack_198 + 0x1b) = 0;
  uStack_608 = 0x1a9;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d8870(param_2,auStack_1a8,&uStack_608,&uStack_610);
  if ((auStack_1a8[0] & 1) != 0) {
    func_0x04a8cfc8(puStack_198,auStack_1a8[0] & 0xfffffffffffffffe);
  }
  bStack_5b0 = 0x28;
  uStack_5af = _DAT_0071ecc8;
  uStack_5ab = _UNK_0071eccc;
  uStack_5a7 = _UNK_0071ecd0;
  uStack_5a3 = _UNK_0071ecd4;
  uStack_59f = 0x72656966;
  uStack_59b = 0;
  uStack_608 = 0x1b1;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d8870(param_2,&bStack_5b0,&uStack_608,&uStack_610);
  if ((bStack_5b0 & 1) != 0) {
    func_0x04a8cfc8(CONCAT26(uStack_59a,CONCAT15(uStack_59b,CONCAT41(uStack_59f,uStack_5a3._3_1_))),
                    CONCAT35((undefined3)uStack_5ab,CONCAT41(uStack_5af,bStack_5b0)) &
                    0xfffffffffffffffe);
  }
  puStack_1b0 = (undefined4 *)func_0x04b078f0(0x1a);
  uVar4 = _UNK_005e5018;
  uVar3 = _UNK_005e5014;
  uVar2 = _UNK_005e5010;
  auStack_1c0[0] = 0x1b;
  auStack_1c0[1] = 0x17;
  *puStack_1b0 = _DAT_005e500c;
  puStack_1b0[1] = uVar2;
  puStack_1b0[2] = uVar3;
  puStack_1b0[3] = uVar4;
  *(undefined8 *)((long)puStack_1b0 + 0xf) = 0x7265696669746e65;
  *(undefined1 *)((long)puStack_1b0 + 0x17) = 0;
  uStack_608 = 0x1b9;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d8870(param_2,auStack_1c0,&uStack_608,&uStack_610);
  if ((auStack_1c0[0] & 1) != 0) {
    func_0x04a8cfc8(puStack_1b0,auStack_1c0[0] & 0xfffffffffffffffe);
  }
  puStack_1c8 = (undefined8 *)func_0x04b078f0(0x20);
  auStack_1d8[0] = 0x21;
  auStack_1d8[1] = 0x1e;
  uVar1 = CONCAT26(_DAT_00863c56,_UNK_00863c50);
  *puStack_1c8 = _DAT_00863c48;
  puStack_1c8[1] = uVar1;
  uVar4 = _UNK_00863c62;
  uVar3 = _UNK_00863c5e;
  uVar2 = _UNK_00863c5a;
  *(uint *)((long)puStack_1c8 + 0xe) = CONCAT22(_UNK_00863c58,_DAT_00863c56);
  *(undefined4 *)((long)puStack_1c8 + 0x12) = uVar2;
  *(undefined4 *)((long)puStack_1c8 + 0x16) = uVar3;
  *(undefined4 *)((long)puStack_1c8 + 0x1a) = uVar4;
  *(undefined1 *)((long)puStack_1c8 + 0x1e) = 0;
  uStack_608 = 0x1c1;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d73d0(param_2,auStack_1d8,&uStack_608,&uStack_610);
  if ((auStack_1d8[0] & 1) != 0) {
    func_0x04a8cfc8(puStack_1c8,auStack_1d8[0] & 0xfffffffffffffffe);
  }
  puStack_1e0 = (undefined8 *)func_0x04b078f0(0x20);
  auStack_1f0[0] = 0x21;
  auStack_1f0[1] = 0x1f;
  uVar1 = CONCAT17(DAT_0082ae99,_UNK_0082ae92);
  *puStack_1e0 = _DAT_0082ae8a;
  puStack_1e0[1] = uVar1;
  uVar4 = _UNK_0082aea5;
  uVar3 = _UNK_0082aea1;
  uVar2 = _UNK_0082ae9d;
  *(undefined4 *)((long)puStack_1e0 + 0xf) = _DAT_0082ae99;
  *(undefined4 *)((long)puStack_1e0 + 0x13) = uVar2;
  *(undefined4 *)((long)puStack_1e0 + 0x17) = uVar3;
  *(undefined4 *)((long)puStack_1e0 + 0x1b) = uVar4;
  *(undefined1 *)((long)puStack_1e0 + 0x1f) = 0;
  uStack_608 = 0x1c9;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d73d0(param_2,auStack_1f0,&uStack_608,&uStack_610);
  if ((auStack_1f0[0] & 1) != 0) {
    func_0x04a8cfc8(puStack_1e0,auStack_1f0[0] & 0xfffffffffffffffe);
  }
  bStack_208 = 0x22;
  uStack_207 = _DAT_00703516;
  uStack_203 = _UNK_0070351a;
  uStack_1ff = _UNK_0070351e;
  uStack_1fb = _UNK_00703522;
  uStack_1f7 = 0x73;
  uStack_608 = 0x1d1;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d8000(param_2,&bStack_208,&uStack_608,&uStack_610);
  if ((bStack_208 & 1) != 0) {
    func_0x04a8cfc8(CONCAT53(uStack_1f5,CONCAT21(uStack_1f7,uStack_1fb._3_1_)),
                    CONCAT35((undefined3)uStack_203,CONCAT41(uStack_207,bStack_208)) &
                    0xfffffffffffffffe);
  }
  bStack_5c8 = 0x24;
  uStack_5c7 = _DAT_004d77b2;
  uStack_5c3 = _UNK_004d77b6;
  uStack_5bf = _UNK_004d77ba;
  uStack_5bb = _UNK_004d77be;
  uStack_5b7 = 0x7265;
  uStack_5b5 = 0;
  uStack_608 = 0x1d9;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d8960(param_2,&bStack_5c8,&uStack_608,&uStack_610);
  if ((bStack_5c8 & 1) != 0) {
    func_0x04a8cfc8(CONCAT44(uStack_5b4,CONCAT13(uStack_5b5,CONCAT21(uStack_5b7,uStack_5bb._3_1_))),
                    CONCAT35((undefined3)uStack_5c3,CONCAT41(uStack_5c7,bStack_5c8)) &
                    0xfffffffffffffffe);
  }
  bStack_5e0 = 0x26;
  uStack_5df = _DAT_006a1620;
  uStack_5db = _UNK_006a1624;
  uStack_5d7 = _UNK_006a1628;
  uStack_5d3 = (undefined3)_UNK_006a162c;
  uStack_5d0 = 0x72;
  uStack_5cf = 0x726576;
  uStack_608 = 0x1e1;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d6b60(param_2,&bStack_5e0,&uStack_608,&uStack_610);
  if ((bStack_5e0 & 1) != 0) {
    func_0x04a8cfc8(CONCAT35(uStack_5cb,CONCAT41(uStack_5cf,uStack_5d0)),
                    CONCAT35((undefined3)uStack_5db,CONCAT41(uStack_5df,bStack_5e0)) &
                    0xfffffffffffffffe);
  }
  bStack_5f8 = 0x24;
  uStack_5f7 = _DAT_00512533;
  uStack_5f3 = _UNK_00512537;
  uStack_5ef = _UNK_0051253b;
  uStack_5eb = _UNK_0051253f;
  uStack_5e7 = 0x7265;
  uStack_5e5 = 0;
  uStack_608 = 0x1e9;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d8a50(param_2,&bStack_5f8,&uStack_608,&uStack_610);
  if ((bStack_5f8 & 1) != 0) {
    func_0x04a8cfc8(CONCAT44(uStack_5e4,CONCAT13(uStack_5e5,CONCAT21(uStack_5e7,uStack_5eb._3_1_))),
                    CONCAT35((undefined3)uStack_5f3,CONCAT41(uStack_5f7,bStack_5f8)) &
                    0xfffffffffffffffe);
  }
  bStack_220 = 0x22;
  uStack_21f = _DAT_0071ecdd;
  uStack_21b = _UNK_0071ece1;
  uStack_217 = _UNK_0071ece5;
  uStack_213 = _UNK_0071ece9;
  uStack_20f = 0x72;
  uStack_608 = 0x1f1;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d8a50(param_2,&bStack_220,&uStack_608,&uStack_610);
  if ((bStack_220 & 1) != 0) {
    func_0x04a8cfc8(CONCAT53(uStack_20d,CONCAT21(uStack_20f,uStack_213._3_1_)),
                    CONCAT35((undefined3)uStack_21b,CONCAT41(uStack_21f,bStack_220)) &
                    0xfffffffffffffffe);
  }
  puStack_228 = (undefined4 *)func_0x04b078f0(0x1a);
  uVar4 = _UNK_0052d85e;
  uVar3 = _UNK_0052d85a;
  uVar2 = _UNK_0052d856;
  auStack_238[0] = 0x1b;
  auStack_238[1] = 0x17;
  *puStack_228 = _DAT_0052d852;
  puStack_228[1] = uVar2;
  puStack_228[2] = uVar3;
  puStack_228[3] = uVar4;
  *(undefined8 *)((long)puStack_228 + 0xf) = 0x7375746174735f72;
  *(undefined1 *)((long)puStack_228 + 0x17) = 0;
  uStack_608 = 0x1f9;
  uStack_600 = 0;
  uStack_610 = param_1;
  func_0x026d8a50(param_2,auStack_238,&uStack_608,&uStack_610);
  if ((auStack_238[0] & 1) != 0) {
    func_0x04a8cfc8(puStack_228,auStack_238[0] & 0xfffffffffffffffe);
  }
  return;
}

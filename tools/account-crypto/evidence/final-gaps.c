// FUNCTION 02b7f780 bounds 02b7f89f

ulong * FUN_02b7f780(ulong *param_1,byte *param_2,undefined8 param_3)

{
  byte bVar1;
  char cVar2;
  ulong uVar3;
  undefined1 *puVar4;
  long *plVar5;
  undefined8 uVar6;
  undefined8 *puVar7;
  ulong *puVar8;
  ulong uVar9;
  ulong uVar10;

  *param_1 = 0;
  param_1[1] = 0;
  param_1[2] = 0;
  bVar1 = *param_2;
  if ((bVar1 & 1) == 0) {
    uVar10 = (ulong)(bVar1 >> 1);
  }
  else {
    uVar10 = *(ulong *)(param_2 + 8);
  }
  if (0xfffffffffffffff6 < uVar10) {
    uVar6 = func_0x019f8660();
    puVar7 = (undefined8 *)func_0x019f83c0(uVar6);
    func_0x019f83c0();
    *puVar7 = 0;
    puVar8 = (ulong *)FUN_02b7f8e0(puVar7 + 1,(int)cRam0000000004ca79a1);
    puVar7[8] = 0;
    puVar7[9] = 0;
    puVar7[6] = 0;
    puVar7[7] = 0;
    puVar7[4] = 0;
    puVar7[5] = 0;
    puVar7[2] = 0;
    puVar7[3] = 0;
    puVar7[10] = 0;
    return puVar8;
  }
  if (uVar10 < 0x17) {
    if ((bVar1 & 1) == 0) {
      param_2 = param_2 + 1;
    }
    else {
      param_2 = *(byte **)(param_2 + 0x10);
    }
    if (uVar10 == 0) {
      return param_1;
    }
  }
  else {
    uVar3 = (uVar10 & 0xfffffffffffffff8) + 8;
    uVar9 = 0x1a;
    if (uVar3 != 0x18) {
      uVar9 = uVar3;
    }
    puVar4 = (undefined1 *)func_0x04b078f0(uVar9);
    *puVar4 = *(undefined1 *)((long)param_1 + 1);
    *param_1 = uVar9 | 1;
    param_1[1] = 0;
    param_1[2] = (ulong)puVar4;
    if ((bVar1 & 1) == 0) {
      param_2 = param_2 + 1;
    }
    else {
      param_2 = *(byte **)(param_2 + 0x10);
    }
  }
  uVar9 = 0;
  do {
    bVar1 = param_2[uVar9];
    plVar5 = (long *)func_0x04aeb1d0(param_3,&DAT_04de06e0);
    cVar2 = (**(code **)(*plVar5 + 0x28))(plVar5,(int)(char)bVar1);
    func_0x04aacac0(param_1,(int)cVar2);
    uVar9 = uVar9 + 1;
  } while (uVar10 != uVar9);
  return param_1;
}


// FUNCTION 02b81970 bounds 02b8198f

undefined8 FUN_02b81970(undefined8 param_1)

{
  FUN_026421e0();
  return param_1;
}


// FUNCTION 0480cdc0 bounds 0480cde9

undefined8 FUN_0480cdc0(undefined8 param_1,undefined8 *param_2)

{
  func_0x0480daa0(param_1,*param_2);
  return param_1;
}


// FUNCTION 0480daa0 bounds 0480dc6f

undefined8 *
FUN_0480daa0(undefined8 *param_1,undefined8 *param_2,undefined8 param_3,undefined8 param_4,
            undefined8 param_5,undefined8 param_6,undefined4 param_7,undefined4 param_8,
            undefined1 param_9)

{
  long *plVar1;
  long *plVar2;
  long lVar3;
  long *plVar4;
  long *plVar5;
  long *plStack_58;
  long *plStack_50;
  undefined8 uStack_48;
  long *plStack_40;

  plVar5 = (long *)func_0x04b078f0(0x20);
  plVar5[1] = 0;
  plVar5[2] = 0;
  *plVar5 = (long)&UNK_04c89318;
  plVar1 = plVar5 + 3;
  *(undefined1 *)(plVar5 + 3) = 0;
  LOCK();
  plVar5[1] = plVar5[1] + 1;
  UNLOCK();
  uStack_48 = 0;
  plStack_40 = (long *)0x0;
  plStack_58 = plVar1;
  plStack_50 = plVar5;
  func_0x04834b10(plVar1,&plStack_58,param_3,param_4,param_5,param_6,param_7,param_8,*param_2,
                  param_9,0);
  plVar4 = plStack_50;
  if (plStack_50 != (long *)0x0) {
    LOCK();
    plVar2 = plStack_50 + 1;
    lVar3 = *plVar2;
    *plVar2 = *plVar2 + -1;
    UNLOCK();
    if (lVar3 == 0) {
      (**(code **)(*plStack_50 + 0x10))(plStack_50);
      func_0x04aa8780(plVar4);
    }
  }
  plVar4 = plStack_40;
  if (plStack_40 != (long *)0x0) {
    LOCK();
    plVar2 = plStack_40 + 1;
    lVar3 = *plVar2;
    *plVar2 = *plVar2 + -1;
    UNLOCK();
    if (lVar3 == 0) {
      (**(code **)(*plStack_40 + 0x10))(plStack_40);
      func_0x04aa8780(plVar4);
    }
  }
  if (plVar5 == (long *)0x0) {
    *param_1 = 0x18;
    param_1[1] = 0;
  }
  else {
    LOCK();
    plVar5[1] = plVar5[1] + 1;
    UNLOCK();
    *param_1 = plVar1;
    param_1[1] = plVar5;
    LOCK();
    plVar5[1] = plVar5[1] + 1;
    UNLOCK();
    LOCK();
    plVar1 = plVar5 + 1;
    lVar3 = *plVar1;
    *plVar1 = *plVar1 + -1;
    UNLOCK();
    if (lVar3 == 0) {
      (**(code **)(*plVar5 + 0x10))(plVar5);
      func_0x04aa8780(plVar5);
    }
  }
  if (plVar5 != (long *)0x0) {
    LOCK();
    plVar1 = plVar5 + 1;
    lVar3 = *plVar1;
    *plVar1 = *plVar1 + -1;
    UNLOCK();
    if (lVar3 == 0) {
      (**(code **)(*plVar5 + 0x10))(plVar5);
      func_0x04aa8780(plVar5);
    }
  }
  return param_1;
}


// FUNCTION 04812180 bounds 0481225f

undefined8 * FUN_04812180(undefined8 *param_1,undefined8 param_2)

{
  undefined8 uVar1;
  ulong auStack_58 [4];
  ulong auStack_38 [4];

  uVar1 = func_0x04b078f0(0x10);
  auStack_38[0] = 0;
  auStack_38[1] = 0;
  auStack_38[2] = 0;
  auStack_58[0] = 0;
  auStack_58[1] = 0;
  auStack_58[2] = 0;
  func_0x04812260(uVar1,param_2,auStack_38,auStack_58);
  if ((auStack_58[0] & 1) != 0) {
    func_0x04a8cfc8(auStack_58[2],auStack_58[0] & 0xfffffffffffffffe);
  }
  if ((auStack_38[0] & 1) != 0) {
    func_0x04a8cfc8(auStack_38[2],auStack_38[0] & 0xfffffffffffffffe);
  }
  *param_1 = uVar1;
  return param_1;
}


// FUNCTION 04810e10 bounds 0481116f

undefined8 * FUN_04810e10(undefined8 *param_1,long *param_2)

{
  undefined1 uVar1;
  ulong *puVar2;
  long *plVar3;
  code *pcVar4;
  undefined4 uVar5;
  undefined4 uVar6;
  long lVar7;
  ulong uVar8;
  long *plVar9;
  ulong uVar10;
  undefined8 uVar11;
  undefined8 *puVar12;
  ulong uVar13;
  ulong uVar14;
  ulong uVar15;
  ulong uVar16;
  ulong uVar17;
  bool bVar18;
  undefined8 uStack_88;
  ulong uStack_80;
  ulong uStack_78;
  undefined8 *puStack_68;
  ulong uStack_60;
  long *plStack_58;
  long *plStack_50;
  long *plStack_48;
  ulong uStack_40;
  long lStack_38;

  *param_1 = 0;
  param_1[1] = 0;
  param_1[2] = 0;
  plStack_50 = (long *)*param_2;
  plStack_58 = param_2 + 1;
  puStack_68 = param_1;
  if (plStack_50 != plStack_58) {
    do {
      uStack_88 = 0;
      uStack_80 = 0;
      uStack_78 = 0;
      bVar18 = (*(byte *)(plStack_50 + 4) & 1) != 0;
      lStack_38 = (long)plStack_50 + 0x21;
      if (bVar18) {
        lStack_38 = plStack_50[6];
      }
      uVar17 = (ulong)(*(byte *)(plStack_50 + 4) >> 1);
      if (bVar18) {
        uVar17 = plStack_50[5];
      }
      if (uVar17 != 0) {
        plVar9 = (long *)func_0x04b07b90();
        uVar15 = 0;
        plStack_48 = plVar9;
        uStack_40 = uVar17;
        do {
          uVar1 = *(undefined1 *)(*plVar9 + (ulong)*(byte *)(lStack_38 + uVar15) * 4);
          if ((uStack_88 & 1) == 0) {
            if ((byte)uStack_88 == 0x2c) {
              uVar17 = 0x30;
              uVar16 = 0x16;
              goto LAB_04810f93;
            }
            uVar13 = (ulong)((byte)uStack_88 >> 1);
            uStack_88 = CONCAT71(uStack_88._1_7_,(byte)uStack_88 + 2);
            uVar16 = (long)&uStack_88 + 1;
          }
          else {
            uStack_60 = uStack_88;
            uVar13 = uStack_88 & 0xfffffffffffffffe;
            uVar16 = uVar13 - 1;
            if (uStack_80 == uVar16) {
              if (0xfffffffffffffff6 < uVar13) {
                uVar11 = func_0x019f8660();
                if ((uStack_88 & 1) != 0) {
                  func_0x04a8cfc8(uStack_78,uStack_88 & 0xfffffffffffffffe);
                }
                func_0x0480cf50(puStack_68);
                func_0x04b07a10(uVar11);
                pcVar4 = (code *)swi(3);
                puVar12 = (undefined8 *)(*pcVar4)();
                return puVar12;
              }
              uVar17 = 0xfffffffffffffff8;
              if (uVar16 < 0x7ffffffffffffff4) {
                uVar17 = uVar16 * 2;
                if (uVar16 * 2 < uVar13) {
                  uVar17 = uVar13;
                }
                uVar13 = (uVar17 & 0xfffffffffffffff8) + 8;
                uVar17 = 0x1a;
                if (uVar13 != 0x18) {
                  uVar17 = uVar13;
                }
              }
LAB_04810f93:
              uStack_60 = uStack_88;
              uVar14 = uStack_88 & 0xfffffffffffffffe;
              uVar10 = func_0x04b078f0(uVar17);
              uVar8 = uStack_78;
              uVar13 = uStack_78;
              if ((uStack_60 & 1) == 0) {
                uVar13 = (long)&uStack_88 + 1;
              }
              func_0x04b07a30(uVar10,uVar13,uVar16);
              if ((uStack_60 & 1) != 0) {
                func_0x04a8cfc8(uVar8,uVar14);
              }
              uStack_88 = uVar17 | 1;
              plVar9 = plStack_48;
              uStack_80 = uVar16;
              uVar17 = uStack_40;
              uStack_78 = uVar10;
            }
            uVar13 = uStack_80;
            uVar16 = uStack_78;
            uStack_80 = uStack_80 + 1;
          }
          *(undefined1 *)(uVar16 + uVar13) = uVar1;
          *(undefined1 *)(uVar16 + 1 + uVar13) = 0;
          uVar15 = uVar15 + 1;
        } while (uVar17 != uVar15);
      }
      plVar9 = plStack_50;
      puVar2 = (ulong *)puStack_68[1];
      if (puVar2 < (ulong *)puStack_68[2]) {
        puVar2[2] = uStack_78;
        *puVar2 = uStack_88;
        puVar2[1] = uStack_80;
        uStack_88 = 0;
        uStack_80 = 0;
        uStack_78 = 0;
        if ((*(byte *)(plStack_50 + 7) & 1) == 0) {
          puVar2[5] = plStack_50[9];
          uVar5 = *(undefined4 *)((long)plStack_50 + 0x3c);
          lVar7 = plStack_50[8];
          uVar6 = *(undefined4 *)((long)plStack_50 + 0x44);
          *(undefined4 *)(puVar2 + 3) = *(undefined4 *)(plStack_50 + 7);
          *(undefined4 *)((long)puVar2 + 0x1c) = uVar5;
          *(int *)(puVar2 + 4) = (int)lVar7;
          *(undefined4 *)((long)puVar2 + 0x24) = uVar6;
        }
        else {
          func_0x019f86e0(puVar2 + 3,plStack_50[9],plStack_50[8]);
        }
        puStack_68[1] = puVar2 + 6;
      }
      else {
        uVar11 = func_0x048142d0(puStack_68,&uStack_88);
        puStack_68[1] = uVar11;
      }
      if ((uStack_88 & 1) != 0) {
        func_0x04a8cfc8(uStack_78,uStack_88 & 0xfffffffffffffffe);
      }
      plVar3 = (long *)plVar9[1];
      if (plVar3 == (long *)0x0) {
        do {
          plStack_50 = (long *)plVar9[2];
          bVar18 = plVar9 != (long *)*plStack_50;
          plVar9 = plStack_50;
        } while (bVar18);
      }
      else {
        do {
          plStack_50 = plVar3;
          plVar3 = (long *)*plStack_50;
        } while ((long *)*plStack_50 != (long *)0x0);
      }
    } while (plStack_50 != plStack_58);
  }
  return puStack_68;
}


// FUNCTION 04811170 bounds 048112af

long * FUN_04811170(long *param_1,long *param_2,long param_3)

{
  code *pcVar1;
  long lVar2;
  long lVar3;
  undefined8 uVar4;
  long *plVar5;
  long lVar6;
  long unaff_R13;
  ulong uVar7;
  long *plStack_38;
  undefined1 uStack_30;

  if ((*(byte *)(param_3 + 0x18) & 1) == 0) {
    uVar7 = param_2[1];
    if (*(byte *)(param_3 + 0x18) >> 1 == 0) goto LAB_048111ce;
  }
  else {
    uVar7 = param_2[1];
    if (*(long *)(param_3 + 0x20) == 0) goto LAB_048111ce;
  }
  if (uVar7 < (ulong)param_2[2]) {
    func_0x04814470(uVar7,&UNK_007b9bb9);
    uVar7 = uVar7 + 0x30;
    param_2[1] = uVar7;
  }
  else {
    uVar7 = func_0x04814550(param_2,&UNK_007b9bb9,param_3 + 0x18);
  }
  param_2[1] = uVar7;
LAB_048111ce:
  *param_1 = 0;
  param_1[1] = 0;
  param_1[2] = 0;
  lVar3 = *param_2;
  uStack_30 = 0;
  lVar6 = uVar7 - lVar3;
  if (lVar6 != 0) {
    plStack_38 = param_1;
    if (0x555555555555555 < (ulong)((lVar6 >> 4) * -0x5555555555555555)) {
      uVar4 = func_0x01a24420();
      *(ulong *)(lVar3 + 8) = uVar7;
      uVar4 = func_0x04b07a10(uVar4);
      param_1[1] = unaff_R13;
      func_0x0261de20(&plStack_38);
      uVar4 = func_0x04b07a10(uVar4);
      func_0x0261de20(&plStack_38);
      func_0x04b07a10(uVar4);
      pcVar1 = (code *)swi(3);
      plVar5 = (long *)(*pcVar1)();
      return plVar5;
    }
    lVar2 = func_0x04b078f0(lVar6);
    *param_1 = lVar2;
    param_1[1] = lVar2;
    param_1[2] = lVar6 + lVar2;
    lVar3 = func_0x02baebc0(param_1 + 2,lVar3,uVar7,lVar2);
    param_1[1] = lVar3;
  }
  return param_1;
}


// FUNCTION 04847620 bounds 0484770f

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined8 FUN_04847620(long *param_1)

{
  int iVar1;
  int iVar2;
  undefined8 uVar3;
  long *plVar4;
  long lVar5;
  long lVar6;

  lVar6 = *param_1;
  if (lVar6 == 0) {
    if ((DAT_04ddc230 & 1) == 0) {
      lVar5 = func_0x039b91f0(0x4ddc231,DAT_04ddc230 >> 1);
    }
    else {
      lVar5 = func_0x039b91f0(_DAT_04ddc240,_DAT_04ddc238);
    }
    if (lVar5 == 0) {
      return 0;
    }
    lVar6 = func_0x03b1aa30(lVar5,0,0,0);
    func_0x039aee10(lVar5);
    if (lVar6 == 0) {
      return 0;
    }
    *param_1 = lVar6;
  }
  uVar3 = func_0x03b3a580();
  iVar1 = func_0x03b0d6e0(lVar6);
  if (0 < iVar1) {
    iVar1 = 0;
    do {
      plVar4 = (long *)func_0x03b0d6f0(lVar6,iVar1);
      if (*plVar4 != 0) {
        func_0x03b3ad90(uVar3);
      }
      if (plVar4[1] != 0) {
        func_0x03b3af10(uVar3);
      }
      iVar1 = iVar1 + 1;
      iVar2 = func_0x03b0d6e0(lVar6);
    } while (iVar1 < iVar2);
  }
  return uVar3;
}


// FUNCTION 04847710 bounds 0484784f

undefined8 * FUN_04847710(undefined8 *param_1,long param_2)

{
  byte *pbVar1;
  byte bVar2;
  long lVar3;
  ulong uVar4;
  ulong uVar5;
  undefined8 uVar6;
  byte *pbVar7;
  byte *pbVar8;
  bool bVar9;
  undefined4 uVar10;
  undefined4 uVar11;
  undefined4 uVar12;
  undefined4 uVar13;

  pbVar1 = (byte *)(param_2 + 0x20);
  bVar2 = *(byte *)(param_2 + 0x20);
  uVar5 = *(ulong *)(param_2 + 0x28);
  uVar4 = (ulong)(bVar2 >> 1);
  if ((bVar2 & 1) != 0) {
    uVar4 = uVar5;
  }
  if (uVar4 == 0) {
    pbVar7 = *(byte **)(param_2 + 0x10);
    for (pbVar8 = *(byte **)(param_2 + 8); pbVar8 != pbVar7; pbVar8 = pbVar8 + 0x18) {
      if ((*pbVar8 & 1) == 0) {
        lVar3 = func_0x04b07c30(pbVar8 + 1,&UNK_007917dc);
      }
      else {
        lVar3 = func_0x04b07c30(*(undefined8 *)(pbVar8 + 0x10),&UNK_007917dc);
      }
      if (lVar3 != 0) {
        if (pbVar8 != pbVar1) {
          bVar2 = *pbVar8;
          if ((*pbVar1 & 1) == 0) {
            if ((bVar2 & 1) == 0) {
              *(undefined8 *)(param_2 + 0x30) = *(undefined8 *)(pbVar8 + 0x10);
              uVar10 = *(undefined4 *)(pbVar8 + 4);
              uVar11 = *(undefined4 *)(pbVar8 + 8);
              uVar12 = *(undefined4 *)(pbVar8 + 0xc);
              *(undefined4 *)pbVar1 = *(undefined4 *)pbVar8;
              *(undefined4 *)(param_2 + 0x24) = uVar10;
              *(undefined4 *)(param_2 + 0x28) = uVar11;
              *(undefined4 *)(param_2 + 0x2c) = uVar12;
            }
            else {
              func_0x01a126b0(pbVar1,*(undefined8 *)(pbVar8 + 0x10),*(undefined8 *)(pbVar8 + 8));
            }
          }
          else {
            bVar9 = (bVar2 & 1) != 0;
            pbVar7 = pbVar8 + 1;
            if (bVar9) {
              pbVar7 = *(byte **)(pbVar8 + 0x10);
            }
            uVar5 = (ulong)(bVar2 >> 1);
            if (bVar9) {
              uVar5 = *(ulong *)(pbVar8 + 8);
            }
            func_0x01a125a0(pbVar1,pbVar7,uVar5);
          }
        }
        if ((*pbVar8 & 1) == 0) {
          param_1[2] = *(undefined8 *)(pbVar8 + 0x10);
          uVar10 = *(undefined4 *)pbVar8;
          uVar11 = *(undefined4 *)(pbVar8 + 4);
          uVar12 = *(undefined4 *)(pbVar8 + 8);
          uVar13 = *(undefined4 *)(pbVar8 + 0xc);
          goto LAB_048477b7;
        }
        uVar5 = *(ulong *)(pbVar8 + 8);
        uVar6 = *(undefined8 *)(pbVar8 + 0x10);
        goto LAB_0484773f;
      }
    }
    *param_1 = 0;
    param_1[1] = 0;
    param_1[2] = 0;
  }
  else if ((bVar2 & 1) == 0) {
    param_1[2] = *(undefined8 *)(param_2 + 0x30);
    uVar10 = *(undefined4 *)pbVar1;
    uVar11 = *(undefined4 *)(param_2 + 0x24);
    uVar12 = *(undefined4 *)(param_2 + 0x28);
    uVar13 = *(undefined4 *)(param_2 + 0x2c);
LAB_048477b7:
    *(undefined4 *)param_1 = uVar10;
    *(undefined4 *)((long)param_1 + 4) = uVar11;
    *(undefined4 *)(param_1 + 1) = uVar12;
    *(undefined4 *)((long)param_1 + 0xc) = uVar13;
  }
  else {
    uVar6 = *(undefined8 *)(param_2 + 0x30);
LAB_0484773f:
    func_0x019f86e0(param_1,uVar6,uVar5);
  }
  return param_1;
}

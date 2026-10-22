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


// FUNCTION 02b81930 bounds 02b8194f

undefined8 FUN_02b81930(undefined8 param_1)

{
  func_0x02b83320();
  return param_1;
}


// FUNCTION 02b81950 bounds 02b8196f

undefined8 FUN_02b81950(undefined8 param_1)

{
  func_0x02b83320();
  return param_1;
}


// FUNCTION 02b81970 bounds 02b8198f

undefined8 FUN_02b81970(undefined8 param_1)

{
  func_0x026421e0();
  return param_1;
}


// FUNCTION 02b819b0 bounds 02b81f7f

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

uint * FUN_02b819b0(uint *param_1,undefined8 param_2,byte *param_3,undefined8 param_4,
                   undefined8 param_5,int param_6)

{
  code *pcVar1;
  long *plVar2;
  ulong uVar3;
  long lVar4;
  long lVar5;
  ulong uVar6;
  ulong uVar7;
  undefined8 uStack_1b8;
  ulong uStack_1b0;
  long lStack_1a8;
  long *plStack_1a0;
  byte bStack_198;
  undefined1 uStack_197;
  undefined6 uStack_196;
  undefined8 uStack_188;
  uint uStack_180;
  uint uStack_17c;
  uint uStack_178;
  uint uStack_174;
  undefined8 uStack_170;
  uint uStack_168;
  uint uStack_164;
  uint uStack_160;
  uint uStack_15c;
  undefined8 uStack_158;
  char cStack_150;
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

  func_0x04858de0(&plStack_1a0);
  if ((*param_3 & 1) == 0) {
    param_3 = param_3 + 1;
  }
  else {
    param_3 = *(byte **)(param_3 + 0x10);
  }
  (**(code **)(*plStack_1a0 + 0x10))(plStack_1a0,param_3);
  if (param_6 == 1) {
    func_0x02b4a050(&uStack_180,&plStack_1a0,param_5,param_4);
    if (cStack_150 == '\0') {
      func_0x01ab8040(alStack_148);
      func_0x01a53960(alStack_148 + 2,&UNK_00509a2c,0x28);
      if ((uStack_d0 & 0x10) == 0) {
        uVar7 = 0;
        lVar5 = lStack_120;
        lVar4 = 0;
        if ((uStack_d0 & 8) != 0) goto LAB_02b81cb4;
LAB_02b81cc7:
        lVar5 = lVar4;
        uStack_1b8 = CONCAT71(uStack_1b8._1_7_,(char)uVar7 * '\x02');
        lVar4 = (long)&uStack_1b8 + 1;
      }
      else {
        lVar5 = lStack_108;
        uStack_110 = uStack_d8;
        if (uStack_d8 < uStack_100) {
          uStack_d8 = uStack_100;
          uStack_110 = uStack_100;
        }
LAB_02b81cb4:
        uVar7 = uStack_110 - lVar5;
        if ((long)uVar7 < 0) {
LAB_02b81ed5:
                    /* WARNING: Does not return */
          pcVar1 = (code *)invalidInstructionException();
          (*pcVar1)();
        }
        lVar4 = lVar5;
        if (uVar7 < 0x17) goto LAB_02b81cc7;
        uVar3 = (uVar7 & 0x7ffffffffffffff8) + 8;
        uVar6 = 0x1a;
        if (uVar3 != 0x18) {
          uVar6 = uVar3;
        }
        lVar4 = func_0x04b078f0(uVar6);
        uStack_1b8 = uVar6 | 1;
        uStack_1b0 = uVar7;
        lStack_1a8 = lVar4;
      }
      func_0x04b07a30(lVar4,lVar5,uVar7);
      *(undefined1 *)(lVar4 + uVar7) = 0;
      bStack_198 = 0;
      uStack_197 = 0;
      func_0x02acb490(&uStack_1b8,2,&bStack_198,0);
      goto LAB_02b81db4;
    }
    if ((uStack_180 & 1) == 0) {
      *(undefined8 *)(param_1 + 4) = uStack_170;
      *param_1 = uStack_180;
      param_1[1] = uStack_17c;
      param_1[2] = uStack_178;
      param_1[3] = uStack_174;
    }
    else {
      func_0x019f86e0(param_1,uStack_170,CONCAT44(uStack_174,uStack_178));
    }
    if ((uStack_168 & 1) == 0) {
LAB_02b81bda:
      *(undefined8 *)(param_1 + 10) = uStack_158;
      param_1[6] = uStack_168;
      param_1[7] = uStack_164;
      param_1[8] = uStack_160;
      param_1[9] = uStack_15c;
    }
    else {
      func_0x019f86e0(param_1 + 6,uStack_158,CONCAT44(uStack_15c,uStack_160));
    }
  }
  else {
    if (param_6 != 0) {
      param_1[8] = 0;
      param_1[9] = 0;
      param_1[10] = 0;
      param_1[0xb] = 0;
      param_1[4] = 0;
      param_1[5] = 0;
      param_1[6] = 0;
      param_1[7] = 0;
      param_1[0] = 0;
      param_1[1] = 0;
      param_1[2] = 0;
      param_1[3] = 0;
      plVar2 = plStack_1a0;
      goto joined_r0x02b81eca;
    }
    func_0x02b525e0(&uStack_180,&plStack_1a0,param_5,param_4);
    if (cStack_150 != '\0') {
      if ((uStack_180 & 1) == 0) {
        *(undefined8 *)(param_1 + 4) = uStack_170;
        *param_1 = uStack_180;
        param_1[1] = uStack_17c;
        param_1[2] = uStack_178;
        param_1[3] = uStack_174;
      }
      else {
        func_0x019f86e0(param_1,uStack_170,CONCAT44(uStack_174,uStack_178));
      }
      if ((uStack_168 & 1) == 0) goto LAB_02b81bda;
      func_0x019f86e0(param_1 + 6,uStack_158,CONCAT44(uStack_15c,uStack_160));
      goto joined_r0x02b81e95;
    }
    func_0x01ab8040(alStack_148);
    func_0x01a53960(alStack_148 + 2,&UNK_0085b078,0x28);
    if ((uStack_d0 & 0x10) == 0) {
      uVar7 = 0;
      lVar5 = lStack_120;
      lVar4 = 0;
      if ((uStack_d0 & 8) != 0) goto LAB_02b81c6c;
LAB_02b81c7b:
      lVar5 = lVar4;
      uStack_1b8 = CONCAT71(uStack_1b8._1_7_,(char)uVar7 * '\x02');
      lVar4 = (long)&uStack_1b8 + 1;
    }
    else {
      lVar5 = lStack_108;
      uStack_110 = uStack_d8;
      if (uStack_d8 < uStack_100) {
        uStack_d8 = uStack_100;
        uStack_110 = uStack_100;
      }
LAB_02b81c6c:
      uVar7 = uStack_110 - lVar5;
      if ((long)uVar7 < 0) goto LAB_02b81ed5;
      lVar4 = lVar5;
      if (uVar7 < 0x17) goto LAB_02b81c7b;
      uVar3 = (uVar7 & 0x7ffffffffffffff8) + 8;
      uVar6 = 0x1a;
      if (uVar3 != 0x18) {
        uVar6 = uVar3;
      }
      lVar4 = func_0x04b078f0(uVar6);
      uStack_1b8 = uVar6 | 1;
      uStack_1b0 = uVar7;
      lStack_1a8 = lVar4;
    }
    func_0x04b07a30(lVar4,lVar5,uVar7);
    *(undefined1 *)(lVar4 + uVar7) = 0;
    bStack_198 = 0;
    uStack_197 = 0;
    func_0x02acb490(&uStack_1b8,2,&bStack_198,0);
LAB_02b81db4:
    if ((bStack_198 & 1) != 0) {
      func_0x04a8cfc8(uStack_188,
                      CONCAT62(uStack_196,CONCAT11(uStack_197,bStack_198)) & 0xfffffffffffffffe);
    }
    if ((uStack_1b8 & 1) != 0) {
      func_0x04a8cfc8(lStack_1a8,uStack_1b8 & 0xfffffffffffffffe);
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
    param_1[8] = 0;
    param_1[9] = 0;
    param_1[10] = 0;
    param_1[0xb] = 0;
    param_1[4] = 0;
    param_1[5] = 0;
    param_1[6] = 0;
    param_1[7] = 0;
    param_1[0] = 0;
    param_1[1] = 0;
    param_1[2] = 0;
    param_1[3] = 0;
  }
joined_r0x02b81e95:
  if ((uStack_168 & 1) != 0) {
    func_0x04a8cfc8(uStack_158,CONCAT44(uStack_164,uStack_168) & 0xfffffffffffffffe);
  }
  plVar2 = plStack_1a0;
  if ((uStack_180 & 1) != 0) {
    func_0x04a8cfc8(uStack_170,CONCAT44(uStack_17c,uStack_180) & 0xfffffffffffffffe);
    plVar2 = plStack_1a0;
  }
joined_r0x02b81eca:
  if (plVar2 != (long *)0x0) {
    plStack_1a0 = (long *)0x0;
    (**(code **)(*plVar2 + 8))();
  }
  return param_1;
}


// FUNCTION 02b83320 bounds 02b8343f

ulong * FUN_02b83320(ulong *param_1,byte *param_2,byte *param_3,int param_4,undefined4 param_5)

{
  char cVar1;
  ulong uVar2;
  ulong uVar3;
  byte *pbVar4;
  ulong uVar5;
  byte *pbVar6;
  bool bVar7;
  undefined8 uStack_48;
  ulong uStack_40;
  ulong uStack_38;

  uStack_48 = 0;
  uStack_40 = 0;
  uStack_38 = 0;
  if (param_4 == 0) {
    uStack_48._0_1_ = 0;
  }
  else {
    func_0x04aac620(&uStack_48,param_4,0);
  }
  bVar7 = (*param_2 & 1) != 0;
  pbVar6 = param_2 + 1;
  if (bVar7) {
    pbVar6 = *(byte **)(param_2 + 0x10);
  }
  uVar5 = (ulong)(*param_2 >> 1);
  if (bVar7) {
    uVar5 = *(ulong *)(param_2 + 8);
  }
  bVar7 = (*param_3 & 1) != 0;
  pbVar4 = param_3 + 1;
  if (bVar7) {
    pbVar4 = *(byte **)(param_3 + 0x10);
  }
  uVar3 = (ulong)(*param_3 >> 1);
  if (bVar7) {
    uVar3 = *(ulong *)(param_3 + 8);
  }
  uVar2 = uStack_38;
  if (((byte)uStack_48 & 1) == 0) {
    uVar2 = (long)&uStack_48 + 1;
  }
  cVar1 = func_0x048594a0(pbVar6,uVar5,pbVar4,uVar3,param_4,param_5,uVar2);
  if (cVar1 == '\0') {
    *param_1 = 0;
    param_1[1] = 0;
    param_1[2] = 0;
    if ((uStack_48 & 1) != 0) {
      func_0x04a8cfc8(uStack_38,uStack_48 & 0xfffffffffffffffe);
    }
  }
  else {
    param_1[2] = uStack_38;
    *param_1 = uStack_48;
    param_1[1] = uStack_40;
  }
  return param_1;
}


// FUNCTION 02b83440 bounds 02b8374f

undefined8 * FUN_02b83440(undefined8 *param_1,undefined8 *param_2,byte *param_3,uint *param_4)

{
  code *pcVar1;
  ulong uVar2;
  long lVar3;
  long lVar4;
  ulong uVar5;
  byte *pbVar6;
  ulong uVar7;
  long lVar8;
  bool bVar9;
  undefined8 uStack_58;
  undefined8 uStack_50;
  long lStack_48;
  long lStack_40;
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
    uVar2 = (ulong)(byte)uStack_38;
    if ((uStack_38 & 1) == 0) goto LAB_02b83474;
LAB_02b834af:
    uVar5 = CONCAT44(uStack_2c,uStack_30);
  }
  else {
    func_0x019f86e0(&uStack_38,*(undefined8 *)(param_4 + 4),*(undefined8 *)(param_4 + 2));
    uVar2 = (ulong)(byte)uStack_38;
    if ((uStack_38 & 1) != 0) goto LAB_02b834af;
LAB_02b83474:
    uVar5 = uVar2 >> 1;
  }
  if (uVar5 != 0x10) {
    *(undefined8 *)((long)param_1 + 9) = 0;
    *(undefined8 *)((long)param_1 + 0x11) = 0;
    *param_1 = 0;
    param_1[1] = 0;
    if ((uVar2 & 1) == 0) {
      return param_1;
    }
    goto LAB_02b8369b;
  }
  lVar4 = lStack_28;
  if ((uVar2 & 1) == 0) {
    lVar4 = (long)&uStack_38 + 1;
  }
  bVar9 = (*param_3 & 1) != 0;
  pbVar6 = param_3 + 1;
  if (bVar9) {
    pbVar6 = *(byte **)(param_3 + 0x10);
  }
  uVar2 = (ulong)(*param_3 >> 1);
  if (bVar9) {
    uVar2 = *(ulong *)(param_3 + 8);
  }
  (**(code **)(*(long *)*param_2 + 0x18))(&lStack_40,(long *)*param_2,pbVar6,lVar4,uVar2);
  lVar4 = lStack_40;
  if (lStack_40 == 0) {
    *(undefined8 *)((long)param_1 + 9) = 0;
    *(undefined8 *)((long)param_1 + 0x11) = 0;
    *param_1 = 0;
    param_1[1] = 0;
    lStack_40 = 0;
  }
  else {
    if ((*param_3 & 1) == 0) {
      uVar2 = (ulong)(*param_3 >> 1);
    }
    else {
      uVar2 = *(ulong *)(param_3 + 8);
    }
    uStack_58 = 0;
    uStack_50 = 0;
    lStack_48 = 0;
    if (uVar2 == 0) {
      uVar5 = 0;
    }
    else {
      func_0x04aac620(&uStack_58,uVar2,0);
      uVar5 = uStack_58 & 0xff;
    }
    uVar7 = uVar5 >> 1;
    if ((uVar5 & 1) != 0) {
      uVar7 = uStack_50;
    }
    if (uVar2 - 1 < uVar7) {
      lVar3 = (long)&uStack_58 + 1;
      if (uVar2 == 1) {
        uVar5 = 0;
      }
      else {
        uVar5 = 0;
        do {
          uVar7 = uStack_50;
          if ((uStack_58 & 1) == 0) {
            uVar7 = (ulong)((byte)uStack_58 >> 1);
          }
          if (uVar7 < uVar5) goto LAB_02b836bd;
          lVar8 = lVar3;
          if ((uStack_58 & 1) != 0) {
            lVar8 = lStack_48;
          }
          *(undefined1 *)(lVar8 + uVar5) = *(undefined1 *)(lVar4 + uVar5);
          uVar7 = uStack_50;
          if ((uStack_58 & 1) == 0) {
            uVar7 = (ulong)((byte)uStack_58 >> 1);
          }
          if (uVar7 <= uVar5) goto LAB_02b836bd;
          lVar8 = lVar3;
          if ((uStack_58 & 1) != 0) {
            lVar8 = lStack_48;
          }
          *(undefined1 *)(lVar8 + 1 + uVar5) = *(undefined1 *)(lVar4 + 1 + uVar5);
          uVar5 = uVar5 + 2;
        } while ((uVar2 & 0xfffffffffffffffe) != uVar5);
        if ((uVar2 & 1) == 0) goto LAB_02b83666;
      }
      uVar2 = uStack_50;
      if ((uStack_58 & 1) == 0) {
        uVar2 = (ulong)((byte)uStack_58 >> 1);
      }
      if (uVar2 < uVar5) {
LAB_02b836bd:
                    /* WARNING: Does not return */
        pcVar1 = (code *)invalidInstructionException();
        (*pcVar1)();
      }
      if ((uStack_58 & 1) != 0) {
        lVar3 = lStack_48;
      }
      *(undefined1 *)(lVar3 + uVar5) = *(undefined1 *)(lVar4 + uVar5);
    }
LAB_02b83666:
    lVar4 = lStack_40;
    param_1[2] = lStack_48;
    *(undefined4 *)param_1 = (undefined4)uStack_58;
    *(undefined4 *)((long)param_1 + 4) = uStack_58._4_4_;
    *(undefined4 *)(param_1 + 1) = (undefined4)uStack_50;
    *(undefined4 *)((long)param_1 + 0xc) = uStack_50._4_4_;
    *(undefined1 *)(param_1 + 3) = 1;
    lStack_40 = 0;
    if (lVar4 != 0) {
      func_0x04a8cfd0();
    }
  }
  if ((uStack_38 & 1) == 0) {
    return param_1;
  }
LAB_02b8369b:
  func_0x04a8cfc8(lStack_28,CONCAT44(uStack_34,uStack_38) & 0xfffffffffffffffe);
  return param_1;
}


// FUNCTION 048592b0 bounds 048592bf

/* WARNING: Removing unreachable block (ram,0x03d29369) */
/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void FUN_048592b0(long param_1,ulong param_2,undefined8 param_3)

{
  ulong auStack_f8 [2];
  undefined4 uStack_e8;
  undefined4 uStack_e4;
  undefined4 uStack_e0;
  undefined4 uStack_dc;
  undefined4 uStack_d8;
  undefined4 uStack_d4;
  undefined4 uStack_d0;
  undefined4 uStack_cc;
  undefined4 uStack_c8;
  undefined4 uStack_c4;
  undefined4 uStack_c0;
  undefined4 uStack_bc;
  undefined4 uStack_b8;
  undefined4 uStack_b4;
  undefined4 uStack_b0;
  undefined4 uStack_ac;
  undefined8 uStack_a8;
  undefined8 uStack_a0;
  undefined8 uStack_98;
  undefined8 uStack_90;
  undefined8 uStack_88;
  undefined8 uStack_80;
  undefined8 uStack_78;
  undefined8 uStack_70;
  undefined8 uStack_68;
  undefined8 uStack_60;
  undefined8 uStack_58;
  undefined8 uStack_50;
  undefined8 uStack_48;
  undefined8 uStack_40;
  undefined8 uStack_38;
  undefined8 uStack_30;
  undefined8 uStack_28;

  uStack_38 = 0;
  uStack_30 = 0;
  uStack_48 = 0;
  uStack_40 = 0;
  uStack_58 = 0;
  uStack_50 = 0;
  uStack_68 = 0;
  uStack_60 = 0;
  uStack_78 = 0;
  uStack_70 = 0;
  uStack_88 = 0;
  uStack_80 = 0;
  uStack_98 = 0;
  uStack_90 = 0;
  uStack_a8 = 0;
  uStack_a0 = 0;
  auStack_f8[0] = 0;
  auStack_f8[1] = 0;
  _uStack_e8 = CONCAT44(_UNK_008e3f34,_UNK_008e3f30);
  _uStack_e0 = CONCAT44(_UNK_008e3f3c,_UNK_008e3f38);
  _uStack_d8 = CONCAT44(_UNK_008e7af4,_UNK_008e7af0);
  _uStack_d0 = CONCAT44(_UNK_008e7afc,_UNK_008e7af8);
  _uStack_c8 = CONCAT44(_UNK_008e38d4,_UNK_008e38d0);
  _uStack_c0 = CONCAT44(_UNK_008e38dc,_UNK_008e38d8);
  _uStack_b8 = CONCAT44(_UNK_008e8504,_UNK_008e8500);
  _uStack_b0 = CONCAT44(_UNK_008e850c,_UNK_008e8508);
  uStack_28 = 0;
  if (param_2 != 0) {
    auStack_f8[0] = param_2;
    if (0x7f < param_2) {
      do {
        func_0x03d28720(auStack_f8,param_1);
        param_1 = param_1 + 0x80;
        param_2 = param_2 - 0x80;
      } while (0x7f < param_2);
      if (param_2 == 0) goto code_r0x03d2940e;
    }
    func_0x04b07ad0(&uStack_a8,param_1,param_2);
  }
code_r0x03d2940e:
  func_0x03d28e70(auStack_f8,param_3);
  func_0x03d1f350(auStack_f8,0xd8);
  return;
}


// FUNCTION 04858b50 bounds 04858b7f

void FUN_04858b50(undefined8 *param_1)

{
  undefined8 *puVar1;

  puVar1 = (undefined8 *)func_0x04b078f0(0x10);
  *puVar1 = &UNK_04c8aea8;
  puVar1[1] = 0;
  *param_1 = puVar1;
  return;
}


// FUNCTION 02b238a0 bounds 02b2407f

/* WARNING: Type propagation algorithm not settling */

long * FUN_02b238a0(long *param_1,long *param_2,long *param_3,long *param_4,undefined8 *param_5)

{
  code *pcVar1;
  long *plVar2;
  undefined7 *puVar3;
  byte bVar4;
  char cVar5;
  int iVar6;
  uint uVar7;
  long lVar8;
  ulong uVar9;
  ulong uVar10;
  undefined8 uVar11;
  long *plVar12;
  long *plVar13;
  ulong uVar14;
  uint *puVar15;
  byte *pbVar16;
  long *plVar17;
  long *extraout_RDX;
  long *plVar18;
  byte *extraout_RDX_00;
  long lVar19;
  long *plVar20;
  long *plVar21;
  undefined8 uVar22;
  undefined7 *puVar23;
  ulong unaff_R15;
  long lVar24;
  byte *pbVar25;
  bool bVar26;
  long *plStack_3e0;
  undefined8 uStack_3d8;
  ulong uStack_3d0;
  long lStack_3c8;
  byte bStack_3c0;
  undefined7 uStack_3bf;
  ulong uStack_3b8;
  undefined7 *puStack_3b0;
  char cStack_3a8;
  long *plStack_398;
  long *plStack_390;
  uint uStack_388;
  uint uStack_384;
  uint uStack_380;
  uint uStack_37c;
  undefined8 uStack_378;
  uint uStack_368;
  uint uStack_364;
  uint uStack_360;
  uint uStack_35c;
  undefined8 uStack_358;
  long *plStack_350;
  long *plStack_348;
  byte bStack_340;
  undefined7 uStack_33f;
  undefined8 uStack_330;
  byte bStack_328;
  undefined7 uStack_327;
  ulong uStack_320;
  undefined7 *puStack_318;
  byte *pbStack_308;
  long *plStack_2f8;
  undefined1 auStack_2f0 [24];
  ulong uStack_2d8;
  ulong uStack_2d0;
  ulong uStack_2c8;
  ulong uStack_2c0;
  long lStack_2b0;
  undefined1 auStack_2a8 [8];
  ulong uStack_2a0;
  ulong uStack_268;
  ulong uStack_260;
  long lStack_258;
  long *plStack_250;
  long *plStack_248;
  undefined8 uStack_240;
  undefined8 uStack_238;
  undefined8 uStack_230;
  long *plStack_228;
  long *plStack_220;
  undefined *puStack_218;
  long *plStack_210;
  long *plStack_208;
  long *plStack_1f8;
  undefined8 uStack_1f0;
  ulong uStack_1e8;
  long lStack_1e0;
  undefined8 uStack_1d8;
  ulong uStack_1d0;
  long lStack_1c8;
  undefined8 uStack_1c0;
  long *plStack_1b8;
  long lStack_1b0;
  undefined8 uStack_1a8;
  ulong uStack_1a0;
  long lStack_198;
  long lStack_190;
  long *plStack_188;
  char cStack_180;
  undefined8 *puStack_178;
  byte bStack_170;
  undefined7 uStack_16f;
  undefined8 uStack_160;
  char cStack_158;
  undefined8 uStack_150;
  long *plStack_148;
  undefined8 uStack_140;
  undefined8 uStack_138;
  undefined8 uStack_128;
  long *plStack_120;
  long *plStack_118;
  ulong uStack_110;
  ulong uStack_108;
  long *plStack_100;
  long *plStack_f8;
  undefined8 uStack_f0;
  ulong uStack_e8;
  long lStack_e0;
  undefined8 uStack_d8;
  ulong uStack_d0;
  long lStack_c8;
  undefined8 uStack_b8;
  ulong uStack_b0;
  long lStack_a8;
  long lStack_a0;
  undefined8 uStack_98;
  ulong uStack_90;
  long lStack_88;
  long *plStack_78;
  long *plStack_70;
  char cStack_68;
  undefined8 *puStack_60;
  undefined8 uStack_58;
  long *plStack_50;
  undefined8 uStack_48;
  undefined8 uStack_40;

  plStack_100 = (long *)0x2b238d4;
  plStack_f8 = param_1;
  plStack_78 = param_4;
  puStack_60 = param_5;
  lVar8 = (**(code **)(*param_2 + 0x18))(param_2);
  plVar20 = (long *)0x6;
  if (lVar8 == 0) {
    return (long *)0x6;
  }
  plStack_100 = (long *)0x2b238ec;
  lVar8 = (**(code **)(*param_3 + 0x18))(param_3);
  if (lVar8 == 0) {
    return (long *)0x6;
  }
  uStack_98 = 0;
  uStack_90 = 0;
  lStack_88 = 0;
  plStack_100 = (long *)0x2b2391b;
  iVar6 = func_0x02b710f0(plStack_f8[0x35],&uStack_98);
  if (iVar6 == 0) {
    plStack_100 = (long *)0x2b23fe0;
    uVar9 = (**(code **)(*param_2 + 0x10))(param_2);
    plStack_100 = (long *)0x2b23fed;
    unaff_R15 = (**(code **)(*param_2 + 0x18))(param_2);
    if (unaff_R15 != 0xffffffffffffffff) {
      lVar8 = (long)&uStack_98 + 1;
      uVar14 = uStack_98 >> 1 & 0x7f;
      if ((uStack_98 & 1) != 0) {
        lVar8 = lStack_88;
        uVar14 = uStack_90;
      }
      uVar10 = uVar14;
      if (unaff_R15 < uVar14) {
        uVar10 = unaff_R15;
      }
      plStack_100 = (long *)0x2b24024;
      iVar6 = func_0x04b07bb0(lVar8,uVar9,uVar10);
      if ((iVar6 != 0) || (uVar14 != unaff_R15)) goto LAB_02b23923;
      goto LAB_02b2392d;
    }
  }
  else {
LAB_02b23923:
    plStack_100 = (long *)0x2b2392d;
    (**(code **)(*plStack_f8 + 0x40))();
LAB_02b2392d:
    plStack_70 = plStack_f8 + 4;
    cStack_68 = '\x01';
    plStack_100 = (long *)0x2b2394a;
    func_0x04ab5980();
    plVar20 = (long *)plStack_f8[0x35];
    if (plVar20 == (long *)0x0) {
      plStack_100 = (long *)0x2b23968;
      plVar20 = (long *)func_0x04b078f0(0x1c8);
      plStack_100 = (long *)0x2b2397a;
      func_0x04b07ba0(plVar20,0,0x1c8);
      plStack_100 = (long *)0x2b23982;
      func_0x04ab6070(plVar20);
      plStack_100 = (long *)0x2b23990;
      func_0x047d2ec0(plVar20 + 0x12,0);
      *(undefined1 *)(plVar20 + 0x38) = 0;
      lVar8 = plStack_f8[0x35];
      plStack_f8[0x35] = (long)plVar20;
      if (lVar8 != 0) {
        plStack_100 = (long *)0x2b239ba;
        func_0x047d3460(lVar8 + 0x90);
        plStack_100 = (long *)0x2b239c3;
        func_0x04ab4af0(lVar8 + 0x58);
        plStack_100 = (long *)0x2b239cc;
        func_0x04ab4af0(lVar8 + 0x28);
        plStack_100 = (long *)0x2b239d9;
        func_0x04a8cfc8(lVar8,0x1c8);
        plVar20 = (long *)plStack_f8[0x35];
      }
    }
    plStack_100 = (long *)0x2b239ee;
    lStack_a0 = (**(code **)(*param_2 + 0x10))(param_2);
    plStack_100 = (long *)0x2b239fd;
    uVar9 = (**(code **)(*param_2 + 0x18))(param_2);
    if (uVar9 < 0xfffffffffffffff7) {
      if (uVar9 < 0x17) {
        uStack_f0 = CONCAT71(uStack_f0._1_7_,(char)uVar9 * '\x02');
        lVar8 = (long)&uStack_f0 + 1;
      }
      else {
        uVar10 = (uVar9 & 0xfffffffffffffff8) + 8;
        uVar14 = 0x1a;
        if (uVar10 != 0x18) {
          uVar14 = uVar10;
        }
        plStack_100 = (long *)0x2b23a42;
        lVar8 = func_0x04b078f0(uVar14);
        uStack_f0 = uVar14 | 1;
        uStack_e8 = uVar9;
        lStack_e0 = lVar8;
      }
      plStack_100 = (long *)0x2b23a68;
      func_0x04b07a30(lVar8,lStack_a0,uVar9);
      *(undefined1 *)(lVar8 + uVar9) = 0;
      plStack_100 = (long *)0x2b23a7a;
      func_0x02b728f0(plVar20,&uStack_f0);
      if ((uStack_f0 & 1) != 0) {
        plStack_100 = (long *)0x2b23a94;
        func_0x04a8cfc8(lStack_e0,uStack_f0 & 0xfffffffffffffffe);
      }
      lStack_a0 = plStack_f8[0x35];
      plStack_100 = (long *)0x2b23aae;
      unaff_R15 = (**(code **)(*param_3 + 0x10))(param_3);
      plStack_100 = (long *)0x2b23abb;
      uVar9 = (**(code **)(*param_3 + 0x18))(param_3);
      if (uVar9 < 0xfffffffffffffff7) {
        if (uVar9 < 0x17) {
          uStack_d8 = (long *)CONCAT71(uStack_d8._1_7_,(char)uVar9 * '\x02');
          lVar8 = (long)&uStack_d8 + 1;
        }
        else {
          uVar10 = (uVar9 & 0xfffffffffffffff8) + 8;
          uVar14 = 0x1a;
          if (uVar10 != 0x18) {
            uVar14 = uVar10;
          }
          plStack_100 = (long *)0x2b23aff;
          lVar8 = func_0x04b078f0(uVar14);
          plVar20 = (long *)(uVar14 | 1);
          uStack_d8 = plVar20;
          uStack_d0 = uVar9;
          lStack_c8 = lVar8;
        }
        plStack_100 = (long *)0x2b23b23;
        func_0x04b07a30(lVar8,unaff_R15,uVar9);
        *(undefined1 *)(lVar8 + uVar9) = 0;
        plStack_100 = (long *)0x2b23b32;
        unaff_R15 = (**(code **)(*param_2 + 0x10))(param_2);
        plStack_100 = (long *)0x2b23b3f;
        uVar9 = (**(code **)(*param_2 + 0x18))(param_2);
        if (uVar9 < 0xfffffffffffffff7) {
          if (uVar9 < 0x17) {
            uStack_b8 = CONCAT71(uStack_b8._1_7_,(char)uVar9 * '\x02');
            lVar8 = (long)&uStack_b8 + 1;
          }
          else {
            uVar10 = (uVar9 & 0xfffffffffffffff8) + 8;
            uVar14 = 0x1a;
            if (uVar10 != 0x18) {
              uVar14 = uVar10;
            }
            plStack_100 = (long *)0x2b23b83;
            lVar8 = func_0x04b078f0(uVar14);
            uStack_b8 = uVar14 | 1;
            uStack_b0 = uVar9;
            lStack_a8 = lVar8;
          }
          plStack_100 = (long *)0x2b23ba7;
          func_0x04b07a30(lVar8,unaff_R15,uVar9);
          *(undefined1 *)(lVar8 + uVar9) = 0;
          plStack_100 = (long *)0x2b23bc3;
          FUN_02b7fc70(&uStack_f0,&uStack_d8,&uStack_b8);
          plStack_100 = (long *)0x2b23bd0;
          func_0x02b72970(lStack_a0,&uStack_f0);
          if ((uStack_f0 & 1) != 0) {
            plStack_100 = (long *)0x2b23c51;
            func_0x04a8cfc8(lStack_e0,uStack_f0 & 0xfffffffffffffffe);
          }
          if ((uStack_b8 & 1) != 0) {
            plStack_100 = (long *)0x2b23c6b;
            func_0x04a8cfc8(lStack_a8,uStack_b8 & 0xfffffffffffffffe);
          }
          if (((ulong)uStack_d8 & 1) != 0) {
            plStack_100 = (long *)0x2b23bf8;
            func_0x04a8cfc8(lStack_c8,(ulong)uStack_d8 & 0xfffffffffffffffe);
          }
          plVar20 = (long *)plStack_f8[0x35];
          plStack_100 = (long *)0x2b23c0d;
          unaff_R15 = (**(code **)(*param_3 + 0x10))(param_3);
          plStack_100 = (long *)0x2b23c1a;
          uVar9 = (**(code **)(*param_3 + 0x18))(param_3);
          if (uVar9 < 0xfffffffffffffff7) {
            if (uVar9 < 0x17) {
              uStack_d8 = (long *)CONCAT71(uStack_d8._1_7_,(char)uVar9 * '\x02');
              lVar8 = (long)&uStack_d8 + 1;
            }
            else {
              uVar10 = (uVar9 & 0xfffffffffffffff8) + 8;
              uVar14 = 0x1a;
              if (uVar10 != 0x18) {
                uVar14 = uVar10;
              }
              plStack_100 = (long *)0x2b23c99;
              lVar8 = func_0x04b078f0(uVar14);
              param_3 = (long *)(uVar14 | 1);
              uStack_d8 = param_3;
              uStack_d0 = uVar9;
              lStack_c8 = lVar8;
            }
            plStack_100 = (long *)0x2b23cbd;
            func_0x04b07a30(lVar8,unaff_R15,uVar9);
            *(undefined1 *)(lVar8 + uVar9) = 0;
            plStack_100 = (long *)0x2b23ccc;
            unaff_R15 = (**(code **)(*param_2 + 0x10))(param_2);
            plStack_100 = (long *)0x2b23cd9;
            uVar9 = (**(code **)(*param_2 + 0x18))(param_2);
            if (uVar9 < 0xfffffffffffffff7) {
              if (uVar9 < 0x17) {
                uStack_b8 = CONCAT71(uStack_b8._1_7_,(char)uVar9 * '\x02');
                lVar8 = (long)&uStack_b8 + 1;
              }
              else {
                uVar10 = (uVar9 & 0xfffffffffffffff8) + 8;
                uVar14 = 0x1a;
                if (uVar10 != 0x18) {
                  uVar14 = uVar10;
                }
                plStack_100 = (long *)0x2b23d1e;
                lVar8 = func_0x04b078f0(uVar14);
                uStack_b8 = uVar14 | 1;
                uStack_b0 = uVar9;
                lStack_a8 = lVar8;
              }
              plStack_100 = (long *)0x2b23d42;
              func_0x04b07a30(lVar8,unaff_R15,uVar9);
              *(undefined1 *)(lVar8 + uVar9) = 0;
              param_3 = &uStack_d8;
              plStack_100 = (long *)0x2b23d61;
              FUN_02b7fe30(&uStack_f0,param_3,&uStack_b8);
              plStack_100 = (long *)0x2b23d6c;
              func_0x02b72a70(plVar20,&uStack_f0);
              if ((uStack_f0 & 1) != 0) {
                plStack_100 = (long *)0x2b23e2e;
                func_0x04a8cfc8(lStack_e0,uStack_f0 & 0xfffffffffffffffe);
              }
              if ((uStack_b8 & 1) != 0) {
                plStack_100 = (long *)0x2b23e4c;
                func_0x04a8cfc8(lStack_a8,uStack_b8 & 0xfffffffffffffffe);
              }
              if (((ulong)uStack_d8 & 1) != 0) {
                plStack_100 = (long *)0x2b23d9c;
                func_0x04a8cfc8(lStack_c8,(ulong)uStack_d8 & 0xfffffffffffffffe);
              }
              LOCK();
              *(undefined4 *)((long)plStack_f8 + 0x20c) = 0xffffffff;
              UNLOCK();
              param_2 = (long *)puStack_60[1];
              uStack_48 = *puStack_60;
              uStack_40 = puStack_60[1];
              if (param_2 != (long *)0x0) {
                LOCK();
                param_2[1] = param_2[1] + 1;
                UNLOCK();
              }
              uStack_58 = 0;
              plStack_50 = (long *)0x0;
              plStack_100 = (long *)0x2b23de6;
              param_4 = plStack_f8;
              unaff_R15 = (**(code **)(*plStack_78 + 0x10))();
              plStack_100 = (long *)0x2b23df7;
              uVar9 = (**(code **)(*plStack_78 + 0x18))();
              if (uVar9 < 0xfffffffffffffff7) {
                if (uVar9 < 0x17) {
                  uStack_f0 = CONCAT71(uStack_f0._1_7_,(char)uVar9 * '\x02');
                  lVar8 = (long)&uStack_f0 + 1;
                }
                else {
                  uVar10 = (uVar9 & 0xfffffffffffffff8) + 8;
                  uVar14 = 0x1a;
                  if (uVar10 != 0x18) {
                    uVar14 = uVar10;
                  }
                  plStack_100 = (long *)0x2b23e7c;
                  lVar8 = func_0x04b078f0(uVar14);
                  uStack_f0 = uVar14 | 1;
                  uStack_e8 = uVar9;
                  lStack_e0 = lVar8;
                }
                plStack_100 = (long *)0x2b23ea0;
                func_0x04b07a30(lVar8,unaff_R15,uVar9);
                *(undefined1 *)(lVar8 + uVar9) = 0;
                uStack_d8 = (long *)0x0;
                uStack_d0 = 0;
                lStack_c8 = 0;
                uStack_108 = 0;
                uStack_110 = 0x2b23ee7;
                plStack_100 = param_3;
                uVar7 = func_0x02b220b0(plStack_f8,&plStack_70,plStack_f8 + 0x48,&uStack_48,
                                        &uStack_58,&uStack_f0);
                if (((ulong)uStack_d8 & 1) != 0) {
                  plStack_100 = (long *)0x2b23f1d;
                  func_0x04a8cfc8(lStack_c8,(ulong)uStack_d8 & 0xfffffffffffffffe);
                }
                plVar20 = plStack_50;
                if ((uStack_f0 & 1) != 0) {
                  plStack_100 = (long *)0x2b23f37;
                  func_0x04a8cfc8(lStack_e0,uStack_f0 & 0xfffffffffffffffe);
                  plVar20 = plStack_50;
                }
                plStack_50 = plVar20;
                if (plVar20 != (long *)0x0) {
                  LOCK();
                  plVar17 = plVar20 + 1;
                  lVar8 = *plVar17;
                  *plVar17 = *plVar17 + -1;
                  UNLOCK();
                  if (lVar8 == 0) {
                    plStack_100 = (long *)0x2b23f5f;
                    (**(code **)(*plVar20 + 0x10))(plVar20);
                    plStack_100 = (long *)0x2b23f67;
                    func_0x04aa8780(plVar20);
                  }
                }
                if (param_2 != (long *)0x0) {
                  LOCK();
                  plVar20 = param_2 + 1;
                  lVar8 = *plVar20;
                  *plVar20 = *plVar20 + -1;
                  UNLOCK();
                  if (lVar8 == 0) {
                    plStack_100 = (long *)0x2b23f89;
                    (**(code **)(*param_2 + 0x10))(param_2);
                    plStack_100 = (long *)0x2b23f91;
                    func_0x04aa8780(param_2);
                  }
                }
                if (cStack_68 == '\x01') {
                  plStack_100 = (long *)0x2b23fa8;
                  func_0x04ab59b0(plStack_70);
                }
                if ((uStack_98 & 1) != 0) {
                  plStack_100 = (long *)0x2b23fc2;
                  func_0x04a8cfc8(lStack_88,uStack_98 & 0xfffffffffffffffe);
                }
                return (long *)(ulong)uVar7;
              }
            }
          }
        }
      }
    }
    plStack_100 = (long *)0x2b2403f;
    func_0x019f8660();
  }
  plStack_100 = (long *)0x2b24044;
  uVar11 = func_0x01a1b050();
  plStack_100 = (long *)0x2b24050;
  func_0x04ab4af0(plVar20 + 0xb);
  plStack_100 = (long *)0x2b2405c;
  func_0x04ab4af0(plVar20 + 5);
  plVar21 = (long *)0x1c8;
  plStack_100 = (long *)0x2b2406e;
  func_0x04a8cfc8(plVar20);
  plStack_100 = (long *)0x2b24076;
  plVar12 = (long *)func_0x019f83c0(uVar11);
  plStack_100 = (long *)0x2b2407e;
  func_0x019f83c0();
  plStack_208 = (long *)&UNK_02b240b0;
  plVar17 = param_4;
  plStack_1f8 = plVar12;
  puStack_178 = param_5;
  uStack_128 = uVar11;
  plStack_120 = param_2;
  plStack_118 = param_3;
  uStack_110 = uVar9;
  uStack_108 = unaff_R15;
  plStack_100 = plVar20;
  plVar20 = (long *)(**(code **)(*param_4 + 0x10))(param_4);
  plStack_208 = (long *)&UNK_02b240bc;
  plVar13 = (long *)(**(code **)(*param_4 + 0x18))(param_4);
  plVar12 = extraout_RDX;
  if (plVar13 < (long *)0xfffffffffffffff7) {
    if (plVar13 < (long *)0x17) {
      uStack_1c0 = CONCAT71(uStack_1c0._1_7_,(char)plVar13 * '\x02');
      lVar8 = (long)&uStack_1c0 + 1;
    }
    else {
      uVar14 = ((ulong)plVar13 & 0xfffffffffffffff8) + 8;
      uVar9 = 0x1a;
      if (uVar14 != 0x18) {
        uVar9 = uVar14;
      }
      plStack_208 = (long *)&UNK_02b240ff;
      lVar8 = func_0x04b078f0(uVar9);
      uStack_1c0 = uVar9 | 1;
      plStack_1b8 = plVar13;
      lStack_1b0 = lVar8;
    }
    plStack_208 = (long *)&UNK_02b24123;
    func_0x04b07a30(lVar8,plVar20,plVar13);
    *(undefined1 *)(lVar8 + (long)plVar13) = 0;
    plStack_208 = (long *)&UNK_02b2413a;
    func_0x02b83d90(&bStack_170,&uStack_1c0);
    if ((uStack_1c0 & 1) != 0) {
      plStack_208 = (long *)&UNK_02b24154;
      func_0x04a8cfc8(lStack_1b0,uStack_1c0 & 0xfffffffffffffffe);
    }
    plStack_208 = (long *)&UNK_02b2415d;
    lVar8 = (**(code **)(*plVar21 + 0x18))(plVar21);
    plVar13 = (long *)0x6;
    if (lVar8 == 0) goto code_r0x02b247ed;
    plStack_208 = (long *)&UNK_02b24175;
    lVar8 = (**(code **)(*extraout_RDX + 0x18))(extraout_RDX);
    if ((lVar8 == 0) || (cStack_158 != '\x01')) goto code_r0x02b247ed;
    uStack_1c0 = 0;
    plStack_1b8 = (long *)0x0;
    lStack_1b0 = 0;
    plStack_208 = (long *)&UNK_02b241b3;
    iVar6 = func_0x02b710f0(plStack_1f8[0x35],&uStack_1c0);
    if (iVar6 != 0) {
code_r0x02b241bb:
      plStack_208 = (long *)&UNK_02b241c6;
      (**(code **)(*plStack_1f8 + 0x40))();
code_r0x02b241c6:
      plStack_188 = plStack_1f8 + 4;
      cStack_180 = '\x01';
      plStack_208 = (long *)&UNK_02b241e1;
      func_0x04ab5980();
      plVar20 = (long *)plStack_1f8[0x35];
      if (plVar20 == (long *)0x0) {
        plStack_208 = (long *)&UNK_02b24200;
        plVar20 = (long *)func_0x04b078f0(0x1c8);
        plStack_208 = (long *)&UNK_02b24212;
        func_0x04b07ba0(plVar20,0,0x1c8);
        plStack_208 = (long *)&UNK_02b2421a;
        func_0x04ab6070(plVar20);
        plStack_208 = (long *)&UNK_02b24228;
        func_0x047d2ec0(plVar20 + 0x12,0);
        *(undefined1 *)(plVar20 + 0x38) = 0;
        lVar8 = plStack_1f8[0x35];
        plStack_1f8[0x35] = (long)plVar20;
        if (lVar8 != 0) {
          plStack_208 = (long *)&UNK_02b24254;
          func_0x047d3460(lVar8 + 0x90);
          plStack_208 = (long *)&UNK_02b2425d;
          func_0x04ab4af0(lVar8 + 0x58);
          plStack_208 = (long *)&UNK_02b24266;
          func_0x04ab4af0(lVar8 + 0x28);
          plStack_208 = (long *)&UNK_02b24273;
          func_0x04a8cfc8(lVar8,0x1c8);
          plVar20 = (long *)plStack_1f8[0x35];
        }
      }
      plStack_208 = (long *)&UNK_02b24288;
      lStack_190 = (**(code **)(*plVar21 + 0x10))(plVar21);
      plStack_208 = (long *)&UNK_02b24296;
      uVar9 = (**(code **)(*plVar21 + 0x18))(plVar21);
      if (uVar9 < 0xfffffffffffffff7) {
        if (uVar9 < 0x17) {
          uStack_1f0 = CONCAT71(uStack_1f0._1_7_,(char)uVar9 * '\x02');
          lVar8 = (long)&uStack_1f0 + 1;
        }
        else {
          uVar10 = (uVar9 & 0xfffffffffffffff8) + 8;
          uVar14 = 0x1a;
          if (uVar10 != 0x18) {
            uVar14 = uVar10;
          }
          plStack_208 = (long *)&UNK_02b242da;
          lVar8 = func_0x04b078f0(uVar14);
          uStack_1f0 = uVar14 | 1;
          uStack_1e8 = uVar9;
          lStack_1e0 = lVar8;
        }
        plStack_208 = (long *)&UNK_02b24300;
        func_0x04b07a30(lVar8,lStack_190,uVar9);
        *(undefined1 *)(lVar8 + uVar9) = 0;
        plStack_208 = (long *)&UNK_02b24312;
        func_0x02b728f0(plVar20,&uStack_1f0);
        if ((uStack_1f0 & 1) != 0) {
          plStack_208 = (long *)&UNK_02b2432c;
          func_0x04a8cfc8(lStack_1e0,uStack_1f0 & 0xfffffffffffffffe);
        }
        lStack_190 = plStack_1f8[0x35];
        plStack_208 = (long *)&UNK_02b24347;
        plVar13 = (long *)(**(code **)(*extraout_RDX + 0x10))(extraout_RDX);
        plStack_208 = (long *)&UNK_02b24354;
        uVar9 = (**(code **)(*extraout_RDX + 0x18))(extraout_RDX);
        if (uVar9 < 0xfffffffffffffff7) {
          if (uVar9 < 0x17) {
            uStack_1d8 = (long *)CONCAT71(uStack_1d8._1_7_,(char)uVar9 * '\x02');
            lVar8 = (long)&uStack_1d8 + 1;
          }
          else {
            uVar10 = (uVar9 & 0xfffffffffffffff8) + 8;
            uVar14 = 0x1a;
            if (uVar10 != 0x18) {
              uVar14 = uVar10;
            }
            plStack_208 = (long *)&UNK_02b24399;
            lVar8 = func_0x04b078f0(uVar14);
            plVar20 = (long *)(uVar14 | 1);
            uStack_1d8 = plVar20;
            uStack_1d0 = uVar9;
            lStack_1c8 = lVar8;
          }
          plStack_208 = (long *)&UNK_02b243bd;
          func_0x04b07a30(lVar8,plVar13,uVar9);
          *(undefined1 *)(lVar8 + uVar9) = 0;
          plStack_208 = (long *)&UNK_02b243cb;
          plVar13 = (long *)(**(code **)(*plVar21 + 0x10))(plVar21);
          plStack_208 = (long *)&UNK_02b243d7;
          uVar9 = (**(code **)(*plVar21 + 0x18))(plVar21);
          if (uVar9 < 0xfffffffffffffff7) {
            if (uVar9 < 0x17) {
              uStack_1a8 = CONCAT71(uStack_1a8._1_7_,(char)uVar9 * '\x02');
              lVar8 = (long)&uStack_1a8 + 1;
            }
            else {
              uVar10 = (uVar9 & 0xfffffffffffffff8) + 8;
              uVar14 = 0x1a;
              if (uVar10 != 0x18) {
                uVar14 = uVar10;
              }
              plStack_208 = (long *)&UNK_02b2441c;
              lVar8 = func_0x04b078f0(uVar14);
              uStack_1a8 = uVar14 | 1;
              uStack_1a0 = uVar9;
              lStack_198 = lVar8;
            }
            plStack_208 = (long *)&UNK_02b24440;
            func_0x04b07a30(lVar8,plVar13,uVar9);
            *(undefined1 *)(lVar8 + uVar9) = 0;
            plStack_208 = (long *)&UNK_02b2445c;
            FUN_02b7fc70(&uStack_1f0,&uStack_1d8,&uStack_1a8);
            plStack_208 = (long *)&UNK_02b24469;
            func_0x02b72970(lStack_190,&uStack_1f0);
            if ((uStack_1f0 & 1) != 0) {
              plStack_208 = (long *)&UNK_02b244eb;
              func_0x04a8cfc8(lStack_1e0,uStack_1f0 & 0xfffffffffffffffe);
            }
            if ((uStack_1a8 & 1) != 0) {
              plStack_208 = (long *)&UNK_02b24505;
              func_0x04a8cfc8(lStack_198,uStack_1a8 & 0xfffffffffffffffe);
            }
            if (((ulong)uStack_1d8 & 1) != 0) {
              plStack_208 = (long *)&UNK_02b24491;
              func_0x04a8cfc8(lStack_1c8,(ulong)uStack_1d8 & 0xfffffffffffffffe);
            }
            plVar20 = (long *)plStack_1f8[0x35];
            plStack_208 = (long *)&UNK_02b244a7;
            plVar13 = (long *)(**(code **)(*extraout_RDX + 0x10))(extraout_RDX);
            plStack_208 = (long *)&UNK_02b244b4;
            uVar9 = (**(code **)(*extraout_RDX + 0x18))(extraout_RDX);
            if (uVar9 < 0xfffffffffffffff7) {
              if (uVar9 < 0x17) {
                uStack_1d8 = (long *)CONCAT71(uStack_1d8._1_7_,(char)uVar9 * '\x02');
                lVar8 = (long)&uStack_1d8 + 1;
              }
              else {
                uVar10 = (uVar9 & 0xfffffffffffffff8) + 8;
                uVar14 = 0x1a;
                if (uVar10 != 0x18) {
                  uVar14 = uVar10;
                }
                plStack_208 = (long *)&UNK_02b24536;
                lVar8 = func_0x04b078f0(uVar14);
                uStack_1d8 = (long *)(uVar14 | 1);
                uStack_1d0 = uVar9;
                lStack_1c8 = lVar8;
              }
              plStack_208 = (long *)&UNK_02b2455a;
              func_0x04b07a30(lVar8,plVar13,uVar9);
              *(undefined1 *)(lVar8 + uVar9) = 0;
              plStack_208 = (long *)&UNK_02b24568;
              plVar12 = (long *)(**(code **)(*plVar21 + 0x10))(plVar21);
              plStack_208 = (long *)&UNK_02b24574;
              uVar9 = (**(code **)(*plVar21 + 0x18))(plVar21);
              if (uVar9 < 0xfffffffffffffff7) {
                if (uVar9 < 0x17) {
                  uStack_1a8 = CONCAT71(uStack_1a8._1_7_,(char)uVar9 * '\x02');
                  lVar8 = (long)&uStack_1a8 + 1;
                }
                else {
                  uVar10 = (uVar9 & 0xfffffffffffffff8) + 8;
                  uVar14 = 0x1a;
                  if (uVar10 != 0x18) {
                    uVar14 = uVar10;
                  }
                  plStack_208 = (long *)&UNK_02b245b9;
                  lVar8 = func_0x04b078f0(uVar14);
                  uStack_1a8 = uVar14 | 1;
                  uStack_1a0 = uVar9;
                  lStack_198 = lVar8;
                }
                plStack_208 = (long *)&UNK_02b245dd;
                func_0x04b07a30(lVar8,plVar12,uVar9);
                *(undefined1 *)(lVar8 + uVar9) = 0;
                plStack_208 = (long *)&UNK_02b245f9;
                FUN_02b7fe30(&uStack_1f0,&uStack_1d8,&uStack_1a8);
                plStack_208 = (long *)&UNK_02b24604;
                func_0x02b72a70(plVar20,&uStack_1f0);
                if ((uStack_1f0 & 1) != 0) {
                  plStack_208 = (long *)&UNK_02b2470c;
                  func_0x04a8cfc8(lStack_1e0,uStack_1f0 & 0xfffffffffffffffe);
                }
                if ((uStack_1a8 & 1) != 0) {
                  plStack_208 = (long *)&UNK_02b2472a;
                  func_0x04a8cfc8(lStack_198,uStack_1a8 & 0xfffffffffffffffe);
                }
                if (((ulong)uStack_1d8 & 1) != 0) {
                  plStack_208 = (long *)&UNK_02b24634;
                  func_0x04a8cfc8(lStack_1c8,(ulong)uStack_1d8 & 0xfffffffffffffffe);
                }
                LOCK();
                *(undefined4 *)((long)plStack_1f8 + 0x20c) = 0xffffffff;
                UNLOCK();
                plVar20 = (long *)puStack_178[1];
                uStack_140 = *puStack_178;
                uStack_138 = puStack_178[1];
                if (plVar20 != (long *)0x0) {
                  LOCK();
                  plVar20[1] = plVar20[1] + 1;
                  UNLOCK();
                }
                uStack_150 = 0;
                plStack_148 = (long *)0x0;
                uStack_1f0 = 0;
                uStack_1e8 = 0;
                lStack_1e0 = 0;
                if (cStack_158 == '\0') {
                    /* WARNING: Does not return */
                  pcVar1 = (code *)invalidInstructionException();
                  (*pcVar1)();
                }
                plStack_208 = (long *)&UNK_02b246a1;
                func_0x02b84190(&uStack_1d8,&bStack_170);
                plStack_210 = (long *)0x0;
                puStack_218 = &UNK_02b246cf;
                plStack_208 = &uStack_1d8;
                uVar7 = func_0x02b220b0(plStack_1f8,&plStack_188,plStack_1f8 + 0x48,&uStack_140,
                                        &uStack_150,&uStack_1f0);
                plVar13 = (long *)(ulong)uVar7;
                if (((ulong)uStack_1d8 & 1) != 0) {
                  plStack_208 = (long *)&UNK_02b2474d;
                  func_0x04a8cfc8(lStack_1c8,(ulong)uStack_1d8 & 0xfffffffffffffffe);
                }
                plVar17 = plStack_148;
                if ((uStack_1f0 & 1) != 0) {
                  plStack_208 = (long *)&UNK_02b24767;
                  func_0x04a8cfc8(lStack_1e0,uStack_1f0 & 0xfffffffffffffffe);
                  plVar17 = plStack_148;
                }
                plStack_148 = plVar17;
                if (plVar17 != (long *)0x0) {
                  LOCK();
                  plVar12 = plVar17 + 1;
                  lVar8 = *plVar12;
                  *plVar12 = *plVar12 + -1;
                  UNLOCK();
                  if (lVar8 == 0) {
                    plStack_208 = (long *)&UNK_02b2478f;
                    (**(code **)(*plVar17 + 0x10))(plVar17);
                    plStack_208 = (long *)&UNK_02b24797;
                    func_0x04aa8780(plVar17);
                  }
                }
                if (plVar20 != (long *)0x0) {
                  LOCK();
                  plVar17 = plVar20 + 1;
                  lVar8 = *plVar17;
                  *plVar17 = *plVar17 + -1;
                  UNLOCK();
                  if (lVar8 == 0) {
                    plStack_208 = (long *)&UNK_02b247b7;
                    (**(code **)(*plVar20 + 0x10))(plVar20);
                    plStack_208 = (long *)&UNK_02b247bf;
                    func_0x04aa8780(plVar20);
                  }
                }
                if (cStack_180 == '\x01') {
                  plStack_208 = (long *)&UNK_02b247d3;
                  func_0x04ab59b0(plStack_188);
                }
                if ((uStack_1c0 & 1) != 0) {
                  plStack_208 = (long *)&UNK_02b247ed;
                  func_0x04a8cfc8(lStack_1b0,uStack_1c0 & 0xfffffffffffffffe);
                }
code_r0x02b247ed:
                if ((cStack_158 == '\x01') && ((bStack_170 & 1) != 0)) {
                  plStack_208 = (long *)&UNK_02b2481a;
                  func_0x04a8cfc8(uStack_160,CONCAT71(uStack_16f,bStack_170) & 0xfffffffffffffffe);
                }
                return plVar13;
              }
            }
          }
        }
      }
      goto code_r0x02b24892;
    }
    plStack_208 = (long *)&UNK_02b24837;
    uVar9 = (**(code **)(*plVar21 + 0x10))(plVar21);
    plStack_208 = (long *)&UNK_02b24843;
    plVar20 = (long *)(**(code **)(*plVar21 + 0x18))(plVar21);
    if (plVar20 != (long *)0xffffffffffffffff) {
      lVar8 = (long)&uStack_1c0 + 1;
      plVar2 = (long *)(uStack_1c0 >> 1 & 0x7f);
      if ((uStack_1c0 & 1) != 0) {
        lVar8 = lStack_1b0;
        plVar2 = plStack_1b8;
      }
      plVar18 = plVar2;
      if (plVar20 < plVar2) {
        plVar18 = plVar20;
      }
      plStack_208 = (long *)&UNK_02b2487a;
      iVar6 = func_0x04b07bb0(lVar8,uVar9,plVar18);
      if ((iVar6 != 0) || (plVar2 != plVar20)) goto code_r0x02b241bb;
      goto code_r0x02b241c6;
    }
  }
  else {
code_r0x02b24892:
    plStack_208 = (long *)&UNK_02b24897;
    func_0x019f8660();
  }
  plStack_208 = (long *)&UNK_02b2489c;
  uVar11 = func_0x01a1b050();
  plStack_208 = (long *)&UNK_02b248a8;
  func_0x04ab4af0(plVar20 + 0xb);
  plStack_208 = (long *)&UNK_02b248b4;
  func_0x04ab4af0(plVar20 + 5);
  uVar22 = 0x1c8;
  plStack_208 = (long *)&UNK_02b248c6;
  func_0x04a8cfc8(plVar20,0x1c8);
  plStack_208 = (long *)&UNK_02b248ce;
  lVar8 = func_0x019f83c0(uVar11);
  plStack_208 = (long *)&UNK_02b248d6;
  func_0x019f83c0();
  if (*(long *)(lVar8 + 0x1a8) == 0) {
    return (long *)0x1;
  }
  plStack_2f8 = plVar17;
  uStack_230 = uVar11;
  plStack_228 = plVar12;
  plStack_220 = plVar20;
  puStack_218 = (undefined *)uVar9;
  plStack_210 = plVar21;
  plStack_208 = plVar13;
  plVar20 = (long *)func_0x04b078f0(0x40);
  plVar20[1] = 0;
  plVar20[2] = 0;
  *plVar20 = (long)&UNK_04b75dc0;
  func_0x04768bb0(plVar20 + 3,0);
  plStack_350 = plVar20 + 3;
  plStack_348 = plVar20;
  plVar20 = (long *)func_0x04b078f0(200);
  plVar20[1] = 0;
  plVar20[2] = 0;
  *plVar20 = (long)&UNK_04b75e10;
  func_0x04764190(plVar20 + 3,0);
  plStack_398 = plVar20 + 3;
  plStack_390 = plVar20;
  func_0x047d76f0(auStack_2f0,0);
  func_0x01c596d0(&uStack_3d8,uVar22);
  lVar24 = (long)&uStack_3d8 + 1;
  uVar9 = uStack_3d8 >> 1 & 0x7f;
  lVar19 = lVar24;
  if ((uStack_3d8 & 1) != 0) {
    uVar9 = uStack_3d0;
    lVar19 = lStack_3c8;
  }
  cVar5 = func_0x044027a0(auStack_2f0,lVar19,uVar9);
  if ((uStack_3d8 & 1) != 0) {
    func_0x04a8cfc8(lStack_3c8,uStack_3d8 & 0xfffffffffffffffe);
  }
  plVar20 = (long *)0x6;
  if (cVar5 == '\0') goto code_r0x02b24efe;
  func_0x01c596d0(&bStack_340,uStack_2c0 & 0xfffffffffffffffc);
  puVar15 = (uint *)(uStack_2c8 & 0xfffffffffffffffc);
  if ((*puVar15 & 1) == 0) {
    uStack_358 = *(undefined8 *)(puVar15 + 4);
    uStack_368 = *puVar15;
    uStack_364 = puVar15[1];
    uStack_360 = puVar15[2];
    uStack_35c = puVar15[3];
    puVar15 = (uint *)(uStack_2d0 & 0xfffffffffffffffc);
    if ((*puVar15 & 1) == 0) goto code_r0x02b24a93;
code_r0x02b24a4d:
    func_0x019f86e0(&uStack_388,*(undefined8 *)(puVar15 + 4),*(undefined8 *)(puVar15 + 2));
  }
  else {
    func_0x019f86e0(&uStack_368,*(undefined8 *)(puVar15 + 4),*(undefined8 *)(puVar15 + 2));
    puVar15 = (uint *)(uStack_2d0 & 0xfffffffffffffffc);
    if ((*puVar15 & 1) != 0) goto code_r0x02b24a4d;
code_r0x02b24a93:
    uStack_378 = *(undefined8 *)(puVar15 + 4);
    uStack_388 = *puVar15;
    uStack_384 = puVar15[1];
    uStack_380 = puVar15[2];
    uStack_37c = puVar15[3];
  }
  func_0x04858de0(&plStack_3e0);
  uStack_3d8 = 0;
  uStack_3d0 = 0;
  lStack_3c8 = 0;
  func_0x04aac620(&uStack_3d8,0x20,0);
  bVar26 = (*extraout_RDX_00 & 1) != 0;
  pbVar16 = extraout_RDX_00 + 1;
  if (bVar26) {
    pbVar16 = *(byte **)(extraout_RDX_00 + 0x10);
  }
  uVar9 = (ulong)(*extraout_RDX_00 >> 1);
  if (bVar26) {
    uVar9 = *(ulong *)(extraout_RDX_00 + 8);
  }
  lVar19 = lVar24;
  if ((uStack_3d8 & 1) != 0) {
    lVar19 = lStack_3c8;
  }
  func_0x04859290(pbVar16,uVar9,lVar19);
  if ((uStack_3d8 & 1) != 0) {
    lVar24 = lStack_3c8;
  }
  (**(code **)(*plStack_3e0 + 0x10))(plStack_3e0,lVar24);
  if ((uStack_3d8 & 1) != 0) {
    func_0x04a8cfc8(lStack_3c8,uStack_3d8 & 0xfffffffffffffffe);
  }
  func_0x02b4a050(&uStack_3d8,&plStack_3e0,&uStack_388,&bStack_340);
  plVar20 = (long *)0x6;
  if (cStack_3a8 != '\0') {
    func_0x01c596d0(&bStack_328,&uStack_368);
    bVar4 = bStack_328;
    if ((bStack_328 & 1) == 0) {
      uStack_320 = (ulong)(bStack_328 >> 1);
      if ((bStack_3c0 & 1) != 0) goto code_r0x02b24bb3;
code_r0x02b24b91:
      if (uStack_320 != bStack_3c0 >> 1) goto code_r0x02b24b9a;
code_r0x02b24bbd:
      puVar3 = puStack_318;
      if ((bStack_328 & 1) == 0) {
        puVar3 = &uStack_327;
      }
      puVar23 = puStack_3b0;
      if ((bStack_3c0 & 1) == 0) {
        puVar23 = &uStack_3bf;
      }
      iVar6 = func_0x04b07bb0(puVar3,puVar23);
      bVar26 = iVar6 == 0;
    }
    else {
      if ((bStack_3c0 & 1) == 0) goto code_r0x02b24b91;
code_r0x02b24bb3:
      if (uStack_320 == uStack_3b8) goto code_r0x02b24bbd;
code_r0x02b24b9a:
      bVar26 = false;
    }
    if ((bVar4 & 1) != 0) {
      func_0x04a8cfc8(puStack_318,CONCAT71(uStack_327,bStack_328) & 0xfffffffffffffffe);
    }
    if (bVar26) {
      *(uint *)(plStack_350 + 2) = *(uint *)(plStack_350 + 2) | 1;
      uVar9 = plStack_350[1];
      if ((uVar9 & 1) != 0) {
        uVar9 = *(ulong *)(uVar9 & 0xfffffffffffffffe);
      }
      func_0x042dca90(plStack_350 + 3,uStack_2d8 & 0xfffffffffffffffc,uVar9);
      lStack_2b0 = lVar8;
      func_0x047d7730(auStack_2a8,0,auStack_2f0);
      lStack_258 = lStack_3c8;
      uStack_268 = uStack_3d8;
      uStack_260 = uStack_3d0;
      uStack_3d8 = 0;
      uStack_3d0 = 0;
      lStack_3c8 = 0;
      plStack_250 = plStack_398;
      plStack_248 = plStack_390;
      if (plStack_390 != (long *)0x0) {
        LOCK();
        plStack_390[1] = plStack_390[1] + 1;
        UNLOCK();
      }
      uStack_240 = *plStack_2f8;
      uStack_238 = plStack_2f8[1];
      if (plStack_2f8[1] != 0) {
        LOCK();
        plVar20 = (long *)(plStack_2f8[1] + 8);
        *plVar20 = *plVar20 + 1;
        UNLOCK();
      }
      pbStack_308 = (byte *)0x0;
      pbVar16 = (byte *)func_0x04b078f0(0x88);
      *(undefined **)pbVar16 = &UNK_04b73f40;
      *(long *)(pbVar16 + 8) = lStack_2b0;
      pbVar25 = pbVar16 + 0x10;
      func_0x047d76f0(pbVar25,0);
      uVar9 = *(ulong *)(pbVar16 + 0x18);
      if ((uVar9 & 1) == 0) {
        if ((uStack_2a0 & 1) == 0) goto code_r0x02b24d28;
code_r0x02b24d5a:
        if (uVar9 == *(ulong *)(uStack_2a0 & 0xfffffffffffffffe)) goto code_r0x02b24d66;
code_r0x02b24d2d:
        func_0x047d7f80(pbVar25,auStack_2a8);
      }
      else {
        uVar9 = *(ulong *)(uVar9 & 0xfffffffffffffffe);
        if ((uStack_2a0 & 1) != 0) goto code_r0x02b24d5a;
code_r0x02b24d28:
        if (uVar9 != uStack_2a0) goto code_r0x02b24d2d;
code_r0x02b24d66:
        func_0x047d7fb0(pbVar25,auStack_2a8);
      }
      *(long *)(pbVar16 + 0x60) = lStack_258;
      *(ulong *)(pbVar16 + 0x50) = uStack_268;
      *(ulong *)(pbVar16 + 0x58) = uStack_260;
      uStack_268 = 0;
      uStack_260 = 0;
      lStack_258 = 0;
      *(long **)(pbVar16 + 0x68) = plStack_250;
      *(long **)(pbVar16 + 0x70) = plStack_248;
      plStack_250 = (long *)0x0;
      plStack_248 = (long *)0x0;
      *(undefined4 *)(pbVar16 + 0x78) = (undefined4)uStack_240;
      *(undefined4 *)(pbVar16 + 0x7c) = uStack_240._4_4_;
      *(undefined4 *)(pbVar16 + 0x80) = (undefined4)uStack_238;
      *(undefined4 *)(pbVar16 + 0x84) = uStack_238._4_4_;
      uStack_240 = 0;
      uStack_238 = 0;
      pbStack_308 = pbVar16;
      func_0x047d77f0(auStack_2a8);
      (**(code **)(**(long **)(lVar8 + 200) + 0x10))
                (*(long **)(lVar8 + 200),&plStack_350,&plStack_398,&bStack_328);
      if (pbStack_308 == &bStack_328) {
        lVar8 = 0x20;
code_r0x02b24e10:
        (**(code **)(*(long *)pbStack_308 + lVar8))();
      }
      else {
        lVar8 = 0x28;
        if (pbStack_308 != (byte *)0x0) goto code_r0x02b24e10;
      }
      plVar20 = (long *)0x0;
    }
  }
  if ((bStack_3c0 & 1) != 0) {
    func_0x04a8cfc8(puStack_3b0,CONCAT71(uStack_3bf,bStack_3c0) & 0xfffffffffffffffe);
  }
  plVar17 = plStack_3e0;
  if ((uStack_3d8 & 1) != 0) {
    func_0x04a8cfc8(lStack_3c8,uStack_3d8 & 0xfffffffffffffffe);
    plVar17 = plStack_3e0;
  }
  plStack_3e0 = (long *)0x0;
  if (plVar17 != (long *)0x0) {
    plStack_3e0 = (long *)0x0;
    (**(code **)(*plVar17 + 8))();
  }
  if ((uStack_388 & 1) != 0) {
    func_0x04a8cfc8(uStack_378,CONCAT44(uStack_384,uStack_388) & 0xfffffffffffffffe);
  }
  if ((uStack_368 & 1) != 0) {
    func_0x04a8cfc8(uStack_358,CONCAT44(uStack_364,uStack_368) & 0xfffffffffffffffe);
  }
  if ((bStack_340 & 1) != 0) {
    func_0x04a8cfc8(uStack_330,CONCAT71(uStack_33f,bStack_340) & 0xfffffffffffffffe);
  }
code_r0x02b24efe:
  func_0x047d77f0(auStack_2f0);
  plVar17 = plStack_390;
  if (plStack_390 != (long *)0x0) {
    LOCK();
    plVar12 = plStack_390 + 1;
    lVar8 = *plVar12;
    *plVar12 = *plVar12 + -1;
    UNLOCK();
    if (lVar8 == 0) {
      (**(code **)(*plStack_390 + 0x10))(plStack_390);
      func_0x04aa8780(plVar17);
    }
  }
  plVar17 = plStack_348;
  if (plStack_348 != (long *)0x0) {
    LOCK();
    plVar12 = plStack_348 + 1;
    lVar8 = *plVar12;
    *plVar12 = *plVar12 + -1;
    UNLOCK();
    if (lVar8 == 0) {
      (**(code **)(*plStack_348 + 0x10))(plStack_348);
      func_0x04aa8780(plVar17);
    }
  }
  return plVar20;
}


// FUNCTION 044cfea0 bounds 044cff9f

void FUN_044cfea0(long param_1,undefined8 *param_2,undefined8 *param_3,long *param_4)

{
  undefined8 uVar1;
  undefined8 uVar2;
  long *plVar3;
  undefined8 uVar4;
  long lVar5;
  undefined1 uStack_59;
  long alStack_58 [4];
  long *plStack_38;
  undefined *puStack_30;

  lVar5 = *(long *)(param_1 + 8);
  if (lVar5 != 0) {
    uVar1 = *param_2;
    uVar2 = *param_3;
    uVar4 = func_0x044d6150(param_2,param_3,param_4);
    plVar3 = *(long **)(lVar5 + 8);
    puStack_30 = &UNK_04771635;
    func_0x0437c560(0x4cf1de0);
                    /* WARNING: Could not recover jumptable at 0x0477165c. Too many branches */
                    /* WARNING: Treating indirect jump as call */
    (**(code **)(*plVar3 + 0x10))
              (plVar3,*(undefined8 *)(lRam0000000004ddc0c0 + 0x30),param_1 + 0x58,uVar1,uVar2,uVar4)
    ;
    return;
  }
  uVar1 = *(undefined8 *)(param_1 + 0x50);
  plVar3 = (long *)param_4[4];
  if (plVar3 == (long *)0x0) {
    plStack_38 = (long *)0x0;
  }
  else if (plVar3 == param_4) {
    plStack_38 = alStack_58;
    (**(code **)(*plVar3 + 0x18))();
  }
  else {
    plStack_38 = (long *)(**(code **)(*plVar3 + 0x10))();
  }
  func_0x044d85e0(&uStack_59,alStack_58,uVar1);
  if (plStack_38 == alStack_58) {
    lVar5 = 0x20;
  }
  else {
    lVar5 = 0x28;
    if (plStack_38 == (long *)0x0) {
      return;
    }
  }
  (**(code **)(*plStack_38 + lVar5))();
  return;
}

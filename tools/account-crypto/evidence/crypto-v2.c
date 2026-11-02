// FUNCTION 02b81f80 bounds 02b8203f

void FUN_02b81f80(undefined8 *param_1)

{
  *param_1 = &UNK_04b77d98;
  param_1[1] = 0;
  *(undefined4 *)(param_1 + 2) = 0;
  func_0x04859540(param_1 + 3);
  func_0x04858b50(param_1 + 4);
  func_0x04858b50(param_1 + 5);
  param_1[6] = 0x1000000020;
  *(undefined4 *)(param_1 + 7) = 0xc;
  return;
}


// FUNCTION 02b82110 bounds 02b8218f

byte * FUN_02b82110(byte *param_1,undefined8 param_2,byte *param_3)

{
  byte *pbVar1;
  ulong uVar2;
  byte *pbVar3;
  bool bVar4;

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
  func_0x04aac620(param_1,0x40,0);
  bVar4 = (*param_3 & 1) != 0;
  pbVar3 = param_3 + 1;
  if (bVar4) {
    pbVar3 = *(byte **)(param_3 + 0x10);
  }
  uVar2 = (ulong)(*param_3 >> 1);
  if (bVar4) {
    uVar2 = *(ulong *)(param_3 + 8);
  }
  if ((*param_1 & 1) == 0) {
    pbVar1 = param_1 + 1;
  }
  else {
    pbVar1 = *(byte **)(param_1 + 0x10);
  }
  func_0x048592b0(pbVar3,uVar2,pbVar1);
  return param_1;
}


// FUNCTION 02b82190 bounds 02b821bf

undefined8 FUN_02b82190(undefined8 param_1,undefined8 param_2,undefined8 param_3,undefined8 param_4)

{
  func_0x02b83320(param_1,param_3,param_4,0x20);
  return param_1;
}


// FUNCTION 02b821c0 bounds 02b823bf

ulong * FUN_02b821c0(ulong *param_1,long *param_2,undefined8 param_3)

{
  uint uStack_60;
  undefined4 uStack_5c;
  undefined4 uStack_58;
  undefined4 uStack_54;
  ulong uStack_50;
  ulong uStack_48;
  ulong uStack_40;
  ulong uStack_38;
  byte bStack_30;
  undefined7 uStack_2f;
  long lStack_28;
  undefined8 uStack_20;

  (**(code **)(*param_2 + 0x30))(&bStack_30,param_2,(int)param_2[6]);
  if ((bStack_30 & 1) == 0) {
    if (bStack_30 >> 1 == 0) goto LAB_02b822a0;
  }
  else if (lStack_28 == 0) {
LAB_02b822a0:
    param_1[4] = 0;
    param_1[5] = 0;
    param_1[2] = 0;
    param_1[3] = 0;
    *param_1 = 0;
    param_1[1] = 0;
    goto joined_r0x02b82309;
  }
  (**(code **)(*param_2 + 0x78))(&uStack_60,param_2,param_3,&bStack_30);
  if ((uStack_60 & 1) == 0) {
    if ((byte)uStack_60 >> 1 != 0) goto LAB_02b8222c;
LAB_02b822c1:
    param_1[4] = 0;
    param_1[5] = 0;
    param_1[2] = 0;
    param_1[3] = 0;
    *param_1 = 0;
    param_1[1] = 0;
  }
  else {
    if (CONCAT44(uStack_54,uStack_58) == 0) goto LAB_02b822c1;
LAB_02b8222c:
    func_0x026421e0(&uStack_48,&bStack_30);
    param_1[2] = uStack_38;
    *param_1 = uStack_48;
    param_1[1] = uStack_40;
    uStack_48 = 0;
    uStack_40 = 0;
    uStack_38 = 0;
    if ((uStack_60 & 1) == 0) {
      param_1[5] = uStack_50;
      *(uint *)(param_1 + 3) = uStack_60;
      *(undefined4 *)((long)param_1 + 0x1c) = uStack_5c;
      *(undefined4 *)(param_1 + 4) = uStack_58;
      *(undefined4 *)((long)param_1 + 0x24) = uStack_54;
    }
    else {
      func_0x019f86e0(param_1 + 3,uStack_50,CONCAT44(uStack_54,uStack_58));
      if ((uStack_48 & 1) != 0) {
        func_0x04a8cfc8(uStack_38,uStack_48 & 0xfffffffffffffffe);
      }
    }
  }
  if ((uStack_60 & 1) != 0) {
    func_0x04a8cfc8(uStack_50,CONCAT44(uStack_5c,uStack_60) & 0xfffffffffffffffe);
  }
joined_r0x02b82309:
  if ((bStack_30 & 1) != 0) {
    func_0x04a8cfc8(uStack_20,CONCAT71(uStack_2f,bStack_30) & 0xfffffffffffffffe);
  }
  return param_1;
}


// FUNCTION 02b823c0 bounds 02b8247f

byte * FUN_02b823c0(byte *param_1,long param_2,long param_3)

{
  char cVar1;
  byte *pbVar2;

  if (param_3 + 0x8000000000000001U < 0x8000000000000002) {
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
  }
  else {
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
    func_0x04aac620(param_1,param_3,0);
    if ((*param_1 & 1) == 0) {
      pbVar2 = param_1 + 1;
    }
    else {
      pbVar2 = *(byte **)(param_1 + 0x10);
    }
    cVar1 = (**(code **)(**(long **)(param_2 + 0x18) + 0x18))
                      (*(long **)(param_2 + 0x18),pbVar2,param_3);
    if (cVar1 == '\0') {
      if ((*param_1 & 1) == 0) {
        param_1[0] = 0;
        param_1[1] = 0;
      }
      else {
        **(undefined1 **)(param_1 + 0x10) = 0;
        param_1[8] = 0;
        param_1[9] = 0;
        param_1[10] = 0;
        param_1[0xb] = 0;
        param_1[0xc] = 0;
        param_1[0xd] = 0;
        param_1[0xe] = 0;
        param_1[0xf] = 0;
      }
    }
  }
  return param_1;
}


// FUNCTION 02b82480 bounds 02b8275f

/* WARNING: Type propagation algorithm not settling */

uint * FUN_02b82480(uint *param_1,byte *param_2,undefined8 param_3,byte *param_4)

{
  byte bVar1;
  code *pcVar2;
  uint uVar3;
  uint uVar4;
  undefined8 uVar5;
  int iVar6;
  ulong uVar7;
  undefined8 *puVar8;
  uint *puVar9;
  undefined1 *puVar10;
  long lVar11;
  ulong uVar12;
  long lVar13;
  ulong uVar14;
  uint *puVar15;
  byte *pbVar16;
  long *plVar17;
  ulong uVar18;
  uint uVar19;
  byte *extraout_RDX;
  byte *extraout_RDX_00;
  byte *pbVar20;
  byte *extraout_RDX_01;
  undefined1 *puVar21;
  undefined7 *puVar22;
  long *plVar23;
  undefined7 *puVar24;
  uint *puVar25;
  undefined7 *puVar26;
  undefined1 *puVar27;
  undefined1 *puVar28;
  undefined1 *puVar29;
  bool bVar30;
  undefined8 uStack_180;
  undefined1 *puStack_178;
  long lStack_170;
  uint *puStack_168;
  undefined8 uStack_160;
  undefined1 *puStack_158;
  long lStack_150;
  undefined8 uStack_148;
  undefined1 *puStack_140;
  byte *pbStack_138;
  uint uStack_12c;
  undefined1 *puStack_128;
  uint uStack_120;
  uint uStack_11c;
  uint uStack_118;
  uint uStack_114;
  undefined8 uStack_110;
  byte bStack_108;
  undefined7 uStack_107;
  undefined1 *puStack_100;
  undefined8 uStack_f8;
  byte *pbStack_f0;
  uint *puStack_e8;
  ulong uStack_e0;
  ulong uStack_d8;
  undefined1 *puStack_d0;
  ulong uStack_c8;
  undefined8 uStack_b8;
  ulong uStack_b0;
  ulong uStack_a8;
  uint uStack_9c;
  uint uStack_98;
  uint uStack_94;
  byte bStack_90;
  undefined7 uStack_8f;
  ulong uStack_88;
  undefined7 *puStack_80;
  byte bStack_78;
  undefined7 uStack_77;
  undefined1 *puStack_70;
  undefined7 *puStack_68;
  byte bStack_60;
  undefined7 uStack_5f;
  ulong uStack_58;
  undefined7 *puStack_50;
  undefined7 *puStack_48;
  undefined7 *puStack_40;
  ulong uStack_38;

  if ((*param_4 & 1) == 0) {
    if ((uint)(*param_4 >> 1) != *(uint *)(param_2 + 0x30)) goto LAB_02b824ad;
  }
  else if (*(ulong *)(param_4 + 8) != (ulong)*(uint *)(param_2 + 0x30)) {
LAB_02b824ad:
    param_1[0] = 0;
    param_1[1] = 0;
    param_1[2] = 0;
    param_1[3] = 0;
    param_1[4] = 0;
    param_1[5] = 0;
    return param_1;
  }
  (**(code **)(*(long *)param_2 + 0x30))(&bStack_90,param_2,(long)*(int *)(param_2 + 0x38));
  func_0x02b819b0(&bStack_78);
  uVar18 = (ulong)bStack_78;
  puVar29 = puStack_70;
  if ((bStack_78 & 1) == 0) {
    puVar29 = (undefined1 *)(ulong)(bStack_78 >> 1);
  }
  if (puVar29 != (undefined1 *)0x0) {
    pbVar16 = (byte *)(ulong)bStack_60;
    uVar12 = uStack_58;
    if ((bStack_60 & 1) == 0) {
      uVar12 = (ulong)(bStack_60 >> 1);
    }
    if (uVar12 != 0) {
      if ((bStack_90 & 1) == 0) {
        uStack_88 = (ulong)(bStack_90 >> 1);
      }
      uVar14 = uStack_88 + uVar12;
      if (uVar14 < 0xfffffffffffffff7) {
        uStack_94 = (uint)bStack_78;
        uStack_38 = uStack_88;
        if (uVar14 < 0x17) {
          bVar30 = false;
          uStack_b8 = CONCAT71(uStack_b8._1_7_,(char)uVar14 * '\x02');
          puVar22 = puStack_80;
          puVar24 = puStack_50;
          uVar19 = (uint)bStack_90;
        }
        else {
          puStack_48 = puStack_80;
          puStack_40 = puStack_50;
          uVar7 = (uVar14 & 0xfffffffffffffff8) + 8;
          uVar18 = 0x1a;
          if (uVar7 != 0x18) {
            uVar18 = uVar7;
          }
          uStack_9c = (uint)bStack_90;
          uStack_98 = (uint)bStack_60;
          uStack_88 = func_0x04b078f0(uVar18);
          uStack_b8 = uVar18 | 1;
          bVar30 = true;
          pbVar16 = (byte *)(ulong)uStack_98;
          puVar22 = puStack_48;
          puVar24 = puStack_40;
          uStack_b0 = uVar14;
          uStack_a8 = uStack_88;
          uVar19 = uStack_9c;
        }
        puVar26 = &uStack_8f;
        if ((uVar19 & 1) != 0) {
          puVar26 = puVar22;
        }
        puVar22 = &uStack_5f;
        if (((ulong)pbVar16 & 1) != 0) {
          puVar22 = puVar24;
        }
        uVar18 = (long)&uStack_b8 + 1;
        if (bVar30) {
          uVar18 = uStack_88;
        }
        func_0x04b07a30(uVar18,puVar22,uVar12);
        uVar14 = uStack_38;
        func_0x04b07a30(uVar18 + uVar12,puVar26,uStack_38);
        *(undefined1 *)(uVar14 + uVar18 + uVar12) = 0;
        puVar22 = puStack_68;
        if ((uStack_94 & 1) == 0) {
          puVar22 = &uStack_77;
        }
        puVar8 = (undefined8 *)func_0x04aac7b0(&uStack_b8,puVar22,puVar29);
        *(undefined8 *)(param_1 + 4) = puVar8[2];
        uVar5 = puVar8[1];
        *(undefined8 *)param_1 = *puVar8;
        *(undefined8 *)(param_1 + 2) = uVar5;
        *puVar8 = 0;
        puVar8[1] = 0;
        puVar8[2] = 0;
        if ((uStack_b8 & 1) != 0) {
          func_0x04a8cfc8(uStack_a8,uStack_b8 & 0xfffffffffffffffe);
        }
        goto LAB_02b82689;
      }
      puVar9 = (uint *)func_0x019f8660();
      if ((uStack_b8 & 1) != 0) {
        param_2 = (byte *)(uStack_b8 & 0xfffffffffffffffe);
        func_0x04a8cfc8(uStack_a8);
      }
      func_0x01b680c0(&bStack_78);
      if ((bStack_90 & 1) != 0) {
        param_2 = (byte *)(CONCAT71(uStack_8f,bStack_90) & 0xfffffffffffffffe);
        func_0x04a8cfc8(puStack_80);
      }
      puVar25 = puVar9;
      func_0x04b07a10();
      if ((*pbVar16 & 1) == 0) {
        if ((uint)(*pbVar16 >> 1) != *(uint *)(param_2 + 0x30)) goto code_r0x02b8278b;
      }
      else if (*(ulong *)(pbVar16 + 8) != (ulong)*(uint *)(param_2 + 0x30)) {
code_r0x02b8278b:
        puVar25[0] = 0;
        puVar25[1] = 0;
        puVar25[2] = 0;
        puVar25[3] = 0;
        puVar25[4] = 0;
        puVar25[5] = 0;
        return puVar25;
      }
      puVar10 = (undefined1 *)(long)*(int *)(param_2 + 0x34);
      bVar1 = *extraout_RDX;
      if ((bVar1 & 1) == 0) {
        puVar27 = (undefined1 *)(ulong)(bVar1 >> 1);
      }
      else {
        puVar27 = *(undefined1 **)(extraout_RDX + 8);
      }
      puVar21 = puVar10;
      if (puVar27 < puVar10) {
        puVar21 = puVar27;
      }
      puStack_e8 = puVar9;
      uStack_e0 = uVar18;
      uStack_d8 = uVar14;
      puStack_d0 = puVar29;
      uStack_c8 = uVar12;
      if (puVar21 < (undefined1 *)0xfffffffffffffff7) {
        pbVar20 = *(byte **)(extraout_RDX + 0x10);
        puStack_168 = puVar25;
        puStack_128 = puVar10;
        pbStack_f0 = pbVar16;
        if (puVar21 < (undefined1 *)0x17) {
          uStack_180 = (undefined1 *)CONCAT71(uStack_180._1_7_,(char)puVar21 * '\x02');
          lVar11 = (long)&uStack_180 + 1;
          uVar19 = (uint)bVar1;
        }
        else {
          lVar11 = ((ulong)puVar21 & 0xfffffffffffffff8) + 8;
          lVar13 = 0x1a;
          if (lVar11 != 0x18) {
            lVar13 = lVar11;
          }
          uStack_12c = (uint)bVar1;
          lVar11 = func_0x04b078f0(lVar13);
          puVar29 = (undefined1 *)(lVar13 + 1);
          uStack_180 = puVar29;
          puStack_178 = puVar21;
          lStack_170 = lVar11;
          uVar19 = uStack_12c;
        }
        pbVar16 = extraout_RDX + 1;
        if ((uVar19 & 1) != 0) {
          pbVar16 = pbVar20;
        }
        func_0x04b07a30(lVar11,pbVar16,puVar21);
        puVar21[lVar11] = 0;
        puVar21 = puVar27 + -(long)puStack_128;
        if (puVar27 < puStack_128) goto code_r0x02b82b44;
        iVar6 = *(int *)(param_2 + 0x38);
        puVar29 = (undefined1 *)(long)iVar6;
        if (puVar29 <= puVar21) {
          puVar21 = puVar29;
        }
        if ((undefined1 *)0xfffffffffffffff6 < puVar21) goto code_r0x02b82b49;
        if (puVar21 < (undefined1 *)0x17) {
          uStack_148 = CONCAT71(uStack_148._1_7_,(char)puVar21 * '\x02');
          param_2 = (byte *)((long)&uStack_148 + 1);
        }
        else {
          uVar12 = ((ulong)puVar21 & 0xfffffffffffffff8) + 8;
          uVar18 = 0x1a;
          if (uVar12 != 0x18) {
            uVar18 = uVar12;
          }
          param_2 = (byte *)func_0x04b078f0(uVar18);
          uStack_148 = uVar18 | 1;
          puStack_140 = puVar21;
          pbStack_138 = param_2;
        }
        puVar29 = puStack_128;
        func_0x04b07a30(param_2,puStack_128 + (long)pbVar16,puVar21);
        param_2[(long)puVar21] = 0;
        puVar29 = (undefined1 *)(long)((int)puVar29 + iVar6);
        bVar30 = puVar29 <= puVar27;
        puVar27 = puVar27 + -(long)puVar29;
        if (bVar30) {
          if ((undefined1 *)0xfffffffffffffff6 < puVar27) goto code_r0x02b82b53;
          if (puVar27 < (undefined1 *)0x17) {
            uStack_160 = CONCAT71(uStack_160._1_7_,(char)puVar27 * '\x02');
            lVar13 = (long)&uStack_160 + 1;
          }
          else {
            uVar12 = ((ulong)puVar27 & 0xfffffffffffffff8) + 8;
            uVar18 = 0x1a;
            if (uVar12 != 0x18) {
              uVar18 = uVar12;
            }
            lVar13 = func_0x04b078f0(uVar18);
            uStack_160 = uVar18 | 1;
            puStack_158 = puVar27;
            lStack_150 = lVar13;
          }
          func_0x04b07a30(lVar13,pbVar16 + (long)puVar29,puVar27);
          puVar27[lVar13] = 0;
          func_0x02b819b0(&uStack_120);
          uVar19 = uStack_120;
          uVar18 = CONCAT44(uStack_114,uStack_118);
          uVar12 = (ulong)((byte)uStack_120 >> 1);
          if ((uStack_120 & 1) != 0) {
            uVar12 = uVar18;
          }
          if (uVar12 != 0) {
            if ((bStack_108 & 1) == 0) {
              puStack_100 = (undefined1 *)(ulong)(bStack_108 >> 1);
            }
            if (puStack_100 != (undefined1 *)0x0) {
              puVar29 = puStack_178;
              if (((ulong)uStack_180 & 1) == 0) {
                puVar29 = (undefined1 *)((ulong)uStack_180 >> 1 & 0x7f);
              }
              if (puVar29 == puStack_100) {
                if (((ulong)uStack_180 & 1) == 0) {
                  lVar13 = (long)&uStack_180 + 1;
                  if ((bStack_108 & 1) == 0) goto code_r0x02b82a02;
code_r0x02b82af0:
                  iVar6 = func_0x04b07bb0(lVar13,uStack_f8);
                  puVar9 = puStack_168;
                }
                else {
                  lVar13 = lStack_170;
                  if ((bStack_108 & 1) != 0) goto code_r0x02b82af0;
code_r0x02b82a02:
                  iVar6 = func_0x04b07bb0(lVar13,&uStack_107);
                  puVar9 = puStack_168;
                }
                puStack_168 = puVar9;
                if (iVar6 == 0) {
                  if ((uVar19 & 1) == 0) {
                    *(undefined8 *)(puVar9 + 4) = uStack_110;
                    *puVar9 = uStack_120;
                    puVar9[1] = uStack_11c;
                    puVar9[2] = uStack_118;
                    puVar9[3] = uStack_114;
                  }
                  else {
                    func_0x019f86e0(puVar9,uStack_110,uVar18);
                  }
                  goto code_r0x02b82a27;
                }
              }
            }
          }
          puStack_168[0] = 0;
          puStack_168[1] = 0;
          puStack_168[2] = 0;
          puStack_168[3] = 0;
          puStack_168[4] = 0;
          puStack_168[5] = 0;
          puVar9 = puStack_168;
code_r0x02b82a27:
          if ((bStack_108 & 1) != 0) {
            func_0x04a8cfc8(uStack_f8,CONCAT71(uStack_107,bStack_108) & 0xfffffffffffffffe);
          }
          if ((uStack_120 & 1) != 0) {
            func_0x04a8cfc8(uStack_110,CONCAT44(uStack_11c,uStack_120) & 0xfffffffffffffffe);
          }
          if ((uStack_160 & 1) != 0) {
            func_0x04a8cfc8(lStack_150,uStack_160 & 0xfffffffffffffffe);
          }
          if ((uStack_148 & 1) != 0) {
            func_0x04a8cfc8(pbStack_138,uStack_148 & 0xfffffffffffffffe);
          }
          if (((ulong)uStack_180 & 1) == 0) {
            return puVar9;
          }
          func_0x04a8cfc8(lStack_170,(ulong)uStack_180 & 0xfffffffffffffffe);
          return puVar9;
        }
      }
      else {
        func_0x019f8660();
        pbVar16 = extraout_RDX;
code_r0x02b82b44:
        func_0x01a1b050();
code_r0x02b82b49:
        func_0x019f8660();
      }
      func_0x01a1b050();
code_r0x02b82b53:
      puVar9 = (uint *)func_0x019f8660();
      func_0x01b680c0(&uStack_120);
      if ((uStack_160 & 1) != 0) goto code_r0x02b82b86;
      if ((uStack_148 & 1) != 0) goto code_r0x02b82bac;
      while (((ulong)uStack_180 & 1) == 0) {
        while( true ) {
          func_0x04b07a10(puVar9);
code_r0x02b82b86:
          func_0x04a8cfc8(lStack_150,uStack_160 & 0xfffffffffffffffe);
          if ((uStack_148 & 1) == 0) break;
code_r0x02b82bac:
          func_0x04a8cfc8(pbStack_138,uStack_148 & 0xfffffffffffffffe);
          if (((ulong)uStack_180 & 1) != 0) goto code_r0x02b82bd0;
        }
      }
code_r0x02b82bd0:
      plVar23 = (long *)((ulong)uStack_180 & 0xfffffffffffffffe);
      func_0x04a8cfc8(lStack_170);
      puVar25 = puVar9;
      func_0x04b07a10();
      plVar17 = plVar23 + 4;
      pbVar20 = extraout_RDX_00;
      puVar8 = &uStack_180;
      do {
        *(undefined1 **)((long)puVar8 + -8) = puVar21;
        *(byte **)((long)puVar8 + -0x10) = pbVar16;
        *(undefined1 **)((long)puVar8 + -0x18) = puVar29;
        *(byte **)((long)puVar8 + -0x20) = param_2;
        *(undefined1 **)((long)puVar8 + -0x28) = puVar27;
        *(uint **)((long)puVar8 + -0x30) = puVar9;
        if ((*pbVar20 & 1) == 0) {
          if (*pbVar20 >> 1 == 0) goto code_r0x02b82c79;
        }
        else if (*(long *)(pbVar20 + 8) == 0) {
code_r0x02b82c79:
          puVar25[0] = 0;
          puVar25[1] = 0;
          puVar25[2] = 0;
          puVar25[3] = 0;
          puVar25[4] = 0;
          puVar25[5] = 0;
          return puVar25;
        }
        puVar27 = (undefined1 *)((long)puVar8 + -0x68);
        pcVar2 = *(code **)(*plVar23 + 0x30);
        *(undefined **)((long)puVar8 + -0xa0) = &UNK_02b82c3a;
        (*pcVar2)(puVar27);
        *(undefined **)((long)puVar8 + -0xa0) = &UNK_02b82c4b;
        puVar10 = (undefined1 *)
                  func_0x02b83440((undefined1 *)((long)puVar8 + -0x98),plVar17,pbVar20,puVar27);
        if (*(char *)((long)puVar8 + -0x80) == '\0') {
          puVar25[0] = 0;
          puVar25[1] = 0;
          puVar25[2] = 0;
          puVar25[3] = 0;
          puVar25[4] = 0;
          puVar25[5] = 0;
          bVar1 = *(byte *)((long)puVar8 + -0x98);
          goto joined_r0x02b82c9e;
        }
        bVar1 = *(byte *)((long)puVar8 + -0x68);
        uVar19 = (uint)bVar1;
        if ((bVar1 & 1) == 0) {
          puVar29 = (undefined1 *)(ulong)(bVar1 >> 1);
          uVar18 = (ulong)*(byte *)((long)puVar8 + -0x98);
          if ((*(byte *)((long)puVar8 + -0x98) & 1) == 0) goto code_r0x02b82c6a;
code_r0x02b82cb8:
          pbVar16 = *(byte **)((long)puVar8 + -0x90);
        }
        else {
          puVar29 = *(undefined1 **)((long)puVar8 + -0x60);
          uVar18 = (ulong)*(byte *)((long)puVar8 + -0x98);
          if ((*(byte *)((long)puVar8 + -0x98) & 1) != 0) goto code_r0x02b82cb8;
code_r0x02b82c6a:
          pbVar16 = (byte *)(uVar18 >> 1);
        }
        param_2 = pbVar16 + (long)puVar29;
        if (param_2 < (byte *)0xfffffffffffffff7) {
          puVar27 = *(undefined1 **)((long)puVar8 + -0x58);
          puVar21 = *(undefined1 **)((long)puVar8 + -0x88);
          *(undefined1 **)((long)puVar8 + -0x70) = puVar21;
          if (param_2 < (byte *)0x17) {
            bVar30 = false;
            *(char *)((long)puVar8 + -0x50) = (char)param_2 * '\x02';
          }
          else {
            *(undefined1 **)((long)puVar8 + -0x38) = puVar27;
            *(uint *)((long)puVar8 + -0x74) = (uint)bVar1;
            uVar14 = ((ulong)param_2 & 0xfffffffffffffff8) + 8;
            uVar12 = 0x1a;
            if (uVar14 != 0x18) {
              uVar12 = uVar14;
            }
            *(undefined **)((long)puVar8 + -0xa0) = &UNK_02b82d16;
            puVar10 = (undefined1 *)func_0x04b078f0(uVar12);
            *(ulong *)((long)puVar8 + -0x50) = uVar12 | 1;
            *(byte **)((long)puVar8 + -0x48) = param_2;
            *(undefined1 **)((long)puVar8 + -0x40) = puVar10;
            bVar30 = true;
            uVar19 = *(uint *)((long)puVar8 + -0x74);
            puVar21 = *(undefined1 **)((long)puVar8 + -0x70);
            puVar27 = *(undefined1 **)((long)puVar8 + -0x38);
          }
          puVar28 = (undefined1 *)((long)puVar8 + -0x97);
          if ((uVar18 & 1) != 0) {
            puVar28 = puVar21;
          }
          puVar21 = (undefined1 *)((long)puVar8 + -0x67);
          if ((uVar19 & 1) != 0) {
            puVar21 = puVar27;
          }
          puVar27 = (undefined1 *)((long)puVar8 + -0x4f);
          if (bVar30) {
            puVar27 = puVar10;
          }
          *(undefined **)((long)puVar8 + -0xa0) = &UNK_02b82d70;
          func_0x04b07a30(puVar27,puVar21,puVar29);
          *(undefined **)((long)puVar8 + -0xa0) = &UNK_02b82d81;
          func_0x04b07a30(puVar27 + (long)puVar29,puVar28,pbVar16);
          pbVar16[(long)(puVar27 + (long)puVar29)] = 0;
          if ((uVar18 & 1) != 0) {
            *(undefined **)((long)puVar8 + -0xa0) = &UNK_02b82d9e;
            func_0x04a8cfc8(*(undefined8 *)((long)puVar8 + -0x70),
                            *(ulong *)((long)puVar8 + -0x98) & 0xfffffffffffffffe);
          }
          *(undefined8 *)((long)puVar8 + -0x88) = *(undefined8 *)((long)puVar8 + -0x40);
          *(undefined4 *)((long)puVar8 + -0x98) = *(undefined4 *)((long)puVar8 + -0x50);
          *(undefined4 *)((long)puVar8 + -0x94) = *(undefined4 *)((long)puVar8 + -0x4c);
          *(undefined4 *)((long)puVar8 + -0x90) = *(undefined4 *)((long)puVar8 + -0x48);
          *(undefined4 *)((long)puVar8 + -0x8c) = *(undefined4 *)((long)puVar8 + -0x44);
          if ((*(byte *)((long)puVar8 + -0x98) & 1) == 0) {
            *(undefined8 *)(puVar25 + 4) = *(undefined8 *)((long)puVar8 + -0x88);
            uVar19 = *(uint *)((long)puVar8 + -0x94);
            uVar3 = *(uint *)((long)puVar8 + -0x90);
            uVar4 = *(uint *)((long)puVar8 + -0x8c);
            *puVar25 = *(uint *)((long)puVar8 + -0x98);
            puVar25[1] = uVar19;
            puVar25[2] = uVar3;
            puVar25[3] = uVar4;
            bVar1 = *(byte *)((long)puVar8 + -0x98);
          }
          else {
            *(undefined **)((long)puVar8 + -0xa0) = &UNK_02b82dc9;
            func_0x019f86e0(puVar25,*(undefined8 *)((long)puVar8 + -0x88),
                            *(undefined8 *)((long)puVar8 + -0x90));
            bVar1 = *(byte *)((long)puVar8 + -0x98);
          }
joined_r0x02b82c9e:
          if ((bVar1 & 1) != 0) {
            *(undefined **)((long)puVar8 + -0xa0) = &UNK_02b82de1;
            func_0x04a8cfc8(*(undefined8 *)((long)puVar8 + -0x88),
                            *(ulong *)((long)puVar8 + -0x98) & 0xfffffffffffffffe);
          }
          if ((*(byte *)((long)puVar8 + -0x68) & 1) != 0) {
            *(undefined **)((long)puVar8 + -0xa0) = &UNK_02b82dfb;
            func_0x04a8cfc8(*(undefined8 *)((long)puVar8 + -0x58),
                            *(ulong *)((long)puVar8 + -0x68) & 0xfffffffffffffffe);
          }
          return puVar25;
        }
        *(undefined **)((long)puVar8 + -0xa0) = &UNK_02b82e2a;
        puVar15 = (uint *)func_0x019f8660();
        *(undefined **)((long)puVar8 + -0xa0) = &UNK_02b82e32;
        plVar23 = plVar17;
        func_0x019f83c0();
        plVar17 = plVar23 + 5;
        pbVar20 = extraout_RDX_01;
        puVar9 = puVar25;
        puVar8 = (undefined8 *)((long)puVar8 + -0x98);
        puVar25 = puVar15;
      } while( true );
    }
  }
  param_1[0] = 0;
  param_1[1] = 0;
  param_1[2] = 0;
  param_1[3] = 0;
  param_1[4] = 0;
  param_1[5] = 0;
LAB_02b82689:
  if ((bStack_60 & 1) != 0) {
    func_0x04a8cfc8(puStack_50,CONCAT71(uStack_5f,bStack_60) & 0xfffffffffffffffe);
  }
  if ((bStack_78 & 1) != 0) {
    func_0x04a8cfc8(puStack_68,CONCAT71(uStack_77,bStack_78) & 0xfffffffffffffffe);
  }
  if ((bStack_90 & 1) == 0) {
    return param_1;
  }
  func_0x04a8cfc8(puStack_80,CONCAT71(uStack_8f,bStack_90) & 0xfffffffffffffffe);
  return param_1;
}


// FUNCTION 02b82760 bounds 02b82bef

/* WARNING: Type propagation algorithm not settling */

uint * FUN_02b82760(uint *param_1,byte *param_2,byte *param_3,byte *param_4)

{
  byte bVar1;
  code *pcVar2;
  uint uVar3;
  uint uVar4;
  int iVar5;
  undefined1 *puVar6;
  long lVar7;
  ulong uVar8;
  long lVar9;
  uint *puVar10;
  ulong uVar11;
  uint *puVar12;
  long *plVar13;
  ulong uVar14;
  uint uVar15;
  byte *extraout_RDX;
  byte *pbVar16;
  byte *extraout_RDX_00;
  undefined8 *puVar17;
  undefined1 *puVar18;
  long *plVar19;
  uint *puVar20;
  undefined1 *puVar21;
  undefined1 *puVar22;
  undefined1 *unaff_R14;
  bool bVar23;
  undefined8 uStack_c8;
  undefined1 *puStack_c0;
  long lStack_b8;
  uint *puStack_b0;
  undefined8 uStack_a8;
  undefined1 *puStack_a0;
  long lStack_98;
  undefined8 uStack_90;
  undefined1 *puStack_88;
  byte *pbStack_80;
  uint uStack_74;
  undefined1 *puStack_70;
  uint uStack_68;
  uint uStack_64;
  uint uStack_60;
  uint uStack_5c;
  undefined8 uStack_58;
  byte bStack_50;
  undefined7 uStack_4f;
  undefined1 *puStack_48;
  undefined8 uStack_40;
  byte *pbStack_38;

  if ((*param_4 & 1) == 0) {
    if ((uint)(*param_4 >> 1) != *(uint *)(param_2 + 0x30)) goto LAB_02b8278b;
  }
  else if (*(ulong *)(param_4 + 8) != (ulong)*(uint *)(param_2 + 0x30)) {
LAB_02b8278b:
    param_1[0] = 0;
    param_1[1] = 0;
    param_1[2] = 0;
    param_1[3] = 0;
    param_1[4] = 0;
    param_1[5] = 0;
    return param_1;
  }
  puVar6 = (undefined1 *)(long)*(int *)(param_2 + 0x34);
  bVar1 = *param_3;
  if ((bVar1 & 1) == 0) {
    puVar21 = (undefined1 *)(ulong)(bVar1 >> 1);
  }
  else {
    puVar21 = *(undefined1 **)(param_3 + 8);
  }
  puVar18 = puVar6;
  if (puVar21 < puVar6) {
    puVar18 = puVar21;
  }
  if (puVar18 < (undefined1 *)0xfffffffffffffff7) {
    pbVar16 = *(byte **)(param_3 + 0x10);
    puStack_b0 = param_1;
    puStack_70 = puVar6;
    pbStack_38 = param_4;
    if (puVar18 < (undefined1 *)0x17) {
      uStack_c8 = (undefined1 *)CONCAT71(uStack_c8._1_7_,(char)puVar18 * '\x02');
      lVar7 = (long)&uStack_c8 + 1;
      uVar15 = (uint)bVar1;
    }
    else {
      lVar7 = ((ulong)puVar18 & 0xfffffffffffffff8) + 8;
      lVar9 = 0x1a;
      if (lVar7 != 0x18) {
        lVar9 = lVar7;
      }
      uStack_74 = (uint)bVar1;
      lVar7 = func_0x04b078f0(lVar9);
      unaff_R14 = (undefined1 *)(lVar9 + 1);
      uStack_c8 = unaff_R14;
      puStack_c0 = puVar18;
      lStack_b8 = lVar7;
      uVar15 = uStack_74;
    }
    param_3 = param_3 + 1;
    if ((uVar15 & 1) != 0) {
      param_3 = pbVar16;
    }
    func_0x04b07a30(lVar7,param_3,puVar18);
    puVar18[lVar7] = 0;
    puVar18 = puVar21 + -(long)puStack_70;
    if (puVar21 < puStack_70) goto LAB_02b82b44;
    iVar5 = *(int *)(param_2 + 0x38);
    unaff_R14 = (undefined1 *)(long)iVar5;
    if (unaff_R14 <= puVar18) {
      puVar18 = unaff_R14;
    }
    if ((undefined1 *)0xfffffffffffffff6 < puVar18) goto LAB_02b82b49;
    if (puVar18 < (undefined1 *)0x17) {
      uStack_90 = CONCAT71(uStack_90._1_7_,(char)puVar18 * '\x02');
      param_2 = (byte *)((long)&uStack_90 + 1);
    }
    else {
      uVar8 = ((ulong)puVar18 & 0xfffffffffffffff8) + 8;
      uVar14 = 0x1a;
      if (uVar8 != 0x18) {
        uVar14 = uVar8;
      }
      param_2 = (byte *)func_0x04b078f0(uVar14);
      uStack_90 = uVar14 | 1;
      puStack_88 = puVar18;
      pbStack_80 = param_2;
    }
    puVar6 = puStack_70;
    func_0x04b07a30(param_2,puStack_70 + (long)param_3,puVar18);
    param_2[(long)puVar18] = 0;
    unaff_R14 = (undefined1 *)(long)((int)puVar6 + iVar5);
    bVar23 = unaff_R14 <= puVar21;
    puVar21 = puVar21 + -(long)unaff_R14;
    if (bVar23) {
      if ((undefined1 *)0xfffffffffffffff6 < puVar21) goto LAB_02b82b53;
      if (puVar21 < (undefined1 *)0x17) {
        uStack_a8 = CONCAT71(uStack_a8._1_7_,(char)puVar21 * '\x02');
        lVar9 = (long)&uStack_a8 + 1;
      }
      else {
        uVar8 = ((ulong)puVar21 & 0xfffffffffffffff8) + 8;
        uVar14 = 0x1a;
        if (uVar8 != 0x18) {
          uVar14 = uVar8;
        }
        lVar9 = func_0x04b078f0(uVar14);
        uStack_a8 = uVar14 | 1;
        puStack_a0 = puVar21;
        lStack_98 = lVar9;
      }
      func_0x04b07a30(lVar9,param_3 + (long)unaff_R14,puVar21);
      puVar21[lVar9] = 0;
      func_0x02b819b0(&uStack_68);
      uVar15 = uStack_68;
      uVar14 = CONCAT44(uStack_5c,uStack_60);
      uVar8 = (ulong)((byte)uStack_68 >> 1);
      if ((uStack_68 & 1) != 0) {
        uVar8 = uVar14;
      }
      if (uVar8 != 0) {
        if ((bStack_50 & 1) == 0) {
          puStack_48 = (undefined1 *)(ulong)(bStack_50 >> 1);
        }
        if (puStack_48 != (undefined1 *)0x0) {
          puVar6 = puStack_c0;
          if (((ulong)uStack_c8 & 1) == 0) {
            puVar6 = (undefined1 *)((ulong)uStack_c8 >> 1 & 0x7f);
          }
          if (puVar6 == puStack_48) {
            if (((ulong)uStack_c8 & 1) == 0) {
              lVar9 = (long)&uStack_c8 + 1;
              if ((bStack_50 & 1) != 0) goto LAB_02b82af0;
LAB_02b82a02:
              iVar5 = func_0x04b07bb0(lVar9,&uStack_4f);
              puVar10 = puStack_b0;
            }
            else {
              lVar9 = lStack_b8;
              if ((bStack_50 & 1) == 0) goto LAB_02b82a02;
LAB_02b82af0:
              iVar5 = func_0x04b07bb0(lVar9,uStack_40);
              puVar10 = puStack_b0;
            }
            puStack_b0 = puVar10;
            if (iVar5 == 0) {
              if ((uVar15 & 1) == 0) {
                *(undefined8 *)(puVar10 + 4) = uStack_58;
                *puVar10 = uStack_68;
                puVar10[1] = uStack_64;
                puVar10[2] = uStack_60;
                puVar10[3] = uStack_5c;
              }
              else {
                func_0x019f86e0(puVar10,uStack_58,uVar14);
              }
              goto LAB_02b82a27;
            }
          }
        }
      }
      puStack_b0[0] = 0;
      puStack_b0[1] = 0;
      puStack_b0[2] = 0;
      puStack_b0[3] = 0;
      puStack_b0[4] = 0;
      puStack_b0[5] = 0;
      puVar10 = puStack_b0;
LAB_02b82a27:
      if ((bStack_50 & 1) != 0) {
        func_0x04a8cfc8(uStack_40,CONCAT71(uStack_4f,bStack_50) & 0xfffffffffffffffe);
      }
      if ((uStack_68 & 1) != 0) {
        func_0x04a8cfc8(uStack_58,CONCAT44(uStack_64,uStack_68) & 0xfffffffffffffffe);
      }
      if ((uStack_a8 & 1) != 0) {
        func_0x04a8cfc8(lStack_98,uStack_a8 & 0xfffffffffffffffe);
      }
      if ((uStack_90 & 1) != 0) {
        func_0x04a8cfc8(pbStack_80,uStack_90 & 0xfffffffffffffffe);
      }
      if (((ulong)uStack_c8 & 1) == 0) {
        return puVar10;
      }
      func_0x04a8cfc8(lStack_b8,(ulong)uStack_c8 & 0xfffffffffffffffe);
      return puVar10;
    }
  }
  else {
    func_0x019f8660();
LAB_02b82b44:
    func_0x01a1b050();
LAB_02b82b49:
    func_0x019f8660();
  }
  func_0x01a1b050();
LAB_02b82b53:
  puVar10 = (uint *)func_0x019f8660();
  func_0x01b680c0(&uStack_68);
  if ((uStack_a8 & 1) != 0) goto LAB_02b82b86;
  if ((uStack_90 & 1) != 0) goto LAB_02b82bac;
  while (((ulong)uStack_c8 & 1) == 0) {
    while( true ) {
      func_0x04b07a10(puVar10);
LAB_02b82b86:
      func_0x04a8cfc8(lStack_98,uStack_a8 & 0xfffffffffffffffe);
      if ((uStack_90 & 1) == 0) break;
LAB_02b82bac:
      func_0x04a8cfc8(pbStack_80,uStack_90 & 0xfffffffffffffffe);
      if (((ulong)uStack_c8 & 1) != 0) goto LAB_02b82bd0;
    }
  }
LAB_02b82bd0:
  plVar19 = (long *)((ulong)uStack_c8 & 0xfffffffffffffffe);
  func_0x04a8cfc8(lStack_b8);
  puVar20 = puVar10;
  func_0x04b07a10();
  plVar13 = plVar19 + 4;
  pbVar16 = extraout_RDX;
  puVar17 = &uStack_c8;
  do {
    *(undefined1 **)((long)puVar17 + -8) = puVar18;
    *(byte **)((long)puVar17 + -0x10) = param_3;
    *(undefined1 **)((long)puVar17 + -0x18) = unaff_R14;
    *(byte **)((long)puVar17 + -0x20) = param_2;
    *(undefined1 **)((long)puVar17 + -0x28) = puVar21;
    *(uint **)((long)puVar17 + -0x30) = puVar10;
    if ((*pbVar16 & 1) == 0) {
      if (*pbVar16 >> 1 == 0) goto code_r0x02b82c79;
    }
    else if (*(long *)(pbVar16 + 8) == 0) {
code_r0x02b82c79:
      puVar20[0] = 0;
      puVar20[1] = 0;
      puVar20[2] = 0;
      puVar20[3] = 0;
      puVar20[4] = 0;
      puVar20[5] = 0;
      return puVar20;
    }
    puVar21 = (undefined1 *)((long)puVar17 + -0x68);
    pcVar2 = *(code **)(*plVar19 + 0x30);
    *(undefined **)((long)puVar17 + -0xa0) = &UNK_02b82c3a;
    (*pcVar2)(puVar21);
    *(undefined **)((long)puVar17 + -0xa0) = &UNK_02b82c4b;
    puVar6 = (undefined1 *)
             func_0x02b83440((undefined1 *)((long)puVar17 + -0x98),plVar13,pbVar16,puVar21);
    if (*(char *)((long)puVar17 + -0x80) == '\0') {
      puVar20[0] = 0;
      puVar20[1] = 0;
      puVar20[2] = 0;
      puVar20[3] = 0;
      puVar20[4] = 0;
      puVar20[5] = 0;
      bVar1 = *(byte *)((long)puVar17 + -0x98);
      goto joined_r0x02b82c9e;
    }
    bVar1 = *(byte *)((long)puVar17 + -0x68);
    uVar15 = (uint)bVar1;
    if ((bVar1 & 1) == 0) {
      unaff_R14 = (undefined1 *)(ulong)(bVar1 >> 1);
      uVar14 = (ulong)*(byte *)((long)puVar17 + -0x98);
      if ((*(byte *)((long)puVar17 + -0x98) & 1) != 0) goto code_r0x02b82cb8;
code_r0x02b82c6a:
      param_3 = (byte *)(uVar14 >> 1);
    }
    else {
      unaff_R14 = *(undefined1 **)((long)puVar17 + -0x60);
      uVar14 = (ulong)*(byte *)((long)puVar17 + -0x98);
      if ((*(byte *)((long)puVar17 + -0x98) & 1) == 0) goto code_r0x02b82c6a;
code_r0x02b82cb8:
      param_3 = *(byte **)((long)puVar17 + -0x90);
    }
    param_2 = param_3 + (long)unaff_R14;
    if (param_2 < (byte *)0xfffffffffffffff7) {
      puVar21 = *(undefined1 **)((long)puVar17 + -0x58);
      puVar18 = *(undefined1 **)((long)puVar17 + -0x88);
      *(undefined1 **)((long)puVar17 + -0x70) = puVar18;
      if (param_2 < (byte *)0x17) {
        bVar23 = false;
        *(char *)((long)puVar17 + -0x50) = (char)param_2 * '\x02';
      }
      else {
        *(undefined1 **)((long)puVar17 + -0x38) = puVar21;
        *(uint *)((long)puVar17 + -0x74) = (uint)bVar1;
        uVar11 = ((ulong)param_2 & 0xfffffffffffffff8) + 8;
        uVar8 = 0x1a;
        if (uVar11 != 0x18) {
          uVar8 = uVar11;
        }
        *(undefined **)((long)puVar17 + -0xa0) = &UNK_02b82d16;
        puVar6 = (undefined1 *)func_0x04b078f0(uVar8);
        *(ulong *)((long)puVar17 + -0x50) = uVar8 | 1;
        *(byte **)((long)puVar17 + -0x48) = param_2;
        *(undefined1 **)((long)puVar17 + -0x40) = puVar6;
        bVar23 = true;
        uVar15 = *(uint *)((long)puVar17 + -0x74);
        puVar18 = *(undefined1 **)((long)puVar17 + -0x70);
        puVar21 = *(undefined1 **)((long)puVar17 + -0x38);
      }
      puVar22 = (undefined1 *)((long)puVar17 + -0x97);
      if ((uVar14 & 1) != 0) {
        puVar22 = puVar18;
      }
      puVar18 = (undefined1 *)((long)puVar17 + -0x67);
      if ((uVar15 & 1) != 0) {
        puVar18 = puVar21;
      }
      puVar21 = (undefined1 *)((long)puVar17 + -0x4f);
      if (bVar23) {
        puVar21 = puVar6;
      }
      *(undefined **)((long)puVar17 + -0xa0) = &UNK_02b82d70;
      func_0x04b07a30(puVar21,puVar18,unaff_R14);
      *(undefined **)((long)puVar17 + -0xa0) = &UNK_02b82d81;
      func_0x04b07a30(puVar21 + (long)unaff_R14,puVar22,param_3);
      param_3[(long)(puVar21 + (long)unaff_R14)] = 0;
      if ((uVar14 & 1) != 0) {
        *(undefined **)((long)puVar17 + -0xa0) = &UNK_02b82d9e;
        func_0x04a8cfc8(*(undefined8 *)((long)puVar17 + -0x70),
                        *(ulong *)((long)puVar17 + -0x98) & 0xfffffffffffffffe);
      }
      *(undefined8 *)((long)puVar17 + -0x88) = *(undefined8 *)((long)puVar17 + -0x40);
      *(undefined4 *)((long)puVar17 + -0x98) = *(undefined4 *)((long)puVar17 + -0x50);
      *(undefined4 *)((long)puVar17 + -0x94) = *(undefined4 *)((long)puVar17 + -0x4c);
      *(undefined4 *)((long)puVar17 + -0x90) = *(undefined4 *)((long)puVar17 + -0x48);
      *(undefined4 *)((long)puVar17 + -0x8c) = *(undefined4 *)((long)puVar17 + -0x44);
      if ((*(byte *)((long)puVar17 + -0x98) & 1) == 0) {
        *(undefined8 *)(puVar20 + 4) = *(undefined8 *)((long)puVar17 + -0x88);
        uVar15 = *(uint *)((long)puVar17 + -0x94);
        uVar3 = *(uint *)((long)puVar17 + -0x90);
        uVar4 = *(uint *)((long)puVar17 + -0x8c);
        *puVar20 = *(uint *)((long)puVar17 + -0x98);
        puVar20[1] = uVar15;
        puVar20[2] = uVar3;
        puVar20[3] = uVar4;
        bVar1 = *(byte *)((long)puVar17 + -0x98);
      }
      else {
        *(undefined **)((long)puVar17 + -0xa0) = &UNK_02b82dc9;
        func_0x019f86e0(puVar20,*(undefined8 *)((long)puVar17 + -0x88),
                        *(undefined8 *)((long)puVar17 + -0x90));
        bVar1 = *(byte *)((long)puVar17 + -0x98);
      }
joined_r0x02b82c9e:
      if ((bVar1 & 1) != 0) {
        *(undefined **)((long)puVar17 + -0xa0) = &UNK_02b82de1;
        func_0x04a8cfc8(*(undefined8 *)((long)puVar17 + -0x88),
                        *(ulong *)((long)puVar17 + -0x98) & 0xfffffffffffffffe);
      }
      if ((*(byte *)((long)puVar17 + -0x68) & 1) != 0) {
        *(undefined **)((long)puVar17 + -0xa0) = &UNK_02b82dfb;
        func_0x04a8cfc8(*(undefined8 *)((long)puVar17 + -0x58),
                        *(ulong *)((long)puVar17 + -0x68) & 0xfffffffffffffffe);
      }
      return puVar20;
    }
    *(undefined **)((long)puVar17 + -0xa0) = &UNK_02b82e2a;
    puVar12 = (uint *)func_0x019f8660();
    *(undefined **)((long)puVar17 + -0xa0) = &UNK_02b82e32;
    plVar19 = plVar13;
    func_0x019f83c0();
    plVar13 = plVar19 + 5;
    pbVar16 = extraout_RDX_00;
    puVar10 = puVar20;
    puVar17 = (undefined8 *)((long)puVar17 + -0x98);
    puVar20 = puVar12;
  } while( true );
}


// FUNCTION 02b83040 bounds 02b8308f

undefined8 FUN_02b83040(long param_1,byte *param_2)

{
  byte bVar1;
  undefined8 uVar2;

  bVar1 = *param_2;
  if ((bVar1 & 1) == 0) {
    if ((uint)(bVar1 >> 1) == *(uint *)(param_1 + 0x30)) goto LAB_02b83064;
  }
  else if (*(ulong *)(param_2 + 8) == (ulong)*(uint *)(param_1 + 0x30)) {
LAB_02b83064:
    if ((bVar1 & 1) == 0) {
      param_2 = param_2 + 1;
    }
    else {
      param_2 = *(byte **)(param_2 + 0x10);
    }
    uVar2 = (**(code **)(**(long **)(param_1 + 0x28) + 0x10))(*(long **)(param_1 + 0x28),param_2);
    return uVar2;
  }
  return 0;
}


// FUNCTION 02b7fc70 bounds 02b7fe2f

undefined8 FUN_02b7fc70(undefined8 param_1,byte *param_2,undefined8 param_3)

{
  undefined8 uVar1;
  ulong *puVar2;
  ulong uVar3;
  byte *pbVar4;
  bool bVar5;
  ulong uStack_98;
  ulong uStack_90;
  ulong uStack_88;
  ulong uStack_78;
  ulong uStack_70;
  ulong uStack_68;
  undefined1 auStack_60 [8];
  byte bStack_58;
  undefined7 uStack_57;
  undefined8 uStack_48;
  byte bStack_40;
  undefined7 uStack_3f;
  undefined8 uStack_30;

  uVar1 = func_0x04ae5390();
  func_0x04aea9d0(auStack_60,uVar1);
  func_0x02b7f780(&bStack_40,param_3,auStack_60);
  puVar2 = (ulong *)func_0x04aac7b0(&bStack_40,&UNK_0087b0d1,8);
  uStack_88 = puVar2[2];
  uStack_98 = *puVar2;
  uStack_90 = puVar2[1];
  *puVar2 = 0;
  puVar2[1] = 0;
  puVar2[2] = 0;
  bVar5 = (*param_2 & 1) != 0;
  pbVar4 = param_2 + 1;
  if (bVar5) {
    pbVar4 = *(byte **)(param_2 + 0x10);
  }
  uVar3 = (ulong)(*param_2 >> 1);
  if (bVar5) {
    uVar3 = *(ulong *)(param_2 + 8);
  }
  puVar2 = (ulong *)func_0x04aac7b0(&uStack_98,pbVar4,uVar3);
  uStack_68 = puVar2[2];
  uStack_78 = *puVar2;
  uStack_70 = puVar2[1];
  *puVar2 = 0;
  puVar2[1] = 0;
  puVar2[2] = 0;
  func_0x02b81930(&bStack_58,param_2,&uStack_78,10000);
  if ((uStack_78 & 1) != 0) {
    func_0x04a8cfc8(uStack_68,uStack_78 & 0xfffffffffffffffe);
  }
  if ((uStack_98 & 1) != 0) {
    func_0x04a8cfc8(uStack_88,uStack_98 & 0xfffffffffffffffe);
  }
  if ((bStack_40 & 1) != 0) {
    func_0x04a8cfc8(uStack_30,CONCAT71(uStack_3f,bStack_40) & 0xfffffffffffffffe);
  }
  func_0x04aea9f0(auStack_60);
  func_0x02b81970(param_1,&bStack_58);
  if ((bStack_58 & 1) != 0) {
    func_0x04a8cfc8(uStack_48,CONCAT71(uStack_57,bStack_58) & 0xfffffffffffffffe);
  }
  return param_1;
}


// FUNCTION 02b7fe30 bounds 02b800bf

undefined8 FUN_02b7fe30(undefined8 param_1,byte *param_2,undefined8 param_3)

{
  undefined8 uVar1;
  ulong *puVar2;
  ulong uVar3;
  byte *pbVar4;
  bool bVar5;
  ulong uStack_88;
  ulong uStack_80;
  ulong uStack_78;
  ulong uStack_68;
  ulong uStack_60;
  ulong uStack_58;
  undefined1 auStack_48 [8];
  byte bStack_40;
  undefined7 uStack_3f;
  undefined8 uStack_30;

  uVar1 = func_0x04ae5390();
  func_0x04aea9d0(auStack_48,uVar1);
  func_0x02b7f780(&bStack_40,param_3,auStack_48);
  puVar2 = (ulong *)func_0x04aac7b0(&bStack_40,&UNK_008b8a1d,0xd);
  uStack_78 = puVar2[2];
  uStack_88 = *puVar2;
  uStack_80 = puVar2[1];
  *puVar2 = 0;
  puVar2[1] = 0;
  puVar2[2] = 0;
  bVar5 = (*param_2 & 1) != 0;
  pbVar4 = param_2 + 1;
  if (bVar5) {
    pbVar4 = *(byte **)(param_2 + 0x10);
  }
  uVar3 = (ulong)(*param_2 >> 1);
  if (bVar5) {
    uVar3 = *(ulong *)(param_2 + 8);
  }
  puVar2 = (ulong *)func_0x04aac7b0(&uStack_88,pbVar4,uVar3);
  uStack_58 = puVar2[2];
  uStack_68 = *puVar2;
  uStack_60 = puVar2[1];
  *puVar2 = 0;
  puVar2[1] = 0;
  puVar2[2] = 0;
  func_0x02b81950(param_1,param_2,&uStack_68,10000);
  if ((uStack_68 & 1) != 0) {
    func_0x04a8cfc8(uStack_58,uStack_68 & 0xfffffffffffffffe);
  }
  if ((uStack_88 & 1) != 0) {
    func_0x04a8cfc8(uStack_78,uStack_88 & 0xfffffffffffffffe);
  }
  if ((bStack_40 & 1) != 0) {
    func_0x04a8cfc8(uStack_30,CONCAT71(uStack_3f,bStack_40) & 0xfffffffffffffffe);
  }
  func_0x04aea9f0(auStack_48);
  return param_1;
}

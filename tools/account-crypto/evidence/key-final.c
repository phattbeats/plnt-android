// FUNCTION 02b80da0 bounds 02b80eef

ulong FUN_02b80da0(long *param_1,byte *param_2,byte *param_3)

{
  long *plVar1;
  ulong uVar2;
  long *plVar3;
  byte bStack_40;
  undefined7 uStack_3f;
  long lStack_38;
  undefined8 uStack_30;
  undefined4 uStack_28;
  undefined4 uStack_24;
  undefined4 uStack_20;
  undefined4 uStack_1c;
  long lStack_18;

  if ((*param_2 & 1) == 0) {
    if (*param_2 >> 1 == 0) {
      return 0;
    }
  }
  else if (*(long *)(param_2 + 8) == 0) {
    return 0;
  }
  if ((*param_3 & 1) == 0) {
    if (*param_3 >> 1 == 0) {
      return 0;
    }
  }
  else if (*(long *)(param_3 + 8) == 0) {
    return 0;
  }
  func_0x02b81990(&bStack_40);
  if ((bStack_40 & 1) == 0) {
    if (bStack_40 >> 1 != 0) goto LAB_02b80e15;
  }
  else if (lStack_38 != 0) {
LAB_02b80e15:
    plVar3 = (long *)*param_1;
    if (plVar3 == (long *)0x0) {
      FUN_02b7f8e0(&uStack_28,(int)DAT_04ca79a0);
      plVar3 = (long *)CONCAT44(uStack_24,uStack_28);
      plVar1 = (long *)*param_1;
      *param_1 = (long)plVar3;
      if (plVar1 != (long *)0x0) {
        (**(code **)(*plVar1 + 8))();
        plVar3 = (long *)*param_1;
      }
    }
    (**(code **)(*plVar3 + 0x80))(&uStack_28,plVar3,param_3,&bStack_40);
    if ((*(byte *)(param_1 + 2) & 1) != 0) {
      func_0x04a8cfc8(param_1[4],param_1[2] & 0xfffffffffffffffe);
    }
    param_1[4] = lStack_18;
    *(undefined4 *)(param_1 + 2) = uStack_28;
    *(undefined4 *)((long)param_1 + 0x14) = uStack_24;
    *(undefined4 *)(param_1 + 3) = uStack_20;
    *(undefined4 *)((long)param_1 + 0x1c) = uStack_1c;
    if ((*(byte *)(param_1 + 2) & 1) == 0) {
      if (*(byte *)(param_1 + 2) >> 1 != 0) goto LAB_02b80e9e;
    }
    else if (param_1[3] != 0) {
LAB_02b80e9e:
      uVar2 = (**(code **)(*(long *)*param_1 + 0x70))((long *)*param_1,param_1 + 2);
      goto joined_r0x02b80eaf;
    }
  }
  uVar2 = 0;
joined_r0x02b80eaf:
  if ((bStack_40 & 1) != 0) {
    func_0x04a8cfc8(uStack_30,CONCAT71(uStack_3f,bStack_40) & 0xfffffffffffffffe);
    return uVar2 & 0xffffffff;
  }
  return uVar2;
}


// FUNCTION 02b80ff0 bounds 02b812af

/* WARNING: Possible PIC construction at 0x02b810c3: Changing call to branch */
/* WARNING: Possible PIC construction at 0x02b81571: Changing call to branch */
/* WARNING: Removing unreachable block (ram,0x02b810c8) */
/* WARNING: Removing unreachable block (ram,0x02b810fb) */
/* WARNING: Removing unreachable block (ram,0x02b810d2) */
/* WARNING: Removing unreachable block (ram,0x02b81109) */
/* WARNING: Removing unreachable block (ram,0x02b81117) */
/* WARNING: Removing unreachable block (ram,0x02b810e1) */
/* WARNING: Removing unreachable block (ram,0x02b8111c) */
/* WARNING: Removing unreachable block (ram,0x02b810eb) */
/* WARNING: Removing unreachable block (ram,0x02b81121) */
/* WARNING: Removing unreachable block (ram,0x02b81293) */
/* WARNING: Removing unreachable block (ram,0x02b8112a) */
/* WARNING: Removing unreachable block (ram,0x02b81298) */
/* WARNING: Removing unreachable block (ram,0x02b81138) */
/* WARNING: Removing unreachable block (ram,0x02b81151) */
/* WARNING: Removing unreachable block (ram,0x02b8116b) */
/* WARNING: Removing unreachable block (ram,0x02b8116f) */
/* WARNING: Removing unreachable block (ram,0x02b81142) */
/* WARNING: Removing unreachable block (ram,0x02b81192) */
/* WARNING: Removing unreachable block (ram,0x02b81196) */
/* WARNING: Removing unreachable block (ram,0x02b8119a) */
/* WARNING: Removing unreachable block (ram,0x02b811d2) */
/* WARNING: Removing unreachable block (ram,0x02b811e3) */
/* WARNING: Removing unreachable block (ram,0x02b8125f) */
/* WARNING: Removing unreachable block (ram,0x02b811fd) */
/* WARNING: Removing unreachable block (ram,0x02b81266) */
/* WARNING: Removing unreachable block (ram,0x02b81272) */
/* WARNING: Removing unreachable block (ram,0x02b81206) */
/* WARNING: Removing unreachable block (ram,0x02b81219) */
/* WARNING: Removing unreachable block (ram,0x02b8121e) */
/* WARNING: Removing unreachable block (ram,0x02b81225) */
/* WARNING: Removing unreachable block (ram,0x02b8123c) */
/* WARNING: Removing unreachable block (ram,0x02b81246) */
/* WARNING: Removing unreachable block (ram,0x02b81576) */
/* WARNING: Removing unreachable block (ram,0x02b815e0) */
/* WARNING: Removing unreachable block (ram,0x02b8159a) */
/* WARNING: Removing unreachable block (ram,0x02b815e9) */
/* WARNING: Removing unreachable block (ram,0x02b815ff) */
/* WARNING: Removing unreachable block (ram,0x02b815a4) */
/* WARNING: Removing unreachable block (ram,0x02b81604) */
/* WARNING: Removing unreachable block (ram,0x02b815bb) */
/* WARNING: Removing unreachable block (ram,0x02b8160a) */
/* WARNING: Removing unreachable block (ram,0x02b81719) */
/* WARNING: Removing unreachable block (ram,0x02b81618) */
/* WARNING: Removing unreachable block (ram,0x02b8162f) */
/* WARNING: Removing unreachable block (ram,0x02b8164c) */
/* WARNING: Removing unreachable block (ram,0x02b81650) */
/* WARNING: Removing unreachable block (ram,0x02b81623) */
/* WARNING: Removing unreachable block (ram,0x02b81674) */
/* WARNING: Removing unreachable block (ram,0x02b8167d) */
/* WARNING: Removing unreachable block (ram,0x02b81681) */
/* WARNING: Removing unreachable block (ram,0x02b8168b) */
/* WARNING: Removing unreachable block (ram,0x02b8168f) */
/* WARNING: Removing unreachable block (ram,0x02b816ae) */
/* WARNING: Removing unreachable block (ram,0x02b816c1) */
/* WARNING: Removing unreachable block (ram,0x02b816fd) */
/* WARNING: Removing unreachable block (ram,0x02b81717) */
/* WARNING: Removing unreachable block (ram,0x02b816c8) */
/* WARNING: Removing unreachable block (ram,0x02b816cf) */
/* WARNING: Removing unreachable block (ram,0x02b816e2) */
/* WARNING: Type propagation algorithm not settling */

byte *******
FUN_02b80ff0(byte *******param_1,byte *param_2,byte *******param_3,byte *******param_4,
            byte *******param_5)

{
  ulong uVar1;
  byte bVar2;
  long *plVar3;
  code *pcVar4;
  byte *****pppppbVar5;
  undefined8 uVar6;
  byte *******pppppppbVar7;
  ulong uVar8;
  undefined1 *puVar9;
  undefined8 *puVar10;
  byte ******ppppppbVar11;
  byte *******extraout_RDX;
  undefined8 *puVar12;
  undefined8 *puVar13;
  byte *******pppppppbVar14;
  byte ******ppppppbVar15;
  byte *******pppppppbVar16;
  byte ******ppppppbVar17;
  byte *******pppppppbVar18;
  byte *******pppppppbVar19;
  ulong uVar20;
  undefined1 *puVar21;
  byte *******pppppppbVar22;
  byte *******pppppppbVar23;
  bool bVar24;
  undefined8 uStack_a0;
  int iStack_98;
  byte *pbStack_88;
  int iStack_7c;
  byte *******apppppppbStack_60 [6];

  pppppppbVar14 = (byte *******)(param_2 + 1);
  pppppppbVar22 = pppppppbVar14;
  if ((*param_2 & 1) != 0) {
    pppppppbVar22 = *(byte ********)(param_2 + 0x10);
  }
  bVar2 = *(byte *)pppppppbVar22;
  if (bVar2 == 0) {
    pppppppbVar7 = (byte *******)0x5;
  }
  else {
    pppppppbVar7 = (byte *******)0x1;
    if ((char)bVar2 < '\x03') {
      iStack_7c = 2;
      if (DAT_04ca79a0 <= (char)bVar2) {
        iStack_7c = (uint)(DAT_04ca79a0 < (char)*(byte *)pppppppbVar22) * 3;
      }
      if ((char)*(byte *)pppppppbVar22 < 2) {
LAB_02b81076:
        uStack_a0 = 0x2b81095;
        pbStack_88 = param_2;
        FUN_02b7f8e0(apppppppbStack_60,(int)(char)*(byte *)pppppppbVar22);
        ppppppbVar15 = *param_1;
        *param_1 = (byte ******)apppppppbStack_60[0];
        pppppppbVar7 = apppppppbStack_60[0];
        if (ppppppbVar15 != (byte ******)0x0) {
          uStack_a0 = 0x2b810ab;
          (*(code *)(*ppppppbVar15)[1])();
          pppppppbVar7 = (byte *******)*param_1;
        }
        iStack_98 = (int)(char)*(byte *)pppppppbVar22;
        puVar12 = &uStack_a0;
        uStack_a0 = 0x2b810c8;
        pppppppbVar16 = (byte *******)apppppppbStack_60;
        pppppppbVar18 = param_4;
        pppppppbVar19 = param_3;
        pppppppbVar23 = param_5;
        do {
          *(byte ********)((long)puVar12 + -8) = pppppppbVar14;
          *(byte ********)((long)puVar12 + -0x10) = pppppppbVar23;
          *(byte ********)((long)puVar12 + -0x18) = pppppppbVar22;
          *(byte ********)((long)puVar12 + -0x20) = pppppppbVar19;
          *(byte ********)((long)puVar12 + -0x28) = pppppppbVar18;
          *(byte ********)((long)puVar12 + -0x30) = param_1;
          if (*(char *)((long)puVar12 + 8) == '\x02') {
            if (((ulong)*param_4 & 1) == 0) {
              pppppppbVar16[2] = param_4[2];
              ppppppbVar15 = param_4[1];
              *pppppppbVar16 = *param_4;
              pppppppbVar16[1] = ppppppbVar15;
            }
            else {
              ppppppbVar15 = param_4[1];
              ppppppbVar11 = param_4[2];
              *(undefined **)((long)puVar12 + -0xa0) = &UNK_02b812fb;
              func_0x019f86e0(pppppppbVar16,ppppppbVar11,ppppppbVar15);
            }
            return pppppppbVar16;
          }
          if (*(char *)((long)puVar12 + 8) != '\x01') {
            *pppppppbVar16 = (byte ******)0x0;
            pppppppbVar16[1] = (byte ******)0x0;
            pppppppbVar16[2] = (byte ******)0x0;
            return pppppppbVar16;
          }
          bVar2 = *(byte *)param_5;
          if ((bVar2 & 1) == 0) {
            ppppppbVar15 = (byte ******)(ulong)(bVar2 >> 1);
          }
          else {
            ppppppbVar15 = param_5[1];
          }
          uVar1 = (long)ppppppbVar15 + 0xd;
          if (uVar1 < 0xfffffffffffffff7) {
            *(byte ********)((long)puVar12 + -0x50) = pppppppbVar7;
            ppppppbVar11 = param_5[2];
            if (uVar1 < 0x17) {
              bVar24 = false;
              *(char *)((long)puVar12 + -0x90) = (char)uVar1 * '\x02';
              puVar9 = (undefined1 *)0x1;
            }
            else {
              *(byte *******)((long)puVar12 + -0x60) = ppppppbVar11;
              *(byte ********)((long)puVar12 + -0x58) = param_5;
              uVar8 = (uVar1 & 0xfffffffffffffff8) + 8;
              uVar20 = 0x1a;
              if (uVar8 != 0x18) {
                uVar20 = uVar8;
              }
              *(undefined **)((long)puVar12 + -0xa0) = &UNK_02b8137f;
              puVar9 = (undefined1 *)func_0x04b078f0(uVar20);
              *(ulong *)((long)puVar12 + -0x90) = uVar20 | 1;
              *(ulong *)((long)puVar12 + -0x88) = uVar1;
              *(undefined1 **)((long)puVar12 + -0x80) = puVar9;
              bVar24 = true;
              param_5 = *(byte ********)((long)puVar12 + -0x58);
              ppppppbVar11 = *(byte *******)((long)puVar12 + -0x60);
            }
            ppppppbVar17 = (byte ******)((long)param_5 + 1);
            if ((bVar2 & 1) != 0) {
              ppppppbVar17 = ppppppbVar11;
            }
            puVar21 = (undefined1 *)((long)puVar12 + -0x8f);
            if (bVar24) {
              puVar21 = puVar9;
            }
            *(undefined **)((long)puVar12 + -0xa0) = &UNK_02b813c5;
            func_0x04b07a30(puVar21,ppppppbVar17,ppppppbVar15);
            *(undefined8 *)(puVar21 + (long)ppppppbVar15) = 0x5073736150337374;
            *(undefined8 *)(puVar21 + (long)ppppppbVar15 + 5) = 0x6573617268507373;
            puVar21[(long)ppppppbVar15 + 0xd] = 0;
            bVar24 = (*(byte *)param_3 & 1) != 0;
            ppppppbVar15 = (byte ******)((long)param_3 + 1);
            if (bVar24) {
              ppppppbVar15 = param_3[2];
            }
            ppppppbVar11 = (byte ******)(ulong)(*(byte *)param_3 >> 1);
            if (bVar24) {
              ppppppbVar11 = param_3[1];
            }
            *(undefined **)((long)puVar12 + -0xa0) = &UNK_02b8140b;
            puVar10 = (undefined8 *)
                      func_0x04aac7b0((undefined1 *)((long)puVar12 + -0x90),ppppppbVar15,
                                      ppppppbVar11);
            *(undefined8 *)((long)puVar12 + -0x68) = puVar10[2];
            uVar6 = puVar10[1];
            *(undefined8 *)((long)puVar12 + -0x78) = *puVar10;
            *(undefined8 *)((long)puVar12 + -0x70) = uVar6;
            *puVar10 = 0;
            puVar10[1] = 0;
            puVar10[2] = 0;
            plVar3 = *(long **)((long)puVar12 + -0x50);
            pcVar4 = *(code **)(*plVar3 + 0x18);
            *(undefined **)((long)puVar12 + -0xa0) = &UNK_02b81442;
            (*pcVar4)((undefined1 *)((long)puVar12 + -0x48),plVar3,
                      (undefined1 *)((long)puVar12 + -0x78));
            if ((*(byte *)((long)puVar12 + -0x78) & 1) != 0) {
              *(undefined **)((long)puVar12 + -0xa0) = &UNK_02b8145c;
              func_0x04a8cfc8(*(undefined8 *)((long)puVar12 + -0x68),
                              *(ulong *)((long)puVar12 + -0x78) & 0xfffffffffffffffe);
            }
            if ((*(byte *)((long)puVar12 + -0x90) & 1) != 0) {
              *(undefined **)((long)puVar12 + -0xa0) = &UNK_02b81476;
              func_0x04a8cfc8(*(undefined8 *)((long)puVar12 + -0x80),
                              *(ulong *)((long)puVar12 + -0x90) & 0xfffffffffffffffe);
            }
            pcVar4 = *(code **)(*plVar3 + 0x20);
            *(undefined **)((long)puVar12 + -0xa0) = &UNK_02b81490;
            (*pcVar4)(pppppppbVar16,plVar3,param_3,(undefined1 *)((long)puVar12 + -0x48),10000);
            if ((*(byte *)((long)puVar12 + -0x48) & 1) == 0) {
              return pppppppbVar16;
            }
            *(undefined **)((long)puVar12 + -0xa0) = &UNK_02b814aa;
            func_0x04a8cfc8(*(undefined8 *)((long)puVar12 + -0x38),
                            *(ulong *)((long)puVar12 + -0x48) & 0xfffffffffffffffe);
            return pppppppbVar16;
          }
          *(undefined **)((long)puVar12 + -0xa0) = &UNK_02b814c1;
          param_1 = (byte *******)func_0x019f8660();
          *(undefined **)((long)puVar12 + -0xa0) = &UNK_02b814c9;
          func_0x019f83c0();
          *(ulong *)((long)puVar12 + -0xa0) = (ulong)bVar2;
          *(byte ********)((long)puVar12 + -0xa8) = param_3;
          *(ulong *)((long)puVar12 + -0xb0) = uVar1;
          *(byte ********)((long)puVar12 + -0xb8) = pppppppbVar19;
          *(byte *******)((long)puVar12 + -0xc0) = ppppppbVar15;
          *(byte ********)((long)puVar12 + -200) = pppppppbVar16;
          if ((*(byte *)extraout_RDX & 1) == 0) {
            if (*(byte *)extraout_RDX >> 1 == 0) goto code_r0x02b815cd;
          }
          else if (extraout_RDX[1] == (byte ******)0x0) goto code_r0x02b815cd;
          if ((*(byte *)param_5 & 1) == 0) {
            if (*(byte *)param_5 >> 1 == 0) goto code_r0x02b815cd;
          }
          else if (param_5[1] == (byte ******)0x0) {
code_r0x02b815cd:
            *param_1 = (byte ******)0x0;
            param_1[1] = (byte ******)0x0;
            param_1[2] = (byte ******)0x0;
            return param_1;
          }
          *(undefined **)((long)puVar12 + -0x138) = &UNK_02b81537;
          FUN_02b7f8e0((undefined1 *)((long)puVar12 + -0x108),(int)DAT_04ca79a0);
          pppppppbVar7 = *(byte ********)((long)puVar12 + -0x108);
          pppppbVar5 = (*pppppppbVar7)[6];
          *(undefined **)((long)puVar12 + -0x138) = &UNK_02b81550;
          (*(code *)pppppbVar5)((undefined1 *)((long)puVar12 + -0xf8),pppppppbVar7,0x20);
          *(int *)((long)puVar12 + -0x130) = (int)DAT_04ca79a0;
          pppppppbVar14 = (byte *******)((long)puVar12 + -0xe0);
          puVar13 = (undefined8 *)((long)puVar12 + -0x138);
          puVar12 = (undefined8 *)((long)puVar12 + -0x138);
          *puVar13 = &UNK_02b81576;
          param_3 = extraout_RDX;
          pppppppbVar16 = pppppppbVar14;
          pppppppbVar18 = extraout_RDX;
          pppppppbVar19 = pppppppbVar7;
          pppppppbVar22 = param_5;
          pppppppbVar23 = param_4;
        } while( true );
      }
      if ((*(byte *)param_4 & 1) == 0) {
        if (*(byte *)param_4 >> 1 != 0) goto LAB_02b81076;
      }
      else if (param_4[1] != (byte ******)0x0) goto LAB_02b81076;
      pppppppbVar7 = (byte *******)0x4;
    }
  }
  return pppppppbVar7;
}


// FUNCTION 02b812b0 bounds 02b814cf

ulong * FUN_02b812b0(ulong *param_1,undefined8 param_2,byte *param_3,ulong *param_4,byte *param_5,
                    long *param_6,char param_7)

{
  ulong uVar1;
  byte bVar2;
  char cVar3;
  ulong uVar4;
  long lVar5;
  ulong *puVar6;
  char *pcVar7;
  undefined8 uVar8;
  byte *pbVar9;
  ulong uVar10;
  byte *extraout_RDX;
  undefined7 *puVar11;
  ulong uVar12;
  long *plVar13;
  undefined7 *puVar14;
  byte *pbVar15;
  long lVar16;
  char *pcVar17;
  bool bVar18;
  byte bStack_120;
  undefined7 uStack_11f;
  ulong uStack_118;
  undefined7 *puStack_110;
  long *plStack_108;
  undefined7 *puStack_100;
  byte bStack_f8;
  undefined7 uStack_f7;
  undefined8 uStack_e8;
  byte bStack_e0;
  undefined7 uStack_df;
  undefined8 uStack_d0;
  ulong *puStack_c8;
  ulong uStack_c0;
  undefined8 uStack_90;
  ulong uStack_88;
  long lStack_80;
  ulong uStack_78;
  ulong uStack_70;
  ulong uStack_68;
  byte *pbStack_60;
  byte *pbStack_58;
  long *plStack_50;
  byte bStack_48;
  undefined7 uStack_47;
  undefined8 uStack_38;

  if (param_7 == '\x02') {
    if ((*param_4 & 1) == 0) {
      param_1[2] = param_4[2];
      uVar10 = param_4[1];
      *param_1 = *param_4;
      param_1[1] = uVar10;
    }
    else {
      func_0x019f86e0(param_1,param_4[2],param_4[1]);
    }
    return param_1;
  }
  if (param_7 != '\x01') {
    *param_1 = 0;
    param_1[1] = 0;
    param_1[2] = 0;
    return param_1;
  }
  bVar2 = *param_5;
  if ((bVar2 & 1) == 0) {
    uVar10 = (ulong)(bVar2 >> 1);
  }
  else {
    uVar10 = *(ulong *)(param_5 + 8);
  }
  uVar1 = uVar10 + 0xd;
  if (uVar1 < 0xfffffffffffffff7) {
    plStack_50 = param_6;
    if (uVar1 < 0x17) {
      bVar18 = false;
      uStack_90 = CONCAT71(uStack_90._1_7_,(char)uVar1 * '\x02');
      lVar5 = 1;
      pbVar9 = *(byte **)(param_5 + 0x10);
    }
    else {
      uVar4 = (uVar1 & 0xfffffffffffffff8) + 8;
      uVar12 = 0x1a;
      if (uVar4 != 0x18) {
        uVar12 = uVar4;
      }
      pbStack_60 = *(byte **)(param_5 + 0x10);
      pbStack_58 = param_5;
      lVar5 = func_0x04b078f0(uVar12);
      uStack_90 = uVar12 | 1;
      bVar18 = true;
      pbVar9 = pbStack_60;
      param_5 = pbStack_58;
      uStack_88 = uVar1;
      lStack_80 = lVar5;
    }
    pbVar15 = param_5 + 1;
    if ((bVar2 & 1) != 0) {
      pbVar15 = pbVar9;
    }
    lVar16 = (long)&uStack_90 + 1;
    if (bVar18) {
      lVar16 = lVar5;
    }
    func_0x04b07a30(lVar16,pbVar15,uVar10);
    *(undefined8 *)(lVar16 + uVar10) = 0x5073736150337374;
    *(undefined8 *)(lVar16 + 5 + uVar10) = 0x6573617268507373;
    *(undefined1 *)(lVar16 + 0xd + uVar10) = 0;
    bVar18 = (*param_3 & 1) != 0;
    pbVar9 = param_3 + 1;
    if (bVar18) {
      pbVar9 = *(byte **)(param_3 + 0x10);
    }
    uVar10 = (ulong)(*param_3 >> 1);
    if (bVar18) {
      uVar10 = *(ulong *)(param_3 + 8);
    }
    puVar6 = (ulong *)func_0x04aac7b0(&uStack_90,pbVar9,uVar10);
    plVar13 = plStack_50;
    uStack_68 = puVar6[2];
    uStack_78 = *puVar6;
    uStack_70 = puVar6[1];
    *puVar6 = 0;
    puVar6[1] = 0;
    puVar6[2] = 0;
    (**(code **)(*plStack_50 + 0x18))(&bStack_48,plStack_50,&uStack_78);
    if ((uStack_78 & 1) != 0) {
      func_0x04a8cfc8(uStack_68,uStack_78 & 0xfffffffffffffffe);
    }
    if ((uStack_90 & 1) != 0) {
      func_0x04a8cfc8(lStack_80,uStack_90 & 0xfffffffffffffffe);
    }
    (**(code **)(*plVar13 + 0x20))(param_1,plVar13,param_3,&bStack_48,10000);
    if ((bStack_48 & 1) == 0) {
      return param_1;
    }
    func_0x04a8cfc8(uStack_38,CONCAT71(uStack_47,bStack_48) & 0xfffffffffffffffe);
    return param_1;
  }
  puVar6 = (ulong *)func_0x019f8660();
  func_0x019f83c0();
  if ((*extraout_RDX & 1) == 0) {
    if (*extraout_RDX >> 1 == 0) goto code_r0x02b815cd;
  }
  else if (*(long *)(extraout_RDX + 8) == 0) goto code_r0x02b815cd;
  if ((*param_5 & 1) == 0) {
    if (*param_5 >> 1 == 0) goto code_r0x02b815cd;
  }
  else if (*(long *)(param_5 + 8) == 0) {
code_r0x02b815cd:
    *puVar6 = 0;
    puVar6[1] = 0;
    puVar6[2] = 0;
    return puVar6;
  }
  puStack_c8 = param_1;
  uStack_c0 = uVar10;
  FUN_02b7f8e0(&plStack_108,(int)DAT_04ca79a0);
  (**(code **)(*plStack_108 + 0x30))(&bStack_f8,plStack_108,0x20);
  FUN_02b812b0(&bStack_e0);
  plVar13 = plStack_108;
  (**(code **)(*plStack_108 + 0x38))(&bStack_120,plStack_108,&bStack_f8,&bStack_e0);
  if ((bStack_120 & 1) == 0) {
    if (bStack_120 >> 1 == 0) goto code_r0x02b815e9;
  }
  else if (uStack_118 == 0) {
code_r0x02b815e9:
    *puVar6 = 0;
    puVar6[1] = 0;
    puVar6[2] = 0;
    goto joined_r0x02b816ac;
  }
  cVar3 = (**(code **)(*plStack_108 + 0x10))(plStack_108);
  bVar2 = bStack_120;
  uVar10 = uStack_118;
  if ((bStack_120 & 1) == 0) {
    uVar10 = (ulong)(bStack_120 >> 1);
  }
  uVar1 = uVar10 + 1;
  if (0xfffffffffffffff6 < uVar1) {
    uVar8 = func_0x019f8660();
    func_0x019f83c0(uVar8);
                    /* WARNING: Could not recover jumptable at 0x02b81737. Too many branches */
                    /* WARNING: Treating indirect jump as call */
    puVar6 = (ulong *)(**(code **)(*(long *)plVar13[1] + 0x30))();
    return puVar6;
  }
  if (uVar1 < 0x17) {
    bVar18 = false;
    *(char *)puVar6 = (char)uVar1 * '\x02';
    pcVar7 = (char *)puVar6[2];
    puVar11 = puStack_110;
  }
  else {
    puStack_100 = puStack_110;
    uVar4 = (uVar1 & 0xfffffffffffffff8) + 8;
    uVar12 = 0x1a;
    if (uVar4 != 0x18) {
      uVar12 = uVar4;
    }
    pcVar7 = (char *)func_0x04b078f0(uVar12);
    *puVar6 = uVar12 | 1;
    puVar6[1] = uVar1;
    puVar6[2] = (ulong)pcVar7;
    bVar18 = true;
    puVar11 = puStack_100;
  }
  puVar14 = &uStack_11f;
  if ((bVar2 & 1) != 0) {
    puVar14 = puVar11;
  }
  pcVar17 = (char *)((long)puVar6 + 1);
  if (bVar18) {
    pcVar17 = pcVar7;
  }
  *pcVar17 = cVar3;
  func_0x04b07a30(pcVar17 + 1,puVar14,uVar10);
  pcVar17[uVar10 + 1] = '\0';
joined_r0x02b816ac:
  if ((bStack_120 & 1) != 0) {
    func_0x04a8cfc8(puStack_110,CONCAT71(uStack_11f,bStack_120) & 0xfffffffffffffffe);
  }
  if ((bStack_e0 & 1) != 0) {
    func_0x04a8cfc8(uStack_d0,CONCAT71(uStack_df,bStack_e0) & 0xfffffffffffffffe);
  }
  if ((bStack_f8 & 1) != 0) {
    func_0x04a8cfc8(uStack_e8,CONCAT71(uStack_f7,bStack_f8) & 0xfffffffffffffffe);
  }
  (**(code **)(*plStack_108 + 8))(plStack_108);
  return puVar6;
}

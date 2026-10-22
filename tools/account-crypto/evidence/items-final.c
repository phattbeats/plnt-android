// FUNCTION 02b7af30 bounds 02b7b69f

/* WARNING: Removing unreachable block (ram,0x02b7bb1f) */

ulong * FUN_02b7af30(long param_1,long *param_2,ulong *param_3,long param_4,long *param_5,
                    ulong *param_6)

{
  uint uVar1;
  long *plVar2;
  ulong *puVar3;
  undefined8 *puVar4;
  code *pcVar5;
  undefined4 uVar6;
  undefined4 uVar7;
  undefined4 uVar8;
  undefined4 uVar9;
  undefined4 uVar10;
  undefined4 uVar11;
  ulong *puVar12;
  byte bVar13;
  ulong *puVar14;
  char cVar15;
  undefined4 uVar16;
  uint uVar17;
  int iVar18;
  byte *pbVar19;
  long lVar20;
  long *plVar21;
  long lVar22;
  undefined8 uVar23;
  ulong *puVar24;
  undefined8 uVar25;
  undefined8 *puVar26;
  long *plVar27;
  ulong *puVar28;
  undefined8 *extraout_RDX;
  long *unaff_RBP;
  undefined8 *puVar29;
  ulong uVar30;
  long *unaff_R12;
  long *plVar31;
  long *plVar32;
  ulong *puVar33;
  ulong uVar34;
  ulong uStack_1a0;
  long *plStack_198;
  undefined8 uStack_190;
  long *plStack_188;
  ulong *puStack_180;
  ulong **ppuStack_178;
  ulong *puStack_170;
  ulong uStack_168;
  undefined4 uStack_15c;
  ulong *puStack_158;
  undefined *puStack_150;
  undefined8 uStack_148;
  undefined8 uStack_140;
  long *plStack_138;
  undefined *puStack_130;
  undefined8 uStack_128;
  undefined8 uStack_120;
  undefined8 uStack_118;
  long *plStack_110;
  long *plStack_108;
  long *plStack_100;
  long lStack_f8;
  long *plStack_f0;
  undefined8 uStack_e8;
  undefined8 uStack_e0;
  ulong uStack_d8;
  ulong *puStack_d0;
  undefined1 auStack_c8 [8];
  byte *pbStack_c0;
  ulong *puStack_b8;
  long lStack_b0;
  byte bStack_a8;
  undefined1 uStack_a7;
  undefined2 uStack_a6;
  undefined4 uStack_a4;
  undefined4 uStack_a0;
  undefined4 uStack_9c;
  ulong uStack_98;
  ulong uStack_90;
  ulong *puStack_88;
  ulong *puStack_80;
  long lStack_78;
  long lStack_70;
  ulong uStack_68;
  long lStack_60;
  long *plStack_58;
  ulong *puStack_50;
  long *plStack_48;
  long lStack_40;
  long lStack_38;

  lVar22 = *param_2;
  uVar16 = *(undefined4 *)((long)param_2 + 4);
  lVar20 = param_2[1];
  uVar6 = *(undefined4 *)((long)param_2 + 0xc);
  if (param_2[1] != 0) {
    LOCK();
    plVar32 = (long *)(param_2[1] + 8);
    *plVar32 = *plVar32 + 1;
    UNLOCK();
  }
  plVar32 = *(long **)(param_1 + 0x40);
  *(int *)(param_1 + 0x38) = (int)lVar22;
  *(undefined4 *)(param_1 + 0x3c) = uVar16;
  *(int *)(param_1 + 0x40) = (int)lVar20;
  *(undefined4 *)(param_1 + 0x44) = uVar6;
  puStack_d0 = param_3;
  lStack_b0 = param_1;
  puStack_80 = param_6;
  lStack_60 = param_4;
  if (plVar32 != (long *)0x0) {
    LOCK();
    plVar31 = plVar32 + 1;
    lVar22 = *plVar31;
    *plVar31 = *plVar31 + -1;
    UNLOCK();
    if (lVar22 == 0) {
      plStack_f0 = (long *)0x2b7af99;
      (**(code **)(*plVar32 + 0x10))(plVar32);
      plStack_f0 = (long *)0x2b7afa1;
      func_0x04aa8780(plVar32);
    }
  }
  lVar22 = *param_2;
  plStack_48 = (long *)(*(ulong *)(lVar22 + 0x18) + 7);
  if ((*(ulong *)(lVar22 + 0x18) & 1) == 0) {
    plStack_48 = (long *)(lVar22 + 0x18);
  }
  if ((long)*(int *)(lVar22 + 0x20) != 0) {
    plStack_58 = plStack_48 + *(int *)(lVar22 + 0x20);
    puStack_50 = (ulong *)(lStack_b0 + 0x18);
    do {
      lVar22 = *plStack_48;
      uVar1 = *(uint *)(lVar22 + 0x38);
      plVar31 = (long *)(ulong)uVar1;
      if (*(char *)(lVar22 + 0x3c) == '\x01') {
        plStack_f0 = (long *)0x2b7b027;
        uVar16 = func_0x02b1b850(plVar31);
        bStack_a8 = (byte)uVar16;
        uStack_a7 = (undefined1)((uint)uVar16 >> 8);
        uStack_a6 = (undefined2)((uint)uVar16 >> 0x10);
        uStack_e8 = (long *)puStack_80[1];
        pbStack_c0 = &bStack_a8;
        puStack_b8 = puStack_80;
        if (uStack_e8 < (long *)puStack_80[2]) {
          *(undefined4 *)uStack_e8 = uVar16;
          plVar27 = (long *)((long)uStack_e8 + 4);
          auStack_c8 = (undefined1  [8])&uStack_e8;
        }
        else {
          plStack_f0 = (long *)0x2b7b468;
          auStack_c8 = (undefined1  [8])&uStack_e8;
          func_0x02b7e360(auStack_c8);
          plVar27 = uStack_e8;
        }
        puStack_80[1] = (ulong)plVar27;
      }
      plStack_f0 = (long *)0x2b7b070;
      uVar16 = func_0x02b1b850(plVar31);
      bStack_a8 = (byte)uVar16;
      uStack_a7 = (undefined1)((uint)uVar16 >> 8);
      uStack_a6 = (undefined2)((uint)uVar16 >> 0x10);
      uStack_e8 = *(long **)(lStack_b0 + 0x20);
      pbStack_c0 = &bStack_a8;
      puStack_b8 = puStack_50;
      if (uStack_e8 < *(long **)(lStack_b0 + 0x28)) {
        *(undefined4 *)uStack_e8 = uVar16;
        plVar27 = (long *)((long)uStack_e8 + 4);
        auStack_c8 = (undefined1  [8])&uStack_e8;
      }
      else {
        plStack_f0 = (long *)0x2b7b455;
        auStack_c8 = (undefined1  [8])&uStack_e8;
        func_0x02b7e360(auStack_c8);
        plVar27 = uStack_e8;
      }
      *(long **)(lStack_b0 + 0x20) = plVar27;
      uVar30 = *(ulong *)(lVar22 + 0x18);
      puVar28 = (ulong *)(uVar30 & 0xffffffff);
      lStack_40 = uVar30 + 7;
      if ((uVar30 & 1) == 0) {
        lStack_40 = lVar22 + 0x18;
      }
      lStack_78 = (long)*(int *)(lVar22 + 0x20);
      if (lStack_78 != 0) {
        lStack_78 = lStack_78 << 3;
        lVar22 = 0;
        do {
          pbVar19 = (byte *)(*(ulong *)(*(long *)(lStack_40 + lVar22) + 0x28) & 0xfffffffffffffffc);
          if ((*pbVar19 & 1) == 0) {
            puVar28 = *(ulong **)(pbVar19 + 0x10);
            auStack_c8 = *(undefined1 (*) [8])pbVar19;
            pbStack_c0 = *(byte **)(pbVar19 + 8);
            puStack_b8 = puVar28;
          }
          else {
            plStack_f0 = (long *)0x2b7b13a;
            func_0x019f86e0(auStack_c8,*(undefined8 *)(pbVar19 + 0x10),*(undefined8 *)(pbVar19 + 8))
            ;
          }
          plStack_f0 = (long *)0x2b7b15b;
          lVar20 = func_0x04b078f0(0x60);
          plStack_f0 = (long *)0x2b7b168;
          func_0x047cfdf0(lVar20,0);
          lStack_70 = lVar20;
          if (uVar1 != 4) {
            uStack_e8 = (long *)0x0;
            uStack_e0 = (long *)0x0;
            uStack_d8 = 0;
            uVar30 = *(ulong *)(lStack_b0 + 8);
            plStack_f0 = (long *)0x2b7b1d8;
            func_0x02b803f0(&bStack_a8,uVar30,auStack_c8);
            if (((ulong)uStack_e8 & 1) != 0) {
              uVar30 = (ulong)uStack_e8 & 0xfffffffffffffffe;
              plStack_f0 = (long *)0x2b7b1f0;
              func_0x04a8cfc8(uStack_d8);
            }
            bVar13 = bStack_a8;
            uStack_e8 = (long *)CONCAT44(uStack_a4,CONCAT22(uStack_a6,CONCAT11(uStack_a7,bStack_a8))
                                        );
            uStack_e0 = (long *)CONCAT44(uStack_9c,uStack_a0);
            uStack_d8 = uStack_98;
            bStack_a8 = 0;
            uStack_a7 = 0;
            if ((bVar13 & 1) == 0) {
              if (bVar13 >> 1 != 0) goto LAB_02b7b23d;
            }
            else if (uStack_e0 != (long *)0x0) {
LAB_02b7b23d:
              uVar30 = (long)&uStack_e8 + 1;
              if ((bVar13 & 1) != 0) {
                uVar30 = uStack_98;
              }
              plStack_f0 = (long *)0x2b7b253;
              uVar17 = func_0x044027a0(lVar20);
              unaff_RBP = (long *)(ulong)uVar17;
              if ((bStack_a8 & 1) != 0) {
                uVar30 = CONCAT44(uStack_a4,CONCAT22(uStack_a6,CONCAT11(uStack_a7,bStack_a8))) &
                         0xfffffffffffffffe;
                plStack_f0 = (long *)0x2b7b26f;
                func_0x04a8cfc8(uStack_98);
              }
              if ((char)uVar17 != '\0') {
                if (((ulong)uStack_e8 & 1) != 0) {
                  plStack_f0 = (long *)0x2b7b290;
                  func_0x04a8cfc8(uStack_d8,(ulong)uStack_e8 & 0xfffffffffffffffe);
                }
                goto LAB_02b7b290;
              }
            }
            bStack_a8 = 4;
            uStack_a7 = 0;
            uStack_a6 = 0;
            plVar27 = *(long **)(lStack_60 + 0x20);
            if (plVar27 != (long *)0x0) {
              plStack_f0 = (long *)0x2b7b499;
              (**(code **)(*plVar27 + 0x30))(plVar27,&bStack_a8);
              uVar30 = *puStack_d0;
              for (uVar34 = puStack_d0[1]; uVar30 != uVar34; uVar34 = uVar34 - 0x10) {
                plVar32 = *(long **)(uVar34 - 8);
                if (plVar32 != (long *)0x0) {
                  LOCK();
                  plVar31 = plVar32 + 1;
                  lVar22 = *plVar31;
                  *plVar31 = *plVar31 + -1;
                  UNLOCK();
                  if (lVar22 == 0) {
                    plStack_f0 = (long *)0x2b7b54d;
                    (**(code **)(*plVar32 + 0x10))(plVar32);
                    plStack_f0 = (long *)0x2b7b555;
                    func_0x04aa8780(plVar32);
                  }
                }
              }
              puStack_d0[1] = uVar30;
              lVar22 = lStack_70;
              if (((ulong)uStack_e8 & 1) != 0) {
                plStack_f0 = (long *)0x2b7b4cb;
                func_0x04a8cfc8(uStack_d8,(ulong)uStack_e8 & 0xfffffffffffffffe);
                lVar22 = lStack_70;
              }
joined_r0x02b7b4d3:
              lStack_70 = lVar22;
              if (lVar22 != 0) {
                plStack_f0 = (long *)0x2b7b4dd;
                func_0x047cff30(lVar22);
                plStack_f0 = (long *)0x2b7b4ea;
                func_0x04a8cfc8(lVar22,0x60);
              }
              if (((ulong)auStack_c8 & 1) != 0) {
                plStack_f0 = (long *)0x2b7b504;
                func_0x04a8cfc8(puStack_b8,(ulong)auStack_c8 & 0xfffffffffffffffe);
              }
              return (ulong *)0x0;
            }
            plStack_f0 = (long *)0x2b7b5d3;
            func_0x01a8f9c0();
            plVar27 = plVar31;
LAB_02b7b5d3:
            plStack_f0 = (long *)0x2b7b5d8;
            func_0x01a06330();
LAB_02b7b5d8:
            plStack_f0 = (long *)0x2b7b5dd;
            uVar23 = func_0x02b6dca0();
            plStack_f0 = (long *)0x2b7b5ea;
            func_0x02b6d660(&uStack_e8);
            plStack_f0 = (long *)0x2b7b65a;
            func_0x02b6d760(&lStack_70);
            if (((ulong)auStack_c8 & 1) != 0) {
              uVar30 = (ulong)auStack_c8 & 0xfffffffffffffffe;
              plStack_f0 = (long *)0x2b7b68b;
              func_0x04a8cfc8(puStack_b8);
            }
            plStack_f0 = (long *)0x2b7b693;
            func_0x04b07a10(uVar23);
            puVar24 = (ulong *)&UNK_04b77590;
            puStack_150 = &UNK_04b77590;
            uStack_148 = 0;
            uStack_140 = 0;
            puStack_130 = &UNK_04b77590;
            uStack_128 = 0;
            uStack_120 = 0;
            puVar33 = (ulong *)*extraout_RDX;
            puVar3 = (ulong *)extraout_RDX[1];
            lStack_f8 = lVar20;
            uStack_118 = uVar23;
            puVar14 = puVar28;
            plStack_f0 = unaff_RBP;
            plStack_138 = param_5;
            plStack_110 = unaff_R12;
            plStack_108 = plVar27;
            plStack_100 = plVar32;
            puVar12 = puStack_158;
            goto joined_r0x02b7b6e8;
          }
          pbVar19 = (byte *)((ulong)auStack_c8 >> 1 & 0x7f);
          puVar24 = (ulong *)(auStack_c8 + 1);
          if (((ulong)auStack_c8 & 1) != 0) {
            pbVar19 = pbStack_c0;
            puVar24 = puStack_b8;
          }
          plStack_f0 = (long *)0x2b7b19a;
          cVar15 = func_0x044027a0(lVar20,puVar24,pbVar19);
          if (cVar15 == '\0') {
            uVar30 = *puStack_d0;
            for (uVar34 = puStack_d0[1]; uVar30 != uVar34; uVar34 = uVar34 - 0x10) {
              plVar32 = *(long **)(uVar34 - 8);
              if (plVar32 != (long *)0x0) {
                LOCK();
                plVar31 = plVar32 + 1;
                lVar22 = *plVar31;
                *plVar31 = *plVar31 + -1;
                UNLOCK();
                if (lVar22 == 0) {
                  plStack_f0 = (long *)0x2b7b5bd;
                  (**(code **)(*plVar32 + 0x10))(plVar32);
                  plStack_f0 = (long *)0x2b7b5c5;
                  func_0x04aa8780(plVar32);
                }
              }
            }
            puStack_d0[1] = uVar30;
            lVar22 = lStack_70;
            goto joined_r0x02b7b4d3;
          }
LAB_02b7b290:
          plStack_f0 = (long *)0x2b7b2a2;
          lStack_38 = lVar22;
          plVar21 = (long *)func_0x04b078f0(0x20);
          plVar21[1] = 0;
          plVar21[2] = 0;
          *plVar21 = (long)&UNK_04b777d8;
          plStack_f0 = (long *)0x2b7b2c1;
          lVar22 = func_0x04b078f0(0x60);
          plStack_f0 = (long *)0x2b7b2d1;
          func_0x047cfdf0(lVar22,0);
          plVar27 = plVar21 + 3;
          *plVar27 = lVar22;
          plStack_f0 = (long *)0x2b7b2e7;
          func_0x047d0ae0(lVar22,lVar20);
          plVar2 = (long *)puStack_d0[1];
          plVar32 = (long *)puStack_d0[2];
          uStack_e8 = plVar27;
          uStack_e0 = plVar21;
          if (plVar2 < plVar32) {
            *plVar2 = (long)plVar27;
            plVar2[1] = (long)plVar21;
            unaff_RBP = plVar2 + 2;
            unaff_R12 = unaff_RBP;
          }
          else {
            uVar34 = *puStack_d0;
            unaff_RBP = (long *)((long)plVar2 - uVar34);
            uVar30 = (long)unaff_RBP >> 4;
            puVar24 = (ulong *)(uVar30 + 1);
            puVar28 = (ulong *)((ulong)puVar24 >> 0x3c);
            unaff_R12 = plVar21;
            if (puVar28 != (ulong *)0x0) goto LAB_02b7b5d8;
            plVar32 = (long *)((long)plVar32 - uVar34);
            puVar28 = (ulong *)((long)plVar32 >> 3);
            if ((ulong *)((long)plVar32 >> 3) <= puVar24) {
              puVar28 = puVar24;
            }
            if ((long *)0x7fffffffffffffef < plVar32) {
              puVar28 = (ulong *)0xfffffffffffffff;
            }
            uStack_90 = uVar30;
            uStack_68 = uVar34;
            if ((ulong *)0xfffffffffffffff < puVar28) goto LAB_02b7b5d3;
            puStack_88 = (ulong *)((long)puVar28 << 4);
            plStack_f0 = (long *)0x2b7b3d3;
            lVar22 = func_0x04b078f0(puStack_88);
            uVar30 = uStack_68;
            puStack_88 = (ulong *)((long)puStack_88 + lVar22);
            *(long **)(lVar22 + (long)unaff_RBP) = plVar27;
            *(long **)(lVar22 + 8 + (long)unaff_RBP) = plVar21;
            uStack_e8 = (long *)0x0;
            uStack_e0 = (long *)0x0;
            unaff_R12 = (long *)((long)unaff_RBP + lVar22 + 0x10);
            uStack_90 = (long)unaff_RBP + uStack_90 * -0x10 + lVar22;
            plStack_f0 = (long *)0x2b7b418;
            func_0x04b07ad0(uStack_90,uStack_68,unaff_RBP);
            *puStack_d0 = uStack_90;
            puStack_d0[1] = (ulong)unaff_R12;
            puStack_d0[2] = (ulong)puStack_88;
            puVar28 = puStack_88;
            if (uVar30 != 0) {
              plStack_f0 = (long *)0x2b7b446;
              func_0x04a8cfc8(uVar30,plVar32);
            }
          }
          puStack_d0[1] = (ulong)unaff_R12;
          plStack_f0 = (long *)0x2b7b325;
          func_0x047cff30(lVar20);
          plStack_f0 = (long *)0x2b7b332;
          func_0x04a8cfc8(lVar20);
          lVar22 = lStack_38;
          if (((ulong)auStack_c8 & 1) != 0) {
            plStack_f0 = (long *)0x2b7b358;
            func_0x04a8cfc8(puStack_b8);
          }
          lVar22 = lVar22 + 8;
        } while (lStack_78 != lVar22);
      }
      plStack_48 = plStack_48 + 1;
    } while (plStack_48 != plStack_58);
  }
  return (ulong *)0x1;
joined_r0x02b7b6e8:
  puStack_158 = puVar14;
  if (puVar33 == puVar3) {
    puVar29 = (undefined8 *)*puVar28;
    puVar4 = (undefined8 *)puVar28[1];
    if (puVar29 != puVar4) {
      puStack_158 = puVar12;
      do {
        uStack_1a0 = 0;
        plStack_198 = (long *)0x0;
        uVar16 = *(undefined4 *)puVar29;
        uVar6 = *(undefined4 *)((long)puVar29 + 4);
        uStack_190 = *puVar29;
        uVar7 = *(undefined4 *)(puVar29 + 1);
        uVar8 = *(undefined4 *)((long)puVar29 + 0xc);
        plStack_188 = (long *)puVar29[1];
        if (puVar29[1] != 0) {
          LOCK();
          plVar32 = (long *)(puVar29[1] + 8);
          *plVar32 = *plVar32 + 1;
          UNLOCK();
        }
        puVar26 = *(undefined8 **)(uVar30 + 8);
        if (puVar26 < *(undefined8 **)(uVar30 + 0x10)) {
          *puVar26 = 0;
          puVar26[1] = 0;
          *(undefined4 *)(puVar26 + 2) = uVar16;
          *(undefined4 *)((long)puVar26 + 0x14) = uVar6;
          *(undefined4 *)(puVar26 + 3) = uVar7;
          *(undefined4 *)((long)puVar26 + 0x1c) = uVar8;
          if (plStack_188 != (long *)0x0) {
            LOCK();
            plStack_188[1] = plStack_188[1] + 1;
            UNLOCK();
          }
          puVar26 = puVar26 + 4;
        }
        else {
          puVar26 = (undefined8 *)func_0x02b7e5a0(uVar30,&uStack_1a0);
        }
        *(undefined8 **)(uVar30 + 8) = puVar26;
        uVar23 = *puVar29;
        uVar25 = func_0x02b63910(&puStack_130);
        puVar24 = (ulong *)func_0x02b6f580(uVar23,uVar25);
        plVar31 = plStack_188;
        plVar32 = plStack_198;
        if (plStack_188 != (long *)0x0) {
          LOCK();
          plVar27 = plStack_188 + 1;
          puVar24 = (ulong *)*plVar27;
          *plVar27 = *plVar27 + -1;
          UNLOCK();
          if (puVar24 == (ulong *)0x0) {
            (**(code **)(*plStack_188 + 0x10))(plStack_188);
            puVar24 = (ulong *)func_0x04aa8780(plVar31);
            plVar32 = plStack_198;
          }
        }
        if (plVar32 != (long *)0x0) {
          LOCK();
          plVar31 = plVar32 + 1;
          puVar24 = (ulong *)*plVar31;
          *plVar31 = *plVar31 + -1;
          UNLOCK();
          if (puVar24 == (ulong *)0x0) {
            plStack_198 = plVar32;
            (**(code **)(*plVar32 + 0x10))(plVar32);
            puVar24 = (ulong *)func_0x04aa8780(plVar32);
          }
        }
        puVar29 = puVar29 + 2;
      } while (puVar29 != puVar4);
    }
    return puVar24;
  }
  uVar34 = *puVar33;
  uVar23 = func_0x02b63910(&puStack_150);
  puVar24 = (ulong *)func_0x02b6f580(uVar34,uVar23);
  if (((int)puVar24 == 0) &&
     (puVar24 = (ulong *)func_0x02b6f560(*puVar33,&uStack_15c), plVar32 = plStack_138,
     (int)puVar24 == 0)) {
    lVar22 = plStack_138[1];
    lVar20 = func_0x04b07cf0(*plStack_138,uStack_15c,lVar22 - *plStack_138 >> 2);
    if (lVar20 == 0) {
      lVar20 = lVar22;
    }
    if (lVar20 == plVar32[1]) {
      puVar4 = (undefined8 *)puVar28[1];
      for (puVar29 = (undefined8 *)*puVar28; puVar28 = puStack_158, puVar29 != puVar4;
          puVar29 = puVar29 + 2) {
        uVar23 = *puVar29;
        uVar25 = func_0x02b63910(&puStack_130);
        iVar18 = func_0x02b6f580(uVar23,uVar25);
        if (iVar18 == 0) {
          uVar23 = func_0x02b63910(&puStack_130);
          cVar15 = func_0x02b63a00(&puStack_150,uVar23);
          if (cVar15 != '\0') {
            uStack_1a0 = *puVar33;
            plStack_198 = (long *)puVar33[1];
            if (plStack_198 != (long *)0x0) {
              LOCK();
              plStack_198[1] = plStack_198[1] + 1;
              UNLOCK();
            }
            uVar16 = *(undefined4 *)puVar29;
            uVar6 = *(undefined4 *)((long)puVar29 + 4);
            uStack_190 = *puVar29;
            uVar7 = *(undefined4 *)(puVar29 + 1);
            uVar8 = *(undefined4 *)((long)puVar29 + 0xc);
            plStack_188 = (long *)puVar29[1];
            if (puVar29[1] != 0) {
              LOCK();
              plVar32 = (long *)(puVar29[1] + 8);
              *plVar32 = *plVar32 + 1;
              UNLOCK();
            }
            puStack_180 = *(ulong **)(uVar30 + 8);
            ppuStack_178 = &puStack_180;
            if (puStack_180 < *(ulong **)(uVar30 + 0x10)) {
              *puStack_180 = uStack_1a0;
              puStack_180[1] = (ulong)plStack_198;
              uStack_1a0 = 0;
              plStack_198 = (long *)0x0;
              *(undefined4 *)(puStack_180 + 2) = uVar16;
              *(undefined4 *)((long)puStack_180 + 0x14) = uVar6;
              *(undefined4 *)(puStack_180 + 3) = uVar7;
              *(undefined4 *)((long)puStack_180 + 0x1c) = uVar8;
              uStack_190 = 0;
              plStack_188 = (long *)0x0;
              *(ulong **)(uVar30 + 8) = puStack_180 + 4;
              puStack_170 = &uStack_1a0;
              uStack_168 = uVar30;
            }
            else {
              puStack_170 = &uStack_1a0;
              uStack_168 = uVar30;
              func_0x02b7e480(&ppuStack_178);
              plVar32 = plStack_188;
              *(ulong **)(uVar30 + 8) = puStack_180;
              if (plStack_188 != (long *)0x0) {
                LOCK();
                plVar31 = plStack_188 + 1;
                lVar22 = *plVar31;
                *plVar31 = *plVar31 + -1;
                UNLOCK();
                if (lVar22 == 0) {
                  (**(code **)(*plStack_188 + 0x10))(plStack_188);
                  func_0x04aa8780(plVar32);
                }
              }
            }
            plVar32 = plStack_198;
            if (plStack_198 != (long *)0x0) {
              LOCK();
              plVar31 = plStack_198 + 1;
              lVar22 = *plVar31;
              *plVar31 = *plVar31 + -1;
              UNLOCK();
              if (lVar22 == 0) {
                (**(code **)(*plStack_198 + 0x10))(plStack_198);
                func_0x04aa8780(plVar32);
              }
            }
            puVar28 = puStack_158;
            uVar34 = puStack_158[1];
            uVar16 = *(undefined4 *)(uVar34 - 0xc);
            uVar6 = *(undefined4 *)(uVar34 - 8);
            uVar7 = *(undefined4 *)(uVar34 - 4);
            plVar32 = (long *)puVar29[1];
            uVar8 = *(undefined4 *)puVar29;
            uVar9 = *(undefined4 *)((long)puVar29 + 4);
            uVar10 = *(undefined4 *)(puVar29 + 1);
            uVar11 = *(undefined4 *)((long)puVar29 + 0xc);
            *(undefined4 *)puVar29 = *(undefined4 *)(uVar34 - 0x10);
            *(undefined4 *)((long)puVar29 + 4) = uVar16;
            *(undefined4 *)(puVar29 + 1) = uVar6;
            *(undefined4 *)((long)puVar29 + 0xc) = uVar7;
            *(undefined4 *)(uVar34 - 0x10) = uVar8;
            *(undefined4 *)(uVar34 - 0xc) = uVar9;
            *(undefined4 *)(uVar34 - 8) = uVar10;
            *(undefined4 *)(uVar34 - 4) = uVar11;
            if (*puStack_158 == uVar34) {
                    /* WARNING: Does not return */
              pcVar5 = (code *)invalidInstructionException();
              (*pcVar5)();
            }
            puVar24 = puStack_158;
            if (plVar32 != (long *)0x0) {
              LOCK();
              plVar31 = plVar32 + 1;
              puVar24 = (ulong *)*plVar31;
              *plVar31 = *plVar31 + -1;
              UNLOCK();
              if (puVar24 == (ulong *)0x0) {
                (**(code **)(*plVar32 + 0x10))(plVar32);
                puVar24 = (ulong *)func_0x04aa8780(plVar32);
              }
            }
            puVar28[1] = uVar34 - 0x10;
            goto code_r0x02b7b710;
          }
        }
      }
      uStack_1a0 = *puVar33;
      plStack_198 = (long *)puVar33[1];
      if (plStack_198 != (long *)0x0) {
        LOCK();
        plStack_198[1] = plStack_198[1] + 1;
        UNLOCK();
      }
      uStack_190 = 0;
      plStack_188 = (long *)0x0;
      puStack_180 = *(ulong **)(uVar30 + 8);
      ppuStack_178 = &puStack_180;
      if (puStack_180 < *(ulong **)(uVar30 + 0x10)) goto code_r0x02b7b9b6;
      puStack_170 = &uStack_1a0;
      uStack_168 = uVar30;
      func_0x02b7e480(&ppuStack_178);
code_r0x02b7ba4b:
      plVar32 = plStack_188;
      *(ulong **)(uVar30 + 8) = puStack_180;
      puVar24 = puStack_180;
      if (plStack_188 != (long *)0x0) {
        LOCK();
        plVar31 = plStack_188 + 1;
        puVar24 = (ulong *)*plVar31;
        *plVar31 = *plVar31 + -1;
        UNLOCK();
        if (puVar24 == (ulong *)0x0) {
          (**(code **)(*plStack_188 + 0x10))(plStack_188);
          puVar24 = (ulong *)func_0x04aa8780(plVar32);
        }
      }
    }
    else {
      uStack_1a0 = uStack_1a0 & 0xffffffff00000000;
      func_0x02b6f890(*puVar33,&uStack_1a0);
      uStack_1a0 = *puVar33;
      plStack_198 = (long *)puVar33[1];
      if (plStack_198 != (long *)0x0) {
        LOCK();
        plStack_198[1] = plStack_198[1] + 1;
        UNLOCK();
      }
      uStack_190 = 0;
      plStack_188 = (long *)0x0;
      puStack_180 = *(ulong **)(uVar30 + 8);
      ppuStack_178 = &puStack_180;
      if (*(ulong **)(uVar30 + 0x10) <= puStack_180) {
        puStack_170 = &uStack_1a0;
        uStack_168 = uVar30;
        func_0x02b7e480(&ppuStack_178);
        goto code_r0x02b7ba4b;
      }
code_r0x02b7b9b6:
      puStack_170 = &uStack_1a0;
      ppuStack_178 = &puStack_180;
      *puStack_180 = uStack_1a0;
      puStack_180[1] = (ulong)plStack_198;
      uStack_1a0 = 0;
      plStack_198 = (long *)0x0;
      *(undefined4 *)(puStack_180 + 2) = 0;
      *(undefined4 *)((long)puStack_180 + 0x14) = 0;
      *(undefined4 *)(puStack_180 + 3) = 0;
      *(undefined4 *)((long)puStack_180 + 0x1c) = 0;
      uStack_190 = 0;
      plStack_188 = (long *)0x0;
      *(ulong **)(uVar30 + 8) = puStack_180 + 4;
      puVar24 = puStack_180 + 4;
      uStack_168 = uVar30;
    }
    plVar32 = plStack_198;
    if (plStack_198 != (long *)0x0) {
      LOCK();
      plVar31 = plStack_198 + 1;
      puVar24 = (ulong *)*plVar31;
      *plVar31 = *plVar31 + -1;
      UNLOCK();
      if (puVar24 == (ulong *)0x0) {
        (**(code **)(*plStack_198 + 0x10))(plStack_198);
        puVar24 = (ulong *)func_0x04aa8780(plVar32);
      }
    }
  }
code_r0x02b7b710:
  puVar33 = puVar33 + 2;
  puVar14 = puStack_158;
  puVar12 = puStack_158;
  goto joined_r0x02b7b6e8;
}


// FUNCTION 02b2e760 bounds 02b2e97f

ulong FUN_02b2e760(long param_1,undefined8 param_2,undefined8 param_3,ulong *param_4)

{
  char cVar1;
  int iVar2;
  long lVar3;
  ulong uVar4;
  long lStack_60;
  undefined8 uStack_58;
  undefined8 uStack_50;
  ulong uStack_48;
  long alStack_40 [2];

  func_0x04ab5980(param_1 + 0x20);
  func_0x02b59db0(alStack_40,*(undefined8 *)(param_1 + 0xd8));
  lVar3 = func_0x04b078f0(0x60);
  func_0x047cdb70(lVar3,0);
  lStack_60 = lVar3;
  iVar2 = func_0x02b6fba0(alStack_40,&lStack_60);
  if (iVar2 == 0) {
    uStack_58 = 0;
    uStack_50 = 0;
    uStack_48 = 0;
    iVar2 = func_0x02b85140(&lStack_60,&uStack_58);
    uVar4 = uStack_58 & 0xff;
    if (iVar2 == 0) {
      if ((uStack_58 & 1) == 0) {
        if ((byte)uStack_58 >> 1 != 0) goto LAB_02b2e898;
      }
      else if (uStack_50 != 0) {
LAB_02b2e898:
        cVar1 = func_0x02b75460(*(undefined8 *)(param_1 + 0x1a8),param_2,param_3);
        if (cVar1 != '\0') {
          if ((*param_4 & 1) != 0) {
            func_0x04a8cfc8(param_4[2],*param_4 & 0xfffffffffffffffe);
            *param_4 = 0;
            param_4[1] = 0;
            param_4[2] = 0;
          }
          param_4[2] = uStack_48;
          *(undefined4 *)param_4 = (undefined4)uStack_58;
          *(undefined4 *)((long)param_4 + 4) = uStack_58._4_4_;
          *(undefined4 *)(param_4 + 1) = (undefined4)uStack_50;
          *(undefined4 *)((long)param_4 + 0xc) = uStack_50._4_4_;
          uVar4 = CONCAT71((int7)((ulong)lVar3 >> 8),1);
          lVar3 = lStack_60;
          goto joined_r0x02b2e81e;
        }
        uVar4 = uStack_58 & 0xff;
      }
    }
    if ((uVar4 & 1) != 0) {
      func_0x04a8cfc8(uStack_48,uStack_58 & 0xfffffffffffffffe);
    }
  }
  uVar4 = 0;
  lVar3 = lStack_60;
joined_r0x02b2e81e:
  lStack_60 = 0;
  if (lVar3 != 0) {
    lStack_60 = 0;
    func_0x047cdce0(lVar3);
    func_0x04a8cfc8(lVar3,0x60);
  }
  lVar3 = alStack_40[0];
  alStack_40[0] = 0;
  if (lVar3 != 0) {
    func_0x047cff30(lVar3);
    func_0x04a8cfc8(lVar3,0x60);
  }
  func_0x04ab59b0(param_1 + 0x20);
  return uVar4 & 0xffffffff;
}

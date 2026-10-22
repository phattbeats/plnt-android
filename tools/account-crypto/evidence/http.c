// FUNCTION 047dd050 bounds 047dd6af

/* WARNING: Type propagation algorithm not settling */

void FUN_047dd050(long ******param_1,undefined8 param_2,long *param_3,byte *param_4,
                 undefined8 param_5,long *param_6)

{
  long lVar1;
  long *****ppppplVar2;
  long *plVar3;
  long *******ppppppplVar4;
  long *******ppppppplVar5;
  long lVar6;
  bool bVar7;
  long *******ppppppplStack_1c8;
  long *****ppppplStack_1c0;
  long lStack_1b8;
  long *******ppppppplStack_1b0;
  long *******ppppppplStack_1a8;
  long *******ppppppplStack_1a0;
  long lStack_198;
  uint uStack_190;
  undefined4 uStack_18c;
  undefined8 uStack_188;
  undefined8 uStack_180;
  undefined1 uStack_178;
  char cStack_170;
  byte bStack_168;
  undefined7 uStack_167;
  undefined8 uStack_160;
  undefined8 uStack_158;
  undefined2 uStack_150;
  undefined4 uStack_148;
  undefined4 uStack_144;
  undefined4 uStack_140;
  undefined4 uStack_13c;
  long *******ppppppplStack_138;
  long *******ppppppplStack_130;
  long lStack_128;
  uint uStack_120;
  undefined4 uStack_11c;
  undefined4 uStack_118;
  undefined4 uStack_114;
  undefined8 uStack_110;
  undefined1 uStack_108;
  char cStack_100;
  ulong uStack_f8;
  undefined8 uStack_f0;
  undefined8 uStack_e8;
  undefined2 uStack_e0;
  undefined4 uStack_d8;
  undefined4 uStack_d4;
  undefined4 uStack_d0;
  undefined4 uStack_cc;
  undefined8 uStack_c8;
  long *plStack_c0;
  byte *pbStack_b8;
  ulong uStack_b0;
  undefined1 auStack_a8 [8];
  byte bStack_a0;
  undefined7 uStack_9f;
  undefined8 uStack_90;
  byte bStack_80;
  undefined7 uStack_7f;
  undefined8 uStack_70;
  byte bStack_68;
  undefined7 uStack_67;
  undefined8 uStack_58;
  byte bStack_50;
  undefined7 uStack_4f;
  undefined8 uStack_40;

  uStack_c8 = param_5;
  plStack_c0 = param_6;
  func_0x04846ec0(auStack_a8);
  func_0x047de460(&ppppppplStack_1a8,param_1[1] + 0x1d);
  ppppppplStack_1c8 = (long *******)&ppppplStack_1c0;
  ppppplStack_1c0 = (long *****)0x0;
  lStack_1b8 = 0;
  lVar6 = *param_3;
  lVar1 = param_3[1];
  if (lVar6 != lVar1) {
    do {
      ppppppplStack_1b0 = (long *******)&ppppppplStack_1c8;
      func_0x047de8a0(&ppppppplStack_1b0,lVar6,lVar6);
      lVar6 = lVar6 + 0x30;
    } while (lVar6 != lVar1);
  }
  if (lStack_198 == 0) {
    ppppppplStack_1a0 =
         (long *******)func_0x025a7b00(&ppppppplStack_1a8,ppppplStack_1c0,&ppppppplStack_1a8);
    ppppppplStack_1a8 = (long *******)&ppppppplStack_1a0;
    if (ppppppplStack_1a0 != (long *******)0x0) {
      ppppppplStack_1a0[2] = (long ******)ppppppplStack_1a8;
      ppppppplVar4 = ppppppplStack_1a0;
      ppppppplVar5 = ppppppplStack_1a0;
      goto LAB_047dd160;
    }
    ppppppplVar5 = (long *******)0x0;
  }
  else {
    ppppppplVar4 = (long *******)
                   func_0x029be7d0(&ppppppplStack_1a8,ppppppplStack_1a0,ppppplStack_1c0);
    ppppppplVar5 = ppppppplVar4;
    if (ppppppplVar4 == (long *******)0x0) {
      ppppppplStack_1a8 = (long *******)&ppppppplStack_1a0;
      ppppppplVar5 = (long *******)0x0;
      ppppppplStack_1a0 = ppppppplVar4;
    }
    else {
LAB_047dd160:
      do {
        ppppppplStack_1a8 = ppppppplVar4;
        ppppppplVar4 = (long *******)*ppppppplStack_1a8;
        ppppppplStack_1a0 = ppppppplVar5;
      } while ((long *******)*ppppppplStack_1a8 != (long *******)0x0);
    }
  }
  lStack_198 = lStack_1b8;
  ppppppplStack_130 = (long *******)0x0;
  lStack_128 = 0;
  ppppppplStack_138 = (long *******)&ppppppplStack_130;
  if (lStack_1b8 != 0) {
    ppppppplStack_130 =
         (long *******)func_0x025a7b00(&ppppppplStack_138,ppppppplVar5,&ppppppplStack_138);
    ppppppplStack_130[2] = (long ******)&ppppppplStack_130;
    ppppppplVar4 = ppppppplStack_130;
    do {
      ppppppplStack_138 = ppppppplVar4;
      ppppppplVar4 = (long *******)*ppppppplStack_138;
    } while ((long *******)*ppppppplStack_138 != (long *******)0x0);
    lStack_128 = lStack_198;
  }
  if ((uStack_190 & 1) == 0) {
    uStack_110 = uStack_180;
    uStack_120 = uStack_190;
    uStack_11c = uStack_18c;
    uStack_118 = (undefined4)uStack_188;
    uStack_114 = uStack_188._4_4_;
  }
  else {
    func_0x019f86e0(&uStack_120,uStack_180,uStack_188);
  }
  uStack_108 = uStack_178;
  cStack_100 = '\0';
  if (cStack_170 == '\x01') {
    if ((bStack_168 & 1) == 0) {
      uStack_e8 = uStack_158;
      uStack_f8 = CONCAT71(uStack_167,bStack_168);
      uStack_f0 = uStack_160;
    }
    else {
      func_0x019f86e0(&uStack_f8,uStack_158,uStack_160);
    }
    uStack_e0 = uStack_150;
    cStack_100 = '\x01';
  }
  uStack_d8 = uStack_148;
  uStack_d4 = uStack_144;
  uStack_d0 = uStack_140;
  uStack_cc = uStack_13c;
  ppppppplStack_1b0 = (long *******)&ppppplStack_1c0;
  func_0x01a76090(&ppppppplStack_1b0,ppppplStack_1c0);
  if (cStack_170 == '\x01') {
    if ((bStack_168 & 1) != 0) {
      func_0x04a8cfc8(uStack_158,CONCAT71(uStack_167,bStack_168) & 0xfffffffffffffffe);
    }
    cStack_170 = '\0';
  }
  if ((uStack_190 & 1) != 0) {
    func_0x04a8cfc8(uStack_180,CONCAT44(uStack_18c,uStack_190) & 0xfffffffffffffffe);
  }
  ppppppplStack_1c8 = (long *******)&ppppppplStack_1a0;
  func_0x01a76090(&ppppppplStack_1c8,ppppppplStack_1a0);
  ppppplVar2 = param_1[1];
  plVar3 = (long *)plStack_c0[4];
  if (plVar3 == (long *)0x0) {
    uStack_188 = (long *******)0x0;
  }
  else if (plVar3 == plStack_c0) {
    uStack_188 = (long *******)&ppppppplStack_1a8;
    (**(code **)(*plVar3 + 0x18))(plVar3,&ppppppplStack_1a8);
  }
  else {
    uStack_188 = (long *******)(**(code **)(*plVar3 + 0x10))();
  }
  func_0x0256db60(&ppppppplStack_1a8,ppppplVar2 + 0x10);
  if ((long ********)uStack_188 == &ppppppplStack_1a8) {
    lVar6 = 0x20;
LAB_047dd367:
    (**(code **)((long)*uStack_188 + lVar6))();
  }
  else {
    lVar6 = 0x28;
    if (uStack_188 != (long *******)0x0) goto LAB_047dd367;
  }
  ppppppplStack_1a8 = (long *******)&UNK_04c87c28;
  ppppplVar2 = param_1[1];
  ppppppplStack_1a0 = (long *******)param_1;
  uStack_188 = (long *******)&ppppppplStack_1a8;
  func_0x04ab5980(ppppplVar2 + 0x18);
  bVar7 = (*param_4 & 1) != 0;
  pbStack_b8 = param_4 + 1;
  if (bVar7) {
    pbStack_b8 = *(byte **)(param_4 + 0x10);
  }
  uStack_b0 = (ulong)(*param_4 >> 1);
  if (bVar7) {
    uStack_b0 = *(ulong *)(param_4 + 8);
  }
  func_0x0480cdc0(&ppppppplStack_1c8,param_1[1],auStack_a8,&ppppppplStack_138,uStack_c8,
                  &ppppppplStack_1a8,pbStack_b8._0_4_,(undefined4)uStack_b0);
  func_0x0480cd00(param_1[1] + 0x16,&ppppppplStack_1c8);
  func_0x0480ccc0(&ppppppplStack_1c8);
  func_0x04ab59b0(ppppplVar2 + 0x18);
  if ((long ********)uStack_188 == &ppppppplStack_1a8) {
    lVar6 = 0x20;
  }
  else {
    lVar6 = 0x28;
    if (uStack_188 == (long *******)0x0) goto joined_r0x047dd55d;
  }
  (**(code **)((long)*uStack_188 + lVar6))();
joined_r0x047dd55d:
  if (cStack_100 == '\x01') {
    if ((uStack_f8 & 1) != 0) {
      func_0x04a8cfc8(uStack_e8,uStack_f8 & 0xfffffffffffffffe);
    }
    cStack_100 = '\0';
  }
  if ((uStack_120 & 1) != 0) {
    func_0x04a8cfc8(uStack_110,CONCAT44(uStack_11c,uStack_120) & 0xfffffffffffffffe);
  }
  ppppppplStack_1a8 = (long *******)&ppppppplStack_130;
  func_0x01a76090(&ppppppplStack_1a8,ppppppplStack_130);
  if ((bStack_50 & 1) != 0) {
    func_0x04a8cfc8(uStack_40,CONCAT71(uStack_4f,bStack_50) & 0xfffffffffffffffe);
  }
  if ((bStack_68 & 1) != 0) {
    func_0x04a8cfc8(uStack_58,CONCAT71(uStack_67,bStack_68) & 0xfffffffffffffffe);
  }
  if ((bStack_80 & 1) != 0) {
    func_0x04a8cfc8(uStack_70,CONCAT71(uStack_7f,bStack_80) & 0xfffffffffffffffe);
  }
  if ((bStack_a0 & 1) != 0) {
    func_0x04a8cfc8(uStack_90,CONCAT71(uStack_9f,bStack_a0) & 0xfffffffffffffffe);
  }
  return;
}


// FUNCTION 0480cd70 bounds 0480ce7f

void FUN_0480cd70(undefined8 *param_1,undefined8 param_2)

{
  undefined8 *puVar1;

  puVar1 = (undefined8 *)func_0x04b078f0(8);
  *puVar1 = param_2;
  *param_1 = puVar1;
  return;
}


// FUNCTION 047d9130 bounds 047d974f

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

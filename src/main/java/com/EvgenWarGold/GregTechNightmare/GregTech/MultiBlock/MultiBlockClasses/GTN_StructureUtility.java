package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses;

import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureUtility;

import gtPlusPlus.core.block.base.BasicBlock;
import gtPlusPlus.core.block.base.BlockBaseModular;
import gtPlusPlus.core.material.Material;

public class GTN_StructureUtility {

    public static <T> IStructureElement<T> ofFrame(Material material) {
        return StructureUtility.ofBlock(BlockBaseModular.getMaterialBlock(material, BasicBlock.BlockTypes.FRAME), 0);
    }
}

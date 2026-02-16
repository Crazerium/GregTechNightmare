package com.EvgenWarGold.GregTechNightmare.GregTech.Items;

import com.EvgenWarGold.GregTechNightmare.GregTech.Api.AbstractGtnMetaBlock;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.IHasMoreBlockInfo;
import com.EvgenWarGold.GregTechNightmare.GregTech.Api.IHasTooltips;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.util.GTLanguageManager;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.List;

public class GtnMetaBlockItem extends ItemBlock {

    public GtnMetaBlockItem(Block aBlock) {
        super(requireGtnMetaBlock(aBlock));
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    private static AbstractGtnMetaBlock requireGtnMetaBlock(Block block) {
        try {
            return (AbstractGtnMetaBlock) block;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(
                "Block is not a TstMetaBlockBase: " + block.getUnlocalizedName()
                    + " ("
                    + block.getClass()
                    .getSimpleName()
                    + ")");
        }
    }

    protected final AbstractGtnMetaBlock getMetaBlock() {
        return (AbstractGtnMetaBlock) this.field_150939_a;
    }

    public final String mNoMobsToolTip = GTLanguageManager
        .addStringLocalization("gt.nomobspawnsonthisblock", "Mobs cannot Spawn on this Block");
    public final String mNoTileEntityToolTip = GTLanguageManager
        .addStringLocalization("gt.notileentityinthisblock", "This is NOT a TileEntity!");

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack aItemStack, EntityPlayer p_77624_2_, List<String> theTooltipsList,
                               boolean advanced) {
        int meta = aItemStack.getItemDamage();

        var tooltips = getMetaBlock().getTooltips(meta, IHasTooltips.isShiftKeyDown());
        if (tooltips != null) {
            theTooltipsList.addAll(Arrays.asList(tooltips));
        }

        // additional information about the block
        // see IHasMoreBlockInfo
        if (getMetaBlock() instanceof IHasMoreBlockInfo hasMoreBlockInfo) {
            if (hasMoreBlockInfo.isNoMobSpawn()) {
                theTooltipsList.add(mNoMobsToolTip);
            }
            if (hasMoreBlockInfo.isNotTileEntity()) {
                theTooltipsList.add(mNoTileEntityToolTip);
            }
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack aStack) {
        return getMetaBlock().getUnlocalizedName() + "." + this.getDamage(aStack);
    }

    @Override
    public int getMetadata(int aMeta) {
        return getMetaBlock().isValidVariant(aMeta) ? aMeta : 0;
    }
}

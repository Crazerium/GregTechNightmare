package com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Dyes;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

@SideOnly(Side.CLIENT)
public final class WildcardPrefixItemRenderer implements IItemRenderer {

    private static final Object COLOR_LOCK = new Object();
    private static final short[] WHITE_RGBA = { 255, 255, 255, 255 };
    private static final Materials[] RENDER_MATERIALS = {
        Materials.Tin,
        Materials.Silver,
        Materials.Platinum,
        Materials.StainlessSteel,
        Materials.Iron,
        Materials.Steel };

    private final ItemStack[] renderStacks = new ItemStack[WildcardPrefix.values().length];
    private final Materials[] renderMaterials = new Materials[WildcardPrefix.values().length];
    private final boolean[] resolved = new boolean[WildcardPrefix.values().length];

    public static void register(Item item) {
        MinecraftForgeClient.registerItemRenderer(item, new WildcardPrefixItemRenderer());
    }

    @Override
    public boolean handleRenderType(ItemStack stack, ItemRenderType type) {
        return type == ItemRenderType.INVENTORY && getRenderBlock(stack) != null;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack stack, ItemRendererHelper helper) {
        return type == ItemRenderType.INVENTORY && helper == ItemRendererHelper.INVENTORY_BLOCK
            && getRenderBlock(stack) != null;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        if (type != ItemRenderType.INVENTORY || stack == null) return;

        WildcardPrefix prefix = WildcardPrefix.byMeta(stack.getItemDamage());
        ItemStack renderStack = getRenderStack(prefix);
        Materials material = getRenderMaterial(prefix);
        Block block = getBlock(renderStack);
        if (block == null || material == null) return;

        RenderBlocks renderBlocks = data.length > 0 && data[0] instanceof RenderBlocks
            ? (RenderBlocks) data[0]
            : new RenderBlocks();

        synchronized (COLOR_LOCK) {
            short[] materialColor = material.mRGBa;
            short[] previousMaterialColor = materialColor.clone();
            short[] insulationColor = prefix.isCable() ? Dyes.CABLE_INSULATION.getRGBA() : null;
            short[] previousInsulationColor = insulationColor == null ? null : insulationColor.clone();

            GL11.glPushMatrix();
            try {
                // GT block models read these global tints while rendering, so restore them immediately afterwards.
                copyColor(WHITE_RGBA, materialColor);
                copyColor(WHITE_RGBA, insulationColor);

                Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                RenderingRegistry.instance()
                    .renderInventoryBlock(renderBlocks, block, renderStack.getItemDamage(), block.getRenderType());
            } finally {
                copyColor(previousMaterialColor, materialColor);
                copyColor(previousInsulationColor, insulationColor);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glPopMatrix();
            }
        }
    }

    private Block getRenderBlock(ItemStack wildcardStack) {
        if (wildcardStack == null) return null;
        return getBlock(getRenderStack(WildcardPrefix.byMeta(wildcardStack.getItemDamage())));
    }

    private ItemStack getRenderStack(WildcardPrefix prefix) {
        if (prefix == null || !prefix.usesGregTechBlockModel()) return null;

        resolve(prefix);
        return renderStacks[prefix.getMeta()];
    }

    private Materials getRenderMaterial(WildcardPrefix prefix) {
        if (prefix == null || !prefix.usesGregTechBlockModel()) return null;

        resolve(prefix);
        return renderMaterials[prefix.getMeta()];
    }

    private void resolve(WildcardPrefix prefix) {
        int meta = prefix.getMeta();
        if (resolved[meta]) return;

        OrePrefixes orePrefix = prefix.getOrePrefix();
        for (Materials material : RENDER_MATERIALS) {
            ItemStack candidate = GTOreDictUnificator.get(orePrefix, material, 1L);
            if (GTUtility.isStackInvalid(candidate) || getBlock(candidate) == null) continue;

            renderStacks[meta] = GTUtility.copyAmount(1, candidate);
            renderMaterials[meta] = material;
            break;
        }
        resolved[meta] = true;
    }

    private static void copyColor(short[] source, short[] target) {
        if (source == null || target == null) return;
        System.arraycopy(source, 0, target, 0, Math.min(source.length, target.length));
    }

    private static Block getBlock(ItemStack stack) {
        if (GTUtility.isStackInvalid(stack) || !(stack.getItem() instanceof ItemBlock)) return null;

        Block block = Block.getBlockFromItem(stack.getItem());
        return block == null || block == Blocks.air ? null : block;
    }
}

package com.EvgenWarGold.GregTechNightmare.Tooltips;

import static net.minecraft.util.EnumChatFormatting.DARK_GRAY;
import static net.minecraft.util.EnumChatFormatting.GOLD;
import static net.minecraft.util.EnumChatFormatting.GREEN;
import static net.minecraft.util.EnumChatFormatting.RED;
import static net.minecraft.util.EnumChatFormatting.YELLOW;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.input.Keyboard;

import com.slprime.chromatictooltips.api.EnricherPlace;
import com.slprime.chromatictooltips.api.ITooltipComponent;
import com.slprime.chromatictooltips.api.ITooltipEnricher;
import com.slprime.chromatictooltips.api.TooltipContext;
import com.slprime.chromatictooltips.api.TooltipLines;
import com.slprime.chromatictooltips.api.TooltipModifier;
import com.slprime.chromatictooltips.component.TextComponent;
import com.slprime.chromatictooltips.event.AttributeEnricherEvent;
import com.slprime.chromatictooltips.event.ItemInfoEnricherEvent;
import com.slprime.chromatictooltips.util.TooltipUtils;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class DebugEnricher implements ITooltipEnricher {

    private boolean isPressed = false;

    @Override
    public String sectionId() {
        return "debugEnricher";
    }

    @Override
    public EnricherPlace place() {
        return EnricherPlace.BODY;
    }

    @Override
    public EnumSet<TooltipModifier> mode() {
        return EnumSet.of(TooltipModifier.NONE);
    }

    @Override
    public TooltipLines build(TooltipContext context) {
        final ItemStack stack = context.getItem();

        if (stack == null) return null;

        final Item item = stack.getItem();

        if (item == null) return null;

        isPressed = false;

        if (!Keyboard.isKeyDown(Keyboard.KEY_K)) return null;

        isPressed = true;

        final List<ITooltipComponent> components = new ArrayList<>();

        addMainInfo(components, stack, item);
        addOreDictInfo(components, stack);
        addNBTInfo(components, stack);

        return new TooltipLines(components);
    }

    @SubscribeEvent
    public void onItemInfoEnricher(ItemInfoEnricherEvent event) {
        if (isPressed) {
            event.tooltip.clear();
        }
    }

    @SubscribeEvent
    public void onAttributeEnricher(AttributeEnricherEvent event) {
        if (isPressed) {
            event.stats.clear();
        }
    }

    private void addMainInfo(List<ITooltipComponent> components, ItemStack stack, Item item) {
        components.add(new TextComponent(RED + "======== Item Info ========"));
        components.add(new TextComponent(GOLD + "String: " + GREEN + stack.toString()));
        components.add(new TextComponent(GOLD + "Display Name: " + GREEN + stack.getDisplayName()));
        components
            .add(new TextComponent(GOLD + "ItemStack Display Name: " + GREEN + item.getItemStackDisplayName(stack)));

        String unlocalizedName = stack.getUnlocalizedName();

        components.add(new TextComponent(GOLD + "Unlocalized Name: " + GREEN + unlocalizedName));

        if (!unlocalizedName.equals(item.getUnlocalizedNameInefficiently(stack))) {
            components.add(
                new TextComponent(
                    GOLD + "Unlocalized Inefficiently Name: " + GREEN + item.getUnlocalizedNameInefficiently(stack)));
        }

        if (!unlocalizedName.equals(item.getUnlocalizedName(stack))) {
            components
                .add(new TextComponent(GOLD + "ItemStack Unlocalized Name: " + GREEN + item.getUnlocalizedName(stack)));
        }

        components.add(
            new TextComponent(
                GOLD + "Class Item: "
                    + GREEN
                    + item.getClass()
                        .getName()));

        Block block = Block.getBlockFromItem(item);

        if (block != null && !block.equals(Blocks.air)) {
            components.add(
                new TextComponent(
                    GOLD + "Class Block: "
                        + GREEN
                        + block.getClass()
                            .getName()));
        }

        components.add(new TextComponent(GOLD + "ID: " + GREEN + Item.getIdFromItem(item)));
        components.add(new TextComponent(GOLD + "Stack size: " + GREEN + stack.stackSize));
        components.add(new TextComponent(GOLD + "Damage: " + GREEN + stack.getItemDamage()));
        components.add(new TextComponent(GOLD + "Max Damage: " + GREEN + stack.getMaxDamage()));
    }

    private void addOreDictInfo(List<ITooltipComponent> components, ItemStack stack) {
        final List<ITooltipComponent> list = new ArrayList<>();
        for (int oreDict : OreDictionary.getOreIDs(stack)) {
            list.add(
                new TextComponent(
                    TooltipUtils.translate("enricher.oreDictionary.entry", OreDictionary.getOreName(oreDict))));
        }

        if (!list.isEmpty()) {
            components.add(new TextComponent(RED + "======== OreDict ========"));
            components.addAll(list);
        }
    }

    private void addNBTInfo(List<ITooltipComponent> components, ItemStack stack) {
        final List<ITooltipComponent> list = new ArrayList<>();
        NBTTagCompound nbt = stack.stackTagCompound;

        if (nbt != null) {
            for (String keyObj : nbt.func_150296_c()) {
                list.add(
                    new TextComponent(
                        DARK_GRAY + " - " + YELLOW + keyObj + DARK_GRAY + " : " + YELLOW + nbt.getTag(keyObj)));
            }
        }

        if (!list.isEmpty()) {
            components.add(new TextComponent(RED + "======== NBT Data ========"));
            components.addAll(list);
        }
    }
}

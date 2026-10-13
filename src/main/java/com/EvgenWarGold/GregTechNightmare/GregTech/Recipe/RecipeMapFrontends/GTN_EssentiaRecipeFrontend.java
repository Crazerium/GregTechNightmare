package com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.RecipeMapFrontends;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.EvgenWarGold.GregTechNightmare.GregTech.Recipe.MetaData.SimpleMetaData;
import com.gtnewhorizons.modularui.api.ModularUITextures;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.math.Size;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.TieredVariant;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.recipe.RecipeMapFrontend;
import gregtech.api.util.GTRecipe;
import gregtech.common.gui.modularui.UIHelper;
import gregtech.nei.GTNEIDefaultHandler;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class GTN_EssentiaRecipeFrontend extends RecipeMapFrontend {

    public static final int MAX_ASPECT_SLOTS = 16;
    /** 6x4 grid: the central item and the pedestal items of the biggest infusion recipes. */
    public static final int INFUSION_ITEM_SLOTS = 24;
    private static final int INFUSION_GRID_COLUMNS = 6;
    private static final int CRUCIBLE_ITEM_SLOTS = 1;

    private final boolean infusion;

    public GTN_EssentiaRecipeFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder, boolean infusion) {
        super(uiPropertiesBuilder, neiPropertiesBuilder);
        this.infusion = infusion;
    }

    @Override
    protected NEIRecipePropertiesBuilder modifyNEIProperties(NEIRecipePropertiesBuilder builder) {
        boolean isInfusion = uiProperties.maxItemInputs > CRUCIBLE_ITEM_SLOTS + MAX_ASPECT_SLOTS;
        return builder.recipeBackgroundSize(new Size(170, isInfusion ? 148 : 106))
            .itemInputsGetter(recipe -> getDisplayInputs(recipe, isInfusion));
    }

    @Override
    public ModularWindow.Builder createNEITemplate(GTNEIDefaultHandler.NEITemplateContext ctx) {
        ModularWindow.Builder builder = ModularWindow.builder(neiProperties.recipeBackgroundSize)
            .setBackground(GTUITextures.BACKGROUND_NEI_SINGLE_RECIPE);
        if (uiProperties.useProgressBar) addProgressBar(builder, ctx);
        UIHelper.forEachSlots(
            (i, backgrounds, pos) -> builder.widget(
                SlotWidget.phantom(ctx.itemInputsInventory, i)
                    .setBackground(
                        i < (infusion ? INFUSION_ITEM_SLOTS : CRUCIBLE_ITEM_SLOTS) ? backgrounds
                            : new IDrawable[] { IDrawable.EMPTY })
                    .setPos(pos)
                    .setSize(18, 18)),
            (i, backgrounds, pos) -> builder.widget(
                SlotWidget.phantom(ctx.itemOutputsInventory, i)
                    .setBackground(backgrounds)
                    .setPos(pos)
                    .setSize(18, 18)),
            (i, backgrounds, pos) -> {},
            (i, backgrounds, pos) -> {},
            (i, backgrounds, pos) -> {},
            ModularUITextures.ITEM_SLOT,
            ModularUITextures.FLUID_SLOT,
            uiProperties,
            uiProperties.maxItemInputs,
            uiProperties.maxItemOutputs,
            uiProperties.maxFluidInputs,
            uiProperties.maxFluidOutputs,
            TieredVariant.STANDARD,
            ctx.windowOffset);
        addGregTechLogo(builder, ctx.windowOffset);
        return builder;
    }

    @Override
    public List<Pos2d> getItemInputPositions(int itemInputCount) {
        List<Pos2d> result = new ArrayList<>(itemInputCount);
        int reserved = infusion ? INFUSION_ITEM_SLOTS : CRUCIBLE_ITEM_SLOTS;
        for (int i = 0; i < itemInputCount; i++) {
            if (i < reserved) {
                result.add(
                    infusion ? new Pos2d(8 + (i % INFUSION_GRID_COLUMNS) * 18, 12 + (i / INFUSION_GRID_COLUMNS) * 18)
                        : new Pos2d(8, 17));
            } else {
                int aspectIndex = i - reserved;
                result.add(new Pos2d(8 + (aspectIndex % 8) * 18, (infusion ? 94 : 53) + (aspectIndex / 8) * 18));
            }
        }
        return result;
    }

    @Override
    public List<Pos2d> getItemOutputPositions(int itemOutputCount) {
        List<Pos2d> result = new ArrayList<>(itemOutputCount);
        for (int i = 0; i < itemOutputCount; i++) {
            result.add(infusion ? new Pos2d(144, 39 + i * 18) : new Pos2d(135, 17 + i * 18));
        }
        return result;
    }

    private static Map<String, ItemStack> aspectTemplates;

    public static boolean hasAspectItem() {
        return !getAspectTemplates().isEmpty();
    }

    private static Map<String, ItemStack> getAspectTemplates() {
        if (aspectTemplates == null) {
            Map<String, ItemStack> templates = new LinkedHashMap<>();
            readAspectTemplates(GameRegistry.findItem("thaumcraftneiplugin", "Aspect"), templates, false);
            readAspectTemplates(GameRegistry.findItem("aspectrecipeindex", "aspect"), templates, true);
            aspectTemplates = templates;
        }
        return aspectTemplates;
    }

    private static void readAspectTemplates(Item item, Map<String, ItemStack> templates, boolean nbtBased) {
        if (item == null) return;
        List<ItemStack> variants = new ArrayList<>();
        try {
            item.getSubItems(item, item.getCreativeTab(), variants);
        } catch (RuntimeException | LinkageError ignored) {}
        for (ItemStack variant : variants) {
            if (variant == null || variant.getItem() != item) continue;
            String tag = readAspectTag(variant);
            if (tag == null) tag = matchAspectName(variant);
            if (tag != null && !templates.containsKey(tag)) templates.put(tag, variant.copy());
        }
        if (nbtBased) {
            for (Aspect aspect : Aspect.aspects.values()) {
                if (aspect == null || templates.containsKey(aspect.getTag())) continue;
                ItemStack stack = new ItemStack(item, 1, 0);
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setString("Aspect", aspect.getTag());
                stack.setTagCompound(nbt);
                templates.put(aspect.getTag(), stack);
            }
        }
    }

    private static String readAspectTag(ItemStack stack) {
        if (!stack.hasTagCompound()) return null;
        NBTTagCompound nbt = stack.getTagCompound();
        String[] keys = { "Aspect", "aspect", "AspectTag", "aspectTag" };
        for (String key : keys) {
            String tag = nbt.getString(key);
            if (!tag.isEmpty() && Aspect.getAspect(tag) != null) return tag;
        }
        return null;
    }

    private static String matchAspectName(ItemStack stack) {
        String displayed;
        try {
            displayed = cleanName(stack.getDisplayName());
        } catch (RuntimeException | LinkageError ignored) {
            return null;
        }
        if (displayed.isEmpty()) return null;
        for (Aspect aspect : Aspect.aspects.values()) {
            if (aspect != null && (nameEndsWith(displayed, cleanName(aspect.getTag()))
                || nameEndsWith(displayed, cleanName(aspect.getName())))) {
                return aspect.getTag();
            }
        }
        return null;
    }

    private static String cleanName(String name) {
        if (name == null) return "";
        return name.replaceAll("\u00a7.", "")
            .trim()
            .toLowerCase(Locale.ROOT);
    }

    private static boolean nameEndsWith(String text, String name) {
        if (name.isEmpty() || !text.endsWith(name)) return false;
        int preceding = text.length() - name.length() - 1;
        return preceding < 0 || !Character.isLetterOrDigit(text.charAt(preceding));
    }

    public static List<String> getMissingAspectTags(AspectList aspects) {
        List<String> missing = new ArrayList<>();
        if (aspects == null) return missing;
        Map<String, ItemStack> templates = getAspectTemplates();
        for (Aspect aspect : aspects.getAspects()) {
            if (aspect != null && aspects.getAmount(aspect) > 0 && !templates.containsKey(aspect.getTag())) {
                missing.add(aspect.getTag());
            }
        }
        return missing;
    }

    private static ItemStack[] getDisplayInputs(GTRecipe recipe, boolean infusion) {
        int reserved = infusion ? INFUSION_ITEM_SLOTS : CRUCIBLE_ITEM_SLOTS;
        AspectList aspects = recipe.getMetadata(SimpleMetaData.ASPECT_COST);
        if (aspects == null || !getMissingAspectTags(aspects).isEmpty()) return copyInputs(recipe.mInputs);
        Map<String, ItemStack> templates = getAspectTemplates();
        Aspect[] ordered = aspects.getAspects();
        Arrays.sort(
            ordered,
            Comparator.nullsLast(
                Comparator.<Aspect>comparingInt(aspects::getAmount)
                    .reversed()
                    .thenComparing(Aspect::getTag)));
        List<ItemStack> display = new ArrayList<>(reserved + MAX_ASPECT_SLOTS);
        for (int i = 0; i < reserved; i++) {
            display.add(i < recipe.mInputs.length && recipe.mInputs[i] != null ? recipe.mInputs[i].copy() : null);
        }
        int displayed = 0;
        for (Aspect aspect : ordered) {
            if (aspect == null || aspects.getAmount(aspect) <= 0) continue;
            if (displayed == MAX_ASPECT_SLOTS) break;
            ItemStack template = templates.get(aspect.getTag());
            ItemStack stack = template.copy();
            stack.stackSize = aspects.getAmount(aspect);
            display.add(stack);
            displayed++;
        }
        return display.toArray(new ItemStack[0]);
    }

    private static ItemStack[] copyInputs(ItemStack[] inputs) {
        ItemStack[] result = new ItemStack[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            result[i] = inputs[i] == null ? null : inputs[i].copy();
        }
        return result;
    }
}

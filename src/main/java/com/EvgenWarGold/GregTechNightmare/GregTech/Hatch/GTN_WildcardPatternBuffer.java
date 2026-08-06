package com.EvgenWarGold.GregTechNightmare.GregTech.Hatch;

import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_ME_CRAFTING_INPUT_BUFFER;
import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import java.util.Collections;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.EvgenWarGold.GregTechNightmare.GregTech.Gui.GTN_WildcardPatternBufferGui;
import com.EvgenWarGold.GregTechNightmare.Utils.Constants;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.CycleButtonWidget;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.SlotGroup;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;

import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.items.misc.ItemEncodedPattern;
import gregtech.api.enums.SoundResource;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.render.TextureFactory;
import gregtech.common.tileentities.machines.MTEHatchCraftingInputME;

public class GTN_WildcardPatternBuffer extends MTEHatchCraftingInputME {

    public static final int PHYSICAL_PATTERN_SLOTS = 36;
    public static final int PRIMARY_PATTERN_SLOT = 0;
    public static final int CIRCUIT_SLOT = PHYSICAL_PATTERN_SLOTS;
    public static final int SHARED_INPUT_START = CIRCUIT_SLOT + 1;
    public static final int SHARED_INPUT_END = SHARED_INPUT_START + 8;

    public GTN_WildcardPatternBuffer(int aID, String aName) {
        super(aID, aName, aName, true);
    }

    public GTN_WildcardPatternBuffer(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures, true);
    }

    @Override
    public GTN_WildcardPatternBuffer newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GTN_WildcardPatternBuffer(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public int rows() {
        return 1;
    }

    @Override
    public int rowSize() {
        return 1;
    }

    @Override
    public int numSlots() {
        return 1;
    }

    @Override
    public void setInventorySlotContents(int aIndex, ItemStack aStack) {
        if (aIndex > PRIMARY_PATTERN_SLOT && aIndex < PHYSICAL_PATTERN_SLOTS) return;
        super.setInventorySlotContents(aIndex, aStack);
    }

    public boolean isPrimaryPattern(ICraftingPatternDetails source) {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        World world = base == null ? null : base.getWorld();
        if (source == null || world == null) return false;

        ItemStack pattern = getStackInSlot(PRIMARY_PATTERN_SLOT);
        if (pattern == null || !(pattern.getItem() instanceof ICraftingPatternItem)) return false;

        ICraftingPatternDetails primary = ((ICraftingPatternItem) pattern.getItem()).getPatternForItem(pattern, world);
        if (primary == null) return false;
        if (source == primary || source.equals(primary)) return true;

        ItemStack sourcePattern = source.getPattern();
        ItemStack primaryPattern = primary.getPattern();
        return sourcePattern != null && primaryPattern != null && sourcePattern.isItemEqual(primaryPattern)
            && ItemStack.areItemStackTagsEqual(sourcePattern, primaryPattern);
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        return new GTN_WildcardPatternBufferGui(this).build(data, syncManager, uiSettings);
    }

    @Override
    public int getGUIWidth() {
        return 212;
    }

    @Override
    public int getGUIHeight() {
        return 184;
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        addDedicatedUI(builder);
    }

    private void addDedicatedUI(ModularWindow.Builder builder) {
        builder.widget(
                SlotGroup.ofItemHandler(inventoryHandler, 1)
                    .startFromSlot(PRIMARY_PATTERN_SLOT)
                    .endAtSlot(PRIMARY_PATTERN_SLOT)
                    .phantom(false)
                    .background(getGUITextureSet().getItemSlot(), GTUITextures.OVERLAY_SLOT_PATTERN_ME)
                    .widgetCreator(slot -> new SlotWidget(slot) {
                        @Override
                        protected ItemStack getItemStackForRendering(Slot slotIn) {
                            ItemStack stack = slot.getStack();
                            if (stack == null || !(stack.getItem() instanceof ItemEncodedPattern)) return stack;
                            ItemStack output = ((ItemEncodedPattern) stack.getItem()).getOutput(stack);
                            return output != null ? output : stack;
                        }
                    }.setFilter(itemStack -> itemStack.getItem() instanceof ICraftingPatternItem)
                        .setChangeListener(() -> onPatternChange(slot.getSlotIndex(), slot.getStack())))
                    .build()
                    .setPos(8, 9))

            .widget(
                SlotGroup.ofItemHandler(inventoryHandler, 1)
                    .startFromSlot(CIRCUIT_SLOT)
                    .endAtSlot(CIRCUIT_SLOT)
                    .phantom(false)
                    .background(getGUITextureSet().getItemSlot())
                    .build()
                    .setPos(32, 9))

            .widget(
                SlotGroup.ofItemHandler(inventoryHandler, 3)
                    .startFromSlot(SHARED_INPUT_START)
                    .endAtSlot(SHARED_INPUT_END)
                    .phantom(false)
                    .background(getGUITextureSet().getItemSlot())
                    .build()
                    .setPos(8, 36))
            .widget(
                new ButtonWidget().setOnClick((clickData, widget) -> {
                    if (clickData.mouseButton == 0) refundAll(false);
                })
                    .setPlayClickSound(true)
                    .setBackground(GTUITextures.BUTTON_STANDARD, GTUITextures.OVERLAY_BUTTON_EXPORT)
                    .addTooltip(StatCollector.translateToLocal("GT5U.gui.tooltip.hatch.crafting_input_me.export"))
                    .setSize(16, 16)
                    .setPos(80, 9))
            .widget(
                new CycleButtonWidget().setToggle(
                    () -> disablePatternOptimization,
                    value -> disablePatternOptimization = value)
                    .setStaticTexture(GTUITextures.OVERLAY_BUTTON_PATTERN_OPTIMIZE)
                    .setVariableBackground(GTUITextures.BUTTON_STANDARD_TOGGLE)
                    .addTooltip(0, "Pattern Optimization:\n§7Allowed")
                    .addTooltip(1, "Pattern Optimization:\n§7Disabled")
                    .setPos(98, 9)
                    .setSize(16, 16))
            .widget(
                new ButtonWidget().setOnClick((clickData, widget) -> {
                    int value = clickData.shift ? 1 : 0;
                    if (clickData.mouseButton == 1) value |= 0b10;
                    doublePatterns(value);
                })
                    .setPlayClickSound(true)
                    .setBackground(GTUITextures.BUTTON_STANDARD, GTUITextures.OVERLAY_BUTTON_X2)
                    .addTooltip(StatCollector.translateToLocal("gui.tooltips.appliedenergistics2.DoublePatterns"))
                    .setSize(16, 16)
                    .setPos(116, 9))
            .widget(
                new ButtonWidget().setOnClick((clickData, widget) -> showPattern = !showPattern)
                    .setPlayClickSoundResource(
                        () -> showPattern ? SoundResource.GUI_BUTTON_UP.resourceLocation
                            : SoundResource.GUI_BUTTON_DOWN.resourceLocation)
                    .setBackground(() -> {
                        if (showPattern) {
                            return new IDrawable[] {
                                GTUITextures.BUTTON_STANDARD_PRESSED,
                                GTUITextures.OVERLAY_BUTTON_WHITELIST };
                        }
                        return new IDrawable[] {
                            GTUITextures.BUTTON_STANDARD,
                            GTUITextures.OVERLAY_BUTTON_BLACKLIST };
                    })
                    .attachSyncer(
                        new FakeSyncWidget.BooleanSyncer(() -> showPattern, value -> showPattern = value),
                        builder)
                    .dynamicTooltip(
                        () -> Collections.singletonList(
                            StatCollector.translateToLocal(
                                "GT5U.infodata.hatch.crafting_input_me.show_pattern."
                                    + (showPattern ? "enable" : "disabled"))))
                    .setTooltipShowUpDelay(TOOLTIP_DELAY)
                    .setUpdateTooltipEveryTick(true)
                    .setPos(134, 9)
                    .setSize(16, 16));
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return getTexturesInactive(aBaseTexture);
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, TextureFactory.of(OVERLAY_ME_CRAFTING_INPUT_BUFFER) };
    }

    @Override
    public String[] getDescription() {
        return new String[] {
            "One encoded pattern expands for every compatible GregTech material.",
            "Supports AE item and fluid processing patterns through AE2 Fluid Crafting.",
            "Programmed Circuit and nine shared slots are available in the dedicated GUI.",
            "Wildcard token stack size becomes the requested item count or fluid amount in mB.",
            "Requires an AE channel.",
            "Added by: " + Constants.MOD_NAME };
    }
}

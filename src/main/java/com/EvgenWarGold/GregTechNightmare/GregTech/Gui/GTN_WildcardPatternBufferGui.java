package com.EvgenWarGold.GregTechNightmare.GregTech.Gui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import net.minecraft.util.StatCollector;

import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_WildcardPatternBuffer;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.common.CommonWidgets;
import gregtech.common.gui.modularui.hatch.base.MTEHatchBaseGui;
import gregtech.common.gui.modularui.util.PatternSlot;
import gregtech.common.modularui2.widget.GhostCircuitSlotWidget;
import gregtech.common.modularui2.widget.builder.ItemSlotGridBuilder;

public final class GTN_WildcardPatternBufferGui extends MTEHatchBaseGui<GTN_WildcardPatternBuffer> {

    private static final String PATTERN_INV_NAME = "gtn_wildcard_pattern";
    private static final String SHARED_INV_NAME = "gtn_wildcard_shared";

    public GTN_WildcardPatternBufferGui(GTN_WildcardPatternBuffer machine) {
        super(machine);
    }

    @Override
    protected int getBasePanelWidth() {
        return 232;
    }

    @Override
    protected int getBasePanelHeight() {
        return 184;
    }

    @Override
    protected ParentWidget<?> createContentSection(ModularPanel panel, PanelSyncManager syncManager) {
        syncManager.registerSlotGroup(PATTERN_INV_NAME, 1);

        ParentWidget<?> content = super.createContentSection(panel, syncManager);

        content.child(
            IKey.str("Pattern")
                .asWidget()
                .pos(4, 0));
        content.child(createPatternSlot().pos(4, 14));

        content.child(createGhostCircuitSlot(syncManager).pos(30, 14));

        content.child(
            IKey.str("Shared inputs")
                .asWidget()
                .pos(58, 0));
        content.child(createSharedSlots(syncManager).pos(58, 14));

        content.child(
            IKey.str("Blacklist")
                .asWidget()
                .pos(148, 0));
        content.child(
            IKey.str("Item / fluid")
                .asWidget()
                .pos(148, 16));
        content.child(
            IKey.str("reserved")
                .asWidget()
                .pos(148, 28));

        return content;
    }

    private Grid createPatternSlot() {
        return new Grid().coverChildren()
            .gridOfWidthHeight(
                1,
                1,
                ($x, $y, index) -> new PatternSlot()
                    .slot(
                        new ModularSlot(machine.inventoryHandler, GTN_WildcardPatternBuffer.PRIMARY_PATTERN_SLOT)
                            .changeListener((stack, amountChanged, client, initialization) -> {
                                if (!client) {
                                    machine.onPatternChange(GTN_WildcardPatternBuffer.PRIMARY_PATTERN_SLOT, stack);
                                }
                            })
                            .slotGroup(PATTERN_INV_NAME)));
    }

    private Widget<?> createGhostCircuitSlot(PanelSyncManager syncManager) {
        Widget<?> circuitSlot = CommonWidgets.createCircuitSlot(syncManager, machine)
            .tooltipShowUpTimer(TOOLTIP_DELAY);

        if (circuitSlot instanceof GhostCircuitSlotWidget ghostCircuitSlot) {
            ghostCircuitSlot.getSlot()
                .changeListener((newItem, amountChanged, client, initialization) -> {
                    if (!client) {
                        IGregTechTileEntity base = machine.getBaseMetaTileEntity();
                        if (base != null) {
                            base.enableTicking();
                        }
                        machine.onContentsChanged(machine.getCircuitSlot());
                    }
                });
        }

        return circuitSlot;
    }

    private Grid createSharedSlots(PanelSyncManager syncManager) {
        return new ItemSlotGridBuilder(machine.inventoryHandler, syncManager)
            .size(3, 3)
            .slotGroupKey(SHARED_INV_NAME)
            .indexOffset(GTN_WildcardPatternBuffer.SHARED_INPUT_START)
            .build();
    }

    @Override
    protected Flow createBottomLeftCornerFlow(ModularPanel panel, PanelSyncManager syncManager) {
        return super.createBottomLeftCornerFlow(panel, syncManager)
            .child(createOptimizerButton())
            .child(createShowPatternButton())
            .child(createExportButton())
            .child(createDoublePatternButton());
    }

    private ToggleButton createOptimizerButton() {
        BooleanSyncValue value = new BooleanSyncValue(
            () -> !machine.disablePatternOptimization,
            enabled -> machine.disablePatternOptimization = !enabled).allowC2S();

        return new ToggleButton().value(value)
            .overlay(GTGuiTextures.OVERLAY_BUTTON_PATTERN_OPTIMIZE)
            .addTooltipLine(StatCollector.translateToLocal("GT5U.infodata.hatch.crafting_input_me.optimize_pattern"))
            .addTooltip(
                true,
                StatCollector.translateToLocal("GT5U.infodata.hatch.crafting_input_me.optimize_pattern.enable"))
            .addTooltip(
                false,
                StatCollector.translateToLocal("GT5U.infodata.hatch.crafting_input_me.optimize_pattern.disabled"))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private ToggleButton createShowPatternButton() {
        BooleanSyncValue value = new BooleanSyncValue(
            () -> machine.showPattern,
            enabled -> machine.showPattern = enabled).allowC2S();

        return new ToggleButton().value(value)
            .overlay(true, GTGuiTextures.OVERLAY_BUTTON_WHITELIST)
            .overlay(false, GTGuiTextures.OVERLAY_BUTTON_BLACKLIST)
            .addTooltip(
                true,
                StatCollector.translateToLocal("GT5U.infodata.hatch.crafting_input_me.show_pattern.enable"))
            .addTooltip(
                false,
                StatCollector.translateToLocal("GT5U.infodata.hatch.crafting_input_me.show_pattern.disabled"))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private ButtonWidget<?> createExportButton() {
        InteractionSyncHandler handler = new InteractionSyncHandler().setOnMousePressed(mouse -> {
            if (!mouse.isClient() && mouse.mouseButton == 0) {
                machine.refundAll(false);
            }
        });

        return new ButtonWidget<>().syncHandler(handler)
            .overlay(GTGuiTextures.OVERLAY_BUTTON_EXPORT)
            .addTooltipLine(StatCollector.translateToLocal("GT5U.gui.tooltip.hatch.crafting_input_me.export"))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private ButtonWidget<?> createDoublePatternButton() {
        InteractionSyncHandler handler = new InteractionSyncHandler().setOnMousePressed(mouse -> {
            if (!mouse.isClient()) {
                int value = mouse.shift ? 1 : 0;
                if (mouse.mouseButton == 1) value |= 0b10;
                machine.doublePatterns(value);
            }
        });

        return new ButtonWidget<>().syncHandler(handler)
            .overlay(GTGuiTextures.OVERLAY_BUTTON_X2)
            .addTooltipLine(StatCollector.translateToLocal("gui.tooltips.appliedenergistics2.DoublePatterns"))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    @Override
    protected boolean doesAddCircuitSlot() {
        return false;
    }

    @Override
    protected boolean supportsBottomRowOverlap() {
        return false;
    }
}

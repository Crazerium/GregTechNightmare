package com.EvgenWarGold.GregTechNightmare.GregTech.Gui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import net.minecraft.util.StatCollector;

import com.EvgenWarGold.GregTechNightmare.GregTech.Hatch.GTN_WildcardPatternBuffer;
import com.EvgenWarGold.GregTechNightmare.GregTech.Wildcard.WildcardBlacklistMode;
import com.cleanroommc.modularui.api.IPanelHandler;
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
import com.cleanroommc.modularui.widgets.slot.PhantomItemSlot;

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
    private static final String BLACKLIST_PANEL_KEY = "gtn_wildcard_blacklist";
    private static final String BLACKLIST_INV_NAME = "gtn_wildcard_blacklist_inventory";
    private int blacklistPage;

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
        IPanelHandler blacklistPanel = syncManager.syncedPanel(BLACKLIST_PANEL_KEY, true, this::createBlacklistPanel);

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
            IKey.lang("GTN.Wildcard.blacklist.title")
                .asWidget()
                .pos(148, 0));
        content.child(createOpenBlacklistButton(blacklistPanel).pos(148, 14));

        return content;
    }

    private ModularPanel createBlacklistPanel(PanelSyncManager syncManager, IPanelHandler $panelHandler) {
        ModularPanel panel = new ModularPanel(BLACKLIST_PANEL_KEY) {

            @Override
            public boolean disablePanelsBelow() {
                return true;
            }

        }.size(198, 256);

        panel.child(
            IKey.lang("GTN.Wildcard.blacklist.title")
                .asWidget()
                .pos(8, 6));
        panel.child(ButtonWidget.panelCloseButton());
        panel.child(
            IKey.lang(
                () -> machine.getBlacklistMode() == WildcardBlacklistMode.INPUT ? "GTN.Wildcard.blacklist.mode.input"
                    : "GTN.Wildcard.blacklist.mode.output")
                .asWidget()
                .pos(8, 20));
        panel.child(createBlacklistModeButton().pos(174, 24));
        panel.child(createClearBlacklistButton().pos(174, 46));
        panel.child(createBlacklistPageButton(-1).pos(8, 38));
        panel.child(
            IKey.lang(
                "GTN.Wildcard.blacklist.page",
                () -> new Object[] { blacklistPage + 1, GTN_WildcardPatternBuffer.BLACKLIST_PAGE_COUNT })
                .asWidget()
                .pos(76, 43));
        panel.child(createBlacklistPageButton(1).pos(152, 38));
        for (int page = 0; page < GTN_WildcardPatternBuffer.BLACKLIST_PAGE_COUNT; page++) {
            panel.child(createBlacklistSlots(syncManager, page).pos(8, 58));
        }
        panel.bindPlayerInventory(7);
        return panel;
    }

    private Grid createPatternSlot() {
        return new Grid().coverChildren()
            .gridOfWidthHeight(
                1,
                1,
                (_, _, _) -> new PatternSlot().slot(
                    new ModularSlot(machine.inventoryHandler, GTN_WildcardPatternBuffer.PRIMARY_PATTERN_SLOT)
                        .changeListener((stack, _, client, _) -> {
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
                .changeListener((_, _, client, _) -> {
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
        return new ItemSlotGridBuilder(machine.inventoryHandler, syncManager).size(3, 3)
            .slotGroupKey(SHARED_INV_NAME)
            .indexOffset(GTN_WildcardPatternBuffer.SHARED_INPUT_START)
            .build();
    }

    private Grid createBlacklistSlots(PanelSyncManager syncManager, int page) {
        Grid grid = new ItemSlotGridBuilder(machine.getBlacklistInventory(), syncManager)
            .size(GTN_WildcardPatternBuffer.BLACKLIST_COLUMNS, GTN_WildcardPatternBuffer.BLACKLIST_ROWS)
            .slotGroupKey(BLACKLIST_INV_NAME + "_" + page)
            .indexOffset(page * GTN_WildcardPatternBuffer.BLACKLIST_PAGE_SIZE)
            .itemSlotSupplier(PhantomItemSlot::new)
            .build();
        if (page != 0) grid.disabled();
        return grid.setEnabledIf($ -> blacklistPage == page);
    }

    private ButtonWidget<?> createBlacklistPageButton(int direction) {
        String tooltipKey = direction < 0 ? "GTN.Wildcard.blacklist.page.previous" : "GTN.Wildcard.blacklist.page.next";

        return new ButtonWidget<>().onMousePressed(mouseButton -> {
            if (mouseButton != 0) {
                return false;
            }

            blacklistPage = Math.floorMod(blacklistPage + direction, GTN_WildcardPatternBuffer.BLACKLIST_PAGE_COUNT);
            return true;
        })
            .overlay(IKey.str(direction < 0 ? "<" : ">"))
            .addTooltipLine(StatCollector.translateToLocal(tooltipKey))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private ButtonWidget<?> createOpenBlacklistButton(IPanelHandler panelHandler) {
        InteractionSyncHandler handler = new InteractionSyncHandler().setOnMousePressed(mouse -> {
            if (mouse.isClient() && mouse.mouseButton == 0) {
                panelHandler.openPanel();
            }
        });

        return new ButtonWidget<>().syncHandler(handler)
            .overlay(GTGuiTextures.OVERLAY_BUTTON_BLACKLIST)
            .addTooltipLine(StatCollector.translateToLocal("GTN.Wildcard.blacklist.open"))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private ToggleButton createBlacklistModeButton() {
        BooleanSyncValue value = new BooleanSyncValue(
            () -> machine.getBlacklistMode() == WildcardBlacklistMode.INPUT,
            inputMode -> machine
                .setBlacklistMode(inputMode ? WildcardBlacklistMode.INPUT : WildcardBlacklistMode.OUTPUT)).allowC2S();

        return new ToggleButton().value(value)
            .overlay(true, GTGuiTextures.OVERLAY_BUTTON_IMPORT)
            .overlay(false, GTGuiTextures.OVERLAY_BUTTON_EXPORT)
            .addTooltip(true, StatCollector.translateToLocal("GTN.Wildcard.blacklist.mode.input.tooltip"))
            .addTooltip(false, StatCollector.translateToLocal("GTN.Wildcard.blacklist.mode.output.tooltip"))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private ButtonWidget<?> createClearBlacklistButton() {
        InteractionSyncHandler handler = new InteractionSyncHandler().setOnMousePressed(mouse -> {
            if (!mouse.isClient() && mouse.mouseButton == 0) {
                machine.clearBlacklist();
            }
        });

        return new ButtonWidget<>().syncHandler(handler)
            .overlay(GTGuiTextures.TT_OVERLAY_BUTTON_TRASH_CAN)
            .addTooltipLine(StatCollector.translateToLocal("GTN.Wildcard.blacklist.clear"))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    @Override
    protected Flow createBottomLeftCornerFlow(ModularPanel panel, PanelSyncManager syncManager) {
        return super.createBottomLeftCornerFlow(panel, syncManager).child(createOptimizerButton())
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

}

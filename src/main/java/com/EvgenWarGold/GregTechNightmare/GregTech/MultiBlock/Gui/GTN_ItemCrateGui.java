package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Gui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import net.minecraft.util.StatCollector;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Storage.GTN_ItemCrate;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ScrollWidget;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Grid;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTGuis;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.modularui2.widget.builder.ItemSlotGridBuilder;

public class GTN_ItemCrateGui extends MTEMultiBlockBaseGui<GTN_ItemCrate> {

    private static final String STORAGE_INV_NAME = "gtn_item_crate_storage";
    private static final int COLUMNS = 9;
    private static final int ROWS = 64;
    private static final int SLOT_SIZE = 18;
    private static final int STORAGE_VIEW_WIDTH = COLUMNS * SLOT_SIZE + 6;
    private static final int STORAGE_VIEW_HEIGHT = 130;
    private static final int STORAGE_CONTENT_HEIGHT = ROWS * SLOT_SIZE;

    public GTN_ItemCrateGui(GTN_ItemCrate itemCrate) {
        super(itemCrate);
    }

    @Override
    public ModularPanel build(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        registerSyncValues(syncManager);
        syncManager.registerSlotGroup(STORAGE_INV_NAME, 0);

        VerticalScrollData scrollData = new VerticalScrollData();
        scrollData.setScrollSize(STORAGE_CONTENT_HEIGHT);

        ScrollWidget storage = new ScrollWidget(scrollData);
        storage.size(STORAGE_VIEW_WIDTH, STORAGE_VIEW_HEIGHT);
        storage.child(createStorageSlots(syncManager));

        ModularPanel panel = GTGuis.mteTemplatePanelBuilder(multiblock, guiData, syncManager, uiSettings)
            .setWidth(181)
            .setHeight(244)
            .doesBindPlayerInventory(true)
            .doesAddTitle(true)
            .doesAddCoverTabs(false)
            .doesAddGhostCircuitSlot(false)
            .doesAddGregTechLogo(false)
            .build();

        panel.child(
            IKey.lang(
                "GTN.ItemCrate.slots",
                () -> new Object[] { multiblock.countOccupiedSlots(), GTN_ItemCrate.SLOT_COUNT })
                .asWidget()
                .pos(7, 17));
        storage.pos(7, 28);
        panel.child(storage);
        panel.child(createSortButton().pos(145, 8));
        panel.child(createDepositAllButton(syncManager).pos(163, 8));
        return panel;
    }

    private ButtonWidget<?> createSortButton() {
        InteractionSyncHandler handler = new InteractionSyncHandler().setOnMousePressed(mouse -> {
            if (!mouse.isClient() && mouse.mouseButton == 0) {
                multiblock.sortStorage();
            }
        });

        return new ButtonWidget<>().syncHandler(handler)
            .overlay(IKey.str("S"))
            .addTooltipLine(StatCollector.translateToLocal("GTN.ItemCrate.sort"))
            .tooltipShowUpTimer(TOOLTIP_DELAY)
            .setEnabledIf($ -> multiblock.mMachine);
    }

    private ButtonWidget<?> createDepositAllButton(PanelSyncManager syncManager) {
        InteractionSyncHandler handler = new InteractionSyncHandler().setOnMousePressed(mouse -> {
            if (!mouse.isClient() && mouse.mouseButton == 0) {
                multiblock.depositAll(syncManager.getPlayer());
            }
        });

        return new ButtonWidget<>().syncHandler(handler)
            .overlay(GTGuiTextures.OVERLAY_BUTTON_IMPORT)
            .addTooltipLine(StatCollector.translateToLocal("GTN.ItemCrate.depositAll"))
            .tooltipShowUpTimer(TOOLTIP_DELAY)
            .setEnabledIf($ -> multiblock.mMachine);
    }

    private Grid createStorageSlots(PanelSyncManager syncManager) {
        Grid grid = new ItemSlotGridBuilder(multiblock.getStorageInventory(), syncManager).size(COLUMNS, ROWS)
            .slotGroupKey(STORAGE_INV_NAME)
            .build();
        return grid.setEnabledIf($ -> multiblock.mMachine);
    }
}

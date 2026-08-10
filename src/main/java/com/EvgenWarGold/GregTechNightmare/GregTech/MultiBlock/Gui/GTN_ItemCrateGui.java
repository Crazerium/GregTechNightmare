package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Gui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import java.util.Locale;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.LV.GTN_ItemCrate;
import com.EvgenWarGold.GregTechNightmare.Utils.GTN_Utils;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.ScrollWidget;
import com.cleanroommc.modularui.widget.WidgetTree;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTGuis;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;

public class GTN_ItemCrateGui extends MTEMultiBlockBaseGui<GTN_ItemCrate> {

    private static final String STORAGE_INV_NAME = "gtn_item_crate_storage";
    private static final int COLUMNS = 9;
    private static final int ROWS = 64;
    private static final int SLOT_SIZE = 18;
    private static final int STORAGE_VIEW_WIDTH = COLUMNS * SLOT_SIZE + 6;
    private static final int STORAGE_VIEW_HEIGHT = 126;
    private static final int STORAGE_CONTENT_HEIGHT = ROWS * SLOT_SIZE;

    private final ItemSlot[] storageSlots = new ItemSlot[GTN_ItemCrate.SLOT_COUNT];
    private String searchQuery = "";
    private String appliedSearchQuery = null;

    public GTN_ItemCrateGui(GTN_ItemCrate itemCrate) {
        super(itemCrate);
    }

    @Override
    public ModularPanel build(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        registerSyncValues(syncManager);
        syncManager.registerSlotGroup(STORAGE_INV_NAME, 0);

        VerticalScrollData scrollData = createVerticalScrollData();

        ParentWidget<?> storageSlots = createStorageSlots();

        StringSyncValue searchValue = createStringSyncValue();

        ScrollWidget<?> storage = createScrollWidget(scrollData, storageSlots);

        TextFieldWidget searchField = createTextFieldWidget(searchValue, scrollData, storage, storageSlots);

        ModularPanel panel = createModularPanel(guiData, syncManager, uiSettings);

        panel.child(getSearchTextWidget());
        panel.child(searchField);
        panel.child(getSlotsTextWidget());
        panel.child(storage);
        panel.child(createSortButton());
        panel.child(createDepositAllButton(syncManager));
        return panel;
    }

    private TextFieldWidget createTextFieldWidget(StringSyncValue searchValue, VerticalScrollData scrollData,
        ScrollWidget<?> storage, ParentWidget<?> storageSlots) {
        return new TextFieldWidget().pos(40, 11)
            .value(searchValue)
            .autoUpdateOnChange(true)
            .size(96, 18)
            .onUpdateListener(_ -> {
                if (GTN_Utils.isServer()) {
                    return;
                }

                String normalizedQuery = normalize(searchQuery);
                if (normalizedQuery.equals(appliedSearchQuery)) {
                    return;
                }

                appliedSearchQuery = normalizedQuery;
                updateStorageLayout(storageSlots, scrollData);
                scrollData.scrollTo(storage.getScrollArea(), 0);
            }, true);
    }

    private ScrollWidget<?> createScrollWidget(VerticalScrollData scrollData, ParentWidget<?> storageSlots) {
        return new ScrollWidget<>(scrollData).pos(9, 45)
            .size(STORAGE_VIEW_WIDTH, STORAGE_VIEW_HEIGHT)
            .child(storageSlots);
    }

    private VerticalScrollData createVerticalScrollData() {
        VerticalScrollData verticalScrollData = new VerticalScrollData();
        verticalScrollData.setScrollSize(STORAGE_CONTENT_HEIGHT);
        return verticalScrollData;
    }

    private StringSyncValue createStringSyncValue() {
        return new StringSyncValue(() -> searchQuery, value -> searchQuery = value == null ? "" : value).allowC2S();
    }

    private ModularPanel createModularPanel(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        return GTGuis.mteTemplatePanelBuilder(multiblock, guiData, syncManager, uiSettings)
            .setWidth(181)
            .setHeight(261)
            .doesBindPlayerInventory(true)
            .doesAddTitle(true)
            .doesAddCoverTabs(false)
            .doesAddGhostCircuitSlot(false)
            .doesAddGregTechLogo(false)
            .build();
    }

    private TextWidget<?> getSearchTextWidget() {
        return IKey.lang("GTN.ItemCrate.search")
            .asWidget()
            .pos(5, 16);
    }

    private TextWidget<?> getSlotsTextWidget() {
        return IKey
            .lang(
                "GTN.ItemCrate.slots",
                () -> new Object[] { multiblock.countOccupiedSlots(), GTN_ItemCrate.SLOT_COUNT })
            .asWidget()
            .pos(9, 34);
    }

    private ButtonWidget<?> createSortButton() {
        InteractionSyncHandler handler = new InteractionSyncHandler().setOnMousePressed(mouse -> {
            if (!mouse.isClient() && mouse.mouseButton == 0) {
                multiblock.sortStorage();
            }
        });

        return new ButtonWidget<>().syncHandler(handler)
            .pos(138, 11)
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
            .pos(158, 11)
            .overlay(GTGuiTextures.OVERLAY_BUTTON_IMPORT)
            .addTooltipLine(StatCollector.translateToLocal("GTN.ItemCrate.depositAll"))
            .tooltipShowUpTimer(TOOLTIP_DELAY)
            .setEnabledIf($ -> multiblock.mMachine);
    }

    private ParentWidget<?> createStorageSlots() {
        ParentWidget<?> container = new ParentWidget<>();
        container.size(COLUMNS * SLOT_SIZE, STORAGE_CONTENT_HEIGHT);
        for (int index = 0; index < GTN_ItemCrate.SLOT_COUNT; index++) {
            final int slotIndex = index;
            ItemSlot itemSlot = new ItemSlot()
                .slot(new ModularSlot(multiblock.getStorageInventory(), slotIndex).slotGroup(STORAGE_INV_NAME))
                .setEnabledIf($ -> isStorageSlotVisible(slotIndex));
            itemSlot.pos((slotIndex % COLUMNS) * SLOT_SIZE, (slotIndex / COLUMNS) * SLOT_SIZE);
            storageSlots[slotIndex] = itemSlot;
            container.child(itemSlot);
        }
        container.setEnabledIf($ -> multiblock.mMachine);
        return container;
    }

    private void updateStorageLayout(ParentWidget<?> storageContainer, VerticalScrollData scrollData) {
        if (GTN_Utils.isServer()) {
            return;
        }

        int visibleIndex = 0;
        for (int slot = 0; slot < GTN_ItemCrate.SLOT_COUNT; slot++) {
            if (!matchesSearch(slot)) {
                continue;
            }

            ItemSlot itemSlot = storageSlots[slot];
            if (itemSlot != null) {
                itemSlot.pos((visibleIndex % COLUMNS) * SLOT_SIZE, (visibleIndex / COLUMNS) * SLOT_SIZE);
            }
            visibleIndex++;
        }
        scrollData.setScrollSize(getFilteredContentHeight(visibleIndex));

        if (storageContainer.isValid()) {
            WidgetTree.resizeInternal(storageContainer.resizer(), false);
        }
    }

    private boolean isStorageSlotVisible(int slot) {
        if (GTN_Utils.isServer()) {
            return true;
        }
        return matchesSearch(slot);
    }

    private boolean matchesSearch(int slot) {
        String query = normalize(searchQuery);
        if (query.isEmpty()) {
            return true;
        }

        ItemStack stack = multiblock.getStorageInventory()
            .getStackInSlot(slot);
        if (stack == null) {
            return false;
        }

        String displayName = normalize(stack.getDisplayName());
        String unlocalizedName = normalize(stack.getUnlocalizedName());
        for (String token : query.split("\\s+")) {
            if (!displayName.contains(token) && !unlocalizedName.contains(token)) {
                return false;
            }
        }
        return true;
    }

    private static int getFilteredContentHeight(int matches) {
        int rows = Math.max(1, (matches + COLUMNS - 1) / COLUMNS);
        return Math.max(STORAGE_VIEW_HEIGHT, rows * SLOT_SIZE);
    }

    private static String normalize(String text) {
        if (text == null) {
            return "";
        }
        String clean = EnumChatFormatting.getTextWithoutFormattingCodes(text);
        return (clean == null ? "" : clean).trim()
            .toLowerCase(Locale.ROOT);
    }
}

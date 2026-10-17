package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Gui;

import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatNumber;

import com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.Processing.MV.GTN_BotanicalGreenhouse;
import com.EvgenWarGold.GregTechNightmare.Utils.GTN_Utils;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.TextWidget;

import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;

/** Controller screen of the greenhouse with the mana made per second. */
public class GTN_BotanicalGreenhouseGui extends MTEMultiBlockBaseGui<GTN_BotanicalGreenhouse> {

    private static final String SYNC_MANA_RATE = "manaPerSecond";

    public GTN_BotanicalGreenhouseGui(GTN_BotanicalGreenhouse greenhouse) {
        super(greenhouse);
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(SYNC_MANA_RATE, new LongSyncValue(multiblock::getManaPerSecond));
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        LongSyncValue manaRate = syncManager.findSyncHandler(SYNC_MANA_RATE, LongSyncValue.class);
        return super.createTerminalTextWidget(syncManager, parent).child(
            new TextWidget<>(
                IKey.dynamic(
                    () -> GTN_Utils
                        .tr("multiblock.BotanicalGreenhouse.waila.mana_rate", formatNumber(manaRate.getLongValue())))));
    }
}

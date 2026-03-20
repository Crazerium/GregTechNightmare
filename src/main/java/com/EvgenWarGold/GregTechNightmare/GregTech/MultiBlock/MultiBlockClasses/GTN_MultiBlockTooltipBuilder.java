package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.MultiBlockClasses;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.EvgenWarGold.GregTechNightmare.Mixins.Late.MultiblockTooltipBuilderAccessor;
import com.EvgenWarGold.GregTechNightmare.Utils.Authors;
import com.google.common.collect.SetMultimap;

import gregtech.GTMod;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.StringUtils;

public class GTN_MultiBlockTooltipBuilder extends MultiblockTooltipBuilder {

    private static final String TT_hold = StatCollector.translateToLocal("GT5U.MBTT.Hold");
    private static final String TT_todisplay = StatCollector.translateToLocal("GT5U.MBTT.Display");
    private static final String TT_addedBy = StatCollector.translateToLocal("GT5U.MBTT.Mod");
    private static final String TT_machineType = StatCollector.translateToLocal("GT5U.MBTT.MachineType");
    private static final String TAB = "   ";
    private static final String COLON = ": ";
    private static final String SEPARATOR = ", ";
    private static final String TT_structurehint = StatCollector.translateToLocal("GT5U.MBTT.StructureHint");
    private static final String TT_steaminputbus = StatCollector.translateToLocal("GTPP.MBTT.SteamInputBus");
    private static final String TT_steamoutputbus = StatCollector.translateToLocal("GTPP.MBTT.SteamOutputBus");
    private static final String TT_steamhatch = StatCollector.translateToLocal("GTPP.MBTT.SteamHatch");
    private static final String TT_maintenancehatch = StatCollector.translateToLocal("GT5U.MBTT.MaintenanceHatch");
    private static final String TT_energyhatch = StatCollector.translateToLocal("GT5U.MBTT.EnergyHatch");
    private static final String TT_dynamohatch = StatCollector.translateToLocal("GT5U.MBTT.DynamoHatch");
    private static final String TT_mufflerhatch = StatCollector.translateToLocal("GT5U.MBTT.MufflerHatch");
    private static final String TT_inputbus = StatCollector.translateToLocal("GT5U.MBTT.InputBus");
    private static final String TT_inputhatch = StatCollector.translateToLocal("GT5U.MBTT.InputHatch");
    private static final String TT_outputbus = StatCollector.translateToLocal("GT5U.MBTT.OutputBus");
    private static final String TT_outputhatch = StatCollector.translateToLocal("GT5U.MBTT.OutputHatch");
    private static final String TT_tectechhatch = StatCollector.translateToLocal("GTN.TooltipBuilder.ExoticHatch");
    private static final String TT_EnergyOrTecTechHatch = StatCollector
        .translateToLocal("GTN.TooltipBuilder.ExoticOrEnergyHatch");
    private static final String TT_DynamoOrBufferedHatch = StatCollector
        .translateToLocal("GTN.TooltipBuilder.DynamoOrBufferedHatch");
    private static final String TT_dimensions = StatCollector.translateToLocal("GT5U.MBTT.Dimensions");
    private static final String TT_structure = StatCollector.translateToLocal("GT5U.MBTT.Structure");
    private static final String[] TT_dots = IntStream.range(0, 16)
        .mapToObj(i -> StatCollector.translateToLocal("structurelib.blockhint." + i + ".name"))
        .toArray(String[]::new);

    public void addInfoMultiLineTranslated(String key) {
        for (int i = 0; i <= 99; i++) {
            String langKey = key + "." + String.format("%02d", i);
            if (StatCollector.canTranslate(langKey)) {
                addInfo(StatCollector.translateToLocal(langKey));
            } else {
                break;
            }
        }
    }

    public MultiblockTooltipBuilder addMachineType(String machine) {
        MultiblockTooltipBuilderAccessor accessor = (MultiblockTooltipBuilderAccessor) this;
        List<String> iLines = accessor.getILines();
        iLines.add(
            EnumChatFormatting.GRAY + TT_machineType
                + COLON
                + EnumChatFormatting.YELLOW
                + machine
                + EnumChatFormatting.RESET);
        return this;
    }

    public MultiblockTooltipBuilder addInfo(String info) {
        MultiblockTooltipBuilderAccessor accessor = (MultiblockTooltipBuilderAccessor) this;
        List<String> iLines = accessor.getILines();
        iLines.add(EnumChatFormatting.GRAY + info);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addAuthor(Authors author) {
        addInfo("Author: " + author.name);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addExtraInfo(String extraInfo) {
        MultiblockTooltipBuilderAccessor accessor = (MultiblockTooltipBuilderAccessor) this;
        accessor.getSLines()
            .add(extraInfo);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addExtraInfoWithSpace(String info) {
        MultiblockTooltipBuilderAccessor accessor = (MultiblockTooltipBuilderAccessor) this;
        List<String> sLines = accessor.getSLines();
        sLines.add(EnumChatFormatting.GRAY + TAB + info);
        return this;
    }

    private GTN_MultiBlockTooltipBuilder addHatch(String key, int count, int dot) {
        MultiblockTooltipBuilderAccessor accessor = (MultiblockTooltipBuilderAccessor) this;
        List<String> sLines = accessor.getSLines();
        sLines.add(
            EnumChatFormatting.AQUA + TAB
                + key
                + EnumChatFormatting.YELLOW
                + " - at least "
                + EnumChatFormatting.RED
                + count
                + EnumChatFormatting.YELLOW
                + " in any hint dot "
                + EnumChatFormatting.RED
                + dot
                + EnumChatFormatting.YELLOW
                + " block");
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addSteamInputBus(int count, int dot) {
        addHatch(TT_steaminputbus, count, dot);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addSteamHatch(int count, int dot) {
        addHatch(TT_steamhatch, count, dot);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addSteamOutputBus(int count, int dot) {
        addHatch(TT_steamoutputbus, count, dot);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addEnergyHatch(int count, int dot) {
        addHatch(TT_energyhatch, count, dot);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addDynamoHatch(int count, int dot) {
        addHatch(TT_dynamohatch, count, dot);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addMaintenanceHatch(int count, int dot) {
        addHatch(TT_maintenancehatch, count, dot);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addMufflerHatch(int count, int dot) {
        addHatch(TT_mufflerhatch, count, dot);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addInputBus(int count, int dot) {
        addHatch(TT_inputbus, count, dot);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addInputHatch(int count, int dot) {
        addHatch(TT_inputhatch, count, dot);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addOutputBus(int count, int dot) {
        addHatch(TT_outputbus, count, dot);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addOutputHatch(int count, int dot) {
        addHatch(TT_outputhatch, count, dot);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addExoticEnergyHatch(int count, int dot) {
        addHatch(TT_tectechhatch, count, dot);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addExoticOrEnergyHatch(int count, int dot) {
        addHatch(TT_EnergyOrTecTechHatch, count, dot);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addDynamoOrBufferedHatch(int count, int dot) {
        addHatch(TT_DynamoOrBufferedHatch, count, dot);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addSteamHatch() {
        addSteamHatch(1, 1);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addSteamInputBus() {
        addSteamInputBus(1, 1);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addSteamOutputBus() {
        addSteamOutputBus(1, 1);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addEnergyHatch() {
        addEnergyHatch(1, 1);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addDynamoHatch() {
        addDynamoHatch(1, 1);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addMaintenanceHatch() {
        addMaintenanceHatch(1, 1);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addMufflerHatch() {
        addMufflerHatch(1, 1);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addInputBus() {
        addInputBus(1, 1);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addInputHatch() {
        addInputHatch(1, 1);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addOutputBus() {
        addOutputBus(1, 1);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addOutputHatch() {
        addOutputHatch(1, 1);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addExoticEnergyHatch() {
        addExoticEnergyHatch(1, 1);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addExoticOrEnergyHatch() {
        addExoticOrEnergyHatch(1, 1);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addDynamoOrBufferedHatch() {
        addDynamoOrBufferedHatch(1, 1);
        return this;
    }

    public MultiblockTooltipBuilder addMultiBlockAreaInfoWithName(String name, int w, int h, int l) {
        MultiblockTooltipBuilderAccessor accessor = (MultiblockTooltipBuilderAccessor) this;

        List<String> sLines = accessor.getSLines();

        sLines.add(
            EnumChatFormatting.GRAY + TT_dimensions
                + (name.isEmpty() ? "" : EnumChatFormatting.AQUA + " " + name + EnumChatFormatting.GRAY)
                + COLON
                + EnumChatFormatting.GOLD
                + w
                + EnumChatFormatting.GRAY
                + "x"
                + EnumChatFormatting.GOLD
                + h
                + EnumChatFormatting.GRAY
                + "x"
                + EnumChatFormatting.GOLD
                + l
                + EnumChatFormatting.GRAY
                + " ("
                + EnumChatFormatting.GOLD
                + "W"
                + EnumChatFormatting.GRAY
                + "x"
                + EnumChatFormatting.GOLD
                + "H"
                + EnumChatFormatting.GRAY
                + "x"
                + EnumChatFormatting.GOLD
                + "L"
                + EnumChatFormatting.GRAY
                + ")");
        return this;
    }

    public MultiblockTooltipBuilder addMultiBlockAreaInfo(int w, int h, int l) {
        addMultiBlockAreaInfoWithName("", w, h, l);
        return this;
    }

    public MultiblockTooltipBuilder beginStructureBlock() {
        MultiblockTooltipBuilderAccessor accessor = (MultiblockTooltipBuilderAccessor) this;
        List<String> sLines = accessor.getSLines();
        sLines.add(EnumChatFormatting.GRAY + TT_structure + COLON);
        return this;
    }

    public MultiblockTooltipBuilder toolTipFinisher(@Nullable String... authors) {
        return toolTipFinisher(EnumChatFormatting.GRAY, 41, authors);
    }

    public MultiblockTooltipBuilder toolTipFinisher(EnumChatFormatting separatorColor, int separatorLength,
        @Nullable String... authors) {
        MultiblockTooltipBuilderAccessor accessor = (MultiblockTooltipBuilderAccessor) this;

        List<String> iLines = accessor.getILines();
        List<String> hLines = accessor.getHLines();
        List<String> sLines = accessor.getSLines();
        String[] iArray = accessor.getIArray();
        String[] sArray = accessor.getSArray();
        String[] hArray = accessor.getHArray();
        SetMultimap<Integer, String> hBlocks = accessor.getHBlocks();

        switch (GTMod.proxy.tooltipFinisherStyle) {
            case 0 -> {}
            case 1 -> iLines.add(" ");
            case 2 -> iLines.add(separatorColor + StringUtils.getRepetitionOf('-', separatorLength));
            default -> iLines.add(
                separatorColor.toString() + EnumChatFormatting.STRIKETHROUGH
                    + StringUtils.getRepetitionOf('-', separatorLength));
        }

        iLines.add(
            TT_hold + " "
                + EnumChatFormatting.BOLD
                + "[LSHIFT]"
                + EnumChatFormatting.RESET
                + EnumChatFormatting.GRAY
                + " "
                + TT_todisplay);
        if (authors != null && authors.length > 0) {
            final String authorTag = "Author: ";
            final StringBuilder sb = new StringBuilder();
            sb.append(EnumChatFormatting.GRAY);
            sb.append(TT_addedBy);
            sb.append(COLON);
            for (int i = 0; i < authors.length; i++) {
                String author = authors[i];
                if (author.startsWith(authorTag)) {
                    // to support all the values in GTValues
                    // that already have Author at the start
                    sb.append(author.substring(authorTag.length()));
                } else {
                    sb.append(author);
                }
                if (i != authors.length - 1) {
                    sb.append(EnumChatFormatting.RESET);
                    sb.append(EnumChatFormatting.GRAY);
                    sb.append(" & ");
                    sb.append(EnumChatFormatting.GREEN);
                }
            }
            iLines.add(sb.toString());
        }
        hLines.add(TT_structurehint);
        this.addStructureInfoSeparator(EnumChatFormatting.GRAY, 30, true);
        // create the final arrays
        accessor.setIArray(iLines.toArray(new String[0]));
        accessor.setSArray(sLines.toArray(new String[0]));

        accessor.setHArray(
            Stream.concat(
                hLines.stream(),
                hBlocks.asMap()
                    .entrySet()
                    .stream()
                    .map(e -> TT_dots[e.getKey() - 1] + COLON + String.join(SEPARATOR, e.getValue())))
                .toArray(String[]::new));
        // free memory
        iLines = null;
        sLines = null;
        hLines = null;
        hBlocks = null;
        return this;
    }
}

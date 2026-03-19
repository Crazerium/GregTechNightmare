package com.EvgenWarGold.GregTechNightmare.GregTech.MultiBlock.NewMultiBlockClasses;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.EvgenWarGold.GregTechNightmare.Mixins.Late.MultiblockTooltipBuilderAccessor;
import com.google.common.collect.SetMultimap;

import gregtech.GTMod;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.StringUtils;

public class GTN_MultiBlockTooltipBuilder extends MultiblockTooltipBuilder {

    private static final String TT_hold = StatCollector.translateToLocal("GT5U.MBTT.Hold");
    private static final String TT_todisplay = StatCollector.translateToLocal("GT5U.MBTT.Display");
    private static final String TT_addedBy = StatCollector.translateToLocal("GT5U.MBTT.Mod");
    private static final String TAB = "   ";
    private static final String COLON = ": ";
    private static final String SEPARATOR = ", ";
    private static final String TT_structurehint = StatCollector.translateToLocal("GT5U.MBTT.StructureHint");
    private static final String TT_steaminputbus = StatCollector.translateToLocal("GTPP.MBTT.SteamInputBus");
    private static final String TT_steamoutputbus = StatCollector.translateToLocal("GTPP.MBTT.SteamOutputBus");
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

    public GTN_MultiBlockTooltipBuilder addAuthor(String author) {
        addInfo("Author: " + author);
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
        sLines.add(EnumChatFormatting.WHITE + TAB + info);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addSteamInputBus(int count, int dot) {
        MultiblockTooltipBuilderAccessor accessor = (MultiblockTooltipBuilderAccessor) this;
        List<String> sLines = accessor.getSLines();
        sLines.add(
            EnumChatFormatting.AQUA + TAB
                + TT_steaminputbus
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

    public GTN_MultiBlockTooltipBuilder addSteamOutputBus(int count, int dot) {
        MultiblockTooltipBuilderAccessor accessor = (MultiblockTooltipBuilderAccessor) this;
        List<String> sLines = accessor.getSLines();
        sLines.add(
            EnumChatFormatting.AQUA + TAB
                + TT_steamoutputbus
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

    public GTN_MultiBlockTooltipBuilder addSteamInputBus() {
        addSteamInputBus(1, 1);
        return this;
    }

    public GTN_MultiBlockTooltipBuilder addSteamOutputBus() {
        addSteamOutputBus(1, 1);
        return this;
    }



    public MultiblockTooltipBuilder beginStructureBlock(int w, int h, int l) {
        MultiblockTooltipBuilderAccessor accessor = (MultiblockTooltipBuilderAccessor) this;

        List<String> sLines = accessor.getSLines();

        sLines.add(
            EnumChatFormatting.WHITE + TT_dimensions
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
        sLines.add(EnumChatFormatting.WHITE + TT_structure + COLON);
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

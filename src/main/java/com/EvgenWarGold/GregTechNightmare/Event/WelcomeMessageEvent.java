package com.EvgenWarGold.GregTechNightmare.Event;

import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import com.EvgenWarGold.GregTechNightmare.Utils.Authors;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class WelcomeMessageEvent {

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        String playerName = event.player.getDisplayName();

        String[] lines = { EnumChatFormatting.GRAY + "=================================================",
            EnumChatFormatting.DARK_RED + "      GregTechNightmare " + "by " + Authors.EVGEN_WAR_GOLD.name,
            EnumChatFormatting.GRAY + "=================================================",
            EnumChatFormatting.YELLOW + "  Adds new multiblocks and tweaks to GTNH.",
            EnumChatFormatting.YELLOW + "  Makes some things easier, some things harder.",
            EnumChatFormatting.YELLOW + "  All for more fun and chaos!",
            EnumChatFormatting.GRAY + "=================================================",
            EnumChatFormatting.GREEN + "  Enjoy the nightmare, " + playerName + " Play with style!",
            EnumChatFormatting.GRAY + "=================================================" };

        for (String line : lines) {
            event.player.addChatMessage(new ChatComponentText(line));
        }

        ChatComponentText githubLabel = new ChatComponentText(EnumChatFormatting.WHITE + "  GitHub: ");
        ChatComponentText githubLink = new ChatComponentText(EnumChatFormatting.RED + "Click to open");
        githubLink.setChatStyle(
            new ChatStyle().setChatClickEvent(
                new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/evgengoldwar/GregTechNightmare")));

        githubLink.getChatStyle()
            .setChatHoverEvent(
                new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    new ChatComponentText(EnumChatFormatting.YELLOW + "Click to open GitHub repository")));

        ChatComponentText githubMessage = new ChatComponentText("");
        githubMessage.appendSibling(githubLabel);
        githubMessage.appendSibling(githubLink);
        event.player.addChatMessage(githubMessage);
        event.player.addChatMessage(
            new ChatComponentText(EnumChatFormatting.GRAY + "================================================="));
    }
}

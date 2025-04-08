package com.dmu.wikiwindowopener.item;

import com.dmu.wikiwindowopener.screens.WikiScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class WikiOpener extends Item {
    public WikiOpener(Settings settings) {
        super(settings);
        settings.component(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true));
    }
    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) {
            // Create a clickable chat message with a link
            Text linkText = Text.literal("Click here to open the Minecraft Wiki")
                    .styled(style -> style
                            .withColor(Formatting.AQUA)
                            .withUnderline(true)
                            .withClickEvent(new net.minecraft.text.ClickEvent(
                                    net.minecraft.text.ClickEvent.Action.OPEN_URL,
                                    "https://minecraft.wiki/"
                            ))
                            .withHoverEvent(new net.minecraft.text.HoverEvent(
                                    net.minecraft.text.HoverEvent.Action.SHOW_TEXT,
                                    Text.literal("Opens the official Minecraft Wiki")
                            ))
                    );

            user.sendMessage(linkText, false); // false: not an action bar message
            user.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, 1.0F, 1.0F);
        }

        return ActionResult.SUCCESS;
    }


    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("Right Click any Block using this tool to open its minecraft Wiki page!").formatted(Formatting.GOLD));
    }
}

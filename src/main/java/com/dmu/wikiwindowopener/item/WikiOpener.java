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
import net.minecraft.util.Util;
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
            user.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, 1.0F, 1.0F);

            String url = "https://minecraft.wiki/";
            MinecraftClient client = MinecraftClient.getInstance();
            Util.getOperatingSystem().open(url);
            
        }

        return ActionResult.SUCCESS;
    }



    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("Right Click any Block using this tool to open its minecraft Wiki page!").formatted(Formatting.GOLD));
    }
}

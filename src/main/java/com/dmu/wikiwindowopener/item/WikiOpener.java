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

            MinecraftClient client = MinecraftClient.getInstance();

            // If used in the offhand, get item from main hand
            if (hand == Hand.OFF_HAND) {
                ItemStack mainHandStack = user.getMainHandStack();
                if (!mainHandStack.isEmpty()) {
                    var id = net.minecraft.registry.Registries.ITEM.getId(mainHandStack.getItem());

                    String itemName = id.getPath().replace('_', ' ');
                    itemName = capitalizeWords(itemName);
                    String url = "https://minecraft.wiki/wiki/" + itemName;

                    Util.getOperatingSystem().open(url);
                    return ActionResult.SUCCESS;
                } else {
                    // Fall back to homepage if main hand is empty
                    Util.getOperatingSystem().open("https://minecraft.wiki/");
                    return ActionResult.SUCCESS;
                }
            }

            // Normal behavior: block lookup
            var hitResult = client.crosshairTarget;
            if (hitResult != null && hitResult.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK) {
                var blockHitResult = (net.minecraft.util.hit.BlockHitResult) hitResult;
                var pos = blockHitResult.getBlockPos();
                var state = world.getBlockState(pos);
                var block = state.getBlock();
                var id = net.minecraft.registry.Registries.BLOCK.getId(block);

                String blockName = id.getPath().replace('_', ' ');
                blockName = capitalizeWords(blockName);
                String url = "https://minecraft.wiki/wiki/" + blockName;

                Util.getOperatingSystem().open(url);
            } else {
                Util.getOperatingSystem().open("https://minecraft.wiki/");
            }
        }

        return ActionResult.SUCCESS;
    }


    private String capitalizeWords(String input) {
        String[] words = input.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append("_");  // Use underscore instead of space for actual Wiki URL
            }
        }
        return sb.toString().replaceAll("_$", ""); // Remove trailing underscore
    }





    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("Right Click any Block using this tool to open its Minecraft Wiki page!").formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("Use in offhand to look up the item in your main hand.").formatted(Formatting.GRAY));
    }

}

package com.dmu.wikiwindowopener.item;

import net.minecraft.client.MinecraftClient;
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

                    String itemName = getCorrectedWikiName(id.getPath());

                    String url = "https://minecraft.wiki/wiki/" + itemName;

                    openWikiUrl(url);  // Open the URL and handle any exceptions
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

                // Check if the block is a liquid (like water)
                if (block.getDefaultState().isLiquid()) {
                    // If the player is sneaking, ignore the liquid
                    if (!user.isSneaking()) {
                        String fluidName = capitalizeWords(block.asItem().getName().getString());
                        String url = "https://minecraft.wiki/wiki/" + fluidName;
                        openWikiUrl(url);  // Open the fluid's wiki URL
                    }
                    return ActionResult.SUCCESS;
                }

                var id = net.minecraft.registry.Registries.BLOCK.getId(block);
                String blockName = id.getPath().replace('_', ' ');
                blockName = capitalizeWords(blockName);
                String url = "https://minecraft.wiki/wiki/" + blockName;

                openWikiUrl(url);  // Open the block's wiki URL
            } else {
                openWikiUrl("https://minecraft.wiki/");  // Open the general Wiki URL if nothing is hit
            }
        }

        return ActionResult.SUCCESS;
    }

    // Helper method for opening URLs safely
    private void openWikiUrl(String url) {
        try {
            Util.getOperatingSystem().open(url);
        } catch (Exception e) {
            System.err.println("Failed to open Wiki URL: " + url);
        }
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

    private String getCorrectedWikiName(String itemIdPath) {
        return switch (itemIdPath) {
            case "flint_and_steel" -> "Flint_and_Steel";
            case "enchanted_book" -> "Enchanted_Book"; //switch needs second case
            default -> capitalizeWords(itemIdPath.replace('_', ' '));
        };
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("Right Click any Block using this tool to open its Minecraft Wiki page!").formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("Use in offhand to look up the item in your main hand.").formatted(Formatting.GRAY));
    }
}

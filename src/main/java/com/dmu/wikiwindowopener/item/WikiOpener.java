package com.dmu.wikiwindowopener.item;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.List;

public class WikiOpener extends Item {
    public WikiOpener(Settings settings) {
        super(settings);
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

            // First, check if the player is sneaking. If not, try to raycast for fluids.
            if (!user.isSneaking()) {
                // Perform custom ray tracing for fluid
                HitResult result = rayTrace(user, 5.0);  // 5.0 is the reach distance

                if (result != null && result.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult blockHitResult = (BlockHitResult) result;
                    var pos = blockHitResult.getBlockPos();
                    var state = world.getBlockState(pos);

                    // Raycasting for fluid (check if the ray hit a fluid)
                    Fluid fluid = state.getFluidState().getFluid();
                    if (fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER) {
                        String url = "https://minecraft.wiki/w/Water";
                        openWikiUrl(url);  // Open the fluid's wiki URL
                        return ActionResult.SUCCESS;
                    } else if (fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA) {
                        String url = "https://minecraft.wiki/w/Lava";
                        openWikiUrl(url);  // Open the fluid's wiki URL
                        return ActionResult.SUCCESS;
                    }
                }
            }

            // Fall back to normal behavior if no fluid was detected or if sneaking
            // Fall back to normal behavior if no fluid was detected or if sneaking
            // Fall back to normal behavior if no fluid was detected or if sneaking
            var hitResult = client.crosshairTarget;
            if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHitResult = (BlockHitResult) hitResult;
                var pos = blockHitResult.getBlockPos();
                var state = world.getBlockState(pos);
                var block = state.getBlock();

                // Get block name without "minecraft:" and capitalize it
                String blockName = block.getTranslationKey().replace("block.minecraft.", "").replace('_', ' ');
                blockName = capitalizeWords(blockName);
                String url = "https://minecraft.wiki/wiki/" + blockName;
                openWikiUrl(url);
                return ActionResult.SUCCESS;
            }
            else if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
                // Handle entity-based lookups if needed
                String entityName = hitResult.getClass().getName();
                String url = "https://minecraft.wiki/wiki/" + entityName;
                openWikiUrl(url);
                return ActionResult.SUCCESS;
            }

            // Default to opening the general Wiki page if nothing is hit
            openWikiUrl("https://minecraft.wiki/");
        }

        return ActionResult.SUCCESS;
    }



    // Custom rayTrace method for detecting blocks or fluids

    public HitResult rayTrace(Entity entity, double playerReach) {
        Vec3d eyePosition = entity.getEyePos();  // Eye position of the entity (player)
        Vec3d lookVector = entity.getRotationVector().multiply(playerReach);  // Look direction, multiplied by reach distance
        Vec3d traceEnd = eyePosition.add(lookVector);  // End position of the ray trace

        // Raycast context to only consider fluids
        RaycastContext.FluidHandling fluidView = RaycastContext.FluidHandling.SOURCE_ONLY;
        RaycastContext context = new RaycastContext(eyePosition, traceEnd, RaycastContext.ShapeType.OUTLINE, fluidView, entity);

        // Perform the ray trace
        return entity.getEntityWorld().raycast(context);
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
        tooltip.add(Text.translatable("Right-click a block for its Wiki.").formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("Offhand: Lookup main-hand item.").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("Sneak: Ignore fluids & usesable items in main hand.").formatted(Formatting.GRAY));

    }
}

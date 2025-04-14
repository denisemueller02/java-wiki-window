package com.dmu.wikiwindowopener.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.resource.Resource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
                    String itemName = getWikiPageFromItemName(mainHandStack, user);
                    openWikiUrl(user, itemName);
                    return ActionResult.SUCCESS;
                }
            }


            if (!user.isSneaking()) {
                HitResult result = rayTrace(user, 5.0);

                if (result != null && result.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult blockHitResult = (BlockHitResult) result;
                    var pos = blockHitResult.getBlockPos();
                    var state = world.getBlockState(pos);

                    Fluid fluid = state.getFluidState().getFluid();
                    if (fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER) {
                        openWikiUrl(user, "Water");
                        return ActionResult.SUCCESS;
                    } else if (fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA) {
                        openWikiUrl(user, "Lava");
                        return ActionResult.SUCCESS;
                    }
                }
            }

            var hitResult = client.crosshairTarget;
            if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHitResult = (BlockHitResult) hitResult;
                var pos = blockHitResult.getBlockPos();
                var state = world.getBlockState(pos);
                var block = state.getBlock();

                String blockName = block.getTranslationKey().replace("block.minecraft.", "").replace('_', ' ');
                blockName = capitalizeWords(blockName);
                openWikiUrl(user, blockName);
                return ActionResult.SUCCESS;
            } else if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
                var entityHitResult = (EntityHitResult) hitResult;
                var entity = entityHitResult.getEntity();

                String entityName = entity.getType().toString()
                        .replace("minecraft:", "")
                        .replace("entity.minecraft.", "")
                        .replace('_', ' ');

                entityName = capitalizeWords(entityName);

                openWikiUrl(user, entityName);
                return ActionResult.SUCCESS;
            }

            openWikiUrl(user, "https://minecraft.wiki/");
        }

        return ActionResult.SUCCESS;
    }

    public HitResult rayTrace(Entity entity, double playerReach) {
        Vec3d eyePosition = entity.getEyePos();
        Vec3d lookVector = entity.getRotationVector().multiply(playerReach);
        Vec3d traceEnd = eyePosition.add(lookVector);

        RaycastContext.FluidHandling fluidView = RaycastContext.FluidHandling.SOURCE_ONLY;
        RaycastContext context = new RaycastContext(eyePosition, traceEnd, RaycastContext.ShapeType.OUTLINE, fluidView, entity);

        return entity.getEntityWorld().raycast(context);
    }

    private void openWikiUrl(PlayerEntity player, String pageTitle) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            String languageCode = client.options.language;
            String baseUrl = languageCode.equalsIgnoreCase("de_de") ? "https://de.minecraft.wiki/w/" : "https://minecraft.wiki/wiki/";
            Util.getOperatingSystem().open(baseUrl + pageTitle);
        } catch (Exception e) {
            System.err.println("Failed to open Wiki URL for page: " + pageTitle);
        }
    }

    private String getWikiPageFromItemName(ItemStack stack, PlayerEntity player) {
        MinecraftClient client = MinecraftClient.getInstance();
        String language = client.options.language;

        // Always get the translation key (like "item.minecraft.iron_pickaxe")
        String translationKey = stack.getItem().getTranslationKey();

        if (language.equalsIgnoreCase("de_de")) {
            // Just resolve the localized name normally
            String translated = Text.translatable(translationKey).getString();
            return translated.replace(' ', '_');
        } else {
            // Use registry ID for English or fallback
            var id = net.minecraft.registry.Registries.ITEM.getId(stack.getItem());
            return getCorrectedWikiName(id.getPath());
        }
    }


    private String capitalizeWords(String input) {
        String[] words = input.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append("_");
            }
        }
        return sb.toString().replaceAll("_$", "");
    }

    private String getCorrectedWikiName(String itemIdPath) {
        return switch (itemIdPath) {
            case "flint_and_steel" -> "Flint_and_Steel";
            case "enchanted_book" -> "Enchanted_Book";
            default -> capitalizeWords(itemIdPath.replace('_', ' '));
        };
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.wikiopener.tooltip.use").formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("item.wikiopener.tooltip.offhand").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.wikiopener.tooltip.sneak").formatted(Formatting.GRAY));
    }
}
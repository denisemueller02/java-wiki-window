package com.dmu.wikiwindowopener.mixin;

import com.dmu.wikiwindowopener.item.WikiOpener;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.Util;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
    private void onUseItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (hand == Hand.MAIN_HAND &&
                player.isSneaking() &&
                player.getOffHandStack().getItem() instanceof WikiOpener) {

            ItemStack main = player.getMainHandStack();
            if (!main.isEmpty()) {
                Identifier id = Registries.ITEM.getId(main.getItem());
                String itemName = getCorrectedWikiName(id.getPath());
                String url = "https://minecraft.wiki/wiki/" + itemName;

                net.minecraft.util.Util.getOperatingSystem().open(url); // correct Util
                player.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, 1.0F, 1.0F);
                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }

    @Unique
    private String getCorrectedWikiName(String path) {
        return switch (path) {
            case "flint_and_steel" -> "Flint_and_Steel";
            case "ender_pearl" -> "Ender_Pearl";
            default -> capitalizeWords(path.replace('_', ' '));
        };
    }

    @Unique
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
}


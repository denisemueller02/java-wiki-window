package com.dmu.wikiwindowopener.item;

import com.dmu.wikiwindowopener.WikiOpenerClient;
import com.dmu.wikiwindowopener.WikiWindowOpener;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ModItems {
    public static final Item.Settings settings = new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("wikiwindowopener", "wiki_opener")));
    public static final Item WIKI_OPENER = registerItem("wiki_opener", new Item(settings));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(WikiWindowOpener.MOD_ID, name), item);
    };
    public static void register() {
        WikiWindowOpener.LOGGER.info("Registering ModItems"+ WikiWindowOpener.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(WIKI_OPENER);
        });
    }

    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        user.playSound(SoundEvents.BLOCK_WOOL_BREAK, 1.0F, 1.0F);
        return ActionResult.SUCCESS;
    }
}

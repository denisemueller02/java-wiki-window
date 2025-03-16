package com.dmu.wikiwindowopener.item;


import com.dmu.wikiwindowopener.WikiWindowOpener;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import net.minecraft.util.Identifier;

import java.util.function.Function;


public class ModItems {
/*
    //Method that is called when loading the mod
    public static void register() {
        WikiWindowOpener.LOGGER.info("Registering ModItems" + WikiWindowOpener.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(WIKI_OPENER);
        });
    }
    //Preparing Settings for Item
    public static final Item.Settings settings = new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(WikiWindowOpener.MOD_ID, "wiki_opener")));

    //Creating Item instance
    public static final WikiOpener WIKI_OPENER = registerItem("wiki_opener", new WikiOpener(settings));

    //registering the Item to the Creative Menu
    private static WikiOpener registerItem(String name, WikiOpener item) {
        return Registry.register(Registries.ITEM, Identifier.of(WikiWindowOpener.MOD_ID, name), item);
    }
*/

    public static final WikiOpener WIKI_OPENER = register("wiki_opener", WikiOpener::new, new Item.Settings());

    public static WikiOpener register(String path, Function<Item.Settings, Item> factory, Item.Settings settings) {
        final RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of("wikiwindowopener", path));
        return (WikiOpener) Items.register(registryKey, factory, settings);
    }
    ;

    public static void initialize() {

    }



}

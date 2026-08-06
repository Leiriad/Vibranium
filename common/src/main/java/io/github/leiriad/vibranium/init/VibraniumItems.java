package io.github.leiriad.vibranium.init;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.item.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

import java.util.List;

public class VibraniumItems {
    private static DeferredRegister<Item> ITEMS = DeferredRegister.create(VibraniumMod.MOD_ID, Registries.ITEM);

    //INGREDIENTS
    public static final RegistrySupplier<Item> VIBRANIUM_DUST = ITEMS.register("vibranium_dust", () -> {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "vibranium_dust"));

        return new VibraniumDust(new Item.Properties().setId(key));
    });
    public static final RegistrySupplier<Item> BLACK_CLAY_BALL = ITEMS.register("black_clay_ball", () -> {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "black_clay_ball"));

        return new BlackClayBall(new Item.Properties().setId(key));
    });
    public static final RegistrySupplier<Item> HEART_SHAPED_HERB = ITEMS.register("heart_shaped_herb", () -> {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "heart_shaped_herb"));

        return new HeartShapedHerb(HeartShapedHerb.getProperties().setId(key));
    });
    public static final RegistrySupplier<Item> VIBRANIUM_INGOT = ITEMS.register("vibranium_ingot", () -> {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "vibranium_ingot"));

        return new VibraniumIngot(new Item.Properties().setId(key));
    });
    public static final RegistrySupplier<Item> DEPLETED_VIBRANIUM_INGOT = ITEMS.register("depleted_vibranium_ingot", () -> {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "depleted_vibranium_ingot"));

        return new DepletedVibraniumIngot(new Item.Properties().setId(key));
    });
    public static final RegistrySupplier<Item> BLACK_BRICK = ITEMS.register("black_brick", () -> {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "black_brick"));

        return new BlackBrick(new Item.Properties().setId(key));
    });

    //TOOLS & UTILITIES
    public static final RegistrySupplier<Item> HOT_WATER_BUCKET = ITEMS.register("hot_water_bucket", () -> {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "hot_water_bucket"));

        return new HotWaterBucket(VibraniumFluids.HOT_WATER_STILL, HotWaterBucket.getProperties().setId(key));
    });
    public static final RegistrySupplier<Item> VIBRANIUM_SHOVEL = ITEMS.register("vibranium_shovel", () -> {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "vibranium_shovel"));

        return new VibraniumShovel(VibraniumShovel.getProperties(new Item.Properties()).setId(key));
    });
    public static final RegistrySupplier<Item> VIBRANIUM_PICKAXE = ITEMS.register("vibranium_pickaxe", () -> {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "vibranium_pickaxe"));

        return new VibraniumPickaxe(VibraniumPickaxe.getProperties(new Item.Properties()).setId(key));
    });

    //FOOD & DRINKS
    public static final RegistrySupplier<Item> BLUE_SOUP = ITEMS.register("blue_soup", () -> {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "blue_soup"));

        return new BlueSoup(BlueSoup.getProperties().setId(key));
    });
    public static final RegistrySupplier<Item> ANCESTRAL_NECTAR = ITEMS.register("ancestral_nectar", () -> {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "ancestral_nectar"));

        return new AncestralNectar(AncestralNectar.getProperties().setId(key));
    });
    public static final RegistrySupplier<Item> BLUE_BERRY_SPIRIT = ITEMS.register("blue_berry_spirit", () -> {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "blue_berry_spirit"));

        return new BlueBerrySpirit(BlueBerrySpirit.getProperties().setId(key).component(DataComponents.LORE, new ItemLore(List.of(
                Component.translatable("tooltip.vibranium.distilled_alcohol")
        ))));
    });

    //COMBAT
    public static final RegistrySupplier<Item> VIBRANIUM_SPEAR= ITEMS.register("vibranium_spear", () -> {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "vibranium_spear"));

        return new VibraniumSpear(VibraniumSpear.getProperties(new Item.Properties()).setId(key));
    });
    public static final RegistrySupplier<Item> VIBRANIUM_SWORD= ITEMS.register("vibranium_sword", () -> {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "vibranium_sword"));

        return new VibraniumSword(VibraniumSword.getProperties(new Item.Properties()).setId(key));
    });

    ///Registers items in the game
    public static void registerModItems() {
        ITEMS.register();
    }

    public static void addItemsToTabs() {
        //Add objets to creative tabs
        //INGREDIENTS
        List.of(
                VIBRANIUM_DUST,
                BLACK_CLAY_BALL,
                HEART_SHAPED_HERB,
                VIBRANIUM_INGOT,
                DEPLETED_VIBRANIUM_INGOT,
                BLACK_BRICK
        ).forEach(supplier -> {
            CreativeTabRegistry.appendStack(CreativeModeTabs.INGREDIENTS, () -> new ItemStack(supplier.get()));
        });

        //TOOLS & UTILITIES
        List.of(
                HOT_WATER_BUCKET,
                VIBRANIUM_SHOVEL,
                VIBRANIUM_PICKAXE
        ).forEach(supplier -> {
            CreativeTabRegistry.appendStack(CreativeModeTabs.TOOLS_AND_UTILITIES, () -> new ItemStack(supplier.get()));
        });

        //FOOD & DRINKS
        List.of(
                BLUE_SOUP,
                BLUE_BERRY_SPIRIT,
                ANCESTRAL_NECTAR
        ).forEach(supplier -> {
            CreativeTabRegistry.appendStack(CreativeModeTabs.FOOD_AND_DRINKS, () -> new ItemStack(supplier.get()));
        });

        //COMBAT
        List.of(
                VIBRANIUM_SPEAR,
                VIBRANIUM_SWORD
        ).forEach(supplier -> {
            CreativeTabRegistry.appendStack(CreativeModeTabs.COMBAT, () -> new ItemStack(supplier.get()));
        });

        //VIBRANIUM TAB
        List.of(
                VIBRANIUM_DUST,
                BLACK_CLAY_BALL,
                HEART_SHAPED_HERB,
                HOT_WATER_BUCKET,
                VIBRANIUM_INGOT,
                DEPLETED_VIBRANIUM_INGOT,
                BLACK_BRICK,
                BLUE_SOUP,
                BLUE_BERRY_SPIRIT,
                ANCESTRAL_NECTAR,
                VIBRANIUM_SPEAR,
                VIBRANIUM_SWORD,
                VIBRANIUM_PICKAXE,
                VIBRANIUM_SHOVEL
        ).forEach(supplier -> {
            CreativeTabRegistry.appendStack(VibraniumCreativeTabs.VIBRANIUM_TAB, () -> new ItemStack(supplier.get()));
        });
    }
}

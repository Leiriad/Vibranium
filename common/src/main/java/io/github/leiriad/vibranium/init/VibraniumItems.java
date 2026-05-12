package io.github.leiriad.vibranium.init;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.item.BlackClayBall;
import io.github.leiriad.vibranium.item.HeartShapedHerb;
import io.github.leiriad.vibranium.item.VibraniumDust;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class VibraniumItems {
    private static DeferredRegister<Item> ITEMS = DeferredRegister.create(VibraniumMod.MOD_ID, Registries.ITEM);

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

    ///Registers items in the game
    public static void registerModItems() {
        ITEMS.register();
    }

    public static void addItemsToTabs() {
        //Add objets to creative tabs
        List.of(
                VIBRANIUM_DUST,
                BLACK_CLAY_BALL,
                HEART_SHAPED_HERB
        ).forEach(supplier -> {
            CreativeTabRegistry.appendStack(CreativeModeTabs.INGREDIENTS, () -> new ItemStack(supplier.get()));
        });
    }
}

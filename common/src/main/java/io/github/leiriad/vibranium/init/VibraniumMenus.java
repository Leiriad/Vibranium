package io.github.leiriad.vibranium.init;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.menu.ReactorControlPanelMenu;
import io.github.leiriad.vibranium.menu.ReactorHatchMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;

public class VibraniumMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(VibraniumMod.MOD_ID, Registries.MENU);
    public static final RegistrySupplier<MenuType<ReactorControlPanelMenu>> REACTOR_CONTROL_PANEL_MENU =
            MENUS.register("reactor_control_panel", () -> MenuRegistry.ofExtended((id, inv, buf) -> {
                return new ReactorControlPanelMenu(id, inv, buf);
            }));
    public static final RegistrySupplier<MenuType<ReactorHatchMenu>> REACTOR_HATCH_MENU =
            MENUS.register("reactor_hatch", () -> MenuRegistry.ofExtended((id, inv, buf) -> {
                return new ReactorHatchMenu(id, inv, buf);
            }));

    public static void registerModMenus() {
        MENUS.register();
    }
}

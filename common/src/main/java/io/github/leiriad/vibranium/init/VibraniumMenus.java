package io.github.leiriad.vibranium.init;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.menu.ReactorControlPanelMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;

public class VibraniumMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(VibraniumMod.MOD_ID, Registries.MENU);
    public static final RegistrySupplier<MenuType<ReactorControlPanelMenu>> REACTOR_CONTROL_PANEL_MENU =
            MENUS.register("reactor_control_panel", () -> MenuRegistry.ofExtended((id, inv, buf) -> {
                BlockPos pos = buf.readBlockPos();
                return new ReactorControlPanelMenu(id, inv, pos);
            }));

    public static void registerModMenus() {
        MENUS.register();
    }
}

package io.github.leiriad.vibranium.init;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import io.github.leiriad.vibranium.event.ArmorKineticChargeHandler;
import io.github.leiriad.vibranium.event.WeaponKineticChargeHandler;

public class VibraniumEvents {

    public static void registerEvents() {
        EntityEvent.LIVING_HURT.register((entity, source, amount) -> {
            //Check active weapon parry or passive weapon charge
            EventResult weaponResult = WeaponKineticChargeHandler.handleWeaponCharge(entity, source, amount);

            //If the weapon successfully parried (returned interruptFalse), stop processing armor damage
            if (weaponResult.isFalse()) {
                return weaponResult;
            }

            //Else Process armor kinetic distribution and auto-discharge
            return ArmorKineticChargeHandler.handleArmorCharge(entity, source, amount);
        });
    }
}
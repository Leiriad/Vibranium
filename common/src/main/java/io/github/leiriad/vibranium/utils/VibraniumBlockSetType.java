package io.github.leiriad.vibranium.utils;

import io.github.leiriad.vibranium.sound.VibraniumSoundType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class VibraniumBlockSetType {
    public static final BlockSetType VIBRANIUM = new BlockSetType(
            "vibranium",
            false, // canOpenByHand
            false, // canOpenByWindCharge
            false, // canButtonBeActivatedByArrows
            BlockSetType.PressurePlateSensitivity.EVERYTHING,
            VibraniumSoundType.VIBRANIUM_BLOCK,
            SoundEvents.IRON_DOOR_CLOSE,
            SoundEvents.IRON_DOOR_OPEN,
            SoundEvents.IRON_TRAPDOOR_CLOSE,
            SoundEvents.IRON_TRAPDOOR_OPEN,
            SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF,
            SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON,
            SoundEvents.STONE_BUTTON_CLICK_OFF,
            SoundEvents.STONE_BUTTON_CLICK_ON
    );
    public static final BlockSetType DEPLETED_VIBRANIUM = new BlockSetType(
            "depleted_vibranium",
            false,
            false,
            false,
            BlockSetType.PressurePlateSensitivity.EVERYTHING,
            VibraniumSoundType.DEPLETED_VIBRANIUM_BLOCK,
            SoundEvents.IRON_DOOR_CLOSE,
            SoundEvents.IRON_DOOR_OPEN,
            SoundEvents.IRON_TRAPDOOR_CLOSE,
            SoundEvents.IRON_TRAPDOOR_OPEN,
            SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF,
            SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON,
            SoundEvents.STONE_BUTTON_CLICK_OFF,
            SoundEvents.STONE_BUTTON_CLICK_ON
    );
}
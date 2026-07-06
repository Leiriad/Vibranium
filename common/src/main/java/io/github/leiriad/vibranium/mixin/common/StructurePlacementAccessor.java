package io.github.leiriad.vibranium.mixin.common;

import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StructurePlacement.class)
public interface StructurePlacementAccessor {

    // This grants public access to the protected 'salt' field/method
    @Accessor("salt")
    int getSalt();
}

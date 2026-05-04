package io.github.leiriad.vibranium.init;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.structure.MeteoritePiece;
import io.github.leiriad.vibranium.structure.MeteoriteStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class VibraniumStructures {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(VibraniumMod.MOD_ID,Registries.STRUCTURE_TYPE);

    public static final DeferredRegister<StructurePieceType> PIECE_TYPES =
            DeferredRegister.create(VibraniumMod.MOD_ID,Registries.STRUCTURE_PIECE);

    // Meteorite placement construction
    public static final RegistrySupplier<StructureType<MeteoriteStructure>> METEORITE =
            STRUCTURE_TYPES.register("meteorite", () -> (StructureType<MeteoriteStructure>) () -> MeteoriteStructure.CODEC);

    // Meteorite building logics
    public static final RegistrySupplier<StructurePieceType> METEORITE_PIECE =
            PIECE_TYPES.register("meteorite_piece", () -> MeteoritePiece::new);

    public static void register() {
        STRUCTURE_TYPES.register();
        PIECE_TYPES.register();
    }
}

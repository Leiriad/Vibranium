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

    //Meteorites types getter
    private static StructureType<MeteoriteStructure> getSmallType() { return METEORITE_SMALL.get(); }
    private static StructureType<MeteoriteStructure> getMediumType() { return METEORITE_MEDIUM.get(); }
    private static StructureType<MeteoriteStructure> getBigType() { return METEORITE_BIG.get(); }
    private static StructureType<MeteoriteStructure> getHugeType() { return METEORITE_HUGE.get(); }

    // Meteorite placement construction
    public static final RegistrySupplier<StructureType<MeteoriteStructure>> METEORITE_SMALL =
            STRUCTURE_TYPES.register("meteorite_small",
                    () -> () -> MeteoriteStructure.directCodec(VibraniumStructures::getSmallType));
    public static final RegistrySupplier<StructureType<MeteoriteStructure>> METEORITE_MEDIUM =
            STRUCTURE_TYPES.register("meteorite_medium",
                    () -> () -> MeteoriteStructure.directCodec(VibraniumStructures::getMediumType));

    public static final RegistrySupplier<StructureType<MeteoriteStructure>> METEORITE_BIG =
            STRUCTURE_TYPES.register("meteorite_big",
                    () -> () -> MeteoriteStructure.directCodec(VibraniumStructures::getBigType));

    public static final RegistrySupplier<StructureType<MeteoriteStructure>> METEORITE_HUGE =
            STRUCTURE_TYPES.register("meteorite_huge",
                    () -> () -> MeteoriteStructure.directCodec(VibraniumStructures::getHugeType));

    // Meteorite building logics
    public static final RegistrySupplier<StructurePieceType> METEORITE_PIECE =
            PIECE_TYPES.register("meteorite_piece", () -> MeteoritePiece::new);

    public static void register() {
        STRUCTURE_TYPES.register();
        PIECE_TYPES.register();
    }
}

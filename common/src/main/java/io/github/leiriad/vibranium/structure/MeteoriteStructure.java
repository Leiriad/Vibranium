package io.github.leiriad.vibranium.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.leiriad.vibranium.init.VibraniumStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.Optional;
import java.util.function.Supplier;

public class MeteoriteStructure extends Structure {
    //PROPERTIES
    // Codec is used to register parameters
    public static final MapCodec<MeteoriteStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    settingsCodec(instance),
                    Codec.INT.fieldOf("min_radius").forGetter(s -> s.minRadius),
                    Codec.INT.fieldOf("max_radius").forGetter(s -> s.maxRadius)
            ).apply(instance, (settings, min, max) -> {
                return new MeteoriteStructure(settings, min, max, null);
            })
    );

    private final StructureType<MeteoriteStructure> type;
    private final int minRadius;
    private final int maxRadius;

    //CONSTRUCTOR
    public MeteoriteStructure(StructureSettings settings, int minRadius, int maxRadius, StructureType<MeteoriteStructure> type) {
        super(settings);
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.type = type;
    }

    public static MapCodec<MeteoriteStructure> directCodec(Supplier<StructureType<MeteoriteStructure>> typeSupplier) {
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        settingsCodec(instance),
                        Codec.INT.fieldOf("min_radius").forGetter(s -> s.minRadius),
                        Codec.INT.fieldOf("max_radius").forGetter(s -> s.maxRadius)
                ).apply(instance, (settings, min, max) ->
                        new MeteoriteStructure(settings, min, max, typeSupplier.get()))
        );
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        // Meterite center is defined relatively to chunk center
        RandomSource random = context.random();
        ChunkPos chunkPos = context.chunkPos();
        int checkX = chunkPos.getMiddleBlockX();
        int checkZ = chunkPos.getMiddleBlockZ();

        int radius = minRadius + random.nextInt(maxRadius - minRadius + 1);

        int surfaceY = context.chunkGenerator().getFirstOccupiedHeight(checkX, checkZ, Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());
        int minY = context.heightAccessor().getMinY();

        //Choose random Y position between surface and bedrock. The geode's X origin is calculated relatively to the chunk's center, with a vertical offset to bury it in the ground
        int range = Math.max(1, surfaceY - minY);
        int originY = minY + random.nextInt(range);

        BlockPos tempOrigin;
        NoiseColumn column = context.chunkGenerator().getBaseColumn(checkX, checkZ, context.heightAccessor(), context.randomState());
        BlockState groundState = column.getBlock(surfaceY - 1);

        // Erosion logics
        if (originY >= surfaceY - radius) {
            // Meteorites that are at the surface are buried at 75%
            int finalY = surfaceY - (int)(radius * 0.75);
            tempOrigin = new BlockPos(checkX, finalY, checkZ);

            // Specifics for End
            Holder<Biome> biome = context.biomeSource().getNoiseBiome(QuartPos.fromBlock(checkX), QuartPos.fromBlock(finalY), QuartPos.fromBlock(checkZ), context.randomState().sampler());
            if (biome.is(BiomeTags.IS_END)) {
                if (surfaceY < 15 || groundState.isAir()) return Optional.empty();
            } else {
                if (groundState.isAir() || groundState.is(Blocks.WATER) || groundState.is(BlockTags.REPLACEABLE)) return Optional.empty();
            }
        }
        // Underground
        else {
            tempOrigin = new BlockPos(checkX, originY, checkZ);
        }

        // Avoiding Bedrock
        if (tempOrigin.getY() - radius < minY) {
            tempOrigin = new BlockPos(checkX, minY + radius, checkZ);
        }

        //Final validity check
        final BlockPos finalOrigin = tempOrigin;
        BlockState stateAtOrigin = column.getBlock(finalOrigin.getY());
        if (stateAtOrigin.isAir() || stateAtOrigin.is(Blocks.WATER)) {
            return Optional.empty();
        }

        // Returns stub calling the structure piece
        return Optional.of(new Structure.GenerationStub(finalOrigin, (builder) -> {
            builder.addPiece(new MeteoritePiece(random, finalOrigin, radius));
        }));
    }
    @Override
    public StructureType<?> type() {
        return this.type;
    }
}

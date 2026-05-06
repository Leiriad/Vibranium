package io.github.leiriad.vibranium.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.Optional;
import java.util.function.Supplier;

public class MeteoriteStructure extends Structure {
    // CODEC for registration and parameter parsing
    public static final MapCodec<MeteoriteStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    settingsCodec(instance),
                    Codec.INT.fieldOf("min_radius").forGetter(s -> s.minRadius),
                    Codec.INT.fieldOf("max_radius").forGetter(s -> s.maxRadius)
            ).apply(instance, (settings, min, max) -> new MeteoriteStructure(settings, min, max, null))
    );

    private final StructureType<MeteoriteStructure> type;
    private final int minRadius;
    private final int maxRadius;

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
                ).apply(instance, (settings, min, max) -> new MeteoriteStructure(settings, min, max, typeSupplier.get()))
        );
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        RandomSource random = context.random();
        ChunkPos chunkPos = context.chunkPos();
        int checkX = chunkPos.getMiddleBlockX();
        int checkZ = chunkPos.getMiddleBlockZ();
        int radius = minRadius + random.nextInt(maxRadius - minRadius + 1);

        // 1. HEIGHT DETECTION: Get land and ocean floor heights
        int landSurfaceY = context.chunkGenerator().getFirstOccupiedHeight(checkX, checkZ, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, context.heightAccessor(), context.randomState());
        int oceanFloorY = context.chunkGenerator().getFirstOccupiedHeight(checkX, checkZ, Heightmap.Types.OCEAN_FLOOR, context.heightAccessor(), context.randomState());

        // 2. TRUE SURFACE: Target the lowest point (handles water bodies correctly)
        int surfaceY = Math.min(landSurfaceY, oceanFloorY);
        int absoluteMinY = context.heightAccessor().getMinY() + 5 + radius;

        boolean isSurface = random.nextFloat() < 0.35f;
        BlockPos finalOrigin;

        if (isSurface) {
            // Anchor partially buried in the surface
            int offset = (int) (radius * (0.5 + random.nextFloat() * 0.2));
            finalOrigin = new BlockPos(checkX, Math.max(absoluteMinY, surfaceY - offset), checkZ);
        } else {
            // UNDERGROUND ANCHORING LOGIC: Find a random height and verify contact with terrain
            int originY = absoluteMinY + random.nextInt(Math.max(1, surfaceY - absoluteMinY - radius));
            finalOrigin = new BlockPos(checkX, originY, checkZ);

            // Fail generation if floating in a giant air pocket (e.g., Lush Caves)
            if (!isAnchored(context, finalOrigin, radius)) return Optional.empty();
        }

        return Optional.of(new Structure.GenerationStub(finalOrigin, (builder) -> {
            builder.addPiece(new MeteoritePiece(random, finalOrigin, radius, isSurface, surfaceY));
        }));
    }

    /**
     * Verifies if the meteorite shell touches solid terrain in cardinal directions.
     * This prevents meteorites from floating awkwardly in large cave systems.
     */
    private boolean isAnchored(Structure.GenerationContext context, BlockPos pos, int radius) {
        NoiseColumn column = context.chunkGenerator().getBaseColumn(pos.getX(), pos.getZ(), context.heightAccessor(), context.randomState());

        // Vertical Check (Floor/Ceiling)
        if (column.getBlock(pos.getY() - radius).isSolid() || column.getBlock(pos.getY() + radius).isSolid()) return true;

        // Horizontal Check (Walls)
        int[] offsets = {-radius, radius};
        for (int ox : offsets) {
            if (context.chunkGenerator().getBaseColumn(pos.getX() + ox, pos.getZ(), context.heightAccessor(), context.randomState()).getBlock(pos.getY()).isSolid()) return true;
        }
        for (int oz : offsets) {
            if (context.chunkGenerator().getBaseColumn(pos.getX(), pos.getZ() + oz, context.heightAccessor(), context.randomState()).getBlock(pos.getY()).isSolid()) return true;
        }

        return false;
    }

    @Override
    public StructureType<?> type() {
        return this.type;
    }
}
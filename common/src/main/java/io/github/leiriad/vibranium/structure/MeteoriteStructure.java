package io.github.leiriad.vibranium.structure;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.leiriad.vibranium.init.VibraniumStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
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

public class MeteoriteStructure extends Structure {
    // Codec is used to register parameters
    public static final MapCodec<MeteoriteStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Structure.settingsCodec(instance)
            ).apply(instance, MeteoriteStructure::new)
    );

    public MeteoriteStructure(Structure.StructureSettings settings) {
        super(settings);
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        // Meterite center is defined relatively to chunk center
        RandomSource random = context.random();
        ChunkPos chunkPos = context.chunkPos();
        int checkX = chunkPos.getMiddleBlockX();
        int checkZ = chunkPos.getMiddleBlockZ();

        int radius = getRadius(random);


        //Check ground under origin if close to surface, origin is surface minus half the radius
        //Position center, the geode's origin is calculated relatively to the chunk's center, with a vertical offset to bury it in the ground
        int surfaceY = context.chunkGenerator().getFirstOccupiedHeight(
                checkX,
                checkZ,
                Heightmap.Types.OCEAN_FLOOR_WG,
                context.heightAccessor(),
                context.randomState()
        );
        int minY = context.heightAccessor().getMinY();
        int maxSpawnY = surfaceY;

        //Insure floor support
        int range = Math.max(1, maxSpawnY - minY);
        int originY = minY + random.nextInt(range);
        NoiseColumn column = context.chunkGenerator().getBaseColumn(checkX, checkZ, context.heightAccessor(), context.randomState());
        BlockState groundState = column.getBlock(surfaceY - 1);
        BlockPos finalOrigin;

        //Meteorite positioning
        if(originY>= surfaceY - 30){ //Surface meteorite
            int targetSurfaceY = surfaceY - (int)(radius * 0.75); // we must have at least 75% of the meteorite buried in the ground
            int finalY = Math.min(originY, targetSurfaceY);
            finalOrigin = new BlockPos(checkX, finalY, checkZ);
            BlockPos groundPos = new BlockPos(checkX, surfaceY - 1, checkZ);
            //End dimension generation specifics
            Holder<Biome> biome = context.biomeSource().getNoiseBiome(
                    QuartPos.fromBlock(checkX), QuartPos.fromBlock(finalY), QuartPos.fromBlock(checkZ),
                    context.randomState().sampler()
            );
            boolean isEnd = biome.is(BiomeTags.IS_END);
            if (isEnd) {
                if (surfaceY < 15 || groundState.isAir()) { return Optional.empty();}
            } else { //Overworld
                if (groundState.isAir() || groundState.is(Blocks.WATER) || groundState.is(BlockTags.REPLACEABLE)) {
                    return Optional.empty();
                }
            }
        }
        //Underground meteorites
        else{
            finalOrigin = new BlockPos(checkX, originY, checkZ);
        }

        //Validity check
        BlockState stateAtOrigin = column.getBlock(finalOrigin.getY());
        if (stateAtOrigin.isAir() || stateAtOrigin.is(Blocks.WATER)) {
            return Optional.empty();
        }
        if (!context.heightAccessor().isInsideBuildHeight(finalOrigin.getY())) {
            return Optional.empty();
        }

        // Returns stub calling the structure piece
        return Optional.of(new Structure.GenerationStub(finalOrigin, (builder) -> {
            builder.addPiece(new MeteoritePiece(random, finalOrigin, radius));
        }));
    }

    @Override
    public StructureType<?> type() {
        return VibraniumStructures.METEORITE.get();
    }
    private static int getRadius(RandomSource random) {
        float roll = random.nextFloat();
        if (roll < 0.10f) return 3 + random.nextInt(3);// 10% very small meteorite
        if (roll < 0.60f) return 6 + random.nextInt(6);//50% standard 6 to 9 blocs radius - 1 to 2 players
        if (roll < 0.90f) return 10 + random.nextInt(6); //30% Massive 10 to 15 blocs
        return 20 + random.nextInt(11);//10% huge (20 to 30)
    }
}

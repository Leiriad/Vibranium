package vibranium.utils;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import vibranium.init.VibraniumBlocks;

public class MeteoriteFeature extends Feature<NoneFeatureConfiguration> {

    public MeteoriteFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }


     ///Helper method to set blocks only if they are within writable chunk boundaries.
    private void safeSetBlock(WorldGenLevel world, BlockPos pos, BlockState state) {
        if (world.ensureCanWrite(pos)) {
            world.setBlock(pos, state, 3);
        }
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel world = context.level();
        // The geode's origin is calculated relatively to the chunk's center
        BlockPos origin = context.origin().offset(8, 0, 8);
        RandomSource random = context.random();

        boolean isWet = random.nextInt(5) == 0;
        int radius = getRadius(random);
        int waterLevel = calculateWaterLevel(isWet, radius, origin, random);

        // First pass: Structural generation
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);
                    if (distance <= radius) {
                        BlockPos target = origin.offset(x, y, z);
                        double noiseFactor = getNoise(target.getX(), target.getY(), target.getZ());
                        double distortedDistance = distance + noiseFactor;
                        generateStructure(world, target, distortedDistance, radius, waterLevel, origin, random);
                    }
                }
            }
        }

        // Second pass: Interior decoration
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);
                    if (distance < radius - 1) {
                        decorateInterior(world, origin.offset(x, y, z), random, waterLevel);
                    }
                }
            }
        }
        return true;
    }

    private double getNoise(int x, int y, int z) {
        // Uses different frequency sinuses to create irregular topography
        return Math.sin(x * 0.2) * 1.5 + Math.cos(y * 0.2) * 1.5 + Math.sin(z * 0.2) * 1.5;
    }

    private int calculateWaterLevel(boolean isWet, int radius, BlockPos origin, RandomSource random) {
        if (isWet) return origin.getY() + (radius-2);
        //defines the amount of water in the geode
        float puddleChance = random.nextFloat();
        if (puddleChance < 0.9f) {
            int baseFloorY = (int) (origin.getY() - (radius * 0.6));
            return baseFloorY + 1 + random.nextInt(4);
        }
        return origin.getY() - radius - 1;
    }

    private void generateStructure(WorldGenLevel world, BlockPos target, double distortedDistance, int radius, int waterLevel, BlockPos origin, RandomSource random) {
        //If the meteorite is at the surface, it is eroded: no roof and mossy stone eats at it
        int surfaceY = world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, target.getX(), target.getZ());
        BlockState current = world.getBlockState(target);

        if (target.getY() > surfaceY && current.isAir()) return;
        boolean isAtSurface = target.getY() >= surfaceY - 1;

        if (distortedDistance < radius - 3) {
            handleHollowInterior(world, target, distortedDistance, radius, waterLevel, origin, surfaceY);
        } else if (distortedDistance > radius - 1.5) {
            generateShell(world, target, target.getY() >= surfaceY - 1, 0.4f, random);
        } else {
            generateVibraniumVeins(world, target, target.getY() >= surfaceY - 1, random);
        }
    }

    private void handleHollowInterior(WorldGenLevel world, BlockPos target, double distance, int radius, int waterLevel, BlockPos origin, int surfaceY) {
        double noise = Math.sin(target.getX() * 0.15) * Math.cos(target.getZ() * 0.15) * 4;
        noise += Math.sin(target.getX() * 0.4) * 1.5;
        double edgeBoost = Math.max(0, (distance - (radius * 0.4)));
        int floorHeight = (int) (origin.getY() - (radius * 0.6) + noise + edgeBoost);

        if (target.getY() <= floorHeight) {
            generateFloor(world, target, waterLevel);
        } else if (target.getY() <= waterLevel) {
            safeSetBlock(world, target, target.getY() < surfaceY - 1 ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState());
        } else {
            // Air at last to clean the remaining vanilla objects
            safeSetBlock(world, target, Blocks.AIR.defaultBlockState());
        }
    }

    private void generateVibraniumVeins(WorldGenLevel world, BlockPos target, boolean isAtSurface, RandomSource random) {
        double oreNoise = Math.sin(target.getX() * 0.5) + Math.sin(target.getY() * 0.5) + Math.sin(target.getZ() * 0.5);
        //ore patches (the closest to 3 the rarest)
        if (oreNoise > 2.8 || (oreNoise > 2.0 && random.nextFloat() < 0.4f)) {// 0.4f means we keep only 40% of the blocks
            safeSetBlock(world, target, VibraniumBlocks.VIBRANIUM_ORE.defaultBlockState());
        } else {
            generateShell(world, target, isAtSurface, 0.2f, random);
        }
    }

    private void generateShell(WorldGenLevel world, BlockPos target, boolean isAtSurface, float mossChance, RandomSource random) {
        BlockState shell = (isAtSurface && random.nextFloat() < mossChance) ?
                Blocks.MOSSY_COBBLESTONE.defaultBlockState() : Blocks.BLACKSTONE.defaultBlockState();
        safeSetBlock(world, target, shell);
    }

    private void generateFloor(WorldGenLevel world, BlockPos target, int waterLevel) {
        double patchNoise = Math.sin(target.getX() * 0.12) + Math.sin(target.getZ() * 0.12);
        BlockState state;
        //perlin noise sets the size of the patches
        // sediments (underwater)
        if (target.getY() <= waterLevel) {
            // On crée trois zones distinctes selon le bruit
            if (patchNoise > 0.2) {
                safeSetBlock(world, target, VibraniumBlocks.VIBRANIUM_DIRT.defaultBlockState());
            } else if (patchNoise > -0.2) {
                safeSetBlock(world, target, VibraniumBlocks.BLACKCLAY.defaultBlockState());
            } else {
                safeSetBlock(world, target, VibraniumBlocks.BLACKGRAVEL.defaultBlockState());
            }
        }
        else { //Grass or moss
            if (patchNoise > 0.3) {
                safeSetBlock(world, target, VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.defaultBlockState());
            } else {
                safeSetBlock(world, target, VibraniumBlocks.PURPLE_MOSS_BLOCK.defaultBlockState());
            }
        }

    }

    private void decorateInterior(WorldGenLevel world, BlockPos target, RandomSource random, int waterLevel) {
        BlockState current = world.getBlockState(target);
        //Clean vanilla blocks
        if (current.is(BlockTags.LOGS) || current.is(BlockTags.LEAVES) || current.is(Blocks.OAK_LOG)) {
            safeSetBlock(world, target, Blocks.AIR.defaultBlockState());
            return;
        }
        if (!current.isAir() && !current.is(Blocks.WATER)) return;
        //add dripstones
        if (world.isEmptyBlock(target) && random.nextFloat() < 0.02f) { // Très rare
            if (world.getBlockState(target.above()).is(Blocks.BLACKSTONE)) {
                placeDripstoneColumn(world, target, Direction.DOWN, random); // Stalactite
                return;
            } else if (world.getBlockState(target.below()).isFaceSturdy(world, target.below(), Direction.UP)) {
                placeDripstoneColumn(world, target, Direction.UP, random); // Stalagmite
                return;
            }
        }
        if (current.isAir()) {
            BlockState above = world.getBlockState(target.above());
            if (above.is(Blocks.BLACKSTONE) || above.is(VibraniumBlocks.VIBRANIUM_ORE) || above.is(Blocks.MOSSY_COBBLESTONE)) {
                if (random.nextFloat() < 0.20f) placeVineGrapnel(world, target, random);
            }
        }

        if (world.getBlockState(target.below()).is(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK)) {
            if (world.getFluidState(target).is(FluidTags.WATER)) {
                if (random.nextFloat() < 0.15f) placeSmallDripleaf(world, target);
            } else if (world.isEmptyBlock(target)) {
                float r = random.nextFloat();
                if (r < 0.10f) safeSetBlock(world, target, VibraniumBlocks.BIG_PURPLE_DRIPLEAF.defaultBlockState());
                else if (r < 0.25f) safeSetBlock(world, target, VibraniumBlocks.PURPLE_MOSS_CARPET.defaultBlockState());
            }
        }
    }

    private void placeDripstoneColumn(WorldGenLevel world, BlockPos pos, Direction direction, RandomSource random) {
        //place support bloc
        BlockPos supportPos = pos.relative(direction.getOpposite());
        safeSetBlock(world, supportPos, Blocks.DRIPSTONE_BLOCK.defaultBlockState());

        // place first spike
        safeSetBlock(world, pos, Blocks.POINTED_DRIPSTONE.defaultBlockState()
                .setValue(PointedDripstoneBlock.TIP_DIRECTION, direction)
                .setValue(PointedDripstoneBlock.THICKNESS, DripstoneThickness.TIP));

        // 50% chance to grow the spike
        if (random.nextBoolean()) {
            BlockPos nextPos = pos.relative(direction);
            if (world.isEmptyBlock(nextPos) || world.getFluidState(nextPos).is(FluidTags.WATER)) {

                safeSetBlock(world, pos, world.getBlockState(pos).setValue(PointedDripstoneBlock.THICKNESS, DripstoneThickness.FRUSTUM));
                safeSetBlock(world, nextPos, Blocks.POINTED_DRIPSTONE.defaultBlockState()
                        .setValue(PointedDripstoneBlock.TIP_DIRECTION, direction)
                        .setValue(PointedDripstoneBlock.THICKNESS, DripstoneThickness.TIP));
            }
        }
    }

    private void placeSmallDripleaf(WorldGenLevel world, BlockPos target) {
        safeSetBlock(world, target, VibraniumBlocks.SMALL_PURPLE_DRIPLEAF.defaultBlockState()
                .setValue(SmallDripleafBlock.HALF, DoubleBlockHalf.LOWER)
                .setValue(BlockStateProperties.WATERLOGGED, true));

        BlockPos upperPos = target.above();
        if (world.isEmptyBlock(upperPos) || world.getFluidState(upperPos).is(FluidTags.WATER)) {
            safeSetBlock(world, upperPos, VibraniumBlocks.SMALL_PURPLE_DRIPLEAF.defaultBlockState()
                    .setValue(SmallDripleafBlock.HALF, DoubleBlockHalf.UPPER)
                    .setValue(BlockStateProperties.WATERLOGGED, world.getFluidState(upperPos).is(FluidTags.WATER)));
        }
    }

    private void placeVineGrapnel(WorldGenLevel world, BlockPos target, RandomSource random) {
        if (world.getBlockState(target.above()).isFaceSturdy(world, target.above(), Direction.DOWN)) {
            // Add Age and Berries to ensure it stays as PurpleCaveVines
            BlockState head = VibraniumBlocks.PURPLE_CAVE_VINES.defaultBlockState()
                    .setValue(CaveVinesBlock.AGE, random.nextInt(25))
                    .setValue(CaveVinesBlock.BERRIES, random.nextFloat() < 0.15f);
            safeSetBlock(world, target, head);
            return;
        }

        BlockState wallVine = VibraniumBlocks.PURPLE_VINE.defaultBlockState();
        boolean placed = false;
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos neighbor = target.relative(dir);
            BlockState neighborState = world.getBlockState(neighbor);
            if (!neighborState.isAir() && !neighborState.is(Blocks.WATER)) {
                wallVine = wallVine.setValue(VineBlock.getPropertyForFace(dir), true);
                placed = true;
            }
        }
        if (placed) safeSetBlock(world, target, wallVine);
    }

    private static int getRadius(RandomSource random) {
        float roll = random.nextFloat();
        if (roll < 0.30f) return 3 + random.nextInt(3);// 30% very small meteorite
        if (roll < 0.55f) return 7 + random.nextInt(4);//55% standard 7 to 11 blocs radius
        return 12 + random.nextInt(3);//10% huge (12 to 15)
    }
}
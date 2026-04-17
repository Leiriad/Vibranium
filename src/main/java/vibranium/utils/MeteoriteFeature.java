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
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        //Check ground under origin if close to surface, origin is surface minus half the radius
        //Position center, the geode's origin is calculated relatively to the chunk's center, with a vertical offset to bury it in the ground
        int surfaceY = world.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, origin.getX(), origin.getZ());
        BlockPos finalOrigin;
        if (Math.abs(origin.getY() - surfaceY) < 10) {
            finalOrigin = new BlockPos(origin.getX() + 8, surfaceY - (getRadius(random) / 2), origin.getZ() + 8);
        } else {
            finalOrigin = origin.offset(8, 0, 8);
        }
        if (!world.isInsideBuildHeight(finalOrigin.getY())) {
            return false;
        }

        // First pass: Structural generation
        int radius = getRadius(random);
        boolean isWet = random.nextInt(5) == 0;
        int waterLevel = calculateWaterLevel(isWet, radius, finalOrigin, random);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);
                    if (distance <= radius) {
                        BlockPos target = finalOrigin.offset(x, y, z);
                        double noiseFactor = getNoise(target.getX(), target.getY(), target.getZ());
                        double distortedDistance = distance + noiseFactor;
                        generateStructure(world, target, distortedDistance, radius, waterLevel, finalOrigin, random);
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
                        decorateInterior(world, finalOrigin.offset(x, y, z), random);
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
        int baseFloorY = (int) (origin.getY() - (radius * 0.6));
        return baseFloorY + 1 + random.nextInt(2);
    }

    private void generateStructure(WorldGenLevel world, BlockPos target, double distortedDistance, int radius, int waterLevel, BlockPos origin, RandomSource random) {
        //If the meteorite is at the surface, it is eroded: no roof and mossy stone eats at it
        int surfaceY = world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, target.getX(), target.getZ());
        BlockState current = world.getBlockState(target);

        if (target.getY() > surfaceY && current.isAir()) return;

        if (distortedDistance < radius - 3) {
            handleHollowInterior(world, target, distortedDistance, radius, waterLevel, origin, surfaceY, random);
        } else if (distortedDistance > radius - 1.5) {
            generateShell(world, target, target.getY() >= surfaceY - 1, 0.4f, random);
        } else {
            generateVibraniumVeins(world, target, target.getY() >= surfaceY - 1, random,distortedDistance,radius);
        }
    }

    private void handleHollowInterior(WorldGenLevel world, BlockPos target, double distance, int radius, int waterLevel, BlockPos origin, int surfaceY, RandomSource random) {
        double noise = Math.sin(target.getX() * 0.15) * Math.cos(target.getZ() * 0.15) * 4;
        noise += Math.sin(target.getX() * 0.4) * 1.5;
        double edgeBoost = Math.max(0, (distance - (radius * 0.4)));
        int floorHeight = (int) (origin.getY() - (radius * 0.6) + noise + edgeBoost);

        if (target.getY() <= floorHeight) {
            int puddleDepth = 1 + random.nextInt(3);
            generateFloor(world, target, waterLevel, puddleDepth);
        } else if (target.getY() <= waterLevel) {
            safeSetBlock(world, target, target.getY() < surfaceY - 1 ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState());
        } else {
            // Air at last to clean the remaining vanilla objects
            safeSetBlock(world, target, Blocks.AIR.defaultBlockState());
        }
    }

    private void generateVibraniumVeins(WorldGenLevel world, BlockPos target, boolean isAtSurface, RandomSource random, double distortedDistance, int radius) {
        double oreNoise = Math.sin(target.getX() * 0.5) + Math.sin(target.getY() * 0.5) + Math.sin(target.getZ() * 0.5);
        //ore patches (the closest to 3 the rarest)
        boolean isInnerShell = distortedDistance < (radius - 1.5) && distortedDistance > (radius - 2.5);
        if (oreNoise > 2.4 || (isInnerShell && random.nextFloat() < 0.2f)) {// 0.2f means we keep only 20% of the blocks
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

    private void generateFloor(WorldGenLevel world, BlockPos target, int waterLevel,int puddleDepth) {
        double patchNoise = Math.sin(target.getX() * 0.12) + Math.sin(target.getZ() * 0.12);
        //perlin noise sets the size of the patches
        // sediments (underwater)
        if (target.getY() <= waterLevel) {
            // On crée trois zones distinctes selon le bruit

            if(target.getY()< waterLevel-puddleDepth){
                if (patchNoise > 0.2) {
                    safeSetBlock(world, target, VibraniumBlocks.VIBRANIUM_DIRT.defaultBlockState());
                } else if (patchNoise > -0.2) {
                    safeSetBlock(world, target, VibraniumBlocks.BLACKCLAY.defaultBlockState());
                } else {
                    safeSetBlock(world, target, VibraniumBlocks.BLACKGRAVEL.defaultBlockState());
                }
            }else {
                safeSetBlock(world, target, VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.defaultBlockState());
            }
        }
        else { //Grass or moss
            safeSetBlock(world, target, VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.defaultBlockState());
        }

    }

    private void decorateInterior(WorldGenLevel world, BlockPos target, RandomSource random) {
        BlockState current = world.getBlockState(target);
        //Clean vanilla blocks
        if (current.is(BlockTags.LOGS) || current.is(BlockTags.LEAVES) || current.is(Blocks.OAK_LOG)) {
            safeSetBlock(world, target, Blocks.AIR.defaultBlockState());
            return;
        }
        if (!current.isAir() && !world.getFluidState(target).is(FluidTags.WATER)) return;
        //add dripstones
        if (current.isAir() && random.nextFloat() < 0.05f) {
            if (world.getBlockState(target.above()).is(Blocks.BLACKSTONE)) {
                placeDripstoneColumn(world, target, random);
                return;
            }
        }
        //add vines
        if (current.isAir()) {
            if (random.nextFloat() < 0.25f) {
                placeVineGrapnel(world, target, random);
            }
        }
        //add cave vines and moss carpets
        if (world.getBlockState(target.below()).is(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK)) {
            if (world.getFluidState(target).is(FluidTags.WATER)) {
                if (random.nextFloat() < 0.30f) placeSmallDripleaf(world, target);
            } else if (world.isEmptyBlock(target)) {
                float r = random.nextFloat();
                if (r < 0.15f) {
                    safeSetBlock(world, target.below(), VibraniumBlocks.PURPLE_MOSS_BLOCK.defaultBlockState());
                    if (random.nextBoolean()) {
                        safeSetBlock(world, target, VibraniumBlocks.PURPLE_MOSS_CARPET.defaultBlockState());
                    }
                }
                else if (r < 0.25f) {
                    safeSetBlock(world, target, VibraniumBlocks.BIG_PURPLE_DRIPLEAF.defaultBlockState());
                }
                else if (r < 0.40f) {
                    safeSetBlock(world, target, VibraniumBlocks.PURPLE_MOSS_CARPET.defaultBlockState());
                }
            }
        }
    }

    private void placeDripstoneColumn(WorldGenLevel world, BlockPos pos, RandomSource random) {
        //place support bloc
        Direction direction = Direction.DOWN;
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
        boolean placed = false;
        if (world.getBlockState(target.above()).isFaceSturdy(world, target.above(), Direction.DOWN)) {
            // Add Age and Berries to ensure it stays as PurpleCaveVines
            BlockState head = VibraniumBlocks.PURPLE_CAVE_VINES.defaultBlockState()
                    .setValue(CaveVinesBlock.AGE, random.nextInt(25))
                    .setValue(CaveVinesBlock.BERRIES, random.nextFloat() < 0.15f);
            safeSetBlock(world, target, head);
            placed = true;
        }

        if (!placed) {
            BlockState wallVine = VibraniumBlocks.PURPLE_VINE.defaultBlockState();
            boolean wallFound = false;
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos neighbor = target.relative(dir);
                BlockState neighborState = world.getBlockState(neighbor);

                if (neighborState.isSolidRender()) {
                    wallVine = wallVine.setValue(VineBlock.getPropertyForFace(dir), true);
                    wallFound = true;
                }
            }
            if (wallFound) safeSetBlock(world, target, wallVine);
        }
    }

    private static int getRadius(RandomSource random) {
        float roll = random.nextFloat();
        if (roll < 0.10f) return 1 + random.nextInt(5);// 10% very small meteorite
        if (roll < 0.55f) return 5 + random.nextInt(3);//55% standard 4 to 7 blocs radius
        return 8 + random.nextInt(3);//45% huge (5 to 8)
    }
}
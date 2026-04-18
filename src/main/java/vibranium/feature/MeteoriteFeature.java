package vibranium.feature;

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
        int checkX = origin.getX() + 8;
        int checkZ = origin.getZ() + 8;
        int surfaceY = world.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, checkX, checkZ);

        //Insure floor support
        boolean isEnd = world.getLevel().dimension().equals(net.minecraft.world.level.Level.END);
        BlockPos groundPos = new BlockPos(checkX, surfaceY - 1, checkZ);
        BlockState groundState = world.getBlockState(groundPos);
        //End dimension generation specifics
        if (isEnd) {
            if (surfaceY < 15 || groundState.isAir()) {
                return false;
            }
        } else { //Overworld
            if (groundState.isAir() || groundState.is(Blocks.WATER) || groundState.is(BlockTags.REPLACEABLE)) {
                return false;
            }
        }

        //Meteorite positioning
        int radius = getRadius(random);
        BlockPos finalOrigin = new BlockPos(checkX, surfaceY - (int)(radius * 0.75), checkZ);
        if (!world.isInsideBuildHeight(finalOrigin.getY())) {
            return false;
        }

        //Meteorite theoretical volume
        double theoreticalVolume = (4.0 / 3.0) * Math.PI * Math.pow(radius, 3);
        int blocksPlaced = 0;
        int[] vibraniumOreCounter = new int[]{0};

        boolean isWet = random.nextInt(5) == 0;
        int waterLevel = calculateWaterLevel(isWet, radius, finalOrigin, random);

        // First pass: Structural generation
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance <= radius) {
                        BlockPos target = finalOrigin.offset(x, y, z);
                        double noiseFactor = getNoise(target.getX(), target.getY(), target.getZ());
                        double distortedDistance = distance + noiseFactor;

                        // Generate structure implement erosion logics
                        generateStructure(world, target, distortedDistance, radius, waterLevel, finalOrigin, random, vibraniumOreCounter);

                        // Counts meteorite solid blocks
                        if (distortedDistance > radius - 3) {
                            blocksPlaced++;
                        }
                    }
                }
            }
        }

        //Dynamic validation
        double fillRatio = (double) blocksPlaced / theoreticalVolume;
        if (fillRatio < 0.25) {
            return false;
        }

        // Second pass: Interior decoration
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);
                    if (distance < radius - 1.5) {
                        decorateInterior(world, finalOrigin.offset(x, y, z), random);
                    }
                }
            }
        }

        //Meteorites should have at least 2 ores (forced placement)
        if (vibraniumOreCounter[0] < 2) {
            BlockState centerState = world.getBlockState(finalOrigin);
            BlockState ore = VibraniumBlocks.VIBRANIUM_ORE.defaultBlockState();

            //Place ore in the center if possible
            if (centerState.is(Blocks.BLACKSTONE)) {
                safeSetBlock(world, finalOrigin, ore);
                safeSetBlock(world, finalOrigin.below(), ore);
            } else {
                // on the meteorite floor otherwise
                BlockPos floorPos = finalOrigin.below((int)(radius * 0.5));
                if (world.getBlockState(floorPos).is(Blocks.BLACKSTONE)) {
                    safeSetBlock(world, floorPos, ore);
                    safeSetBlock(world, floorPos.above(), ore);
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

    private void generateStructure(WorldGenLevel world, BlockPos target, double distortedDistance, int radius, int waterLevel, BlockPos origin, RandomSource random, int[] oreCounter) {
        //If the meteorite is at the surface, it is eroded: no roof, and mossy stone eats at it
        int floorY = world.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, target.getX(), target.getZ());
        BlockState current = world.getBlockState(target);

        if (target.getY() > floorY && (current.isAir()||current.is(Blocks.WATER))) return;

        double finalDist = distortedDistance;
        if (target.getY() < origin.getY()) {
            finalDist = Math.sqrt(target.distSqr(origin)) + (getNoise(target.getX(), target.getY(), target.getZ()) * 0.5);
        }

        if (finalDist > radius - 1.5) {
            // Maintains a round exterior despite noise
            generateShell(world, target, target.getY() >= floorY - 1, 0.4f, random);
        } else if (finalDist < radius - 3) {
            handleHollowInterior(world, target, finalDist, radius, waterLevel, origin, floorY);
        } else {
            oreCounter[0] += generateVibraniumVeins(world, target, target.getY() >= floorY - 1, random, finalDist, radius);
        }
    }

    private void handleHollowInterior(WorldGenLevel world, BlockPos target, double distance, int radius, int waterLevel, BlockPos origin, int surfaceY) {
        double noise = Math.sin(target.getX() * 0.15) * Math.cos(target.getZ() * 0.15) * 4;
        noise += Math.sin(target.getX() * 0.4) * 1.5;
        double edgeBoost = Math.max(0, (distance - (radius * 0.4)));
        int floorHeight = (int) (origin.getY() - (radius * 0.6) + noise + edgeBoost);

        if (target.getY() <= floorHeight) {
            generateFloor(world, target, waterLevel);
            return;
        }

        //Generate water or air depending on blocks above
        boolean isPreExistingWater = target.getY() < surfaceY && world.getFluidState(target).is(FluidTags.WATER);

        boolean touchesWater = world.getFluidState(target.above()).is(FluidTags.WATER) ||
                world.getFluidState(target.north()).is(FluidTags.WATER) ||
                world.getFluidState(target.south()).is(FluidTags.WATER) ||
                world.getFluidState(target.east()).is(FluidTags.WATER) ||
                world.getFluidState(target.west()).is(FluidTags.WATER);

        // If the meteorite is hollow, it can have a puddle
        if (target.getY() <= waterLevel) {
            safeSetBlock(world, target, Blocks.WATER.defaultBlockState());
        }
        else if (isPreExistingWater || touchesWater) {
            safeSetBlock(world, target, Blocks.WATER.defaultBlockState());
        }
        else {
            // fills the hollow of the meteorite with air where not filled by water
            safeSetBlock(world, target, Blocks.AIR.defaultBlockState());
        }
    }

    private int generateVibraniumVeins(WorldGenLevel world, BlockPos target, boolean isAtSurface, RandomSource random, double distortedDistance, int radius) {
        double oreNoise = Math.sin(target.getX() * 0.5) + Math.sin(target.getY() * 0.5) + Math.sin(target.getZ() * 0.5);
        //ore patches (the closest to 3 the rarest)
        boolean isInnerShell = distortedDistance < (radius - 1.5) && distortedDistance > (radius - 2.5);
        if (oreNoise > 2.4 || (isInnerShell && random.nextFloat() < 0.2f)) {// 0.2f means we keep only 20% of the blocks
            safeSetBlock(world, target, VibraniumBlocks.VIBRANIUM_ORE.defaultBlockState());
            return 1;
        } else {
            generateShell(world, target, isAtSurface, 0.2f, random);
            return 0;
        }
    }

    private void generateShell(WorldGenLevel world, BlockPos target, boolean isAtSurface, float mossChance, RandomSource random) {
        boolean isEnd = world.getLevel().dimension().equals(net.minecraft.world.level.Level.END);
        BlockState shell;
        if (isEnd) {
            shell = (isAtSurface && random.nextFloat() < mossChance) ?
                    Blocks.END_STONE.defaultBlockState() : Blocks.BLACKSTONE.defaultBlockState();
        }
        else {
            shell = (isAtSurface && random.nextFloat() < mossChance) ?
                Blocks.MOSSY_COBBLESTONE.defaultBlockState() : Blocks.BLACKSTONE.defaultBlockState();
        }

        safeSetBlock(world, target, shell);
    }

    private void generateFloor(WorldGenLevel world, BlockPos target, int waterLevel) {
        double patchNoise = Math.sin(target.getX() * 0.12) + Math.sin(target.getZ() * 0.12);
        //perlin noise sets the size of the patches
        // sediments (underwater)
        if (target.getY() < waterLevel) {
            if (patchNoise > 0.3) {
                safeSetBlock(world, target, VibraniumBlocks.BLACKGRAVEL.defaultBlockState());
            } else if (patchNoise > -0.1) {
                safeSetBlock(world, target, VibraniumBlocks.BLACKCLAY.defaultBlockState());
            } else {
                safeSetBlock(world, target, VibraniumBlocks.VIBRANIUM_DIRT.defaultBlockState());
            }
        }
        else { //Grass or moss
            safeSetBlock(world, target, VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.defaultBlockState());
        }

    }

    private void decorateInterior(WorldGenLevel world, BlockPos target, RandomSource random) {
        BlockState current = world.getBlockState(target);

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
        //add dripleaves and moss carpets
        BlockState floorState = world.getBlockState(target.below());
        boolean isValidFloor = floorState.is(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK) ||
                floorState.is(VibraniumBlocks.VIBRANIUM_DIRT);
        if (isValidFloor) {
            if (world.getFluidState(target).is(FluidTags.WATER)) {
                if (random.nextFloat() < 0.30f) placeSmallDripleaf(world, target, random);
            } else if (world.isEmptyBlock(target)) {
                float r = random.nextFloat();
                if (r < 0.15f) {
                    safeSetBlock(world, target.below(), VibraniumBlocks.PURPLE_MOSS_BLOCK.defaultBlockState());
                    if (random.nextBoolean()) {
                        safeSetBlock(world, target, VibraniumBlocks.PURPLE_MOSS_CARPET.defaultBlockState());
                    }
                }
                else if (r < 0.25f) {
                    Direction randomFacing = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                    BlockState bigDripleafState =  VibraniumBlocks.BIG_PURPLE_DRIPLEAF.defaultBlockState()
                            .setValue(SmallDripleafBlock.FACING, randomFacing);
                    safeSetBlock(world, target, bigDripleafState);
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
        BlockState pointedDripstone = Blocks.POINTED_DRIPSTONE.defaultBlockState()
                .setValue(PointedDripstoneBlock.TIP_DIRECTION, direction)
                .setValue(PointedDripstoneBlock.THICKNESS, DripstoneThickness.TIP);
        safeSetBlock(world, pos, pointedDripstone);

        // 50% chance to grow the spike
        if (random.nextBoolean()) {
            BlockPos nextPos = pos.relative(direction);
            if (world.isEmptyBlock(nextPos) || world.getFluidState(nextPos).is(FluidTags.WATER)) {

                BlockState thickerState = pointedDripstone.setValue(PointedDripstoneBlock.THICKNESS, DripstoneThickness.FRUSTUM);
                safeSetBlock(world, pos, thickerState);
                safeSetBlock(world, nextPos, Blocks.POINTED_DRIPSTONE.defaultBlockState()
                        .setValue(PointedDripstoneBlock.TIP_DIRECTION, direction)
                        .setValue(PointedDripstoneBlock.THICKNESS, DripstoneThickness.TIP));
            }
        }
    }

    private void placeSmallDripleaf(WorldGenLevel world, BlockPos target, RandomSource random) {
        // Define direction
        Direction randomFacing = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockState lowerState = VibraniumBlocks.SMALL_PURPLE_DRIPLEAF.defaultBlockState()
                .setValue(SmallDripleafBlock.HALF, DoubleBlockHalf.LOWER)
                .setValue(SmallDripleafBlock.FACING, randomFacing) // On applique l'orientation
                .setValue(BlockStateProperties.WATERLOGGED, world.getFluidState(target).is(FluidTags.WATER));

        safeSetBlock(world, target, lowerState);

        BlockPos upperPos = target.above();
        if (world.isEmptyBlock(upperPos) || world.getFluidState(upperPos).is(FluidTags.WATER)) {
            // Create upper bloc
            BlockState upperState = VibraniumBlocks.SMALL_PURPLE_DRIPLEAF.defaultBlockState()
                    .setValue(SmallDripleafBlock.HALF, DoubleBlockHalf.UPPER)
                    .setValue(SmallDripleafBlock.FACING, randomFacing)
                    .setValue(BlockStateProperties.WATERLOGGED, world.getFluidState(upperPos).is(FluidTags.WATER));

            safeSetBlock(world, upperPos, upperState);
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
        if (roll < 0.10f) return 3 + random.nextInt(2);// 10% very small meteorite
        if (roll < 0.55f) return 5 + random.nextInt(3);//55% standard 5 to 8 blocs radius
        return 8 + random.nextInt(3);//45% huge (8 to 11)
    }
}
package io.github.leiriad.vibranium.structure;

import io.github.leiriad.vibranium.init.VibraniumBlocks;
import io.github.leiriad.vibranium.init.VibraniumStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;


public class MeteoritePiece extends StructurePiece {
    //PROPERTIES
    private final int radius;

    //CONSTRUCTORS
    public MeteoritePiece(RandomSource random, BlockPos origin, int radius) {
        super(VibraniumStructures.METEORITE_PIECE.get(), 0, makeBoundingBox(origin, radius));
        this.radius = radius;
    }

    public MeteoritePiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(VibraniumStructures.METEORITE_PIECE.get(), tag);
        this.radius = tag.getInt("Radius").orElse(0);
    }

    //METHODS
    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putInt("Radius", this.radius);
    }

    @Override
    public void postProcess(WorldGenLevel world, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos origin) {

        BlockPos finalOrigin = origin;
        int radius = this.radius;

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
                        if(box.isInside(target)){
                            double noiseFactor = getNoise(target.getX(), target.getY(), target.getZ());
                            double distortedDistance = distance + noiseFactor;

                            // Generate structure implement erosion logics
                            generateStructure(world, target, distortedDistance, radius, waterLevel, finalOrigin, random, vibraniumOreCounter, box);

                            // Counts meteorite solid blocks
                            if (distortedDistance > radius - 3) {
                                blocksPlaced++;
                            }
                        }
                    }
                }
            }
        }

        //Dynamic validation
        double fillRatio = (double) blocksPlaced / theoreticalVolume;
        if (fillRatio < 0.25) {
            return;
        }

        // Second pass: Interior decoration
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);
                    BlockPos target = finalOrigin.offset(x, y, z);
                    if (distance < radius - 1.5) {
                        if(box.isInside(target)){
                            decorateInterior(world, finalOrigin.offset(x, y, z), random, box);
                        }
                    }
                }
            }
        }

        //Meteorites should have at least 2 ores (forced placement)
        if (vibraniumOreCounter[0] < 2) {
            Block vibraniumOre = VibraniumBlocks.VIBRANIUM_ORE.get();
            BlockState ore = vibraniumOre.defaultBlockState();

            //Place ore in the center if possible
            if (box.isInside(finalOrigin)) {
                if (world.getBlockState(finalOrigin).is(Blocks.BLACKSTONE)) {
                    safeSetBlock(world, finalOrigin, ore, box);

                    BlockPos below = finalOrigin.below();
                    if (box.isInside(below)) {
                        safeSetBlock(world, below, ore, box);
                    }
                    return;
                }
            }
                // on the meteorite floor otherwise
            BlockPos floorPos = finalOrigin.below((int)(radius * 0.5));
            if (box.isInside(floorPos)) {
                if (world.getBlockState(floorPos).is(Blocks.BLACKSTONE)) {
                    safeSetBlock(world, floorPos, ore, box);

                    BlockPos above = floorPos.above();
                    if (box.isInside(above)) {
                        safeSetBlock(world, above, ore, box);
                    }
                }
            }

        }
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

    private void generateStructure(WorldGenLevel world, BlockPos target, double distortedDistance, int radius, int waterLevel, BlockPos origin, RandomSource random, int[] oreCounter, BoundingBox box) {
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
            generateShell(world, target, target.getY() >= floorY - 1, 0.4f, random, box);
        } else if (finalDist < radius - 3) {
            handleHollowInterior(world, target, finalDist, radius, waterLevel, origin, floorY, box);
        } else {
            oreCounter[0] += generateVibraniumVeins(world, target, target.getY() >= floorY - 1, random, finalDist, radius, box);
        }
    }

    private void handleHollowInterior(WorldGenLevel world, BlockPos target, double distance, int radius, int waterLevel, BlockPos origin, int surfaceY, BoundingBox box) {
        double noise = Math.sin(target.getX() * 0.15) * Math.cos(target.getZ() * 0.15) * 4;
        noise += Math.sin(target.getX() * 0.4) * 1.5;
        double edgeBoost = Math.max(0, (distance - (radius * 0.4)));
        int floorHeight = (int) (origin.getY() - (radius * 0.6) + noise + edgeBoost);

        if (target.getY() <= floorHeight) {
            generateFloor(world, target, waterLevel, box);
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
            safeSetBlock(world, target, Blocks.WATER.defaultBlockState(), box);
        }
        else if (isPreExistingWater || touchesWater) {
            safeSetBlock(world, target, Blocks.WATER.defaultBlockState(), box);
        }
        else {
            // fills the hollow of the meteorite with air where not filled by water
            safeSetBlock(world, target, Blocks.AIR.defaultBlockState(), box);
        }
    }

    private int generateVibraniumVeins(WorldGenLevel world, BlockPos target, boolean isAtSurface, RandomSource random, double distortedDistance, int radius, BoundingBox box) {
        double oreNoise = Math.sin(target.getX() * 0.5) + Math.sin(target.getY() * 0.5) + Math.sin(target.getZ() * 0.5);
        //ore patches (the closest to 3 the rarest)
        boolean isInnerShell = distortedDistance < (radius - 1.5) && distortedDistance > (radius - 2.5);
        if (oreNoise > 2.4 || (isInnerShell && random.nextFloat() < 0.2f)) {// 0.2f means we keep only 20% of the blocks
            Block vibraniumOre = VibraniumBlocks.VIBRANIUM_ORE.get();
            safeSetBlock(world, target, vibraniumOre.defaultBlockState(), box);
            return 1;
        } else {
            generateShell(world, target, isAtSurface, 0.2f, random, box);
            return 0;
        }
    }

    private void generateShell(WorldGenLevel world, BlockPos target, boolean isAtSurface, float mossChance, RandomSource random, BoundingBox box) {
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

        safeSetBlock(world, target, shell, box);
    }

    private void generateFloor(WorldGenLevel world, BlockPos target, int waterLevel, BoundingBox box) {
        double patchNoise = Math.sin(target.getX() * 0.12) + Math.sin(target.getZ() * 0.12);
        //perlin noise sets the size of the patches
        // sediments (underwater)
        if (target.getY() < waterLevel) {
            if (patchNoise > 0.3) {
                safeSetBlock(world, target, VibraniumBlocks.BLACKGRAVEL.get().defaultBlockState(), box);
            } else if (patchNoise > -0.1) {
                safeSetBlock(world, target, VibraniumBlocks.BLACKCLAY.get().defaultBlockState(), box);
            } else {
                safeSetBlock(world, target, VibraniumBlocks.VIBRANIUM_DIRT.get().defaultBlockState(), box);
            }
        }
        else { //Grass or moss
            safeSetBlock(world, target, VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get().defaultBlockState(), box);
        }

    }

    private void decorateInterior(WorldGenLevel world, BlockPos target, RandomSource random, BoundingBox box) {
        BlockState current = world.getBlockState(target);

        if (!current.isAir() && !world.getFluidState(target).is(FluidTags.WATER)) return;
        //add dripstones
        if (current.isAir() && random.nextFloat() < 0.05f) {
            if (world.getBlockState(target.above()).is(Blocks.BLACKSTONE)) {
                placeDripstoneColumn(world, target, random, box);
                return;
            }
        }
        //add vines
        if (current.isAir()) {
            if (random.nextFloat() < 0.25f) {
                placeVineGrapnel(world, target, random, box);
            }
        }
        //add dripleaves and moss carpets
        BlockState floorState = world.getBlockState(target.below());
        boolean isValidFloor = floorState.is(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get()) ||
                floorState.is(VibraniumBlocks.VIBRANIUM_DIRT.get());
        if (isValidFloor) {
            if (world.getFluidState(target).is(FluidTags.WATER)) {
                if (random.nextFloat() < 0.30f) placeSmallDripleaf(world, target, random, box);
            } else if (world.isEmptyBlock(target)) {
                float r = random.nextFloat();
                if (r < 0.15f) {
                    safeSetBlock(world, target.below(), VibraniumBlocks.PURPLE_MOSS_BLOCK.get().defaultBlockState(), box);
                    if (random.nextBoolean()) {
                        safeSetBlock(world, target, VibraniumBlocks.PURPLE_MOSS_CARPET.get().defaultBlockState(), box);
                    }
                }
                else if (r < 0.25f) {
                    Direction randomFacing = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                    BlockState bigDripleafState =  VibraniumBlocks.BIG_PURPLE_DRIPLEAF.get().defaultBlockState()
                            .setValue(SmallDripleafBlock.FACING, randomFacing);
                    safeSetBlock(world, target, bigDripleafState, box);
                }
                else if (r < 0.40f) {
                    safeSetBlock(world, target, VibraniumBlocks.PURPLE_MOSS_CARPET.get().defaultBlockState(), box);
                }
            }
        }
    }

    private void placeDripstoneColumn(WorldGenLevel world, BlockPos pos, RandomSource random, BoundingBox box) {
        //place support bloc
        Direction direction = Direction.DOWN;
        BlockPos supportPos = pos.relative(direction.getOpposite());
        safeSetBlock(world, supportPos, Blocks.DRIPSTONE_BLOCK.defaultBlockState(), box);

        // place first spike
        BlockState pointedDripstone = Blocks.POINTED_DRIPSTONE.defaultBlockState()
                .setValue(PointedDripstoneBlock.TIP_DIRECTION, direction)
                .setValue(PointedDripstoneBlock.THICKNESS, DripstoneThickness.TIP);
        safeSetBlock(world, pos, pointedDripstone, box);

        // 50% chance to grow the spike
        if (random.nextBoolean()) {
            BlockPos nextPos = pos.relative(direction);
            if (world.isEmptyBlock(nextPos) || world.getFluidState(nextPos).is(FluidTags.WATER)) {

                BlockState thickerState = pointedDripstone.setValue(PointedDripstoneBlock.THICKNESS, DripstoneThickness.FRUSTUM);
                safeSetBlock(world, pos, thickerState, box);
                safeSetBlock(world, nextPos, Blocks.POINTED_DRIPSTONE.defaultBlockState()
                        .setValue(PointedDripstoneBlock.TIP_DIRECTION, direction)
                        .setValue(PointedDripstoneBlock.THICKNESS, DripstoneThickness.TIP), box);
            }
        }
    }

    private void placeSmallDripleaf(WorldGenLevel world, BlockPos target, RandomSource random, BoundingBox box) {
        // Define direction
        Direction randomFacing = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockState lowerState = VibraniumBlocks.SMALL_PURPLE_DRIPLEAF.get().defaultBlockState()
                .setValue(SmallDripleafBlock.HALF, DoubleBlockHalf.LOWER)
                .setValue(SmallDripleafBlock.FACING, randomFacing) // On applique l'orientation
                .setValue(BlockStateProperties.WATERLOGGED, world.getFluidState(target).is(FluidTags.WATER));

        safeSetBlock(world, target, lowerState, box);

        BlockPos upperPos = target.above();
        if (world.isEmptyBlock(upperPos) || world.getFluidState(upperPos).is(FluidTags.WATER)) {
            // Create upper bloc
            BlockState upperState = VibraniumBlocks.SMALL_PURPLE_DRIPLEAF.get().defaultBlockState()
                    .setValue(SmallDripleafBlock.HALF, DoubleBlockHalf.UPPER)
                    .setValue(SmallDripleafBlock.FACING, randomFacing)
                    .setValue(BlockStateProperties.WATERLOGGED, world.getFluidState(upperPos).is(FluidTags.WATER));

            safeSetBlock(world, upperPos, upperState, box);
        }
    }

    private void placeVineGrapnel(WorldGenLevel world, BlockPos target, RandomSource random, BoundingBox box) {
        boolean placed = false;
        if (world.getBlockState(target.above()).isFaceSturdy(world, target.above(), Direction.DOWN)) {
            // Add Age and Berries to ensure it stays as PurpleCaveVines
            BlockState head = VibraniumBlocks.PURPLE_CAVE_VINES.get().defaultBlockState()
                    .setValue(CaveVinesBlock.AGE, random.nextInt(25))
                    .setValue(CaveVinesBlock.BERRIES, random.nextFloat() < 0.15f);
            safeSetBlock(world, target, head, box);
            placed = true;
        }

        if (!placed) {
            BlockState wallVine = VibraniumBlocks.PURPLE_VINE.get().defaultBlockState();
            boolean wallFound = false;
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos neighbor = target.relative(dir);
                BlockState neighborState = world.getBlockState(neighbor);

                if (neighborState.isSolidRender()) {
                    wallVine = wallVine.setValue(VineBlock.getPropertyForFace(dir), true);
                    wallFound = true;
                }
            }
            if (wallFound) safeSetBlock(world, target, wallVine, box);
        }
    }

    ///Helper method to create a bounding box using the position and the radius of the meteorite
    private static BoundingBox makeBoundingBox(BlockPos origin, int radius) {
        int margin = 3; // Margin to make room for the noise/deformation
        return new BoundingBox(
                origin.getX() - radius - margin,
                origin.getY() - radius - margin,
                origin.getZ() - radius - margin,
                origin.getX() + radius + margin,
                origin.getY() + radius + margin,
                origin.getZ() + radius + margin
        );
    }
    ///Helper method to set blocks only if they are within writable chunk boundaries.
    private void safeSetBlock(WorldGenLevel world, BlockPos pos, BlockState state, BoundingBox box) {
        if (box.isInside(pos) && world.ensureCanWrite(pos)) {
            world.setBlock(pos, state, 3);
        }
    }

}
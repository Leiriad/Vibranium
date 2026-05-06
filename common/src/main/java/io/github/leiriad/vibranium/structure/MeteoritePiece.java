package io.github.leiriad.vibranium.structure;

import io.github.leiriad.vibranium.init.VibraniumBlocks;
import io.github.leiriad.vibranium.init.VibraniumStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
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
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

public class MeteoritePiece extends StructurePiece {
    // PROPERTIES
    private final int radius;
    private final int surfaceHeight;
    private final float waterFillRatio;
    private final boolean isSurfacePiece;

    // CONSTRUCTORS
    public MeteoritePiece(RandomSource random, BlockPos origin, int radius, boolean isSurface, int surfaceHeight) {
        super(VibraniumStructures.METEORITE_PIECE.get(), 0, makeBoundingBox(origin, radius));
        this.radius = radius;
        this.isSurfacePiece = isSurface;
        this.waterFillRatio = determineWaterLevel(random, radius);
        this.surfaceHeight = surfaceHeight;
    }

    public MeteoritePiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(VibraniumStructures.METEORITE_PIECE.get(), tag);
        this.radius = tag.getInt("Radius").orElse(0);
        this.surfaceHeight = tag.getInt("surfaceHeight").orElse(320);
        this.isSurfacePiece = tag.getBoolean("IsSurface").orElse(false);
        this.waterFillRatio = tag.getFloat("waterFillRatio").orElse(0.5f);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putInt("Radius", this.radius);
        tag.putInt("surfaceHeight", this.surfaceHeight);
        tag.putFloat("waterFillRatio", this.waterFillRatio);
        tag.putBoolean("IsSurface", this.isSurfacePiece);
    }

    @Override
    public void postProcess(WorldGenLevel world, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos origin) {
        BlockPos finalOrigin = origin;
        int radius = this.radius;

        // Calculate theoretical volume for validation
        double theoreticalVolume = (4.0 / 3.0) * Math.PI * Math.pow(radius, 3);
        int blocksPlaced = 0;
        int[] vibraniumOreCounter = new int[]{0};

        // FIRST PASS: Main structural generation and erosion
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance <= radius) {
                        BlockPos target = finalOrigin.offset(x, y, z);
                        double noiseFactor = getNoise(target.getX(), target.getY(), target.getZ());
                        double distortedDistance = distance + noiseFactor;

                        if (distortedDistance <= radius) {
                            // Run the erosion and shell placement logic
                            generateStructure(world, target, distortedDistance, radius, finalOrigin, random, vibraniumOreCounter, box);

                            // Count solid shell blocks for density validation
                            if (distortedDistance > radius - 3) {
                                blocksPlaced++;
                            }
                        }
                    }
                }
            }
        }

        // SECOND PASS: Cavity decoration (Floor & Ceiling)
        double thickness = Math.max(2.0, radius * 0.25);
        double innerRadiusSq = Math.pow(radius - thickness, 2);
        int waterLevel = (int) (finalOrigin.getY() - (radius - thickness) + ((radius - thickness) * 2 * this.waterFillRatio));

        for (int x = box.minX(); x <= box.maxX(); x++) {
            for (int z = box.minZ(); z <= box.maxZ(); z++) {
                double dx = x - finalOrigin.getX();
                double dz = z - finalOrigin.getZ();
                double horizontalDistSq = (dx * dx) + (dz * dz);

                if (horizontalDistSq > innerRadiusSq) continue;

                for (int y = box.minY(); y <= box.maxY(); y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    double dy = y - finalOrigin.getY();
                    double distSq = horizontalDistSq + (dy * dy);

                    // Check if we are inside the hollow cavity
                    if (distSq <= innerRadiusSq) {
                        BlockState state = world.getBlockState(pos);

                        // 1. FLOOR DETECTION: If current is empty and block below is solid
                        if (state.isAir() || state.is(Blocks.WATER)) {
                            BlockPos belowPos = pos.below();
                            BlockState belowState = world.getBlockState(belowPos);

                            if (belowState.isSolid() && !isVibraniumFloor(belowState)) {
                                generateFloor(world, belowPos, waterLevel, box);
                                decorateFloor(world, pos, random, box, waterLevel);
                            }
                        }

                        // 2. CEILING DETECTION: If current is air and block above is solid
                        if (state.isAir()) {
                            BlockPos abovePos = pos.above();
                            BlockState aboveState = world.getBlockState(abovePos);

                            if (aboveState.isSolid()) {
                                if (!aboveState.is(Blocks.BLACKSTONE)) {
                                    safeSetBlock(world, abovePos, Blocks.BLACKSTONE.defaultBlockState(), box);
                                }
                                decorateCeiling(world, pos, random, box);
                            }
                        }
                    }
                }
            }
        }

        // Dynamic validation: prevent tiny or failed meteorites
        if ((double) blocksPlaced / theoreticalVolume < 0.1) return;

        // Guaranteed ore placement (minimum 2 ores per meteorite)
        if (vibraniumOreCounter[0] < 2) {
            BlockState ore = VibraniumBlocks.VIBRANIUM_ORE.get().defaultBlockState();
            if (box.isInside(finalOrigin) && world.getBlockState(finalOrigin).is(Blocks.BLACKSTONE)) {
                safeSetBlock(world, finalOrigin, ore, box);
                safeSetBlock(world, finalOrigin.below(), ore, box);
            }
        }
    }

    private float determineWaterLevel(RandomSource random, int radius) {
        if (random.nextFloat() < 0.10f && radius >= 4) return 0.05f;
        float targetDepth = 1.5f + random.nextFloat();
        float estimatedInnerDiameter = (radius * 0.75f) * 2.0f;
        return Math.min(0.25f, targetDepth / estimatedInnerDiameter);
    }

    private double getNoise(int x, int y, int z) {
        // Multi-frequency noise for organic surface deformation
        return Math.sin(x * 0.2) * 1.5 + Math.cos(y * 0.2) * 1.5 + Math.sin(z * 0.2) * 1.5;
    }

    private int[] generateStructure(WorldGenLevel world, BlockPos target, double distortedDistance, int radius, BlockPos origin, RandomSource random, int[] oreCounter, BoundingBox box) {
        if (distortedDistance > radius) return oreCounter;

        double thickness = Math.max(1.8, radius * 0.2);
        double innerLimit = radius - thickness;

        BlockState stateAtTarget = world.getBlockState(target);
        boolean isActuallyAirOrWater = stateAtTarget.isAir() || stateAtTarget.getFluidState().is(FluidTags.WATER);
        boolean isPhysicallyAtSurface = target.getY() >= (this.surfaceHeight - 2);

        // CLIFF/SURFACE EROSION LOGIC
        // If exposed to air in a surface context, apply heavy procedural erosion
        if (this.isSurfacePiece && isPhysicallyAtSurface && isActuallyAirOrWater) {
            double erosionNoise = Math.sin(target.getX() * 0.2) + Math.cos(target.getZ() * 0.2) + Math.sin(target.getY() * 0.2);
            // If noise threshold is met, cancel block placement (creating holes in the shell)
            if (erosionNoise > 0.4) return oreCounter;
        }

        // OUTER SHELL ZONE
        if (distortedDistance > innerLimit) {
            generateShell(world, target, isActuallyAirOrWater, 0.5f, random, box);
        }
        // FOUNDATION ZONE (Layer beneath the shell)
        else if (distortedDistance > innerLimit - 1.5) {
            if (!isActuallyAirOrWater || target.getY() < origin.getY()) {
                BlockState layerState = (target.getY() <= origin.getY() + 1) ?
                        VibraniumBlocks.VIBRANIUM_DIRT.get().defaultBlockState() : Blocks.BLACKSTONE.defaultBlockState();
                safeSetBlock(world, target, layerState, box);
                oreCounter[0] += generateVibraniumVeins(world, target, isActuallyAirOrWater, random, distortedDistance, radius, box);
            }
        }
        // INTERNAL CAVITY ZONE
        else {
            int waterLevel = (int) (origin.getY() - (radius - thickness) + ((radius - thickness) * 2 * this.waterFillRatio));
            if (!isActuallyAirOrWater) hollowOut(world, target, waterLevel, box);
        }
        return oreCounter;
    }

    private void hollowOut(WorldGenLevel world, BlockPos target, int waterLevel, BoundingBox box) {
        safeSetBlock(world, target, target.getY() <= waterLevel ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), box);
    }

    private int generateVibraniumVeins(WorldGenLevel world, BlockPos target, boolean isAtSurface, RandomSource random, double distortedDistance, int radius, BoundingBox box) {
        double oreNoise = Math.sin(target.getX() * 0.5) + Math.sin(target.getY() * 0.5) + Math.sin(target.getZ() * 0.5);
        boolean isInnerShell = distortedDistance < (radius - 1.5) && distortedDistance > (radius - 2.5);

        if (oreNoise > 2.4 || (isInnerShell && random.nextFloat() < 0.2f)) {
            safeSetBlock(world, target, VibraniumBlocks.VIBRANIUM_ORE.get().defaultBlockState(), box);
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
            shell = (isAtSurface && random.nextFloat() < mossChance) ? Blocks.END_STONE.defaultBlockState() : Blocks.BLACKSTONE.defaultBlockState();
        } else {
            shell = (isAtSurface && random.nextFloat() < mossChance) ? Blocks.MOSSY_COBBLESTONE.defaultBlockState() : Blocks.BLACKSTONE.defaultBlockState();
        }
        safeSetBlock(world, target, shell, box);
    }

    private void generateFloor(WorldGenLevel world, BlockPos target, int waterLevel, BoundingBox box) {
        double patchNoise = Math.sin(target.getX() * 0.12) + Math.sin(target.getZ() * 0.12);
        BlockState floorState;

        if (target.getY() < waterLevel) { // SEDIMENT LOGIC
            if (patchNoise > 0.8) floorState = VibraniumBlocks.BLACKGRAVEL.get().defaultBlockState();
            else if (patchNoise > 0.0) floorState = VibraniumBlocks.BLACKCLAY.get().defaultBlockState();
            else floorState = VibraniumBlocks.VIBRANIUM_DIRT.get().defaultBlockState();
        } else { // SURFACE LOGIC
            floorState = VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get().defaultBlockState();
        }
        safeSetBlock(world, target, floorState, box);
    }

    private void decorateCeiling(WorldGenLevel world, BlockPos target, RandomSource random, BoundingBox box) {
        if (random.nextFloat() < 0.15f) {
            placeDripstoneColumn(world, target, random, box);
        } else if (random.nextFloat() < 0.40f) {
            placeVineGrapnel(world, target, random, box);
        }
    }

    private void decorateFloor(WorldGenLevel world, BlockPos target, RandomSource random, BoundingBox box, int waterLevel) {
        BlockState floorState = world.getBlockState(target.below());
        BlockState currentState = world.getBlockState(target);
        boolean isUnderWater = target.getY() <= waterLevel;

        if (!currentState.isAir() && !currentState.is(Blocks.WATER)) return;

        if (!isUnderWater) {
            if (floorState.is(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get()) || floorState.is(VibraniumBlocks.VIBRANIUM_DIRT.get())) {
                float r = random.nextFloat();
                // Moss & Carpet Logic
                if (r < 0.20f) {
                    safeSetBlock(world, target.below(), VibraniumBlocks.PURPLE_MOSS_BLOCK.get().defaultBlockState(), box);
                    if (random.nextBoolean()) safeSetBlock(world, target, VibraniumBlocks.PURPLE_MOSS_CARPET.get().defaultBlockState(), box);
                } else {
                    if (floorState.is(VibraniumBlocks.VIBRANIUM_DIRT.get())) {
                        safeSetBlock(world, target.below(), VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get().defaultBlockState(), box);
                    }
                    if (r < 0.35f) safeSetBlock(world, target, VibraniumBlocks.PURPLE_MOSS_CARPET.get().defaultBlockState(), box);
                    else if (r < 0.45f) {
                        Direction randomFacing = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                        safeSetBlock(world, target, VibraniumBlocks.BIG_PURPLE_DRIPLEAF.get().defaultBlockState().setValue(SmallDripleafBlock.FACING, randomFacing), box);
                    }
                }
            }
        } else {
            if ((floorState.is(VibraniumBlocks.BLACKCLAY.get()) || floorState.is(VibraniumBlocks.VIBRANIUM_DIRT.get())) && random.nextFloat() < 0.30f) {
                placeSmallDripleaf(world, target, random, box);
            }
        }
    }

    private void placeDripstoneColumn(WorldGenLevel world, BlockPos pos, RandomSource random, BoundingBox box) {
        Direction direction = Direction.DOWN;
        safeSetBlock(world, pos.relative(direction.getOpposite()), Blocks.DRIPSTONE_BLOCK.defaultBlockState(), box);

        BlockState pointedDripstone = Blocks.POINTED_DRIPSTONE.defaultBlockState()
                .setValue(PointedDripstoneBlock.TIP_DIRECTION, direction)
                .setValue(PointedDripstoneBlock.THICKNESS, DripstoneThickness.TIP);
        safeSetBlock(world, pos, pointedDripstone, box);

        if (random.nextBoolean()) {
            BlockPos nextPos = pos.relative(direction);
            if (world.isEmptyBlock(nextPos) || world.getFluidState(nextPos).is(FluidTags.WATER)) {
                safeSetBlock(world, pos, pointedDripstone.setValue(PointedDripstoneBlock.THICKNESS, DripstoneThickness.FRUSTUM), box);
                safeSetBlock(world, nextPos, pointedDripstone, box);
            }
        }
    }

    private void placeSmallDripleaf(WorldGenLevel world, BlockPos target, RandomSource random, BoundingBox box) {
        Direction randomFacing = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        boolean lowerIsWater = world.getBlockState(target).getFluidState().is(FluidTags.WATER);
        safeSetBlock(world, target, VibraniumBlocks.SMALL_PURPLE_DRIPLEAF.get().defaultBlockState()
                .setValue(SmallDripleafBlock.HALF, DoubleBlockHalf.LOWER).setValue(SmallDripleafBlock.FACING, randomFacing)
                .setValue(BlockStateProperties.WATERLOGGED, lowerIsWater), box);

        BlockPos upperPos = target.above();
        if (box.isInside(upperPos)) {
            boolean upperIsWater = world.getBlockState(upperPos).getFluidState().is(FluidTags.WATER);
            safeSetBlock(world, upperPos, VibraniumBlocks.SMALL_PURPLE_DRIPLEAF.get().defaultBlockState()
                    .setValue(SmallDripleafBlock.HALF, DoubleBlockHalf.UPPER).setValue(SmallDripleafBlock.FACING, randomFacing)
                    .setValue(BlockStateProperties.WATERLOGGED, upperIsWater), box);
        }
    }

    private void placeVineGrapnel(WorldGenLevel world, BlockPos target, RandomSource random, BoundingBox box) {
        boolean placed = false;
        if (world.getBlockState(target.above()).isFaceSturdy(world, target.above(), Direction.DOWN)) {
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
                if (world.getBlockState(target.relative(dir)).isSolidRender()) {
                    wallVine = wallVine.setValue(VineBlock.getPropertyForFace(dir), true);
                    wallFound = true;
                }
            }
            if (wallFound) safeSetBlock(world, target, wallVine, box);
        }
    }

    private static BoundingBox makeBoundingBox(BlockPos origin, int radius) {
        int margin = radius;
        return new BoundingBox(origin.getX() - radius - margin, origin.getY() - radius - margin, origin.getZ() - radius - margin,
                origin.getX() + radius + margin, origin.getY() + radius + margin, origin.getZ() + radius + margin);
    }

    private void safeSetBlock(WorldGenLevel world, BlockPos pos, BlockState state, BoundingBox box) {
        if (box.isInside(pos) && world.ensureCanWrite(pos)) world.setBlock(pos, state, 2 | 16);
    }

    private boolean isVibraniumFloor(BlockState state) {
        return state.is(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get()) || state.is(VibraniumBlocks.VIBRANIUM_DIRT.get()) ||
                state.is(VibraniumBlocks.BLACKCLAY.get()) || state.is(VibraniumBlocks.PURPLE_MOSS_BLOCK.get());
    }
}
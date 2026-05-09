package io.github.leiriad.vibranium.structure;

import io.github.leiriad.vibranium.block.SmallPurpleDripleaf;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import io.github.leiriad.vibranium.init.VibraniumStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import java.util.*;
import java.util.stream.Collectors;

public class MeteoritePiece extends StructurePiece {
    // PROPERTIES
    private final int radius;
    private final float waterFillRatio;
    private final boolean isSurfacePiece;
    private final double thickness;

    // CONSTRUCTORS
    public MeteoritePiece(RandomSource random, BlockPos origin, int radius, boolean isSurface) {
        super(VibraniumStructures.METEORITE_PIECE.get(), 0, makeBoundingBox(origin, radius));
        this.radius = radius;
        this.isSurfacePiece = isSurface;
        this.waterFillRatio = determineWaterFillRatio(random, radius);
        this.thickness = Math.max(2.0, radius * 0.25);
    }
    public MeteoritePiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(VibraniumStructures.METEORITE_PIECE.get(), tag);
        this.radius = tag.getInt("Radius").orElse(0);
        this.isSurfacePiece = tag.getBoolean("IsSurface").orElse(false);
        this.waterFillRatio = tag.getFloat("waterFillRatio").orElse(0.5f);
        this.thickness = tag.getDouble("thickness").orElse(0.5);
    }

    //METHODS
    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putInt("Radius", this.radius);
        tag.putFloat("waterFillRatio", this.waterFillRatio);
        tag.putBoolean("IsSurface", this.isSurfacePiece);
        tag.putDouble("thickness", this.thickness);
    }
    @Override
    public void postProcess(WorldGenLevel world, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos origin) {
        //Using a buffer to insure piece quality
        Map<BlockPos, BlockState> blocksToPlace = new LinkedHashMap<>();

        //BUFFER GENERATION
        //create shape
        generateInitialShape(world, origin,random,box,blocksToPlace);

        //create cavity
        int waterLevel = (int) (origin.getY() - (radius - thickness) + ((radius - thickness) * 2 * this.waterFillRatio));
        generateCavity(origin,box,waterLevel, blocksToPlace);

        //Cavity decoration (Floor & Ceiling)
        double innerRadiusSq = Math.pow(radius - thickness, 2);
        generateDecoration(world, origin, innerRadiusSq, waterLevel, random, box, blocksToPlace);

        //COUNTING
        int oreCount = 0;
        int dirtCount = 0;
        int shellBlocksCount = 0;
        int waterBlocksCount = 0;
        int cavevinesCount = 0;
        for (BlockState state : blocksToPlace.values()) {
            if (state.is(VibraniumBlocks.VIBRANIUM_ORE.get())) oreCount++;
            if (state.is(VibraniumBlocks.VIBRANIUM_DIRT.get())||state.is(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get())) dirtCount++;
            if (state.is(Blocks.BLACKSTONE)) shellBlocksCount++;
            if (state.is(Blocks.WATER)) waterBlocksCount++;
            if (state.is(VibraniumBlocks.PURPLE_CAVE_VINES)) cavevinesCount++;
        }

        //VALIDATION
        if (oreCount < 2 || dirtCount < 1 || shellBlocksCount<1) {
            repairStructure(origin, blocksToPlace, oreCount, dirtCount, shellBlocksCount, box, world);
        }
        if (cavevinesCount < 1) {
            repairDecorations(blocksToPlace, random);
        }
        if (waterBlocksCount < 1) {
            repairWater(blocksToPlace, random);
        }

        //PLACE PIECE
        blocksToPlace.forEach((pos, state) -> {
            if (!world.getBlockState(pos).is(Blocks.BEDROCK)) {
                world.setBlock(pos, state, 3);
            }
            world.getChunk(pos).markUnsaved();
        });

    }

    private void generateInitialShape(WorldGenLevel world, BlockPos origin, RandomSource random, BoundingBox box, Map<BlockPos, BlockState> blocksToPlace) {

        //Main structural generation and erosion
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);

                    if (distance <= radius) {
                        BlockPos target = origin.offset(x, y, z);
                        double noiseFactor = getNoise(target.getX(), target.getY(), target.getZ());
                        double distortedDistance = distance + noiseFactor;

                        if (distortedDistance <= radius) {
                            // Run the erosion and shell placement logic
                            generateStructure(world, target, distortedDistance, radius, origin, random, box, blocksToPlace);
                        }
                    }
                }
            }
        }
    }
    private void generateCavity(BlockPos origin, BoundingBox box, int waterLevel, Map<BlockPos, BlockState> blocksToPlace) {
        double innerRadius = radius - thickness;
        double innerRadiusSq = innerRadius * innerRadius;

        for (int x = (int)-innerRadius; x <= innerRadius; x++) {
            for (int y = (int)-innerRadius; y <= innerRadius; y++) {
                for (int z = (int)-innerRadius; z <= innerRadius; z++) {
                    double distSq = x*x + y*y + z*z;
                    if (distSq <= innerRadiusSq) {
                        BlockPos target = origin.offset(x, y, z);
                        if (box.isInside(target)) {
                            BlockState state = (target.getY() <= waterLevel) ?
                                    Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                            blocksToPlace.put(target, state);
                        }
                    }
                }
            }
        }
    }
    private void generateDecoration(WorldGenLevel world, BlockPos origin, double innerRadiusSq, int waterLevel, RandomSource random, BoundingBox box, Map<BlockPos, BlockState> blocksToPlace) {
        for (int x = box.minX(); x <= box.maxX(); x++) {
            for (int z = box.minZ(); z <= box.maxZ(); z++) {
                double dx = x - origin.getX();
                double dz = z - origin.getZ();
                double horizontalDistSq = (dx * dx) + (dz * dz);

                if (horizontalDistSq > innerRadiusSq) continue;

                for (int y = box.minY(); y <= box.maxY(); y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    double dy = y - origin.getY();
                    double distSq = horizontalDistSq + (dy * dy);

                    // Check if we are inside the hollow cavity
                    if (distSq <= innerRadiusSq) {
                        BlockState state = getEffectiveState(world, pos, blocksToPlace);

                        // 1. FLOOR DETECTION: If current is empty and block below is solid
                        if (state.isAir() || state.is(Blocks.WATER)) {
                            BlockPos belowPos = pos.below();
                            BlockState belowState = getEffectiveState(world, belowPos,  blocksToPlace);

                            if (belowState.isSolid() && !isVibraniumFloor(belowState)) {
                                generateFloor(belowPos, waterLevel, box, blocksToPlace);
                                decorateFloor(world, pos, random, box, waterLevel, blocksToPlace);
                            }
                        }

                        // 2. CEILING DETECTION: If current is air and block above is solid
                        if (state.isAir()) {
                            BlockPos abovePos = pos.above();
                            BlockState aboveState = getEffectiveState(world, abovePos, blocksToPlace);

                            if (aboveState.isSolid()) {
                                if (!aboveState.is(Blocks.BLACKSTONE)) {
                                    safeSetBlock(abovePos, Blocks.BLACKSTONE.defaultBlockState(), box, blocksToPlace);
                                }
                                decorateCeiling(world, pos, random, box, blocksToPlace);
                            }
                        }
                    }
                }
            }
        }
    }

    private void repairStructure(BlockPos origin, Map<BlockPos, BlockState> blocksToPlace, int orecount, int dirtcount, int shellblockcount, BoundingBox box, WorldGenLevel world) {
        //Find wall
        List<BlockPos> validAnchors = blocksToPlace.entrySet().stream()
                .filter(e -> {
                    BlockState state = e.getValue();
                    return state.isSolid() &&
                            !state.is(VibraniumBlocks.VIBRANIUM_ORE.get()) &&
                            !state.is(VibraniumBlocks.VIBRANIUM_DIRT.get()) &&
                            !state.is(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get());
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (validAnchors.isEmpty()) return;
        Collections.shuffle(validAnchors);

        //Repare loot
        for (BlockPos anchorPos : validAnchors) {
            if (orecount >= 2 && dirtcount >= 1) break;

            if (orecount < 2) {
                blocksToPlace.put(anchorPos, VibraniumBlocks.VIBRANIUM_ORE.get().defaultBlockState());
                orecount++;
            }
            else if (dirtcount < 1) {
                BlockPos abovePos = anchorPos.above();
                BlockState stateAbove = blocksToPlace.get(abovePos);

                if (box.isInside(abovePos) && (stateAbove == null || stateAbove.isAir() || stateAbove.is(Blocks.WATER))) {
                    blocksToPlace.put(abovePos, VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get().defaultBlockState());
                } else {
                    blocksToPlace.put(anchorPos, VibraniumBlocks.VIBRANIUM_DIRT.get().defaultBlockState());
                }
                dirtcount++;
            }
        }

        //Repare blackstone
        if (shellblockcount < 1) {
            // On cherche un bloc solide qui n'est toujours pas du loot
            for (BlockPos pos : validAnchors) {
                BlockState current = blocksToPlace.get(pos);
                if (!current.is(VibraniumBlocks.VIBRANIUM_ORE.get()) && !current.is(VibraniumBlocks.VIBRANIUM_DIRT.get())) {
                    blocksToPlace.put(pos, Blocks.BLACKSTONE.defaultBlockState());
                    break;
                }
            }
        }
    }
    private void repairDecorations(Map<BlockPos, BlockState> blocksToPlace, RandomSource random) {

        for (Map.Entry<BlockPos, BlockState> entry : blocksToPlace.entrySet()) {
            BlockPos pos = entry.getKey();
            if (entry.getValue().isAir()) {
                BlockState above = blocksToPlace.get(pos.above());
                if (above != null && above.isSolid()) {
                    blocksToPlace.put(pos, VibraniumBlocks.PURPLE_CAVE_VINES.get().defaultBlockState()
                            .setValue(CaveVinesBlock.AGE, random.nextInt(10))
                            .setValue(CaveVinesBlock.BERRIES, true));
                    break;
                }
            }
        }
    }
    private void repairWater(Map<BlockPos, BlockState> blocksToPlace, RandomSource random) {
        // Convert map keys to list to pick a random spot inside
        List<BlockPos> airPositions = blocksToPlace.entrySet().stream()
                .filter(entry -> entry.getValue().isAir())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (airPositions.isEmpty()) return;

        // Randomise selection
        Collections.shuffle(airPositions);

        //place 1 to 3 sources
        int sourcesPlaced = 0;
        int maxSources = 1 + random.nextInt(3);

        for (BlockPos pos : airPositions) {
            if (sourcesPlaced >= maxSources) break;

            // check for support
            boolean adjacentToWall = false;
            for (Direction dir : Direction.values()) {
                BlockState neighbor = blocksToPlace.get(pos.relative(dir));
                if (neighbor != null && (neighbor.isSolid()&& !neighbor.is(Blocks.POINTED_DRIPSTONE))) {
                    adjacentToWall = true;
                    break;
                }
            }

            if (adjacentToWall) {
                blocksToPlace.put(pos, Blocks.WATER.defaultBlockState());
                sourcesPlaced++;
            }
        }
    }

    //SUB-METHODS
    private void generateStructure(WorldGenLevel world, BlockPos target, double distortedDistance, int radius, BlockPos origin, RandomSource random, BoundingBox box,  Map<BlockPos, BlockState> blocksToPlace) {
        if (distortedDistance > radius) return;

        double innerLimit = radius - thickness;

        BlockState stateAtTarget = getEffectiveState(world, target, blocksToPlace);
        boolean isActuallyAirOrWater = stateAtTarget.isAir() || stateAtTarget.getFluidState().is(FluidTags.WATER);

        // CLIFF/SURFACE EROSION LOGIC
        // If exposed to air in a surface context, apply heavy procedural erosion
        if (this.isSurfacePiece && isActuallyAirOrWater) {
            int realSurfaceY = world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, target.getX(), target.getZ());
            if (target.getY() > realSurfaceY) {
                // leave 1% shell debris
                if (random.nextFloat() > 0.1f);
            }

            //Flying blocks fix
            if (target.getY() > realSurfaceY - 2 && isActuallyAirOrWater) {
                BlockState belowState = getEffectiveState(world, target.below(), blocksToPlace);
                boolean isValidSupportBlock = belowState.is(Blocks.BLACKSTONE)|| belowState.is(VibraniumBlocks.VIBRANIUM_ORE.get())||belowState.is(Blocks.MOSSY_COBBLESTONE) ||belowState.is(Blocks.END_STONE);
                boolean hasSupport = belowState.isSolid() || isValidSupportBlock;
                if (!hasSupport) {
                    return;
                }
            }
        }

        // OUTER SHELL ZONE
        if (distortedDistance > innerLimit) {
            generateShell(world, target, isActuallyAirOrWater, 0.5f, random, box, blocksToPlace);
        }
        // FOUNDATION ZONE (Layer beneath the shell)
        else if (distortedDistance > innerLimit - 1.5) {
            if (!isActuallyAirOrWater || target.getY() < origin.getY()) {
                BlockState layerState = (target.getY() <= origin.getY() + 1) ?
                        VibraniumBlocks.VIBRANIUM_DIRT.get().defaultBlockState() : Blocks.BLACKSTONE.defaultBlockState();
                safeSetBlock(target, layerState, box,  blocksToPlace);
                generateVibraniumVeins(world, target, isActuallyAirOrWater, random, distortedDistance, radius, box, blocksToPlace);
            }
        }
    }
    private void generateShell(WorldGenLevel world, BlockPos target, boolean isAtSurface, float mossChance, RandomSource random, BoundingBox box,Map<BlockPos, BlockState> blocksToPlace) {
        boolean isEnd = world.getLevel().dimension().equals(net.minecraft.world.level.Level.END);
        BlockState shell;
        if (isEnd) {
            shell = (isAtSurface && random.nextFloat() < mossChance) ? Blocks.END_STONE.defaultBlockState() : Blocks.BLACKSTONE.defaultBlockState();
        } else {
            shell = (isAtSurface && random.nextFloat() < mossChance) ? Blocks.MOSSY_COBBLESTONE.defaultBlockState() : Blocks.BLACKSTONE.defaultBlockState();
        }
        safeSetBlock(target, shell, box, blocksToPlace);
    }
    private void generateVibraniumVeins(WorldGenLevel world, BlockPos target, boolean isAtSurface, RandomSource random, double distortedDistance, int radius, BoundingBox box, Map<BlockPos, BlockState> blocksToPlace) {
        double oreNoise = Math.sin(target.getX() * 0.5) + Math.sin(target.getY() * 0.5) + Math.sin(target.getZ() * 0.5);
        boolean isInnerShell = distortedDistance < (radius - 1.5) && distortedDistance > (radius - 2.5);

        if (oreNoise > 2.4 || (isInnerShell && random.nextFloat() < 0.2f)) {
            safeSetBlock(target, VibraniumBlocks.VIBRANIUM_ORE.get().defaultBlockState(), box, blocksToPlace);
        } else {
            generateShell(world, target, isAtSurface, 0.2f, random, box, blocksToPlace);
        }
    }

    private void generateFloor(BlockPos target, int waterLevel, BoundingBox box, Map<BlockPos, BlockState> blocksToPlace) {
        double patchNoise = Math.sin(target.getX() * 0.12) + Math.sin(target.getZ() * 0.12);
        BlockState floorState;

        boolean isBank = Math.abs(target.getY() - waterLevel) <= 1;

        if (target.getY() < waterLevel) {
            // lake bottom
            if (patchNoise > 0.6) floorState = VibraniumBlocks.BLACKCLAY.get().defaultBlockState();
            else if (patchNoise > 0.0) floorState = VibraniumBlocks.BLACKGRAVEL.get().defaultBlockState();
            else floorState = VibraniumBlocks.VIBRANIUM_DIRT.get().defaultBlockState();
        } else if (isBank) {
            // lake side
            floorState = (patchNoise > -0.2) ?
                    VibraniumBlocks.BLACKCLAY.get().defaultBlockState() :
                    VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get().defaultBlockState();
        } else {
            // surface
            floorState = VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get().defaultBlockState();
        }
        safeSetBlock(target, floorState, box, blocksToPlace);
    }
    private void decorateFloor(WorldGenLevel world, BlockPos target, RandomSource random, BoundingBox box, int waterLevel, Map<BlockPos, BlockState> blocksToPlace) {
        BlockState floorState = getEffectiveState(world, target.below(), blocksToPlace);
        BlockState currentState = getEffectiveState(world, target, blocksToPlace);
        boolean isUnderWater = target.getY() <= waterLevel;

        // On ne décore que si l'espace est libre (air ou eau)
        if (!currentState.isAir() && !currentState.is(Blocks.WATER)) return;

        if (!isUnderWater) {
            // Out of water
            float r = random.nextFloat();

            // Clay
            if (floorState.is(VibraniumBlocks.BLACKCLAY.get())) {
                if (r < 0.50f) { // 50% dripleaf rate
                    placeSmallDripleaf(world, target, random, box, blocksToPlace);
                }
            }
            // Grass/dirt
            else if (floorState.is(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get()) || floorState.is(VibraniumBlocks.VIBRANIUM_DIRT.get())) {

                if (r < 0.20f) {
                    // Moss
                    safeSetBlock(target.below(), VibraniumBlocks.PURPLE_MOSS_BLOCK.get().defaultBlockState(), box, blocksToPlace);
                    if (random.nextBoolean()) safeSetBlock(target, VibraniumBlocks.PURPLE_MOSS_CARPET.get().defaultBlockState(), box, blocksToPlace);
                }
                else if (r < 0.45f) {
                    // plant/carpet
                    if (r < 0.35f) {
                        safeSetBlock(target, VibraniumBlocks.PURPLE_MOSS_CARPET.get().defaultBlockState(), box, blocksToPlace);
                    } else {
                        // Big Dripleaf
                        Direction randomFacing = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                        safeSetBlock(target, VibraniumBlocks.BIG_PURPLE_DRIPLEAF.get().defaultBlockState()
                                .setValue(BigDripleafBlock.FACING, randomFacing), box, blocksToPlace);
                    }
                }
            }
        } else {
            //underwater
            if ((floorState.is(VibraniumBlocks.BLACKCLAY.get()) || floorState.is(VibraniumBlocks.VIBRANIUM_DIRT.get())) && random.nextFloat() < 0.30f) {
                placeSmallDripleaf(world, target, random, box, blocksToPlace);
            }
        }
    }
    private void placeSmallDripleaf(WorldGenLevel world, BlockPos target, RandomSource random, BoundingBox box, Map<BlockPos, BlockState> blocksToPlace) {
        Direction randomFacing = Direction.Plane.HORIZONTAL.getRandomDirection(random);

        //bottom
        BlockState currentState = getEffectiveState(world, target, blocksToPlace);
        boolean lowerIsWater = currentState.getFluidState().is(FluidTags.WATER);

        safeSetBlock(target, VibraniumBlocks.SMALL_PURPLE_DRIPLEAF.get().defaultBlockState()
                .setValue(SmallDripleafBlock.HALF, DoubleBlockHalf.LOWER)
                .setValue(SmallDripleafBlock.FACING, randomFacing)
                .setValue(BlockStateProperties.WATERLOGGED, lowerIsWater), box, blocksToPlace);

        //top
        BlockPos upperPos = target.above();
        if (box.isInside(upperPos)) {
            BlockState upperState = getEffectiveState(world, upperPos, blocksToPlace);
            boolean upperIsWater = upperState.getFluidState().is(FluidTags.WATER) || upperState.is(Blocks.WATER);

            safeSetBlock(upperPos, VibraniumBlocks.SMALL_PURPLE_DRIPLEAF.get().defaultBlockState()
                    .setValue(SmallDripleafBlock.HALF, DoubleBlockHalf.UPPER)
                    .setValue(SmallDripleafBlock.FACING, randomFacing)
                    .setValue(BlockStateProperties.WATERLOGGED, upperIsWater), box, blocksToPlace);
        }
    }

    private void decorateCeiling(WorldGenLevel world, BlockPos target, RandomSource random, BoundingBox box, Map<BlockPos, BlockState> blocksToPlace) {
        if (random.nextFloat() < 0.15f) {
            placeDripstoneColumn(world, target, random, box, blocksToPlace);
        } else if (random.nextFloat() < 0.40f) {
            placeVineGrapnel(world, target, random, box, blocksToPlace);
        }
    }
    private void placeDripstoneColumn(WorldGenLevel world, BlockPos pos, RandomSource random, BoundingBox box, Map<BlockPos, BlockState> blocksToPlace) {
        Direction direction = Direction.DOWN;
        safeSetBlock(pos.relative(direction.getOpposite()), Blocks.DRIPSTONE_BLOCK.defaultBlockState(), box, blocksToPlace);

        BlockState pointedDripstone = Blocks.POINTED_DRIPSTONE.defaultBlockState()
                .setValue(PointedDripstoneBlock.TIP_DIRECTION, direction)
                .setValue(PointedDripstoneBlock.THICKNESS, DripstoneThickness.TIP);
        safeSetBlock(pos, pointedDripstone, box, blocksToPlace);

        if (random.nextBoolean()) {
            BlockPos nextPos = pos.relative(direction);
            if (world.isEmptyBlock(nextPos) || world.getFluidState(nextPos).is(FluidTags.WATER)) {
                safeSetBlock(pos, pointedDripstone.setValue(PointedDripstoneBlock.THICKNESS, DripstoneThickness.FRUSTUM), box, blocksToPlace);
                safeSetBlock(nextPos, pointedDripstone, box, blocksToPlace);
            }
        }
    }
    private void placeVineGrapnel(WorldGenLevel world, BlockPos target, RandomSource random, BoundingBox box, Map<BlockPos, BlockState> blocksToPlace) {
        // Cave Vines (Ceiling)
        BlockState stateAbove = getEffectiveState(world, target.above(), blocksToPlace);
        if(isVibraniumCeiling(stateAbove)){
            BlockState head = VibraniumBlocks.PURPLE_CAVE_VINES.get().defaultBlockState()
                    .setValue(CaveVinesBlock.AGE, random.nextInt(25))
                    .setValue(CaveVinesBlock.BERRIES, random.nextFloat() < 0.15f);

            safeSetBlock(target, head, box, blocksToPlace);
        }

        // Wall Vines - Check all 4 horizontal directions
        BlockState wallVine = VibraniumBlocks.PURPLE_VINE.get().defaultBlockState();
        boolean wallFound = false;

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos neighbor = target.relative(dir);
            if (getEffectiveState(world, neighbor, blocksToPlace).isSolidRender()) {
                // This activates the specific face of the vine block
                wallVine = wallVine.setValue(VineBlock.getPropertyForFace(dir), true);
                wallFound = true;
            }
        }

        if (wallFound) {
            safeSetBlock(target, wallVine, box, blocksToPlace);
        }
    }

    private boolean isVibraniumFloor(BlockState state) {
        return state.is(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get()) || state.is(VibraniumBlocks.VIBRANIUM_DIRT.get()) ||
                state.is(VibraniumBlocks.BLACKCLAY.get()) || state.is(VibraniumBlocks.PURPLE_MOSS_BLOCK.get());
    }
    private boolean isVibraniumCeiling(BlockState state) {
        return state.is(BlockTags.STONE_ORE_REPLACEABLES)||state.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES)||
                state.is(Blocks.BLACKSTONE)||state.is(Blocks.MOSSY_COBBLESTONE)||state.is(Blocks.END_STONE)||state.is(VibraniumBlocks.VIBRANIUM_ORE.get());
    }


    //HELPERS
    private static BoundingBox makeBoundingBox(BlockPos origin, int radius) {
        int margin = radius;
        return new BoundingBox(origin.getX() - radius - margin, origin.getY() - radius - margin, origin.getZ() - radius - margin,
                origin.getX() + radius + margin, origin.getY() + radius + margin, origin.getZ() + radius + margin);
    }
    private float determineWaterFillRatio(RandomSource random, int radius) {
        if (random.nextFloat() < 0.10f && radius >= 4) return 0.05f;
        float targetDepth = 1.5f + random.nextFloat();
        float estimatedInnerDiameter = (radius * 0.75f) * 2.0f;
        return Math.min(0.25f, targetDepth / estimatedInnerDiameter);
    }
    private double getNoise(int x, int y, int z) {
        // Multi-frequency noise for organic surface deformation
        return Math.sin(x * 0.2) * 1.5 + Math.cos(y * 0.2) * 1.5 + Math.sin(z * 0.2) * 1.5;
    }
    private void safeSetBlock(BlockPos pos, BlockState state, BoundingBox box, Map<BlockPos, BlockState> blocksToPlace) {
        if (box.isInside(pos)) {
            blocksToPlace.put(pos, state);
        }
    }
    private BlockState getEffectiveState(WorldGenLevel world, BlockPos pos, Map<BlockPos, BlockState> blocksToPlace) {
        // If block is in buffer
        if (blocksToPlace.containsKey(pos)) {
            return blocksToPlace.get(pos);
        }
        // Else check world
        return world.getBlockState(pos);
    }

}
package io.github.leiriad.vibranium.structure;

import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.block.SmallPurpleDripleaf;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import io.github.leiriad.vibranium.init.VibraniumStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.PalettedContainer;
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
        int waterBlocksCount = 0;
        int waterLevel = (int) (origin.getY() - (radius - thickness) + ((radius - thickness) * 2 * this.waterFillRatio));
        waterBlocksCount = generateCavity(origin,box,waterLevel, blocksToPlace, random);

        //Add vibranium
        int oreCount = 0;
        oreCount = generateVibraniumVeins(origin, radius, box, blocksToPlace, random);

        //Cavity decoration (Floor & Ceiling)
        double innerRadiusSq = Math.pow(radius - thickness, 2);
        int[] decoCounts = generateDecoration(world, origin, innerRadiusSq, waterLevel, random, box, blocksToPlace);
        int dirtCount = decoCounts[0];
        int cavevinesCount = decoCounts[1];

        //COUNTING
        int shellBlocksCount = 0;
        for (BlockState state : blocksToPlace.values()) {
            if (state.is(Blocks.BLACKSTONE)) shellBlocksCount++;
        }

        //VALIDATION
        if (oreCount < 2 || dirtCount < 1 || shellBlocksCount<1) {
            repairStructure(origin, blocksToPlace, oreCount, dirtCount, shellBlocksCount, box, world);
        }
        if (cavevinesCount < 1) {
            repairDecorations(blocksToPlace, random);
        }
        if (waterBlocksCount < 1) {
            BlockPos chunkCenter = new BlockPos(chunkPos.getMiddleBlockX(), origin.getY(), chunkPos.getMiddleBlockZ());
            boolean isCentralChunk = origin.closerThan(chunkCenter, 16);

            if (isCentralChunk) {
                repairWater(blocksToPlace, random);
            }
        }

        //PLACE PIECE
        blocksToPlace.forEach((pos, state) -> {
            if (!world.getBlockState(pos).is(Blocks.BEDROCK)) {

                world.setBlock(pos, state, 3);

                if (state.is(Blocks.WATER)) {
                    world.scheduleTick(pos, Fluids.WATER, 0);
                }

            }
        });

        //Create biome
        BoundingBox meteoriteBox = BoundingBox.fromCorners(
                origin.offset(-radius - 2, -radius - 2, -radius - 2),
                origin.offset(radius + 2, radius + 2, radius + 2)
        );

        this.setMeteoriteBiome(world, meteoriteBox);
    }

    private void generateInitialShape(WorldGenLevel world, BlockPos origin, RandomSource random, BoundingBox box, Map<BlockPos, BlockState> blocksToPlace) {
        int margin = 3;
        for (int y = -radius - margin; y <= radius + margin; y++) {
            for (int x = -radius - margin; x <= radius + margin; x++) {
                for (int z = -radius - margin; z <= radius + margin; z++) {
                    double distortedDistance = getDistortedDistance(origin, x, y, z);

                    if (distortedDistance <= radius) {
                        BlockPos target = origin.offset(x, y, z);
                        generateStructure(world, target, distortedDistance, radius, origin, random, box, blocksToPlace);
                    }
                }
            }
        }
    }
    private int generateCavity(BlockPos origin, BoundingBox box, int waterLevel, Map<BlockPos, BlockState> blocksToPlace, RandomSource random) {
        double innerRadius = radius - thickness;
        int margin = 3;
        int waterBlocksCount = 0;

        for (int x = (int)-innerRadius - margin; x <= innerRadius + margin; x++) {
            for (int y = (int)-innerRadius - margin; y <= innerRadius + margin; y++) {
                for (int z = (int)-innerRadius - margin; z <= innerRadius + margin; z++) {

                    double distortedDistance = getDistortedDistance(origin, x, y, z);

                    if (distortedDistance <= innerRadius) {
                        BlockPos target = origin.offset(x, y, z);
                        if (box.isInside(target)) {
                            BlockState state = (target.getY() <= waterLevel) ?
                                    Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                            if(state.is(Blocks.WATER)){
                                waterBlocksCount++;
                            }
                            blocksToPlace.put(target, state);
                        }
                    }
                }
            }
        }
        //add waterfall(s)
        float waterfallChance = Math.clamp((radius - 3) / 7.0f, 0, 1.0f);//0% chances if radius = 3/100% if radius>10
        int maxWaterfalls = Math.clamp((radius - 4) / 2, 1, 5);
        if (random.nextFloat() < waterfallChance) {
            for (int i = 0; i < maxWaterfalls; i++) {
                waterBlocksCount += generateRandomWaterfall(origin, innerRadius, waterLevel, box, blocksToPlace, random);
            }
        }
        return waterBlocksCount;
    }
    private int generateRandomWaterfall(BlockPos origin, double innerRadius, int waterLevel, BoundingBox box, Map<BlockPos, BlockState> blocksToPlace, RandomSource random) {
        //Pick a random angle and height for the spring
        double angle = random.nextDouble() * Math.PI * 2;
        int yOffset = random.nextInt((int)innerRadius);
        int targetY = origin.getY() + yOffset;

        //Don't place a spring too close to the existing lake level
        if (targetY <= waterLevel + 2) return 0;

        //Find wall point
        BlockPos sourcePos = null;
        for (double d = 0; d <= innerRadius; d += 0.5) {
            int x = (int) (Math.cos(angle) * d);
            int z = (int) (Math.sin(angle) * d);
            int localY = targetY - origin.getY();

            //Use the mathematical shape of the meteorite to find the inner surface
            if (getDistortedDistance(origin, x, localY, z) >= innerRadius - 0.8) {
                sourcePos = origin.offset(x, localY, z);
                break;
            }
        }

        if (sourcePos == null || !box.isInside(sourcePos)) return 0;

        // We place a 1x2 or 2x1 cluster of watersources to make the waterfall look powerful
        int sourcesPlaced = 0;
        BlockPos[] cluster = {
                sourcePos,
                sourcePos.below(), // Vertical cluster
                sourcePos.relative(random.nextBoolean() ? Direction.NORTH : Direction.EAST) // Random spread
        };

        for (BlockPos pos : cluster) {
            if (box.isInside(pos)) {
                // Check if we are placing water in air (not inside the wall)
                BlockState state = blocksToPlace.get(pos);
                if (state == null || state.isAir()) {
                    blocksToPlace.put(pos, Blocks.WATER.defaultBlockState());
                    sourcesPlaced++;
                }
            }
        }

        // Find direction towards the wall to place an anchor block
        int dx = sourcePos.getX() - origin.getX();
        int dz = sourcePos.getZ() - origin.getZ();
        Direction wallDir = Math.abs(dx) > Math.abs(dz) ? (dx > 0 ? Direction.EAST : Direction.WEST) : (dz > 0 ? Direction.SOUTH : Direction.NORTH);

        BlockPos anchorPos = sourcePos.relative(wallDir);
        if (box.isInside(anchorPos)) {
            // Place ore or a distinctive block where the water "leaks" from
            blocksToPlace.put(anchorPos, Blocks.BLACKSTONE.defaultBlockState());
        }

        return sourcesPlaced;
    }
    private int[] generateDecoration(WorldGenLevel world, BlockPos origin, double innerRadiusSq, int waterLevel, RandomSource random, BoundingBox box, Map<BlockPos, BlockState> blocksToPlace) {
        int[] counts = new int[2];
        int dirtCount = 0;
        int cavevinesCount = 0;
        double decorationScanRadiusSq = Math.pow(radius - thickness + 2.0, 2);

        for (int x = box.minX(); x <= box.maxX(); x++) {
            for (int z = box.minZ(); z <= box.maxZ(); z++) {
                double dx = x - origin.getX();
                double dz = z - origin.getZ();
                double horizontalDistSq = (dx * dx) + (dz * dz);

                if (horizontalDistSq > decorationScanRadiusSq) continue;

                for (int y = box.minY(); y <= box.maxY(); y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    double dy = y - origin.getY();
                    double distSq = horizontalDistSq + (dy * dy);

                    // Check if we are inside the hollow cavity
                    if (distSq <= decorationScanRadiusSq) {
                        BlockState state = getEffectiveState(world, pos, blocksToPlace);

                        // 1. FLOOR DETECTION: If current is empty and block below is solid
                        if (state.isAir() || state.is(Blocks.WATER)) {
                            BlockPos belowPos = pos.below();
                            BlockState belowState = getEffectiveState(world, belowPos,  blocksToPlace);

                            if (belowState.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES)||belowState.is(BlockTags.STONE_ORE_REPLACEABLES)||belowState.is(Blocks.BLACKSTONE)) {
                                dirtCount += generateFloor(belowPos, waterLevel, box, blocksToPlace);
                                decorateFloor(world, pos, random, box, waterLevel, blocksToPlace);
                            }
                        }

                        // 2. CEILING DETECTION: If current is air and block above is solid
                        if (state.isAir()) {
                            BlockPos abovePos = pos.above();
                            BlockState aboveState = getEffectiveState(world, abovePos, blocksToPlace);

                            if (aboveState.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES)||aboveState.is(BlockTags.STONE_ORE_REPLACEABLES)||aboveState.is(Blocks.BLACKSTONE)) {
                                cavevinesCount = decorateCeiling(world, pos, random, box, blocksToPlace, cavevinesCount);
                            }
                        }
                    }
                }
            }
        }
        counts[0] = dirtCount;
        counts[1] = cavevinesCount;
        return  counts;
    }
    public void setMeteoriteBiome(WorldGenLevel level, BoundingBox box) {
        HolderLookup<Biome> biomeLookup = level.holderLookup(Registries.BIOME);
        Identifier myBiomeLocation = Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "vibranium_meteorite");
        ResourceKey<Biome> myBiomeKey = ResourceKey.create(Registries.BIOME, myBiomeLocation);
        Optional<Holder.Reference<Biome>> biomeHolder = biomeLookup.get(myBiomeKey);

        if (biomeHolder.isPresent()) {
            Holder<Biome> holder = biomeHolder.get();

            //Loop on area by 4 to 4 steps
            for (int x = box.minX(); x <= box.maxX(); x += 4) {
                for (int z = box.minZ(); z <= box.maxZ(); z += 4) {
                    for (int y = box.minY(); y <= box.maxY(); y += 4) {

                        BlockPos pos = new BlockPos(x, y, z);
                        ChunkAccess chunk = level.getChunk(pos);
                        int sectionIdx = chunk.getSectionIndex(y);

                        if (sectionIdx >= 0 && sectionIdx < chunk.getSections().length) {
                            var section = chunk.getSection(sectionIdx);

                            //Access container
                            PalettedContainer<Holder<Biome>> container = (PalettedContainer<Holder<Biome>>) section.getBiomes();

                            // LOCAL INDEX CALCULATIONS (0 to 3)
                            // Get the relative position within the section (0-15) then convert to Quarter (0-3)
                            int localX = (x & 15) >> 2;
                            int localY = (y & 15) >> 2;
                            int localZ = (z & 15) >> 2;

                            container.set(localX, localY, localZ, holder);
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
        //Find the lowest Y level where we have AIR (the actual floor of the cavity)
        int minAirY = blocksToPlace.entrySet().stream()
                .filter(e -> e.getValue().isAir())
                .mapToInt(e -> e.getKey().getY())
                .min()
                .orElse(Integer.MAX_VALUE);

        //Filter for air blocks that are at the bottom (with a small tolerance)
        // We allow minAirY + 1 to account for slightly uneven procedural floors
        List<BlockPos> deepFloorPositions = blocksToPlace.entrySet().stream()
                .filter(entry -> {
                    BlockPos pos = entry.getKey();
                    if (!entry.getValue().isAir()) return false;
                    if (pos.getY() > minAirY + 1) return false; // Only the very bottom

                    BlockState belowState = blocksToPlace.get(pos.below());
                    return belowState != null && belowState.isSolid() && !belowState.is(Blocks.POINTED_DRIPSTONE);
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (deepFloorPositions.isEmpty()) return;

        Collections.shuffle(deepFloorPositions);

        int sourcesPlaced = 0;
        int maxSources = 1 + random.nextInt(2); // 1 or 2 sources is enough for a puddle

        for (BlockPos pos : deepFloorPositions) {
            if (sourcesPlaced >= maxSources) break;

            // Check for adjacent walls (horizontal only)
            boolean adjacentToWall = false;
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockState neighbor = blocksToPlace.get(pos.relative(dir));
                if (neighbor != null && neighbor.isSolid()) {
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
    private void generateStructure(WorldGenLevel world, BlockPos target, double distortedDistance, int radius, BlockPos origin, RandomSource random, BoundingBox box, Map<BlockPos, BlockState> blocksToPlace) {
        double innerLimit = radius - thickness;
        BlockState stateAtTarget = getEffectiveState(world, target, blocksToPlace);
        boolean isActuallyAirOrWater = stateAtTarget.isAir() || stateAtTarget.getFluidState().is(FluidTags.WATER);

        // CLIFF/SURFACE EROSION LOGIC
        // If exposed to air in a surface context, apply heavy procedural erosion
        if (this.isSurfacePiece && isActuallyAirOrWater) {
            int realSurfaceY = world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, target.getX(), target.getZ());

            if (target.getY() > realSurfaceY) {
                // leave 1% shell debris
                if (random.nextFloat() > 0.1f) return;
            }

            //Flying blocks fix
            if (target.getY() > realSurfaceY - 2) {
                BlockState belowState = getEffectiveState(world, target.below(), blocksToPlace);
                boolean isValidSupport = belowState.isSolid() || belowState.is(Blocks.BLACKSTONE) || belowState.is(VibraniumBlocks.VIBRANIUM_ORE.get());
                if (!isValidSupport) return;
            }
        }

        // OUTER SHELL ZONE
        if (distortedDistance > innerLimit) {
            generateShell(world, target, isActuallyAirOrWater, 0.5f, random, box, blocksToPlace);
        }
        // FOUNDATION ZONE (Layer beneath the shell)
        else if (distortedDistance > innerLimit - 1.5) {
            if (!isActuallyAirOrWater || target.getY() < origin.getY()) {
                safeSetBlock(target, Blocks.BLACKSTONE.defaultBlockState(), box, blocksToPlace);
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
    private int generateVibraniumVeins(BlockPos origin, int radius, BoundingBox box, Map<BlockPos, BlockState> blocksToPlace, RandomSource random) {
        List<BlockPos> currentPositions = new ArrayList<>(blocksToPlace.keySet());
        int oreCount = 0;

        for (BlockPos target : currentPositions) {
            BlockState currentState = blocksToPlace.get(target);

            if (currentState.is(Blocks.BLACKSTONE)) {
                double distortedDistance = Math.sqrt(target.distSqr(origin));
                boolean isInnerShell = distortedDistance < (radius - 0.5) && distortedDistance > (radius - 2.5);

                if (isInnerShell) {
                    double oreNoise = Math.sin(target.getX() * 0.5) + Math.sin(target.getY() * 0.5) + Math.sin(target.getZ() * 0.5);

                    if (oreNoise > 2.2 || random.nextFloat() < 0.15f) {
                        blocksToPlace.put(target, VibraniumBlocks.VIBRANIUM_ORE.get().defaultBlockState());
                        oreCount++;
                    }
                }
            }
        }
        return oreCount;
    }

    private int generateFloor(BlockPos target, int waterLevel, BoundingBox box, Map<BlockPos, BlockState> blocksToPlace) {
        int dirtCount = 0;
        double patchNoise = Math.sin(target.getX() * 0.12) + Math.sin(target.getZ() * 0.12);
        double detailNoise = Math.sin(target.getX() * 0.5) * Math.cos(target.getZ() * 0.5);

        // Protect vibranium
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockState neighbor = blocksToPlace.get(target.relative(dir));
            if (neighbor != null && neighbor.is(VibraniumBlocks.VIBRANIUM_ORE.get())) {
                return 0;
            }
        }

        BlockState floorState;
        boolean isBank = Math.abs(target.getY() - waterLevel) <= 1;

        if (target.getY() < waterLevel) {
            //Lake bottom
            if (patchNoise > 1.0) {
                floorState = VibraniumBlocks.BLACKCLAY.get().defaultBlockState();
            } else if (patchNoise > -1.5) {
                if (detailNoise > 0.3) {
                    floorState = VibraniumBlocks.VIBRANIUM_DIRT.get().defaultBlockState();
                } else {
                    floorState = VibraniumBlocks.BLACKGRAVEL.get().defaultBlockState();
                }
            } else {
                // Leave Blackstone once in a while
                return 0;
            }
        }
        //Bank
        else if (isBank) {
            if (patchNoise > 1.3) {
                floorState = VibraniumBlocks.BLACKCLAY.get().defaultBlockState();
            } else if (detailNoise > 0.4) {
                floorState = VibraniumBlocks.BLACKGRAVEL.get().defaultBlockState();
            } else {
                floorState = VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get().defaultBlockState();
                dirtCount++;
            }
        }
        //Surface
        else {
            if (detailNoise > 0.95) return 0;
            floorState = VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get().defaultBlockState();
            dirtCount++;
        }

        safeSetBlock(target, floorState, box, blocksToPlace);
        return dirtCount;
    }
    private void decorateFloor(WorldGenLevel world, BlockPos target, RandomSource random, BoundingBox box, int waterLevel, Map<BlockPos, BlockState> blocksToPlace) {
        BlockState floorState = getEffectiveState(world, target.below(), blocksToPlace);
        BlockState currentState = getEffectiveState(world, target, blocksToPlace);
        boolean isUnderWater = target.getY() <= waterLevel;

        // No decoration on occupied space
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

    private int decorateCeiling(WorldGenLevel world, BlockPos target, RandomSource random, BoundingBox box, Map<BlockPos, BlockState> blocksToPlace, int caveVinesCount) {
        if (random.nextFloat() < 0.15f) {
            placeDripstoneColumn(world, target, random, box, blocksToPlace);
        } else if (random.nextFloat() < 0.40f) {
            caveVinesCount = placeVineGrapnel(world, target, random, box, blocksToPlace, caveVinesCount);
        }
        return caveVinesCount;
    }
    private void placeDripstoneColumn(WorldGenLevel world, BlockPos pos, RandomSource random, BoundingBox box, Map<BlockPos, BlockState> blocksToPlace) {
        // Solid base placed everytime
        safeSetBlock(pos.above(), Blocks.DRIPSTONE_BLOCK.defaultBlockState(), box, blocksToPlace);

        //choose a random length
        int length = random.nextInt(4); // 0, 1, 2 or 3
        if (length == 0) return;// 0 = base only

        for (int i = 0; i < length; i++) {
            BlockPos currentPos = pos.below(i);
            if (!box.isInside(currentPos) || !getEffectiveState(world, currentPos, blocksToPlace).isAir()) break;

            DripstoneThickness thickness;
            if (length == 1) {
                thickness = DripstoneThickness.TIP; //1 = base + tip
            } else {
                // Longer ones
                if (i == 0) thickness = DripstoneThickness.BASE;
                else if (i == length - 1) thickness = DripstoneThickness.TIP;
                else thickness = DripstoneThickness.FRUSTUM;
            }

            BlockState state = Blocks.POINTED_DRIPSTONE.defaultBlockState()
                    .setValue(PointedDripstoneBlock.TIP_DIRECTION, Direction.DOWN)
                    .setValue(PointedDripstoneBlock.THICKNESS, thickness);

            safeSetBlock(currentPos, state, box, blocksToPlace);
        }

        // cluster effect
        if (random.nextFloat() < 0.3f) { // 30% neighbor chance
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                if (random.nextBoolean()) {
                    BlockPos neighborPos = pos.relative(dir);
                    safeSetBlock(neighborPos.above(), Blocks.DRIPSTONE_BLOCK.defaultBlockState(), box, blocksToPlace);
                    safeSetBlock(neighborPos, Blocks.POINTED_DRIPSTONE.defaultBlockState()
                            .setValue(PointedDripstoneBlock.TIP_DIRECTION, Direction.DOWN)
                            .setValue(PointedDripstoneBlock.THICKNESS, DripstoneThickness.TIP), box, blocksToPlace);
                }
            }
        }
    }
    private int placeVineGrapnel(WorldGenLevel world, BlockPos target, RandomSource random, BoundingBox box, Map<BlockPos, BlockState> blocksToPlace, int caveVinesCount) {
        BlockState stateAbove = getEffectiveState(world, target.above(), blocksToPlace);

        // CAVE VINES
        if (stateAbove.is(BlockTags.STONE_ORE_REPLACEABLES)||stateAbove.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES)||
                stateAbove.is(Blocks.BLACKSTONE)||stateAbove.is(Blocks.MOSSY_COBBLESTONE)||stateAbove.is(Blocks.END_STONE)
                ||stateAbove.is(VibraniumBlocks.VIBRANIUM_ORE.get())) {
            int length = random.nextInt(4) + 1; // length

            for (int i = 0; i < length; i++) {
                BlockPos currentPos = target.below(i);

                if (!box.isInside(currentPos)) break;
                BlockState currentState = getEffectiveState(world, currentPos, blocksToPlace);
                if (!currentState.isAir() && !currentState.is(Blocks.WATER)) break;

                boolean isTip = (i == length - 1);
                BlockState vineState;

                if (isTip) {
                    vineState = VibraniumBlocks.PURPLE_CAVE_VINES.get().defaultBlockState()
                            .setValue(CaveVinesBlock.AGE, random.nextInt(25));
                } else {
                    vineState = VibraniumBlocks.PURPLE_CAVE_VINES_PLANT.get().defaultBlockState();
                }

                //Berries
                vineState = vineState.setValue(CaveVinesBlock.BERRIES, random.nextFloat() < 0.2f);

                safeSetBlock(currentPos, vineState, box, blocksToPlace);
                caveVinesCount++;
            }
        }

        // Wall vines
        if (getEffectiveState(world, target, blocksToPlace).isAir()) {
            BlockState wallVine = VibraniumBlocks.PURPLE_VINE.get().defaultBlockState();
            boolean wallFound = false;

            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos neighbor = target.relative(dir);
                if (getEffectiveState(world, neighbor, blocksToPlace).isSolidRender()) {
                    wallVine = wallVine.setValue(VineBlock.getPropertyForFace(dir), true);
                    wallFound = true;
                }
            }

            if (wallFound) {
                safeSetBlock(target, wallVine, box, blocksToPlace);
            }
        }

        return caveVinesCount;
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
    private double getDistortedDistance(BlockPos origin, int x, int y, int z) {
        double distance = Math.sqrt(x * x + y * y + z * z);
        BlockPos target = origin.offset(x, y, z);
        double noiseFactor = getNoise(target.getX(), target.getY(), target.getZ());
        return distance + noiseFactor;
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
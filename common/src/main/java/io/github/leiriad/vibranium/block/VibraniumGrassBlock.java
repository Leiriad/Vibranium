package io.github.leiriad.vibranium.block;

import com.mojang.serialization.MapCodec;
import io.github.leiriad.vibranium.VibraniumMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import io.github.leiriad.vibranium.utils.VibraniumBlockActions;

import static io.github.leiriad.vibranium.block.VibraniumCommonDirtProperties.baseVibraniumDirtSettings;
import static io.github.leiriad.vibranium.init.VibraniumBlocks.VIBRANIUM_DIRT;

public class VibraniumGrassBlock extends GrassBlock implements BonemealableBlock{
    //PROPERTIES
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings){
        return baseVibraniumDirtSettings().mapColor(MapColor.COLOR_PURPLE);
    }
    public static final MapCodec<GrassBlock> CODEC = simpleCodec(VibraniumGrassBlock::new);

    @Override
    public MapCodec<GrassBlock> codec() {
        return CODEC;
    }
    public static final TagKey<Block> VIBRANIUM_VEGETATION = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID,"vibranium_vegetation"));

    //CONSTRUCTOR
    public VibraniumGrassBlock(Properties properties) {
        super(properties);
    }

    //ACTIONS
    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (!canSurviveAsGrass(world, pos)) {
            // If dies replaced by vibranium_dirt
            world.setBlockAndUpdate(pos, VIBRANIUM_DIRT.get()
                    .defaultBlockState());
            return;
        }
        //If there is enough light (9 as for vanilla grass) it propagates and lives
        if (world.getMaxLocalRawBrightness(pos.above()) >= 9) {
            //Vibranium_Grass_block spreads
            spreadsToTarget(world, random, pos);
            //Vibranium_Grass_Block spawns plants
            growsCompatiblePlants(world, random, pos);
            //Vibranium_Grass_Block make plants grow faster with a risk of destruction
            VibraniumBlockActions.fertilizes(world, random, pos);
        }

    }
    private boolean canSurviveAsGrass(ServerLevel world, BlockPos pos) {
        BlockPos abovePos = pos.above();
        BlockState aboveState = world.getBlockState(abovePos);

        // Is under water ?
        if (aboveState.getFluidState().getAmount() > 0) {
            return false;
        }
        //Is under a dark bloc
        if (aboveState.isViewBlocking(world, abovePos)) {
            return false;
        }

        return true;
    }
    private void spreadsToTarget(ServerLevel world, RandomSource random, BlockPos pos) {

        BlockPos targetPos = pos.offset(
                random.nextInt(3) - 1,
                random.nextInt(5) - 3,
                random.nextInt(3) - 1
        );

        BlockState targetState = world.getBlockState(targetPos);
        if (targetState.is(VIBRANIUM_DIRT.get())) {

            //Converts the Vibranium_Dirt blocks around to vibranium_grass
            world.setBlockAndUpdate(targetPos, this.defaultBlockState());

        } else if (targetState.is(Blocks.DIRT)) {

            // Turn the normal grass_block around to vibranium_grass (very rare)
            if (random.nextFloat() < 0.02f) {

                world.setBlockAndUpdate(targetPos, this.defaultBlockState());

            } else {
                // turns the normal dirt block around into normal grass
                world.setBlockAndUpdate(targetPos, Blocks.GRASS_BLOCK.defaultBlockState());
            }
        }

    }

    //Bonemealable methods

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos pos, BlockState state) {
        return super.isValidBonemealTarget(levelReader, pos, state);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        growsCompatiblePlants(level, random, pos);
    }


    private void growsCompatiblePlants(ServerLevel level, RandomSource random, BlockPos pos) {

        BlockPos targetBasePos = pos.above();
        var blockRegistry = level.registryAccess().lookupOrThrow(Registries.BLOCK);
        int growthspeed = random.nextInt(5);

        // Get all vibranium plants
        var tagIterable = blockRegistry.get(VIBRANIUM_VEGETATION);

        if (tagIterable.isPresent()) {
            for (int i = 0; i < 8; ++i) {
                BlockPos targetPos = targetBasePos.offset(
                        random.nextInt(3) - 1,
                        random.nextInt(2) - random.nextInt(2),
                        random.nextInt(3) - 1
                );

                if (level.isEmptyBlock(targetPos) && level.getBlockState(targetPos.below()).is(this)) {

                    //pick one of the plants in the list
                    var nodes = tagIterable.get();
                    if (nodes.size() > 0) {
                        Block randomPlant = nodes.get(random.nextInt(nodes.size())).value();

                        //if randomPlant it has less chances to spawn
                        boolean isFlower = randomPlant.defaultBlockState().is(BlockTags.FLOWERS);
                        if (isFlower && growthspeed!=0){continue;}

                        // Place the plant depending on its size
                        if (randomPlant instanceof DoublePlantBlock) {
                            if (level.isEmptyBlock(targetPos.above())) {
                                DoublePlantBlock.placeAt(level, randomPlant.defaultBlockState(), targetPos, 3);
                            }
                        } else {
                            level.setBlock(targetPos, randomPlant.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }

}
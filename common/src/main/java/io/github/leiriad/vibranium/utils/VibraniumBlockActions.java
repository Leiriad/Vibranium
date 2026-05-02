package io.github.leiriad.vibranium.utils;

import io.github.leiriad.vibranium.VibraniumMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import io.github.leiriad.vibranium.init.VibraniumBlocks;

import static io.github.leiriad.vibranium.init.VibraniumBlocks.BLOCKS;

public class VibraniumBlockActions {
    static int vibraniumPurple = 0xD500F9;
    private static final DustParticleOptions vibraniumDust = new DustParticleOptions(vibraniumPurple, 1.0f);


    public static void turnsToVibraniumGrass(ServerLevel world, BlockPos pos, RandomSource random) {
        // check if neighbor block is grass
        Block vibraniumGrass = VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get();
        BlockState vibraniumGrassState = vibraniumGrass.defaultBlockState();

        for (BlockPos targetPos : BlockPos.betweenClosed(
                pos.offset(-1, -1, -1),
                pos.offset(1, 1, 1))) {

            BlockState neighbor = world.getBlockState(targetPos);

            // if neighbor block is vibranium_grass it turns into vibranium_grass more quickly
            if (neighbor.is(vibraniumGrass)) {

                world.setBlockAndUpdate(pos, vibraniumGrassState);
                return;
            }
            if (neighbor.is(Blocks.GRASS_BLOCK)) {

                // is it is, there's a little chance block turns to grass
                if (random.nextFloat() < 0.25f) {

                    world.setBlockAndUpdate(pos,vibraniumGrassState);
                    return;
                }
            }

        }
    }
    public static void turnToVibraniumDirt(Entity entity, BlockState state, Level world, BlockPos pos) {
        // Definition of the target state (your custom vibranium dirt block)
        Block vibraniumDirt = VibraniumBlocks.VIBRANIUM_DIRT.get();
        BlockState newState = vibraniumDirt.defaultBlockState();

        // Using the static Block method to handle entity collisions and prevent clipping
        // pushEntitiesUp shifts entities upward if the new block shape would trap them
        BlockState finalizedState = Block.pushEntitiesUp(state, newState, world, pos);

        // Updating the block in the world and notify neighboring blocks
        world.setBlockAndUpdate(pos, finalizedState);

        // Triggering a game event for features like Sculk Sensors or Wardens
        world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, finalizedState));
    }
    public static void fertilizes(ServerLevel world, RandomSource random, BlockPos pos) {
        BlockPos above = pos.above();
        BlockState plant = world.getBlockState(above);

        if (plant.getBlock() instanceof BonemealableBlock growable) {
            float chance = random.nextFloat();

            if (chance < 0.01f) {
                // 1% probabilities of plant death
                world.destroyBlock(above, true);
            }
            else if (chance < 0.15f) {
                // 10% probabilities (from 0.05 to 0.15) of light growth
                if (growable.isValidBonemealTarget(world, above, plant)) {
                    growPlant(world, random, growable, above, plant);
                }
            }
            else if (chance < 0.60f) {
                // 45% probabilities (from 0.15 tp 0.60) of high growth
                if (growable.isValidBonemealTarget(world, above, plant)) {
                    growable.performBonemeal(world, random, above, plant);
                }
            }
            // The other 40% of the time, nothing happens
        }
    }
    private static void growPlant(ServerLevel world, RandomSource random, BonemealableBlock growable, BlockPos above, BlockState plant) {
        growable.performBonemeal(world, random, above, plant);
        sendVibraniumParticles(world, above);
    }

    ///Send particles to indicate an action (fertilization for exemple)
    private static void sendVibraniumParticles(ServerLevel world, BlockPos above) {

        //Vibranium purple particles
        world.sendParticles(vibraniumDust, above.getX() + 0.5, above.getY() + 0.5, above.getZ() + 0.5, 10, 0.3, 0.3, 0.3, 0.1);
    }
    ///Shows vibranium particles around inert object
    public static void showVibraniumParticles(Level world, RandomSource random, BlockPos pos){
        //1 chance over 4 to emit vibranium particles in a tick
        if (random.nextInt(4) == 0) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();

            // Ici on utilise level.addParticle car on est déjà côté CLIENT
            world.addParticle(vibraniumDust, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }
    ///Shows reverse portal particles around inert object
    public static void showReversePortalParticles(Level world, RandomSource random, BlockPos pos){
        if (random.nextInt(10) == 0) {
            world.addParticle(ParticleTypes.REVERSE_PORTAL,
                    pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 1.2,
                    pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 1.2,
                    pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 1.2,
                    0, 0, 0);
        }
    }

}
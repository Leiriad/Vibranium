package vibranium.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import vibranium.init.VibraniumBlocks;

public class VibraniumBlockActions {
    static int vibraniumPurple = 0xD500F9;
    private static final DustParticleOptions vibraniumDust = new DustParticleOptions(vibraniumPurple, 1.0f);


    public static void turnsIntoGrass(ServerLevel world, BlockPos pos, RandomSource random) {
        // check if neighbor block is grass
        for (BlockPos targetPos : BlockPos.betweenClosed(
                pos.offset(-1, -1, -1),
                pos.offset(1, 1, 1))) {

            BlockState neighbor = world.getBlockState(targetPos);

            // if neighbor block is vibranium_grass it turns into vibranium_grass more quickly
            if (neighbor.is(VibraniumBlocks.VIBRANIUM_GRASS_BLOCK)) {

                world.setBlockAndUpdate(pos,
                        VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.defaultBlockState());
                return;
            }
            if (neighbor.is(Blocks.GRASS_BLOCK)) {

                // is it is, there's a little chance block turns to grass
                if (random.nextFloat() < 0.25f) {

                    world.setBlockAndUpdate(pos,
                            VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.defaultBlockState());
                    return;
                }
            }

        }
    }
    public static void fertilizes(ServerLevel world, RandomSource random, BlockPos pos) {

            BlockPos above = pos.above();
            BlockState plant = world.getBlockState(above);

            // If plant on block
            if (plant.getBlock() instanceof BonemealableBlock growable) {

            // Random bonus chance behaviour either light and constant (10%)
            if (random.nextFloat() < 0.10f) {
                if (growable.isValidBonemealTarget(world, above, plant)) {
                    growPlant(world, random, growable, above, plant);
                }
            }
            // or big (10%)
            if (random.nextFloat() < 0.45f) {
                if (growable.isValidBonemealTarget(world, above, plant)) {
                    growable.performBonemeal(world, random, above, plant);
                }
            }

            //or grows too quickly and dies
            if (random.nextFloat() < 0.05f) {
                world.destroyBlock(above, true);
            }
        }
    }

    private static void growPlant(ServerLevel world, RandomSource random, BonemealableBlock growable, BlockPos above, BlockState plant) {
        growable.performBonemeal(world, random, above, plant);
        sendVibraniumParticles(world, above);
    }

    //Send particles to indicate an action (fertilization for exemple)
    private static void sendVibraniumParticles(ServerLevel world, BlockPos above) {

        //Vibranium purple particles
        world.sendParticles(vibraniumDust, above.getX() + 0.5, above.getY() + 0.5, above.getZ() + 0.5, 10, 0.3, 0.3, 0.3, 0.1);
    }

    //Shows vibranium particules around inert object
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

    //Shows reverse portal particles around inert object
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
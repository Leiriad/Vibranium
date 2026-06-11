package io.github.leiriad.vibranium.init;

import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.block.HotWaterLiquidBlock;
import io.github.leiriad.vibranium.fluid.HotWaterFluidAttributes;
import io.github.leiriad.vibranium.fluid.MilkFluidAttributes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

public class VibraniumFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(VibraniumMod.MOD_ID, Registries.FLUID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(VibraniumMod.MOD_ID, Registries.BLOCK);

    //Instantiated lazily via a shared attribute reference wrapper
    private static final ArchitecturyFluidAttributes MILK_ATTRIBUTES = new MilkFluidAttributes(
            () -> VibraniumFluids.VANILLA_MILK_STILL.get(),
            () -> VibraniumFluids.VANILLA_MILK_FLOWING.get()
    );

    public static final ArchitecturyFluidAttributes HOT_WATER_ATTRIBUTES = new HotWaterFluidAttributes(
            () -> VibraniumFluids.HOT_WATER_STILL.get(),
            () -> VibraniumFluids.HOT_WATER_FLOWING.get()
    );

    //Main Fluid Registries using our isolated attributes class safely
    public static final RegistrySupplier<FlowingFluid> VANILLA_MILK_STILL = FLUIDS.register("vanilla_milk",
            () -> new ArchitecturyFlowingFluid.Source(VibraniumFluids.MILK_ATTRIBUTES));

    public static final RegistrySupplier<FlowingFluid> VANILLA_MILK_FLOWING = FLUIDS.register("vanilla_milk_flowing",
            () -> new ArchitecturyFlowingFluid.Flowing(VibraniumFluids.MILK_ATTRIBUTES));

    public static final RegistrySupplier<FlowingFluid> HOT_WATER_STILL = FLUIDS.register("hot_water",
            () -> new ArchitecturyFlowingFluid.Source(HOT_WATER_ATTRIBUTES));

    public static final RegistrySupplier<FlowingFluid> HOT_WATER_FLOWING = FLUIDS.register("hot_water_flowing",
            () -> new ArchitecturyFlowingFluid.Flowing(HOT_WATER_ATTRIBUTES));

    // Liquid Block Registry
    public static final RegistrySupplier<LiquidBlock> VANILLA_MILK_BLOCK = BLOCKS.register("vanilla_milk", () -> {
        Identifier id = Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "vanilla_milk");
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);

        BlockBehaviour.Properties props = BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)
                .noCollision()
                .noOcclusion()
                .liquid()
                .strength(100.0F);
        props.setId(blockKey);

        // CRITICAL: Must use VANILLA_MILK_FLOWING because standard LiquidBlock demands a fluid with the 'LEVEL' property!
        return new LiquidBlock(VibraniumFluids.VANILLA_MILK_FLOWING.get(), props);
    });
    public static final RegistrySupplier<LiquidBlock> HOT_WATER_BLOCK = BLOCKS.register("hot_water", () -> {
        Identifier id = Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "hot_water");
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);

        BlockBehaviour.Properties props = BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)
                .noCollision()
                .noOcclusion()
                .liquid()
                .strength(100.0F);
        props.setId(blockKey);

        return new LiquidBlock(VibraniumFluids.HOT_WATER_FLOWING.get(), props);
    });


    public static void register() {
        FLUIDS.register();
        BLOCKS.register();
    }

    public static Item getBucketFromFluid(Fluid fluid) {
        if (fluid == VANILLA_MILK_STILL.get() || fluid == VANILLA_MILK_FLOWING.get()) {
            return Items.MILK_BUCKET;
        }

        return fluid.getBucket();
    }
}

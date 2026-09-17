package io.github.leiriad.vibranium.init;

import dev.architectury.core.block.ArchitecturyLiquidBlock;
import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import dev.architectury.core.fluid.SimpleArchitecturyFluidAttributes;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.leiriad.vibranium.VibraniumMod;
import io.github.leiriad.vibranium.block.HotWaterLiquidBlock;
import io.github.leiriad.vibranium.fluid.HotWaterFluidAttributes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;

import java.util.Optional;

import static io.github.leiriad.vibranium.VibraniumMod.MOD_ID;

public class VibraniumFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(MOD_ID, Registries.FLUID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(MOD_ID, Registries.BLOCK);

    //Main Fluid Registries using our isolated attributes class safely
    public static final RegistrySupplier<ArchitecturyFlowingFluid.Source> VANILLA_MILK_STILL = FLUIDS.register("vanilla_milk",
            () -> new ArchitecturyFlowingFluid.Source(VibraniumFluids.MILK_ATTRIBUTES));

    public static final RegistrySupplier<ArchitecturyFlowingFluid.Flowing> VANILLA_MILK_FLOWING = FLUIDS.register("vanilla_milk_flowing",
            () -> new ArchitecturyFlowingFluid.Flowing(VibraniumFluids.MILK_ATTRIBUTES));

    public static final RegistrySupplier<ArchitecturyFlowingFluid.Source> HOT_WATER_STILL = FLUIDS.register("hot_water",
            () -> new ArchitecturyFlowingFluid.Source(VibraniumFluids.HOT_WATER_ATTRIBUTES));

    public static final RegistrySupplier<ArchitecturyFlowingFluid.Flowing> HOT_WATER_FLOWING = FLUIDS.register("hot_water_flowing",
            () -> new ArchitecturyFlowingFluid.Flowing(VibraniumFluids.HOT_WATER_ATTRIBUTES));


    // Liquid Block Registry
    public static final RegistrySupplier<ArchitecturyLiquidBlock> VANILLA_MILK_BLOCK = BLOCKS.register("vanilla_milk", () -> {
        Identifier id = Identifier.fromNamespaceAndPath(MOD_ID, "vanilla_milk");
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);

        BlockBehaviour.Properties props = BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)
                .noCollision()
                .noOcclusion()
                .liquid()
                .strength(100.0F);
        props.setId(blockKey);

        return new ArchitecturyLiquidBlock(VibraniumFluids.VANILLA_MILK_STILL, props);
    });
    public static final RegistrySupplier<HotWaterLiquidBlock> HOT_WATER_BLOCK = BLOCKS.register("hot_water", () -> {
        Identifier id = Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "hot_water");
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);

        BlockBehaviour.Properties props = BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)
                .noCollision()
                .noOcclusion()
                .liquid()
                .strength(100.0F);
        props.setId(blockKey);

        return new HotWaterLiquidBlock(VibraniumFluids.HOT_WATER_STILL, props);
    });

    //Register Attributes
    public static final ArchitecturyFluidAttributes MILK_ATTRIBUTES =
            SimpleArchitecturyFluidAttributes.ofSupplier(() -> VANILLA_MILK_FLOWING, () -> VANILLA_MILK_STILL)
                    .blockSupplier(() -> VANILLA_MILK_BLOCK)
                    .bucketItem(() -> Optional.of(Items.MILK_BUCKET))
                    .sourceTexture(Identifier.fromNamespaceAndPath("minecraft", "block/water_still"))
                    .flowingTexture(Identifier.fromNamespaceAndPath("minecraft", "block/water_flow"))
                    .viscosity(1500)
                    .convertToSource(false);

    public static final ArchitecturyFluidAttributes HOT_WATER_ATTRIBUTES =
            new HotWaterFluidAttributes(
                    () -> HOT_WATER_FLOWING.get(),
                    () -> HOT_WATER_STILL.get()
            )
                    .blockSupplier(() -> HOT_WATER_BLOCK)
                    .bucketItem(VibraniumItems.HOT_WATER_BUCKET)
                    .sourceTexture(Identifier.fromNamespaceAndPath("minecraft", "block/water_still"))
                    .flowingTexture(Identifier.fromNamespaceAndPath("minecraft", "block/water_flow"))
                    .viscosity(1000)
                    .temperature(573)
                    .convertToSource(false);

    public static void register() {
        FLUIDS.register();
        BLOCKS.register();
    }

}

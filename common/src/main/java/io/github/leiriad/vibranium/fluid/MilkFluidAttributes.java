package io.github.leiriad.vibranium.fluid;

import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import dev.architectury.fluid.FluidStack;
import io.github.leiriad.vibranium.init.VibraniumFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class MilkFluidAttributes implements ArchitecturyFluidAttributes {
    private final Supplier<? extends Fluid> stillFluid;
    private final Supplier<? extends Fluid> flowingFluid;

    public MilkFluidAttributes(Supplier<? extends Fluid> stillFluid, Supplier<? extends Fluid> flowingFluid) {
        this.stillFluid = stillFluid;
        this.flowingFluid = flowingFluid;
    }

    //  Fluid Registry Links
    @Override
    public Fluid getSourceFluid() {
        return stillFluid.get();
    }

    @Override
    public Fluid getFlowingFluid() {
        return flowingFluid.get();
    }

    @Override
    public LiquidBlock getBlock() {
        return VibraniumFluids.VANILLA_MILK_BLOCK.get();
    }

    @Override
    public Item getBucketItem() {
        return Items.MILK_BUCKET;
    }

    // Physics & Mechanics (Standard values matching Water)
    @Override
    public boolean canConvertToSource() {
        return false;
    }

    @Override
    public int getSlopeFindDistance(@Nullable LevelReader level) {
        return 4; // Flows up to 4 blocks to find a hole
    }

    @Override
    public int getDropOff(@Nullable LevelReader level) {
        return 1; // Fluid level drops by 1 per block traveled
    }

    @Override
    public int getTickDelay(@Nullable LevelReader level) {
        return 5; // How fast the fluid spreads (same as water)
    }

    @Override
    public float getExplosionResistance() {
        return 100.0F;
    }

    // Textures & Visuals
    @SuppressWarnings("removal")
    @Override
    public Identifier getSourceTexture(@Nullable FluidStack stack, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos) {
        return Identifier.fromNamespaceAndPath("minecraft", "block/water_still");
    }

    @SuppressWarnings("removal")
    @Override
    public Identifier getFlowingTexture(@Nullable FluidStack stack, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos) {
        return Identifier.fromNamespaceAndPath("minecraft", "block/water_flow");
    }

    @SuppressWarnings("removal")
    @Override
    public int getColor(@Nullable FluidStack stack, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos) {
        // Returns pure solid opaque white (0xFFFFFFFF)
        return 0xFFFFFFFF;
    }

    // Technical Specifications
    @Override
    public int getLuminosity(@Nullable FluidStack stack, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos) {
        return 0; // Milk does not emit light
    }

    @Override
    public int getDensity(@Nullable FluidStack stack, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos) {
        return 1000; // Same density as water
    }

    public int getTemperature(@Nullable FluidStack stack, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos) {
            return 310; // Around 37°C (body temperature for cow's milk)
    }

    @Override
    public int getViscosity(@Nullable FluidStack stack, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos) {
        return 1000; // Flows at normal water speed
    }

    @Override
    public boolean isLighterThanAir(@Nullable FluidStack stack, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos) {
        return false; // It falls down, it's not a gas
    }

    @Override
    public Rarity getRarity(@Nullable FluidStack stack, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos) {
        return Rarity.COMMON;
    }

    // Sounds
    @Override
    public @Nullable SoundEvent getFillSound(@Nullable FluidStack stack, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos) {
        return SoundEvents.BUCKET_FILL;
    }

    @Override
    public @Nullable SoundEvent getEmptySound(@Nullable FluidStack stack, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos) {
        return SoundEvents.BUCKET_EMPTY;
    }

    // Localization
    @Override
    public @Nullable String getTranslationKey(@Nullable FluidStack stack) {
        return "block.vibranium.vanilla_milk"; // Will read from your lang JSON file
    }
}

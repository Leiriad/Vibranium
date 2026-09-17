package io.github.leiriad.vibranium.fluid;

import dev.architectury.core.fluid.SimpleArchitecturyFluidAttributes;
import dev.architectury.fluid.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import java.util.function.Supplier;

public class HotWaterFluidAttributes extends SimpleArchitecturyFluidAttributes {
    public HotWaterFluidAttributes(Supplier<? extends Fluid> flowingFluid, Supplier<? extends Fluid> sourceFluid) {
        super(flowingFluid, sourceFluid);
    }

    @Override
    public int getColor(@Nullable FluidStack stack, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos) {
        if ((Object) level instanceof LevelReader levelReader && pos != null) {
            return levelReader.getBiome(pos).value().getWaterColor();
        }

        return 0x3F76E4;
    }


    // Localization
    @Override
    public @Nullable String getTranslationKey(@Nullable FluidStack stack) {
        return "block.vibranium.hot_water"; // Will read from your lang JSON file
    }
}

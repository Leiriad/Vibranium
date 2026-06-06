package io.github.leiriad.vibranium.fluid;

import io.github.leiriad.vibranium.init.VibraniumFluids;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class FluidHelper {
    /**
     * To extract fluid from bucket item.
     */
    public static Fluid getFluidFromItem(ItemStack stack) {
        // Milk
        if (stack.is(Items.MILK_BUCKET)) {
            return VibraniumFluids.VANILLA_MILK_STILL.get();
        }

        // Others
        if (stack.getItem() instanceof net.minecraft.world.item.BucketItem bucketItem) {
            return bucketItem.getContent();
        }

        return Fluids.EMPTY;
    }

    /**
     * Returns empty bucket from given fluid.
     */
    public static Item getBucketFromFluid(Fluid fluid) {
        if (fluid == VibraniumFluids.VANILLA_MILK_STILL.get() || fluid == VibraniumFluids.VANILLA_MILK_FLOWING.get()) {
            return Items.MILK_BUCKET;
        }
        return fluid.getBucket();
    }
}

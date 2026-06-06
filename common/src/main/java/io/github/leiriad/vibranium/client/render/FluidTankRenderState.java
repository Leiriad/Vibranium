package io.github.leiriad.vibranium.client.render;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class FluidTankRenderState extends BlockEntityRenderState {
    // These fields will hold the clean data snapshot for the render thread
    public Fluid storedFluid = Fluids.EMPTY;
    public long fluidAmount = 0;
    public long capacity = 8000;
}

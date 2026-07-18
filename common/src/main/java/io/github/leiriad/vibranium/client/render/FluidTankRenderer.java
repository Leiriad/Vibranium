package io.github.leiriad.vibranium.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.client.fluid.ClientFluidStackHooks;
import io.github.leiriad.vibranium.client.VibraniumModClient;
import io.github.leiriad.vibranium.entity.FluidTankEntity;
import io.github.leiriad.vibranium.init.VibraniumFluids;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FluidTankRenderer implements BlockEntityRenderer<FluidTankEntity, FluidTankRenderState> {

    public FluidTankRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public FluidTankRenderState createRenderState() {
        return new FluidTankRenderState();
    }

    @Override
    public void extractRenderState(FluidTankEntity tank, FluidTankRenderState state, float partialTicks, Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(tank, state, partialTicks, cameraPos, crumblingOverlay);

        state.storedFluid = tank.getStoredFluid();
        state.fluidAmount = tank.getFluidAmount();
        state.capacity = tank.getCapacity();
    }

    @Override
    public void submit(FluidTankRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        Fluid fluid = state.storedFluid;
        long amount = state.fluidAmount;
        long capacity = state.capacity;

        // Do not render anything if the tank has no fluid
        if (fluid == null || fluid == Fluids.EMPTY || amount <= 0 || capacity <= 0) {
            return;
        }

        // CRITICAL REI SECURITY: Fetch Minecraft's block texture atlas immediately.
        // If REI triggers this method during early initialization or asynchronous recipe caching
        // before the graphic engine is fully stitched, the atlas will be null.
        // Aborting here prevents cascading NullPointerExceptions down the pipeline.
        TextureAtlas blocksAtlas = Minecraft.getInstance()
                .getTextureManager()
                .getTexture(TextureAtlas.LOCATION_BLOCKS) instanceof TextureAtlas atlas ? atlas : null;

        if (blocksAtlas == null) {
            return;
        }

        // Define model boundaries with a tiny inset to prevent Z-Fighting / Clipping
        // 1 pixel = 0.0625f. We use 0.0635f to pull the fluid slightly away from the walls.
        float minX = 0.0635f;
        float maxX = 0.9365f; // 1.0f - 0.0635f
        float minZ = 0.0635f;
        float maxZ = 0.9365f; // 1.0f - 0.0635f

        // Push the bottom slightly up from the tank's base floor
        float minY = 0.0635f;
        float maxY = 1.0000f; // Completely open top face

        // Calculate dynamic fluid surface height based on capacity ratio
        float fluidRatio = (float) amount / (float) capacity;
        float currentTopY = minY + (fluidRatio * (maxY - minY));

        // Prepare fluid stack payload for hooks lookup
        FluidStack fluidStack = FluidStack.create(fluid, amount);
        TextureAtlasSprite sprite = null;

        // --- Texture (Sprite) Resolution Section ---
        if (fluid == Fluids.LAVA) {
            sprite = blocksAtlas.getSprite(Identifier.fromNamespaceAndPath("minecraft", "block/lava_still"));
        }
        // TARGETED BYPASS: Your custom milk and hot water do not have physical textures assigned.
        // We explicitly force them to overlay onto the vanilla water still sprite.
        // This bypasses early Architectury registration lookups which cause crashes with REI.
        else if (fluid == VibraniumFluids.VANILLA_MILK_STILL.get() || fluid == VibraniumFluids.HOT_WATER_STILL.get()) {
            sprite = blocksAtlas.getSprite(Identifier.fromNamespaceAndPath("minecraft", "block/water_still"));
        }
        // COMPATIBILITY FALLBACK: Safely delegate external mod fluids to Architectury API.
        // Wrapped in a wide Throwable catch to trap early load exceptions triggered by REI menus.
        else {
            try {
                sprite = ClientFluidStackHooks.getStillTexture(fluidStack);
            } catch (Throwable t) {
                sprite = null;
            }
        }

        // GLOBAL FALLBACK: If the sprite resolution completely failed or returned an empty texture,
        // recover using a guaranteed vanilla layout baseline before aborting.
        if (sprite == null || sprite.getU0() == sprite.getU1()) {
            Identifier fallbackId = (fluid == Fluids.LAVA)
                    ? Identifier.fromNamespaceAndPath("minecraft", "block/lava_still")
                    : Identifier.fromNamespaceAndPath("minecraft", "block/water_still");

            sprite = blocksAtlas.getSprite(fallbackId);
        }

        // Final safety guard - abort rendering for this frame if no valid sprite can be computed
        if (sprite == null) {
            return;
        }

        // --- Color Resolution Section ---
        int colorValue;
        if (VibraniumModClient.hasColorOverride(fluid)) {
            colorValue = VibraniumModClient.getFluidColor(fluid);
        } else {
            try {
                // Fetch native tinting (e.g., standard water color variations or modded fluid tints)
                colorValue = ClientFluidStackHooks.getColor(fluidStack);
            } catch (Throwable t) {
                colorValue = 0xFFFFFFFF; // Fallback to flat opaque white if the hook fails during early loading
            }
        }

        final int color = colorValue;

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();
        int light = 15728880; // Full brightness default fallback

        // Create the rendering logic wrapped inside the CustomGeometryRenderer functional interface
        SubmitNodeCollector.CustomGeometryRenderer fluidRenderer = (pose, builder) -> {

            // TOP FACE (Normal: Up -> 0, 1, 0)
            builder.addVertex(pose, minX, currentTopY, minZ).setColor(color).setUv(u0, v0).setLight(light).setNormal(pose, 0.0F, 1.0F, 0.0F);
            builder.addVertex(pose, minX, currentTopY, maxZ).setColor(color).setUv(u0, v1).setLight(light).setNormal(pose, 0.0F, 1.0F, 0.0F);
            builder.addVertex(pose, maxX, currentTopY, maxZ).setColor(color).setUv(u1, v1).setLight(light).setNormal(pose, 0.0F, 1.0F, 0.0F);
            builder.addVertex(pose, maxX, currentTopY, minZ).setColor(color).setUv(u1, v0).setLight(light).setNormal(pose, 0.0F, 1.0F, 0.0F);

            // NORTH FACE (Normal: North -> 0, 0, -1)
            builder.addVertex(pose, minX, minY,       minZ).setColor(color).setUv(u0, v1).setLight(light).setNormal(pose, 0.0F, 0.0F, -1.0F);
            builder.addVertex(pose, minX, currentTopY, minZ).setColor(color).setUv(u0, v0).setLight(light).setNormal(pose, 0.0F, 0.0F, -1.0F);
            builder.addVertex(pose, maxX, currentTopY, minZ).setColor(color).setUv(u1, v0).setLight(light).setNormal(pose, 0.0F, 0.0F, -1.0F);
            builder.addVertex(pose, maxX, minY,       minZ).setColor(color).setUv(u1, v1).setLight(light).setNormal(pose, 0.0F, 0.0F, -1.0F);

            // SOUTH FACE (Normal: South -> 0, 0, 1)
            builder.addVertex(pose, maxX, minY,       maxZ).setColor(color).setUv(u0, v1).setLight(light).setNormal(pose, 0.0F, 0.0F, 1.0F);
            builder.addVertex(pose, maxX, currentTopY, maxZ).setColor(color).setUv(u0, v0).setLight(light).setNormal(pose, 0.0F, 0.0F, 1.0F);
            builder.addVertex(pose, minX, currentTopY, maxZ).setColor(color).setUv(u1, v0).setLight(light).setNormal(pose, 0.0F, 0.0F, 1.0F);
            builder.addVertex(pose, minX, minY,       maxZ).setColor(color).setUv(u1, v1).setLight(light).setNormal(pose, 0.0F, 0.0F, 1.0F);

            // WEST FACE (Normal: West -> -1, 0, 0)
            builder.addVertex(pose, minX, minY,       maxZ).setColor(color).setUv(u0, v1).setLight(light).setNormal(pose, -1.0F, 0.0F, 0.0F);
            builder.addVertex(pose, minX, currentTopY, maxZ).setColor(color).setUv(u0, v0).setLight(light).setNormal(pose, -1.0F, 0.0F, 0.0F);
            builder.addVertex(pose, minX, currentTopY, minZ).setColor(color).setUv(u1, v0).setLight(light).setNormal(pose, -1.0F, 0.0F, 0.0F);
            builder.addVertex(pose, minX, minY,       minZ).setColor(color).setUv(u1, v1).setLight(light).setNormal(pose, -1.0F, 0.0F, 0.0F);

            // EAST FACE (Normal: East -> 1, 0, 0)
            builder.addVertex(pose, maxX, minY,       minZ).setColor(color).setUv(u0, v1).setLight(light).setNormal(pose, 1.0F, 0.0F, 0.0F);
            builder.addVertex(pose, maxX, currentTopY, minZ).setColor(color).setUv(u0, v0).setLight(light).setNormal(pose, 1.0F, 0.0F, 0.0F);
            builder.addVertex(pose, maxX, currentTopY, maxZ).setColor(color).setUv(u1, v0).setLight(light).setNormal(pose, 1.0F, 0.0F, 0.0F);
            builder.addVertex(pose, maxX, minY,       maxZ).setColor(color).setUv(u1, v1).setLight(light).setNormal(pose, 1.0F, 0.0F, 0.0F);

            // BOTTOM FACE (Normal: Down -> 0, -1, 0)
            builder.addVertex(pose, minX, minY, minZ).setColor(color).setUv(u0, v0).setLight(light).setNormal(pose, 0.0F, -1.0F, 0.0F);
            builder.addVertex(pose, maxX, minY, minZ).setColor(color).setUv(u1, v0).setLight(light).setNormal(pose, 0.0F, -1.0F, 0.0F);
            builder.addVertex(pose, maxX, minY, maxZ).setColor(color).setUv(u1, v1).setLight(light).setNormal(pose, 0.0F, -1.0F, 0.0F);
            builder.addVertex(pose, minX, minY, maxZ).setColor(color).setUv(u0, v1).setLight(light).setNormal(pose, 0.0F, -1.0F, 0.0F);
        };

        // --- Material Layer Setup ---
        RenderType fluidLayer;
        if (fluid == Fluids.LAVA) {
            fluidLayer = RenderTypes.translucentMovingBlock();
        } else if (fluid == VibraniumFluids.VANILLA_MILK_STILL.get()) {
            fluidLayer = RenderTypes.cutoutMovingBlock(); // Keeps milk sharp and non-transparent
        } else {
            fluidLayer = RenderTypes.translucentMovingBlock();
        }

        // Submit our custom geometry renderer execution proxy to the collector pipeline
        submitNodeCollector.submitCustomGeometry(
                poseStack,
                fluidLayer,
                new net.minecraft.client.renderer.SubmitNodeCollector.CustomGeometryRenderer() {
                    @Override
                    public void render(PoseStack.Pose pose, VertexConsumer vertexConsumer) {
                        fluidRenderer.render(pose, vertexConsumer);
                    }
                }
        );
    }
}

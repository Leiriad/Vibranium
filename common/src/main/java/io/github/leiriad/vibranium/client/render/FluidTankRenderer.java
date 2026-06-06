package io.github.leiriad.vibranium.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.client.fluid.ClientFluidStackHooks;
import dev.architectury.hooks.fluid.FluidStackHooks;
import io.github.leiriad.vibranium.client.VibraniumModClient;
import io.github.leiriad.vibranium.entity.FluidTankEntity;
import io.github.leiriad.vibranium.init.VibraniumFluids;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.InventoryMenu;
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
        if (fluid == Fluids.EMPTY || amount <= 0 || capacity <= 0) {
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

        // Fetch fluid textures and colors using the proper Architectury Hook from your files
        FluidStack fluidStack = FluidStack.create(fluid, amount);

        TextureAtlasSprite sprite = null;

        if (fluid != VibraniumFluids.VANILLA_MILK_STILL.get() && fluid != VibraniumFluids.VANILLA_MILK_FLOWING.get()) {
            sprite = ClientFluidStackHooks.getStillTexture(fluidStack);
        }

        if (sprite == null || sprite.atlasLocation() == null) {
            // Fallback to the missing texture sprite if the atlas hasn't finished loading yet
            TextureAtlas blocksAtlas = Minecraft.getInstance()
                    .getTextureManager()
                    .getTexture(TextureAtlas.LOCATION_BLOCKS) instanceof TextureAtlas atlas ? atlas : null;

            // Ensure blocksAtlas is not null before using it
            if (blocksAtlas != null) {
                sprite = blocksAtlas.getSprite(Identifier.fromNamespaceAndPath("minecraft", "block/water_still"));
            }
        }
        if (sprite == null) {
            return;
        }
        if (sprite.atlasLocation() == null) {
            return;
        }

        int color;
        if (VibraniumModClient.hasColorOverride(fluid)) {
            color = VibraniumModClient.getFluidColor(fluid);
        } else {
            // Fallback to Architectury's native lookup for standard fluids like Water and Lava
            color = ClientFluidStackHooks.getColor(fluidStack);
        }

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();
        int light = 15728880; // Full brightness default fallback

        // Create the rendering logic wrapped inside the new CustomGeometryRenderer functional interface
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

        // Submit our custom geometry renderer to the collector pipeline
        // Architectury / Mojang maps the execution queue here using the active PoseStack state
        RenderType fluidLayer;
        if (fluid == Fluids.LAVA) {
            fluidLayer = RenderTypes.translucentMovingBlock(); // Ou un type spécifique au feu/lave si besoin
        } else if (fluid == VibraniumFluids.VANILLA_MILK_STILL.get()) {
            fluidLayer = RenderTypes.cutoutMovingBlock();
        } else {
            fluidLayer = RenderTypes.translucentMovingBlock();
        }

        // Look for the pushing method inside your mapped version, usually names like 'add', 'submit' or 'push'
        // taking (RenderType, PoseStack.Pose, CustomGeometryRenderer)
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

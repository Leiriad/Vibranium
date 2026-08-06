package io.github.leiriad.vibranium.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OreHighlightRenderer {
    // Stores scanned positions along with their expiration timestamp (in ms)
    private static final Map<BlockPos, Long> HIGHLIGHTED_ORES = new HashMap<>();

    public static void addOres(List<BlockPos> positions, int durationTicks) {
        long expireAt = System.currentTimeMillis() + (durationTicks * 50L);
        for (BlockPos pos : positions) {
            HIGHLIGHTED_ORES.put(pos.immutable(), expireAt);
        }
    }

    /**
     * To be called during the world rendering event (e.g., WorldRenderEvents.LAST or RenderLevelStageEvent)
     */
    public static void render(PoseStack poseStack, VertexConsumer buffer, Vec3 cameraPos) {
        if (HIGHLIGHTED_ORES.isEmpty()) return;

        long now = System.currentTimeMillis();
        // Clean up expired ores
        HIGHLIGHTED_ORES.entrySet().removeIf(entry -> entry.getValue() < now);

        poseStack.pushPose();
        // Align the rendering coordinate system with the camera position
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        for (BlockPos pos : HIGHLIGHTED_ORES.keySet()) {
            AABB box = new AABB(pos);
            // Draw a 3D bounding box (R, G, B, Alpha)
            renderLineBox(
                    poseStack,
                    buffer,
                    box,
                    0.6F, 0.2F, 1.0F, 0.9F // Bright purple
            );
        }

        poseStack.popPose();
    }

    public static void renderLineBox(PoseStack poseStack, VertexConsumer buffer, AABB box, float red, float green, float blue, float alpha) {
        PoseStack.Pose entry = poseStack.last();

        float minX = (float) box.minX;
        float minY = (float) box.minY;
        float minZ = (float) box.minZ;
        float maxX = (float) box.maxX;
        float maxY = (float) box.maxY;
        float maxZ = (float) box.maxZ;

        // Bottom (4 base lines)
        line(entry, buffer, minX, minY, minZ, maxX, minY, minZ, red, green, blue, alpha);
        line(entry, buffer, maxX, minY, minZ, maxX, minY, maxZ, red, green, blue, alpha);
        line(entry, buffer, maxX, minY, maxZ, minX, minY, maxZ, red, green, blue, alpha);
        line(entry, buffer, minX, minY, maxZ, minX, minY, minZ, red, green, blue, alpha);

        // Top (4 ceiling lines)
        line(entry, buffer, minX, maxY, minZ, maxX, maxY, minZ, red, green, blue, alpha);
        line(entry, buffer, maxX, maxY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
        line(entry, buffer, maxX, maxY, maxZ, minX, maxY, maxZ, red, green, blue, alpha);
        line(entry, buffer, minX, maxY, maxZ, minX, maxY, minZ, red, green, blue, alpha);

        // Connecting vertical lines (4 posts)
        line(entry, buffer, minX, minY, minZ, minX, maxY, minZ, red, green, blue, alpha);
        line(entry, buffer, maxX, minY, minZ, maxX, maxY, minZ, red, green, blue, alpha);
        line(entry, buffer, maxX, minY, maxZ, maxX, maxY, maxZ, red, green, blue, alpha);
        line(entry, buffer, minX, minY, maxZ, minX, maxY, maxZ, red, green, blue, alpha);
    }

    private static void line(PoseStack.Pose pose, VertexConsumer buffer, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b, float a) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float dz = z2 - z1;
        float length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);

        float nx = length > 0 ? dx / length : 0.0F;
        float ny = length > 0 ? dy / length : 1.0F;
        float nz = length > 0 ? dz / length : 0.0F;

        buffer.addVertex(pose, x1, y1, z1)
                .setColor(r, g, b, a)
                .setNormal(pose, nx, ny, nz)
                .setLineWidth(2.0F);

        buffer.addVertex(pose, x2, y2, z2)
                .setColor(r, g, b, a)
                .setNormal(pose, nx, ny, nz)
                .setLineWidth(2.0F);
    }
}
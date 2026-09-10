package io.github.leiriad.vibranium.mixin.client;

import io.github.leiriad.vibranium.item.BlackPantherArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin {

    @Unique
    private static final Identifier VIBRANIUM$BLACK_PANTHER_ARM_TEXTURE =
            Identifier.fromNamespaceAndPath("vibranium", "textures/entity/equipment/humanoid_arm/black_panther.png");

    @ModifyVariable(
            method = "renderRightHand",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private Identifier overrideRightHandTexture(Identifier identifier) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
            if (chestStack.getItem() instanceof BlackPantherArmorItem) {
                return VIBRANIUM$BLACK_PANTHER_ARM_TEXTURE;
            }
        }
        return identifier;
    }

    @ModifyVariable(
            method = "renderLeftHand",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private Identifier overrideLeftHandTexture(Identifier identifier) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
            if (chestStack.getItem() instanceof BlackPantherArmorItem) {
                return VIBRANIUM$BLACK_PANTHER_ARM_TEXTURE;
            }
        }
        return identifier;
    }
}
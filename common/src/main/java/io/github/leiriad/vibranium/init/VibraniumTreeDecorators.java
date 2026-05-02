package io.github.leiriad.vibranium.init;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.leiriad.vibranium.VibraniumMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import io.github.leiriad.vibranium.utils.VibraniumGradientDecorator;

public class VibraniumTreeDecorators {
    ///Gradient tree making tool initializer

    private static final RegistrarManager REGISTRIES = RegistrarManager.get(VibraniumMod.MOD_ID);
    private static final Registrar<TreeDecoratorType<?>> DECORATORS = REGISTRIES.get(Registries.TREE_DECORATOR_TYPE);
    public static final MapCodec<VibraniumGradientDecorator> CODEC = MapCodec.unit(VibraniumGradientDecorator::new);
    public static RegistrySupplier<TreeDecoratorType<VibraniumGradientDecorator>> GRADIENT_DECORATOR;

    @SuppressWarnings("unchecked")
    public static void register() {
        Identifier id = Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "gradient_decorator");

        GRADIENT_DECORATOR = (RegistrySupplier<TreeDecoratorType<VibraniumGradientDecorator>>) (Object) DECORATORS.register(id,
                () -> new TreeDecoratorType<>(CODEC)
        );

        VibraniumMod.LOGGER.info("Vibranium Tree has been registered !");
    }
}

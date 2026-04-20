package io.github.leiriad.vibranium.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.leiriad.vibranium.utils.VibraniumGradientDecorator;

import static io.github.leiriad.vibranium.VibraniumMod.MOD_ID;

public class VibraniumTreeDecorators {
    ///Gradient tree making tool initializer
    public static final TreeDecoratorType<VibraniumGradientDecorator> GRADIENT_DECORATOR =
            Registry.register(BuiltInRegistries.TREE_DECORATOR_TYPE,
                    Identifier.fromNamespaceAndPath("com/vibranium", "gradient_decorator"),
                    new TreeDecoratorType<>(VibraniumGradientDecorator.CODEC));

    public static void register() {
        Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
        LOGGER.info("Vibranium Tree Decorator est chargé!");
    }
}

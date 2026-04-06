package vibranium.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vibranium.utils.VibraniumGradientDecorator;

import static vibranium.Vibranium.MOD_ID;

public class VibraniumTreeDecorators {
    public static final TreeDecoratorType<VibraniumGradientDecorator> GRADIENT_DECORATOR =
            Registry.register(BuiltInRegistries.TREE_DECORATOR_TYPE,
                    Identifier.fromNamespaceAndPath("vibranium", "gradient_decorator"),
                    new TreeDecoratorType<>(VibraniumGradientDecorator.CODEC));

    public static void register() {
        Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
        LOGGER.info("Vibranium Tree Decorator est chargé!");
    }
}

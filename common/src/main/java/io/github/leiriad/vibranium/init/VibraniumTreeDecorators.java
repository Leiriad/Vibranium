package io.github.leiriad.vibranium.init;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import io.github.leiriad.vibranium.VibraniumMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.leiriad.vibranium.utils.VibraniumGradientDecorator;

import static io.github.leiriad.vibranium.VibraniumMod.MOD_ID;

public class VibraniumTreeDecorators {
    ///Gradient tree making tool initializer

    private static final RegistrarManager REGISTRIES = RegistrarManager.get(VibraniumMod.MOD_ID);
    private static final Registrar<TreeDecoratorType<?>> DECORATORS = REGISTRIES.get(Registries.TREE_DECORATOR_TYPE);

    public static TreeDecoratorType<VibraniumGradientDecorator> GRADIENT_DECORATOR;



    public static void register() {
        Identifier id = Identifier.fromNamespaceAndPath(VibraniumMod.MOD_ID, "gradient_decorator");

        // On enregistre et on récupère l'instance créée par le jeu
        GRADIENT_DECORATOR = (TreeDecoratorType<VibraniumGradientDecorator>) DECORATORS.register(id,
                () -> new TreeDecoratorType<>(VibraniumGradientDecorator.CODEC) {}
        ).get();

        Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
        LOGGER.info("Vibranium Tree Decorator est chargé!");
    }
}

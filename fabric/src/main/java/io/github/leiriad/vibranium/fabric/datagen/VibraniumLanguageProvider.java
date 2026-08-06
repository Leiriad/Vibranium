package io.github.leiriad.vibranium.fabric.datagen;

import io.github.leiriad.vibranium.init.VibraniumCreativeTabs;
import io.github.leiriad.vibranium.init.VibraniumFluids;
import io.github.leiriad.vibranium.init.VibraniumItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import io.github.leiriad.vibranium.init.VibraniumBlocks;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class VibraniumLanguageProvider extends FabricLanguageProvider {
    ///JSON language file automation
    private String languageCode = "";

    ///Default en constructor
    public VibraniumLanguageProvider (FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup){
        this(dataOutput, registryLookup, "en_us");
    }

    ///Constructor for other languages
    public VibraniumLanguageProvider (FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup, String languageCode){
        super(dataOutput, languageCode, registryLookup);
        this.languageCode=languageCode;
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
        switch (languageCode){
            case "fr_fr":
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_ORE.get(), "Minerai de vibranium");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_DIRT.get(), "Terre vibranisée");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get(), "Herbe vibranisée");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_PATH.get(), "Chemin de terre vibranisée");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_FARMLAND.get(), "Terre vibranisée labourée");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BLACK_GRAVEL.get(), "Gravier noir");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BLACK_CLAY.get(), "Argile noire");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_SHORT_GRASS.get(), "Herbe courte pourpre");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_TALL_GRASS.get(), "Herbe haute pourpre");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_AZALEA.get(), "Azalée pourpre");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_AZALEA_LEAVES_VIOLET.get(), "Feuilles d'azalée pourpre");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_AZALEA_LEAVES_DARK_BLUE.get(), "Feuilles d'azalée pourpre indigo");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_AZALEA_LEAVES_CYAN.get(), "Feuilles d'azalée pourpre cyan");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLOWERING_PURPLE_AZALEA.get(), "Azalée pourpre fleuri");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_VIOLET.get(), "Feuilles d'azalée pourpre fleuri");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE.get(), "Feuilles d'azalée pourpre fleuri indigo");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_CYAN.get(), "Feuilles d'azalée pourpre fleuri cyan");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_MOSS_BLOCK.get(), "Block de mousse pourpre");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_MOSS_CARPET.get(), "Tapis de mousse pourpre");
                addBlockWithItem(translationBuilder,VibraniumBlocks.POTTED_PURPLE_AZALEA_BUSH.get(), "Azalée pourpre en pot");
                addBlockWithItem(translationBuilder,VibraniumBlocks.POTTED_FLOWERING_PURPLE_AZALEA_BUSH.get(), "Azalée pourpre fleuri en pot");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BIG_PURPLE_DRIPLEAF.get(), "Grande foliogoutte pourpre");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BIG_PURPLE_DRIPLEAF_STEM.get(), "Tige de grande foliogoutte pourpre");
                addBlockWithItem(translationBuilder,VibraniumBlocks.SMALL_PURPLE_DRIPLEAF.get(), "Petite foliogoutte pourpre");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_VINE.get(), "Liane pourpre");
                addBlockWithItem(translationBuilder,VibraniumBlocks.REACTOR_CORE.get(), "Cœur de réacteur");
                addBlockWithItem(translationBuilder,VibraniumBlocks.REACTOR_CONTROL_PANEL.get(), "Panneau de Contrôle de réacteur");
                addBlockWithItem(translationBuilder,VibraniumBlocks.REACTOR_HATCH.get(), "Trappe d'alimentation de réacteur");
                addBlockWithItem(translationBuilder,VibraniumBlocks.REACTOR_PUMP.get(), "Pompe de refroidissement du reacteur");
                addBlockWithItem(translationBuilder,VibraniumBlocks.REACTOR_OUTLET.get(), "Buse d'évacuation du reacteur");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_GLASS.get(), "Verre de vibranium");
                addBlockWithItem(translationBuilder,VibraniumBlocks.REINFORCED_VIBRANIUM_GLASS.get(), "Verre de vibranium renforcé");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_GLASS_PANE.get(), "Vitre de vibranium");
                addBlockWithItem(translationBuilder,VibraniumBlocks.REINFORCED_VIBRANIUM_GLASS_PANE.get(), "Vitre de vibranium renforcée");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLUID_TANK.get(), "Réservoir à fluides");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BLACK_BRICKS.get(), "Briques noires");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BLACK_BRICK_STAIRS.get(), "Escaliers en briques noires");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BLACK_BRICK_SLAB.get(), "Dalle en briques noires");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BLACK_BRICK_WALL.get(), "Muret en briques noires");

                translationBuilder.add(VibraniumBlocks.HEART_SHAPED_HERB_FLOWER.get(), "Fleur d'herbe cœur");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES.get(), "Liane des cavernes pourpre");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES_PLANT.get(), "Liane des cavernes pourpre");

                translationBuilder.add(VibraniumBlocks.HEART_SHAPED_HERB_FLOWER.get().asItem(), "Pousse d'herbe cœur");
                translationBuilder.add(VibraniumBlocks.BLUE_GLOW_BERRIES.get(), "Baies lumineuses bleues");
                translationBuilder.add(VibraniumItems.VIBRANIUM_DUST.get(), "Poudre de vibranium");
                translationBuilder.add(VibraniumItems.BLACK_CLAY_BALL.get(), "Boule d'argile noire");
                translationBuilder.add(VibraniumItems.BLACK_BRICK.get(), "Brique noire");
                translationBuilder.add(VibraniumItems.HEART_SHAPED_HERB.get(), "Herbe coeur");
                translationBuilder.add(VibraniumItems.VIBRANIUM_INGOT.get(), "Lingot de vibranium");
                translationBuilder.add(VibraniumItems.DEPLETED_VIBRANIUM_INGOT.get(), "Lingot de vibranium appauvrit");

                translationBuilder.add(VibraniumItems.BLUE_SOUP.get(), "Soupe bleue");
                translationBuilder.add(VibraniumItems.ANCESTRAL_NECTAR.get(), "Nectar ancestral");
                translationBuilder.add(VibraniumItems.BLUE_BERRY_SPIRIT.get(), "Eau de vie de baies bleues");
                translationBuilder.add("item.vibranium.blue_berry_spirit.effect.water", "Eau de vie de baies bleues");

                translationBuilder.add(VibraniumItems.VIBRANIUM_SPEAR.get(), "Lance en vibranium");
                translationBuilder.add(VibraniumItems.VIBRANIUM_SWORD.get(), "Épée en vibranium");
                translationBuilder.add(VibraniumItems.VIBRANIUM_SHOVEL.get(), "Pelle en vibranium");
                translationBuilder.add(VibraniumItems.VIBRANIUM_PICKAXE.get(), "Pioche en vibranium");

                translationBuilder.add(VibraniumItems.HOT_WATER_BUCKET.get(), "Seau d'eau chaude");
                translationBuilder.add(VibraniumFluids.HOT_WATER_BLOCK.get(), "Eau chaude");

                translationBuilder.add(VibraniumCreativeTabs.VIBRANIUM_TAB.get().getDisplayName().getString(), "Vibranium");

                translationBuilder.add("container.vibranium.reactor_control_panel", "Panneau de contrôle du réacteur");
                translationBuilder.add("container.vibranium.reactor_hatch", "Trappe d'alimentation du réacteur");

                translationBuilder.add("gui.vibranium.energy_tooltip", "Énergie : %d / 100000 FE");
                translationBuilder.add("gui.vibranium.heat_tooltip", "Température : %d°C / 3000°C");
                translationBuilder.add("gui.vibranium.water_tooltip", "Eau : %d / %d mB");
                translationBuilder.add("gui.vibranium.hot_water_tooltip", "Eau Chaude : %d / %d mB");
                translationBuilder.add("gui.vibranium.fuel_tooltip", "Vibranium : %ds restants");
                translationBuilder.add("tooltip.vibranium.slot_fuel", "Insérez la poudre de Vibranium ici");
                translationBuilder.add("tooltip.vibranium.slot_output", "Résidus et scories de combustion");

                translationBuilder.add("tooltip.vibranium.distilled_alcohol", "Distillé dans un alambic. Haute concentration !");

                translationBuilder.add("tooltip.vibranium.weapons.charge", "Charge cinétique");
                translationBuilder.add("tooltip.vibranium.tool.mode.active", "Propulsion Cinétique : ACTIF");
                translationBuilder.add("tooltip.vibranium.tool.mode.inactive", "Propulsion Cinétique : INACTIF");
                translationBuilder.add("tooltip.vibranium.tool.active.toggle", "Shift + Clic Droit : Alterner Mode 3x3");
                translationBuilder.add("tooltip.vibranium.pickaxe.active.echolocation", "Clic Droit : Impulsion d'Écholocalisation");
                translationBuilder.add("tooltip.vibranium.pickaxe.passive.filter", "Passif : Préserve les Minerais et Structures");
                translationBuilder.add("tooltip.vibranium.shovel.passive.gravity", "Passif : Fait S'effondrer les Blocs Soumis à la Gravité");
                translationBuilder.add("tooltip.vibranium.shovel.passive.path", "Clic Droit : Créateur de Chemins 3x3");

                translationBuilder.add("message.vibranium.hatch_no_core", "Cette trappe n'est reliée à aucun réacteur !");
                translationBuilder.add("subtitles.vibranium.meltdown_alarm", "L'alarme de surchauffe du réacteur sonne !");

                translationBuilder.add("text.vibranium.config.title", "Configuration de Vibranium");
                translationBuilder.add("text.vibranium.config.category.overworld", "Génération de Météorites : Overworld");
                translationBuilder.add("text.vibranium.config.category.end", "Génération de Météorites : End");
                translationBuilder.add("text.vibranium.config.option.spacing", "Espacement (Spacing)");
                translationBuilder.add("text.vibranium.config.option.separation", "Séparation (Separation)");
                translationBuilder.add("text.vibranium.config.section.weights", "§6Probabilité d'apparition des structures (Weights)");
                translationBuilder.add("text.vibranium.config.weight_for", "Poids (Weight) pour %s");
                break;
            case "es_es":
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_ORE.get(), "Mineral de vibranio");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_DIRT.get(), "Tierra vibranizada");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get(), "Hierba vibranizada");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_PATH.get(), "Camino de tierra vibranizada");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_FARMLAND.get(), "Tierra vibranizada de cultivo");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BLACK_GRAVEL.get(), "Grava negra");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BLACK_CLAY.get(), "Arcilla negra");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_SHORT_GRASS.get(), "Hierba corta morada");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_TALL_GRASS.get(), "Hierba alta morada");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_AZALEA.get(), "Azalea morada");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_AZALEA_LEAVES_VIOLET.get(), "Hojas de azalea morada");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_AZALEA_LEAVES_DARK_BLUE.get(), "Hojas de azalea morada indigo");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_AZALEA_LEAVES_CYAN.get(), "Hojas de azalea morada cian");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLOWERING_PURPLE_AZALEA.get(), "Azalea morada florecida");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_VIOLET.get(), "Hojas de azalea morada florecida");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE.get(), "Hojas de azalea morada florecida indigo");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_CYAN.get(), "Hojas de azalea morada florecida cian");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_MOSS_BLOCK.get(), "Bloque de musgo morado");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_MOSS_CARPET.get(), "Alfombra de musgo morado");
                addBlockWithItem(translationBuilder,VibraniumBlocks.POTTED_PURPLE_AZALEA_BUSH.get(), "Maceta con azalea morada");
                addBlockWithItem(translationBuilder,VibraniumBlocks.POTTED_FLOWERING_PURPLE_AZALEA_BUSH.get(), "Maceta con azalea morada florecida");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BIG_PURPLE_DRIPLEAF.get(), "Plantaforma morada grande");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BIG_PURPLE_DRIPLEAF_STEM.get(), "Tallo de plantaforma morada grande");
                addBlockWithItem(translationBuilder,VibraniumBlocks.SMALL_PURPLE_DRIPLEAF.get(), "Plantaforma morada pequeña");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_VINE.get(), "Enredadera morada");
                addBlockWithItem(translationBuilder,VibraniumBlocks.REACTOR_CORE.get(), "Núcleo del reactor");
                addBlockWithItem(translationBuilder,VibraniumBlocks.REACTOR_CONTROL_PANEL.get(), "Panel de control del reactor");
                addBlockWithItem(translationBuilder,VibraniumBlocks.REACTOR_HATCH.get(), "Escotilla de Alimentación del Reactor");
                addBlockWithItem(translationBuilder,VibraniumBlocks.REACTOR_PUMP.get(), "Bomba de refrigeración del reactor");
                addBlockWithItem(translationBuilder,VibraniumBlocks.REACTOR_OUTLET.get(), "Boquilla de evacuación del reactor");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_GLASS.get(), "Vidrio de vibranio");
                addBlockWithItem(translationBuilder,VibraniumBlocks.REINFORCED_VIBRANIUM_GLASS.get(), "Vidrio de vibranio reforzado");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_GLASS_PANE.get(), "Panel de cristal de vibranio");
                addBlockWithItem(translationBuilder,VibraniumBlocks.REINFORCED_VIBRANIUM_GLASS_PANE.get(), "Panel de cristal de vibranio reforzado");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLUID_TANK.get(), "Tanque de fluidos");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BLACK_BRICKS.get(), "Ladrillos negros");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BLACK_BRICK_SLAB.get(), "Losa de ladrillos negros");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BLACK_BRICK_STAIRS.get(), "Escaleras de ladrillos negros");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BLACK_BRICK_WALL.get(), "Muro de ladrillos negros");

                translationBuilder.add(VibraniumBlocks.HEART_SHAPED_HERB_FLOWER.get(), "Flor de hierba en forma de corazón");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES.get(), "Lianas de cueva moradas");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES_PLANT.get(), "Enredaderas de cueva moradas");

                translationBuilder.add(VibraniumBlocks.HEART_SHAPED_HERB_FLOWER.get().asItem(), "Brote de hierba en forma de corazón");
                translationBuilder.add(VibraniumBlocks.BLUE_GLOW_BERRIES.get(), "Baya luminosas azules");
                translationBuilder.add(VibraniumItems.BLACK_CLAY_BALL.get(), "Bola de arcilla negra");
                translationBuilder.add(VibraniumItems.BLACK_BRICK.get(), "Ladrillo negro");
                translationBuilder.add(VibraniumItems.HEART_SHAPED_HERB.get(), "Hierba en forma de corazón");
                translationBuilder.add(VibraniumItems.VIBRANIUM_INGOT.get(), "Lingote de vibranio");
                translationBuilder.add(VibraniumItems.DEPLETED_VIBRANIUM_INGOT.get(), "Lingote de vibranio empobrecido");
                translationBuilder.add(VibraniumItems.BLUE_SOUP.get(), "Sopa azul");
                translationBuilder.add(VibraniumItems.ANCESTRAL_NECTAR.get(), "Néctar ancestral");
                translationBuilder.add(VibraniumItems.BLUE_BERRY_SPIRIT.get(), "Aguardiente de baya azul");
                translationBuilder.add("item.vibranium.blue_berry_spirit.effect.water", "Aguardiente de baya azul");

                translationBuilder.add(VibraniumItems.VIBRANIUM_SPEAR.get(), "Lanza de vibranio");
                translationBuilder.add(VibraniumItems.VIBRANIUM_SWORD.get(), "Espada de vibranio");
                translationBuilder.add(VibraniumItems.VIBRANIUM_SHOVEL.get(), "Pala de vibranio");
                translationBuilder.add(VibraniumItems.VIBRANIUM_PICKAXE.get(), "Pico de vibranio");

                translationBuilder.add(VibraniumItems.HOT_WATER_BUCKET.get(), "Cubo de agua caliente");
                translationBuilder.add(VibraniumFluids.HOT_WATER_BLOCK.get(), "Agua caliente");

                translationBuilder.add(VibraniumCreativeTabs.VIBRANIUM_TAB.get().getDisplayName().getString(), "Vibranio");

                translationBuilder.add("container.vibranium.reactor_control_panel", "Panel de control del reactor");
                translationBuilder.add("container.vibranium.reactor_hatch", "Escotilla de alimentación del reactor");

                translationBuilder.add("gui.vibranium.energy_tooltip", "Energía: %d / 100000 FE");
                translationBuilder.add("gui.vibranium.heat_tooltip", "Temperatura: %d°C / 3000°C");
                translationBuilder.add("gui.vibranium.hot_water_tooltip", "Aqua Caliente: %d / %d mB");
                translationBuilder.add("gui.vibranium.water_tooltip", "Aqua: %d / %d mB");
                translationBuilder.add("gui.vibranium.fuel_tooltip", "Vibranio: quedan %ds");
                translationBuilder.add("tooltip.vibranium.slot_fuel", "Inserta el polvo de Vibranium aquí");
                translationBuilder.add("tooltip.vibranium.slot_output", "Residuos y escorias de combustión");

                translationBuilder.add("tooltip.vibranium.distilled_alcohol", "Destilado en un alambique. ¡Alta graduación!");

                translationBuilder.add("tooltip.vibranium.weapons.charge", "Carga cinética");
                translationBuilder.add("tooltip.vibranium.tool.mode.active", "Propulsión Cinética: ACTIVO");
                translationBuilder.add("tooltip.vibranium.tool.mode.inactive", "Propulsión Cinética: INACTIVO");
                translationBuilder.add("tooltip.vibranium.tool.active.toggle", "Shift + Clic Derecho: Alternar Modo 3x3");
                translationBuilder.add("tooltip.vibranium.pickaxe.active.echolocation", "Clic Derecho: Pulso de Ecolocalización");
                translationBuilder.add("tooltip.vibranium.pickaxe.passive.filter", "Pasivo: Preserva Minerales y Estructuras");
                translationBuilder.add("tooltip.vibranium.shovel.passive.gravity", "Pasivo: Colapsa Bloques Afectados por la Gravedad");
                translationBuilder.add("tooltip.vibranium.shovel.passive.path", "Clic Derecho: Creador de Caminos 3x3");

                translationBuilder.add("message.vibranium.hatch_no_core", "¡Esta escotilla no está conectada a ningún reactor!");
                translationBuilder.add("subtitles.vibranium.meltdown_alarm", "¡Suena la alarma de fusión del reactor!");

                translationBuilder.add("text.vibranium.config.title", "Configuración de Vibranium");
                translationBuilder.add("text.vibranium.config.category.overworld", "Generación de Meteoritos: Overworld");
                translationBuilder.add("text.vibranium.config.category.end", "Generación de Meteoritos: End");
                translationBuilder.add("text.vibranium.config.option.spacing", "Espaciado (Spacing)");
                translationBuilder.add("text.vibranium.config.option.separation", "Separación (Separation)");
                translationBuilder.add("text.vibranium.config.section.weights", "§6Probabilidad de Estructuras (Weights)");
                translationBuilder.add("text.vibranium.config.weight_for", "Probabilidad (Weight) para %s");
                break;
            default:
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_ORE.get(), "Vibranium Ore");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_DIRT.get(), "Vibranized Dirt");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_GRASS_BLOCK.get(), "Vibranized Grass ");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_PATH.get(), "Vibranized Dirt Path");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_FARMLAND.get(), "Vibranised Farmland");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BLACK_GRAVEL.get(), "Black Gravel");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BLACK_CLAY.get(), "Black Clay");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_SHORT_GRASS.get(), "Purple Short Grass");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_TALL_GRASS.get(), "Purple tall Grass");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_AZALEA.get(), "Purple Azalea");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_AZALEA_LEAVES_VIOLET.get(), "Purple Azalea Leaves");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_AZALEA_LEAVES_DARK_BLUE.get(), "Indigo Purple Azalea Leaves");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_AZALEA_LEAVES_CYAN.get(), "Cyan Purple Azalea Leaves");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLOWERING_PURPLE_AZALEA.get(), "Flowering Purple Azalea");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_VIOLET.get(), "Flowering Purple Azalea Leaves");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_DARK_BLUE.get(), "Flowering Indigo Purple Azalea Leaves");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLOWERING_PURPLE_AZALEA_LEAVES_CYAN.get(), "Flowering Cyan Purple Azalea Leaves");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_MOSS_BLOCK.get(), "Purple Moss Block");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_MOSS_CARPET.get(), "Purple Moss Carpet");
                addBlockWithItem(translationBuilder,VibraniumBlocks.POTTED_PURPLE_AZALEA_BUSH.get(), "Potted Purple Azalea");
                addBlockWithItem(translationBuilder,VibraniumBlocks.POTTED_FLOWERING_PURPLE_AZALEA_BUSH.get(), "Potted Flowering Purple Azalea");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BIG_PURPLE_DRIPLEAF.get(), "Big Purple Dripleaf");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BIG_PURPLE_DRIPLEAF_STEM.get(), "Big Purple Dripleaf Stem");
                addBlockWithItem(translationBuilder,VibraniumBlocks.SMALL_PURPLE_DRIPLEAF.get(), "Small Purple Dripleaf");
                addBlockWithItem(translationBuilder,VibraniumBlocks.PURPLE_VINE.get(), "Purple Vine");
                addBlockWithItem(translationBuilder,VibraniumBlocks.REACTOR_CORE.get(), "Reactor Core");
                addBlockWithItem(translationBuilder,VibraniumBlocks.REACTOR_CONTROL_PANEL.get(), "Reactor Control Panel");
                addBlockWithItem(translationBuilder,VibraniumBlocks.REACTOR_HATCH.get(), "Reactor Fuel Hatch");
                addBlockWithItem(translationBuilder,VibraniumBlocks.REACTOR_PUMP.get(), "Reactor Cooling Pump");
                addBlockWithItem(translationBuilder,VibraniumBlocks.REACTOR_OUTLET.get(), "Reactor Outlet Nozzle");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_GLASS.get(), "Vibranium Glass");
                addBlockWithItem(translationBuilder,VibraniumBlocks.REINFORCED_VIBRANIUM_GLASS.get(), "Reinforced Vibranium Glass");
                addBlockWithItem(translationBuilder,VibraniumBlocks.VIBRANIUM_GLASS_PANE.get(), "Vibranium Glass Pane");
                addBlockWithItem(translationBuilder,VibraniumBlocks.REINFORCED_VIBRANIUM_GLASS_PANE.get(), "Reinforced Vibranium Glass Pane");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLUID_TANK.get(), "Fluid Tank");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BLACK_BRICKS.get(), "Black Bricks");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BLACK_BRICK_STAIRS.get(), "Black Brick Stairs");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BLACK_BRICK_SLAB.get(), "Black Brick Slab");
                addBlockWithItem(translationBuilder,VibraniumBlocks.BLACK_BRICK_WALL.get(), "Black Brick Wall");

                translationBuilder.add(VibraniumBlocks.HEART_SHAPED_HERB_FLOWER.get(), "Heart-shaped Herb Flower");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES.get(), "Purple Cave Vines");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES_PLANT.get(), "Purple Cave Vines");

                translationBuilder.add(VibraniumBlocks.HEART_SHAPED_HERB_FLOWER.get().asItem(), "Heart-shaped Herb Sprout");
                translationBuilder.add(VibraniumBlocks.BLUE_GLOW_BERRIES.get(), "Blue Glow Berries");
                translationBuilder.add(VibraniumItems.VIBRANIUM_DUST.get(), "Vibranium Dust");
                translationBuilder.add(VibraniumItems.BLACK_CLAY_BALL.get(), "Black Clay Ball");
                translationBuilder.add(VibraniumItems.BLACK_BRICK.get(), "Black Brick");
                translationBuilder.add(VibraniumItems.HEART_SHAPED_HERB.get(), "Heart-shaped Herb");
                translationBuilder.add(VibraniumItems.VIBRANIUM_INGOT.get(), "Vibranium Ingot");
                translationBuilder.add(VibraniumItems.DEPLETED_VIBRANIUM_INGOT.get(), "Depleted Vibranium Ingot");

                translationBuilder.add(VibraniumItems.BLUE_SOUP.get(), "Blue Soup");
                translationBuilder.add(VibraniumItems.ANCESTRAL_NECTAR.get(), "Ancestral Nectar");
                translationBuilder.add(VibraniumItems.BLUE_BERRY_SPIRIT.get(), "Blue Glow Berries Spirit");
                translationBuilder.add("item.vibranium.blue_berry_spirit.effect.water", "Blue Glow Berries Spirit");

                translationBuilder.add(VibraniumItems.VIBRANIUM_SPEAR.get(), "Vibranium Spear");
                translationBuilder.add(VibraniumItems.VIBRANIUM_SWORD.get(), "Vibranium Sword");
                translationBuilder.add(VibraniumItems.VIBRANIUM_SHOVEL.get(), "Vibranium Shovel");
                translationBuilder.add(VibraniumItems.VIBRANIUM_PICKAXE.get(), "Vibranium Pickaxe");

                translationBuilder.add(VibraniumItems.HOT_WATER_BUCKET.get(), "Hot Water Bucket");
                translationBuilder.add(VibraniumFluids.HOT_WATER_BLOCK.get(), "Hot Water");

                translationBuilder.add(VibraniumCreativeTabs.VIBRANIUM_TAB.get().getDisplayName().getString(), "Vibranium");

                translationBuilder.add("container.vibranium.reactor_control_panel", "Reactor Control Panel");
                translationBuilder.add("container.vibranium.reactor_hatch", "Reactor fuel hatch");

                translationBuilder.add("gui.vibranium.energy_tooltip", "Energy : %d / 100000 FE");
                translationBuilder.add("gui.vibranium.heat_tooltip", "Temperature : %d°C / 3000°C");
                translationBuilder.add("gui.vibranium.hot_water_tooltip", "Hot Water: %d / %d mB");
                translationBuilder.add("gui.vibranium.water_tooltip", "Water: %d / %d mB");
                translationBuilder.add("gui.vibranium.fuel_tooltip", "Vibranium : %ds restants");
                translationBuilder.add("tooltip.vibranium.slot_fuel", "Insert Vibranium powder here");
                translationBuilder.add("tooltip.vibranium.slot_output", "Combustion waste and slag");

                translationBuilder.add("tooltip.vibranium.distilled_alcohol", "Distilled in a brewing stand. High potency!");

                translationBuilder.add("tooltip.vibranium.weapons.charge", "Kinetic Charge");
                translationBuilder.add("tooltip.vibranium.tool.mode.active", "Kinetic Burst: ACTIVE");
                translationBuilder.add("tooltip.vibranium.tool.mode.inactive", "Kinetic Burst: INACTIVE");
                translationBuilder.add("tooltip.vibranium.tool.active.toggle", "Shift + Right Click: Toggle 3x3 Mode");
                translationBuilder.add("tooltip.vibranium.pickaxe.active.echolocation", "Right Click: Echolocation Pulse");
                translationBuilder.add("tooltip.vibranium.pickaxe.passive.filter", "Passive: Preserves Ores & Structures");
                translationBuilder.add("tooltip.vibranium.shovel.passive.gravity", "Passive: Collapse Falling Blocks");
                translationBuilder.add("tooltip.vibranium.shovel.passive.path", "Right Click: 3x3 Path Maker");

                translationBuilder.add("message.vibranium.hatch_no_core", "This hatch is not connected to any reactor core!");
                translationBuilder.add("subtitles.vibranium.meltdown_alarm", "Reactor Meltdown Alarm blares!");

                translationBuilder.add("text.vibranium.config.title", "Vibranium Configuration");
                translationBuilder.add("text.vibranium.config.category.overworld", "Meteorite Generation: Overworld");
                translationBuilder.add("text.vibranium.config.category.end", "Meteorite Generation: The End");
                translationBuilder.add("text.vibranium.config.option.spacing", "Spacing");
                translationBuilder.add("text.vibranium.config.option.separation", "Separation");
                translationBuilder.add("text.vibranium.config.section.weights", "§6Structure Spawn Weights");
                translationBuilder.add("text.vibranium.config.weight_for", "Weight for %s");
        }

    }
    ///To provide double translations for block items
    private void addBlockWithItem(TranslationBuilder builder, net.minecraft.world.level.block.Block block, String name) {
        if (block == null) return;
        builder.add(block, name);

        Item item = block.asItem();
        if (item != net.minecraft.world.item.Items.AIR) {
            builder.add(item, name);
        }
    }
}


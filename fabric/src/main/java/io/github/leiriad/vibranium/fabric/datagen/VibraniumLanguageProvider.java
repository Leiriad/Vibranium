package io.github.leiriad.vibranium.fabric.datagen;

import io.github.leiriad.vibranium.VibraniumMod;
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
                //--Blocks with items--
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
                addBlockWithItem(translationBuilder,VibraniumBlocks.KILL_SWITCH.get(), "Bouton d'arrêt d'urgence");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLAT_ELECTRIC_LAMP.get(), "Lampe éléctrique plate");
                addBlockWithItem(translationBuilder, VibraniumBlocks.ELECTRIC_HEATER.get(), "Radiateur électrique");

                //--Blocks--
                translationBuilder.add(VibraniumBlocks.HEART_SHAPED_HERB_FLOWER.get(), "Fleur d'herbe cœur");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES.get(), "Liane des cavernes pourpre");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES_PLANT.get(), "Liane des cavernes pourpre");
                translationBuilder.add(VibraniumBlocks.ELECTRIC_WIRE.get(), "Fil électrique");
                translationBuilder.add(VibraniumBlocks.ELECTRIC_WIRE_WALL.get(), "Fil électrique");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_BLOCK.get(), "Block de vibranium");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_GRATE.get(), "Grille de vibranium");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_DOOR.get(), "Porte en vibranium");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_TRAPDOOR.get(), "Trappe en vibranium");
                translationBuilder.add(VibraniumBlocks.DEPLETED_VIBRANIUM_BLOCK.get(), "Bloc de vibranium appauvrit");
                translationBuilder.add(VibraniumBlocks.DEPLETED_VIBRANIUM_GRATE.get(), "Grille de vibranium appauvrit");
                translationBuilder.add(VibraniumBlocks.DEPLETED_VIBRANIUM_DOOR.get(), "Porte en vibranium appauvrit");
                translationBuilder.add(VibraniumBlocks.DEPLETED_VIBRANIUM_TRAPDOOR.get(), "Trappe en vibranium appauvrit");

                //-- Block items --
                translationBuilder.add(VibraniumBlocks.HEART_SHAPED_HERB_FLOWER.get().asItem(), "Pousse d'herbe cœur");
                translationBuilder.add(VibraniumBlocks.BLUE_GLOW_BERRIES.get(), "Baies lumineuses bleues");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_BLOCK_ITEM.get(), "Block de vibranium");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_GRATE_ITEM.get(), "Grille de vibranium");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_DOOR_ITEM.get(), "Porte en vibranium");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_TRAPDOOR_ITEM.get(), "Trappe en vibranium");
                translationBuilder.add(VibraniumBlocks.DEPLETED_VIBRANIUM_BLOCK_ITEM.get(), "Bloc de vibranium appauvrit");
                translationBuilder.add(VibraniumBlocks.DEPLETED_VIBRANIUM_GRATE_ITEM.get(), "Grille de vibranium appauvrit");
                translationBuilder.add(VibraniumBlocks.DEPLETED_VIBRANIUM_DOOR_ITEM.get(), "Porte en vibranium appauvrit");
                translationBuilder.add(VibraniumBlocks.DEPLETED_VIBRANIUM_TRAPDOOR_ITEM.get(), "Trappe en vibranium appauvrit");

                //-- Items --
                translationBuilder.add(VibraniumItems.VIBRANIUM_DUST.get(), "Poudre de vibranium");
                translationBuilder.add(VibraniumItems.BLACK_CLAY_BALL.get(), "Boule d'argile noire");
                translationBuilder.add(VibraniumItems.BLACK_BRICK.get(), "Brique noire");
                translationBuilder.add(VibraniumItems.HEART_SHAPED_HERB.get(), "Herbe coeur");
                translationBuilder.add(VibraniumItems.VIBRANIUM_INGOT.get(), "Lingot de vibranium");
                translationBuilder.add(VibraniumItems.DEPLETED_VIBRANIUM_INGOT.get(), "Lingot de vibranium appauvrit");
                translationBuilder.add(VibraniumItems.BLUE_SOUP.get(), "Soupe bleue");
                translationBuilder.add(VibraniumItems.ANCESTRAL_NECTAR.get(), "Nectar ancestral");
                translationBuilder.add(VibraniumItems.BLUE_BERRY_SPIRIT.get(), "Eau de vie de baies bleues");
                translationBuilder.add(VibraniumItems.VIBRANIUM_SPEAR.get(), "Lance en vibranium");
                translationBuilder.add(VibraniumItems.VIBRANIUM_SWORD.get(), "Épée en vibranium");
                translationBuilder.add(VibraniumItems.VIBRANIUM_SHOVEL.get(), "Pelle en vibranium");
                translationBuilder.add(VibraniumItems.VIBRANIUM_PICKAXE.get(), "Pioche en vibranium");
                translationBuilder.add(VibraniumItems.VIBRANIUM_HOE.get(), "Houe en vibranium");
                translationBuilder.add(VibraniumItems.VIBRANIUM_AXE.get(), "Hache en vibranium");
                translationBuilder.add(VibraniumItems.HOT_WATER_BUCKET.get(), "Seau d'eau chaude");

                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".blue_berry_spirit.effect.water", "Eau de vie de baies bleues");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".black_electric_wire", "Fil électrique noir");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".blue_electric_wire", "Fil électrique bleu");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".brown_electric_wire", "Fil électrique marron");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".cyan_electric_wire", "Fil électrique cyan");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".gray_electric_wire", "Fil électrique gris");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".green_electric_wire", "Fil électrique vert");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".light_blue_electric_wire", "Fil électrique bleu clair");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".light_gray_electric_wire", "Fil électrique gris clair");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".lime_electric_wire", "Fil électrique vert clair");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".magenta_electric_wire", "Fil électrique magenta");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".orange_electric_wire", "Fil électrique orange");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".pink_electric_wire", "Fil électrique rose");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".purple_electric_wire", "Fil électrique violet");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".red_electric_wire", "Fil électrique rouge");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".white_electric_wire", "Fil électrique blanc");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".yellow_electric_wire", "Fil électrique jaune");

                //-- Fluids --
                translationBuilder.add(VibraniumFluids.HOT_WATER_BLOCK.get(), "Eau chaude");

                //-- Tabs --
                translationBuilder.add(VibraniumCreativeTabs.VIBRANIUM_TAB.get().getDisplayName().getString(), "Vibranium");

                //-- Text --
                translationBuilder.add("container." + VibraniumMod.MOD_ID + ".reactor_control_panel", "Panneau de contrôle du réacteur");
                translationBuilder.add("container." + VibraniumMod.MOD_ID + ".reactor_hatch", "Trappe d'alimentation du réacteur");

                translationBuilder.add("gui." + VibraniumMod.MOD_ID + ".energy_tooltip", "Énergie : %d / 100000 FE");
                translationBuilder.add("gui." + VibraniumMod.MOD_ID + ".heat_tooltip", "Température : %d°C / 3000°C");
                translationBuilder.add("gui." + VibraniumMod.MOD_ID + ".water_tooltip", "Eau : %d / %d mB");
                translationBuilder.add("gui." + VibraniumMod.MOD_ID + ".hot_water_tooltip", "Eau Chaude : %d / %d mB");
                translationBuilder.add("gui." + VibraniumMod.MOD_ID + ".fuel_tooltip", "Vibranium : %ds restants");

                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".slot_fuel", "Insérez la poudre de Vibranium ici");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".slot_output", "Résidus et scories de combustion");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".distilled_alcohol", "Distillé dans un alambic. Haute concentration !");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".weapons.charge", "Charge cinétique");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".tool.mode.active", "Propulsion Cinétique : ACTIF");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".tool.mode.inactive", "Propulsion Cinétique : INACTIF");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".tool.active.toggle", "Shift + Clic Droit : Alterner Mode 3x3");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".pickaxe.active.echolocation", "Clic Droit : Impulsion d'Écholocalisation");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".pickaxe.passive.filter", "Passif : Préserve les Minerais et Structures");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".shovel.passive.gravity", "Passif : Fait S'effondrer les Blocs Soumis à la Gravité");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".shovel.passive.path", "Clic Droit : Créateur de Chemins 3x3");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".hoe.active.resonant_till", "Labourage par résonance : Laboure une zone de 3x3");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".hoe.active.sonic_harvest", "Moisson sonique : Casser une culture mûre libère une onde de récolte");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".hoe.passive.sculk_suppression", "Amortissement acoustique : Miner du Sculk ne produit aucune vibration.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".axe.active.cleave", "Clic Droit : Tranchant Cinétique");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".axe.passive.shield_break", "Passif : Désarme les Boucliers par Surcharge Cinétique");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".wire.tranfer_rate", "Taux de transfert : %s K E/t");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".block.charge_info", "Accumule de l'énergie cinétique en subissant des coups ou en recevant des projectiles ou chutes.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".block.release_info", "Clic droit ou signal Redstone pour libérer une onde de choc.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".block.shockwave_effect", "L'onde de choc repousse les entités et casse tous les blocs aux alentours.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".grate.info", "Laisse passer les fluides et les objets librement.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".grate.repulsion", "Émet une impulsion cinétique directionnelle lorsqu'une entité vivante marche dessus si alimentée par de la redstone.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".door.repulsion", "Repousse les attaquants par onde cinétique en cas de choc ou contact non autorisé.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".door.interaction", "Nécessite de la Redstone ou une pioche en diamant pour interagir en sécurité.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".trapdoor.repulsion", "Éjecte les entités vers le haut par énergie cinétique si l'on marche ou frappe dessus.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".trapdoor.interaction", "Nécessite de la Redstone ou une pioche en diamant pour interagir en sécurité.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".depleted_block.sound_info", "Absorbe les sons environnants et bloque les vibrations acoustiques.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".depleted_block.blast_info", "Extrêmement dense, offre une résistance presque totale aux explosions.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".depleted_grate.sound_info", "Atténue partiellement les sons proches et le bruit de la pluie.");

                translationBuilder.add("message." + VibraniumMod.MOD_ID + ".hatch_no_core", "Cette trappe n'est reliée à aucun réacteur !");
                translationBuilder.add("subtitles." + VibraniumMod.MOD_ID + ".meltdown_alarm", "L'alarme de surchauffe du réacteur sonne !");

                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.title", "Configuration de Vibranium");
                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.category.overworld", "Génération de Météorites : Overworld");
                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.category.end", "Génération de Météorites : End");
                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.option.spacing", "Espacement (Spacing)");
                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.option.separation", "Séparation (Separation)");
                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.section.weights", "§6Probabilité d'apparition des structures (Weights)");
                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.weight_for", "Poids (Weight) pour %s");
                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.tooltip.spacing", "La distance maximale (en chunks) pour la grille de génération. Des valeurs plus élevées rendent les structures plus rares.");
                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.tooltip.separation", "La distance minimale (en chunks) entre les structures. Doit être inférieure à l'espacement (spacing).");
                break;
            case "es_es":
                //--Blocks with items--
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
                addBlockWithItem(translationBuilder,VibraniumBlocks.KILL_SWITCH.get(), "Interruptor de apagado de emergencia");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLAT_ELECTRIC_LAMP.get(), "Lámpara eléctrica plana");
                addBlockWithItem(translationBuilder, VibraniumBlocks.ELECTRIC_HEATER.get(), "Radiador eléctrico");

                //--Blocks--
                translationBuilder.add(VibraniumBlocks.HEART_SHAPED_HERB_FLOWER.get(), "Flor de hierba en forma de corazón");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES.get(), "Lianas de cueva moradas");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES_PLANT.get(), "Enredaderas de cueva moradas");
                translationBuilder.add(VibraniumBlocks.ELECTRIC_WIRE.get(), "Cable eléctrico");
                translationBuilder.add(VibraniumBlocks.ELECTRIC_WIRE_WALL.get(), "Cable eléctrico");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_BLOCK.get(), "Bloque de vibranio");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_GRATE.get(), "Rejilla de vibranio");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_DOOR.get(), "Puerta de vibranium");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_TRAPDOOR.get(), "Trampilla de vibranium");
                translationBuilder.add(VibraniumBlocks.DEPLETED_VIBRANIUM_BLOCK.get(), "Bloque de vibranio empobrecido");
                translationBuilder.add(VibraniumBlocks.DEPLETED_VIBRANIUM_GRATE.get(), "Rejilla de vibranio empobrecido");
                translationBuilder.add(VibraniumBlocks.DEPLETED_VIBRANIUM_DOOR.get(), "Puerta de vibranio empobrecido");
                translationBuilder.add(VibraniumBlocks.DEPLETED_VIBRANIUM_TRAPDOOR.get(), "Trampilla de vibranio empobrecido");

                //-- Block items --
                translationBuilder.add(VibraniumBlocks.HEART_SHAPED_HERB_FLOWER.get().asItem(), "Brote de hierba en forma de corazón");
                translationBuilder.add(VibraniumBlocks.BLUE_GLOW_BERRIES.get(), "Baya luminosas azules");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_BLOCK_ITEM.get(), "Bloque de vibranio");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_GRATE_ITEM.get(), "Rejilla de vibranio");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_DOOR_ITEM.get(), "Puerta de vibranium");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_TRAPDOOR_ITEM.get(), "Trampilla de vibranium");
                translationBuilder.add(VibraniumBlocks.DEPLETED_VIBRANIUM_BLOCK_ITEM.get(), "Bloque de vibranio empobrecido");
                translationBuilder.add(VibraniumBlocks.DEPLETED_VIBRANIUM_GRATE_ITEM.get(), "Rejilla de vibranio empobrecido");
                translationBuilder.add(VibraniumBlocks.DEPLETED_VIBRANIUM_DOOR_ITEM.get(), "Puerta de vibranio empobrecido");
                translationBuilder.add(VibraniumBlocks.DEPLETED_VIBRANIUM_TRAPDOOR_ITEM.get(), "Trampilla de vibranio empobrecido");

                //-- Items --
                translationBuilder.add(VibraniumItems.BLACK_CLAY_BALL.get(), "Bola de arcilla negra");
                translationBuilder.add(VibraniumItems.BLACK_BRICK.get(), "Ladrillo negro");
                translationBuilder.add(VibraniumItems.HEART_SHAPED_HERB.get(), "Hierba en forma de corazón");
                translationBuilder.add(VibraniumItems.VIBRANIUM_INGOT.get(), "Lingote de vibranio");
                translationBuilder.add(VibraniumItems.DEPLETED_VIBRANIUM_INGOT.get(), "Lingote de vibranio empobrecido");
                translationBuilder.add(VibraniumItems.BLUE_SOUP.get(), "Sopa azul");
                translationBuilder.add(VibraniumItems.ANCESTRAL_NECTAR.get(), "Néctar ancestral");
                translationBuilder.add(VibraniumItems.BLUE_BERRY_SPIRIT.get(), "Aguardiente de baya azul");
                translationBuilder.add(VibraniumItems.VIBRANIUM_SPEAR.get(), "Lanza de vibranio");
                translationBuilder.add(VibraniumItems.VIBRANIUM_SWORD.get(), "Espada de vibranio");
                translationBuilder.add(VibraniumItems.VIBRANIUM_SHOVEL.get(), "Pala de vibranio");
                translationBuilder.add(VibraniumItems.VIBRANIUM_PICKAXE.get(), "Pico de vibranio");
                translationBuilder.add(VibraniumItems.VIBRANIUM_HOE.get(), "Azada de vibranio");
                translationBuilder.add(VibraniumItems.VIBRANIUM_AXE.get(), "Hacha de vibranio");
                translationBuilder.add(VibraniumItems.HOT_WATER_BUCKET.get(), "Cubo de agua caliente");

                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".blue_berry_spirit.effect.water", "Aguardiente de baya azul");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".black_electric_wire", "Cable eléctrico negro");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".blue_electric_wire", "Cable eléctrico azul");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".brown_electric_wire", "Cable eléctrico marrón");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".cyan_electric_wire", "Cable eléctrico cian");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".gray_electric_wire", "Cable eléctrico gris");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".green_electric_wire", "Cable eléctrico verde");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".light_blue_electric_wire", "Cable eléctrico azul claro");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".light_gray_electric_wire", "Cable eléctrico gris claro");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".lime_electric_wire", "Cable eléctrico verde lima");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".magenta_electric_wire", "Cable eléctrico magenta");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".orange_electric_wire", "Cable eléctrico naranja");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".pink_electric_wire", "Cable eléctrico rosa");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".purple_electric_wire", "Cable eléctrico morado");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".red_electric_wire", "Cable eléctrico rojo");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".white_electric_wire", "Cable eléctrico blanco");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".yellow_electric_wire", "Cable eléctrico amarillo");

                //-- Fluids --
                translationBuilder.add(VibraniumFluids.HOT_WATER_BLOCK.get(), "Agua caliente");

                //-- Tabs --
                translationBuilder.add(VibraniumCreativeTabs.VIBRANIUM_TAB.get().getDisplayName().getString(), "Vibranio");

                //-- Text --
                translationBuilder.add("container." + VibraniumMod.MOD_ID + ".reactor_control_panel", "Panel de control del reactor");
                translationBuilder.add("container." + VibraniumMod.MOD_ID + ".reactor_hatch", "Escotilla de alimentación del reactor");

                translationBuilder.add("gui." + VibraniumMod.MOD_ID + ".energy_tooltip", "Energía: %d / 100000 FE");
                translationBuilder.add("gui." + VibraniumMod.MOD_ID + ".heat_tooltip", "Temperatura: %d°C / 3000°C");
                translationBuilder.add("gui." + VibraniumMod.MOD_ID + ".hot_water_tooltip", "Aqua Caliente: %d / %d mB");
                translationBuilder.add("gui." + VibraniumMod.MOD_ID + ".water_tooltip", "Aqua: %d / %d mB");
                translationBuilder.add("gui." + VibraniumMod.MOD_ID + ".fuel_tooltip", "Vibranio: quedan %ds");

                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".slot_fuel", "Inserta el polvo de Vibranium aquí");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".slot_output", "Residuos y escorias de combustión");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".distilled_alcohol", "Destilado en un alambique. ¡Alta graduación!");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".weapons.charge", "Carga cinética");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".tool.mode.active", "Propulsión Cinética: ACTIVO");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".tool.mode.inactive", "Propulsión Cinética: INACTIVO");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".tool.active.toggle", "Shift + Clic Derecho: Alternar Modo 3x3");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".pickaxe.active.echolocation", "Clic Derecho: Pulso de Ecolocalización");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".pickaxe.passive.filter", "Pasivo: Preserva Minerales y Estructuras");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".shovel.passive.gravity", "Pasivo: Colapsa Bloques Afectados por la Gravedad");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".shovel.passive.path", "Clic Derecho: Creador de Caminos 3x3");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".hoe.active.resonant_till", "Labrado resonante: Labra un área de 3x3 de tierra");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".hoe.active.sonic_harvest", "Cosecha sónica: Romper cultivos maduros libera una onda de cosecha");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".hoe.passive.sculk_suppression", "Amortiguación acústica: Minar Sculk no genera vibraciones.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".axe.active.cleave", "Clic Derecho: Filo Cinético");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".axe.passive.shield_break", "Pasivo: Desarma Escudos por Sobrecarga Cinética");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".wire.tranfer_rate", "Tasa de transferencia: %s K E/t");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".block.charge_info", "Acumula energía cinética al recibir golpes, proyectiles o caídas.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".block.release_info", "Clic derecho o señal de Redstone para liberar una onda de choque.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".block.shockwave_effect", "La onda de choque empuja a las entidades y rompe todos los bloques a su alrededor.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".grate.info", "Permite el paso libre de fluidos u objetos.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".grate.repulsion", "Emite un impulso cinético direccional cuando una entidad viva la pisa si está alimentada por redstone.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".door.repulsion", "Repele a los atacantes con una onda cinética al recibir un golpe o contacto no autorizado.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".door.interaction", "Requiere Redstone o un pico de diamante para interactuar de forma segura.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".trapdoor.repulsion", "Lanza a las entidades hacia arriba con energía cinética al pisarla o golpearla.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".trapdoor.interaction", "Requiere Redstone o un pico de diamante para interactuar de forma segura.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".depleted_block.sound_info", "Absorbe los sonidos cercanos y bloquea las vibraciones acústicas.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".depleted_block.blast_info", "Extremadamente denso, ofrece una resistencia casi total a las explosiones.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".depleted_grate.sound_info", "Atenúa parcialmente los sonidos cercanos y el ruido de la lluvia.");

                translationBuilder.add("message." + VibraniumMod.MOD_ID + ".hatch_no_core", "¡Esta escotilla no está conectada a ningún reactor!");
                translationBuilder.add("subtitles." + VibraniumMod.MOD_ID + ".meltdown_alarm", "¡Suena la alarma de fusión del reactor!");

                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.title", "Configuración de Vibranium");
                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.category.overworld", "Generación de Meteoritos: Overworld");
                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.category.end", "Generación de Meteoritos: End");
                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.option.spacing", "Espaciado (Spacing)");
                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.option.separation", "Separación (Separation)");
                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.section.weights", "§6Probabilidad de Estructuras (Weights)");
                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.weight_for", "Probabilidad (Weight) para %s");
                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.tooltip.spacing", "La distancia máxima (en chunks) para la cuadrícula de generación. Los valores más altos hacen que las estructuras sean más raras.");
                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.tooltip.separation", "La distancia mínima (en chunks) entre estructuras. Debe ser menor que el espaciado (spacing).");
                break;
            default:
                //--Blocks with items--
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
                addBlockWithItem(translationBuilder,VibraniumBlocks.KILL_SWITCH.get(), "Kill Switch");
                addBlockWithItem(translationBuilder,VibraniumBlocks.FLAT_ELECTRIC_LAMP.get(), "Flat Electric Lamp");
                addBlockWithItem(translationBuilder,VibraniumBlocks.ELECTRIC_HEATER.get(), "Electric Heater");

                //--Blocks--
                translationBuilder.add(VibraniumBlocks.HEART_SHAPED_HERB_FLOWER.get(), "Heart-shaped Herb Flower");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES.get(), "Purple Cave Vines");
                translationBuilder.add(VibraniumBlocks.PURPLE_CAVE_VINES_PLANT.get(), "Purple Cave Vines");
                translationBuilder.add(VibraniumBlocks.ELECTRIC_WIRE.get(), "Electric Wire");
                translationBuilder.add(VibraniumBlocks.ELECTRIC_WIRE_WALL.get(), "Electric Wire");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_BLOCK.get(), "Vibranium Block");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_GRATE.get(), "Vibranium Grate");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_DOOR.get(), "Vibranium Door");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_TRAPDOOR.get(), "Vibranium Trapdoor");
                translationBuilder.add(VibraniumBlocks.DEPLETED_VIBRANIUM_BLOCK.get(), "Depleted Vibranium Block");
                translationBuilder.add(VibraniumBlocks.DEPLETED_VIBRANIUM_GRATE.get(), "Depleted Vibranium Grate");
                translationBuilder.add(VibraniumBlocks.DEPLETED_VIBRANIUM_DOOR.get(), "Depleted Vibranium Door");
                translationBuilder.add(VibraniumBlocks.DEPLETED_VIBRANIUM_TRAPDOOR.get(), "Depleted Vibranium Trapdoor");

                //-- Block items --
                translationBuilder.add(VibraniumBlocks.HEART_SHAPED_HERB_FLOWER.get().asItem(), "Heart-shaped Herb Sprout");
                translationBuilder.add(VibraniumBlocks.BLUE_GLOW_BERRIES.get(), "Blue Glow Berries");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_BLOCK_ITEM.get(), "Vibranium Block");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_GRATE_ITEM.get(), "Vibranium Grate");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_DOOR_ITEM.get(), "Vibranium Door");
                translationBuilder.add(VibraniumBlocks.VIBRANIUM_TRAPDOOR_ITEM.get(), "Vibranium Trapdoor");
                translationBuilder.add(VibraniumBlocks.DEPLETED_VIBRANIUM_BLOCK_ITEM.get(), "Depleted Vibranium Block");
                translationBuilder.add(VibraniumBlocks.DEPLETED_VIBRANIUM_GRATE_ITEM.get(), "Depleted Vibranium Grate");
                translationBuilder.add(VibraniumBlocks.DEPLETED_VIBRANIUM_DOOR_ITEM.get(), "Depleted Vibranium Door");
                translationBuilder.add(VibraniumBlocks.DEPLETED_VIBRANIUM_TRAPDOOR_ITEM.get(), "Depleted Vibranium Trapdoor");

                //-- Items --
                translationBuilder.add(VibraniumItems.VIBRANIUM_DUST.get(), "Vibranium Dust");
                translationBuilder.add(VibraniumItems.BLACK_CLAY_BALL.get(), "Black Clay Ball");
                translationBuilder.add(VibraniumItems.BLACK_BRICK.get(), "Black Brick");
                translationBuilder.add(VibraniumItems.HEART_SHAPED_HERB.get(), "Heart-shaped Herb");
                translationBuilder.add(VibraniumItems.VIBRANIUM_INGOT.get(), "Vibranium Ingot");
                translationBuilder.add(VibraniumItems.DEPLETED_VIBRANIUM_INGOT.get(), "Depleted Vibranium Ingot");
                translationBuilder.add(VibraniumItems.BLUE_SOUP.get(), "Blue Soup");
                translationBuilder.add(VibraniumItems.ANCESTRAL_NECTAR.get(), "Ancestral Nectar");
                translationBuilder.add(VibraniumItems.BLUE_BERRY_SPIRIT.get(), "Blue Glow Berries Spirit");

                translationBuilder.add(VibraniumItems.VIBRANIUM_SPEAR.get(), "Vibranium Spear");
                translationBuilder.add(VibraniumItems.VIBRANIUM_SWORD.get(), "Vibranium Sword");
                translationBuilder.add(VibraniumItems.VIBRANIUM_SHOVEL.get(), "Vibranium Shovel");
                translationBuilder.add(VibraniumItems.VIBRANIUM_PICKAXE.get(), "Vibranium Pickaxe");
                translationBuilder.add(VibraniumItems.VIBRANIUM_HOE.get(), "Vibranium Hoe");
                translationBuilder.add(VibraniumItems.VIBRANIUM_AXE.get(), "Vibranium Axe");
                translationBuilder.add(VibraniumItems.HOT_WATER_BUCKET.get(), "Hot Water Bucket");

                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".blue_berry_spirit.effect.water", "Blue Glow Berries Spirit");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".black_electric_wire", "Black Electric Wire");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".blue_electric_wire", "Blue Electric Wire");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".brown_electric_wire", "Brown Electric Wire");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".cyan_electric_wire", "Cyan Electric Wire");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".gray_electric_wire", "Gray Electric Wire");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".green_electric_wire", "Green Electric Wire");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".light_blue_electric_wire", "Light Blue Electric Wire");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".light_gray_electric_wire", "Light Gray Electric Wire");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".lime_electric_wire", "Lime Electric Wire");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".magenta_electric_wire", "Magenta Electric Wire");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".orange_electric_wire", "Orange Electric Wire");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".pink_electric_wire", "Pink Electric Wire");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".purple_electric_wire", "Purple Electric Wire");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".red_electric_wire", "Red Electric Wire");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".white_electric_wire", "White Electric Wire");
                translationBuilder.add("item." + VibraniumMod.MOD_ID + ".yellow_electric_wire", "Yellow Electric Wire");

                //-- Fluids --
                translationBuilder.add(VibraniumFluids.HOT_WATER_BLOCK.get(), "Hot Water");

                //-- Tabs --
                translationBuilder.add(VibraniumCreativeTabs.VIBRANIUM_TAB.get().getDisplayName().getString(), "Vibranium");

                //-- Text --
                translationBuilder.add("container." + VibraniumMod.MOD_ID + ".reactor_control_panel", "Reactor Control Panel");
                translationBuilder.add("container." + VibraniumMod.MOD_ID + ".reactor_hatch", "Reactor fuel hatch");

                translationBuilder.add("gui." + VibraniumMod.MOD_ID + ".energy_tooltip", "Energy : %d / 100000 FE");
                translationBuilder.add("gui." + VibraniumMod.MOD_ID + ".heat_tooltip", "Temperature : %d°C / 3000°C");
                translationBuilder.add("gui." + VibraniumMod.MOD_ID + ".hot_water_tooltip", "Hot Water: %d / %d mB");
                translationBuilder.add("gui." + VibraniumMod.MOD_ID + ".water_tooltip", "Water: %d / %d mB");
                translationBuilder.add("gui." + VibraniumMod.MOD_ID + ".fuel_tooltip", "Vibranium : %ds restants");

                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".slot_fuel", "Insert Vibranium powder here");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".slot_output", "Combustion waste and slag");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".distilled_alcohol", "Distilled in a brewing stand. High potency!");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".weapons.charge", "Kinetic Charge");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".tool.mode.active", "Kinetic Burst: ACTIVE");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".tool.mode.inactive", "Kinetic Burst: INACTIVE");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".tool.active.toggle", "Shift + Right Click: Toggle 3x3 Mode");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".pickaxe.active.echolocation", "Right Click: Echolocation Pulse");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".pickaxe.passive.filter", "Passive: Preserves Ores & Structures");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".shovel.passive.gravity", "Passive: Collapse Falling Blocks");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".shovel.passive.path", "Right Click: 3x3 Path Maker");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".hoe.active.resonant_till", "Resonant Tilling: Tills a 3x3 area of soil");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".hoe.active.sonic_harvest", "Sonic Harvest: Breaking mature crops releases a harvesting shockwave");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".hoe.passive.sculk_suppression", "Acoustic Dampening: Mining Sculk produces no vibrations.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".axe.active.cleave", "Right-Click: Kinetic Edge");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".axe.passive.shield_break", "Passive: Disarms Shields via Kinetic Overload");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID +".wire.tranfer_rate", "Transfer rate: %s K E/t");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".block.charge_info", "Stores kinetic energy when taking hits, projectiles, or is fallen on.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".block.release_info", "Right-click or apply Redstone to unleash a shockwave.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".block.shockwave_effect", "The shockwave knocks back entities and breaks all surrounding blocks.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".grate.info", "Allows fluids and items to pass through freely.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".grate.repulsion", "Emits a directional kinetic pulse when a living entity steps on it if powered by redstone.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".door.repulsion", "Repels attackers with a kinetic shockwave on hit or unauthorized contact.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".door.interaction", "Requires Redstone or a Diamond Pickaxe to manipulate or open safely.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".trapdoor.repulsion", "Launches entities upward with kinetic energy when stepped on or struck.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".trapdoor.interaction", "Requires Redstone or a Diamond Pickaxe to interact safely.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".depleted_block.sound_info", "Absorbs surrounding sounds and blocks acoustic vibrations.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".depleted_block.blast_info", "Extremely dense, offering near-total resistance to explosions.");
                translationBuilder.add("tooltip." + VibraniumMod.MOD_ID + ".depleted_grate.sound_info", "Partially dampens nearby sounds and weather noise.");

                translationBuilder.add("message." + VibraniumMod.MOD_ID + ".hatch_no_core", "This hatch is not connected to any reactor core!");
                translationBuilder.add("subtitles." + VibraniumMod.MOD_ID + ".meltdown_alarm", "Reactor Meltdown Alarm blares!");

                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.title", "Vibranium Configuration");
                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.category.overworld", "Meteorite Generation: Overworld");
                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.category.end", "Meteorite Generation: The End");
                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.option.spacing", "Spacing");
                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.option.separation", "Separation");
                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.section.weights", "§6Structure Spawn Weights");
                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.weight_for", "Weight for %s");
                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.tooltip.spacing", "The maximum distance (in chunks) for the generation grid. Higher values make structures rarer.");
                translationBuilder.add("text." + VibraniumMod.MOD_ID + ".config.tooltip.separation", "The minimum distance (in chunks) between structures. Must be lower than spacing.");
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


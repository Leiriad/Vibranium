package vibranium.block;

import net.minecraft.world.level.block.AzaleaBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class PurpleAzalea extends AzaleaBlock {
    public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties settings) {
        return BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE);
    }
    public PurpleAzalea(Properties properties) {
        super(properties);
    }
}

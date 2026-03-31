package vibranium.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import java.util.function.ToIntFunction;

public class VibraniumOre extends Block {

    public VibraniumOre(Properties properties) {
        super(properties
                .strength(5.0f, 15.0f) // Dureté et résistance aux explosions
                .lightLevel(state -> 5) // Luminosité fixe de 5
        );
    }
}

package io.github.leiriad.vibranium.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class BlueSoup extends Item {

    public BlueSoup(Properties properties) {super(properties);}
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        ItemStack resultStack = super.finishUsingItem(stack, level, user);

        if (user instanceof Player player && !player.getAbilities().instabuild) {
            return new ItemStack(Items.BOWL);
        }

        return resultStack;
    }
}

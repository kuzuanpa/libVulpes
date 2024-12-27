package zmaster587.libVulpes.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemBlockMeta extends  ItemBlockWithMetadata {

	public ItemBlockMeta(Block p_i45326_1_) {
		super(p_i45326_1_, p_i45326_1_);
	}

	@Override
	public @NotNull String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "." + stack.getItemDamage();
	}

}

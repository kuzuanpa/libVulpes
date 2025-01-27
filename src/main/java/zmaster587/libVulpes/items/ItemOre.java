package zmaster587.libVulpes.items;

import java.util.Locale;

import zmaster587.libVulpes.block.BlockOre;
import zmaster587.libVulpes.block.INamedMetaBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemOre extends ItemBlockWithMetadata {

	public ItemOre(Block p_i45326_1_) {
		super(p_i45326_1_, p_i45326_1_);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return ((INamedMetaBlock)this.field_150939_a).getUnlocalizedName(stack.getItemDamage());
	}
	
	@Override
    public String getItemStackDisplayName(ItemStack stack)
    {
		String translate = "tile." + this.getUnlocalizedNameInefficiently(stack).substring(9) + "." + ((BlockOre)this.field_150939_a).getProduct().name().toLowerCase(Locale.ENGLISH) + ".name";
		if(StatCollector.canTranslate(translate))
			return StatCollector.translateToLocal(translate);
		return (StatCollector.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name") + " " + StatCollector.translateToLocal("type." + ((BlockOre)this.field_150939_a).getProduct().name().toLowerCase(Locale.ENGLISH) + ".name")).trim();
    }
}

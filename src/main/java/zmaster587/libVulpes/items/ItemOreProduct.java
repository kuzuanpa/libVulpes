package zmaster587.libVulpes.items;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import zmaster587.libVulpes.api.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemOreProduct extends Item {

	final HashMap<Integer, Material> properties;
	final String outputType;

	public ItemOreProduct(String outputType) {
		super();

		setUnlocalizedName(outputType);
		setHasSubtypes(true);
		setMaxDamage(0);
		this.outputType = outputType;
		setTextureName("libvulpes:" + outputType);
		properties = new HashMap<>();
	}

	public void registerItem(int meta, Material ore) {
		properties.put(meta, ore);
		for(String oreDictName : ore.getOreDictNames())
			OreDictionary.registerOre(outputType + oreDictName, new ItemStack(this, 1, meta));
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List itemList) {

		for(Entry<Integer, Material> entry : properties.entrySet()) {
			itemList.add(new ItemStack(this, 1, entry.getKey()));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int int1) {
		int itemDamage = stack.getItemDamage();
		//Sanity check if anyone somehow gets an invalid damage
		if(!properties.containsKey(itemDamage))
			return 0xFFFFFFFF;
		return properties.get(itemDamage).getColor();
	}


	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {

		//Attempt to get a specific name first, then fall back
		try {
			String translate = "item." + properties.get(itemstack.getItemDamage()).getUnlocalizedName() + "." + outputType + ".name";
			if(StatCollector.canTranslate(translate))
				return StatCollector.translateToLocal(translate);
			else
				return StatCollector.translateToLocal("material." + properties.get(itemstack.getItemDamage()).getUnlocalizedName() + ".name") + " " + StatCollector.translateToLocal("type." + outputType + ".name");
			} catch (NullPointerException e2) {
				return "No name!!!";
			}
	}
}

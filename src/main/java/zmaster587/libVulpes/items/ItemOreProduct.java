package zmaster587.libVulpes.items;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.material.Material;
import zmaster587.libVulpes.api.material.MaterialRegistry;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ItemOreProduct extends Item {

	public HashMap<Integer, Material> properties;
	String outputType;

	public ItemOreProduct(String outputType) {
		super();

		setUnlocalizedName(outputType);
		setHasSubtypes(true);
		setMaxDamage(0);
		this.outputType = outputType;
		properties = new HashMap<Integer, Material>();
	}

	public void registerItem(int meta, Material ore) {
		properties.put(meta, ore);
		for(String oreDictName : ore.getOreDictNames())
			OreDictionary.registerOre(outputType + oreDictName, new ItemStack(this, 1, meta));

		if(FMLCommonHandler.instance().getSide().isClient()) {
			for(Entry<Integer, Material> entry : properties.entrySet()) {
				ModelLoader.setCustomModelResourceLocation(this, entry.getKey(), new ModelResourceLocation(String.format("%s", this.getRegistryName()), "inventory"));
			}
		}
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
	public String getItemStackDisplayName(ItemStack itemstack) {

		//Attempt to get a specific name first, then fall back
		try {
			String translate = "item." + properties.get(itemstack.getItemDamage()).getUnlocalizedName() + "." + outputType + ".name";
			if(I18n.canTranslate(translate))
				return I18n.translateToLocal(translate);
			else
				return I18n.translateToLocal("material." + properties.get(itemstack.getItemDamage()).getUnlocalizedName() + ".name") + " " + I18n.translateToLocal("type." + outputType + ".name");
			} catch (NullPointerException e2) {
				return "No name!!!";
			}
	}
}

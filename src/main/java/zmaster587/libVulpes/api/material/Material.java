package zmaster587.libVulpes.api.material;

import org.jetbrains.annotations.NotNull;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class Material {
	final String unlocalizedName;
    final String tool;
	final String[] oreDictNames;
	final int harvestLevel;
	final int allowedProducts;
	final int color;
	int index;
	//Hack assigning in libvulpes preinit
	public MaterialRegistry registry;

	public Material(String unlocalizedName, String tool, int level, int color, int allowedProducts, boolean hasOre) {
		this(unlocalizedName, tool, level, color, hasOre ? AllowedProducts.getProductByName("ORE").getFlagValue() | allowedProducts : allowedProducts, new String[] {unlocalizedName});
	}

	public Material(String unlocalizedName, String tool, int level, int color, int allowedProducts, MixedMaterial ... products) {
		this(unlocalizedName, tool, level, color, allowedProducts | AllowedProducts.getProductByName("ORE").getFlagValue(), new String[] {unlocalizedName});
	}

	public Material(String unlocalizedName, String tool, int level, int color, int allowedProducts) {
		this(unlocalizedName, tool, level, color, allowedProducts | AllowedProducts.getProductByName("ORE").getFlagValue(), new String[] {unlocalizedName});
	}

	public Material(String unlocalizedName, String tool, int level, int color, int allowedProducts, String[] oreDictNames) {
		this.unlocalizedName = unlocalizedName;
		this.tool = tool;
		this.harvestLevel = level;
		this.oreDictNames = oreDictNames;
		this.allowedProducts = allowedProducts;
		this.color = color;
	}

	/**
	 * @return true if the material is vanilla (Gold, iron)
	 */
	public boolean isVanilla() {
		return this.unlocalizedName.equals("Iron") ||  this.unlocalizedName.equals("Gold");
	}
	
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @param product
	 * @param amount
	 * @return Itemstack representing the product of this material, or null if nonexistant
	 */
	public @NotNull ItemStack getProduct(AllowedProducts product, int amount) {
		/*ItemStack stack = OreDictionary.getOres(product.getName() + this.name()).get(0);
			stack = stack.copy();
			stack.stackSize = amount;
			return stack;*/

		if(isVanilla()) {
			if(this.unlocalizedName.equals("Iron")) {
				if(product.getName().equals("INGOT"))
					return new ItemStack(Items.iron_ingot, amount);
				else if(product.getName().equals("ORE")) {
					return new ItemStack(Blocks.iron_ore, amount);
				}
				else if(product.getName().equals("BLOCK")) {
					return new ItemStack(Blocks.iron_block, amount);
				}
			}
			if(this.unlocalizedName.equals("Gold")) {
				if(product.getName().equals("INGOT"))
					return new ItemStack(Items.gold_ingot, amount);
				else if(product.getName().equals("ORE")) {
					return new ItemStack(Blocks.gold_ore, amount);
				}
				else if(product.getName().equals("BLOCK")) {
					return new ItemStack(Blocks.gold_block, amount);
				}
			}
		}
		
		if(product.isBlock()) {
			return new ItemStack(registry.getBlockForProduct(product, this, index), amount, getMeta());
		}
		
		return new ItemStack(registry.oreProducts[product.ordinal()], amount, getMeta());
	}
	/**
	 * @param product
	 * @return Itemstack of size 1 representing the product of this material, or null if nonexistant
	 */
	public ItemStack getProduct(@NotNull AllowedProducts product) {
		return getProduct(product,1);
	}

	/**
	 * @return 32wide-bitmask corresponding to allowed products by this material
	 */
	public int getAllowedProducts() {
		return allowedProducts;
	}

	/**
	 * @return harvest level required to harvest the ore of this material
	 */
	public int getHarvestLevel() {
		return harvestLevel;
	}

	/**
	 * @return tool required to harvest the ore of this material
	 */
	public String getTool() {
		return tool;
	}

	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	/**
	 * @return list of ore dictionary names for this material.  Example: {iron, pigiron}
	 */
	public String[] getOreDictNames() {
		return oreDictNames;
	}

	/**
	 * Used in rendering of the item and block
	 * @return color of the material 0xRRGGBB
	 */
	public int getColor() {
		return color;
	}

	@Deprecated
	public Block getBlock() {
		return registry.getBlockListForProduct(AllowedProducts.getProductByName("ORE")).get(index/16);
		//return LibVulpesBlocks.blockOre.get(this.ordinal()/16);
	}

	/**
	 * @return the meta value for the itemstack representing a block of this material
	 */
	public int getMeta() {
		return index % 16;
	}

	public int getIndex() {
		return index;
	}

	/**
	 * 
	 * @param str 
	 * @return the material corresponding to the string supplied or null if non existant
	 */
	public static Material valueOfSafe(String str) {
		try {
			return MaterialRegistry.getMaterialFromName(str);//Material.valueOf(str);
		} catch (Exception e) {
			return null;
		}
	}

}

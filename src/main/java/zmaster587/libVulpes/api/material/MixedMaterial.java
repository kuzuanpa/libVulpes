package zmaster587.libVulpes.api.material;

import net.minecraft.item.ItemStack;

//TODO: needs work
public class MixedMaterial {
	
	final ItemStack[] product;
	final Object input;
	final Class process;
	
	public MixedMaterial(Class process, Object input, ItemStack[] product) {
		this.product = product;
		this.process = process;
		this.input = input;
	}
	
	public ItemStack[] getProducts() {
		return product;
	}
	
	public Object getInput() {
		return input;
	}
	
	public Class getMachine() {
		return process;
	}
}

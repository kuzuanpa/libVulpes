package zmaster587.libVulpes.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class SlotOreDict extends Slot {
	
	final String acceptedNames;
	
	public SlotOreDict(IInventory par1iInventory, int par2, int par3, int par4, String allowedNames) {
		super(par1iInventory, par2, par3, par4);
		acceptedNames = allowedNames;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		int stackId = OreDictionary.getOreID(stack);
		if(stackId == -1)
			return false;
		
		return OreDictionary.getOreName(stackId).contains(acceptedNames);
	}
}

package zmaster587.libVulpes.gui;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SlotSingleItem extends Slot {

	private final ItemStack acceptedItem;

	
	public SlotSingleItem(IInventory par1iInventory, int par2, int par3, int par4, Item item) {
		super(par1iInventory, par2, par3, par4);
		acceptedItem = new ItemStack(item);
	}
	
	public SlotSingleItem(IInventory par1iInventory, int par2, int par3, int par4, Block item) {
		super(par1iInventory, par2, par3, par4);
		acceptedItem = new ItemStack(item);
	}
	
	public SlotSingleItem(IInventory par1iInventory, int par2, int par3, int par4, ItemStack item) {
		super(par1iInventory, par2, par3, par4);
		acceptedItem = item;
	}
	
	
	@Override
	public boolean isItemValid(@NotNull ItemStack stack)
	{
		return acceptedItem.isItemEqual(stack);
	}
}

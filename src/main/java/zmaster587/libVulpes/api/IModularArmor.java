package zmaster587.libVulpes.api;

import java.util.List;

import zmaster587.libVulpes.util.IconResource;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IModularArmor {
	
	void addArmorComponent(World world, ItemStack armor, ItemStack componentStack, int slot);
	
	ItemStack removeComponent(World world, ItemStack armor, int index);
	
	List<ItemStack> getComponents(ItemStack armor);

	ItemStack getComponentInSlot(ItemStack stack, int slot);
	
	//Returns a list of externally modifiable fluidtanks
    boolean canBeExternallyModified(ItemStack armor, int slot);
	
	int getNumSlots(ItemStack stack);
	
	IInventory loadModuleInventory(ItemStack stack);
	
	void saveModuleInventory(ItemStack stack, IInventory inv);
	
	//returns true if the stack is valid for the given slot
    boolean isItemValidForSlot(ItemStack stack, int slot);
	
	//Returns an IconResource to be displayed in the slot, if null default slot texture is used
    IconResource getResourceForSlot(int slot);
}

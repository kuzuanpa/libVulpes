package zmaster587.libVulpes.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IJetPack {
	boolean isActive(ItemStack stack, EntityPlayer player);
	
	boolean isEnabled(ItemStack stack);
	
	void setEnabledState(ItemStack stack, boolean state);
	
	void onAccelerate(ItemStack stack, IInventory inv, EntityPlayer player);
	
	void changeMode(ItemStack stack, IInventory modules, EntityPlayer player);
}

package zmaster587.libVulpes.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MultiInventory implements IInventory {

	List<IInventory> inventories;
	
	public MultiInventory(List<IInventory> list) {
		inventories = list;
	}
	
	@Override
	public int getSizeInventory() {
		int i = 0;
		for(IInventory inv : inventories) {
			i += inv.getSizeInventory();
		}
		return i;
	}

	@Override
	public @Nullable ItemStack getStackInSlot(int i) {
		for(IInventory inv : inventories) {
			if(i >= inv.getSizeInventory()) {
				i -= inv.getSizeInventory();
				continue;
			}
			return inv.getStackInSlot(i);
		}
		return null;
	}

	@Override
	public @Nullable ItemStack decrStackSize(int i, int j) {
		
		for(IInventory inv : inventories) {
			if(i >= inv.getSizeInventory()) {
				i -= inv.getSizeInventory();
				continue;
			}
			return inv.decrStackSize(i, j);
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		for(IInventory inv : inventories) {
			if(i >= inv.getSizeInventory()) {
				i -= inv.getSizeInventory();
				continue;
			}
			return inv.getStackInSlotOnClosing(i);
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack j) {
		for(IInventory inv : inventories) {
			if(i >= inv.getSizeInventory()) {
				i -= inv.getSizeInventory();
				continue;
			}
			inv.setInventorySlotContents(i,j);
			return;
		}
	}

	@Override
	public @Nullable String getInventoryName() {
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
		
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return true;
	}

	@Override
	public void openInventory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeInventory() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack j) {
		
		for(IInventory inv : inventories) {
			if(i >= inv.getSizeInventory()) {
				i -= inv.getSizeInventory();
				continue;
			}
			return inv.isItemValidForSlot(i,j);
			
		}
		return false;
	}
}

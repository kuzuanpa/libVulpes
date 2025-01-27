package zmaster587.libVulpes.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import zmaster587.libVulpes.util.EmbeddedInventory;

public abstract class TileInventoriedForgeProducer extends TileEntityForgeProducer implements ISidedInventory {

	protected final EmbeddedInventory inventory;

	protected TileInventoriedForgeProducer(int energy,int invSize) {
		super(energy);
		inventory = new EmbeddedInventory(invSize);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		inventory.writeToNBT(nbt);
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
		int[] i = new int[inventory.getSizeInventory()];

		for(int j = 0; j < i.length; j++) { i[j] = j;}

		return i;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		inventory.readFromNBT(nbt);
	}


	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		return inventory.decrStackSize(slot, amt);
	}


	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
	}


	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}
	

	@Override
	public String getInventoryName() {
		return null;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return player.getDistanceSq(xCoord, yCoord, zCoord) < 64;
	}
	@Override
	public void openInventory() {
		inventory.openInventory();
	}

	@Override
	public void closeInventory() {
		inventory.closeInventory();
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn,
			int direction) {
		return inventory.canInsertItem(index, itemStackIn, direction);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack,
			int direction) {
		return inventory.canExtractItem(index, stack, direction);
	}
	
	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
	}

}

package zmaster587.libVulpes.tile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import zmaster587.libVulpes.util.EmbeddedInventory;

import javax.annotation.Nonnull;

public abstract class TileInventoriedForgeProducer extends TileEntityForgeProducer implements ISidedInventory {

	protected EmbeddedInventory inventory;

	protected TileInventoriedForgeProducer(TileEntityType<?> tileEntityTypeIn, int energy,int invSize) {
		super(tileEntityTypeIn, energy);
		inventory = new EmbeddedInventory(invSize);
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		int[] i = new int[inventory.getSizeInventory()];

		for(int j = 0; j < i.length; j++) { i[j] = j;}

		return i;
	}

	@Override
	public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
		return true;
	}
	
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);

		inventory.write(nbt);
		return nbt;
	}
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		inventory.readFromNBT(nbt);
	}


	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}

	@Override
	@Nonnull
	public ItemStack decrStackSize(int slot, int amt) {
		return inventory.decrStackSize(slot, amt);
	}


	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return player.getDistanceSq(this.pos.getX(), this.pos.getY(), this.pos.getZ()) < 64;
	}
	
	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}
	
	@Override
	public void openInventory(PlayerEntity player) {
		inventory.openInventory(player);
	}

	@Override
	public void closeInventory(PlayerEntity player) {
		inventory.closeInventory(player);
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn,
			Direction direction) {
		return inventory.canInsertItem(index, itemStackIn, direction);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack,
			Direction direction) {
		return inventory.canExtractItem(index, stack, direction);
	}

	@Override
	@Nonnull
	public ItemStack removeStackFromSlot(int index) {
		return inventory.removeStackFromSlot(index);
	}

	@Override
	public void clear() {
		inventory.clear();
	}

}

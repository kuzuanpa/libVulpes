package zmaster587.libVulpes.gui;

import net.minecraft.item.ItemStack;

public interface IlimitedItemSlotEntity {
	boolean isItemValidForLimitedSlot(int slot, ItemStack itemstack);
}

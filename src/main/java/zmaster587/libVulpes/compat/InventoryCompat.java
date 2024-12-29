package zmaster587.libVulpes.compat;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;
import zmaster587.libVulpes.util.ZUtils;

public class InventoryCompat {

	static boolean buildCraft_injectable;
	
	public static void initCompat() {
		try {
			Class.forName("buildcraft.api.transport.IInjectable");
        } catch (ClassNotFoundException e) {
        }
		
		buildCraft_injectable = false;
	}
	
	public static boolean canInjectItems(TileEntity tile) {
		
		if(buildCraft_injectable) {
			return true;
		}
		return tile instanceof IInventory && ZUtils.numEmptySlots((IInventory) tile) > 0;
	}
	
	public static boolean canInjectItems(TileEntity tile, ItemStack item) {
		
		if(buildCraft_injectable) {
			return true;
		}
		return tile instanceof IInventory && (ZUtils.numEmptySlots((IInventory) tile) > 0 || ZUtils.doesInvHaveRoom(item, (IInventory) tile));
	}
	
	public static boolean canInjectItems(IInventory tile, @NotNull ItemStack item) {
		
		return (ZUtils.numEmptySlots(tile) > 0 || ZUtils.doesInvHaveRoom(item, tile));
	}
	
	public static void injectItem(IInventory tile, ItemStack item) {

        ZUtils.mergeInventory(item, tile);
		
	}
}

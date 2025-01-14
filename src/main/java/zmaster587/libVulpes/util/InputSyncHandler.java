package zmaster587.libVulpes.util;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import zmaster587.libVulpes.api.IJetPack;
import zmaster587.libVulpes.api.IModularArmor;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class InputSyncHandler {

	public static final HashMap<EntityPlayer, Boolean> spaceDown = new HashMap<>();
	

	public static boolean isSpaceDown(EntityPlayer player) {
		Boolean bool = spaceDown.get(player);
		
		return bool != null && bool;
	}
	
	//Called on server
	public static void updateKeyPress(EntityPlayer player, int key, boolean state) {
		ItemStack stack;
		switch(key) {
		case 0:
			stack = player.getEquipmentInSlot(3);
			if(stack != null) {
				IJetPack pack;
				if(stack.getItem() instanceof IJetPack) {
					pack = ((IJetPack)stack.getItem());
					pack.setEnabledState(stack, !pack.isEnabled(stack));
				}
				else if(stack.getItem() instanceof IModularArmor) {
					IInventory inv = ((IModularArmor)stack.getItem()).loadModuleInventory(stack);
					
					for(int i = 0; i < inv.getSizeInventory(); i++) {
						if(inv.getStackInSlot(i) != null && inv.getStackInSlot(i).getItem() instanceof IJetPack) {
							pack = ((IJetPack)inv.getStackInSlot(i).getItem());
							pack.setEnabledState(inv.getStackInSlot(i), !pack.isEnabled(inv.getStackInSlot(i)));
						}
					}
					((IModularArmor)stack.getItem()).saveModuleInventory(stack, inv);
					
				}
			}
			break;
			
		case 1:
			stack = player.getEquipmentInSlot(3);
			if(stack != null) {
				IJetPack pack;
				if(stack.getItem() instanceof IJetPack) {
					pack = ((IJetPack)stack.getItem());
					pack.setEnabledState(stack, !pack.isEnabled(stack));
				}
				else if(stack.getItem() instanceof IModularArmor) {
					IInventory inv = ((IModularArmor)stack.getItem()).loadModuleInventory(stack);
					
					for(int i = 0; i < inv.getSizeInventory(); i++) {
						if(inv.getStackInSlot(i) != null && inv.getStackInSlot(i).getItem() instanceof IJetPack) {
							pack = ((IJetPack)inv.getStackInSlot(i).getItem());
							pack.changeMode(inv.getStackInSlot(i), inv, player);
						}
					}
					((IModularArmor)stack.getItem()).saveModuleInventory(stack, inv);
					
				}
			}
			break;
		case 57: //SPACE
			spaceDown.put(player, state);
			break;
			
			default:
				
		}
	}
	
	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent evt) {
		spaceDown.remove(evt.player);
	}

	@SubscribeEvent
	public void onDimChanged(PlayerChangedDimensionEvent evt) {
		spaceDown.remove(evt.player);
	}
}

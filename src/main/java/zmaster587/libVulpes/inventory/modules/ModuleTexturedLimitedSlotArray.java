package zmaster587.libVulpes.inventory.modules;

import org.jetbrains.annotations.NotNull;
import zmaster587.libVulpes.util.IconResource;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class ModuleTexturedLimitedSlotArray extends ModuleLimitedSlotArray {

	IconResource iconResource;

	public ModuleTexturedLimitedSlotArray(int offsetX, int offsetY,
			IInventory container, int startSlot, int endSlot, IconResource resource) {
		super(offsetX, offsetY, container, startSlot, endSlot);
		this.iconResource = resource;
	}

	public void setResource(IconResource iconResource) {
		this.iconResource = iconResource;
	}
	
	@Override
	public void renderBackground(@NotNull GuiContainer gui, int x, int y, int mouseX, int mouseY,
                                 FontRenderer font) {
		
		if(iconResource != null) {
			if(iconResource.getResourceLocation() != null)
				gui.mc.getTextureManager().bindTexture(iconResource.getResourceLocation());
			for(Slot slot : slotList) {
				gui.drawTexturedModalRect(x + slot.xDisplayPosition - 1, y + slot.yDisplayPosition - 1, iconResource.getxLoc(), iconResource.getyLoc(), iconResource.getxSize(), iconResource.getySize());
			}
		}
		else
			super.renderBackground(gui, x, y, mouseX, mouseY, font);
	}

}

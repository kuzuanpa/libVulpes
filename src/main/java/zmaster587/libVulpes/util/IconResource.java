package zmaster587.libVulpes.util;

import net.minecraft.util.ResourceLocation;

public class IconResource {
	final int xLoc;
    final int yLoc;
    final int xSize;
    final int ySize;
	final ResourceLocation resource;
	
	public IconResource(int xLoc, int yLoc, int xSize, int ySize, ResourceLocation resource) {
		this.xLoc = xLoc;
		this.yLoc = yLoc;
		this.xSize = xSize;
		this.ySize = ySize;
		this.resource = resource;
	}
	
	public ResourceLocation getResourceLocation() {
		return resource;
	}
	
	public int getxLoc() {
		return xLoc;
	}
	
	public int getxSize() {
		return xSize;
	}
	
	public int getyLoc() {
		return yLoc;
	}
	
	public int getySize() {
		return ySize;
	}
}

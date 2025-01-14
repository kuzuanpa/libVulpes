package zmaster587.libVulpes.inventory.modules;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ModuleScaledImage extends ModuleBase {
	
	ResourceLocation icon;
	final int sizeX;
    final int sizeY;
	final float minX;
    final float maxX;
    final float minY;
    final float maxY;
	float alpha;
	
	public ModuleScaledImage(int locX, int locY, int sizeX, int sizeY, ResourceLocation icon) {
		super(locX, locY);
		this.icon = icon;
		
		if(sizeX < 0) {
			minX = 1f;
			maxX = 0f;
			sizeX = -sizeX;
		}
		else {
			minX = 0f;
			maxX = 1f;
		}
		
		if(sizeY < 0) {
			minY = 1f;
			maxY = 0f;
			sizeY = -sizeY;
		}
		else {
			minY = 0f;
			maxY = 1f;
		}
		
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		alpha = 1f;
	}

	public ModuleScaledImage(int locX, int locY, int sizeX, int sizeY, float alpha, ResourceLocation icon) {
		this(locX, locY, sizeX, sizeY, icon);
		this.alpha = alpha;
	}
	
	public void setResourceLocation(ResourceLocation location ) {
		this.icon = location;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void renderBackground(GuiContainer gui, int x, int y, int mouseX, int mouseY,
			FontRenderer font) {
		super.renderBackground(gui, x, y, mouseX, mouseY, font);
		
		if(alpha < 1f) {
			GL11.glColor4d(alpha, alpha, alpha, alpha);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
			
		Minecraft.getMinecraft().getTextureManager().bindTexture(icon);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x + this.offsetX, y + this.offsetY + sizeY, 0, minX, maxY);
        tessellator.addVertexWithUV(x + this.offsetX + sizeX, y + this.offsetY + sizeY, 0, maxX, maxY);
        tessellator.addVertexWithUV(x + this.offsetX + sizeX, y + this.offsetY, 0, maxX, minY);
        tessellator.addVertexWithUV(x + this.offsetX, y + this.offsetY, 0, minX, minY);
        tessellator.draw();
        
        if(alpha < 1f) {
			GL11.glColor4d(1f, 1f, 1f, 1f);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
        }
	}
}

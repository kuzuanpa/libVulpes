package zmaster587.libVulpes.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiToggleButtonImage extends GuiButton {
	
	boolean state = false;
	protected final ResourceLocation[] buttonTexture;
	/**
	 * 
	 * @param id id of the button
	 * @param x x position of the button
	 * @param y y position of the button
	 * @param width width of the button in pixels
	 * @param height height of the button in pixels
	 * @param location index 0: enabled, index 1: disabled
	 */
	public GuiToggleButtonImage(int id, int x, int y, int width, int height, ResourceLocation[] location) {
		super(id, x, y, width, height,"");
		buttonTexture = location;
		//TODO: add exception
	}


	public void setState(boolean state) {
		this.state = state;
	}
	
	@Override
	public void drawButton(Minecraft minecraft, int par2, int par3)
	{
		if (this.visible)
		{
			//
			this.field_146123_n = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
			
			//Only display the hover icon if a pressed icon is found and the mouse is hovered
			if(field_146123_n && (buttonTexture.length > 2 && buttonTexture[2] != null ))
				minecraft.getTextureManager().bindTexture(buttonTexture[1]);
			else if(state && ( buttonTexture.length > 2 && buttonTexture[2] != null ))
				minecraft.getTextureManager().bindTexture(buttonTexture[2]);
			else if(!state)
				minecraft.getTextureManager().bindTexture(buttonTexture[0]);
			else // if !state and button[2] == null
				minecraft.getTextureManager().bindTexture(buttonTexture[1]);
			
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);


			//Draw the button...each button should contain 3 images default state, hover, and pressed
			
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(xPosition, yPosition + height, this.zLevel, 0, 1);
			tessellator.addVertexWithUV(xPosition + width, yPosition + height, this.zLevel, 1, 1);
			tessellator.addVertexWithUV(xPosition + width, yPosition, this.zLevel, 1, 0);
			tessellator.addVertexWithUV(xPosition, yPosition, this.zLevel, 0, 0);
			tessellator.draw();
			
			this.mouseDragged(minecraft, par2, par3);
		}
	}
}

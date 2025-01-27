package zmaster587.libVulpes.inventory.modules;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import zmaster587.libVulpes.inventory.GuiModular;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;

public class ModuleTextBox extends ModuleBase {

	@SideOnly(Side.CLIENT)
	GuiTextField textBox;
	final String currentText;
	final IGuiCallback tile;

	public ModuleTextBox(IGuiCallback tile, int offsetX, int offsetY, int sizeX, int sizeY, int maxStrLen) {
		super(offsetX, offsetY);
		this.tile = tile;
		if(FMLCommonHandler.instance().getSide().isClient()) {
			textBox = new GuiTextField(Minecraft.getMinecraft().fontRenderer ,offsetX, offsetY, sizeX, sizeY);
			textBox.setCanLoseFocus(true);
			textBox.setFocused(false);
			textBox.setEnabled(true);
			textBox.setMaxStringLength(maxStrLen);
			textBox.setEnableBackgroundDrawing(true);
		}
		currentText = "";
	}

	public ModuleTextBox(IGuiCallback tile, int offsetX, int offsetY, String initialString) {
		super(offsetX, offsetY);

		this.tile = tile;
		currentText = initialString;
	}


	@Override
	public boolean keyTyped(char chr, int t) {


		if(textBox.isFocused()) {

			if(Keyboard.KEY_ESCAPE == t) {
				textBox.setFocused(false);
			}
			else {
				textBox.textboxKeyTyped(chr, t);

				//Make callback to calling tile
				tile.onModuleUpdated(this);
				return false;
			}
		}

		return true;
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void onMouseClicked(GuiModular gui, int x, int y, int button) {

		//Make sure we can focus the textboxes
        textBox.setFocused(offsetX < x && offsetY < y && offsetX + textBox.width > x && offsetY + textBox.height > y);

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderForeground(int guiOffsetX, int guiOffsetY, int mouseX,
			int mouseY, float zLevel, GuiContainer gui, FontRenderer font) {
		super.renderForeground(guiOffsetX, guiOffsetY, mouseX, mouseY, zLevel, gui,
				font);

		textBox.drawTextBox();
	}

	public void setText(String str) {
		textBox.setText(str);
	}

	public String getText() {
		return textBox.getText();
	}

}

package zmaster587.libVulpes.inventory;

import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import zmaster587.libVulpes.gui.CommonResources;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class GuiModular extends ContainerScreen<ContainerModular> {

	List<ModuleBase> modules;
	String unlocalizedName;
	boolean hasSlots;
	// field_230708_k_ width
	// field_230709_l_ height
	// field_230710_m_ buttonList (tho now technically it's all wigets)
	// field_230712_o_ fontrenderer
	// func_238474_b_ renderModelRect
	// field_230707_j_.zlevel OR field_230662_a_ zlevel or func_230927_p_()
	// drawString func_243246_a w/ shadow

	public GuiModular(ContainerModular container, PlayerInventory invPlayer, ITextComponent title) {
		super(container, invPlayer, title);
		this.modules = container.modules;
		unlocalizedName = title.getString();
		hasSlots = container.includePlayerInv;
	}

	@Override
	public void func_231158_b_(Minecraft p_231158_1_, int p_231158_2_, int p_231158_3_) {
		if(((ContainerModular)container).guid != guiId.MODULARFULLSCREEN)
		{
			super.func_231158_b_(p_231158_1_, p_231158_2_, p_231158_3_);
			return;
		}

		this.field_230706_i_ = p_231158_1_;
		this.field_230707_j_ = p_231158_1_.getItemRenderer();
		this.field_230712_o_ = p_231158_1_.fontRenderer;
		//this.field_230708_k_ = p_231158_2_;
		//this.field_230709_l_ = p_231158_3_;
		java.util.function.Consumer<Widget> remove = (b) -> {
			field_230710_m_.remove(b);
			field_230705_e_.remove(b);
		};
		if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Pre(this, this.field_230710_m_, this::func_230480_a_, remove))) {
			this.field_230710_m_.clear();
			this.field_230705_e_.clear();
			this.func_231035_a_((IGuiEventListener)null);
			this.func_231160_c_();
		}
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post(this, this.field_230710_m_, this::func_230480_a_, remove));
	}

	// InitGui
	@Override
	public void func_231160_c_() {
		if(((ContainerModular)container).guid == guiId.MODULARFULLSCREEN)
		{
			this.xSize = Minecraft.getInstance().getMainWindow().getWidth();
			this.ySize = Minecraft.getInstance().getMainWindow().getHeight();
			this.field_230708_k_ = Minecraft.getInstance().getMainWindow().getWidth();
			this.field_230709_l_ = Minecraft.getInstance().getMainWindow().getHeight();
		}

		super.func_231160_c_();

		int x = (field_230708_k_ - xSize) / 2;
		int y = (field_230709_l_ - ySize) / 2;

		for(ModuleBase module : modules) {
			List<Button> buttonList = module.addButtons(x, y);
			if(!buttonList.isEmpty()) {
				this.field_230710_m_.addAll(buttonList);
			}
		}
	}

	// Action Performed
	@Override
	public void func_231035_a_(@Nullable IGuiEventListener button) {
		super.func_231035_a_(button);

		if(button == null)
			return;
		for(ModuleBase module : modules) {
			module.actionPerform((Button)button);
		}
	}


	//KeyTyped
	@Override
	protected boolean func_195363_d(int keyCode, int scanCode)  {

		boolean superKeypress = true;

		for(ModuleBase module : modules) {
			if(superKeypress)
				superKeypress = module.keyTyped(keyCode, scanCode);
			else
				module.keyTyped(keyCode, scanCode);
		}


		if(superKeypress)
			return super.func_195363_d(keyCode, scanCode);

		return true;
	}


	// Draw foreground
	@Override
	protected void func_230451_b_(MatrixStack matrix, int a, int b)  {
		//super.func_230451_b_(matrix, a, b);

		//renderString
		this.field_230712_o_.func_243246_a(matrix, field_230704_d_, 8, 6, 0xffffff);

		for(ModuleBase module : modules)
			if(module.getVisible())
				module.renderForeground(matrix, (field_230708_k_ - xSize)/2, (field_230709_l_ - ySize) / 2,a - (field_230708_k_ - xSize)/2 ,b - (field_230709_l_ - ySize) / 2, field_230707_j_.zLevel, this, field_230712_o_);

	}

	//onMouseclicked
	@Override
	public boolean func_231048_c_(double x, double y, int button) {

		boolean handled = super.func_231048_c_(x, y, button);

		//Handles buttons (mostly vanilla copy)
		if(button == 0) {

			List<Widget> fullButtonList = new LinkedList<Widget>();
			fullButtonList.addAll(this.field_230710_m_);


			for(IGuiEventListener iguieventlistener : fullButtonList) {
				if (iguieventlistener.func_231047_b_(x, y)) {
					Button button2 = (Button)iguieventlistener;
					button2.func_230988_a_(this.getMinecraft().getSoundHandler());
					this.func_231035_a_(button2);
				}
			}
		}

		for(ModuleBase module : modules)
			module.onMouseClicked(this, x - (field_230708_k_ - xSize) / 2, y - (field_230709_l_ - ySize) / 2, button);

		return handled;
	}

	// mouse click drag
	@Override
	public boolean func_231045_a_(double x, double y, int button, double x2, double y2) {
		boolean handled = super.func_231045_a_(x, y, button, x2, y2);

		for(ModuleBase module : modules)
			module.onMouseClickedAndDragged(x - (field_230708_k_ - xSize) / 2, y - (field_230709_l_ - ySize) / 2, button);

		return handled;
	}

	// Draw background
	@Override
	protected void func_230450_a_(MatrixStack matrix, float f1, int i2, int i3) 
	{
		//Guarantee proper color
		GlStateManager.color4f(1, 1, 1, 1);

		Minecraft.getInstance().getTextureManager().bindTexture(CommonResources.genericBackground);

		int x = (field_230708_k_ - xSize) / 2, y = (field_230709_l_ - ySize) / 2;
		this.func_238474_b_(matrix,x, y, 0, 0, 176, 171);

		if(!hasSlots) {
			this.func_238474_b_(matrix, x + 7, y + 88, 7, 12, 162, 54);
		}

		for(ModuleBase module : modules) {
			if(module.getVisible())
				module.renderBackground(this, matrix, x, y, i2, i3, field_230712_o_);
		}
	}

	public List<Rectangle> getExtraAreasCovered() {
		List<Rectangle> list = new LinkedList<Rectangle>();

		for(ModuleBase module : modules) {
			list.add(new Rectangle((field_230708_k_ - xSize) / 2 + module.offsetX, (field_230709_l_ - ySize) / 2 + module.offsetY, module.getSizeX(), module.getSizeY()));

		}
		return list;
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
	{
		this.func_230446_a_(matrix); // DrawDefaultWorldBackground
		super.func_230430_a_(matrix, mouseX, mouseY, partialTicks); //drawScreen
		this.func_230459_a_(matrix, mouseX, mouseY); // renderHoveredToolTip
	}
}

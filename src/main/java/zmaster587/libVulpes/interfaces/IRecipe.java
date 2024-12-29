package zmaster587.libVulpes.interfaces;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public interface IRecipe {
	List<ItemStack> getOutput();
	
	List<FluidStack> getFluidOutputs();
	
	LinkedList<LinkedList<ItemStack>> getIngredients();
	
	List<FluidStack> getFluidIngredients();
	
	int getTime();
	
	int getPower();

	@Nullable String getOreDictString(int i);
}

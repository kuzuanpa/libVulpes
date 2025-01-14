package zmaster587.libVulpes.recipe;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.interfaces.IRecipe;

import java.util.*;

public class RecipesMachine {
	public static class Recipe implements IRecipe {

		private LinkedList<LinkedList<ItemStack>> input;
		private Map<Integer, String> inputOreDict;
		private LinkedList<FluidStack> fluidInput;
		private LinkedList<ItemStack> output;
		private LinkedList<FluidStack> fluidOutput;
		private int completionTime, power;

		public Recipe() {}

		public Recipe(List<ItemStack> output, LinkedList<LinkedList<ItemStack>> input, int completionTime, int powerReq, Map<Integer, String> oreDict) {
			this.output = new LinkedList<>();
			this.output.addAll(output);

			this.input = new LinkedList<>();
		
			this.input.addAll(input);

			this.completionTime = completionTime;
			this.power = powerReq;

			this.fluidInput = new LinkedList<>();
			this.fluidOutput = new LinkedList<>();
			
			this.inputOreDict = oreDict;
		}

		public Recipe(List<ItemStack> output, LinkedList<LinkedList<ItemStack>> input, List<FluidStack> fluidOutput, List<FluidStack> fluidInput, int completionTime, int powerReq, Map<Integer, String> oreDict) {
			this(output, input, completionTime, powerReq, oreDict);

			this.fluidInput.addAll(fluidInput);
			this.fluidOutput.addAll(fluidOutput);
		}

		public int getCompletionTime() {return completionTime; }


		public int getPowerReq() {
			return power;
		}

		@Override
		public LinkedList<LinkedList<ItemStack>> getIngredients() {
			return input;
		}
		
		public @Nullable String getOreDictString(int slot) { return inputOreDict == null ? null : inputOreDict.get(slot); }

		@Override
		public List<FluidStack> getFluidIngredients() {
			return fluidInput;
		}

		@Override
		public int getTime() {
			return completionTime;
		}

		@Override
		public int getPower() {
			return power;
		}

		@Override
		public List<ItemStack> getOutput() {
			ArrayList<ItemStack> stack = new ArrayList<>();

			for(ItemStack i : output) {
				stack.add(i.copy());
			}

			return stack;
		}

		@Override
		public List<FluidStack> getFluidOutputs() {
			ArrayList<FluidStack> stack = new ArrayList<>();

			for(FluidStack i : fluidOutput) {
				stack.add(i.copy());
			}

			return stack;
		}

		public @NotNull IRecipe getRecipeAsAllItemsOnly() {
			Recipe recipe = new Recipe(output, input, completionTime, power, null);

			for(@NotNull FluidStack stack : getFluidIngredients()) {
				FluidRegistry.getFluidID(stack.getFluid());

				Block block = stack.getFluid().getBlock();

				if(block == null) {
					for(FluidContainerRegistry.FluidContainerData container : FluidContainerRegistry.getRegisteredFluidContainerData()) {
						if(container.fluid.containsFluid(stack)) {
							LinkedList<ItemStack> list = new LinkedList<>();
							list.add(container.filledContainer.copy());
							recipe.input.add(list);
							break;
						}
					}
				}
				else {
					LinkedList<ItemStack> list = new LinkedList<>();
					ItemStack stack2 = new ItemStack(block);
					stack2.stackSize = stack.amount;
					list.add(stack2);
					recipe.input.add(list);
				}
			}

			for(FluidStack stack : getFluidOutputs()) {
				FluidRegistry.getFluidID(stack.getFluid());

				Block block = stack.getFluid().getBlock();

				if(block == null) {
					for(FluidContainerRegistry.FluidContainerData container : FluidContainerRegistry.getRegisteredFluidContainerData()) {
						if(container.fluid.containsFluid(stack)) {
							recipe.output.add(container.filledContainer.copy());
							break;
						}
					}
				}
				else
					recipe.output.add(new ItemStack(block));
			}

			return recipe;
		}

		@Override
		public boolean equals(Object obj) {
			if(obj instanceof Recipe) {
				Recipe otherRecipe = (Recipe)obj;
				if(input.size() != otherRecipe.input.size() || fluidInput.size() != otherRecipe.fluidInput.size())
					return false;

				for(int i = 0; i < input.size(); i++) {
					if(input.get(i).size() != otherRecipe.input.get(i).size())
						return false;
					for(int j = 0; j < input.get(i).size(); j++) {
						if(!ItemStack.areItemStacksEqual(input.get(i).get(j), otherRecipe.input.get(i).get(j)))
							return false;
					}
				}

				for(int i = 0; i < fluidInput.size(); i++) {
					if(fluidInput.get(i).getFluid() != otherRecipe.fluidInput.get(i).getFluid())
						return false;
				}
			}

			return true;
		}
	}

	public final HashMap<Class<Object>, List<IRecipe>> recipeList;

	private static final @NotNull RecipesMachine instance = new RecipesMachine();

	public RecipesMachine() {
		recipeList = new HashMap<>();
	}

	public static @NotNull RecipesMachine getInstance() { return instance; }

	public void clearRecipes(Class clazz) {
		recipeList.get(clazz).clear();
	}
	
	public void addRecipe(Class clazz , Object[] out, int timeRequired, int power, Object ... inputs) {
		List<IRecipe> recipes = getRecipes(clazz);
		if(recipes == null) {
			recipes = new LinkedList<>();
			recipeList.put(clazz,recipes);
		}


		LinkedList<LinkedList<ItemStack>> stack = new LinkedList<>();
		Map<Integer, String> oreDict = new HashMap<>();
		ArrayList<FluidStack> inputFluidStacks = new ArrayList<>();

		try {
			for(int i = 0; i < inputs.length; i++) {
				LinkedList<ItemStack> innerList = new LinkedList<>();
				if(inputs[i] != null) {
					if(inputs[i] instanceof String) {
						oreDict.put(i, ((String)inputs[i]));
						for (ItemStack itemStack : OreDictionary.getOres((String)inputs[i])) {
							innerList.add(itemStack.copy());
						}
					}
					else if(inputs[i] instanceof NumberedOreDictStack) {
						oreDict.put(i, ((NumberedOreDictStack)inputs[i]).ore);
						for (ItemStack itemStack : OreDictionary.getOres(((NumberedOreDictStack)inputs[i]).getOre())) {
							int number  = ((NumberedOreDictStack)inputs[i]).getNumber();
							ItemStack stack2 = itemStack.copy();
							stack2.stackSize = number;
							innerList.add(stack2);
						}
					}
					else if(inputs[i] instanceof FluidStack)
						inputFluidStacks.add((FluidStack) inputs[i]);
					else {

						if(inputs[i] instanceof Item) 
							inputs[i] = new ItemStack((Item)inputs[i]);
						else if(inputs[i] instanceof Block)
							inputs[i] = new ItemStack((Block)inputs[i]);

						innerList.add((ItemStack)inputs[i]);
					}
				}
				if(!innerList.isEmpty()) stack.add(innerList);
			}
			ArrayList<ItemStack> outputItem = new ArrayList<>();
			ArrayList<FluidStack> outputFluidStacks = new ArrayList<>();

			for(Object outputObject : out) {
				if(outputObject instanceof ItemStack)
					outputItem.add((ItemStack)outputObject);
				else
					outputFluidStacks.add((FluidStack)outputObject);
			}

			Recipe recipe;
			if(inputFluidStacks.isEmpty() && outputFluidStacks.isEmpty())
				recipe = new Recipe(outputItem, stack, timeRequired, power,oreDict);
			else
				recipe = new Recipe(outputItem, stack, outputFluidStacks, inputFluidStacks, timeRequired, power,oreDict);

			//if(recipes.contains(recipe)) LibVulpes.logger.info("Overwriting recipe " + recipes.remove(recipe));
			recipes.add(recipe);

		} catch(ClassCastException e) {
			//Custom handling to make sure it logs and can be suppressed by user
			StringBuilder message = new StringBuilder(e.getLocalizedMessage());

			for(StackTraceElement element : e.getStackTrace()) {
				message.append("\n\t").append(element.toString());
			}

			LibVulpes.logger.warning("Cannot add recipe!");
			LibVulpes.logger.warning(message.toString());

		}
	}

	/**
	 * @param clazz Class object of the machine to register the recipe
	 * @param out outout object of the machine, accepts itemStack and fluidStacks
	 * @param timeRequired base running time for the recipe in ticks
	 * @param power power units per tick
	 * @param inputs input objects for the recipe, accepts forge ore dict entries as strings, itemStacks, Items, Blocks, and fluidStacks
	 */
	public void addRecipe(Class clazz , Object out, int timeRequired, int power, Object ... inputs) {
		addRecipe(clazz, new Object[] {out}, timeRequired, power, inputs);
	}
	
	public void addRecipe(Class clazz , List<Object> out, int timeRequired, int power, List<Object> inputs) {
		
		Object[] outputs = new Object[out.size()];
		outputs = out.toArray(outputs);
		
		Object[] inputs2 = new Object[inputs.size()];
		inputs2 = inputs.toArray(inputs2);
		
		addRecipe(clazz, outputs, timeRequired, power, inputs2);
	}

	//Given the class return the list
	public List<IRecipe> getRecipes(Class clazz) {
		return recipeList.get(clazz);
	}
}
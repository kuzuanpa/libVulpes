package zmaster587.libVulpes.util;

import cpw.mods.fml.common.FMLLog;
import eu.usrv.yamcore.auxiliary.ItemDescriptor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.interfaces.IRecipe;
import zmaster587.libVulpes.recipe.NumberedOreDictStack;
import zmaster587.libVulpes.recipe.RecipesMachine;
import zmaster587.libVulpes.tile.TileEntityMachine;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


public class XMLRecipeLoader {

	Document doc;
	String fileName;

	public XMLRecipeLoader() {
		doc = null;
		fileName = "";
	}

	public boolean loadFile(File xmlFile) throws IOException {
		DocumentBuilder docBuilder;
		doc = null;
		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			return false;
		}

		try {
			doc = docBuilder.parse(xmlFile);
		} catch (SAXException e) {
			e.printStackTrace();
			return false;
		}

		fileName = xmlFile.getAbsolutePath();

		return true;
	}

	public void registerRecipes(Class<? extends TileEntityMachine> clazz) {
		Node masterNode = doc.getElementsByTagName("Recipes").item(0);
		int recipeNum = 1;

		if(masterNode.hasAttributes()) {
			Node defaultNode = masterNode.getAttributes().getNamedItem("useDefault");
			if(defaultNode != null && defaultNode.getNodeValue().equals("false"))
				RecipesMachine.getInstance().clearRecipes(clazz);
		}

		masterNode = masterNode.getChildNodes().item(1);
		
		while(masterNode != null) {
			try {
				int time = 200, energy = 0;
				if(masterNode.getNodeType() != doc.ELEMENT_NODE) {
					masterNode = masterNode.getNextSibling();
					continue;
				}
				if(!masterNode.getNodeName().equals("Recipe")) {
					LibVulpes.logger.warning("Expected \"Recipe\" Node in " + fileName + ", found " + masterNode.getNodeName() + "!  Skipping.");
					masterNode = masterNode.getNextSibling();
					continue;
				}

				Node inputNode = null, outputNode = null;
				for(int i = 0; i < masterNode.getChildNodes().getLength(); i++) {
					Node node = masterNode.getChildNodes().item(i);
					if(node.getNodeName().equals("input")) {
						inputNode = node;
					}
					else if(node.getNodeName().equals("output")) {
						outputNode = node;
					}
				}

				if(outputNode == null) {
					masterNode = masterNode.getNextSibling();
					LibVulpes.logger.warning("Missing \"output\" Node in recipe " + recipeNum + " in " + fileName + "!  Skipping.");
					recipeNum++;
					continue;
				}
				if(inputNode == null) {
					masterNode = masterNode.getNextSibling();
					LibVulpes.logger.warning("Missing \"input\" Node in recipe " + recipeNum + " in " + fileName + "!  Skipping.");
					recipeNum++;
					continue;
				}

				List<Object> inputList = new LinkedList<>();

				for(int i = 0; i < inputNode.getChildNodes().getLength(); i++) {
					Node node = inputNode.getChildNodes().item(i);
					if(node.getNodeType() != doc.ELEMENT_NODE) continue;

					Object obj = parseItemType(node, false);
					if(obj == null) {
						LibVulpes.logger.warning("Invalid item \"input\" (" + node.getNodeName() + " " + node.getTextContent() + ") in recipe " + recipeNum + " in " + fileName + "!  Skipping.");
					}
					else
						inputList.add(obj);
				}

				List<Object> outputList = new LinkedList<>();

				for(int i = 0; i < outputNode.getChildNodes().getLength(); i++) {
					Node node = outputNode.getChildNodes().item(i);

					if(node.getNodeType() != doc.ELEMENT_NODE) continue;

					Object obj = parseItemType(node, true);
					if(obj == null) {
						LibVulpes.logger.warning("Invalid item \"output\" (" + node.getNodeName() + " " + node.getTextContent() + ") in recipe " + recipeNum + " in " + fileName + "!  Skipping.");
					}
					else
						outputList.add(obj);
				}

				if(masterNode.hasAttributes()) {
					Node node = masterNode.getAttributes().getNamedItem("timeRequired");
					if(node != null && !node.getNodeValue().isEmpty()) {
						try {
							time = Integer.parseInt(node.getNodeValue());
						} catch (NumberFormatException e) {
							LibVulpes.logger.warning("Recipe " + recipeNum + " has no time value");
						}
					}

					node = masterNode.getAttributes().getNamedItem("power");
					if(node != null && !node.getNodeValue().isEmpty()) {
						try {
							energy = Integer.parseInt(node.getNodeValue());
						} catch (NumberFormatException e) {
							LibVulpes.logger.warning("Recipe " + recipeNum + " has no power value");
						}
					}
				}
				else {
					LibVulpes.logger.info("Recipe " + recipeNum + " has no time or power consumption");
				}

				if(outputList.isEmpty()) 
					LibVulpes.logger.info("Output List emtpy in recipe " + recipeNum);
				else {
					//LibVulpes.logger.info("Sucessfully added recipe to " + clazz.getName() + " for " + inputList.toString() + " -> " + outputList.toString());
					RecipesMachine.getInstance().addRecipe(clazz, outputList, time, energy, inputList);
				}
			} catch (Exception e) {
				LibVulpes.logger.warning("Recipe entry #" + recipeNum + " load failed for '" + clazz.getCanonicalName() + "'!");
			}

			recipeNum++;
			masterNode = masterNode.getNextSibling();
		}
	}

	public Object parseItemType(Node node, boolean output) {
        switch (node.getNodeName()) {
            case "itemStack": {
                String text = node.getTextContent();
                String[] splitStr;

//NBTTag wouldn't contain ; So we use that.
                splitStr = text.split(";");
                String name = "";
                int size = 1;
                int meta = 0;
                String nbtText = "";
                switch (splitStr.length) {
                    case 4:
                        nbtText = splitStr[3].trim();
                    case 3:
                        meta = Integer.parseInt(splitStr[2].trim());
                    case 2:
                        size = Integer.parseInt(splitStr[1].trim());
                    case 1:
                        name = splitStr[0].trim();
                }

                ItemStack stack;
                ItemDescriptor itemDesc = ItemDescriptor.fromString(name + ":" + meta, true);
                if (itemDesc == null) return null;
                if (!nbtText.isEmpty()) stack = itemDesc.getItemStackwNBT(size, nbtText);
                else stack = itemDesc.getItemStack(size);

                return stack;
            }
            case "oreDict": {
                String text = node.getTextContent();
                String[] splitStr;

//NBTTag wouldn't contain ; So we use that.
                splitStr = text.split(";");
                String name = splitStr[0].trim();
                if (OreDictionary.doesOreNameExist(name)) {

                    Object ret = splitStr[0];
                    int number = 1;
                    if (splitStr.length > 1) {

                        try {
                            number = Integer.parseInt(splitStr[1].trim());
                        } catch (NumberFormatException e) {
                        }
                    }

                    if (output) {
                        List<ItemStack> list = OreDictionary.getOres(name);
                        if (!list.isEmpty()) {
                            ItemStack oreDict = OreDictionary.getOres(name).get(0);
                            ret = new ItemStack(oreDict.getItem(), number, oreDict.getItemDamage());
                        }
                    } else {
                        if (!OreDictionary.getOres(splitStr[0]).isEmpty())
                            ret = new NumberedOreDictStack(splitStr[0], number);
                        else FMLLog.log(Level.ERROR, "Null Input Found: " + text);
                    }

                    return ret;
                }
                break;
            }
            case "fluidStack": {

                String text = node.getTextContent();
                String[] splitStr;

//NBTTag wouldn't contain ; So we use that.
                splitStr = text.split(";");

                Fluid fluid;
                if ((fluid = FluidRegistry.getFluid(splitStr[0].trim())) != null) {
                    int amount = 1000;
                    if (splitStr.length > 1) {
                        try {
                            amount = Integer.parseInt(splitStr[1].trim());
                        } catch (NumberFormatException e) {
                        }
                    }

                    return new FluidStack(fluid, amount);
                }
                break;
            }
        }

		return null;
	}


	public static String writeRecipe(@NotNull IRecipe recipe) {
		int index = 0;
		StringBuilder string = new StringBuilder("\t<Recipe timeRequired=\"" + recipe.getTime() + "\" power =\"" + recipe.getPower() + "\">\n" +
                "\t\t<input>\n");
		for(List<ItemStack> stackList : recipe.getIngredients()) {
			if(!stackList.isEmpty()) {
				ItemStack stack = stackList.get(0);
				String oreStr = recipe.getOreDictString(index++);
				if(oreStr != null) {
					string.append("\t\t\t<oreDict>").append(oreStr).append(stack.stackSize > 1 ? (";" + stack.stackSize) : "").append("</oreDict>\n");
				}
				else {
					string.append("\t\t\t<itemStack>").append(stack.getItem().delegate.name()).append(stack.stackSize > 1 ? (";" + stack.stackSize) : (stack.getItemDamage() > 0 ? ";1" : "")).append(stack.getItemDamage() > 0 ? (";" + stack.getItemDamage()) : "").append("</itemStack>\n");
				}
			}
		}
		for(FluidStack stack : recipe.getFluidIngredients()) {
			string.append("\t\t\t<fluidStack>").append(FluidRegistry.getDefaultFluidName(stack.getFluid()).split(":")[1]).append(";").append(stack.amount).append("</fluidStack>\n");
		}
		string.append("\t\t</input>\n\t\t<output>\n");

		for(ItemStack stack : recipe.getOutput()) {
			string.append("\t\t\t<itemStack>").append(stack.getItem().delegate.name()).append(stack.stackSize > 1 ? (";" + stack.stackSize) : (stack.getItemDamage() > 0 ? ";1" : "")).append(stack.getItemDamage() > 0 ? (";" + stack.getItemDamage()) : "").append("</itemStack>\n");
		}

		for(FluidStack stack : recipe.getFluidOutputs()) {
			string.append("\t\t\t<fluidStack>").append(FluidRegistry.getDefaultFluidName(stack.getFluid()).split(":")[1]).append(";").append(stack.amount).append("</fluidStack>\n");
		}

		string.append("\t\t</output>\n\t</Recipe>");

		return string.toString();
	}
}

package zmaster587.libVulpes.items;

import com.mojang.realmsclient.gui.ChatFormatting;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.data.LH;
import gregapi.tileentity.base.TileEntityBase04MultiTileEntities;
import gregapi.tileentity.base.TileEntityBase09FacingSingle;
import gregapi.tileentity.notick.TileEntityBase03MultiTileEntities;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.util.ForgeDirection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockMachine;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.TextureResources;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.INetworkItem;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketItemModifcation;
import zmaster587.libVulpes.tile.TileSchematic;
import zmaster587.libVulpes.tile.multiblock.DummyTileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.TilePlaceholder;
import zmaster587.libVulpes.util.BlockPosition;
import zmaster587.libVulpes.util.Vector3F;
import zmaster587.libVulpes.util.ZUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class ItemProjector extends Item implements IModularInventory, IButtonInventory, INetworkItem {

	final ArrayList<TileMultiBlock> machineList;
	final ArrayList<BlockTile> blockList;
	final ArrayList<String> descriptionList;
	private static final String IDNAME = "machineId";

	public ItemProjector() {
		machineList = new ArrayList<>();
		blockList = new ArrayList<>();
		descriptionList = new ArrayList<>();
	}

	public void registerMachine(TileMultiBlock multiblock, BlockTile mainBlock) {
		machineList.add(multiblock);
		blockList.add(mainBlock);
		HashMap<Object, Integer> map = new HashMap<>();

		Object[][][] structure = multiblock.getStructure();

        for (Object[][] objects : structure) {
            for (Object[] object : objects) {
                for (Object o : object) {
                    if (!map.containsKey(o)) {
                        map.put(o, 1);
                    } else
                        map.put(o, map.get(o) + 1);
                }
            }
        }

		StringBuilder str = new StringBuilder(Item.getItemFromBlock(mainBlock).getItemStackDisplayName(new ItemStack(mainBlock)) + " x1\n");

		for(Entry<Object, Integer> entry : map.entrySet()) {

			List<BlockMeta> blockMeta = multiblock.getAllowableBlocks(entry.getKey());

			if(blockMeta.isEmpty() || Item.getItemFromBlock(blockMeta.get(0).getBlock()) == null )
				continue;
            for (BlockMeta meta : blockMeta) {
                String itemStr = Item.getItemFromBlock(meta.getBlock()).getItemStackDisplayName(new ItemStack(meta.getBlock(), 1, meta.getMeta()));
                if (!itemStr.contains("tile.")) {
                    str.append(itemStr);
                    str.append(" or ");
                }
            }

			if(str.toString().endsWith(" or ")) {
				str = new StringBuilder(str.substring(0, str.length() - 4));
			}
			str.append(" x").append(entry.getValue()).append("\n");
		}

		descriptionList.add(str.toString());
	}
	public void registerDummy(DummyTileMultiBlock multiblock) {
		machineList.add(multiblock);
		HashMap<String, Integer> map = new HashMap<>();

		Object[][][] stru = multiblock.getStructure();

        for (Object[][] objects : stru) {
            for (Object[] object : objects) {
                for (Object value : object) {
                    StringBuilder str = new StringBuilder();
                    List<BlockMeta> blockMeta = multiblock.getAllowableBlocks(value);

                    if (blockMeta.isEmpty() || Item.getItemFromBlock(blockMeta.get(0).getBlock()) == null)
                        continue;
                    for (BlockMeta meta : blockMeta) {
                        String gtTileStr = "";
                        if (meta.GTTile instanceof TileEntityBase03MultiTileEntities)
                            gtTileStr = LH.get(MultiTileEntityRegistry.getRegistry(((TileEntityBase03MultiTileEntities) meta.GTTile).getMultiTileEntityRegistryID()).mNameInternal + "." + ((TileEntityBase03MultiTileEntities) meta.GTTile).getMultiTileEntityID());
                        if (meta.GTTile instanceof TileEntityBase04MultiTileEntities)
                            gtTileStr = LH.get(MultiTileEntityRegistry.getRegistry(((TileEntityBase04MultiTileEntities) meta.GTTile).getMultiTileEntityRegistryID()).mNameInternal + "." + ((TileEntityBase04MultiTileEntities) meta.GTTile).getMultiTileEntityID());
                        String itemStr = !meta.overrideName.isEmpty() ? meta.overrideName //Override Name
                                //GregTech6 compact
                                : !gtTileStr.isEmpty() ? gtTileStr
                                //normal behavior
                                : Item.getItemFromBlock(meta.getBlock()).getItemStackDisplayName(new ItemStack(meta.getBlock(), 1, meta.getMeta()));
                        if (itemStr != null && !itemStr.contains("tile.")) {
                            str.append(itemStr);
                            str.append(" or ");
                        }
                    }

                    if (str.toString().endsWith(" or ")) {
                        str = new StringBuilder(str.substring(0, str.length() - 4));
                    }


                    if (!map.containsKey(str.toString())) {
                        map.put(str.toString(), 1);
                    } else
                        map.put(str.toString(), map.get(str.toString()) + 1);
                }
            }
        }
		StringBuilder builder = new StringBuilder();
		map.forEach((k,v)->builder.append(k).append("x ").append(v).append("\n"));
		descriptionList.add(builder.toString());
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void mouseEvent(@NotNull MouseEvent event) {
		if(Minecraft.getMinecraft().thePlayer.isSneaking() && event.dwheel != 0) {
			ItemStack stack = Minecraft.getMinecraft().thePlayer.getHeldItem();

			if(stack != null && stack.getItem() == this && getMachineId(stack) != -1) {
				if(event.dwheel < 0) {
					setYLevel(stack, getYLevel(stack) + 1);
				}
				else
					setYLevel(stack, getYLevel(stack) - 1);
				event.setCanceled(true);

				PacketHandler.sendToServer(new PacketItemModifcation(this, Minecraft.getMinecraft().thePlayer, (byte)1));
			}
		}
	}

	private void clearStructure(World world, TileMultiBlock tile, ItemStack stack) {

		int id = getMachineId(stack);
		ForgeDirection direction = ForgeDirection.getOrientation(getDirection(stack));

		TileMultiBlock multiblock = machineList.get(id);

		int prevMachineId = getPrevMachineId(stack);
		Object[][][] structure;
		if(prevMachineId >= 0 && prevMachineId < machineList.size()) {
			structure = machineList.get(prevMachineId).getStructure();

			Vector3F<Integer> basepos = getBasePosition(stack);
			if(basepos == null)return;
			for(int y = 0; y < structure.length; y++) {
				for(int z=0 ; z < structure[0].length; z++) {
					for(int x=0; x < structure[0][0].length; x++) {

						int globalX = basepos.x - x*direction.offsetZ + z*direction.offsetX;
						int globalZ = basepos.z + (x* direction.offsetX) + (z*direction.offsetZ);

						if(world.getBlock(globalX, basepos.y + y, globalZ) == LibVulpesBlocks.blockPhantom) {
							world.setBlockToAir(globalX, basepos.y + y, globalZ);
							world.removeTileEntity(globalX, basepos.y + y, globalZ);
						}
					}
				}
			}
		}
	}

	boolean doubleClick = false;
	private void RebuildStructure(@NotNull World world, TileMultiBlock tile, ItemStack stack, int posX, int posY, int posZ, ForgeDirection orientation) {

		int id = getMachineId(stack);

		TileMultiBlock multiblock = machineList.get(id);
		@Nullable Object[][][] structure;

			clearStructure(world, tile, stack);


		structure = multiblock.getStructure();

		if(structure == null)return;

		int y = getYLevel(stack);
		int endNumber, startNumber;

		if(y == -1) {
			startNumber = 0;
			endNumber = structure.length;
		}
		else {
			startNumber = y;
			endNumber = y + 1;
		}
		for(y=startNumber; y < endNumber; y++) {
			for(int z=0 ; z < structure[0].length; z++) {
				for(int x=0; x < structure[0][0].length; x++) {
					List<BlockMeta> block;
					if(structure[y][z][x] instanceof Character && (Character)structure[y][z][x] == 'c') {
						block = new ArrayList<>();
						block.add(new BlockMeta(blockList.get(id), orientation.ordinal()));
					}
					else if(multiblock.getAllowableBlocks(structure[y][z][x]).isEmpty())
						continue;
					else
						block = multiblock.getAllowableBlocks(structure[y][z][x]);

					int globalX = posX - x* orientation.offsetZ + z* orientation.offsetX;
					int globalZ = posZ + (x* orientation.offsetX)  + (z* orientation.offsetZ);
					int globalY = -y + structure.length + posY - 1;
					block.stream().filter(b->b.GTTile instanceof TileEntityBase09FacingSingle).forEach(b->b.setMeta((byte)orientation.ordinal()));

					if((world.isAirBlock(globalX, globalY, globalZ) || world.getBlock(globalX, globalY, globalZ).isReplaceable(world, globalX, globalY, globalZ)) && block.get(0).getBlock().getMaterial() != Material.air) {
						//block = (Block)structure[y][z][x];
						world.setBlock(globalX, globalY, globalZ, LibVulpesBlocks.blockPhantom, block.get(0).getMeta(), 3);
						TileEntity newTile = world.getTileEntity(globalX, globalY, globalZ);

						//TODO: compatibility fixes with the tile entity not reflecting current block
						if(newTile instanceof TilePlaceholder) {
							((TileSchematic)newTile).setReplacedBlock(block);
							((TilePlaceholder)newTile).setReplacedTileEntity(block.get(0).getBlock().createTileEntity(null, 0));
						}
					}
				}
			}
		}
		this.setPrevMachineId(stack, id);
		this.setBasePosition(stack, posX, posY, posZ);
		this.setDirection(stack, orientation.ordinal());
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world,
			EntityPlayer player) {

		if(!world.isRemote && player.isSneaking()) {
			player.openGui(LibVulpes.instance, GuiHandler.guiId.MODULARNOINV.ordinal(), world, -1, -1, 0);
			return super.onItemRightClick(stack, world, player);
		}
		int id = getMachineId(stack);
		if(!player.isSneaking() && id != -1 && world.isRemote) {
			ForgeDirection dir = ForgeDirection.getOrientation(ZUtils.getDirectionFacing(player.rotationYaw - 180));
			TileMultiBlock tile = machineList.get(getMachineId(stack));


			int x = tile.getStructure()[0][0].length;
			int z = tile.getStructure()[0].length;

			int globalX = (-x*dir.offsetZ + z*dir.offsetX)/2;
			int globalZ = ((x* dir.offsetX)  + (z*dir.offsetZ))/2;

			MovingObjectPosition pos = Minecraft.getMinecraft().objectMouseOver;

			TileEntity tile2;
			if((tile2 = world.getTileEntity(pos.blockX, pos.blockY, pos.blockZ)) instanceof TileMultiBlock) {
				for(TileMultiBlock tiles:  machineList) {
					if(tile2.getClass() == tiles.getClass()) {

						setMachineId(stack, machineList.indexOf(tiles));
						Object[][][] structure = tiles.getStructure();

						BlockPosition controller = getControllerOffset(structure);
						dir = BlockMultiblockMachine.getFront(tile2.getBlockMetadata()).getOpposite();
						int yOffset =0;
						if (controller != null){
							controller.y = (short) (structure.length - controller.y);
							yOffset = controller.y;

							globalX = (-controller.x * dir.offsetZ + controller.z * dir.offsetX);
							globalZ = ((controller.x * dir.offsetX) + (controller.z * dir.offsetZ));
						}
						setDirection(stack, dir.ordinal());

						setBasePosition(stack, pos.blockX - globalX, pos.blockY - yOffset  + 1, pos.blockZ - globalZ);
						PacketHandler.sendToServer(new PacketItemModifcation(this, player, (byte)0));
						PacketHandler.sendToServer(new PacketItemModifcation(this, player, (byte)2));
						return super.onItemRightClick(stack, world, player);
					}
				}
			}

			if(pos.sideHit == 0)
				setBasePosition(stack, pos.blockX - globalX, pos.blockY- tile.getStructure().length, pos.blockZ - globalZ);
			else
				setBasePosition(stack, pos.blockX - globalX, pos.blockY+1, pos.blockZ - globalZ);
			setDirection(stack, dir.ordinal());

			PacketHandler.sendToServer(new PacketItemModifcation(this, player, (byte)2));
		}
		return super.onItemRightClick(stack, world, player);
	}

	protected @Nullable BlockPosition getControllerOffset(Object[][][] structure) {
		for(int y = 0; y < structure.length; y++) {
			for(int z = 0; z < structure[0].length; z++) {
				for(int x = 0; x< structure[0][0].length; x++) {
					if(structure[y][z][x] instanceof Character && (Character)structure[y][z][x] == 'c')
						return new BlockPosition(x, y, z);
				}
			}
		}
		return null;
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<>();
		List<ModuleBase> btns = new LinkedList<>();

		ArrayList<TileMultiBlock> displayList = machineList.stream().filter(TileMultiBlock::isVisibleInProjector).collect(Collectors.toCollection(ArrayList::new));

		for(int i=0; i < displayList.size();i++) {
			TileMultiBlock multiblock = displayList.get(i);
			btns.add(new ModuleButton(20+ i%2*100, 4 + (int) (double) (i / 2) *24, i, LibVulpes.proxy.getLocalizedString(multiblock.getMachineName()), this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild));
		}

		ModuleContainerPan panningContainer = new ModuleContainerPan(5, 20, btns, new LinkedList<>(), TextureResources.starryBG, 160, 100, 0, 500);
		modules.add(panningContainer);
		return modules;
	}

	@Override
	public @NotNull String getModularInventoryName() {
		return "item.holoProjector.name";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return entity != null && !entity.isDead && entity.getHeldItem() != null && entity.getHeldItem().getItem() == this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onInventoryButtonPressed(int buttonId) {
		//PacketHandler.sendToServer(new PacketItemModifcation(this, Minecraft.getMinecraft().thePlayer, (byte)buttonId));
		ItemStack stack = Minecraft.getMinecraft().thePlayer.getHeldItem();
		if(stack != null && stack.getItem() == this) {
			setMachineId(stack, buttonId);
			PacketHandler.sendToServer(new PacketItemModifcation(this, Minecraft.getMinecraft().thePlayer, (byte)0));
		}
	}

	private void setMachineId(ItemStack stack, int id) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
		}
		else 
			nbt = new NBTTagCompound();

		nbt.setInteger(IDNAME, id);
		stack.setTagCompound(nbt);
	}

	private int getMachineId(@NotNull ItemStack stack) {
		if(stack.hasTagCompound()) {
			return stack.getTagCompound().getInteger(IDNAME);
		}
		else
			return -1;
	}

	private void setYLevel(ItemStack stack, int level) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
		}
		else 
			nbt = new NBTTagCompound();

		TileMultiBlock machine = machineList.get(getMachineId(stack));

		if(level == -2)
			level = machine.getStructure().length-1;
		else if(level == machine.getStructure().length)
			level = -1;
		nbt.setInteger("yOffset", level);
		stack.setTagCompound(nbt);
	}

	private int getYLevel(ItemStack stack) {
		if(stack.hasTagCompound()) {
			return stack.getTagCompound().getInteger("yOffset");
		}
		else
			return -1;
	}

	private void setPrevMachineId(@NotNull ItemStack stack, int id) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
		}
		else 
			nbt = new NBTTagCompound();

		nbt.setInteger(IDNAME + "Prev", id);
		stack.setTagCompound(nbt);
	}

	private int getPrevMachineId(@NotNull ItemStack stack) {
		if(stack.hasTagCompound()) {
			return stack.getTagCompound().getInteger(IDNAME + "Prev");
		}
		else
			return -1;
	}

	private @Nullable Vector3F<Integer> getBasePosition(ItemStack stack) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
            return new Vector3F<>(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
		}
		else
			return null;
	}

	private void setBasePosition(@NotNull ItemStack stack, int x, int y, int z) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
		}
		else
			nbt = new NBTTagCompound();

		nbt.setInteger("x", x);
		nbt.setInteger("y", y);
		nbt.setInteger("z", z);

		stack.setTagCompound(nbt);
	}

	public int getDirection(ItemStack stack) {
		if(stack.hasTagCompound()) {
			return stack.getTagCompound().getInteger("dir");
		}
		else
			return -1;
	}

	public void setDirection(@NotNull ItemStack stack, int dir) {
		NBTTagCompound nbt;
		if(stack.hasTagCompound()) {
			nbt = stack.getTagCompound();
		}
		else
			nbt = new NBTTagCompound();

		nbt.setInteger("dir", dir);

		stack.setTagCompound(nbt);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player,
			List list, boolean bool) {
		super.addInformation(stack, player, list, bool);

		list.add(LanguageRegistry.instance().getStringLocalization("item.Projector.desc.0"));
		list.add(LanguageRegistry.instance().getStringLocalization("item.Projector.desc.1"));
		list.add(LanguageRegistry.instance().getStringLocalization("item.Projector.desc.2"));

		int id = getMachineId(stack);
		if(id != -1) {
			list.add("");
			list.add(ChatFormatting.GREEN + LibVulpes.proxy.getLocalizedString(machineList.get(id).getMachineName()));
			String str = descriptionList.get(id);

			String[] strList = str.split("\n");

            list.addAll(Arrays.asList(strList));
		}
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id, ItemStack stack) {
		if(id == 0) {
			out.writeInt(getMachineId(stack));
		}
		else if(id == 1)
			out.writeInt(getYLevel(stack));
		else if(id == 2) {

			Vector3F<Integer> pos = getBasePosition(stack);
			out.writeBoolean(pos == null);
			if(pos == null)return;
			out.writeInt(pos.x);
			out.writeInt(pos.y);
			out.writeInt(pos.z);
			out.writeInt(getDirection(stack));
		}
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
									@NotNull NBTTagCompound nbt, ItemStack stack) {
		if(packetId == 0) {
			nbt.setInteger(IDNAME, in.readInt());
		}
		else if(packetId == 1)
			nbt.setInteger("yLevel", in.readInt());
		else if(packetId == 2) {
			if(in.readBoolean())return;
			nbt.setInteger("x", in.readInt());
			nbt.setInteger("y", in.readInt());
			nbt.setInteger("z", in.readInt());
			nbt.setInteger("dir", in.readInt());
		}
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt, ItemStack stack) {
		if(id == 0) {
			int machineId = nbt.getInteger(IDNAME);
			setMachineId(stack, nbt.getInteger(IDNAME));
			TileMultiBlock tile = machineList.get(machineId);
			setYLevel(stack, tile.getStructure().length-1);
		}
		else if(id == 1) {
			setYLevel(stack, nbt.getInteger("yLevel"));
			Vector3F<Integer> vec = getBasePosition(stack);
			if(vec != null) RebuildStructure(player.worldObj, this.machineList.get(getMachineId(stack)), stack, vec.x, vec.y, vec.z, ForgeDirection.getOrientation(getDirection(stack)));
		}
		else if(id == 2) {
			int x = nbt.getInteger("x");
			int y = nbt.getInteger("y");
			int z = nbt.getInteger("z");
			int dir = nbt.getInteger("dir");

			doubleClick = !doubleClick;
			if(doubleClick){
				clearStructure(player.worldObj, this.machineList.get(getMachineId(stack)), stack);
			}
			else if(getMachineId(stack) != -1)
				RebuildStructure(player.worldObj, this.machineList.get(getMachineId(stack)), stack, x, y, z, ForgeDirection.getOrientation(dir));
		}
	}
}
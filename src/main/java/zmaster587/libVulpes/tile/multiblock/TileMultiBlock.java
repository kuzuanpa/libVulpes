package zmaster587.libVulpes.tile.multiblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.block.multiblock.BlockMultiBlockComponentVisible;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockStructure;
import zmaster587.libVulpes.tile.IMultiblock;
import zmaster587.libVulpes.tile.TilePointer;
import zmaster587.libVulpes.tile.TileSchematic;
import zmaster587.libVulpes.tile.multiblock.hatch.TileFluidHatch;
import zmaster587.libVulpes.tile.multiblock.hatch.TileInputHatch;
import zmaster587.libVulpes.tile.multiblock.hatch.TileOutputHatch;
import zmaster587.libVulpes.util.IFluidHandlerInternal;
import zmaster587.libVulpes.util.Vector3F;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class TileMultiBlock extends TileEntity {

	/*CanRender must be seperate from incomplete because some multiblocks must be completed on the client but
	because chunks on the client.  It is also used to determine if the block on the server has ever been complete */
	protected boolean completeStructure, canRender;
	protected byte timeAlive = 0;

	protected LinkedList<IInventory> itemInPorts = new LinkedList<IInventory>();
	protected LinkedList<IInventory> itemOutPorts = new LinkedList<IInventory>();

	protected LinkedList<IFluidHandlerInternal> fluidInPorts = new LinkedList<IFluidHandlerInternal>();
	protected LinkedList<IFluidHandlerInternal> fluidOutPorts = new LinkedList<IFluidHandlerInternal>();

	protected static HashMap<Character, List<BlockMeta>> charMapping = new HashMap<Character, List<BlockMeta>>();

	public TileMultiBlock() {
		completeStructure = false;
		canRender = false;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos,
			IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public static void addMapping(char character, List<BlockMeta> listToAdd) {
		if(charMapping.containsKey(character))
			LibVulpes.logger.warning("Overwritting Multiblock mapping of \"" + character + "\"");
		charMapping.put(character, listToAdd);
	}

	public static List<BlockMeta> getMapping(char character) {
		return charMapping.get(character);
	}

	/**
	 * Note: it may be true on the server but not the client.  This is because the client needs to form the multiblock
	 * so the tile has references to other blocks in its structure for gui display etc
	 * @return true if the structure is complete
	 */
	public boolean isComplete() {
		return completeStructure;
	}

	/**
	 * 
	 * @return true if the block should be rendered as complete
	 */
	@SideOnly(Side.CLIENT)
	public boolean canRender() {
		return canRender;
	}

	/**
	 * @return the unlocalized name of the machine
	 */
	public String getMachineName() {
		return "";
	}

	public void setMachineRunning(boolean running) {
		worldObj.setBlockState(pos, worldObj.getBlockState(pos).withProperty(BlockTile.STATE, running), 2);
	}

	public boolean isUsableByPlayer(EntityPlayer player) {
		return player.getDistance(this.pos.getX(), this.pos.getY(), this.pos.getZ()) < 64;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("canRender", canRender);
		writeNetworkData(nbt);
		return new SPacketUpdateTileEntity(this.pos, 0, nbt);
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("canRender", canRender);
		writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void handleUpdateTag(NBTTagCompound nbt) {

		canRender = nbt.getBoolean("canRender");
		readNetworkData(nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.getNbtCompound();

		canRender = nbt.getBoolean("canRender");
		readNetworkData(nbt);
	}

	public void invalidateComponent(TileEntity tile) {
		setComplete(false);
	}

	/**Called by inventory blocks that are part of the structure
	 ** This includes recipe management etc
	 **/
	public void onInventoryUpdated() {
	}

	/**
	 * @param world world
	 * @param destroyedX x coord of destroyed block
	 * @param destroyedY y coord of destroyed block
	 * @param destroyedZ z coord of destroyed block
	 * @param blockBroken set true if the block is being broken, otherwise some other means is being used to disassemble the machine
	 */
	public void deconstructMultiBlock(World world, BlockPos destroyedPos, boolean blockBroken, IBlockState state) {
		canRender = completeStructure = false;
		if(this.pos.compareTo(destroyedPos) != 0) 
			worldObj.setBlockState(this.pos, world.getBlockState(pos).withProperty(BlockTile.STATE, false));



		//UNDO all the placeholder blocks
		EnumFacing front = getFrontDirection(state);

		Object[][][] structure = getStructure();
		Vector3F<Integer> offset = getControllerOffset(structure);


		//Mostly to make sure IMultiblocks lose their choke-hold on this machines and to revert placeholder blocks
		for(int y = 0; y < structure.length; y++) {
			for(int z = 0; z < structure[0].length; z++) {
				for(int x = 0; x< structure[0][0].length; x++) {

					int globalX = pos.getX() + (x - offset.x)*front.getFrontOffsetZ() - (z-offset.z)*front.getFrontOffsetX();
					int globalY = pos.getY() - y + offset.y;
					int globalZ = pos.getZ() - (x - offset.x)*front.getFrontOffsetX()  - (z-offset.z)*front.getFrontOffsetZ();


					//This block is being broken anyway so don't bother
					if(blockBroken && globalX == destroyedPos.getX() &&
							globalY == destroyedPos.getY() &&
							globalZ == destroyedPos.getZ())
						continue;
					TileEntity tile = worldObj.getTileEntity(new BlockPos(globalX, globalY, globalZ));
					Block block = worldObj.getBlockState(new BlockPos(globalX, globalY, globalZ)).getBlock();

					destroyBlockAt(new BlockPos(globalX, globalY, globalZ), block, tile);

				}
			}
		}

		resetCache();
		this.markDirty();
		world.notifyBlockUpdate(pos, world.getBlockState(pos),  world.getBlockState(pos), 3);
	}

	/**
	 * Called when the multiblock is being deconstructed.  This is called for each block in the structure.
	 * Provided in case of special handling
	 * @param x
	 * @param y
	 * @param z
	 * @param block
	 * @param tile
	 */
	protected void destroyBlockAt(BlockPos destroyedPos, Block block, TileEntity tile) {

		//if it's an instance of multiblock structure call its destroyStructure method
		if(block instanceof BlockMultiblockStructure) {
			((BlockMultiblockStructure)block).destroyStructure(worldObj, destroyedPos, worldObj.getBlockState(destroyedPos));
		}

		//If the the tile is a placeholder then make sure to replace it with its original block and tile
		if(tile instanceof TilePlaceholder && !(tile instanceof TileSchematic)) {
			TilePlaceholder placeholder = (TilePlaceholder)tile;

			//Must set incomplete BEFORE changing the block to prevent stack overflow!
			placeholder.setIncomplete();

			worldObj.setBlockState(destroyedPos, placeholder.getReplacedState());

			//Dont try to set a tile if none existed
			if(placeholder.getReplacedTileEntity() != null) {
				NBTTagCompound nbt = new NBTTagCompound();
				placeholder.getReplacedTileEntity().writeToNBT(nbt);

				worldObj.getTileEntity(destroyedPos).readFromNBT(nbt);
			}
		}
		//Make all pointers incomplete
		else if(tile instanceof IMultiblock) {
			((IMultiblock)tile).setIncomplete();
		}
	}

	public EnumFacing getFrontDirection(IBlockState state) {
		return RotatableBlock.getFront(state);
	}

	public Object[][][] getStructure() {
		return null;
	}

	public boolean attemptCompleteStructure(IBlockState state) {
		//if(!completeStructure)
		canRender = completeStructure = completeStructure(state);
		return completeStructure;
	}

	public void setComplete(boolean complete) {
		completeStructure = complete;
	}

	public List<BlockMeta> getAllowableWildCardBlocks() {
		List<BlockMeta> list =new ArrayList<BlockMeta>();
		return list;
	}

	/**
	 * Called when cached Tiles need to be cleared (batteries/IO/etc)
	 */
	public void resetCache() {
		itemInPorts.clear();
		itemOutPorts.clear();
		fluidInPorts.clear();
		fluidOutPorts.clear();
	}


	/**
	 * Use '*' to allow any kind of Hatch, or energy device or anything returned by getAllowableWildcards
	 * Use 'L' for liquid input hatches
	 * Use 'l' for liquid output hatches
	 * Use 'I' for input hatch
	 * Use 'O' for output hatch
	 * Use 'P' for power input
	 * Use 'p' for power output
	 * Use 'D' for data hatch
	 * Use 'c' for the main Block, there can only be one
	 * Use null for anything
	 * Use a Block to force the user to place that block there
	 * @return true if the structure is valid
	 */
	protected boolean completeStructure(IBlockState state) {

		//Make sure the environment is clean
		resetCache();

		Object[][][] structure = getStructure();

		Vector3F<Integer> offset = getControllerOffset(structure);

		EnumFacing front = getFrontDirection(state);

		//Store tile entities for later processing so we don't risk the check failing halfway through leaving half the multiblock assigned
		LinkedList<TileEntity> tiles = new LinkedList<TileEntity>();

		for(int y = 0; y < structure.length; y++) {
			for(int z = 0; z < structure[0].length; z++) {
				for(int x = 0; x< structure[0][0].length; x++) {

					//Ignore nulls
					if(structure[y][z][x] == null)
						continue;

					int globalX = pos.getX() + (x - offset.x)*front.getFrontOffsetZ() - (z-offset.z)*front.getFrontOffsetX();
					int globalY = pos.getY() - y + offset.y;
					int globalZ = pos.getZ() - (x - offset.x)*front.getFrontOffsetX()  - (z-offset.z)*front.getFrontOffsetZ();
					BlockPos globalPos = new BlockPos(globalX, globalY, globalZ);

					if(!worldObj.getChunkFromBlockCoords(globalPos).isLoaded())
						return false;

					TileEntity tile = worldObj.getTileEntity(globalPos);
					IBlockState blockState = worldObj.getBlockState(globalPos);
					Block block = blockState.getBlock();
					int meta = block.getMetaFromState(blockState);

					if(block == LibVulpesBlocks.blockPhantom)
						return false;

					if(tile != null)
						tiles.add(tile);

					//If the other block already thinks it's complete just assume valid
					if(tile instanceof TilePointer) {
						if(((IMultiblock)tile).hasMaster() && ((IMultiblock)tile).getMasterBlock() != this) {
							//return false;
							
							if(((IMultiblock)tile).getMasterBlock().getPos().equals(getPos())) {
								((IMultiblock)tile).setMasterBlock(getPos());
								continue;
							}
						}
						else if(((IMultiblock)tile).getMasterBlock() == this) 
							continue;
					}
					//Make sure the structure is valid
					if(!(structure[y][z][x] instanceof Character && (Character)structure[y][z][x] == 'c') && !(structure[y][z][x] instanceof Block && (Block)structure[y][z][x] == Blocks.AIR && worldObj.isAirBlock(globalPos)) && !getAllowableBlocks(structure[y][z][x]).contains(new BlockMeta(block,meta))) {

						LibVulpes.proxy.spawnParticle("errorBox", worldObj, globalX, globalY, globalZ, 0, 0, 0);
						return false;
					}
				}
			}
		}

		//Notify all blocks in the structure that it's being build and assimilate them
		for(int y = 0; y < structure.length; y++) {
			for(int z = 0; z < structure[0].length; z++) {
				for(int x = 0; x< structure[0][0].length; x++) {

					int globalX = pos.getX() + (x - offset.x)*front.getFrontOffsetZ() - (z-offset.z)*front.getFrontOffsetX();
					int globalY = pos.getY() - y + offset.y;
					int globalZ = pos.getZ() - (x - offset.x)*front.getFrontOffsetX()  - (z-offset.z)*front.getFrontOffsetZ();
					BlockPos globalPos = new BlockPos(globalX, globalY, globalZ);


					TileEntity tile = worldObj.getTileEntity(globalPos);
					IBlockState blockState = worldObj.getBlockState(globalPos);
					Block block = blockState.getBlock();
					int meta = block.getMetaFromState(blockState);

					if(block instanceof BlockMultiBlockComponentVisible) {
						((BlockMultiBlockComponentVisible)block).hideBlock(worldObj, globalPos, blockState);

						tile = worldObj.getTileEntity(globalPos);

						if(tile instanceof IMultiblock)
							((IMultiblock)tile).setComplete(globalPos);
					}
					else if(block instanceof BlockMultiblockStructure) {
						if(shouldHideBlock(worldObj, globalPos, blockState))
							((BlockMultiblockStructure)block).hideBlock(worldObj, globalPos, blockState);
					}

					if(structure[y][z][x] != null && !block.isAir(blockState, worldObj, globalPos) && !(tile instanceof IMultiblock) && !(tile instanceof TileMultiBlock)) {
						replaceStandardBlock(globalPos, blockState, tile);
					}
				}
			}
		}

		//Now that we know the multiblock is valid we can assign
		for(TileEntity tile : tiles) {
			integrateTile(tile);
		}
		markDirty();
		worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos),  worldObj.getBlockState(pos), 3);
		return true;
	}


	public List<BlockMeta> getAllowableBlocks(Object input) {
		if(input instanceof Character && (Character)input == '*') {
			return getAllowableWildCardBlocks();
		}
		else if(input instanceof Character  && charMapping.containsKey((Character)input)) {
			return charMapping.get((Character)input);
		}
		else if(input instanceof String) { //OreDict entry
			List<ItemStack> stacks = OreDictionary.getOres((String)input);
			List<BlockMeta> list = new LinkedList<BlockMeta>();
			for(ItemStack stack : stacks) {
				//stack.get
				Block block = Block.getBlockFromItem(stack.getItem());
				if(block != null)
					list.add(new BlockMeta(block, stack.getItem().getMetadata(stack.getItemDamage())));
			}
			return list;
		}
		else if(input instanceof Block) {
			List<BlockMeta> list = new ArrayList<BlockMeta>();
			list.add(new BlockMeta((Block) input, BlockMeta.WILDCARD));
			return list;
		}
		else if(input instanceof BlockMeta) {
			List<BlockMeta> list = new ArrayList<BlockMeta>();
			list.add((BlockMeta) input);
			return list;
		}
		else if(input instanceof Block[]) {
			List<BlockMeta> list = new ArrayList<BlockMeta>();
			for(Block b : (Block[])input) list.add(new BlockMeta(b));
			return list;
		}
		else if(input instanceof List) {
			return (List<BlockMeta>)input;
		}
		List<BlockMeta> list = new ArrayList<BlockMeta>();
		return list;
	}

	public boolean shouldHideBlock(World world, BlockPos pos, IBlockState tile) {
		return false;
	}

	/**
	 * Called when replacing a block that is not specifically designed to be compatible with the multiblocks.  Eg iron black
	 * Most multiblocks have a renderer and so these blocks are converted to an invisible pointer
	 * @return
	 */
	protected void replaceStandardBlock(BlockPos newPos, IBlockState state, TileEntity tile) {

		worldObj.setBlockState(newPos, LibVulpesBlocks.blockPlaceHolder.getDefaultState());
		TilePlaceholder newTile = (TilePlaceholder)worldObj.getTileEntity(newPos);

		newTile.setReplacedBlockState(state);
		newTile.setReplacedTileEntity(tile);
		newTile.setMasterBlock(pos);
	}

	/**
	 * This is used so classes extending this one can have their own handling of tiles without overriding the method
	 * @param tile Current tile in multiblock
	 */
	protected void integrateTile(TileEntity tile) {
		if(tile instanceof IMultiblock)
			((IMultiblock) tile).setComplete(pos);

		if(tile instanceof TileInputHatch)
			itemInPorts.add((IInventory) tile);
		else if(tile instanceof TileOutputHatch) 
			itemOutPorts.add((IInventory) tile);
		else if(tile instanceof TileFluidHatch) {
			TileFluidHatch liquidHatch = (TileFluidHatch)tile;
			if(liquidHatch.isOutputOnly())
				fluidOutPorts.add((IFluidHandlerInternal)liquidHatch);
			else
				fluidInPorts.add((IFluidHandlerInternal)liquidHatch);
		}
	}

	protected Vector3F<Integer> getControllerOffset(Object[][][] structure) {
		for(int y = 0; y < structure.length; y++) {
			for(int z = 0; z < structure[0].length; z++) {
				for(int x = 0; x< structure[0][0].length; x++) {
					if(structure[y][z][x] instanceof Character && (Character)structure[y][z][x] == 'c')
						return new Vector3F<Integer>(x, y, z);
				}
			}
		}
		return null;
	}

	protected void writeNetworkData(NBTTagCompound nbt) {

	}

	protected void readNetworkData(NBTTagCompound nbt) {

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		writeNetworkData(nbt);
		nbt.setBoolean("completeStructure", completeStructure);
		nbt.setBoolean("canRender", canRender);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		readNetworkData(nbt);
		completeStructure = nbt.getBoolean("completeStructure");
		canRender = nbt.getBoolean("canRender");
	}
}

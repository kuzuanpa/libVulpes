package zmaster587.libVulpes.tile;

import java.util.ArrayList;
import java.util.List;

import gregapi.tileentity.base.TileEntityBase01Root;
import gregapi.util.ST;
import net.minecraft.tileentity.TileEntity;
import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.tile.multiblock.TilePlaceholder;

public class TileSchematic extends TilePlaceholder {

	private final int ttl = 6000;
	private int timeAlive = 0;
	List<BlockMeta> possibleBlocks;

	public TileSchematic() {
		possibleBlocks = new ArrayList<>();
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	public void setReplacedBlock(List<BlockMeta> block) {
		possibleBlocks = block;
	}

	@Override
	public void setReplacedBlock(Block block) {
		super.setReplacedBlock(block);
		possibleBlocks.clear();
	}

	@Override
	public void setReplacedBlockMeta(int meta) {
		super.setReplacedBlockMeta(meta);
		possibleBlocks.clear();
	}

	@Override
	public Block getReplacedBlock() {
		if(possibleBlocks.isEmpty())
			return super.getReplacedBlock();
		else {
			return possibleBlocks.get((timeAlive/20) % possibleBlocks.size()).getBlock();
		}
	}

	@Override
	public int getReplacedBlockMeta() {
		if(possibleBlocks.isEmpty())
			return super.getReplacedBlockMeta();
		else
			return possibleBlocks.get((timeAlive/20) % possibleBlocks.size()).getMeta();
	}
	public String getReplacedBlockOverrideName() {
		if(possibleBlocks.isEmpty())
			return "";
		else
			return possibleBlocks.get((timeAlive/20) % possibleBlocks.size()).overrideName;
	}
	public TileEntity getReplacedGTTile() {
		if(possibleBlocks.isEmpty())
			return null;
		else
			return possibleBlocks.get((timeAlive/20) % possibleBlocks.size()).GTTile;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if(!worldObj.isRemote) {
			if(timeAlive == ttl) {
				worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			}
		}
		timeAlive++;
	}

	@Override
	public void writeToNBT(@NotNull NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("timeAlive", timeAlive);

		for(int i = 0;  i < possibleBlocks.size();i++) {
			BlockMeta block = possibleBlocks.get(i);
			NBTTagCompound blockTag = new NBTTagCompound();
			blockTag.setInteger("id", Block.getIdFromBlock(block.getBlock()));
			blockTag.setInteger("meta", block.getMeta());
			if(!block.overrideName.equals(""))blockTag.setString("name", block.overrideName);
			if(block.GTTile!=null){
				NBTTagCompound tag = new NBTTagCompound();
				block.GTTile.writeToNBT(tag);
				blockTag.setTag("GTTile", tag);
			}
			nbt.setTag("block."+i,blockTag);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		timeAlive = nbt.getInteger("timeAlive");

		int i=0;
		while(nbt.hasKey("block."+i)) {
			NBTTagCompound blockTag = nbt.getCompoundTag("block."+i);
			BlockMeta block = new BlockMeta(Block.getBlockById(blockTag.getInteger("id")), blockTag.getInteger("meta"));
			if(blockTag.hasKey("name"))block.overrideName=blockTag.getString("name");
			if(blockTag.hasKey("GTTile")){
				block.GTTile= TileEntity.createAndLoadEntity(blockTag.getCompoundTag("GTTile"));
			}
			possibleBlocks.add(i,block);
			i++;
		}
	}

	public void onChunkUnload()
	{
		this.invalidate();
	}
}

package zmaster587.libVulpes.tile.multiblock;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;
import zmaster587.libVulpes.tile.TilePointer;

//Used to store info about the block previously at the location
public class TilePlaceholder extends TilePointer {

	Block replacedBlock;
	int blockMeta;
	TileEntity replacedTile;
	
	public Block getReplacedBlock() {
		return replacedBlock;
	}
	
	public void setReplacedBlock(Block block) {
		replacedBlock = block;
	}
	
	public int getReplacedBlockMeta() {
		return blockMeta;
	}
	
	public void setReplacedBlockMeta(int meta) {
		blockMeta = meta;
	}
	
	public TileEntity getReplacedTileEntity() {
		return replacedTile;
	}
	
	public void setReplacedTileEntity(TileEntity tile) {
		replacedTile = tile;
	}
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();

		writeToNBT(nbt);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, @NotNull S35PacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.func_148857_g();
		readFromNBT(nbt);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		
		nbt.setInteger("ID", Block.getIdFromBlock(replacedBlock));
		nbt.setInteger("damage", blockMeta);
		NBTTagCompound tag = new NBTTagCompound();
		
		if(replacedTile != null) {
			replacedTile.writeToNBT(tag);
			nbt.setTag("tile", tag);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		//TODO: perform sanity check
		replacedBlock = Block.getBlockById(nbt.getInteger("ID"));
		
		blockMeta = nbt.getInteger("damage");
		
		if(nbt.hasKey("tile")) {
			NBTTagCompound tile = nbt.getCompoundTag("tile");
			replacedTile = TileEntity.createAndLoadEntity(tile);
		}
	}
}

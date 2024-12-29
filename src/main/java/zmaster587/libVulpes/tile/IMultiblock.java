package zmaster587.libVulpes.tile;

import net.minecraft.tileentity.TileEntity;

public interface IMultiblock {
	
	boolean hasMaster();
	
	TileEntity getMasterBlock();
	
	void setComplete(int x, int y, int z);
	
	void setIncomplete();
	
	void setMasterBlock(int x, int y, int z);
}

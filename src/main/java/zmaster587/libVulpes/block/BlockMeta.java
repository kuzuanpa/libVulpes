package zmaster587.libVulpes.block;

import gregapi.block.multitileentity.MultiTileEntityBlock;
import gregapi.tileentity.base.TileEntityBase01Root;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

public class BlockMeta {
	Block block;
	int meta;
	public TileEntity GTTile;
	public String overrideName="";
	public static final int WILDCARD = -1;

	public BlockMeta(Block block, int meta) {
		this.block = block;
		this.meta = meta;
	}


	public BlockMeta(Block block,int meta,String overrideName){
			this.block = block;
		this.meta = meta;
		this.overrideName = overrideName;
	}
	public BlockMeta(Block block) {
		this.block = block;
		this.meta = WILDCARD;
	}
	public BlockMeta(MultiTileEntityBlock block, TileEntity tile) {
		this.block = block;
		this.meta = WILDCARD;
		this.GTTile = tile;
	}
	@Override
	public boolean equals(Object obj) {

		if(obj instanceof BlockMeta) {
			return ((BlockMeta)obj).block == block && ( meta == -1 || ((BlockMeta)obj).meta == -1 || ((BlockMeta)obj).meta == meta) && (GTTile==null || GTTile.equals(((BlockMeta) obj).GTTile));
		}
		return super.equals(obj);
	}

	public Block getBlock() {
		return block;
	}

	public int getMeta() {
		if(meta != WILDCARD)
			return meta;
		return 0;
	}
	public void setMeta(int meta) {
		this.meta=meta;
	}
}

package zmaster587.libVulpes.tile.multiblock;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.BlockMeta;

import java.util.List;

public class DummyTileMultiBlock extends TileMultiBlock{
        public static Object[][][] structure;
        public String name;

        public DummyTileMultiBlock(Object[][][] structure, String name){
            this.structure = structure;
            this.name=name;
        }
        @Override
        public List<BlockMeta> getAllowableWildCardBlocks(Character wildCard) {
            List<BlockMeta> list = super.getAllowableWildCardBlocks(wildCard);
            list.add(new BlockMeta(LibVulpesBlocks.blockHatch, 0));
            list.add(new BlockMeta(LibVulpesBlocks.blockHatch, 1));
            return list;
        }


        @Override
        protected void replaceStandardBlock(int xCoord, int yCoord, int zCoord,	Block block, int meta, TileEntity tile) {
            super.replaceStandardBlock(xCoord, yCoord, zCoord, block, meta, tile);
        }

        @Override
        protected void destroyBlockAt(int x, int y, int z, Block block,	TileEntity tile) {
            super.destroyBlockAt(x, y, z, block, tile);
        }

        @Override
        public Object[][][] getStructure() {
            return structure;
        }


        @Override
        public String getMachineName() {
            return name;
        }

}

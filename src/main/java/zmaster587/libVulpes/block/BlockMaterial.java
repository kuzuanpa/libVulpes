package zmaster587.libVulpes.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.jetbrains.annotations.NotNull;
import zmaster587.libVulpes.block.multiblock.BlockMultiblockStructure;
import zmaster587.libVulpes.tile.TileMaterial;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Unknown Unused Class")
public class BlockMaterial extends BlockMultiblockStructure {

	final String side;
    final String poles;
	IIcon sideIcon, polesIcon;
	public final zmaster587.libVulpes.api.material.Material @NotNull [] ores = new zmaster587.libVulpes.api.material.Material[16];
	

	public BlockMaterial(net.minecraft.block.material.@NotNull Material mat, String side, String poles) {
		super(mat);
		this.side = side;
		this.poles = poles;
		this.isBlockContainer = true;
	}

	@Override
	public int colorMultiplier(@NotNull IBlockAccess access, int x, int y, int z) {
		TileEntity tile = access.getTileEntity(x, y, z);

		int color = super.colorMultiplier(access, x, y, z);

		if(tile instanceof TileMaterial) {
			color = ((TileMaterial)tile).getMaterial().getColor();
		}

		return color;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world,
			int x, int y, int z, EntityPlayer player) {

		TileEntity tile = world.getTileEntity(x, y, z);
		int meta = 0;
		if(tile instanceof TileMaterial) {
			meta = ((TileMaterial)tile).getMaterial().getMeta();
		}

		return new ItemStack(this, 1, meta);
	}

	@Override
	public int getRenderColor(int meta) {
		return ores[meta].getColor();
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab,
			List list) {
		for(int i = 0; i < ores.length && ores[i] != null; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public void registerBlockIcons(@NotNull IIconRegister register) {
		sideIcon = register.registerIcon(side);
		polesIcon = register.registerIcon(poles);
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return (ForgeDirection.getOrientation(side) == ForgeDirection.UP || ForgeDirection.getOrientation(side) == ForgeDirection.DOWN) ? polesIcon : sideIcon;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z,
			int metadata, int fortune) {


        return new ArrayList<>();
	}

	@Override
	public void onBlockHarvested(World world, int x,
			int y, int z, int p_149681_5_,
			EntityPlayer player) {
		super.onBlockHarvested(world, x, y, z,
				p_149681_5_, player);

		if(!player.capabilities.isCreativeMode) {
			TileEntity tile = world.getTileEntity(x, y, z);
			if(tile instanceof TileMaterial) {
				world.spawnEntityInWorld(new EntityItem(world, x + .5f, y + .5f, z+ .5f, new ItemStack(this, 1, ((TileMaterial)tile).getMaterial().getIndex())));
			}
		}
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileMaterial();
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}
}

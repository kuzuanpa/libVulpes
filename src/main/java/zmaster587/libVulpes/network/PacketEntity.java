package zmaster587.libVulpes.network;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zmaster587.libVulpes.interfaces.INetworkEntity;

import java.io.IOException;

public class PacketEntity extends BasePacket {

	INetworkEntity entity;

	@Nullable
    NBTTagCompound nbt;

	byte packetId;

	public PacketEntity() {
		nbt = new NBTTagCompound();
	}

    public PacketEntity(INetworkEntity machine, byte packetId) {
		this();
		this.entity = machine;
		this.packetId = packetId;
	}


	public PacketEntity(INetworkEntity entity, byte packetId, @Nullable NBTTagCompound nbt) {
		this(entity, packetId);
		this.nbt = nbt;
	}

	@Override
	public void write(ByteBuf out) {
		PacketBuffer buffer = new PacketBuffer(out);

		write(buffer);
	}

	private void write(PacketBuffer out) {
		out.writeInt(((Entity)entity).worldObj.provider.dimensionId);
		out.writeInt(((Entity)entity).getEntityId());
		out.writeByte(packetId);

		out.writeBoolean(nbt != null && nbt.hasNoTags());

		if(nbt != null && !nbt.hasNoTags()) {
			try {
				out.writeNBTTagCompoundToBuffer(nbt);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		entity.writeDataToNetwork(out, packetId);
	}

	@Override
	public void read(ByteBuf in) {
		PacketBuffer buffer = new PacketBuffer(in);
		read(buffer, true);
	}

	public void read(@NotNull PacketBuffer in, boolean server) {
		//DEBUG:
		World world;
		world = DimensionManager.getWorld(in.readInt());

		int entityId = in.readInt();
		packetId = in.readByte();

		Entity ent = world.getEntityByID(entityId);

		if(in.readBoolean()) {
			NBTTagCompound nbt = null;

			try {
				nbt = in.readNBTTagCompoundFromBuffer();
			} catch (IOException e) {
				e.printStackTrace();
			}

			this.nbt = nbt;
		}

		if(ent instanceof INetworkEntity) {
			entity = (INetworkEntity)ent;
			entity.readDataFromNetwork(in, packetId, nbt);
		}
		else {
			//Error
		}
	}

	public void execute(EntityPlayer player, Side side) {
		if(entity != null)
			entity.useNetworkData(player, side, packetId, nbt);
	}

	@Override
	public void executeServer(EntityPlayerMP player) {
		execute(player, Side.SERVER);
	}

	@Override
	public void executeClient(EntityPlayer player) {
		execute(player, Side.CLIENT);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void readClient(ByteBuf in) {
		PacketBuffer buffer = new PacketBuffer(in);

		//DEBUG:
		World world;

		buffer.readInt();
		world = Minecraft.getMinecraft().theWorld;


		int entityId = buffer.readInt();
		packetId = buffer.readByte();

		Entity ent = world.getEntityByID(entityId);

		if(buffer.readBoolean()) {
			NBTTagCompound nbt = null;

			try {
				nbt = buffer.readNBTTagCompoundFromBuffer();
			} catch (IOException e) {
				e.printStackTrace();
			}

			this.nbt = nbt;
		}

		if(ent instanceof INetworkEntity) {
			entity = (INetworkEntity)ent;
			entity.readDataFromNetwork(buffer, packetId, nbt);
		}
		else {
			//Error
		}
	}

}

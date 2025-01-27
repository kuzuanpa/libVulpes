package zmaster587.libVulpes.network;

import org.jetbrains.annotations.NotNull;
import zmaster587.libVulpes.util.InputSyncHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketChangeKeyState extends BasePacket {

	int key;
	boolean state;
	
	public PacketChangeKeyState(int key, boolean state) {
		this.key = key;
		this.state = state;
	}
	
	public PacketChangeKeyState() {}

    @Override
	public void write(ByteBuf out) {
		out.writeInt(key);
		out.writeBoolean(state);
	}

	@Override
	public void readClient(ByteBuf in) {
		
	}

	@Override
	public void read(ByteBuf in) {
		key = in.readInt();
		state = in.readBoolean();
	}

	@Override
	public void executeClient(EntityPlayer thePlayer) {
		
	}

	@Override
	public void executeServer(@NotNull EntityPlayerMP player) {
		InputSyncHandler.updateKeyPress(player, key, state);
	}

}

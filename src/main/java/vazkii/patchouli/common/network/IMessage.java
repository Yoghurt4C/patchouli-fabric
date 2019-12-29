package vazkii.patchouli.common.network;

import net.fabricmc.fabric.api.network.PacketContext;

import java.io.Serializable;

//import net.minecraftforge.fml.network.NetworkEvent;

public interface IMessage extends Serializable {

	public boolean receive(PacketContext context);
	
}

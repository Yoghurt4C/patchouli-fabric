package vazkii.patchouli.common.network.message;

import net.fabricmc.fabric.api.network.PacketContext;
import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.common.network.IMessage;

public class MessageSyncAdvancements implements IMessage {

	private static final long serialVersionUID = 6052845538836764260L;
	
	public String[] done;
	public boolean showToast;
	
	public MessageSyncAdvancements() { }
	
	public MessageSyncAdvancements(String[] done, boolean showToast) { 
		this.done = done;
		this.showToast = showToast;
	}
	
	@Override
	public boolean receive(PacketContext context) {
		context.getTaskQueue().execute(() -> {
			ClientAdvancements.setDoneAdvancements(done, showToast, false);
		});
		
		return true;
	}
	
}

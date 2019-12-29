package vazkii.patchouli.client.book;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import vazkii.patchouli.common.util.ItemStackUtil;

public class BookIcon {

	private final IconType type;
	private final ItemStack stack;
	private final Identifier res;
	
	public BookIcon(String str) {
		if(str.endsWith(".png")) {
			type = IconType.RESOURCE;
			stack = null;
			res = new Identifier(str);
		} else {
			type = IconType.STACK;
			stack = ItemStackUtil.loadStackFromString(str);
			res = null;
		}
	}
	
	public BookIcon(ItemStack stack) {
		type = IconType.STACK;
		this.stack = stack;
		res = null;
	}
	
	public BookIcon(Identifier res) {
		type = IconType.RESOURCE;
		stack = null;
		this.res = res;
	}
	
	public void render(int x, int y) {
		MinecraftClient mc = MinecraftClient.getInstance();
		switch(type) {
		case STACK:
			RenderHelper.enableGUIStandardItemLighting();
			mc.getItemRenderer().renderGuiItem(stack, x, y);
			break;
			
		case RESOURCE:
			GlStateManager.color4f(1F, 1F, 1F, 1F);
			mc.getTextureManager().bindTexture(res);
			DrawableHelper.blit(x, y, 0, 0, 16, 16, 16, 16, 16, 16);
			break;
		}
	}
	
	private enum IconType {
		STACK, RESOURCE
	}
	
}

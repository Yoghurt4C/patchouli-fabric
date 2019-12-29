package vazkii.patchouli.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import vazkii.patchouli.client.book.BookContents;
import vazkii.patchouli.client.book.EntryDisplayState;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.book.Book;

public class GuiButtonInventoryBook extends Button {

	private Book book;
	
	public GuiButtonInventoryBook(Book book, int x, int y) {
		super(x, y, 20, 20, "", (b) -> {
			BookContents contents = book.contents;
			contents.openLexiconGui(contents.getCurrentGui(), false);
		});
		this.book = book;
	}
	
	@Override
	public void renderButton(int mouseX, int mouseY, float pticks) {
		Minecraft mc = Minecraft.getInstance();
		Minecraft.getInstance().textureManager.bindTexture(new Identifier(Patchouli.MOD_ID, "textures/gui/inventory_button.png"));
		GlStateManager.color3f(1F, 1F, 1F);
		
		boolean hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
		AbstractGui.blit(x, y, (hovered ? 20 : 0), 0, width, height, 64, 64);
		
		ItemStack stack = book.getBookItem();
		RenderHelper.enableGUIStandardItemLighting();
		mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, x + 2, y + 2);
		
		EntryDisplayState readState = book.contents.getReadState();
		if(readState.hasIcon && readState.showInInventory)
			GuiBook.drawMarking(book, x, y, 0, readState);
	}
	
	public Book getBook() {
		return book;
	}
	
}

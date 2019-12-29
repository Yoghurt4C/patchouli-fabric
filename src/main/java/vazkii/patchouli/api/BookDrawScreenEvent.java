package vazkii.patchouli.api;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is fired on the {@link MinecraftForge#EVENT_BUS} after any
 * book gui {@link #gui} draws the content of a book {@link #book} with
 * the book gui scale still applied to GL. This is useful if additional
 * custom compoenents should be drawn independently of what page a book
 * is currently on.
 */
public class BookDrawScreenEvent
		extends BookEvent {
	
	public final Screen gui;
	public final int mouseX;
	public final int mouseY;
	public final float partialTicks;
	
	public BookDrawScreenEvent(Screen gui, Identifier book, int mouseX, int mouseY, float partialTicks) {
		super(book);
		this.gui = gui;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.partialTicks = partialTicks;
	}
}

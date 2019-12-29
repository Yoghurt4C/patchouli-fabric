package vazkii.patchouli.api;

import net.minecraft.util.Identifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is fired on the {@link MinecraftForge#EVENT_BUS} after a
 * book is reloaded.
 */
public class BookContentsReloadEvent
    extends Event {

  public final Identifier book;

  public BookContentsReloadEvent(Identifier book) {

    this.book = book;
  }
}

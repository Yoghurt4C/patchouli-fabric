package vazkii.patchouli.api;

import net.minecraft.util.Identifier;
import net.minecraftforge.eventbus.api.Event;

public abstract class BookEvent
    extends Event {

  public final Identifier book;

  public BookEvent(Identifier book) {

    this.book = book;
  }
}

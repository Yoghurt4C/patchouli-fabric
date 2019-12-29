package vazkii.patchouli.client.book.gui.button;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonBookHistory extends GuiButtonBook {

	public GuiButtonBookHistory(GuiBook parent, int x, int y, Button.IPressable onPress) {
		super(parent, x, y, 330, 31, 11, 11, onPress,
				I18n.format("patchouli.gui.lexicon.button.history"));
	}

}

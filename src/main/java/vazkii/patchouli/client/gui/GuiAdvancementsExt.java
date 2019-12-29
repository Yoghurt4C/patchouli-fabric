package vazkii.patchouli.client.gui;

import net.minecraft.advancement.Advancement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.util.Identifier;
import vazkii.patchouli.client.base.ClientAdvancements;

public class GuiAdvancementsExt extends AdvancementsScreen {

	Screen parent;
	
	public GuiAdvancementsExt(ClientAdvancementManager manager, Screen parent, String tab) {
		super(manager);
		this.parent = parent;

		Advancement start = manager.getManager().get(new Identifier(tab));
		if(start != null && ClientAdvancements.hasDone(start.getId().toString()))
			manager.selectTab(start, false);
	}

	@Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if(key == minecraft.options.keyAdvancements.getDefaultKeyCode().getKeyCode() || scanCode == 1) {
        	minecraft.openScreen(parent);
            return true;
        }
        else return super.keyPressed(key, scanCode, modifiers);
    }
	
}

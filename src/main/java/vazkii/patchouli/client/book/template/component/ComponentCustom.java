package vazkii.patchouli.client.book.template.component;

import com.google.gson.annotations.SerializedName;

import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.template.TemplateComponent;
import vazkii.patchouli.client.book.template.VariableAssigner;
import vazkii.patchouli.common.util.SerializationUtil;

public class ComponentCustom extends TemplateComponent {

	@SerializedName("class")
	String clazz;

	transient ICustomComponent callbacks;

	@Override
	public void build(BookPage page, BookEntry entry, int pageNum) {
		createCallbackObj();
		
		callbacks.build(x, y, pageNum);
	}
	
	@Override
	public void render(BookPage page, int mouseX, int mouseY, float pticks) {
		callbacks.render(page.parent, pticks, mouseX, mouseY);
	}
	
	@Override
	public void onDisplayed(BookPage page, GuiBookEntry parent, int left, int top) {
		callbacks.onDisplayed(parent);
	}
	
	@Override
	public boolean mouseClicked(BookPage page, double mouseX, double mouseY, int mouseButton) {
		return callbacks.mouseClicked(page.parent, mouseX, mouseY, mouseButton);
	}

	private void createCallbackObj() {
		try {
			Class<?> classObj = Class.forName(clazz);
			if(classObj != null) {
				callbacks = (ICustomComponent) SerializationUtil.RAW_GSON.fromJson(sourceObject, classObj);
				VariableAssigner.assignVariableHolders(callbacks, variables, processor, encapsulation);
			}
		} catch(Exception e) {
			throw new RuntimeException("Failed to create custom component " + clazz, e);
		}
	}

}

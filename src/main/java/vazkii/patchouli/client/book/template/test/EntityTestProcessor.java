package vazkii.patchouli.client.book.template.test;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;

public class EntityTestProcessor implements IComponentProcessor {

	private String entityName;

	@Override
	public void setup(IVariableProvider<String> variables) {
		String entityType = variables.get("entity");
		if (entityType.contains("{"))
			entityType = entityType.substring(0, entityType.indexOf("{"));


		Identifier key = new Identifier(entityType);
		if (Registry.ENTITY_TYPE.containsId(key)) {
			entityName = Registry.ENTITY_TYPE.get(key).getName().getString();
		}
	}

	@Override
	public String process(String key) {
		if (key.equals("name"))
			return entityName;

		return null;
	}


}

package cz.neumimto.rpg.configuration.adapters;

import com.google.common.reflect.TypeToken;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.skills.tree.SkillTree;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

/**
 * Created by NeumimTo on 6.1.2019.
 */
public class SkillTreeLookupAdapter implements TypeSerializer<SkillTree> {

	@Override
	public SkillTree deserialize(TypeToken<?> typeToken, ConfigurationNode configurationNode) throws ObjectMappingException {
		String string = configurationNode.getString();
		SkillTree skillTree = NtRpgPlugin.GlobalScope.skillService.getSkillTrees().get(string);
		if (skillTree == null) {
			throw new ObjectMappingException("Unknown skilltree " + string);
		}
		return skillTree;
	}

	@Override
	public void serialize(TypeToken<?> typeToken, SkillTree skillTree, ConfigurationNode configurationNode) {
		configurationNode.setValue(skillTree.getId());
	}
}

package cz.neumimto.rpg.sponge.configuration.adapters;

import com.google.common.reflect.TypeToken;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
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
            Log.info("Unknown skilltree " + string);
            skillTree = SkillTree.Default;
        }
        return skillTree;
    }

    @Override
    public void serialize(TypeToken<?> typeToken, SkillTree skillTree, ConfigurationNode configurationNode) {
        configurationNode.setValue(skillTree.getId());
    }
}

package cz.neumimto.rpg.api.configuration.adapters;

import com.electronwill.nightconfig.core.conversion.Converter;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.skills.tree.SkillTree;

/**
 * Created by NeumimTo on 6.1.2019.
 */
public class SkillTreeLookupAdapter implements Converter<SkillTree, String> {

    @Override
    public SkillTree convertToField(String skillTreeId) {
        SkillTree skillTree = Rpg.get().getSkillService().getSkillTrees().get(skillTreeId);
        if (skillTree == null) {
            Log.info("Unknown skilltree " + skillTreeId);
            skillTree = SkillTree.Default;
        }
        return skillTree;

    }

    @Override
    public String convertFromField(SkillTree value) {
        return value.getId();
    }
}

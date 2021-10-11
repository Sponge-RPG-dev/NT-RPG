package cz.neumimto.rpg.common.configuration.adapters;

import com.electronwill.nightconfig.core.conversion.Converter;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.common.skills.tree.SkillTree;

import java.util.Map;

/**
 * Created by NeumimTo on 6.1.2019.
 */
public class SkillTreeLookupAdapter implements Converter<SkillTree, String> {

    @Override
    public SkillTree convertToField(String skillTreeId) {
        if (skillTreeId == null) {
            return SkillTree.Default;
        }
        Map<String, SkillTree> skillTrees = Rpg.get().getSkillService().getSkillTrees();
        for (Map.Entry<String, SkillTree> e : skillTrees.entrySet()) {
            if (skillTreeId.equalsIgnoreCase(e.getKey())) {
                return e.getValue();
            }
        }

        Log.info("Unknown skilltree " + skillTreeId);
        return SkillTree.Default;
    }

    @Override
    public String convertFromField(SkillTree value) {
        return value.getId();
    }
}

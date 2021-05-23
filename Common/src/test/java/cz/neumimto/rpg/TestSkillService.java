package cz.neumimto.rpg;

import cz.neumimto.rpg.api.gui.ISkillTreeInterfaceModel;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.common.skills.AbstractSkillService;
import cz.neumimto.rpg.junit.TestDictionary;

import javax.inject.Singleton;

@Singleton
public class TestSkillService extends AbstractSkillService {

    @Override
    public ISkillTreeInterfaceModel getGuiModelByCharacter(char c) {
        return null;
    }

    @Override
    public ISkill getSkillById(String id) {
        if (id.startsWith("ntrpg")) {
            return TestDictionary.DUMMY_SKILL;
        }
        return super.getSkillById(id);
    }
}

package cz.neumimto.rpg;

import cz.neumimto.nts.NTScript;
import cz.neumimto.rpg.common.gui.ISkillTreeInterfaceModel;
import cz.neumimto.rpg.common.scripting.SkillScriptHandlers;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.SkillService;
import cz.neumimto.rpg.junit.TestDictionary;

import javax.inject.Singleton;

@Singleton
public class TestSkillService extends SkillService {

    @Override
    public NTScript getNtScriptCompilerFor(Class<? extends SkillScriptHandlers> c) {
        return null;
    }

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

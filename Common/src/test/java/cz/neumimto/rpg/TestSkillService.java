package cz.neumimto.rpg;

import cz.neumimto.rpg.api.gui.ISkillTreeInterfaceModel;
import cz.neumimto.rpg.common.skills.AbstractSkillService;

import javax.inject.Singleton;

@Singleton
public class TestSkillService extends AbstractSkillService {


    @Override
    public ISkillTreeInterfaceModel getGuiModelByCharacter(char c) {
        return null;
    }
}

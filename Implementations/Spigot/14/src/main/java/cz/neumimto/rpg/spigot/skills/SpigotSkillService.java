package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.rpg.api.gui.ISkillTreeInterfaceModel;
import cz.neumimto.rpg.common.skills.AbstractSkillService;

import javax.inject.Singleton;

@Singleton
public class SpigotSkillService extends AbstractSkillService {

    @Override
    public ISkillTreeInterfaceModel getGuiModelByCharacter(char c) {
        return null;
    }
}

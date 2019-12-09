package cz.neumimto.rpg.spigot.gui;

import cz.neumimto.rpg.api.gui.SkillTreeViewModel;

public class SpigotSkillTreeViewModel extends SkillTreeViewModel {

    static {
        factory = SpigotSkillTreeViewModel::new;
    }

    @Override
    public void reset() {

    }
}

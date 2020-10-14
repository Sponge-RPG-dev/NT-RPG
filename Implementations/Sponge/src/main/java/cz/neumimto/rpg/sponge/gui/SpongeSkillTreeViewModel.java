package cz.neumimto.rpg.sponge.gui;

import cz.neumimto.rpg.api.gui.SkillTreeViewModel;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.utils.Pair;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by NeumimTo on 22.10.2017.
 */
public class SpongeSkillTreeViewModel extends SkillTreeViewModel {

    static {
        factory = SpongeSkillTreeViewModel::new;
    }

    private Map<String, Pair<List<Text>, TextColor>> buttonCache;

    public SpongeSkillTreeViewModel() {
        this.buttonCache = new HashMap<>();
    }

    @Override
    public void reset() {
        buttonCache.clear();
    }

    public Pair<List<Text>, TextColor> getFromCache(ISkill iSkill) {
        return buttonCache.get(iSkill.getId());
    }

    public void addToCache(ISkill iSkill, List<Text> lore, TextColor textColor) {
        buttonCache.put(iSkill.getId(), new Pair<>(lore, textColor));
    }

}

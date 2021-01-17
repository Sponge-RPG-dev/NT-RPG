package cz.neumimto.rpg.api.entity.players;

import cz.neumimto.rpg.api.configuration.AttributeConfig;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.types.PassiveSkill;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SkillTreeChangeObserver {

    private IActiveCharacter character;

    private Map<String, Set<String>> candidates = new HashMap<>();

    public SkillTreeChangeObserver(IActiveCharacter character) {
        this.character = character;
    }

    public void addCandidate(AttributeConfig attributeConfig, PlayerSkillContext candidate) {
        addCandidate(attributeConfig.getId(), candidate);
    }

    public void addCandidate(ISkill skill, PlayerSkillContext candidate) {
        addCandidate(skill.getId(), candidate);
    }

    public void addCandidate(String action, PlayerSkillContext reloadCandidate) {
        Set<String> playerSkillContexts = candidates.computeIfAbsent(action, s -> new HashSet<>());
        playerSkillContexts.add(reloadCandidate.getSkillData().getSkillId());
    }

    public void processChange(AttributeConfig changed) {
        processChange(changed.getId());
    }

    public void processChange(ISkill changed) {
        processChange(changed.getId());
    }

    public void processChange(String action) {
        Set<String> playerSkillContexts = candidates.get(action);
        if (playerSkillContexts != null) {
            for (String skillId : playerSkillContexts) {
                PlayerSkillContext psc = character.getSkill(skillId);
                if (psc != null) {
                    psc.invalidateSkillSettingsCache();
                    if (psc.getSkill() instanceof PassiveSkill) {
                        psc.getSkill().skillUpgrade(character, psc.getLevel(), psc);
                    }
                }
            }
        }
    }
}

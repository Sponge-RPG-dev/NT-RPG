package cz.neumimto.rpg.skills.reagents;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.reagents.Cooldown;

import javax.inject.Singleton;

@Singleton
public class CooldownTest extends Cooldown {

    @Override
    public void notifyFailure(IActiveCharacter character, PlayerSkillContext context) {

    }
}
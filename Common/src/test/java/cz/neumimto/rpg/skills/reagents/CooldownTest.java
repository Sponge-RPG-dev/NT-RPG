package cz.neumimto.rpg.skills.reagents;

import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.reagents.Cooldown;

import javax.inject.Singleton;

@Singleton
public class CooldownTest extends Cooldown {

    @Override
    public void notifyFailure(ActiveCharacter character, PlayerSkillContext context) {

    }
}
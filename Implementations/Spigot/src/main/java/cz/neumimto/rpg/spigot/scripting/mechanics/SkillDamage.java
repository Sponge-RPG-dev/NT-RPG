package cz.neumimto.rpg.spigot.scripting.mechanics;

import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SkillDamage {

    @Inject
    private DamageService damageService;

    public void getTargets(ISpigotCharacter character, IEntity target, float damage, ISkill skill) {
        ((ISpigotEntity) target).getEntity().damage(damage, character.getPlayer());
    }
}

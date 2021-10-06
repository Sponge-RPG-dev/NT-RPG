package cz.neumimto.rpg.common.scripting.mechanics;

import cz.neumimto.nts.annotations.ScriptMeta.Function;
import cz.neumimto.nts.annotations.ScriptMeta.Handler;
import cz.neumimto.nts.annotations.ScriptMeta.NamedParam;
import cz.neumimto.rpg.api.IRpgElement;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.IEntity;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EntitiesM {

    @Inject
    private EntityService entityService;

    @Inject
    private DamageService damageService;

    @Handler
    @Function("heal")
    public double heal(@NamedParam("entity") IEntity target,
                     @NamedParam("amount") float amount,
                     @NamedParam("source") IRpgElement skill) {
        return entityService.healEntity(target, amount, skill);
    }

    @Handler
    @Function("damage")
    public boolean damage(
                    @NamedParam("t|target") IEntity target,
                    @NamedParam("e|damager") IEntity damager,
                    @NamedParam("d|damage") double damage) {

        if (damageService.canDamage()) {
            damageService.damageEntity(target, damage);
        }
    }

}
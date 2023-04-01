package cz.neumimto.rpg.common.scripting.mechanics;

import com.google.auto.service.AutoService;
import cz.neumimto.nts.annotations.ScriptMeta.Function;
import cz.neumimto.nts.annotations.ScriptMeta.Handler;
import cz.neumimto.nts.annotations.ScriptMeta.NamedParam;
import cz.neumimto.rpg.common.IRpgElement;
import cz.neumimto.rpg.common.damage.DamageService;
import cz.neumimto.rpg.common.entity.EntityService;
import cz.neumimto.rpg.common.entity.IEffectConsumer;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@AutoService(NTScriptProxy.class)
public class EntitiesM implements NTScriptProxy {

    @Inject
    private EntityService entityService;

    @Inject
    private CharacterService characterService;

    @Inject
    private DamageService damageService;

    @Handler
    @Function("heal")
    public double heal(@NamedParam("e|entity") IEntity target,
                       @NamedParam("a|amount") float amount,
                       @NamedParam("s|source") IRpgElement skill) {
        return entityService.healEntity(target, amount, skill);
    }

    @Handler
    @Function("gain_resource")
    public void add_resource(@NamedParam("e|entity") IEffectConsumer target,
                             @NamedParam("a|amount") float amount,
                             @NamedParam("r|resource") String resource,
                             @NamedParam("s|source") IRpgElement skill) {
        if (target instanceof ActiveCharacter c) {
            characterService.gainResource(c, amount, skill, resource);
        }
    }


}

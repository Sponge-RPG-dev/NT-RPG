package cz.neumimto.rpg.sponge.skills.passive;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.PassiveSkill;
import cz.neumimto.rpg.sponge.effects.positive.Bash;
import cz.neumimto.rpg.sponge.model.BashModel;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by NeumimTo on 4.7.2017.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:basher")
public class Basher extends PassiveSkill {

    @Inject
    private EffectService effectService;

    @Inject
    private EntityService entityService;

    public Basher() {
        super(Bash.name);
        settings.addNode(SkillNodes.DAMAGE, 10);
        settings.addNode(SkillNodes.CHANCE, 0.1f);
        settings.addNode(SkillNodes.PERIOD, 2500);
        settings.addNode(SkillNodes.DURATION, 1000);
        setDamageType(DamageTypes.ATTACK.getId());
        addSkillType(SkillType.PHYSICAL);
    }

    @Override
    public void applyEffect(PlayerSkillContext info, IActiveCharacter character) {
        BashModel model = getBashModel(info, character);
        effectService.addEffect(new Bash(character, -1, model), this);
    }

    @Override
    public void skillUpgrade(IActiveCharacter IActiveCharacter, int level, PlayerSkillContext context) {
        super.skillUpgrade(IActiveCharacter, level, context);
        PlayerSkillContext info = IActiveCharacter.getSkill(getId());
        BashModel model = getBashModel(info, IActiveCharacter);
        effectService.removeEffect(Bash.name, IActiveCharacter, this);
        effectService.addEffect(new Bash(IActiveCharacter, -1, model), this);
    }

    private BashModel getBashModel(PlayerSkillContext info, IActiveCharacter character) {
        BashModel model = new BashModel();
        model.chance = info.getIntNodeValue(SkillNodes.CHANCE);
        model.cooldown = info.getLongNodeValue(SkillNodes.COOLDOWN);
        model.damage = info.getDoubleNodeValue(SkillNodes.DAMAGE);
        model.stunDuration = info.getLongNodeValue(SkillNodes.DURATION);
        return model;
    }
}

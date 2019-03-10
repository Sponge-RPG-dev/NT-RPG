package cz.neumimto.skills.active;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import org.spongepowered.api.text.Text;

/**
 * Created by NeumimTo on 10.3.2019.
 */
@ResourceLoader.Skill("ntrpg:test")
public class TestSkill extends ActiveSkill {

    @Inject
    private EntityService entityService;

    @Inject
    private EffectService effectService;

    public void init() {
        super.init();
    }

    @Override
    public void cast(IActiveCharacter character, PlayerSkillContext info, SkillContext modifier) {
        effectService.addEffect(new TestEffect(character), this);
        effectService.addEffect(new TestEffect(character), this);
        effectService.addEffect(new TestEffect(character), this);
        effectService.addEffect(new TestEffect(character), this);
        modifier.next(character, info, modifier);
    }

    public static class TestEffect extends EffectBase {

        public TestEffect(IEffectConsumer consumer) {
            super("test", consumer);
        }

        @Override
        public void onApply(IEffect self) {
            self.getConsumer().sendMessage(Text.of("onApply " + getUUID()));
        }

        @Override
        public void onRemove(IEffect self) {
            self.getConsumer().sendMessage(Text.of("onRemove " + getUUID()));
        }

    }

}


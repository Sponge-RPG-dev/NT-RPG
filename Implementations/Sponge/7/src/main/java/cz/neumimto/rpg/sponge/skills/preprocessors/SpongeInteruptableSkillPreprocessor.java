package cz.neumimto.rpg.sponge.skills.preprocessors;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.effects.EffectSourceType;
import cz.neumimto.rpg.api.effects.IEffectSource;
import cz.neumimto.rpg.api.effects.IEffectSourceProvider;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.mods.ActiveSkillPreProcessorWrapper;
import cz.neumimto.rpg.api.skills.mods.PreProcessorTarget;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.preprocessors.InterruptableSkillPreprocessor;
import cz.neumimto.rpg.sponge.effects.common.model.SlowModel;
import cz.neumimto.rpg.sponge.effects.common.negative.SlowEffect;
import cz.neumimto.rpg.sponge.inventory.SpongeItemService;
import org.spongepowered.api.Sponge;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class SpongeInteruptableSkillPreprocessor extends ActiveSkillPreProcessorWrapper implements InterruptableSkillPreprocessor, IEffectSourceProvider {

    private final boolean slowCaster;
    private final long delay;
    private boolean interrupted;
    private static SlowModel model;

    static {
        model = new SlowModel();
        model.decreasedJumpHeight = true;
        model.slowLevel = 2;
    }

    public SpongeInteruptableSkillPreprocessor(boolean slowCaster, long delay) {
        super(PreProcessorTarget.EARLY);
        this.slowCaster = slowCaster;
        this.delay = delay;
    }

    @Override
    public void doNext(IActiveCharacter character, PlayerSkillContext info, SkillContext context) {
        Optional<InterruptableSkillPreprocessor> channeled = character.getChanneledSkill();
        if (channeled.isPresent()) {
            context.next(character, info, context.result(SkillResult.CANCELLED));
            return;
        }
        if (slowCaster) {
            SlowEffect slowEffect = new SlowEffect(character, delay, model);
            Rpg.get().getEffectService().addEffect(slowEffect, this);
        }
        Sponge.getScheduler().createSyncExecutor( ((SpongeItemService)Rpg.get().getItemService())).schedule(() -> {
            character.setChanneledSkill(null);
            if (isInterrupted()) {
                context.continueExecution(false);
            } else {
                context.next(character, info, context);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public void interrupt() {
        interrupted = true;
    }

    @Override
    public boolean isInterrupted() {
        return interrupted;
    }

    @Override
    public IEffectSource getType() {
        return EffectSourceType.SKILL_EXECUTOR;
    }
}

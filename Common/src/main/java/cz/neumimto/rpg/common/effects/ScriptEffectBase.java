package cz.neumimto.rpg.common.effects;

import cz.neumimto.nts.annotations.ScriptMeta;
import cz.neumimto.nts.annotations.ScriptMeta.ScriptTarget;

public class ScriptEffectBase extends EffectBase {

    public static Handler onApply;
    public static Handler onTick;
    public static Handler onRemove;

    public interface Handler<T extends EffectBase> {

        @ScriptTarget
        void run(@ScriptMeta.NamedParam("effect") T t);
    }

    public static class W implements Handler<AllSkillsBonus> {

        @Override
        public void run(AllSkillsBonus allSkillsBonus) {

        }
    }

    static {
        onApply = new W();
    }

    public ScriptEffectBase() {
        super();
        effectName = "w";
    }

    @Override
    public void onApply(IEffect self) {
        if (onApply != null) {
            onApply.run(this);
        }
    }

    @Override
    public void onTick(IEffect self) {
        if (onTick != null) {
            onTick.run(this);
        }
    }

    @Override
    public void onRemove(IEffect self) {
        if (onRemove != null) {
            onRemove.run(this);
        }
    }
}

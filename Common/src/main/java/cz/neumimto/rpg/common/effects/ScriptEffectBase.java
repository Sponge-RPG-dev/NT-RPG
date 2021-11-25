package cz.neumimto.rpg.common.effects;

import cz.neumimto.nts.annotations.ScriptMeta;
import cz.neumimto.nts.annotations.ScriptMeta.ScriptTarget;

public class ScriptEffectBase extends UnstackableEffectBase {


    public interface Handler<T extends EffectBase> {

        @ScriptTarget
        void run(@ScriptMeta.NamedParam("effect") T t);
    }

    public ScriptEffectBase() {
        super();
        effectName = "w";
    }


}

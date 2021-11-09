package cz.neumimto.rpg.spigot.effects.common;

import com.google.auto.service.AutoService;
import cz.neumimto.nts.annotations.ScriptMeta;
import cz.neumimto.rpg.common.effects.CommonEffectTypes;
import cz.neumimto.rpg.common.effects.EffectBase;
import cz.neumimto.rpg.common.effects.Generate;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.entity.IEffectConsumer;
import cz.neumimto.rpg.spigot.effects.common.model.SlowModel;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@AutoService(IEffect.class)
@ScriptMeta.Function("SlowEffect")
@Generate(id = "name", description = "Decreases movement speed")
public class SlowEffect extends EffectBase<SlowModel> {

    public static String name = "SlowEffect";

    @ScriptMeta.Handler
    public SlowEffect(@ScriptMeta.NamedParam("e|entity") IEffectConsumer consumer,
                @ScriptMeta.NamedParam("d|duration") long duration,
                @ScriptMeta.NamedParam("sL|slowLevel") int slowLevel,
                @ScriptMeta.NamedParam("jh|jumpHeight") boolean jh) {
        this(consumer, duration, new SlowModel(slowLevel, jh));
    }


    @Generate.Constructor
    public SlowEffect(IEffectConsumer consumer,
                      long duration,
                      @Generate.Model SlowModel slowModel) {
        super(name, consumer);
        setValue(slowModel);
        setDuration(duration);
        setPeriod(750L);
        addEffectType(CommonEffectTypes.SILENCE);
        addEffectType(CommonEffectTypes.SLOW);
    }

    @Override
    public void onTick(IEffect self) {
        SlowModel value = getValue();
        ISpigotEntity entity = (ISpigotEntity) getConsumer();
        LivingEntity entity1 = entity.getEntity();
        if (value.decreasedJumpHeight) {

            PotionEffect pe = new PotionEffect(PotionEffectType.JUMP, 17, -1);
            entity1.addPotionEffect(pe);
        }
        int slowLevel = value.slowLevel;
        PotionEffect pe = new PotionEffect(PotionEffectType.SLOW_DIGGING, 17, slowLevel);
        entity1.addPotionEffect(pe);
    }

    @Override
    public String getName() {
        return name;
    }
}

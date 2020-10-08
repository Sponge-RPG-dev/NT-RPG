package cz.neumimto.rpg.spigot.effects.common;

import cz.neumimto.rpg.api.effects.CommonEffectTypes;
import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.spigot.effects.common.model.SlowModel;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Generate(id = "name", description = "Decreases movement speed")
public class SlowEffect extends EffectBase<SlowModel> {

    public static String name = "SlowEffect";

    public SlowEffect(IEffectConsumer consumer, long duration, @Generate.Model SlowModel slowModel) {
        super(name, consumer);
        setValue(slowModel);
        setDuration(duration);
        setPeriod(750L);
        addEffectType(CommonEffectTypes.SILENCE);
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

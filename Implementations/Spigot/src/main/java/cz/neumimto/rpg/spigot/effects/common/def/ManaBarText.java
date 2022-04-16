package cz.neumimto.rpg.spigot.effects.common.def;

import cz.neumimto.rpg.common.effects.*;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.utils.MathUtils;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public class ManaBarText extends EffectBase<Object> implements IEffectContainer<Object, ManaBarText>, ManaBar {

    public static Component[] ROWS;

    static {
        ROWS = IntStream.range(0, 21)
                .mapToObj(a -> "")
                .map(Component::text)
                .toList()
                .toArray(Component[]::new);
    }
/*
    /title @a actionbar [{"text":"ꐦꐦꐦꐦꐦꐦꐦꐦꐦꐦꐦꐦꐦꐦ"},{"text":"ꐡꐩꐡꐩꐢꐩꐣꐩꐣꐩꐣꐩꐣꐩꐣꐩꐣꐩꐣꐩ"}]
*/
    protected IActiveCharacter character;
    protected Player player;

    public ManaBarText(ISpigotCharacter consumer) {
        super(name, consumer);
        this.character = consumer;
        this.player = consumer.getPlayer();
        effectTypes.add(CoreEffectTypes.GUI);
        setPeriod(1000);
        setDuration(-1);
    }

    @Override
    public void onTick(IEffect self) {
        double percentage = MathUtils.getPercentage(character.getMana().getValue(), character.getMana().getMaxValue());
        percentage = percentage > 100 ? 100 : percentage;
        percentage = percentage < 0 ? 0 : percentage;
        Component stringg = ROWS[(int) Math.round(percentage / 5)];
        SpigotRpgPlugin.getBukkitAudiences().player(player).sendActionBar(stringg);
    }

    @Override
    public void setDuration(long l) {
        if (l >= 0) {
            throw new IllegalArgumentException();
        }
        super.setDuration(l);
    }

    @Override
    public Set<ManaBarText> getEffects() {
        return new HashSet<>(Collections.singletonList(this));
    }

    @Override
    public ManaBarText getStackedValue() {
        return this;
    }

    @Override
    public void setStackedValue(Object o) {

    }

    @Override
    public ManaBarText constructEffectContainer() {
        return this;
    }

    @Override
    public void stackEffect(ManaBarText v, IEffectSourceProvider effectSourceProvider) {

    }

    @Override
    public void notifyManaChange() {
        onTick(this);
    }

    @Override
    public IEffect asEffect() {
        return this;
    }
}
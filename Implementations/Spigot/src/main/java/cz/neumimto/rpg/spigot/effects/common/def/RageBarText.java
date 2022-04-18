package cz.neumimto.rpg.spigot.effects.common.def;

import cz.neumimto.rpg.common.effects.*;
import cz.neumimto.rpg.common.entity.IEffectConsumer;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.resources.Resource;
import cz.neumimto.rpg.common.resources.ResourceService;
import cz.neumimto.rpg.common.utils.MathUtils;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

@Generate(id = "name", description = "A component which displays rage in action bar")
public class RageBarText extends EffectBase<Object> implements IEffectContainer<Object, RageBarText> {

    public static final String name = "RageBarText";

    public static Component[] ROWS;

    static {
        ROWS = IntStream.range(0, 21)
                .mapToObj(a -> "")
                .map(Component::text)
                .toList()
                .toArray(Component[]::new);
    }

    protected IActiveCharacter character;
    protected Player player;


    @Generate.Constructor
    public RageBarText(IEffectConsumer consumer) {
        this((IActiveCharacter) consumer);
    }

    public RageBarText(IActiveCharacter consumer) {
        super(name, consumer);
        this.character = consumer;
        this.player = (Player) consumer.getEntity();
        effectTypes.add(CoreEffectTypes.GUI);
        setPeriod(1000);
        setDuration(-1);
    }

    @Override
    public void onTick(IEffect self) {
        Resource rage = character.getResource(ResourceService.rage);
        double percentage = MathUtils.getPercentage(rage.getValue(), rage.getMaxValue());
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
    public Set<RageBarText> getEffects() {
        return new HashSet<>(Collections.singletonList(this));
    }

    @Override
    public RageBarText getStackedValue() {
        return this;
    }

    @Override
    public void setStackedValue(Object o) {

    }

    @Override
    public RageBarText constructEffectContainer() {
        return this;
    }

    @Override
    public void stackEffect(RageBarText v, IEffectSourceProvider effectSourceProvider) {

    }

    public void notifyRageChange() {
        onTick(this);
    }

    public IEffect asEffect() {
        return this;
    }
}
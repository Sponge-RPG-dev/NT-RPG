package cz.neumimto.rpg.spigot.effects.common.def;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.api.effects.IEffectSourceProvider;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.utils.MathUtils;
import cz.neumimto.rpg.common.effects.CoreEffectTypes;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ManaBarText extends EffectBase<Object> implements IEffectContainer<Object, ManaBarText>, ManaBar {

    public static BaseComponent[][] ROWS = {
            TextComponent.fromLegacyText("\uE000                         \uE002\uE002\uE002\uE002\uE002\uE002\uE002\uE002\uE002\uE002"),
            TextComponent.fromLegacyText("\uE000                         \uE002\uE002\uE002\uE002\uE002\uE002\uE002\uE002\uE002\uE003"),
            TextComponent.fromLegacyText("\uE000                         \uE002\uE002\uE002\uE002\uE002\uE002\uE002\uE002\uE002\uE004"),
            TextComponent.fromLegacyText("\uE000                         \uE002\uE002\uE002\uE002\uE002\uE002\uE002\uE002\uE003\uE004"),
            TextComponent.fromLegacyText("\uE000                         \uE002\uE002\uE002\uE002\uE002\uE002\uE002\uE002\uE004\uE004"),
            TextComponent.fromLegacyText("\uE000                         \uE002\uE002\uE002\uE002\uE002\uE002\uE002\uE003\uE004\uE004"),
            TextComponent.fromLegacyText("\uE000                         \uE002\uE002\uE002\uE002\uE002\uE002\uE002\uE004\uE004\uE004"),
            TextComponent.fromLegacyText("\uE000                         \uE002\uE002\uE002\uE002\uE002\uE002\uE003\uE004\uE004\uE004"),
            TextComponent.fromLegacyText("\uE000                         \uE002\uE002\uE002\uE002\uE002\uE002\uE004\uE004\uE004\uE004"),
            TextComponent.fromLegacyText("\uE000                         \uE002\uE002\uE002\uE002\uE002\uE003\uE004\uE004\uE004\uE004"),
            TextComponent.fromLegacyText("\uE000                         \uE002\uE002\uE002\uE002\uE002\uE004\uE004\uE004\uE004\uE004"),
            TextComponent.fromLegacyText("\uE000                         \uE002\uE002\uE002\uE002\uE003\uE004\uE004\uE004\uE004\uE004"),
            TextComponent.fromLegacyText("\uE000                         \uE002\uE002\uE002\uE002\uE004\uE004\uE004\uE004\uE004\uE004"),
            TextComponent.fromLegacyText("\uE000                         \uE002\uE002\uE002\uE003\uE004\uE004\uE004\uE004\uE004\uE004"),
            TextComponent.fromLegacyText("\uE000                         \uE002\uE002\uE002\uE004\uE004\uE004\uE004\uE004\uE004\uE004"),
            TextComponent.fromLegacyText("\uE000                         \uE002\uE002\uE003\uE004\uE004\uE004\uE004\uE004\uE004\uE004"),
            TextComponent.fromLegacyText("\uE000                         \uE002\uE002\uE004\uE004\uE004\uE004\uE004\uE004\uE004\uE004"),
            TextComponent.fromLegacyText("\uE000                         \uE002\uE003\uE004\uE004\uE004\uE004\uE004\uE004\uE004\uE004"),
            TextComponent.fromLegacyText("\uE000                         \uE002\uE004\uE004\uE004\uE004\uE004\uE004\uE004\uE004\uE004"),
            TextComponent.fromLegacyText("\uE000                         \uE003\uE004\uE004\uE004\uE004\uE004\uE004\uE004\uE004\uE004"),
            TextComponent.fromLegacyText("\uE000                         \uE004\uE004\uE004\uE004\uE004\uE004\uE004\uE004\uE004\uE004")
    };

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
        BaseComponent[] stringg = ROWS[(int) Math.round(percentage / 5)];
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, stringg);
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
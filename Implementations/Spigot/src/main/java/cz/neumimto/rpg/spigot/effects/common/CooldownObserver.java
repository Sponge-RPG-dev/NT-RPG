package cz.neumimto.rpg.spigot.effects.common;


import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.utils.MathUtils;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by NeumimTo on 26.4.17.
 */
@Generate(id = "name", description = "A component which will be displaying cooldowns in the action bar")
public class CooldownObserver extends EffectBase implements IEffectContainer {

    public static final String name = "CooldownObserver";

    private ISpigotCharacter character;

    public CooldownObserver(IEffectConsumer character, long duration, String value) {
        super(name, character);
        this.character = (ISpigotCharacter) character;
        setDuration(duration);
        String s = MathUtils.extractNumber(value);
        long l = 1000;
        if (s != null) {
            l = Long.parseLong(s);
            if (value.endsWith("s")) {
                l *= 1000;
            }
        }
        setPeriod(l);
    }


    @Override
    public void onTick(IEffect self) {
        Long time = System.currentTimeMillis();
        Map<String, Long> cooldowns = character.getCooldowns();
        long p;
        StringBuilder text = new StringBuilder();
        for (Map.Entry<String, Long> a : cooldowns.entrySet()) {
            p = a.getValue() - time;
            if (p > 120L && p < 0L) {
                text.append(ChatColor.GREEN + ":").append(p > 10 ? ChatColor.RED : ChatColor.YELLOW).append(p).append(" ");
            }
        }
        if (text.length() > 0) {
            character.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text.toString()));
        }
    }

    @Override
    public Set<CooldownObserver> getEffects() {
        return new HashSet<>(Collections.singletonList(this));
    }


    @Override
    public CooldownObserver constructEffectContainer() {
        return this;
    }

    @Override
    public Object getStackedValue() {
        return null;
    }

    @Override
    public void setStackedValue(Object o) {

    }
}

package cz.neumimto.rpg.spigot.effects.common.def;

import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.api.effects.IEffectSourceProvider;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.utils.MathUtils;
import cz.neumimto.rpg.common.effects.CoreEffectTypes;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 9.7.2017.
 */
public class ManaBarNotifier extends EffectBase<Object> implements IEffectContainer<Object, ManaBarNotifier> {

    public static final String name = "ManaBar";
    private IActiveCharacter character;
    private Player player;
    private BossBar bossBar;

    public ManaBarNotifier(ISpigotCharacter consumer) {
        super(name, consumer);
        this.character = consumer;
        this.player = consumer.getPlayer();
        effectTypes.add(CoreEffectTypes.GUI);
        setPeriod(5000);
        setDuration(-1);
    }

    public void notifyManaChange() {
        double maxValue = character.getMana().getMaxValue();
        double value = character.getMana().getValue();
        String title = ChatColor.BLUE.toString() + ChatColor.BOLD + "Mana" + ChatColor.GOLD + ": " + ChatColor.BLUE + value
                + ChatColor.GOLD + " / " + ChatColor.BLUE + maxValue;

        if (bossBar == null) {
            BossBar bossBar = Bukkit.getServer().createBossBar(title, BarColor.BLUE, BarStyle.SOLID);
            bossBar.setProgress(MathUtils.getPercentage(value, maxValue) * 0.01d);
            bossBar.addPlayer(player);
        }

        if (maxValue > 0) {
            bossBar.setTitle(title);
            bossBar.setProgress(MathUtils.getPercentage(value, maxValue) * 0.01d);
            bossBar.setVisible(true);
        }
        setLastTickTime(System.currentTimeMillis());
    }

    @Override
    public void onTick(IEffect self) {
        if (bossBar.isVisible()) {
            bossBar.setVisible(false);
        }
    }

    @Override
    public void onRemove(IEffect self) {
        bossBar.removePlayer(player);
    }

    @Override
    public void setDuration(long l) {
        if (l >= 0) {
            throw new IllegalArgumentException();
        }
        super.setDuration(l);
    }

    @Override
    public Set<ManaBarNotifier> getEffects() {
        return new HashSet<>(Collections.singletonList(this));
    }

    @Override
    public ManaBarNotifier getStackedValue() {
        return this;
    }

    @Override
    public void setStackedValue(Object o) {

    }

    @Override
    public ManaBarNotifier constructEffectContainer() {
        return this;
    }

    @Override
    public void stackEffect(ManaBarNotifier v, IEffectSourceProvider effectSourceProvider) {

    }
}

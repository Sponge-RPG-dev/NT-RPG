package cz.neumimto.rpg.spigot.effects.common.def;

import cz.neumimto.rpg.common.effects.*;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.resources.Resource;
import cz.neumimto.rpg.common.resources.ResourceService;
import cz.neumimto.rpg.common.utils.MathUtils;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 9.7.2017.
 */
public class ManaBarBossBar extends EffectBase<Object> implements IEffectContainer<Object, ManaBarBossBar>, ManaBar {

    protected IActiveCharacter character;
    protected Player player;
    protected org.bukkit.boss.BossBar bossBar;

    public ManaBarBossBar(ISpigotCharacter consumer) {
        super(name, consumer);
        this.character = consumer;
        this.player = consumer.getPlayer();
        effectTypes.add(CoreEffectTypes.GUI);
        setPeriod(5000);
        setDuration(-1);
    }

    @Override
    public void notifyManaChange() {
        Resource mana = character.getResource(ResourceService.mana);
        double maxValue = mana.getMaxValue();
        double value = mana.getValue();
        String title = ChatColor.BLUE.toString() + ChatColor.BOLD + "Mana" + ChatColor.GOLD + ": " + ChatColor.BLUE + value
                + ChatColor.GOLD + " / " + ChatColor.BLUE + maxValue;

        if (maxValue == 0) {
            maxValue = 0.0001D;
        }

        double percentage = MathUtils.getPercentage(value, maxValue) * 0.01;
        if (bossBar == null) {
            bossBar = Bukkit.getServer().createBossBar(title, BarColor.BLUE, BarStyle.SOLID);
            bossBar.addPlayer(player);
        }
        bossBar.setProgress(percentage);

        if (maxValue > 0) {
            bossBar.setTitle(title);
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
    public Set<ManaBarBossBar> getEffects() {
        return new HashSet<>(Collections.singletonList(this));
    }

    @Override
    public ManaBarBossBar getStackedValue() {
        return this;
    }

    @Override
    public void setStackedValue(Object o) {

    }

    @Override
    public ManaBarBossBar constructEffectContainer() {
        return this;
    }

    @Override
    public void stackEffect(ManaBarBossBar v, IEffectSourceProvider effectSourceProvider) {

    }

    @Override
    public IEffect asEffect() {
        return this;
    }
}

package cz.neumimto.rpg.spigot.effects.common.def;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.effects.EffectBase;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.effects.IEffectContainer;
import cz.neumimto.rpg.common.effects.IEffectSourceProvider;
import cz.neumimto.rpg.common.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.common.localization.LocalizationKeys;
import cz.neumimto.rpg.common.localization.LocalizationService;
import cz.neumimto.rpg.common.utils.MathUtils;
import cz.neumimto.rpg.common.effects.CoreEffectTypes;
import cz.neumimto.rpg.common.utils.StringUtils;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by NeumimTo on 25.6.2016.
 */
public class BossBarExpNotifier extends EffectBase<Object> implements IEffectContainer<Object, BossBarExpNotifier> {

    public static final String name = "BossBarExp";
    private Map<String, SessionWrapper> bossBarMap = new HashMap<>();

    public BossBarExpNotifier(ISpigotCharacter consumer) {
        super(name, consumer);
        effectTypes.add(CoreEffectTypes.GUI);
        setPeriod(5000);
        setDuration(-1);
    }

    public void notifyExpChange(ISpigotCharacter character, String clazz, double exps) {
        final String classname = clazz.toLowerCase();
        Optional<PlayerClassData> first = Optional.ofNullable(character.getClassByName(classname));
        if (first.isPresent()) {
            SessionWrapper sessionWrapper = bossBarMap.computeIfAbsent(classname, s -> new SessionWrapper());
            BossBar serverBossBar = sessionWrapper.serverBossBar;
            if (serverBossBar == null) {
                serverBossBar = Bukkit.getServer().createBossBar("", BarColor.BLUE, BarStyle.SEGMENTED_10);
                serverBossBar.setVisible(false);

                serverBossBar.addPlayer(character.getPlayer());
                sessionWrapper.serverBossBar = serverBossBar;
            }
            PlayerClassData playerClassData = first.get();

            sessionWrapper.currentSessionExp += exps;
            DecimalFormat df = new DecimalFormat("0");
            DecimalFormat changeDf = new DecimalFormat("+0.0;-0.0");

            LocalizationService localizationService = Rpg.get().getLocalizationService();
            String preferedColor = playerClassData.getClassDefinition().getPreferedColor();

            ChatColor textColor = ChatColor.valueOf(preferedColor);

            String s = textColor + StringUtils.capitalizeFirst(classname) + " " + localizationService.translate(LocalizationKeys.LEVEL)
                    + ChatColor.DARK_GRAY + " :" + ChatColor.GOLD + playerClassData.getLevel() + " " + ChatColor.GREEN + changeDf.format(sessionWrapper.currentSessionExp)
                    + " " + df.format(playerClassData.getExperiencesFromLevel()) + " / " + df.format(playerClassData.getClassDefinition().getLevelProgression().getLevelMargins()[playerClassData.getLevel()]);


            serverBossBar.setTitle(s);
            serverBossBar.setProgress(MathUtils
                    .getPercentage(playerClassData.getExperiencesFromLevel(), playerClassData.getClassDefinition().getLevelProgression().getLevelMargins()[playerClassData.getLevel()])
                    / 100);
            serverBossBar.setVisible(true);
            setLastTickTime(System.currentTimeMillis());

        }
    }

    @Override
    public void onTick(IEffect self) {
        for (SessionWrapper sessionWrapper : bossBarMap.values()) {
            if (sessionWrapper.serverBossBar != null) {
                BossBar bossBar = sessionWrapper.serverBossBar;
                if (bossBar.isVisible()) {
                    bossBar.setVisible(false);
                    sessionWrapper.currentSessionExp = 0;
                }
            }
        }
    }

    @Override
    public void onRemove(IEffect self) {
        for (SessionWrapper sessionWrapper : bossBarMap.values()) {
            if (sessionWrapper.serverBossBar != null) {
                sessionWrapper.serverBossBar.removePlayer(((ISpigotCharacter) getConsumer()).getPlayer());
            }
        }
    }

    @Override
    public void setDuration(long l) {
        super.setDuration(l);
    }

    @Override
    public Set<BossBarExpNotifier> getEffects() {
        return new HashSet<>(Collections.singletonList(this));
    }

    @Override
    public BossBarExpNotifier getStackedValue() {
        return this;
    }

    @Override
    public void setStackedValue(Object o) {

    }

    @Override
    public BossBarExpNotifier constructEffectContainer() {
        return this;
    }

    @Override
    public void stackEffect(BossBarExpNotifier bossBarExpNotifier, IEffectSourceProvider effectSourceProvider) {

    }

    private static class SessionWrapper {
        BossBar serverBossBar;
        double currentSessionExp;
    }
}



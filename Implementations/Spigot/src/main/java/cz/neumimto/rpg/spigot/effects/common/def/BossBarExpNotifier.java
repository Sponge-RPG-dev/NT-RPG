package cz.neumimto.rpg.spigot.effects.common.def;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.effects.*;
import cz.neumimto.rpg.common.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.common.localization.LocalizationKeys;
import cz.neumimto.rpg.common.localization.LocalizationService;
import cz.neumimto.rpg.common.utils.MathUtils;
import cz.neumimto.rpg.common.utils.StringUtils;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

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

            PlayerClassData playerClassData = first.get();

            sessionWrapper.currentSessionExp += exps;
            DecimalFormat df = new DecimalFormat("0");
            DecimalFormat changeDf = new DecimalFormat("+0.0;-0.0");

            LocalizationService localizationService = Rpg.get().getLocalizationService();
            String preferedColor = playerClassData.getClassDefinition().getPreferedColor();


            TextComponent title = Component.text(StringUtils.capitalizeFirst(classname)).color(TextColor.fromHexString(preferedColor))
                    .append(Component.text(" " + localizationService.translate(LocalizationKeys.LEVEL)))
                    .append(Component.text(" :").color(NamedTextColor.DARK_GRAY))
                    .append(Component.text(playerClassData.getLevel() + " ").color(NamedTextColor.GOLD))
                    .append(Component.text(changeDf.format(sessionWrapper.currentSessionExp) + " " + df.format(playerClassData.getExperiencesFromLevel()) + " / " + df.format(playerClassData.getClassDefinition().getLevelProgression().getLevelMargins()[playerClassData.getLevel()])).color(NamedTextColor.GREEN));

            double progress = MathUtils
                    .getPercentage(playerClassData.getExperiencesFromLevel(), playerClassData.getClassDefinition().getLevelProgression().getLevelMargins()[playerClassData.getLevel()])
                    / 100;


            if (serverBossBar == null) {
                serverBossBar = BossBar.bossBar(title,0, BossBar.Color.BLUE, BossBar.Overlay.NOTCHED_10);

                sessionWrapper.serverBossBar = serverBossBar;
            } else {
                serverBossBar.progress((float)progress);
                serverBossBar.name(title);
            }

            Audience player = SpigotRpgPlugin.getBukkitAudiences().player(character.getPlayer());
            player.showBossBar(serverBossBar);
            sessionWrapper.displayed = true;
            setLastTickTime(System.currentTimeMillis());

        }
    }

    @Override
    public void onTick(IEffect self) {
        Audience player = SpigotRpgPlugin.getBukkitAudiences().player(((ISpigotCharacter)getConsumer()).getPlayer());

        for (SessionWrapper sessionWrapper : bossBarMap.values()) {
            if (sessionWrapper.serverBossBar != null) {
                BossBar bossBar = sessionWrapper.serverBossBar;

                if (sessionWrapper.displayed) {
                    player.hideBossBar(bossBar);
                    sessionWrapper.currentSessionExp = 0;
                    sessionWrapper.displayed = false;
                }
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
        public boolean displayed;
        BossBar serverBossBar;
        double currentSessionExp;
    }
}



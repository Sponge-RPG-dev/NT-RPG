package cz.neumimto.rpg.sponge.effects.common.def;

import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.effects.IEffectContainer;
import cz.neumimto.rpg.api.effects.IEffectSourceProvider;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.common.effects.CoreEffectTypes;
import cz.neumimto.rpg.sponge.effects.SpongeEffectBase;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.entities.players.SpongeCharacter;
import cz.neumimto.rpg.sponge.utils.Utils;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by NeumimTo on 25.6.2016.
 */
public class BossBarExpNotifier extends SpongeEffectBase<Object> implements IEffectContainer<Object, BossBarExpNotifier> {

    public static final String name = "BossBarExp";
    private Map<String, SessionWrapper> bossBarMap = new HashMap<>();

    public BossBarExpNotifier(ISpongeCharacter consumer) {
        super(name, consumer);
        effectTypes.add(CoreEffectTypes.GUI);
        setPeriod(5000);
        setDuration(-1);
    }

    public void notifyExpChange(ISpongeCharacter character, String clazz, double exps) {
        final String classname = clazz.toLowerCase();
        Optional<PlayerClassData> first =
                character.getClasses().values().stream().filter(a -> a.getClassDefinition().getName().equalsIgnoreCase(classname)).findFirst();
        if (first.isPresent()) {
            SessionWrapper sessionWrapper = bossBarMap.computeIfAbsent(classname, s -> new SessionWrapper());
            ServerBossBar serverBossBar = sessionWrapper.serverBossBar;
            if (serverBossBar == null) {
                serverBossBar = ServerBossBar.builder()
                        .visible(false)
                        .playEndBossMusic(false)
                        .darkenSky(false)
                        .name(Text.of("bossbarexp"))
                        .overlay(BossBarOverlays.NOTCHED_10)
                        .color(BossBarColors.BLUE)
                        .createFog(false)
                        .percent(0)
                        .build();
                serverBossBar.addPlayer(character.getPlayer());
                sessionWrapper.serverBossBar = serverBossBar;
            }
            PlayerClassData playerClassData = first.get();

            sessionWrapper.currentSessionExp += exps;
            DecimalFormat df = new DecimalFormat("#.00");


            serverBossBar.setName(
                    Text.builder(Utils.capitalizeFirst(classname)).color(playerClassData.getClassDefinition().getPreferedColor())
                            .append(Text.builder(" ").append(Localizations.LEVEL.toText()).append(Text.of(": ")).color(TextColors.DARK_GRAY)
                                    .build())
                            .append(Text.builder(String.valueOf(playerClassData.getLevel())).color(TextColors.GOLD).build())
                            .append(Text.builder(" +" + df.format(sessionWrapper.currentSessionExp)).color(TextColors.GREEN).build())
                            .append(Text.builder(" " + df.format(playerClassData.getExperiencesFromLevel())
                                    + " / "
                                    + df.format(playerClassData.getClassDefinition().getLevelProgression().getLevelMargins()[playerClassData.getLevel()]))
                                    .color(TextColors.DARK_GRAY)
                                    .style(TextStyles.ITALIC)
                                    .build())
                            .build());


            serverBossBar.setPercent((float) Utils
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
                ServerBossBar bossBar = sessionWrapper.serverBossBar;
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
                sessionWrapper.serverBossBar.removePlayer(((SpongeCharacter) getConsumer()).getPlayer());
            }
        }
    }

    @Override
    public void setDuration(long l) {
        if (l >= 0) {
            throw new IllegalArgumentException();
        }
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
        ServerBossBar serverBossBar;
        double currentSessionExp;
    }
}



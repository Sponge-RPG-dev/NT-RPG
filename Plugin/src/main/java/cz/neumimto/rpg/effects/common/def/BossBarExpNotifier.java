package cz.neumimto.rpg.effects.common.def;

import cz.neumimto.rpg.effects.CoreEffectTypes;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.players.ExtendedNClass;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.boss.BossBar;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by NeumimTo on 25.6.2016.
 */
public class BossBarExpNotifier extends EffectBase {

    public static final String name = "BossBarExp";
    private IActiveCharacter character;
    private Player player;
    private Map<String,ServerBossBar> bossBarMap = new HashMap<>();

    public BossBarExpNotifier(IActiveCharacter consumer) {
        super(name, consumer);
        this.character = consumer;
        this.player = consumer.getPlayer();
        effectTypes.add(CoreEffectTypes.GUI);
        setPeriod(5000);
        setDuration(-1);
    }

    public void notifyExpChange(String classnamee, double exps) {
        final String classname = classnamee.toLowerCase();
        Optional<ExtendedNClass> first = character.getClasses().stream().filter(a -> a.getnClass().getName().equalsIgnoreCase(classname)).findFirst();
        if (first.isPresent()) {
            ServerBossBar serverBossBar = bossBarMap.get(classname);
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
                serverBossBar.addPlayer(player);
                bossBarMap.put(classname,serverBossBar);
            }
            ExtendedNClass extendedNClass = first.get();

            serverBossBar.setName(Text.of(Utils.capitalizeFirst(classname)+" Level: "+extendedNClass.getLevel()+" +" + exps + "  " + extendedNClass.getExperiencesFromLevel()+"/"+extendedNClass.getnClass().getLevels()[extendedNClass.getLevel()]));
            serverBossBar.setPercent((float) Utils.getPercentage(extendedNClass.getExperiencesFromLevel(),extendedNClass.getnClass().getLevels()[extendedNClass.getLevel()])/100);
            serverBossBar.setVisible(true);
            setLastTickTime(System.currentTimeMillis());
        }
    }

    @Override
    public void onTick() {
        for (ServerBossBar  bossBar : bossBarMap.values()) {
            if (bossBar.isVisible()) {
                bossBar.setVisible(false);
            }
        }
    }

    @Override
    public void onRemove() {
        for (ServerBossBar  bossBar : bossBarMap.values()) {

                bossBar.removePlayer(player);

        }
    }
}



package cz.neumimto.rpg.effects.common.def;

import cz.neumimto.rpg.effects.CoreEffectTypes;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.players.ExtendedNClass;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

/**
 * Created by NeumimTo on 25.6.2016.
 */
public class BossBarExpNotifier extends EffectBase {

    public static final String name = "BossBarExp";
    private IActiveCharacter character;
    private Player player;
    private ServerBossBar bossBar;


    public BossBarExpNotifier(IActiveCharacter consumer) {
        super(name, consumer);
        this.character = consumer;
        this.player = consumer.getPlayer();
        effectTypes.add(CoreEffectTypes.GUI);
        bossBar = ServerBossBar.builder()
                .visible(false)
                .playEndBossMusic(false)
                .darkenSky(false)
                .overlay(BossBarOverlays.PROGRESS)
                .color(BossBarColors.WHITE)
                .createFog(false)
                .percent(0)
                .name(Text.of("Exp."))
                .build();
        bossBar.addPlayer(player);
        setPeriod(5000);
        setDuration(-1);
    }

    public void notifyExpChange(String classname, double exps) {
        Optional<ExtendedNClass> first = character.getClasses().stream().filter(a -> a.getnClass().getName().equalsIgnoreCase(classname)).findFirst();
        if (first.isPresent()) {
            ExtendedNClass extendedNClass = first.get();
            bossBar.setName(Text.of(classname+" +" + exps + "  " + extendedNClass.getExperiencesFromLevel()+"/"+extendedNClass.getnClass().getLevels()[extendedNClass.getLevel()-1]));
            bossBar.setPercent((float) Utils.getPercentage(extendedNClass.getExperiencesFromLevel(),extendedNClass.getnClass().getLevels()[extendedNClass.getLevel()-1]));
            bossBar.setVisible(true);
            setLastTickTime(System.currentTimeMillis());
        }
    }

    @Override
    public void onTick() {
        if (bossBar.isVisible()) {
            bossBar.setVisible(false);
        }
    }

    @Override
    public void onRemove() {
        bossBar.removePlayer(player);
    }
}



package cz.neumimto.rpg.effects.common.def;

import cz.neumimto.rpg.effects.CoreEffectTypes;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.effects.IEffectSourceProvider;
import cz.neumimto.rpg.players.ExtendedNClass;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.*;

/**
 * Created by NeumimTo on 9.7.2017.
 */
public class ManaBarNotifier extends  EffectBase<Object> implements IEffectContainer<Object, ManaBarNotifier> {


	public static final String name = "ManaBar";
	private IActiveCharacter character;
	private Player player;
	private ServerBossBar bossBar;


	public ManaBarNotifier(IActiveCharacter consumer) {
		super(name, consumer);
		this.character = consumer;
		this.player = consumer.getPlayer();
		effectTypes.add(CoreEffectTypes.GUI);
		setPeriod(5000);
		setDuration(-1);
	}

	public void notifyManaChange() {
			if (bossBar == null) {
				bossBar = ServerBossBar.builder()
						.visible(false)
						.playEndBossMusic(false)
						.darkenSky(false)
						.name(Text.of("manabar"))
						.overlay(BossBarOverlays.PROGRESS)
						.color(BossBarColors.BLUE)
						.createFog(false)
						.percent(0)
						.build();
				bossBar.addPlayer(player);
			}
			bossBar.setName(Text.of("Mana: " + "(" + character.getMana().getValue() + "/" + character.getMana().getMaxValue()+ ")"));
			bossBar.setPercent((float) (Utils.getPercentage(character.getMana().getValue(),character.getMana().getMaxValue()) * 0.01f));
			bossBar.setVisible(true);
			setLastTickTime(System.currentTimeMillis());

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

	@Override
	public void setDuration(long l) {
		if (l >= 0)
			throw new IllegalArgumentException();
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
	public ManaBarNotifier constructEffectContainer() {
		return this;
	}

	@Override
	public void setStackedValue(Object o) {

	}

	@Override
	public void stackEffect(ManaBarNotifier v, IEffectSourceProvider effectSourceProvider) {

	}
}

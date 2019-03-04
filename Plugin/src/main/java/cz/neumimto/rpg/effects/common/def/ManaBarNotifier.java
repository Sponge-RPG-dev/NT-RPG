package cz.neumimto.rpg.effects.common.def;

import cz.neumimto.rpg.effects.*;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

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
		bossBar.setName(Text.builder("Mana").color(TextColors.BLUE).style(TextStyles.BOLD)
				.append(Text.builder(": ").color(TextColors.GOLD).style(TextStyles.BOLD).build())
				.append(Text.builder(String.valueOf(character.getMana().getValue())).color(TextColors.BLUE).build())
				.append(Text.builder(" / ").color(TextColors.GOLD).style(TextStyles.BOLD).build())
				.append(Text.builder(String.valueOf(character.getMana().getMaxValue())).color(TextColors.DARK_BLUE).build())
				.build())
		;


		if (character.getMana().getMaxValue() > 0) {
			bossBar.setPercent((float) (Utils.getPercentage(character.getMana().getValue(), character.getMana().getMaxValue()) * 0.01f));
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

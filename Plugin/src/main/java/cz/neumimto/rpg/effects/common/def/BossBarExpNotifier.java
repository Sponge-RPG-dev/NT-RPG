package cz.neumimto.rpg.effects.common.def;

import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.effects.CoreEffectTypes;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.effects.IEffectSourceProvider;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.PlayerClassData;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by NeumimTo on 25.6.2016.
 */
public class BossBarExpNotifier extends EffectBase<Object> implements IEffectContainer<Object, BossBarExpNotifier> {

	public static final String name = "BossBarExp";
	private Map<String, ServerBossBar> bossBarMap = new HashMap<>();
	private double expCurrentSession;

	public BossBarExpNotifier(IActiveCharacter consumer) {
		super(name, consumer);
		effectTypes.add(CoreEffectTypes.GUI);
		setPeriod(5000);
		setDuration(-1);
	}

	public void notifyExpChange(IActiveCharacter character, String clazz, double exps) {
		final String classname = clazz.toLowerCase();
		Optional<PlayerClassData> first =
				character.getClasses().stream().filter(a -> a.getClassDefinition().getName().equalsIgnoreCase(classname)).findFirst();
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
				serverBossBar.addPlayer(character.getPlayer());
				bossBarMap.put(classname, serverBossBar);
			}
			PlayerClassData playerClassData = first.get();

			expCurrentSession += exps;
			DecimalFormat df = new DecimalFormat("#.00");


			serverBossBar.setName(
					Text.builder(Utils.capitalizeFirst(classname)).color(playerClassData.getClassDefinition().getPreferedColor())
							.append(Text.builder(" ").append(Localizations.LEVEL.toText()).append(Text.of(": ")).color(TextColors.DARK_GRAY)
					.build())
							.append(Text.builder(String.valueOf(playerClassData.getLevel())).color(TextColors.GOLD).build())
							.append(Text.builder(" +" + df.format(expCurrentSession)).color(TextColors.GREEN).build())
							.append(Text.builder(" " + df.format(playerClassData.getExperiencesFromLevel())
									+ " / "
									+ playerClassData.getClassDefinition().getLevels()[playerClassData.getLevel()])
									.color(TextColors.DARK_GRAY)
									.style(TextStyles.ITALIC)
									.build())
							.build());


			serverBossBar.setPercent((float) Utils
					.getPercentage(playerClassData.getExperiencesFromLevel(), playerClassData.getClassDefinition().getLevels()[playerClassData.getLevel()])
					/ 100);
			serverBossBar.setVisible(true);
			setLastTickTime(System.currentTimeMillis());
		}
	}

	@Override
	public void onTick() {
		for (ServerBossBar bossBar : bossBarMap.values()) {
			if (bossBar.isVisible()) {
				bossBar.setVisible(false);
				expCurrentSession = 0;
			}
		}
	}

	@Override
	public void onRemove() {
		for (ServerBossBar bossBar : bossBarMap.values()) {

			bossBar.removePlayer(((IActiveCharacter) getConsumer()).getPlayer());

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
}



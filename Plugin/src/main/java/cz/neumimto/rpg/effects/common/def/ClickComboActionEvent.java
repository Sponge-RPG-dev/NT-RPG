package cz.neumimto.rpg.effects.common.def;

import cz.neumimto.rpg.ClassGenerator;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.configuration.PluginConfig;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.players.IActiveCharacter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 28.8.2017.
 */
@ClassGenerator.Generate(id = "name")
public class ClickComboActionEvent extends EffectBase implements IEffectContainer {

	public static final String name = "ClickCombos";

	StringBuilder combination;

	private long k = 0;

	private IActiveCharacter character;

	public ClickComboActionEvent(IEffectConsumer t, double duration, float value) {
		this(t);
	}

	public ClickComboActionEvent(IEffectConsumer t) {
		super(name, t);
		character = (IActiveCharacter) t;
		setPeriod(PluginConfig.CLICK_COMBO_MAX_INVERVAL_BETWEEN_ACTIONS);
		setDuration(-1L);
	}

	public void processRMB() {
		if (!hasStarted()) {
			combination = new StringBuilder();
		}
		combination.append('R');
		update();
	}

	public void processLMB() {
		combination.append('L');
		update();
	}

	public void processShift() {
		if (PluginConfig.SHIFT_CANCELS_COMBO) {
			cancel(true);
		} else {
			combination.append('S');
		}
		update();
	}

	public void processQ() {
		combination.append('Q');
		update();
	}

	public void processE() {
		combination.append('E');
		update();
	}

	public void update() {
		if (combination != null) {
			NtRpgPlugin.GlobalScope.skillService.invokeSkillByCombo(getCurrent(), character);
		}
		k = System.currentTimeMillis();
	}

	public void cancel(boolean byShift) {
		combination = null;
		Gui.resetCurrentClicks(this, byShift);
	}

	@Override
	public void onTick() {
		if (getLastTickTime() + getPeriod() >= k) {
			cancel(false);
		}
	}


	public String getCurrent() {
		return combination == null ? "" : combination.toString();
	}

	public IActiveCharacter getCharacter() {
		return character;
	}


	@Override
	public IEffectContainer constructEffectContainer() {
		return this;
	}


	@Override
	public Set<ClickComboActionEvent> getEffects() {
		return new HashSet<>(Collections.singletonList(this));
	}

	@Override
	public Object getStackedValue() {
		return null;
	}

	@Override
	public void setStackedValue(Object o) {

	}

	public boolean hasStarted() {
		return combination != null;
	}
}

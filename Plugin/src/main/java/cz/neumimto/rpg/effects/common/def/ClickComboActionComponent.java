package cz.neumimto.rpg.effects.common.def;

import cz.neumimto.rpg.NtRpgPlugin;
import static cz.neumimto.rpg.NtRpgPlugin.pluginConfig;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.Generate;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 28.8.2017.
 */
@Generate(id = "name", description = "A component which enables click-combos")
public class ClickComboActionComponent extends EffectBase implements IEffectContainer {

	public static final String name = "ClickCombos";

	StringBuilder combination;

	private long k = 0;

	private IActiveCharacter character;

	@Generate.Constructor
	public ClickComboActionComponent(IEffectConsumer t, long duration, Void literallyNothing) {
		this(t);
	}

	public ClickComboActionComponent(IEffectConsumer t) {
		super(name, t);
		character = (IActiveCharacter) t;
		setPeriod(pluginConfig.CLICK_COMBO_MAX_INVERVAL_BETWEEN_ACTIONS);
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
		if (pluginConfig.SHIFT_CANCELS_COMBO) {
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
		boolean exec = false;
		if (combination != null) {
			ExtendedSkillInfo skill = NtRpgPlugin.GlobalScope.skillService.invokeSkillByCombo(getCurrent(), character);
			if (skill != null) {
				Gui.skillExecution(character, skill);
				combination = null;
				exec = true;
			}
		}
		k = System.currentTimeMillis() + 2000L;
		if (!exec) {
			Gui.displayCurrentClicks(character, getCurrent());
		}
	}

	public void cancel(boolean byShift) {
		combination = null;
		Gui.resetCurrentClicks(this, byShift);
	}

	@Override
	public void onTick() {
		if (combination != null && getLastTickTime() + getPeriod() >= k) {
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
	public Set<ClickComboActionComponent> getEffects() {
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

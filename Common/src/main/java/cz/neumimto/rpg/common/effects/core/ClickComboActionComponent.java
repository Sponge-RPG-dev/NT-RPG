package cz.neumimto.rpg.common.effects.core;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.configuration.PluginConfig;
import cz.neumimto.rpg.common.effects.EffectBase;
import cz.neumimto.rpg.common.effects.Generate;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.effects.IEffectContainer;
import cz.neumimto.rpg.common.entity.IEffectConsumer;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.gui.Gui;
import cz.neumimto.rpg.common.localization.LocalizationKeys;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by NeumimTo on 28.8.2017.
 */
@Generate(id = "name", description = "A component which enables click-combos")
public class ClickComboActionComponent extends EffectBase implements IEffectContainer {

    public static final String name = "ClickCombos";

    private StringBuilder combination;

    private long k = 0;

    private boolean notifyIfCancelled;

    private ActiveCharacter character;

    private long lastTimeUsed;

    private byte length;

    private static long MIN_DELAY = 125L;

    @Generate.Constructor
    public ClickComboActionComponent(IEffectConsumer t) {
        this((ActiveCharacter) t);
    }

    private ClickComboActionComponent(ActiveCharacter t) {
        super(name, t);
        character = t;
        PluginConfig pluginConfig = Rpg.get().getPluginConfig();
        setPeriod(pluginConfig.CLICK_COMBO_MAX_INVERVAL_BETWEEN_ACTIONS);
        setDuration(-1L);
    }

    public void processRMB() {
        if (!hasStarted()) {
            combination = new StringBuilder();
        }
        PluginConfig pluginConfig = Rpg.get().getPluginConfig();
        if (lastTimeUsed > System.currentTimeMillis() || length >= pluginConfig.MAX_CLICK_COMBO_LENGTH) {
            return;
        }
        combination.append('R');
        length++;
        update();
    }

    public void processLMB() {
        PluginConfig pluginConfig = Rpg.get().getPluginConfig();
        if (lastTimeUsed > System.currentTimeMillis() || length >= pluginConfig.MAX_CLICK_COMBO_LENGTH) {
            return;
        }
        combination.append('L');
        length++;
        update();
    }

    public void processShift() {
        PluginConfig pluginConfig = Rpg.get().getPluginConfig();
        if (pluginConfig.SHIFT_CANCELS_COMBO) {
            cancel(true);
        } else {
            if (lastTimeUsed > System.currentTimeMillis() || length >= pluginConfig.MAX_CLICK_COMBO_LENGTH) {
                return;
            }
            combination.append('S');
            length++;
        }
        update();
    }

    public void processQ() {
        PluginConfig pluginConfig = Rpg.get().getPluginConfig();
        if (lastTimeUsed > System.currentTimeMillis() || length >= pluginConfig.MAX_CLICK_COMBO_LENGTH) {
            return;
        }
        combination.append('Q');
        length++;
        update();
    }

    public void processE() {
        PluginConfig pluginConfig = Rpg.get().getPluginConfig();
        if (lastTimeUsed > System.currentTimeMillis() || length >= pluginConfig.MAX_CLICK_COMBO_LENGTH) {
            return;
        }
        combination.append('E');
        length++;
        update();
    }

    public void update() {
        notifyIfCancelled = true;
        boolean exec = false;
        if (combination != null) {
            PlayerSkillContext skill = Rpg.get().getSkillService().invokeSkillByCombo(getCurrent(), character);
            if (skill != null) {
                Gui.skillExecution(character, skill);
                combination = null;
                exec = true;
            }
        }
        long delta = System.currentTimeMillis();
        lastTimeUsed = delta + MIN_DELAY;
        if (k <= +delta + 2000L) {
            k = delta + 2000L;
        }
        if (!exec) {
            Gui.displayCurrentClicks(character, getCurrent());
        }
    }

    public void cancel(boolean byShift) {
        length = 0;
        combination = null;
        resetCurrentClicks();
        notifyIfCancelled = false;
    }

    @Override
    public void onTick(IEffect self) {
        if (notifyIfCancelled && getLastTickTime() + getPeriod() >= k) {
            cancel(false);
        }
    }

    public String getCurrent() {
        return combination == null ? "" : combination.toString();
    }

    public ActiveCharacter getCharacter() {
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

    public static void resetCurrentClicks() {
        Rpg.get().getLocalizationService().translate(LocalizationKeys.CANCELLED);
    }

}

package cz.neumimto.effects.common.def;

import cz.neumimto.NtRpgPlugin;
import cz.neumimto.configuration.PluginConfig;
import cz.neumimto.effects.EffectBase;
import cz.neumimto.effects.EffectSource;
import cz.neumimto.effects.EffectStatusType;
import cz.neumimto.events.ManaRegainEvent;
import cz.neumimto.gui.Gui;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.players.properties.DefaultProperties;

/**
 * Created by NeumimTo on 9.8.2015.
 */
public class ManaRegeneration extends EffectBase {

    IActiveCharacter character;
    private static final String apply = "Your mana is regenerating.";
    private static final String remove = "You've lost mana regenartion.";
    public static final String name = "DefaultRegen";

    public ManaRegeneration(IActiveCharacter character) {
        super(name, character);
        this.character = character;
        setPeriod(PluginConfig.MANA_REGENERATION_RATE);
        setApplyMessage(apply);
        setExpireMessage(remove);
        setDuration(-1);
    }


    @Override
    public void onApply() {
        Gui.sendEffectStatus(character, EffectStatusType.APPLIED, this);
    }

    @Override
    public void onRemove() {
        Gui.sendEffectStatus(character, EffectStatusType.EXPIRED, this);
    }

    @Override
    public void onTick() {
        double current = character.getMana().getValue();
        double max = character.getMana().getMaxValue();
        if (current == max)
            return;
        double regen = character.getMana().getRegen()
                + character.getCharacterProperty(DefaultProperties.mana_regen_mult) * character.getLevel();
        current += regen;
        ManaRegainEvent event = new ManaRegainEvent(character);
        if (current > max) {
            event.setNewVal(max);
            event.setAmount(max - current);
        } else {
            event.setNewVal(current);
            event.setAmount(regen);
        }
        NtRpgPlugin.GlobalScope.game.getEventManager().post(event);
        event.getCharacter().getMana().setValue(event.getNewVal());
    }

    @Override
    public int getLevel() {
        return 1;
    }

    @Override
    public void setLevel(int level) {

    }

    @Override
    public boolean isStackable() {
        return false;
    }

    @Override
    public boolean setStackable(boolean b) {
        return false;
    }

    @Override
    public boolean requiresRegister() {
        return true;
    }

    @Override
    public EffectSource getEffectSource() {
        return EffectSource.DEFAULT;
    }


}

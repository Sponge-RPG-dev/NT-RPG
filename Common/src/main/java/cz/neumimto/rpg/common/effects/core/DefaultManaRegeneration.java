package cz.neumimto.rpg.common.effects.core;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.EffectStatusType;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.CommonProperties;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.events.character.CharacterManaRegainEvent;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.common.effects.CoreEffectTypes;

/**
 * Created by NeumimTo on 9.8.2015.
 */
@Generate(id = "name", description = "A component which enables mana regeneration")
public class DefaultManaRegeneration extends EffectBase {

    public static final String name = "DefaultManaRegen";
    private IActiveCharacter character;

    public DefaultManaRegeneration(IEffectConsumer character) {
        super(name, character);
        this.character = (IActiveCharacter) character;
        setPeriod(Rpg.get().getPluginConfig().MANA_REGENERATION_RATE);
        setDuration(-1);
        addEffectType(CoreEffectTypes.MANA_REGEN);
    }

    @Override
    public IEffectConsumer getConsumer() {
        return character;
    }

    @Override
    public void onApply(IEffect self) {
        Gui.sendEffectStatus(character, EffectStatusType.APPLIED, this);
    }

    @Override
    public void onRemove(IEffect self) {
        Gui.sendEffectStatus(character, EffectStatusType.EXPIRED, this);
    }

    @Override
    public void onTick(IEffect self) {
        double current = character.getMana().getValue();
        double max = character.getMana().getMaxValue();
        if (current >= max) {
            return;
        }
        double regen = character.getMana().getRegen()
                * Rpg.get().getEntityService().getEntityProperty(character, CommonProperties.mana_regen_mult);

        CharacterManaRegainEvent event = Rpg.get().getEventFactory().createEventInstance(CharacterManaRegainEvent.class);
        event.setTarget(character);
        event.setAmount(regen);
        event.setSource(this);

        if (Rpg.get().postEvent(event)) return;

        current += event.getAmount();
        if (current > max) current = max;

        event.getTarget().getMana().setValue(current);
        Gui.displayMana(character);
    }

    @Override
    public boolean isStackable() {
        return false;
    }

    @Override
    public boolean requiresRegister() {
        return true;
    }

}

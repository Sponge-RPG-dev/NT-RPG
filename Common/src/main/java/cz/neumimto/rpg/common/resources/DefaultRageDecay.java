package cz.neumimto.rpg.common.resources;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.effects.*;
import cz.neumimto.rpg.common.entity.IEffectConsumer;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.gui.Gui;

/**
 * Created by NeumimTo on 9.8.2015.
 */
@Generate(id = "name", description = "A component which enables rage regeneration")
public class DefaultRageDecay extends EffectBase {

    public static final String name = "DefaultRageRegen";
    private IActiveCharacter character;

    public DefaultRageDecay(IEffectConsumer character) {
        super(name, character);
        this.character = (IActiveCharacter) character;
        setPeriod(Rpg.get().getPluginConfig().RAGE_DECAY_RATE);
        setDuration(-1);
        addEffectType(CoreEffectTypes.RAGE_DECAY);
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
        Resource rage = character.getResource(ResourceService.rage);
        double current = rage.getValue();
        double max = rage.getMaxValue();
        if (current <= 0) {
            return;
        }
        double regen = rage.getTickChange();

        CharacterRageRegainEvent event = Rpg.get().getEventFactory().createEventInstance(CharacterRageRegainEvent.class);
        event.setTarget(character);
        event.setAmount(regen);
        event.setSource(this);

        if (Rpg.get().postEvent(event)) return;

        current -= event.getAmount();
        if (current < 0) current = 0;

        event.getTarget().getResource(ResourceService.rage).setValue(current);
        Gui.displayRage(character);
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

package cz.neumimto.rpg.common.resources;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.effects.*;
import cz.neumimto.rpg.common.entity.IEffectConsumer;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.events.character.CharacterResourceChangeValueEvent;
import cz.neumimto.rpg.common.gui.Gui;

/**
 * Created by NeumimTo on 9.8.2015.
 */
@Generate(id = "name", description = "A component which enables mana regeneration")
public class DefaultManaRegeneration extends EffectBase {

    public static final String name = "DefaultManaRegen";
    private ActiveCharacter character;

    public DefaultManaRegeneration(IEffectConsumer character) {
        super(name, character);
        this.character = (ActiveCharacter) character;
        setPeriod(Rpg.get().getResourceService()
                .getRegistry()
                .stream()
                .filter(a -> a.name.equalsIgnoreCase(ResourceService.mana))
                .findFirst().get().regenRate);
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
        Resource mana = character.getResource(ResourceService.mana);
        if (mana == null) {
            return;
        }
        double current = mana.getValue();
        double max = mana.getMaxValue();
        if (current >= max) {
            return;
        }
        double regen = mana.getTickChange();

        CharacterResourceChangeValueEvent event = Rpg.get().getEventFactory().createEventInstance(CharacterResourceChangeValueEvent.class);
        event.setTarget(character);
        event.setAmount(regen);
        event.setSource(this);
        event.setType(ResourceService.mana);

        if (Rpg.get().postEvent(event)) return;

        current += event.getAmount();
        if (current > max) current = max;

        Resource resource = event.getTarget().getResource(event.getType());
        resource.setValue(current);
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

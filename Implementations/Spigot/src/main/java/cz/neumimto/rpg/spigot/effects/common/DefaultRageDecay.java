package cz.neumimto.rpg.spigot.effects.common;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.effects.*;
import cz.neumimto.rpg.common.entity.IEffectConsumer;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.events.character.CharacterResourceChangeValueEvent;
import cz.neumimto.rpg.common.gui.Gui;
import cz.neumimto.rpg.common.resources.Resource;
import cz.neumimto.rpg.common.resources.ResourceService;
import cz.neumimto.rpg.spigot.effects.common.model.DefaultRageDecayModel;

/**
 * Created by NeumimTo on 9.8.2015.
 */
@Generate(id = "name", description = "A component which enables rage decay")
public class DefaultRageDecay extends UnstackableEffectBase<DefaultRageDecayModel> {

    public static final String name = "DefaultRageDecay";
    private IActiveCharacter character;

    @Generate.Constructor
    public DefaultRageDecay(IEffectConsumer character, long duration, @Generate.Model DefaultRageDecayModel model) {
        super(name, character);
        this.character = (IActiveCharacter) character;
        setValue(model);
        setDuration(duration);
        setPeriod(Rpg.get().getResourceService()
                .getRegistry()
                .stream()
                .filter(a -> a.name.equalsIgnoreCase(ResourceService.rage))
                .findFirst().get().regenRate);
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
        if (rage == null) {
            return;
        }
        double current = rage.getValue();
        if (current <= 0 || rage.getMaxValue() == 0) {
            return;
        }
        double regen = rage.getTickChange();

        CharacterResourceChangeValueEvent event = Rpg.get().getEventFactory().createEventInstance(CharacterResourceChangeValueEvent.class);
        event.setTarget(character);
        event.setAmount(regen);
        event.setSource(this);
        event.setType(ResourceService.rage);

        if (Rpg.get().postEvent(event)) return;

        current -= event.getAmount();
        if (current < 0) current = 0;

        Resource resource = event.getTarget().getResource(event.getType());
        resource.setValue(current);
        character.updateResourceUIHandler();
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

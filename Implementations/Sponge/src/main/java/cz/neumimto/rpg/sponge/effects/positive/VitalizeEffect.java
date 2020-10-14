package cz.neumimto.rpg.sponge.effects.positive;


import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.effects.EffectBase;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.IReservable;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.common.entity.players.CharacterMana;
import cz.neumimto.rpg.sponge.model.VitalizeEffectModel;

/**
 * Created by NeumimTo on 16.9.2018.
 */
@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "Periodically heals the target and recharges mana")
public class VitalizeEffect extends EffectBase<VitalizeEffectModel> {

    public static final String name = "Vitalize";

    private IActiveCharacter character;

    public VitalizeEffect(IEffectConsumer consumer, long duration, VitalizeEffectModel effectModel) {
        super(name, consumer);
        setValue(effectModel);
        setStackable(false, null);
        if (consumer instanceof IActiveCharacter) {
            character = (IActiveCharacter) consumer;
        }
        setDuration(duration);
        setPeriod(effectModel.period);
    }

    @Override
    public void onTick(IEffect self) {
        Rpg.get().getEntityService().healEntity((IEntity) getConsumer(), getValue().healthPerTick, this);
        if (character != null) {
            IReservable mana = character.getMana();
            if (mana instanceof CharacterMana) {
                Rpg.get().getCharacterService().gainMana(character, getValue().manaPerTick, this);
            }
        }
    }
}

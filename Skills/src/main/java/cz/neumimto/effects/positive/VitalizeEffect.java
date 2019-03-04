package cz.neumimto.effects.positive;

import cz.neumimto.model.VitalizeEffectModel;
import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.Generate;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.IReservable;
import cz.neumimto.rpg.players.Mana;
import cz.neumimto.rpg.scripting.JsBinding;

/**
 * Created by NeumimTo on 16.9.2018.
 */
@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "Periodically heals the target and recharges mana")
public class VitalizeEffect extends EffectBase<VitalizeEffectModel> {

	public static final String name = "Vitalize";

	private IActiveCharacter character;

	public VitalizeEffect(IEffectConsumer consumer, VitalizeEffectModel effectModel) {
		super(name, consumer);
		setValue(effectModel);
		setStackable(false, null);
		if (consumer instanceof IActiveCharacter) {
			character = (IActiveCharacter) consumer;
		}
		setDuration(effectModel.duration);
		setPeriod(effectModel.period);
	}

	@Override
	public void onTick(IEffect self) {
		NtRpgPlugin.GlobalScope.entityService.healEntity((IEntity) getConsumer(), getValue().healthPerTick, this);
		if (character != null) {
			IReservable mana = character.getMana();
			if (mana instanceof Mana) {
				NtRpgPlugin.GlobalScope.characterService.gainMana(character, getValue().manaPerTick, this);
			}
		}
	}
}

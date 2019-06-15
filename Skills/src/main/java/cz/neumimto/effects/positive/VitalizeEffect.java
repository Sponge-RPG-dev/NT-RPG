package cz.neumimto.effects.positive;

import cz.neumimto.model.VitalizeEffectModel;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.IReservable;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.effects.SpongeEffectBase;
import cz.neumimto.rpg.sponge.entities.players.CharacterMana;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;

/**
 * Created by NeumimTo on 16.9.2018.
 */
@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "Periodically heals the target and recharges mana")
public class VitalizeEffect extends SpongeEffectBase<VitalizeEffectModel> {

	public static final String name = "Vitalize";

	private ISpongeCharacter character;

	public VitalizeEffect(IEffectConsumer consumer, long duration, VitalizeEffectModel effectModel) {
		super(name, consumer);
		setValue(effectModel);
		setStackable(false, null);
		if (consumer instanceof IActiveCharacter) {
			character = (ISpongeCharacter) consumer;
		}
		setDuration(duration);
		setPeriod(effectModel.period);
	}

	@Override
	public void onTick(IEffect self) {
		NtRpgPlugin.GlobalScope.entityService.healEntity((IEntity) getConsumer(), getValue().healthPerTick, this);
		if (character != null) {
			IReservable mana = character.getMana();
			if (mana instanceof CharacterMana) {
				NtRpgPlugin.GlobalScope.characterService.gainMana(character, getValue().manaPerTick, this);
			}
		}
	}
}

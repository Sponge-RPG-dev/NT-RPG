package cz.neumimto.effects.common.positive;

import cz.neumimto.configuration.Localization;
import cz.neumimto.effects.EffectBase;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.players.properties.DefaultProperties;

/**
 * Created by NeumimTo on 23.7.2015.
 */
public class SpeedBoost extends EffectBase {

    public static final String name = "Speedboost";

    @Override
    public String getName() {
        return name;
    }

    private float speedbonus;
    private IActiveCharacter character;

    public SpeedBoost(IActiveCharacter consumer, long duration, float speedbonus) {
        super(name, consumer);
        setDuration(duration);
        this.speedbonus = speedbonus;
        character = consumer;

    }

    @Override
    public void onStack(int level) {
        super.onStack(level);
    }

    @Override
    public void onApply() {
        super.onApply();
        character.setCharacterProperty(DefaultProperties.walk_speed, character.getCharacterProperty(DefaultProperties.walk_speed) + speedbonus);
        getGlobalScope().characterService.updateWalkSpeed(character);
        getConsumer().sendMessage(Localization.SPEED_BOOST_APPLY);
    }

    @Override
    public void onRemove() {
        super.onRemove();
        character.setCharacterProperty(DefaultProperties.walk_speed, character.getCharacterProperty(DefaultProperties.walk_speed) - speedbonus);
        getGlobalScope().characterService.updateWalkSpeed(character);
        getConsumer().sendMessage(Localization.SPEED_BOOST_EXPIRE);
    }

    @Override
    public boolean requiresRegister() {
        return true;
    }


}

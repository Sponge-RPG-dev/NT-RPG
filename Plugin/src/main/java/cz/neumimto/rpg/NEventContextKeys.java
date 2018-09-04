package cz.neumimto.rpg;

import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.skills.ISkill;
import org.spongepowered.api.event.cause.EventContextKey;

import java.util.UUID;

/**
 * Created by ja on 16.9.2017.
 */
public class NEventContextKeys {

	public static final EventContextKey<UUID> GAME_PROFILE = EventContextKey
			.builder(UUID.class)
			.name("gameprofile")
			.id("ntrpg.gameprofile")
			.build();

	public static final EventContextKey<ISkill> SKILL = EventContextKey
			.builder(ISkill.class)
			.name("skilldamage")
			.id("ntrpg.skilldamage")
			.build();

	public static final EventContextKey<IEffect> EFFECT = EventContextKey
			.builder(IEffect.class)
			.name("effectdamage")
			.id("ntrpg.effectdamage")
			.build();
}

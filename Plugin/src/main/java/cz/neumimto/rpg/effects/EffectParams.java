package cz.neumimto.rpg.effects;

import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class EffectParams extends HashMap<String, String> {

	public EffectParams() {
	}

	public EffectParams(Map<? extends String, ? extends String> m) {
		super(m);
	}
}

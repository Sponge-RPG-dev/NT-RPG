package cz.neumimto.model;

import cz.neumimto.rpg.effects.model.EffectModelMapper;

import java.util.Map;

public class TestModelMapper extends EffectModelMapper {

	public TestModelMapper(Class<?> type) {
		super(type);
	}

	@Override
	public Object parse(Map<String, String> map) {
		ShadowRunModel model = new ShadowRunModel();
		model.attackmult = Double.parseDouble(map.get("attackmult"));
		return model;
	}
}

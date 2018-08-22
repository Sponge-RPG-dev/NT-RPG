package cz.neumimto.rpg;


import cz.neumimto.rpg.effects.EffectBase;
import cz.neumimto.rpg.effects.Generate;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.skills.NDamageType;
import cz.neumimto.rpg.skills.pipeline.SkillTargetProcessors;
import cz.neumimto.rpg.skills.scripting.SkillActions;
import cz.neumimto.rpg.skills.tree.SkillType;

@Generate(description = "test", id = "name", inject = true)
public class EffectTest extends EffectBase {

	public static IGlobalEffect<EffectTest> global = null;

	public static String name = "testeffect";
	public long l;

	public EffectTest(IEffectConsumer c, long duration, String level) {
		SkillBuilder.create("nt-rpg:id")
				.description("something testing")
				.addSkillTypes(SkillType.AOE, SkillType.LIGHTNING)
				.damageType(NDamageType.LIGHTNING)
				.lore("test")
				.name("test")
				.pipeline()
					.target(SkillTargetProcessors.NEARBY_ENEMIES)
						.action(SkillActions.DAMAGE)
						.action(SkillDecorations.SPAWN_LIGHTNING)
				.end()
				.register();
	}

	public EffectTest() {
		setPeriod(100);
		setDuration(50000);
	}

	@Override
	public void onTick() {
		l++;
	}
}

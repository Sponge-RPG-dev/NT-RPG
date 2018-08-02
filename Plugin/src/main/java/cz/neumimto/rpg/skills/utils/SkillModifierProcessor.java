package cz.neumimto.rpg.skills.utils;

/**
 * Created by ja on 22.10.2016.
 */
public abstract class SkillModifierProcessor<T> {

	public static SkillModifierProcessor<Double> D_STACK = new SkillModifierProcessor<Double>() {
		@Override
		public Double process(Double t1, Double t2) {
			return t1 + t2;
		}
	};

	public static SkillModifierProcessor<Double> D_MAX = new SkillModifierProcessor<Double>() {
		@Override
		public Double process(Double t1, Double t2) {
			return Math.max(t1, t2);
		}
	};

	public static SkillModifierProcessor<Float> F_STACK = new SkillModifierProcessor<Float>() {
		@Override
		public Float process(Float t1, Float t2) {
			return t1 + t2;
		}
	};

	public static SkillModifierProcessor<Float> F_MAX = new SkillModifierProcessor<Float>() {
		@Override
		public Float process(Float t1, Float t2) {
			return Math.max(t1, t2);
		}
	};

	public static SkillModifierProcessor<Long> L_STACK = new SkillModifierProcessor<Long>() {
		@Override
		public Long process(Long t1, Long t2) {
			return t1 + t2;
		}
	};


	public static SkillModifierProcessor<String[]> STRARR_CONCAT = new SkillModifierProcessor<String[]>() {
		@Override
		public String[] process(String[] t1, String[] t2) {
			String[] arr = new String[t1.length + t2.length];
			System.arraycopy(t1, 0, arr, t1.length - 1, t2.length);
			return arr;
		}
	};


	public abstract T process(T t1, T t2);
}

package cz.neumimto.rpg.effects;

/**
 * Created by NeumimTo on 30.3.17.
 */
public class EffectValue<K> {

	private IEffectSourceProvider effectSource;

	private K k;

	public EffectValue(IEffectSourceProvider effectSource, K k) {
		this.effectSource = effectSource;
		this.k = k;
	}

	public K getValue() {
		return k;
	}

	public void setValue(K k) {
		this.k = k;
	}

	public IEffectSourceProvider getEffectSource() {
		return effectSource;
	}

	public void setEffectSource(IEffectSourceProvider effectSource) {
		this.effectSource = effectSource;
	}
}

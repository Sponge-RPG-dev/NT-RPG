/*
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package cz.neumimto.rpg.common.effects;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.api.effects.EffectParams;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.effects.IEffectConsumer;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.effects.IEffectSourceProvider;
import cz.neumimto.rpg.effects.InternalEffectSourceProvider;
import cz.neumimto.rpg.effects.model.EffectModelFactory;
import cz.neumimto.rpg.entities.IEntity;
import cz.neumimto.rpg.players.ActiveCharacter;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.SkillSettings;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Consumer;

import static cz.neumimto.rpg.NtRpgPlugin.pluginConfig;

/**
 * Created by NeumimTo on 17.1.2015.
 */
public abstract class EffectService {

	public static final long TICK_PERIOD = 5L;

	private static final long unlimited_duration = -1;

	@Inject
	protected NtRpgPlugin plugin;

	protected Set<IEffect> effectSet = new HashSet<>();
	protected Set<IEffect> pendingAdditions = new HashSet<>();
	protected Set<IEffect> pendingRemovals = new HashSet<>();
	protected Map<String, IGlobalEffect> globalEffects = new HashMap<>();


	/**
	 * calls effect.onApply and registers if effect requires
	 *
	 * @param effect
	 */
	protected void runEffect(IEffect effect) {
		pendingAdditions.add(effect);
	}

	/**
	 * Puts the effect into the remove queue. onRemove will be called one tick later
	 *
	 * @param effect
	 */
	public void stopEffect(IEffect effect) {
		if (effect.requiresRegister()) {
			pendingRemovals.add(effect);
		}
	}

	public void load() {
		File file1 = new File(NtRpgPlugin.workingDir, "SkillsAndEffects.md");
		if (file1.exists()) {
			file1.delete();
		}

		try {
			String finalString = "";
			file1.createNewFile();
			Asset asset = Sponge.getAssetManager().getAsset(plugin, "templates/Effect.md").get();
			for (Map.Entry<String, IGlobalEffect> effect : globalEffects.entrySet()) {
				String s = asset.readString();
				Class aClass = effect.getValue().asEffectClass();
				if (aClass != null && aClass.isAnnotationPresent(Generate.class)) {
					Generate meta = (Generate) aClass.getAnnotation(Generate.class);
					String description = meta.description();
					String name = effect.getKey();

					Class<?> modelType = EffectModelFactory.getModelType(aClass);

					s = s.replaceAll("\\{\\{effect\\.name}}", name);
					s = s.replaceAll("\\{\\{effect\\.description}}", description);

					if (EffectModelFactory.typeMappers.containsKey(modelType)) {
						s = s.replaceAll("\\{\\{effect\\.parameter}}", modelType.getSimpleName());
						s = s.replaceAll("\\{\\{effect\\.parameters}}", "");
					} else {
						Field[] fields = modelType.getFields();
						s = s.replaceAll("\\{\\{effect\\.parameter}}", "");
						StringBuilder buffer = new StringBuilder();
						for (Field field : fields) {
							String fname = field.getName();
							String type = field.getType().getSimpleName();
							buffer.append("   * " + fname + " - " + type + "\n\n");
						}
						s = s.replaceAll("\\{\\{effect\\.parameters}}", buffer.toString());
					}
					finalString += s;
				}
			}

			asset = Sponge.getAssetManager().getAsset(plugin, "templates/Skill.md").get();
			String skills = "";
			for (ISkill iSkill : NtRpgPlugin.GlobalScope.skillService.getAll()) {
				String s = asset.readString();

				DamageType damageType = iSkill.getDamageType();

				s = s.replaceAll("\\{\\{skill\\.damageType}}", damageType == null ? "Deals no damage" : damageType.getName());

				List<Text> description = iSkill.getDescription();
				String desc = "";
				for (Text text : description) {
					desc += text.toPlain();
				}
				s = s.replaceAll("\\{\\{skill\\.description}}", desc);

				String id = iSkill.getId();
				s = s.replaceAll("\\{\\{skill\\.id}}", id);


				s = s.replaceAll("\\{\\{skill\\.name}}", iSkill.getName());

				SkillSettings defaultSkillSettings = iSkill.getDefaultSkillSettings();

				StringBuilder buffer = new StringBuilder();
				for (Map.Entry<String, Float> stringFloatEntry : defaultSkillSettings.getNodes().entrySet()) {
					buffer.append("   * " + stringFloatEntry.getKey() + "\n\n");
				}
				s = s.replaceAll("\\{\\{skill\\.parameters}}", buffer.toString());
				skills += s;
			}
			asset = Sponge.getAssetManager().getAsset(plugin, "templates/SE.md").get();
			String a = asset.readString();
			Files.write(file1.toPath(), a.replaceAll("\\{\\{effects}}", finalString)
					.replaceAll("\\{\\{skills}}", skills).getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}


		startEffectScheduler();
	}

	public abstract void startEffectScheduler();


	public void schedule() {
		for (IEffect pendingRemoval : pendingRemovals) {
			removeEffectContainer(pendingRemoval.getEffectContainer(), pendingRemoval, pendingRemoval.getConsumer());
			effectSet.remove(pendingRemoval);
		}

		pendingRemovals.clear();
		long l = System.currentTimeMillis();
		for (IEffect e : effectSet) {
			if (e.getConsumer().isDetached()) {
				pendingRemovals.add(e);
				continue;
			}
			if ((e.getPeriod() > 0 && !e.isTickingDisabled()) && e.getPeriod() + e.getLastTickTime() <= l) {
				tickEffect(e, l);
			}

			if (e.getDuration() == unlimited_duration) {
				continue;
			}

			if (e.getExpireTime() <= l) {
				removeEffect(e, e.getConsumer());
			}
		}

		effectSet.addAll(pendingAdditions);
		pendingAdditions.clear();
	}

	/**
	 * Calls onTick and increments tickCount
	 *
	 * @param effect
	 */
	public void tickEffect(IEffect effect, long time) {
		effect.onTick(effect);
		effect.setLastTickTime(time);
	}

	/**
	 * Adds effect to the consumer,
	 * Effects requiring register are registered into the scheduler one tick later
	 *
	 * @param effect effect
	 *
	 * @return true if effect is successfully applied
	 */
	public <T extends IEffect> boolean addEffect(T effect) {
		return addEffect(effect, InternalEffectSourceProvider.INSTANCE);
	}

	/**
	 * Adds effect to the consumer,
	 * Effects requiring register are registered into the scheduler one tick later
	 *
	 * @param effect effect
	 * @param effectSourceProvider source
	 *
	 * @return true if effect is successfully applied
	 */
	public <T extends IEffect> boolean addEffect(T effect, IEffectSourceProvider effectSourceProvider) {
		return addEffect(effect, effectSourceProvider, null);
	}

	/**
	 * Adds effect to the consumer,
	 * Effects requiring register are registered into the scheduler one tick later
	 *
	 * @param effect effect
	 * @param effectSourceProvider source
	 * @param entitySource caster of effect
	 *
	 * @return true if effect is successfully applied
	 */
	@SuppressWarnings("unchecked")
	public <T extends IEffect> boolean addEffect(T effect, IEffectSourceProvider effectSourceProvider, IEntity entitySource) {

		IEffectContainer eff = effect.getConsumer().getEffect(effect.getName());
		if (pluginConfig.DEBUG.isDevelop()) {
			IEffectConsumer consumer1 = effect.getConsumer();
			if (consumer1 instanceof ActiveCharacter) {
				ActiveCharacter chara = (ActiveCharacter) consumer1;
				chara.getPlayer().sendMessage(Text.of("Adding effect: " + effect.getName() +
						" container: " + (eff == null ? "null" : eff.getEffects().size()) +
						" provider: " + effectSourceProvider.getType().getClass().getSimpleName()));
			}
		}
		if (eff == null) {
			eff = effect.constructEffectContainer();
			effect.getConsumer().addEffect(eff);
			effect.onApply(effect);
		} else if (eff.isStackable()) {
			eff.stackEffect(effect, effectSourceProvider);
		} else {
			eff.forEach((Consumer<IEffect>) this::stopEffect); //there should be always only one
			//on remove will be called one tick later.
			eff.getEffects().add(effect);
			effect.onApply(effect);
		}

		effect.setEffectContainer(eff);
		if (effect.requiresRegister()) {
			runEffect(effect);
		}

		return true;
	}

	/**
	 * Removes effect from IEffectConsumer, and stops it. The effect will be removed from the scheduler next tick
	 *
	 * @param effect
	 * @param consumer
	 */
	public void removeEffect(IEffect effect, IEffectConsumer consumer) {
		IEffectContainer container = consumer.getEffect(effect.getName());
		if (pluginConfig.DEBUG.isDevelop()) {
			IEffectConsumer consumer1 = effect.getConsumer();
			if (consumer1 instanceof ActiveCharacter) {
				ActiveCharacter chara = (ActiveCharacter) consumer1;
				chara.getPlayer().sendMessage(Text.of("Removing effect: " + effect.getName() +
						" container: " + (container == null ? "null" : container.getEffects().size())));
			}
		}
		if (container != null) {
			removeEffectContainer(container, effect, consumer);
			stopEffect(effect);
		}
	}

	public <T, E extends IEffect<T>> void removeEffectContainer(IEffectContainer<T, E> container, IEffectConsumer consumer) {
		container.forEach(a -> removeEffect(a, consumer));
	}

	protected void removeEffectContainer(IEffectContainer container, IEffect effect, IEffectConsumer consumer) {

		if (effect == container) {
			if (!effect.getConsumer().isDetached()) {
				effect.onRemove(effect);
			}
			if (!consumer.isDetached()) {
				consumer.removeEffect(effect);
			}
		} else if (container.getEffects().contains(effect)) {
			container.removeStack(effect);
			if (container.getEffects().isEmpty()) {
				if (consumer != null) {
					consumer.removeEffect(container);
				}
			} else {
				container.updateStackedValue();
			}
		} else {

		}
	}

	/**
	 * Removes and stops the effect previously applied as item enchantement
	 *
	 * @param iEffect
	 * @param consumer
	 */
	@SuppressWarnings("unchecked")
	public void removeEffect(String iEffect, IEffectConsumer consumer, IEffectSourceProvider effectSource) {
		IEffectContainer effect = consumer.getEffect(iEffect);
		if (effect != null) {
			Iterator<IEffect> iterator = effect.getEffects().iterator();
			IEffect e;
			while (iterator.hasNext()) {
				e = iterator.next();
				if (e.getEffectSourceProvider() == effectSource) {
					removeEffectContainer(effect, e, consumer);
					stopEffect(e);
				}
			}
		}
	}

	/**
	 * Register global effect
	 *
	 * @param iGlobalEffect
	 */
	public void registerGlobalEffect(IGlobalEffect iGlobalEffect) {
		globalEffects.put(iGlobalEffect.getName().toLowerCase(), iGlobalEffect);
	}

	/**
	 * Removes cached globaleffect
	 *
	 * @param name
	 */
	public void removeGlobalEffect(String name) {
		name = name.toLowerCase();
		globalEffects.remove(name);
	}

	/**
	 * Returns global effect by its name, if effect does not exists return null
	 *
	 * @param name
	 * @return effect or null if key is not in the map
	 */
	public IGlobalEffect getGlobalEffect(String name) {
		return globalEffects.get(name.toLowerCase());
	}

	public Map<String, IGlobalEffect> getGlobalEffects() {
		return globalEffects;
	}

	/**
	 * Applies global effect with unlimited duration
	 *
	 * @param effect
	 * @param consumer
	 * @param value
	 */
	public void applyGlobalEffectAsEnchantment(IGlobalEffect effect, IEffectConsumer consumer, Map<String, String> value,
	                                           IEffectSourceProvider effectSourceType) {
		IEffect construct = effect.construct(consumer, unlimited_duration, value);
		addEffect(construct, effectSourceType);
	}

	/**
	 * Applies global effect with unlimited duration
	 *
	 * @param map
	 * @param consumer
	 */
	public void applyGlobalEffectsAsEnchantments(Map<IGlobalEffect, EffectParams> map, IEffectConsumer consumer,
                                                 IEffectSourceProvider effectSourceType) {
		map.forEach((e, l) ->
				applyGlobalEffectAsEnchantment(e, consumer, l, effectSourceType)
		);
	}


	public void removeGlobalEffectsAsEnchantments(Collection<IGlobalEffect> itemEffects, IActiveCharacter character,
	                                              IEffectSourceProvider effectSourceProvider) {
		if (pluginConfig.DEBUG.isDevelop()) {
			character.sendMessage(Text.of(itemEffects.size() + " added echn. effect to remove queue."));
		}
		itemEffects.forEach((e) -> {
			removeEffect(e.getName(), character, effectSourceProvider);
		});
	}


	public boolean isGlobalEffect(String s) {
		return globalEffects.containsKey(s.toLowerCase());
	}

	@SuppressWarnings("unchecked")
	/**
	 * Called only in cases when entities dies, or players logs off
	 */
	public void removeAllEffects(IEffectConsumer<?> character) {
		Iterator<IEffectContainer<Object, IEffect<Object>>> iterator1 = character.getEffects().iterator();
		while (iterator1.hasNext()) {
			IEffectContainer<Object, IEffect<Object>> next = iterator1.next();
			Iterator<IEffect<Object>> iterator2 = next.getEffects().iterator();
			while (iterator2.hasNext()) {
				IEffect<Object> next1 = iterator2.next();
				next1.setConsumer(null);
				pendingRemovals.add(next1);
				iterator2.remove();
			}
			iterator1.remove();
		}
	}

	public abstract void stopEffectScheduler();

	public void purgeEffectCache() {
		effectSet.clear();
		pendingAdditions.clear();
		pendingRemovals.clear();
	}


	public Map<IGlobalEffect, EffectParams> parseItemEffects(Map<String, EffectParams> stringEffectParamsMap) {
		Map<IGlobalEffect, EffectParams> map = new HashMap<>();
		for (Map.Entry<String, EffectParams> w : stringEffectParamsMap.entrySet()) {
			IGlobalEffect globalEffect = getGlobalEffect(w.getKey());
			if (globalEffect != null) {
				map.put(globalEffect, w.getValue());
			}
		}
		return map;
	}
}



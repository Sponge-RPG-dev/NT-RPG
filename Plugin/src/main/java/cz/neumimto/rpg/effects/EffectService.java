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

package cz.neumimto.rpg.effects;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.effects.model.EffectModelFactory;
import cz.neumimto.rpg.players.ActiveCharacter;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.SkillSettings;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static cz.neumimto.rpg.NtRpgPlugin.pluginConfig;

/**
 * Created by NeumimTo on 17.1.2015.
 */
@Singleton
@ResourceLoader.ListenerClass
public class EffectService {

	public static final long TICK_PERIOD = 5L;

	private static final long unlimited_duration = -1;

	@Inject
	private Game game;

	@Inject
	private NtRpgPlugin plugin;

	private Set<IEffect> effectSet = new HashSet<>();
	private Set<IEffect> pendingAdditions = new HashSet<>();
	private Set<IEffect> pendingRemovals = new HashSet<>();
	private Map<String, IGlobalEffect> globalEffects = new HashMap<>();

	private UUID timings;
	private long timingsStart;
	private long timingsTicks;
	private static final long timingsTicksMax = 100;
	private Task effectTask;
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


		start();
	}

	public void start() {
		effectTask = game.getScheduler().createTaskBuilder().name("EffectTask")
				.delay(5L, TimeUnit.MILLISECONDS)
				.interval(TICK_PERIOD, TimeUnit.MILLISECONDS)
				.execute(this::schedule)
				.submit(plugin);
	}


	public void schedule() {
		if (timings != null) {
			timingsTicks++;
		}
		for (IEffect pendingRemoval : pendingRemovals) {
			removeEffectContainer(pendingRemoval.getEffectContainer(), pendingRemoval, pendingRemoval.getConsumer());
			effectSet.remove(pendingRemoval);
		}
		pendingRemovals.clear();
		long l = System.currentTimeMillis();
		for (IEffect e : effectSet) {
			if (e.getConsumer() == null || e.getConsumer().isDetached()) {
				pendingRemovals.add(e);
				continue;
			}
			if (e.getPeriod() + e.getLastTickTime() <= l) {
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
		if (timings != null) {

		}
	}

	/**
	 * Calls onTick and increments tickCount
	 *
	 * @param effect
	 */
	public void tickEffect(IEffect effect, long time) {
		if (!effect.isTickingDisabled()) {
			if (effect.getConsumer().isDetached()) {
				removeEffect(effect.getName(), effect.getConsumer(), effect.getEffectSourceProvider());
				return;
			}
			effect.onTick(effect);
		}
		effect.setLastTickTime(time);
	}

	/**
	 * Adds effect to the consumer,
	 * Effects requiring register are registered into the scheduler one tick later
	 *  @param iEffect
	 *
	 */
	@SuppressWarnings("unchecked")
	public void addEffect(IEffect iEffect, IEffectSourceProvider effectSourceProvider) {
		IEffectContainer eff = iEffect.getConsumer().getEffect(iEffect.getName());
		if (pluginConfig.DEBUG.isDevelop()) {
			IEffectConsumer consumer1 = iEffect.getConsumer();
			if (consumer1 instanceof ActiveCharacter) {
				ActiveCharacter chara = (ActiveCharacter) consumer1;
				chara.getPlayer().sendMessage(Text.of("Adding effect: " + iEffect.getName() +
						" container: " + (eff == null ? "null" : eff.getEffects().size()) + " provider: " + effectSourceProvider.getType().getClass()
						.getSimpleName()));
			}
		}
		if (eff == null) {
			eff = iEffect.constructEffectContainer();
			iEffect.getConsumer().addEffect(eff);
			iEffect.onApply(iEffect);
		} else if (eff.isStackable()) {
			eff.stackEffect(iEffect, effectSourceProvider);
		} else {
			eff.forEach((Consumer<IEffect>) this::stopEffect); //there should be always only one
			//on remove will be called one tick later.
			eff.getEffects().add(iEffect);
			iEffect.onApply(iEffect);
		}

		iEffect.setEffectContainer(eff);
		iEffect.setEffectSourceProvider(effectSourceProvider);
		if (iEffect.requiresRegister()) {
			runEffect(iEffect);
		}
	}

	/**
	 * Removes effect from IEffectConsumer, and stops it. The effect will be removed from the scheduler next tick
	 *
	 * @param iEffect
	 * @param consumer
	 */
	public void removeEffect(IEffect iEffect, IEffectConsumer consumer) {
		IEffectContainer effect = consumer.getEffect(iEffect.getName());
		if (pluginConfig.DEBUG.isDevelop()) {
			IEffectConsumer consumer1 = iEffect.getConsumer();
			if (consumer1 instanceof ActiveCharacter) {
				ActiveCharacter chara = (ActiveCharacter) consumer1;
				chara.getPlayer().sendMessage(Text.of("Adding effect: " + iEffect.getName() +
						" container: " + (effect == null ? "null" : effect.getEffects().size())));
			}
		}
		if (effect != null) {
			removeEffectContainer(effect, iEffect, consumer);
			stopEffect(iEffect);
		}
	}

	public <T, E extends IEffect<T>> void removeEffectContainer(IEffectContainer<T, E> container, IEffectConsumer consumer) {
		container.forEach(a -> removeEffect(a, consumer));
	}

	protected void removeEffectContainer(IEffectContainer container, IEffect iEffect, IEffectConsumer consumer) {
		if (container == null) {
			return;
		}
		if (iEffect == container) {
			if (!iEffect.getConsumer().isDetached()) {
				iEffect.onRemove(iEffect);
			}
			if (!consumer.isDetached()) {
				consumer.removeEffect(iEffect);
			}
		} else if (container.getEffects().contains(iEffect)) {
			container.removeStack(iEffect);
			if (container.getEffects().isEmpty()) {
				if (!consumer.isDetached()) {
					consumer.removeEffect(container);
				}
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
	 * Applies global effects with unlimited duration
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
			character.sendMessage(Text.of(itemEffects.size() + " added echn. effects to remove queue."));
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
				pendingRemovals.add(next1);
				iterator2.remove();
			}
			iterator1.remove();
		}
	}

	public void stop() {
		effectTask.cancel();
	}

	public void purgeEffectCache() {
		effectSet.clear();
		pendingAdditions.clear();
		pendingRemovals.clear();
	}
}



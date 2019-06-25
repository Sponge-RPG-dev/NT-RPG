package cz.neumimto.rpg.sponge.effects;

import cz.neumimto.rpg.api.effects.*;
import cz.neumimto.rpg.api.effects.model.EffectModelFactory;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.SkillSettings;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.sponge.events.effects.SpongeEffectApplyEvent;
import cz.neumimto.rpg.sponge.events.effects.SpongeEffectRemoveEvent;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.scheduler.Task;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Singleton
public class SpongeEffectService extends EffectService {

    @Inject
    private CauseStackManager causeStackManager;

    @Inject
    private Game game;

    @Inject
    private NtRpgPlugin plugin;

    private Task effectTask;

    @Override
    public void startEffectScheduler() {
        effectTask = game.getScheduler().createTaskBuilder().name("EffectTask")
                .delay(5L, TimeUnit.MILLISECONDS)
                .interval(TICK_PERIOD, TimeUnit.MILLISECONDS)
                .execute(this::schedule)
                .submit(plugin);
    }


    @Override
    public void stopEffectScheduler() {
        effectTask.cancel();
    }

    @Override
    protected void removeEffectContainer(IEffectContainer container, IEffect effect, IEffectConsumer consumer) {
        try (CauseStackManager.StackFrame frame = causeStackManager.pushCauseFrame()) {
            SpongeEffectRemoveEvent event = new SpongeEffectRemoveEvent();
            event.setEffect(effect);
            causeStackManager.pushCause(effect);

            event.setCause(causeStackManager.getCurrentCause());
            Sponge.getEventManager().post(event);
        }
        super.removeEffectContainer(container, effect, consumer);
    }

    @Override
    public boolean addEffect(IEffect effect, IEffectSourceProvider effectSourceProvider, IEntity entitySource) {
        effect.setEffectSourceProvider(effectSourceProvider);
        SpongeEffectApplyEvent event = new SpongeEffectApplyEvent();
        event.setEffect(effect);
        try (CauseStackManager.StackFrame frame = causeStackManager.pushCauseFrame()) {
            causeStackManager.pushCause(effect);
            causeStackManager.pushCause(effectSourceProvider);
            if (entitySource != null) {
                causeStackManager.pushCause(entitySource);
            }

            event.setCause(causeStackManager.getCurrentCause());
            if (Sponge.getEventManager().post(event)) {
                return false;
            }
        }
        return super.addEffect(effect, effectSourceProvider, entitySource);
    }

    @Override
    protected boolean mayTick(IEffect e) {
        return !e.getConsumer().isDetached();
    }

    @Override
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
                    } else if (modelType == null) {
                        s = s.replaceAll("\\{\\{effect\\.parameter}}", "");
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

                String damageType = iSkill.getDamageType();

                s = s.replaceAll("\\{\\{skill\\.damageType}}", damageType == null ? "Deals no damage" : damageType);

                List<String> description = iSkill.getDescription();
                StringBuilder desc = new StringBuilder();
                if (description == null) {
                    desc.append("null");
                } else {
                    for (String text : description) {
                        desc.append(text);
                    }
                }
                s = s.replaceAll("\\{\\{skill\\.description}}", desc.toString());

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

    }
}

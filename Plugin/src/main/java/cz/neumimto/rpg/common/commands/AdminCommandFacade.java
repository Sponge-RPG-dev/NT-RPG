package cz.neumimto.rpg.common.commands;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import cz.neumimto.rpg.api.effects.EffectParams;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.effects.model.EffectModelFactory;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.ICharacterService;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.effects.InternalEffectSourceProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class AdminCommandFacade {

    @Inject
    private EffectService effectService;

    @Inject
    private ICharacterService<IActiveCharacter> characterService;

    private Gson gson = new Gson();

    public boolean commandAddEffectToPlayer(String data, IGlobalEffect effect, long duration, IActiveCharacter character) throws CommandProcessingException {
        EffectParams map = new EffectParams();
        Class<?> modelType = EffectModelFactory.getModelType(effect.asEffectClass());
        if (data == null) {
            if (modelType != Void.TYPE)
                throw new CommandProcessingException("Effect data expected! Use ? as data to list parameters");
        } else {
            if (data.equals("?")) {
                if (modelType == Void.TYPE) {
                    Log.error("No data expected");
                    return false;
                } else if (Number.class.isAssignableFrom(modelType) || modelType.isPrimitive()) {
                    Log.error("Expected: " + modelType.getTypeName());
                    return false;
                } else {
                    Map<String, String> q = new HashMap<>();
                    for (Field field : modelType.getFields()) {
                        q.put(field.getName(), field.getType().getName());
                    }
                    Log.error("Expected: " + gson.toJson(q));
                    return false;
                }
            }
            if (modelType == Void.TYPE) {
                //Just do nothing
            } else if (Number.class.isAssignableFrom(modelType) || modelType.isPrimitive()) {
                map.put("value", data);
            } else try {
                //Get rid of unused entries in data string and check for missing
                EffectParams tempMap = gson.fromJson(data, EffectParams.class);
                for (Field field : modelType.getFields()) {
                    if (Modifier.isTransient(field.getModifiers())) continue;
                    if (!tempMap.containsKey(field.getName())) {
                        throw new CommandProcessingException("Missing parameter: " + field.getName());
                    }
                    map.put(field.getName(), tempMap.get(field.getName()));
                }
            } catch (JsonSyntaxException e) {
                Map<String, String> q = new HashMap<>();
                for (Field field : modelType.getFields()) {
                    q.put(field.getName(), field.getType().getName());
                }
                throw new CommandProcessingException("Expected: " + gson.toJson(q));
            }
        }

        if (effectService.addEffect(effect.construct(character, duration, map), InternalEffectSourceProvider.INSTANCE)) {
            Log.info("Effect " + effect.getName() + " applied to player " + character.getUUID());
            return true;
        }
        return false;
    }

    public boolean commandAddExperiences(IActiveCharacter character, Double amount, ClassDefinition classDefinition, String expSource) throws CommandProcessingException{
        Collection<PlayerClassData> classes = character.getClasses().values();

        if (classDefinition != null) {
            classes.stream()
                    .filter(PlayerClassData::takesExp)
                    .filter(c -> c.getClassDefinition().getName().equalsIgnoreCase(classDefinition.getName()))
                    .forEach(c -> characterService.addExperiences(character, amount, c));
        } else if (expSource != null) {
            characterService.addExperiences(character, amount, expSource);
        } else {
            throw new CommandProcessingException("Specify class or experience source!");
        }
        return true;
    }
}

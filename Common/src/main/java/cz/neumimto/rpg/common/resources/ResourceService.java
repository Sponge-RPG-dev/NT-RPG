package cz.neumimto.rpg.common.resources;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.entity.AbstractMob;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.entity.players.classes.ClassResource;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class ResourceService {

    public static final String mana = "mana";
    public static final String rage = "rage";

    public static final String health = "health";
    public static final String stamina = "stamina";

    protected Map<String, FactoryResource> registry = new HashMap<>();

    protected Map<String, UiResourceTracker> guiRegistry = new HashMap<>();

    public ResourceService() {
        registry.put(mana, (character) -> new Resource(mana));
        registry.put(rage, (character) -> new Resource(rage));
        registry.put(health, this::getHpTracker);
   //     registry.put(stamina, this::getStaminaTracker);


    }

    public void reload() {

    }

    public Map<String, FactoryResource> getRegistry() {
        return registry;
    }

    public Map<String, UiResourceTracker> getGuiRegistry() {
        return guiRegistry;
    }

    protected abstract Resource getHpTracker(IActiveCharacter character);

    protected abstract Resource getStaminaTracker(IActiveCharacter character);

    public void removeResource(ActiveCharacter activeCharacter, ClassDefinition klass) {
        Set<ClassResource> classResources = klass.getClassResources();
        if (classResources == null) {
            return;
        }
        for (ClassResource classResource : classResources) {
            removeResource(activeCharacter, classResource, klass.getName());
        }
    }

    public void addResource(ActiveCharacter activeCharacter, ClassDefinition klass) {
        Set<ClassResource> classResources = klass.getClassResources();
        if (classResources == null) {
            return;
        }
        for (ClassResource classResource : classResources) {
            addResource(activeCharacter, classResource, klass.getName());
        }
    }

    public void removeResource(IActiveCharacter activeCharacter, ClassResource classResource, String source) {
        Resource resource = activeCharacter.getResource(classResource.type);
        resource.setMaxValue(source, 0);
        if (resource.getValue() > resource.getMaxValue()) {
            resource.setValue(resource.getMaxValue());
        }
        resource.setTickChange(classResource.type, classResource.tickChange);
    }

    public void addResource(IActiveCharacter activeCharacter, ClassResource classResource, String source) {
        Resource resource = activeCharacter.getResource(classResource.type);
        resource.setMaxValue(source, classResource.baseValue);
        if (resource.getValue() > resource.getMaxValue()) {
            resource.setValue(resource.getMaxValue());
        }
        resource.setTickChange(classResource.type, classResource.tickChange);
    }

    public void initializeForPlayer(IActiveCharacter activeCharacter) {
        for (Map.Entry<String, FactoryResource> e : registry.entrySet()) {
            activeCharacter.addResource(e.getKey(), e.getValue().createFor(activeCharacter));
        }
    }

    public abstract void notifyChange(IActiveCharacter character, Resource resource);

    public abstract Resource initializeForAi(AbstractMob mob);

}

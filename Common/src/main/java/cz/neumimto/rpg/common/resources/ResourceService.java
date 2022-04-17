package cz.neumimto.rpg.common.resources;

import cz.neumimto.rpg.common.entity.AbstractMob;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.entity.players.classes.ClassResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ResourceService {

    public static final String mana = "mana";
    public static final String rage = "rage";

    public static final String health = "health";
    public static final String stamina = "stamina";

    protected Map<String, FactoryResource> registry = new HashMap<>();
    protected Map<String, FactoryResource> vanillaRegistry = new HashMap<>();

    public ResourceService() {
        registry.put(mana, (character) -> new Resource(mana));
        registry.put(rage, (character) -> new Resource(rage));
        vanillaRegistry.put(health, this::getHpTracker);
        vanillaRegistry.put(stamina, this::getStaminaTracker);
    }

    protected abstract Resource getHpTracker(IActiveCharacter character);

    protected abstract Resource getStaminaTracker(IActiveCharacter character);


    public void removeResource(ActiveCharacter activeCharacter, ClassDefinition klass) {
        List<ClassResource> classResources = klass.getClassResources();
        if (classResources == null) {
            return;
        }
        for (ClassResource classResource : classResources) {
            Resource resource = activeCharacter.getResource(classResource.name);
            if (resource == null) {
                continue;
            }
            resource.setMaxValue(klass.getName(), 0);
            if (resource.getValue() > resource.getMaxValue()) {
                resource.setValue(resource.getMaxValue());
            }
            resource.setTickChange(classResource.name, classResource.tickChange);
        }
    }

    public void addResource(ActiveCharacter activeCharacter, ClassDefinition klass) {
        List<ClassResource> classResources = klass.getClassResources();
        if (classResources == null) {
            return;
        }
        for (ClassResource classResource : classResources) {
            Resource resource = activeCharacter.getResource(classResource.name);
            if (resource == null) {
                resource = registry.get(classResource.name).createFor(activeCharacter);
            }
            resource.setMaxValue(klass.getName(), classResource.baseValue);
            if (resource.getValue() > resource.getMaxValue()) {
                resource.setValue(resource.getMaxValue());
            }
            resource.setTickChange(classResource.name, classResource.tickChange);
        }
    }

    public void initializeResources(ActiveCharacter character) {

    }

    public abstract Resource initializeForAi(AbstractMob mob);
}

package cz.neumimto.rpg.common.resources;

import cz.neumimto.rpg.common.entity.AbstractMob;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.entity.players.classes.ClassResource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class ResourceService {

    public static final String mana = "mana";
    public static final String rage = "rage";

    public static final String health = "health";
    public static final String stamina = "stamina";
    protected Set<ResourceDefinition> registry = new HashSet<>();

    public void reload() {

    }

    public Set<ResourceDefinition> getRegistry() {
        return registry;
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
        resource.setTickChange(classResource.type, 0);
        if (resource.getMaxValue() == 0) {
            activeCharacter.removeResource(classResource.type);
            return;
        }
        if (resource.getValue() > resource.getMaxValue()) {
            resource.setValue(resource.getMaxValue());
        }
    }

    protected Resource fromDefinition(ResourceDefinition resourceDefinition) {
        return new Resource(resourceDefinition.name, resourceDefinition);
    }

    public void addResource(IActiveCharacter activeCharacter, ClassResource classResource, String source) {
        Resource resource = activeCharacter.getResource(classResource.type);
        if (resource == null) {
            resource = new Resource()
            resource = registry.get(classResource.type).createFor(activeCharacter);
            activeCharacter.addResource(classResource.type, resource);
        }
        resource.setMaxValue(source, resource.getMaxValue() + classResource.baseValue);
        resource.setTickChange(classResource.type, classResource.tickChange);
        if (resource.getValue() > resource.getMaxValue()) {
            resource.setValue(resource.getMaxValue());
        }
    }

    public void initializeForPlayer(IActiveCharacter activeCharacter) {
       activeCharacter.addResource(health, getHpTracker(activeCharacter));
       activeCharacter.addResource(rage, getHpTracker(activeCharacter));

        for (Map.Entry<String, FactoryResource> e : registry.entrySet()) {
            activeCharacter.addResource(e.getKey(), e.getValue().createFor(activeCharacter));
        }
    }

    public abstract Resource initializeForAi(AbstractMob mob);

}

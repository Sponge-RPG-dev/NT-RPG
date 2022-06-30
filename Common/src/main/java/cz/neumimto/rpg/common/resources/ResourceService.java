package cz.neumimto.rpg.common.resources;

import cz.neumimto.rpg.common.entity.AbstractMob;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.entity.players.classes.ClassResource;

import java.util.*;

public abstract class ResourceService {

    public static final String mana = "mana";
    public static final String rage = "rage";

    public static final String health = "health";
    public static final String stamina = "stamina";
    protected Set<ResourceDefinition> registry = new HashSet<>();

    public void reload() {}

    public Set<ResourceDefinition> getRegistry() {
        return registry;
    }

    protected abstract Resource getHpTracker(IActiveCharacter character, ResourceDefinition resourceDefinition);

    protected abstract Resource getStaminaTracker(IActiveCharacter character, ResourceDefinition resourceDefinition);

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

    public void addResource(IActiveCharacter activeCharacter, ClassResource classResource, String source) {
        Resource resource = activeCharacter.getResource(classResource.type);
        if (resource == null) {
            ResourceDefinition def = registry.stream().filter(a -> a.type.equalsIgnoreCase(classResource.type)).findAny().get();
            resource = new Resource(def);
            activeCharacter.addResource(classResource.type, resource);
        }
        resource.setMaxValue(source, resource.getMaxValue() + classResource.baseValue);
        resource.setTickChange(classResource.type, classResource.tickChange);
        if (resource.getValue() > resource.getMaxValue()) {
            resource.setValue(resource.getMaxValue());
        }
    }

    public void initializeForPlayer(IActiveCharacter activeCharacter) {
        for (ResourceDefinition resourceDefinition : registry) {
            if (resourceDefinition.type.equalsIgnoreCase(health)) {
                activeCharacter.addResource(health, getHpTracker(activeCharacter, resourceDefinition));
            } else if (resourceDefinition.type.equalsIgnoreCase(stamina)) {
                activeCharacter.addResource(stamina, getStaminaTracker(activeCharacter, resourceDefinition));
            } else {
                activeCharacter.addResource(resourceDefinition.name, new Resource(resourceDefinition));
            }
        }
    }

    public abstract Resource initializeForAi(AbstractMob mob);

}

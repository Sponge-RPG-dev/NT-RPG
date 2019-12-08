package cz.neumimto.rpg.api;

import com.google.inject.Module;
import cz.neumimto.persistence.TestHelper;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.configuration.PluginConfig;
import cz.neumimto.rpg.api.damage.DamageService;
import cz.neumimto.rpg.api.effects.EffectService;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.entity.PropertyService;
import cz.neumimto.rpg.api.entity.players.CharacterService;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.parties.PartyService;
import cz.neumimto.rpg.api.events.EventFactoryService;
import cz.neumimto.rpg.api.exp.ExperienceService;
import cz.neumimto.rpg.api.inventory.InventoryService;
import cz.neumimto.rpg.api.items.ItemService;
import cz.neumimto.rpg.api.localization.Arg;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.persistance.model.EquipedSlot;
import cz.neumimto.rpg.api.scripting.IScriptEngine;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.api.skills.mods.ActiveSkillPreProcessorWrapper;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class RpgTests implements RpgApi {

    public RpgTests() {
        Rpg.impl = this;
    }

    @Override
    public void broadcastMessage(String message) {

    }

    @Override
    public void broadcastLocalizableMessage(String message, Arg arg) {

    }

    @Override
    public void broadcastLocalizableMessage(String playerLearnedSkillGlobalMessage, String name, String localizableName) {

    }

    @Override
    public String getTextAssetContent(String templateName) {
        return null;
    }

    @Override
    public void executeCommandBatch(Map<String, String> args, List<String> enterCommands) {

    }

    @Override
    public boolean postEvent(Object event) {
        return false;
    }

    @Override
    public void unregisterListeners(Object listener) {

    }

    @Override
    public void registerListeners(Object listener) {

    }

    @Override
    public ItemService getItemService() {
        return null;
    }

    @Override
    public EventFactoryService getEventFactory() {
        return null;
    }

    @Override
    public SkillService getSkillService() {
        return null;
    }

    @Override
    public LocalizationService getLocalizationService() {
        return null;
    }

    @Override
    public PluginConfig getPluginConfig() {
        return null;
    }

    @Override
    public Executor getAsyncExecutor() {
        return null;
    }

    @Override
    public CharacterService getCharacterService() {
        return null;
    }

    @Override
    public EntityService getEntityService() {
        return null;
    }

    @Override
    public DamageService getDamageService() {
        return null;
    }

    @Override
    public PropertyService getPropertyService() {
        return null;
    }

    @Override
    public PartyService getPartyService() {
        return null;
    }

    @Override
    public String getWorkingDirectory() {
        return "./build/tests/tmp/";
    }

    @Override
    public ResourceLoader getResourceLoader() {
        return null;
    }

    @Override
    public ClassService getClassService() {
        return null;
    }

    @Override
    public EffectService getEffectService() {
        return null;
    }

    @Override
    public IScriptEngine getScriptEngine() {
        return null;
    }

    @Override
    public InventoryService getInventoryService() {
        return new InventoryService() {
            @Override
            public void load() {

            }

            @Override
            public void initializeManagedSlots(IActiveCharacter activeCharacter) {

            }

            @Override
            public boolean isManagedInventory(Class aClass, int slotId) {
                return false;
            }

            @Override
            public Set<ActiveSkillPreProcessorWrapper> processItemCost(IActiveCharacter character, PlayerSkillContext info) {
                return null;
            }

            @Override
            public void initializeCharacterInventory(IActiveCharacter character) {

            }

            @Override
            public EquipedSlot createEquipedSlot(String className, int slotId) {
                return new TestHelper.EquipedSlotImpl(slotId);
            }

            @Override
            public String getItemIconForSkill(ISkill iSkill) {
                return null;
            }
        };
    }

    @Override
    public ExperienceService getExperienceService() {
        return null;
    }

    @Override
    public void reloadMainPluginConfig() {

    }


    @Override
    public void scheduleSyncLater(Runnable runnable) {

    }

    @Override
    public void init(Path workingDirPath, Object commandManager, Class[] commandClasses, RpgAddon defaultStorageImpl,
                     BiFunction<Map, Map<Class<?>, ?>, Module> fnInjProv, Consumer<com.google.inject.Injector> injectorc) {

    }
}

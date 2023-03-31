package cz.neumimto.rpg.common;

import com.google.inject.Injector;
import com.google.inject.Module;
import cz.neumimto.persistence.TestHelper;
import cz.neumimto.rpg.common.classes.ClassService;
import cz.neumimto.rpg.common.configuration.PluginConfig;
import cz.neumimto.rpg.common.damage.DamageService;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.entity.EntityService;
import cz.neumimto.rpg.common.entity.PropertyService;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.entity.players.parties.PartyService;
import cz.neumimto.rpg.common.events.EventFactoryService;
import cz.neumimto.rpg.common.exp.ExperienceService;
import cz.neumimto.rpg.common.inventory.InventoryService;
import cz.neumimto.rpg.common.items.ItemService;
import cz.neumimto.rpg.common.localization.Arg;
import cz.neumimto.rpg.common.localization.LocalizationService;
import cz.neumimto.rpg.common.model.EquipedSlot;
import cz.neumimto.rpg.common.permissions.PermissionService;
import cz.neumimto.rpg.common.resources.ResourceService;
import cz.neumimto.rpg.common.scripting.NTScriptEngine;
import cz.neumimto.rpg.common.skills.SkillService;

import java.nio.file.Path;
import java.util.*;
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
    public void executeCommandAs(UUID sender, Map<String, String> args, List<String> enterCommands) {

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
    public CharacterService<IActiveCharacter> getCharacterService() {
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
    public NTScriptEngine getScriptEngine() {
        return null;
    }

    @Override
    public InventoryService getInventoryService() {
        return new InventoryService() {
            @Override
            public void load() {

            }

            @Override
            public void reload() {

            }

            @Override
            public EquipedSlot createEquipedSlot(String className, int slotId) {
                return new TestHelper.EquipedSlotImpl(slotId);
            }

            @Override
            public void invalidateGUICaches(IActiveCharacter cc) {

            }


        };
    }

    @Override
    public ExperienceService getExperienceService() {
        return null;
    }

    @Override
    public PermissionService getPermissionService() {
        return null;
    }

    @Override
    public Injector getInjector() {
        return null;
    }

    @Override
    public void reloadMainPluginConfig() {

    }


    @Override
    public void scheduleSyncLater(Runnable runnable) {
        runnable.run();
    }

    @Override
    public void scheduleSyncLater(long millis, Runnable runnable) {
        runnable.run();
    }

    @Override
    public void init(Path workingDirPath, Object commandManager, Collection commandClasses, RpgAddon defaultStorageImpl, BiFunction<Map, Map<Class<?>, ?>, Module> fnInjProv, Consumer<Injector> injectorc) {

    }

    @Override
    public Executor getSyncExecutor() {
        return Runnable::run;
    }

    @Override
    public boolean isDisabledInWorld(String worldName) {
        return false;
    }

    @Override
    public Set<UUID> getOnlinePlayers() {
        return null;
    }

    @Override
    public void doImplSpecificreload() {

    }

    @Override
    public String getPlatform() {
        return "Test";
    }

    @Override
    public ResourceService getResourceService() {
        return null;
    }

}

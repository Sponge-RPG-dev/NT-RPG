package cz.neumimto.rpg.common.entity.players;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.effects.IEffectContainer;
import cz.neumimto.rpg.common.entity.EntityHand;
import cz.neumimto.rpg.common.entity.IReservable;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.common.entity.players.party.IParty;
import cz.neumimto.rpg.common.inventory.RpgInventory;
import cz.neumimto.rpg.common.items.RpgItemStack;
import cz.neumimto.rpg.common.items.RpgItemType;
import cz.neumimto.rpg.common.model.CharacterBase;
import cz.neumimto.rpg.common.model.EquipedSlot;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.preprocessors.InterruptableSkillPreprocessor;
import cz.neumimto.rpg.common.skills.tree.SkillTreeSpecialization;
import cz.neumimto.rpg.common.entity.PropertyService;

import java.util.*;


/**
 * Created by NeumimTo on 23.7.2015.
 */
public abstract class PreloadCharacter<T, P extends IParty> implements IActiveCharacter<T, P> {

    private static double[] characterProperties = new double[PropertyService.LAST_ID];
    protected UUID uuid;

    private boolean isusinggui;

    public PreloadCharacter(UUID uuid) {
        this.uuid = uuid;
    }


    @Override
    public void setChanneledSkill(InterruptableSkillPreprocessor o) {

    }

    @Override
    public Optional<InterruptableSkillPreprocessor> getChanneledSkill() {
        return Optional.empty();
    }

    @Override
    public boolean isFriendlyTo(IActiveCharacter character) {
        return false;
    }

    @Override
    public void setCharacterLevelProperty(int index, double value) {

    }

    @Override
    public double[] getSecondaryProperties() {
        return characterProperties;
    }

    @Override
    public void setSecondaryProperties(double[] arr) {

    }

    @Override
    public Map<String, Integer> getTransientAttributes() {
        return null;
    }

    @Override
    public boolean isInvulnerable() {
        return Rpg.get().getPluginConfig().ALLOW_COMBAT_FOR_CHARACTERLESS_PLAYERS;
    }

    @Override
    public void setInvulnerable(boolean b) {

    }

    @Override
    public double getCharacterPropertyWithoutLevel(int index) {
        return 0;
    }

    @Override
    public double getBaseWeaponDamage(RpgItemType type) {
        return 0;
    }

    @Override
    public String getName() {
        return "None";
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public boolean isStub() {
        return true;
    }

    @Override
    public double[] getPrimaryProperties() {
        return characterProperties;
    }

    @Override
    public boolean canUse(RpgItemType weaponItemType, EntityHand type) {
        return false;
    }

    @Override
    public double getWeaponDamage() {
        return 0;
    }

    @Override
    public void setWeaponDamage(double damage) {

    }

    @Override
    public double getArmorValue() {
        return 0;
    }

    @Override
    public void setArmorValue(double value) {

    }

    @Override
    public boolean hasPreferedDamageType() {
        return false;
    }

    @Override
    public String getDamageType() {
        return "none";
    }

    @Override
    public void setDamageType(String damageType) {

    }

    @Override
    public void updateLastKnownLocation(int x, int y, int z, java.lang.String name) {

    }

    @Override
    public Map<java.lang.String, IEffectContainer<Object, IEffect<Object>>> getEffectMap() {
        return Collections.emptyMap();
    }

    @Override
    public double getProperty(int index) {
        return 0;
    }

    @Override
    public void setProperty(int index, double value) {

    }

    @Override
    public IReservable getMana() {
        return null;
    }

    @Override
    public void setMana(IReservable mana) {

    }

    @Override
    public IReservable getHealth() {
        return null;
    }

    @Override
    public void setHealth(IReservable health) {

    }

    @Override
    public int getAttributePoints() {
        return 0;
    }

    @Override
    public void setAttributePoints(int attributePoints) {

    }

    @Override
    public int getAttributeValue(String name) {
        return 0;
    }

    @Override
    public Map<String, Long> getCooldowns() {
        return Collections.emptyMap();
    }

    @Override
    public boolean hasCooldown(String thing) {
        return true;
    }


    @Override
    public Set<RpgItemType> getAllowedArmor() {
        return Collections.emptySet();
    }

    @Override
    public boolean canWear(RpgItemType armor) {
        return false;
    }

    @Override
    public Map<RpgItemType, Double> getAllowedWeapons() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Double> getProjectileDamages() {
        return Collections.emptyMap();
    }

    @Override
    public IActiveCharacter updateItemRestrictions() {
        return this;
    }

    @Override
    public CharacterBase getCharacterBase() {
        return null;
    }

    @Override
    public PlayerClassData getPrimaryClass() {
        return null;
    }

    @Override
    public double getBaseProjectileDamage(String id) {
        return 0;
    }

    @Override
    public Collection<IEffectContainer<Object, IEffect<Object>>> getEffects() {
        return Collections.emptySet();
    }

    @Override
    public boolean hasEffect(String cl) {
        return false;
    }

    @Override
    public void addEffect(IEffect effect) {

    }

    @Override
    public void addEffect(IEffectContainer<Object, IEffect<Object>> IEffectContainer) {

    }

    @Override
    public void removeEffect(String cl) {

    }


    @Override
    public Map<String, PlayerSkillContext> getSkills() {
        return Collections.emptyMap();
    }

    @Override
    public PlayerSkillContext getSkillInfo(ISkill skill) {
        return PlayerSkillContext.EMPTY;
    }

    @Override
    public boolean hasSkill(String name) {
        return false;
    }

    @Override
    public PlayerSkillContext getSkillInfo(String s) {
        return PlayerSkillContext.EMPTY;
    }

    @Override
    public boolean isSilenced() {
        return true;
    }

    @Override
    public void addSkill(String name, PlayerSkillContext info) {

    }

    @Override
    public PlayerSkillContext getSkill(String skillName) {
        return PlayerSkillContext.EMPTY;
    }

    @Override
    public void removeAllSkills() {

    }

    @Override
    public boolean hasParty() {
        return false;
    }

    @Override
    public boolean isInPartyWith(IActiveCharacter character) {
        return false;
    }


    @Override
    public Map<String, PlayerClassData> getClasses() {
        return Collections.EMPTY_MAP;
    }

    @Override
    public P getParty() {
        return null;
    }

    @Override
    public void setParty(P party) {

    }

    @Override
    public P getPendingPartyInvite() {
        return null;
    }

    @Override
    public void setPendingPartyInvite(P party) {

    }

    @Override
    public boolean isUsingGuiMod() {
        return isusinggui;
    }

    @Override
    public void setUsingGuiMod(boolean b) {
        isusinggui = b;
    }

    @Override
    public boolean isPartyLeader() {
        return false;
    }

    @Override
    public boolean hasClass(ClassDefinition configClass) {
        return false;
    }

    @Override
    public List<Integer> getSlotsToReinitialize() {
        return Collections.emptyList();
    }

    @Override
    public void setSlotsToReinitialize(List<Integer> slotsToReinitialize) {

    }


    @Override
    public boolean isDetached() {
        return true;
    }

    @Override
    public void addSkillTreeSpecialization(SkillTreeSpecialization specialization) {

    }

    @Override
    public double getExperienceBonusFor(String name, String type) {
        return 0;
    }

    @Override
    public void addClass(PlayerClassData playerClassData) {

    }

    @Override
    public void removeClass(ClassDefinition classDefinition) {

    }

    @Override
    public void removeSkillTreeSpecialization(SkillTreeSpecialization specialization) {

    }

    @Override
    public boolean hasSkillTreeSpecialization(SkillTreeSpecialization specialization) {
        return false;
    }

    @Override
    public Set<SkillTreeSpecialization> getSkillTreeSpecialization() {
        return Collections.emptySet();
    }

    @Override
    public Set<EquipedSlot> getSlotsCannotBeEquiped() {
        return Collections.emptySet();
    }

    @Override
    public RpgItemStack getMainHand() {
        return null;
    }

    @Override
    public void setMainHand(RpgItemStack customItem, int slot) {

    }

    @Override
    public int getMainHandSlotId() {
        return -1;
    }

    @Override
    public RpgItemStack getOffHand() {
        return null;
    }

    @Override
    public void setOffHand(RpgItemStack customItem) {

    }

    @Override
    public void restartAttributeGuiSession() {

    }

    @Override
    public boolean requiresDamageRecalculation() {
        return false;
    }

    @Override
    public void setRequiresDamageRecalculation(boolean k) {

    }

    @Override
    public int getLastHotbarSlotInteraction() {
        return 0;
    }

    @Override
    public void setLastHotbarSlotInteraction(int last) {

    }

    @Override
    public Map<Class<?>, RpgInventory> getManagedInventory() {
        return Collections.emptyMap();
    }

    @Override
    public SkillTreeChangeObserver getSkillUpgradeObservers() {
        return new SkillTreeChangeObserver(this);
    }
}

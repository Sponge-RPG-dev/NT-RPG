package cz.neumimto.players;

import cz.neumimto.Weapon;
import cz.neumimto.effects.EffectSource;
import cz.neumimto.effects.IEffect;
import cz.neumimto.players.groups.Guild;
import cz.neumimto.players.groups.NClass;
import cz.neumimto.players.groups.PlayerGroup;
import cz.neumimto.players.groups.Race;
import cz.neumimto.players.parties.Party;
import cz.neumimto.players.properties.DefaultProperties;
import cz.neumimto.players.properties.PlayerPropertyService;
import cz.neumimto.skills.ExtendedSkillInfo;
import cz.neumimto.skills.ISkill;
import cz.neumimto.skills.SkillInfo;
import cz.neumimto.skills.StartingPoint;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.potion.PotionEffect;
import org.spongepowered.api.potion.PotionEffectType;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.Tristate;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

/**
 * Created by NeumimTo on 26.12.2014.
 */

public class ActiveCharacter implements IActiveCharacter {
    private transient float[] characterProperties;
    private transient float[] characterPropertiesLevel;
    private IReservable mana = new Mana(this);
    private Health health = new Health(this);
    private transient Player pl;
    private transient Map<Class<? extends IEffect>, IEffect> effects = new HashMap<>();
    private transient Weapon cachedWeapon = Weapon.EmptyHand;
    private transient Click click = new Click();
    private transient Set<ItemType> allowedArmorIds = new HashSet<>();
    private transient Map<ItemType, Double> allowedWeapons = new HashMap<>();
    private transient Party party;
    private Map<String, ExtendedSkillInfo> skills = new HashMap<>();
    private Guild guild = Guild.Default;
    private Race race = Race.Default;
    private Set<ExtendedNClass> classes = new HashSet<>();
    private ExtendedNClass primary;
    private Weapon mainHand = new Weapon();
    private Weapon offHand = new Weapon();
    private CharacterBase base;
    private boolean silenced = false;

    public ActiveCharacter(Player pl, CharacterBase base) {
        this.pl = pl;
        characterProperties = new float[PlayerPropertyService.LAST_ID];
        characterPropertiesLevel = new float[PlayerPropertyService.LAST_ID];
        ExtendedNClass cl = new ExtendedNClass();
        cl.setPrimary(true);
        cl.setnClass(NClass.Default);
        cl.setExperiences(0);
        this.base = base;
        classes.add(cl);
    }

    public boolean isSilenced() {
        return silenced;
    }

    public void setSilenced(boolean silenced) {
        this.silenced = silenced;
    }

    @Override
    public String getName() {
        return getCharacterBase().getName();
    }

    @Override
    public boolean isStub() {
        return false;
    }

    @Override
    public float[] getCharacterProperties() {
        return characterProperties;
    }

    @Override
    public float getCharacterProperty(int index) {
        return characterProperties[index]+characterPropertiesLevel[index]*getPrimaryClass().getLevel();
    }

    @Override
    public void setCharacterProperty(int index, float value) {
        characterProperties[index] = value;
    }

    public void setCharacterLevelProperty(int index, float value) {
        characterPropertiesLevel[index] = value;
    }

    public float getCharacterPropertyWithoutLevel(int index) {
        return characterProperties[index];
    }

    @Override
    public double getMaxMana() {
        return getCharacterProperty(DefaultProperties.max_mana);
    }

    @Override
    public void setMaxMana(float mana) {
        setCharacterProperty(DefaultProperties.max_mana, mana);
    }

    @Override
    public void setMaxHealth(float maxHealth) {
        setCharacterProperty(DefaultProperties.max_health, maxHealth);
    }

    @Override
    public void setHealth(float mana) {
        setCharacterProperty(DefaultProperties.max_mana, mana);
    }

    @Override
    public IReservable getMana() {
        return mana;
    }

    @Override
    public void setMana(IReservable mana) {
        this.mana = mana;
    }

    @Override
    public Health getHealth() {
        return health;
    }

    @Override
    public void setHealth(Health health) {
        this.health = health;
    }

    @Override
    public Collection<IEffect> getEffects() {
        return effects.values();
    }

    @Override
    public IEffect getEffect(Class<? extends IEffect> cl) {
        return effects.get(cl);
    }

    @Override
    public boolean hasEffect(Class<? extends IEffect> cl) {
        return effects.containsKey(cl);
    }

    @Override
    public void addEffect(IEffect effect) {
        effects.put(effect.getClass(), effect);
    }

    @Override
    public void addPotionEffect(PotionEffect e) {

    }

    @Override
    public void removeEffect(Class<? extends IEffect> cl) {
        effects.remove(cl);
    }

    @Override
    public void addPotionEffect(PotionEffectType p, int amplifier, long duration) {
        addPotionEffect(p, amplifier, duration, false);
    }

    @Override
    public void addPotionEffect(PotionEffectType p, int amplifier, long duration, boolean partciles) {
      /*  PotionEffect potionEffect = potionEffectBuilder.potionType(p).amplifier(amplifier).duration((int) (duration / 20)).particles(partciles).build();
        potionEffectBuilder.reset();
        potionEffectData.addPotionEffect(potionEffect,true);*/
    }


    @Override
    public void removePotionEffect(PotionEffectType type) {

    }

    @Override
    public boolean hasPotionEffect(PotionEffectType type) {
        return false;
    }

    @Override
    public double getExperiencs() {
        return getCharacterProperty(DefaultProperties.experiences);
    }

    @Override
    public void addExperiences(float exp) {

    }

    @Override
    public Player getPlayer() {
        return pl;
    }

    @Override
    public void setPlayer(Player pl) {
        this.pl = pl;
    }

    @Override
    public void onRightClickBlock(int slotId) {
        if (cachedWeapon.getSlot() == slotId) {
            return;
        }
        //todo lets see how will work mc 1.9
    }

    @Override
    public void resetRightClicks() {
        click.setTimes(0);
        click.setLastTime(0);
    }

    @Override
    public short getSkillPoints() {
        return base.getSkillPoints();
    }

    @Override
    public void setSkillPoints(short skillPoints) {
        this.base.setSkillPoints(skillPoints);
    }

    @Override
    public short getAttributePoints() {
        return base.getAttributePoints();
    }

    @Override
    public void setAttributePoints(short attributePoints) {
        this.base.setAttributePoints(attributePoints);
    }


    private void fixPropertyValues(Map<Integer, Float> map, int mult) {
        for (Map.Entry<Integer, Float> s : map.entrySet()) {
            setCharacterProperty(s.getKey(), getCharacterProperty(s.getKey()) + s.getValue() * -1);
        }
    }

    private void fixPropertyLevelValues(Map<Integer, Float> map, int mult) {
        for (Map.Entry<Integer, Float> s : map.entrySet()) {
            setCharacterLevelProperty(s.getKey(), getCharacterProperty(s.getKey()) + s.getValue() * -1);
        }
    }

    @Override
    public ExtendedNClass getPrimaryClass() {
        return primary;
    }

    @Override
    public void setPrimaryClass(NClass clazz) {
        setClass(clazz, 0);
    }

    public void setClass(NClass nclass, int slot) {
        if (primary != null) {
            fixPropertyValues(getPrimaryClass().getnClass().getPropBonus(), -1);
            fixPropertyLevelValues(getPrimaryClass().getnClass().getPropLevelBonus(),-1);
            skills.clear();
        }
        if (slot > 0)
            throw new NotImplementedException();
        primary = new ExtendedNClass();
        primary.setnClass(nclass);
        primary.setPrimary(true);
        base.setPrimaryClass(nclass.getName());
        fixPropertyValues(nclass.getPropBonus(), 1);
        fixPropertyLevelValues(getPrimaryClass().getnClass().getPropLevelBonus(),1);
        SkillInfo skillInfo = nclass.getSkillTree().getSkills().get(StartingPoint.name);
        if (skillInfo != null) {
            ExtendedSkillInfo info = new ExtendedSkillInfo();
            info.setLevel(0);
            info.setSkill(null);
            info.setSkillInfo(skillInfo);
        }
    }

    @Override
    public Map<String, Long> getCooldowns() {
        return getCharacterBase().getCooldowns();
    }

    @Override
    public boolean hasCooldown(String thing) {
        return getCharacterBase().getCooldowns().containsKey(thing);
    }

    private void mergeWeapons(PlayerGroup g) {
        for (Map.Entry<ItemType, Double> entries : g.getWeapons().entrySet()) {
            if (getBaseWeaponDamage(entries.getKey()) < entries.getValue()) {
                allowedWeapons.put(entries.getKey(), entries.getValue());
            }
        }
    }

    @Override
    public double getBaseWeaponDamage(ItemType id) {
        Double d = getAllowedWeapons().get(id);
        if (d == null)
            return 0;
        return d;
    }

    public IActiveCharacter updateItemRestrictions() {
        allowedWeapons.clear();
        allowedWeapons.putAll(getRace().getWeapons());
        mergeWeapons(getGuild());
        mergeWeapons(getPrimaryClass().getnClass());
        mergeWeapons(getRace());
        allowedArmorIds.clear();
        allowedArmorIds.addAll(getRace().getAllowedArmor());
        allowedArmorIds.addAll(getGuild().getAllowedArmor());
        allowedArmorIds.addAll(getPrimaryClass().getnClass().getAllowedArmor());
        return this;
    }

    @Override
    public Set<ItemType> getAllowedArmor() {
        return allowedArmorIds;
    }

    @Override
    public boolean canWear(ItemStack armor) {
        return getAllowedArmor().contains(armor.getItem().getId());
    }

    @Override
    public void removeAllTempEffects() {
        for (Map.Entry<Class<? extends IEffect>, IEffect> entry : effects.entrySet()) {
            IEffect effect = entry.getValue();
            if (effect.getEffectSource() == EffectSource.TEMP) {
                removeEffect(entry.getKey());
            }
        }
    }

    @Override
    public Map<ItemType, Double> getAllowedWeapons() {
        return allowedWeapons;
    }

    @Override
    public Set<ExtendedNClass> getClasses() {
        return classes;
    }

    @Override
    public Race getRace() {
        return race;
    }

    @Override
    public void setRace(Race race) {
        if (this.race != Race.Default) {
            fixPropertyValues(this.race.getPropBonus(), -1);
            removePermissions(race.getPermissions());
        }
        this.race = race;
        fixPropertyValues(race.getPropBonus(), 1);
        addPermissions(race.getPermissions());
    }

    @Override
    public Guild getGuild() {
        return guild;
    }

    @Override
    public void setGuild(Guild guild) {
        if (this.guild != Guild.Default) {
            fixPropertyValues(this.guild.getPropBonus(), -1);
            removePermissions(guild.getPermissions());
        }
        this.guild = guild;
        fixPropertyValues(guild.getPropBonus(), 1);
        addPermissions(guild.getPermissions());
    }

    public void addPermissions(Collection<String> perms) {
        SubjectData subjectData = pl.getTransientSubjectData();
        perms.stream().forEach(s -> {
            subjectData.setPermission(SubjectData.GLOBAL_CONTEXT,s, Tristate.TRUE);
        });
    }

    public void removePermissions(Collection<String> perms) {
        SubjectData subjectData = pl.getTransientSubjectData();
        perms.stream().forEach(s -> {
            subjectData.setPermission(SubjectData.GLOBAL_CONTEXT,s,Tristate.FALSE);
        });
    }

    @Override
    public CharacterBase getCharacterBase() {
        return base;
    }

    @Override
    public void sendMessage(String message) {
        pl.sendMessage(Texts.of(message));
    }

    @Override
    public Map<String, ExtendedSkillInfo> getSkills() {
        return Collections.unmodifiableMap(skills); //lets use wrapper class instaed of guava's immutable
    }

    @Override
    public void addSkill(String name, ExtendedSkillInfo info) {
        skills.put(name.toLowerCase(),info);
    }

    @Override
    public ExtendedSkillInfo getSkill(String skillName) {
        return skills.get(skillName.toLowerCase());
    }

    @Override
    public void getRemoveAllSkills() {
        getCharacterBase().getSkills().clear();
        skills.clear();
    }

    @Override
    public ExtendedSkillInfo getSkillInfo(ISkill skill) {
        return skills.get(skill.getName().toLowerCase());
    }

    @Override
    public ExtendedSkillInfo getSkillInfo(String s) {
        return skills.get(s.toLowerCase());
    }

    @Override
    public boolean hasSkill(String name) {
        return skills.containsKey(name.toLowerCase());
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public NClass getNClass(int index) {
        return getPrimaryClass().getnClass();
    }


    @Override
    public void setWalkSpeed(double speed) {

    }

    @Override
    public Party getParty() {
        return party;
    }

    @Override
    public void setParty(Party party) {
        if (this.party != null) {
            party.removePlayer(this);
        }
        this.party = party;
    }

    @Override
    public boolean hasParty() {
        return getParty() != null;
    }

    @Override
    public boolean isInPartyWith(IActiveCharacter character) {
        return (character.hasParty() && hasParty() && character.getParty() == character.getParty());
    }

    @Override
    public int hashCode() {
        return pl.getUniqueId().hashCode()*37;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActiveCharacter that = (ActiveCharacter) o;
        if (that.getCharacterBase().getId() == this.getCharacterBase().getId())
            return true;
        return false;
    }
}

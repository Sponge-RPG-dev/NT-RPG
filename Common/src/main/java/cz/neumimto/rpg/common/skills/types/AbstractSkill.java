package cz.neumimto.rpg.common.skills.types;

import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.localization.Arg;
import cz.neumimto.rpg.common.localization.LocalizationKeys;
import cz.neumimto.rpg.common.localization.LocalizationService;
import cz.neumimto.rpg.common.logging.Log;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.ISkillType;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillSettings;
import cz.neumimto.rpg.common.utils.Console;
import cz.neumimto.rpg.common.utils.DebugLevel;
import cz.neumimto.rpg.common.utils.annotations.CatalogId;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by NeumimTo on 12.3.2015.
 */
public abstract class AbstractSkill<T> implements ISkill<T> {

    private static final String SKILL = "skill";
    public static final String PLAYER = "player";

    @Inject
    protected LocalizationService localizationService;

    protected SkillSettings settings = new SkillSettings();

    @CatalogId
    private String catalogId;

    private Set<ISkillType> skillTypes = new HashSet<>();
    private String damageType = null;


    public AbstractSkill() {
        ResourceLoader.Skill sk = this.getClass().getAnnotation(ResourceLoader.Skill.class);
        if (sk != null) {
            catalogId = sk.value().toLowerCase();
        }
    }

    /**
     * Sets catalog id, if null.
     *
     * @param catalogId
     * @throws IllegalStateException if catalogId not null
     */
    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    @Override
    public void skillLearn(ActiveCharacter ActiveCharacter, PlayerSkillContext context) {
        if (Rpg.get().getPluginConfig().PLAYER_LEARNED_SKILL_GLOBAL_MESSAGE) {
            Rpg.get().broadcastLocalizableMessage(LocalizationKeys.PLAYER_LEARNED_SKILL_GLOBAL_MESSAGE,
                    ActiveCharacter.getName(), context.getSkillData().getSkillName());
        }
    }

    @Override
    public void skillUpgrade(ActiveCharacter ActiveCharacter, int level, PlayerSkillContext context) {
        if (Rpg.get().getPluginConfig().PLAYER_UPGRADED_SKILL_GLOBAL_MESSAGE) {
            Rpg.get().broadcastLocalizableMessage(LocalizationKeys.PLAYER_UPGRADED_SKILL_GLOBAL_MESSAGE,
                    Arg.arg(PLAYER, ActiveCharacter.getName())
                            .with(SKILL, context.getSkillData().getSkillName())
                            .with("level", level));
        }
    }

    @Override
    public void skillRefund(ActiveCharacter ActiveCharacter, PlayerSkillContext context) {
        if (Rpg.get().getPluginConfig().PLAYER_REFUNDED_SKILL_GLOBAL_MESSAGE) {
            Rpg.get().broadcastLocalizableMessage(LocalizationKeys.PLAYER_REFUNDED_SKILL_GLOBAL_MESSAGE,
                    Arg.arg(PLAYER, ActiveCharacter.getName())
                            .with(SKILL, context.getSkillData().getSkillName()));
        }
    }

    @Override
    public SkillSettings getDefaultSkillSettings() {
        return settings;
    }

    @Override
    public void onCharacterInit(ActiveCharacter c, int level, PlayerSkillContext context) {
        if (Rpg.get().getPluginConfig().SKILLGAIN_MESSAGES_AFTER_LOGIN) {
            String msg = localizationService.translate(LocalizationKeys.PLAYER_GAINED_SKILL,
                    Arg.arg("skill", context.getSkillData().getSkillName()));
            c.sendMessage(msg);
        }
    }

    @Override
    public void init() {
    }

    @Override
    public SkillSettings getSettings() {
        return settings;
    }

    @Override
    public void setSettings(SkillSettings settings) {
        this.settings = settings;
    }

    @Override
    public Set<ISkillType> getSkillTypes() {
        return skillTypes;
    }

    @Override
    public String getDamageType() {
        return damageType;
    }

    @Override
    public void setDamageType(String type) {
        damageType = type;
    }

    public void addSkillType(ISkillType type) {
        if (skillTypes == null) {
            skillTypes = new HashSet<>();
        }
        skillTypes.add(type);
    }

    /* Skills are singletons */
    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return catalogId.hashCode() * 77;
    }

    @Override
    public String getId() {
        return catalogId;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Log.info(Console.PURPLE + "Destroying " + getId() + " classloader: " + getClass().getClassLoader().toString(), DebugLevel.DEVELOP);
    }

}

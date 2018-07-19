package cz.neumimto.rpg.skills;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.effects.IEffectContainer;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.effects.model.EffectModelFactory;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by NeumimTo on 15.7.2018.
 */
public abstract class ConfigPassiveSkill extends PassiveSkill {

    private final String effectName;

    private IGlobalEffect effect;

    @Inject
    private Logger logger;


    public ConfigPassiveSkill(String id, String effectName) {
        super(id);
        this.effectName = effectName;
    }


    @Override
    public void init() {
        super.init();
        effect = effectService.getGlobalEffect(effectName);
    }

    public abstract Map<String, String> getModel(ExtendedSkillInfo info);

    @Override
    public void applyEffect(ExtendedSkillInfo info, IActiveCharacter character) {
        Map<String, String> model = getModel(info);

        effect.construct(character, -1, )
        CriticalEffect dodgeEffect = new CriticalEffect(character, -1, model);
        effectService.addEffect(dodgeEffect, character, this);
    }

    @Override
    public void skillUpgrade(IActiveCharacter character, int level) {
        ExtendedSkillInfo info = character.getSkill(getId());
        IEffectContainer effect = character.getEffect(effectName);
        effect.updateValue(getModel(info), this);
        effect.updateStackedValue();
    }
/*
    private CriticalEffectModel getModel(ExtendedSkillInfo info) {
        int chance = getIntNodeValue(info, SkillNodes.CHANCE);
        float mult = getFloatNodeValue(info, SkillNodes.MULTIPLIER);
        return new CriticalEffectModel(chance, mult);
    }
    */
    @Override
    public PassiveSkillEffectData constructSkillData() {
        return new PassiveSkillEffectData(getId());
    }

    @Override
    public <T extends SkillData> void loadSkillData(T skillData, SkillTree context, SkillLoadingErrors errors, Config c) {
        PassiveSkillEffectData pdata = (PassiveSkillEffectData) skillData;
        try {
            List<String> ec = c.getStringList("SkillTypes");
            for (String s : ec) {
                Optional<SkillType> type = Sponge.getRegistry().getType(SkillType.class, s);
                if (type.isPresent()) {
                    addSkillType(type.get());
                } else {
                    logger.error("Unknown SkillType \"" + s + "\" defined in \""+ getId() + "\"");
                }
            }
        } catch (ConfigException e) {

        }
        pdata.setMaxSkillLevel(1);
        try {
            String a = c.getString("ItemIcon");
            Optional<ItemType> type = Sponge.getRegistry().getType(ItemType.class, a);
            type.ifPresent(this::setIcon);
        } catch (ConfigException e) {

        }
        try {
            String effect = c.getString("Effect");
            IGlobalEffect globalEffect = effectService.getGlobalEffect(effect);
            if (globalEffect == null) {
                throw new IllegalStateException("Could not find effect with name " + effect);
            }
            pdata.setEffect(globalEffect);
            pdata.setModel(EffectModelFactory.getModelType(globalEffect.getClass()));

        } catch (ConfigException e) {
            throw new IllegalStateException("Could not find effect section", e);
        }

    }

}

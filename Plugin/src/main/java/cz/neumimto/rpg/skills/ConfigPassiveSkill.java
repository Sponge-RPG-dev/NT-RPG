package cz.neumimto.rpg.skills;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.effects.IEffect;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.effects.model.EffectModelFactory;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by NeumimTo on 15.7.2018.
 */
public class ConfigPassiveSkill extends PassiveSkill {

    private final String effectName;

    private IGlobalEffect effect;

    @Inject
    private Logger logger;

    public ConfigPassiveSkill() {

    }

    @Override
    public void init() {
        super.init();
        effect = effectService.getGlobalEffect(effectName);
    }

    //todo rethink this with asm (?) on the other hand this is not called often its just ugly
    public Map<String, String> getEffectModel(ExtendedSkillInfo info) {
        SkillData skillData = info.getSkillData();
        SkillSettings skillSettings = skillData.getSkillSettings();
        Map<String, String> model = new HashMap<>();
        for (Map.Entry<String, Float> entry : skillSettings.getNodes().entrySet()) {
            if (entry.getKey().endsWith("_levelbonus")) {
                float val = entry.getValue() * info.getTotalLevel();
                String substring = entry.getKey().substring(0, entry.getKey().length() - "_levelbonus".length());
                model.computeIfPresent(substring, (s1, s2) -> String.valueOf(Float.parseFloat(s2) + val));
            } else {
                String s = model.get(entry.getKey());
                model.computeIfPresent(entry.getKey(), (s1, s2) -> String.valueOf(Float.parseFloat(s2) + Float.parseFloat(s)));
            }
        }
        return model;
    }

    @Override
    public void applyEffect(ExtendedSkillInfo info, IActiveCharacter character) {
        Map<String, String> model = getEffectModel(info);
        IEffect eff = effect.construct(character, -1, model);
        effectService.addEffect(eff, character, this);
    }

    @Override
    public void skillUpgrade(IActiveCharacter character, int level) {
        effectService.removeEffect(effectName, character, this);
        applyEffect(character.getSkillInfo(getId()), character);
    }

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

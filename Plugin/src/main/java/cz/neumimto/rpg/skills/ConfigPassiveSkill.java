package cz.neumimto.rpg.skills;

/**
 * Created by NeumimTo on 15.7.2018.
 */
public class ConfigPassiveSkill /*extends PassiveSkill */{
/*
    public ConfigPassiveSkill(String id) {
        super(id);
        SkillSettings skillSettings = new SkillSettings();
        skillSettings.addNode(SkillNodes.CHANCE, 10, 20);
        skillSettings.addNode(SkillNodes.MULTIPLIER, 10, 20);
        super.settings = skillSettings;
        setDamageType(NDamageType.MEELE_CRITICAL);
        addSkillType(SkillType.PHYSICAL);
    }

    @Override
    public void applyEffect(ExtendedSkillInfo info, IActiveCharacter character) {
        CriticalEffectModel model = getModel(info);
        CriticalEffect dodgeEffect = new CriticalEffect(character, -1, model);
        effectService.addEffect(dodgeEffect, character, this);
    }

    @Override
    public void skillUpgrade(IActiveCharacter character, int level) {
        ExtendedSkillInfo info = character.getSkill(getId());
        IEffectContainer<CriticalEffectModel, CriticalEffect> effect = character.getEffect(CriticalEffect.name);
        effect.updateValue(getModel(info), this);
        effect.updateStackedValue();
    }

    private CriticalEffectModel getModel(ExtendedSkillInfo info) {
        int chance = getIntNodeValue(info, SkillNodes.CHANCE);
        float mult = getFloatNodeValue(info, SkillNodes.MULTIPLIER);
        return new CriticalEffectModel(chance, mult);
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
                addSkillType(SkillType.valueOf(s));
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
    */
}

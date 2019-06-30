package cz.neumimto.rpg.api.skills;

import cz.neumimto.rpg.api.utils.TriState;

/**
 * Created by NeumimTo on 23.12.2015.
 */
public interface ISkillType {

    TriState isNegative();


    String getId();

    String getTranslationKey();
}

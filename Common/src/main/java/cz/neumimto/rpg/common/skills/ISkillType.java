package cz.neumimto.rpg.common.skills;

import cz.neumimto.rpg.common.utils.TriState;

/**
 * Created by NeumimTo on 23.12.2015.
 */
public interface ISkillType {

    TriState isNegative();


    String getId();

    String getTranslationKey();
}

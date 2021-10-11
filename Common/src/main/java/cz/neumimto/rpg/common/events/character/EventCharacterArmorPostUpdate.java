

package cz.neumimto.rpg.common.events.character;

import cz.neumimto.rpg.common.items.RpgItemType;

import java.util.Set;

/**
 * Created by NeumimTo on 25.7.2015.
 */
public interface EventCharacterArmorPostUpdate extends TargetCharacterEvent {

    Set<RpgItemType> getArmor();

    void setArmor(Set<RpgItemType> armor);

}

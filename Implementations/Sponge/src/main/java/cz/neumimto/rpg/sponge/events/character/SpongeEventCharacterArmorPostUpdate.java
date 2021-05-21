

package cz.neumimto.rpg.sponge.events.character;

import cz.neumimto.rpg.api.events.character.EventCharacterArmorPostUpdate;
import cz.neumimto.rpg.api.items.RpgItemType;

import java.util.Set;

/**
 * Created by NeumimTo on 25.7.2015.
 */
public class SpongeEventCharacterArmorPostUpdate extends AbstractCharacterEvent implements EventCharacterArmorPostUpdate {

    private Set<RpgItemType> armor;

    @Override
    public Set<RpgItemType> getArmor() {
        return armor;
    }

    @Override
    public void setArmor(Set<RpgItemType> armor) {
        this.armor = armor;
    }

}

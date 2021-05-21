

package cz.neumimto.rpg.api.events.character;

import cz.neumimto.rpg.api.IRpgElement;
import cz.neumimto.rpg.api.events.Cancellable;

/**
 * Created by NeumimTo on 9.8.2015.
 */
public interface CharacterManaRegainEvent extends TargetCharacterEvent, Cancellable {

    double getAmount();

    void setAmount(double amount);

    IRpgElement getSource();

    void setSource(IRpgElement source);

}

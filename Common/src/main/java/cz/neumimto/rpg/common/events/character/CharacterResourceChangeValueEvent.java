package cz.neumimto.rpg.common.events.character;

import cz.neumimto.rpg.common.IRpgElement;
import cz.neumimto.rpg.common.events.Cancellable;

/**
 * Created by NeumimTo on 9.8.2015.
 */
public interface CharacterResourceChangeValueEvent extends TargetCharacterEvent, Cancellable {

    String getType();

    void setType(String type);

    double getAmount();

    void setAmount(double amount);

    IRpgElement getSource();

    void setSource(IRpgElement source);

}

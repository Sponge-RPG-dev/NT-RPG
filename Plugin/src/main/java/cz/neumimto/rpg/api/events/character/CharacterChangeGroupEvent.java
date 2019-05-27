package cz.neumimto.rpg.api.events.character;

import cz.neumimto.rpg.players.groups.ClassDefinition;

/**
 * Created by NeumimTo on 29.4.2017.
 */
public interface CharacterChangeGroupEvent extends TargetCharacterEvent {

    ClassDefinition getNewClass();

    ClassDefinition getOldClass();

    void setNewClass(ClassDefinition classDefinition);

    void setOldClass(ClassDefinition classDefinition);

}

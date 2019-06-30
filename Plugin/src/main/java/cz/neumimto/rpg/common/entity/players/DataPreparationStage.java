package cz.neumimto.rpg.common.entity.players;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;

import java.util.List;

public class DataPreparationStage {

    public final Stage stage;
    public final List<CharacterBase> characters;
    public final IActiveCharacter character;

    public DataPreparationStage(Stage stage) {
        this.stage = stage;
        characters = null;
        character = null;
    }

    public DataPreparationStage(Stage stage, List<CharacterBase> characters) {
        this.character = null;
        this.stage = stage;
        this.characters = characters;
    }

    public DataPreparationStage(Stage stage, IActiveCharacter character) {
        this.stage = stage;
        this.character = character;
        this.characters = null;
    }

    public enum Stage {
        LOADING,
        TO_BE_ASSIGNED,
        NO_CHARS,
        NO_ACTION,
        PLAYER_NOT_YET_READY
    }

}

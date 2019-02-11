package cz.neumimto.rpg.players;

import java.util.List;

class DataPreparationStage {

    public final Stage stage;
    public final List<CharacterBase> characters;


    public DataPreparationStage(Stage stage) {
        this.stage = stage;
        characters = null;
    }

    public DataPreparationStage(Stage stage, List<CharacterBase> characters) {
        this.stage = stage;
        this.characters = characters;
    }

    public enum Stage {
        LOADING,
        TO_BE_ASSIGNED,
        NO_CHARS,
        NO_ACTION,
        PLAYER_NOT_YET_READY
    }

}

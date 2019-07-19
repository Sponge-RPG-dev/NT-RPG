package cz.neumimto.rpg.spigot.events.character;

import cz.neumimto.rpg.api.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.api.events.character.CharacterGainedLevelEvent;
import org.bukkit.event.HandlerList;

/**
 * Created by NeumimTo on 27.12.2015.
 */
public class SpigotCharacterGainedLevelEvent extends AbstractCharacterEvent implements CharacterGainedLevelEvent {

    private int level;
    private PlayerClassData playerClassData;
    private int skillpointsPerLevel;
    private int attributePerLevel;

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public PlayerClassData getPlayerClassData() {
        return playerClassData;
    }

    @Override
    public void setPlayerClassData(PlayerClassData playerClassData) {
        this.playerClassData = playerClassData;
    }

    @Override
    public int getSkillpointsPerLevel() {
        return skillpointsPerLevel;
    }

    @Override
    public void setSkillpointsPerLevel(int skillpointsPerLevel) {
        this.skillpointsPerLevel = skillpointsPerLevel;
    }

    @Override
    public int getAttributepointsPerLevel() {
        return attributePerLevel;
    }

    @Override
    public void setAttributepointsPerLevel(int attributePerLevel) {
        this.attributePerLevel = attributePerLevel;
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}

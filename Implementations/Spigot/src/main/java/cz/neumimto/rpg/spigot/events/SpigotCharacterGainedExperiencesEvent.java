package cz.neumimto.rpg.spigot.events;

import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.events.character.CharacterGainedExperiencesEvent;
import cz.neumimto.rpg.spigot.events.character.AbstractCharacterEvent;
import org.bukkit.event.HandlerList;

public class SpigotCharacterGainedExperiencesEvent extends AbstractCharacterEvent implements CharacterGainedExperiencesEvent {

    private double exp;
    private String epxSource;

    private IActiveCharacter character;

    @Override
    public IActiveCharacter getCharacter() {
        return character;
    }

    @Override
    public void setCharacter(IActiveCharacter character) {
        this.character = character;
    }

    @Override
    public double getExp() {
        return exp;
    }

    @Override
    public void setExp(double exp) {
        this.exp = exp;
    }

    @Override
    public String getExpSource() {
        return epxSource;
    }

    @Override
    public void setExpSource(String source) {
        this.epxSource = source;
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}

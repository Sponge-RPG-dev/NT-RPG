package cz.neumimto.events;

import cz.neumimto.players.IActiveCharacter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.event.callback.CallbackList;

/**
 * Created by NeumimTo on 13.3.2015.
 */
public class CharacterDamageEntityEvent extends CharacterEvent {
    private Player damaged;
    private double damage;


    public static CallbackList list = new CallbackList();

    @Override
    public CallbackList getCallbacks() {
        return list;
    }

    public CharacterDamageEntityEvent(IActiveCharacter IActiveCharacter, Player damaged, double amount) {
        super(IActiveCharacter);
        this.damaged = damaged;
        this.damage = amount;
    }

    public Player getDamaged() {
        return damaged;
    }

    public void setDamaged(Player damaged) {
        this.damaged = damaged;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
}

package cz.neumimto.skills;

import cz.neumimto.players.IReservable;

/**
 * Created by NeumimTo on 1.1.2015.
 */
public interface SkillSource {
    public IReservable getMana();

    public void setMana(IReservable mana);
}

package cz.neumimto.players.groups;

import cz.neumimto.skills.ISkill;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 27.12.2014.
 */
public class Guild extends PlayerGroup {
    public static Guild Default = new Guild("None");

    private Set<ISkill> skills = new HashSet<>();

    public Guild(String name) {
        super(name);
    }


}

package cz.neumimto.players.groups;

import cz.neumimto.skills.ISkill;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NeumimTo on 27.12.2014.
 */
public class Race extends PlayerGroup {

    public static Race Default = new Race("None");
    private List<ISkill> skills = new ArrayList<>();

    public Race(String name) {
        super(name);
    }

    public List<ISkill> getSkills() {
        return skills;
    }


}

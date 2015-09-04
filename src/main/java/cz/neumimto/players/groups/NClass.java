package cz.neumimto.players.groups;

import cz.neumimto.skills.SkillTree;

/**
 * Created by NeumimTo on 27.12.2014.
 */
public class NClass extends PlayerGroup {

    public static NClass Default = new NClass("None");

    private SkillTree skillTree = new SkillTree();

    public NClass(String name) {
        super(name);
    }

    public SkillTree getSkillTree() {
        return skillTree;
    }

    public void setSkillTree(SkillTree skillTree) {
        this.skillTree = skillTree;
    }
}

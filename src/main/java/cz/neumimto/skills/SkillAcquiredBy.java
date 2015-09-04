package cz.neumimto.skills;

/**
 * Created by NeumimTo on 18.1.2015.
 */
//todo better name
public enum SkillAcquiredBy {
    SKILLPOINT(0),
    ADMINCMD(1),
    SERVEREVENT(2);
    private final int i;

    SkillAcquiredBy(int i) {
        this.i = i;
    }

}

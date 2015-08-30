package cz.neumimto.skills;

import cz.neumimto.players.IActiveCharacter;

import java.util.Collections;
import java.util.Set;

/**
 * Created by NeumimTo on 9.8.2015.
 */

public class StartingPoint extends PassiveSkill {
    public static String name = "StartingPoint";
    private static SkillSettings skillSettings = new SkillSettings();
    private static String desc = "";

    @Override
    public boolean showsToPlayers() {
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public void skillLearn(IActiveCharacter IActiveCharacter) {

    }

    @Override
    public void skillUpgrade(IActiveCharacter IActiveCharacter, int level) {

    }

    @Override
    public void skillRefund(IActiveCharacter IActiveCharacter) {

    }

    @Override
    public SkillSettings getDefaultSkillSettings() {
        return skillSettings;
    }

    @Override
    public void onCharacterInit(IActiveCharacter c, int level) {

    }

    @Override
    public void init() {

    }

    @Override
    public SkillSettings getSettings() {
        return getDefaultSkillSettings();
    }

    @Override
    public void setSettings(SkillSettings settings) {

    }

    @Override
    public String getDescription() {
        return desc;
    }

    @Override
    public void setDescription(String description) {

    }

    @Override
    public Set<SkillType> getSkillTypes() {
        return Collections.EMPTY_SET;
    }
}

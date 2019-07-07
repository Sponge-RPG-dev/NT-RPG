package cz.neumimto.rpg.common.skills;

import cz.neumimto.rpg.api.skills.IPlayerSkillHandler;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PlayerSkillHandlers {

    public static class SHARED implements IPlayerSkillHandler {

        private Map<String, PlayerSkillContext> skills = new HashMap<>();

        @Override
        public void add(String iSkill, PlayerSkillContext source) {
            skills.put(iSkill, source);
        }

        @Override
        public PlayerSkillContext get(String id) {
            return skills.get(id);
        }

        @Override
        public PlayerSkillContext get(String id, ClassDefinition classDefinition) {
            return null;
        }

        @Override
        public void remove(ISkill skill, ClassDefinition origin) {
            skills.remove(skill.getId());
        }

        @Override
        public Map<String, PlayerSkillContext> getSkills() {
            return Collections.unmodifiableMap(skills);
        }

        @Override
        public void clear() {
            skills.clear();
        }

        @Override
        public boolean contains(String name) {
            return skills.containsKey(name);
        }
    }
/*
    public static final IPlayerSkillHandler CLASS_PRIORITY = new IPlayerSkillHandler() {

        private Map<String, Map<ClassDefinition, PlayerSkillContext>> skills = new HashMap<>();

        @Override
        public PlayerSkillContext get(String id) {
            return skills.get(id);
        }

        @Override
        public PlayerSkillContext get(String id, ClassDefinition classDefinition) {
            Map<ClassDefinition, PlayerSkillContext> classdefs = skills.get(id);
            if (classdefs == null) {
                return null;
            }
            return classdefs.get(classDefinition);
        }

        @Override
        public void remove(ISkill skill, ClassDefinition origin) {

        }

        @Override
        public Map<String, PlayerSkillContext> getSkills() {
            return Collections.unmodifiableMap(skills);
        }

        @Override
        public void clear() {
            skills.clear();
        }

        @Override
        public boolean contains(String name) {
            return skills.containsKey(name);
        }
    };
    */
}

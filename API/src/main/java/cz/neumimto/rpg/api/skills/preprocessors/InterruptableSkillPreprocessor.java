package cz.neumimto.rpg.api.skills.preprocessors;

public interface InterruptableSkillPreprocessor {

    void interrupt();

    boolean isInterrupted();
}

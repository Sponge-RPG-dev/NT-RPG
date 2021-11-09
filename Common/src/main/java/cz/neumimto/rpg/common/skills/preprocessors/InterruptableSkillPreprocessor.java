package cz.neumimto.rpg.common.skills.preprocessors;

public interface InterruptableSkillPreprocessor {

    void interrupt();

    boolean isInterrupted();
}

package cz.neumimto.rpg.api;

import cz.neumimto.rpg.players.attributes.Attribute;
import cz.neumimto.rpg.skills.mods.SkillPreProcessorFactory;

import java.util.Collection;
import java.util.Optional;

public interface RpgApi {

    Collection<Attribute> getAttributes();

    Optional<SkillPreProcessorFactory> getSkillPreProcessorFactory(String preprocessorFactoryId);
}

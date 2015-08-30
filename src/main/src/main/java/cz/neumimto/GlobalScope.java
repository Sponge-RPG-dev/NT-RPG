package cz.neumimto;

import cz.neumimto.effects.EffectService;
import cz.neumimto.ioc.Inject;
import cz.neumimto.ioc.Singleton;
import cz.neumimto.players.CharacterService;
import cz.neumimto.players.properties.PlayerPropertyService;
import cz.neumimto.skills.SkillService;
import org.spongepowered.api.Game;

/**
 * Created by NeumimTo on 6.8.2015.
 */
@Singleton
public class GlobalScope {

    @Inject
    public CharacterService characterService;

    @Inject
    public EffectService effectService;

    @Inject
    public GroupService groupService;

    @Inject
    public PlayerPropertyService playerPropertyService;

    @Inject
    public SkillService skillService;

    @Inject
    public NtRpgPlugin plugin;

    @Inject
    public Game game;

}

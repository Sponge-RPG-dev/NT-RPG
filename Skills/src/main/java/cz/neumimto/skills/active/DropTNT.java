package cz.neumimto.skills.active;

import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.entity.EntityService;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by NeumimTo on 27.12.2018.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:droptnt")
public class DropTNT extends ActiveSkill<ISpongeCharacter> {

    public static Map<UUID, Float> DROPPED_TNTS = new HashMap<>();

    @Inject
    private EntityService entityService;

    public void init() {
        super.init();
        settings.addNode(SkillNodes.AMOUNT, 1f ,0f);
        settings.addNode(SkillNodes.DAMAGE, 100f ,10f);
        settings.addNode("explosion-radius", 3 ,0.1f);
        addSkillType(SkillType.PHYSICAL);
        addSkillType(SkillType.SUMMON);
    }

    @Override
    public void cast(ISpongeCharacter character, PlayerSkillContext info, SkillContext skillContext) {
        Player player = character.getPlayer();
        Location<World> location = player.getLocation();
        World extent = location.getExtent();
        Entity tnt = extent.createEntity(EntityTypes.PRIMED_TNT, location.getPosition());
        int i = skillContext.getIntNodeValue("explosion-radius");
        tnt.offer(Keys.EXPLOSION_RADIUS, Optional.of(i));
        extent.spawnEntity(tnt);
        skillContext.next(character, info, SkillResult.OK);
    }
}

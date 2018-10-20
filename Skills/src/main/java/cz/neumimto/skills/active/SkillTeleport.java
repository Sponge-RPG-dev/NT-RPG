package cz.neumimto.skills.active;

import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.SkillSettings;
import cz.neumimto.rpg.skills.parents.ActiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.TeleportHelper;
import org.spongepowered.api.world.World;

import java.util.Optional;

/**
 * Created by NeumimTo on 29.12.2015.
 */
@ResourceLoader.Skill("ntrpg:teleport")
public class SkillTeleport extends ActiveSkill {

	public void init() {
		super.init();
		SkillSettings settings = new SkillSettings();
		settings.addNode(SkillNodes.RANGE, 20, 20);
		super.settings = settings;

		addSkillType(SkillType.TELEPORT);
		setIcon(ItemTypes.END_PORTAL_FRAME);
	}

	@Override
	public void cast(IActiveCharacter character, ExtendedSkillInfo extendedSkillInfo, SkillContext skillContext) {
		Player player = character.getPlayer();
		double doubleNodeValue = getDoubleNodeValue(extendedSkillInfo, SkillNodes.RANGE);

		Optional<BlockRayHit<World>> optHit =
				BlockRay.from(player).distanceLimit(doubleNodeValue).stopFilter(Utils.SKILL_TARGET_BLOCK_FILTER).build().end();
		if (optHit.isPresent()) {
			Vector3d lookPos = optHit.get().getBlockPosition().toDouble();
			Location<World> worldLocation = new Location<>(player.getWorld(), lookPos);
			TeleportHelper helper = Sponge.getGame().getTeleportHelper();
			Optional<Location<World>> safeLocation = helper.getSafeLocation(worldLocation);
			if (safeLocation.isPresent()) {
				player.setLocation(safeLocation.get());
			}
		}
		skillContext.next(character, extendedSkillInfo, skillContext.result(SkillResult.OK));
	}
}

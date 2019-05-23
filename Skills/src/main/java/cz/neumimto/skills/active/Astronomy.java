package cz.neumimto.skills.active;

import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.SkillLocalization;
import cz.neumimto.Utils;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.skills.tree.SkillType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.world.weather.Weathers;

import javax.inject.Singleton;

/**
 * Created by NeumimTo on 3.8.2017.
 */
@Singleton
@ResourceLoader.Skill("ntrpg:astronomy")
public class Astronomy extends ActiveSkill {

	private static final long start = 14000;
	private static final long end = 22000; //todo check for night, not possible currently

	@Override
	public void init() {
		super.init();
		setIcon(ItemTypes.CLOCK);
		addSkillType(SkillType.CAN_CAST_WHILE_SILENCED);
		addSkillType(SkillType.UTILITY);
	}

	@Override
	public void cast(IActiveCharacter character, PlayerSkillContext info, SkillContext skillContext) {
		Player character1 = character.getEntity();

		if (character1.getWorld().getWeather() == Weathers.CLEAR) {
			Vector3d position = character1.getLocation().getPosition();
			if (character1.getWorld().getHighestYAt(position.getFloorX(), position.getFloorZ()) > position.getFloorY()) {
				character1.sendMessage(ChatTypes.ACTION_BAR, SkillLocalization.ASTRONOMY_CANNOT_SEE_THE_SKY);
				skillContext.next(character, info, skillContext.result(SkillResult.CANCELLED));
				return;
			}
			ItemStack is = Utils.createTeleportationScroll(character.getLocation());
			character.getEntity().getInventory().offer(is);
			skillContext.next(character, info, skillContext.result(SkillResult.OK));
			return;
		}
		character1.sendMessage(ChatTypes.ACTION_BAR, SkillLocalization.ASTRONOMY_CANNOT_SEE_THE_SKY);
		skillContext.next(character, info, skillContext.result(SkillResult.CANCELLED));
		return;
	}
}

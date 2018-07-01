package cz.neumimto.skills.active;

import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.SkillLocalization;
import cz.neumimto.Utils;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ActiveSkill;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillModifier;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.SkillType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.world.weather.Weathers;

/**
 * Created by NeumimTo on 3.8.2017.
 */
@ResourceLoader.Skill("ntrpg:astronomy")
public class Astronomy extends ActiveSkill {

	private static final long start = 14000;
	private static final long end = 22000; //todo check for night, not possible currently

	public Astronomy() {
		setSettings(settings);
		setIcon(ItemTypes.CLOCK);
		addSkillType(SkillType.CAN_CAST_WHILE_SILENCED);
		addSkillType(SkillType.UTILITY);
	}

	@Override
	public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info, SkillModifier modifier) {
		Player character1 = character.getEntity();

		if (character1.getWorld().getWeather() == Weathers.CLEAR) {
			Vector3d position = character1.getLocation().getPosition();
			if (character1.getWorld().getHighestYAt(position.getFloorX(), position.getFloorZ()) > position.getFloorY()) {
				character1.sendMessage(ChatTypes.ACTION_BAR, SkillLocalization.ASTRONOMY_CANNOT_SEE_THE_SKY);
				return SkillResult.CANCELLED;
			}
			ItemStack is = Utils.createTeleportationScroll(character.getLocation());
			character.getEntity().getInventory().offer(is);
			return SkillResult.OK;
		}
		character1.sendMessage(ChatTypes.ACTION_BAR,SkillLocalization.ASTRONOMY_CANNOT_SEE_THE_SKY);
		return SkillResult.CANCELLED;
	}
}

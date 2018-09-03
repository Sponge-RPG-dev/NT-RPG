/*    
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 */

package cz.neumimto.rpg.skills.parents;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.ProjectileProperties;
import cz.neumimto.rpg.skills.SkillResult;
import cz.neumimto.rpg.skills.utils.SkillModifier;
import cz.neumimto.rpg.utils.TriConsumer;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.entity.DamageEntityEvent;

import java.util.Optional;

/**
 * Created by NeumimTo on 12.3.2015.
 */
public abstract class SkillShot extends ActiveSkill {

	@Inject
	private Game game;


	@Override
	public SkillResult cast(IActiveCharacter character, ExtendedSkillInfo info, SkillModifier modifier) {
		Optional<Projectile> projectile = character.getPlayer().launchProjectile(getProjectile(character, info));
		if (projectile.isPresent()) {
			ProjectileProperties projectileProperties = getProjectileProperties(character, info, modifier, projectile.get());
			projectileProperties.onHit(getHitConsumer());
			return SkillResult.OK;
		}
		return SkillResult.CANCELLED;
	}

	protected abstract ProjectileProperties getProjectileProperties(IActiveCharacter character, ExtendedSkillInfo info, SkillModifier modifier, Projectile projectile);

	protected abstract Class<Projectile> getProjectile(IActiveCharacter character, ExtendedSkillInfo info);

	protected abstract TriConsumer<DamageEntityEvent, IEntity, IEntity> getHitConsumer();

}

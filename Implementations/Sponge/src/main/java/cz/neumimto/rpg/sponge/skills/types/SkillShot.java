

package cz.neumimto.rpg.sponge.skills.types;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.api.utils.TriConsumer;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.skills.ProjectileProperties;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.entity.DamageEntityEvent;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Created by NeumimTo on 12.3.2015.
 */
public abstract class SkillShot extends ActiveSkill<ISpongeCharacter> {

    @Inject
    private Game game;


    @Override
    public SkillResult cast(ISpongeCharacter character, PlayerSkillContext info) {
        Optional<Projectile> projectile = character.getPlayer().launchProjectile(getProjectile(character, info));
        if (projectile.isPresent()) {
            ProjectileProperties projectileProperties = getProjectileProperties(character, info, projectile.get());
            projectileProperties.onHit(getHitConsumer());
            return SkillResult.OK;
        }
        return SkillResult.CANCELLED;
    }

    protected abstract ProjectileProperties getProjectileProperties(IActiveCharacter character, PlayerSkillContext info, Projectile projectile);

    protected abstract Class<Projectile> getProjectile(IActiveCharacter character, PlayerSkillContext info);

    protected abstract TriConsumer<DamageEntityEvent, IEntity, IEntity> getHitConsumer();

}

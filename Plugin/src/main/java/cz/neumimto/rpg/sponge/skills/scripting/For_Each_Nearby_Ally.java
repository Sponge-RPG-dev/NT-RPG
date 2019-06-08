package cz.neumimto.rpg.sponge.skills.scripting;

import cz.neumimto.rpg.common.skills.scripting.SkillComponent;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.api.utils.TriConsumer;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.IEntityType;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;

import java.util.Collection;
import java.util.function.Consumer;

//This is a workaround for some bugs i necountered while working in nashorn Nashorn
@JsBinding(JsBinding.Type.OBJECT)
@SkillComponent(
        value = "Returns a list of nearby allies",
        usage = "for_each_nearby_ally(entity, radius, new Consumer() { apply: function(ally} { ... } )",
        params = {
                @SkillComponent.Param("entity - allies for the entity"),
                @SkillComponent.Param("radius"),
                @SkillComponent.Param("allyEntity - callback")
        }
)
public class For_Each_Nearby_Ally implements TriConsumer<IEntity, Number, Consumer<IEntity>> {

    @Override
    public void accept(IEntity entity, Number radius, Consumer<IEntity> consumer) {
        Collection<Entity> nearbyEntities = entity.getEntity().getNearbyEntities(radius.doubleValue());
        if (entity.getType() == IEntityType.MOB) {
            for (Entity nearbyEntity : nearbyEntities) {
                if (nearbyEntity.getType() == EntityTypes.PLAYER) {
                    continue;
                }
                if (!(nearbyEntity instanceof Living)) {
                    continue;
                }
                if (nearbyEntity.get(Keys.TAMED_OWNER).isPresent()) {
                    continue;
                }
                IEntity iEntity = NtRpgPlugin.GlobalScope.entityService.get(nearbyEntity);
                consumer.accept(iEntity);
            }
        } else if (entity.getType() == IEntityType.CHARACTER) {
            IActiveCharacter character = (IActiveCharacter) entity;
            for (Entity nearbyEntity : nearbyEntities) {
                IEntity iEntity = NtRpgPlugin.GlobalScope.entityService.get(nearbyEntity);
                if (iEntity.isFriendlyTo(character)) {
                    consumer.accept(iEntity);
                }
            }
        }
    }
}

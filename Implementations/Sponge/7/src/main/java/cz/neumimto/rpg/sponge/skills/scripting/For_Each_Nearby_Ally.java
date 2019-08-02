package cz.neumimto.rpg.sponge.skills.scripting;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.IEntityType;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.api.utils.TriConsumer;
import cz.neumimto.rpg.common.skills.scripting.SkillComponent;
import cz.neumimto.rpg.sponge.entities.ISpongeEntity;
import cz.neumimto.rpg.sponge.entities.SpongeEntityService;
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
//this must be _in_this_fucking_format_otherwise_nashorn_bindings_fails_to_bind_this_stuff_and_everything_will_stop_running_it_took_me_few_hours_to_find_this_bug_i_had_to_debug_jre_itself_so_whoever_deletes_this_class_shall_burn_in_hell
public class For_Each_Nearby_Ally implements TriConsumer<ISpongeEntity, Number, Consumer<IEntity>> {

    @Override
    public void accept(ISpongeEntity entity, Number radius, Consumer<IEntity> consumer) {
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
                IEntity iEntity = ((SpongeEntityService)Rpg.get().getEntityService()).get(nearbyEntity);
                consumer.accept(iEntity);
            }
        } else if (entity.getType() == IEntityType.CHARACTER) {
            IActiveCharacter character = (IActiveCharacter) entity;
            for (Entity nearbyEntity : nearbyEntities) {
                IEntity iEntity = ((SpongeEntityService)Rpg.get().getEntityService()).get(nearbyEntity);
                if (iEntity.isFriendlyTo(character)) {
                    consumer.accept(iEntity);
                }
            }
        }
    }
}

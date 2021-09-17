package cz.neumimto.rpg.spigot.skills.scripting;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.IEntityType;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.api.utils.TriConsumer;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;

import java.util.Collection;
import java.util.function.Consumer;

//This is a workaround for some bugs i necountered while working in nashorn Nashorn
@JsBinding(JsBinding.Type.OBJECT)
//this must be _in_this_fucking_format_otherwise_nashorn_bindings_fails_to_bind_this_stuff_and_everything_will_stop_running_it_took_me_few_hours_to_find_this_bug_i_had_to_debug_jre_itself_so_whoever_deletes_this_class_shall_burn_in_hell
public class For_Each_Nearby_Ally implements TriConsumer<ISpigotEntity, Number, Consumer<IEntity>> {

    @Override
    public void accept(ISpigotEntity entity, Number radius, Consumer<IEntity> consumer) {
        Collection<Entity> nearbyEntities = entity.getEntity().getNearbyEntities(radius.doubleValue(), radius.doubleValue(), radius.doubleValue());
        if (entity.getType() == IEntityType.MOB) {
            for (Entity nearbyEntity : nearbyEntities) {
                if (nearbyEntity.getType() == EntityType.PLAYER) {
                    continue;
                }
                if (!(nearbyEntity instanceof LivingEntity)) {
                    continue;
                }
                if (entity instanceof Tameable && ((Tameable) entity).isTamed()) {
                    continue;
                }

                IEntity iEntity = Rpg.get().getEntityService().get(nearbyEntity);
                consumer.accept(iEntity);
            }
        } else if (entity.getType() == IEntityType.CHARACTER) {
            IActiveCharacter character = (IActiveCharacter) entity;
            for (Entity nearbyEntity : nearbyEntities) {
                IEntity iEntity = Rpg.get().getEntityService().get(nearbyEntity);
                if (iEntity.isFriendlyTo(character)) {
                    consumer.accept(iEntity);
                }
            }
        }
    }
}

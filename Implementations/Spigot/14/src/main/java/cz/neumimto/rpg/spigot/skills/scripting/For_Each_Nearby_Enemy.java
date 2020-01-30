package cz.neumimto.rpg.spigot.skills.scripting;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.api.utils.TriConsumer;
import cz.neumimto.rpg.common.skills.scripting.SkillComponent;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.entities.ISpigotEntity;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Collection;
import java.util.function.Consumer;

//This is a workaround for some bugs i necountered while working in nashorn Nashorn
@JsBinding(JsBinding.Type.OBJECT)
@SkillComponent(
        value = "Do action for every nearby enemy",
        usage = "for_each_nearby_enemy(entity, radius, new Consumer() { apply: function(entity} { .. })",
        params = {
                @SkillComponent.Param("entity - An entity which we search for its enemies"),
                @SkillComponent.Param("range - Maximal search range"),
                @SkillComponent.Param("consumer - callback"),
        }
)
//this must be _in_this_fucking_format_otherwise_nashorn_bindings_fails_to_bind_this_stuff_and_everything_will_stop_running_it_took_me_few_hours_to_find_this_bug_i_had_to_debug_jre_itself_so_whoever_deletes_this_class_shall_burn_in_hell
public class For_Each_Nearby_Enemy implements TriConsumer<ISpigotEntity, Number, Consumer<IEntity>> {

    @Override
    public void accept(ISpigotEntity entity, Number radius, Consumer<IEntity> consumer) {
        double r = radius.doubleValue();
        Collection<Entity> nearbyEntities = entity.getEntity().getNearbyEntities(r,r,r);
        ISpigotCharacter character = (ISpigotCharacter) entity;
        for (Entity nearbyEntity : nearbyEntities) {
            if (nearbyEntity instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) nearbyEntity;
                IEntity iEntity = Rpg.get().getEntityService().get(nearbyEntity);
                if (!iEntity.isFriendlyTo(character) && ((SpigotDamageService)Rpg.get().getDamageService()).canDamage(character, living)) {
                    consumer.accept(iEntity);
                }
            }
        }
    }
}
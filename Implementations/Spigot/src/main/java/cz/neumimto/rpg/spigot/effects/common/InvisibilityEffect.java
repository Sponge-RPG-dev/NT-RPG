package cz.neumimto.rpg.spigot.effects.common;

import com.google.auto.service.AutoService;
import cz.neumimto.nts.annotations.ScriptMeta;
import cz.neumimto.rpg.common.effects.EffectBase;
import cz.neumimto.rpg.common.effects.Generate;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.entity.IEffectConsumer;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@ScriptMeta.Function("InvisibilityEffect")
@AutoService(IEffect.class)
@Generate(id = "name", description = "Makes entity invisible")
public class InvisibilityEffect extends EffectBase {

    public static String name = "Invisibility";

    public static Set<UUID> invisibleEntities = new HashSet<>();


    @ScriptMeta.Handler
    public InvisibilityEffect(
            @ScriptMeta.NamedParam("e|entity") IEffectConsumer consumer,
            @ScriptMeta.NamedParam("d|duration") long duration) {
        super(name, consumer);
        setDuration(duration);
    }

    @Override
    public void onApply(IEffect self) {
        Player player = (Player) getConsumer().getEntity();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.hidePlayer(SpigotRpgPlugin.getInstance(), player);
        }

        invisibleEntities.add(player.getUniqueId());

        for (Entity nearbyEntity : player.getNearbyEntities(50, 50, 50)) {
            if (nearbyEntity instanceof Mob m && m.getTarget() == player) {
                m.setTarget(null);
            }
        }

    }

    @Override
    public void onRemove(IEffect self) {
        Player player = (Player) getConsumer().getEntity();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.showPlayer(SpigotRpgPlugin.getInstance(), player);
        }
        invisibleEntities.add(player.getUniqueId());
    }
}

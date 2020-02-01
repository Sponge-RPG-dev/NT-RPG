package cz.neumimto.rpg.spigot.bridges;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.events.skill.SkillPostUsageEvent;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Singleton
public class HolographicDisplaysExpansion {

    @Inject
    private SkillService skillService;

    @Inject
    private SpigotDamageService damageService;

    private static Map<Hologram, Long> holograms = new HashMap<>();
    private static Map<String, String> colors = new HashMap<>();


    static {
        put(EntityDamageEvent.DamageCause.FIRE, ChatColor.RED);
        put(EntityDamageEvent.DamageCause.LAVA, ChatColor.RED);
        put(EntityDamageEvent.DamageCause.FIRE_TICK, ChatColor.RED);

        put(EntityDamageEvent.DamageCause.SUFFOCATION, ChatColor.BLUE);
        put(EntityDamageEvent.DamageCause.DROWNING, ChatColor.BLUE);
        put(EntityDamageEvent.DamageCause.LIGHTNING, ChatColor.YELLOW);

        put(EntityDamageEvent.DamageCause.MAGIC, ChatColor.DARK_AQUA);

        put(EntityDamageEvent.DamageCause.VOID, ChatColor.DARK_PURPLE);
        put(EntityDamageEvent.DamageCause.WITHER, ChatColor.BLACK);

        put(EntityDamageEvent.DamageCause.LIGHTNING, ChatColor.YELLOW);

        put(EntityDamageEvent.DamageCause.POISON, ChatColor.GREEN);

        put(EntityDamageEvent.DamageCause.POISON, ChatColor.GREEN);
    }

    private static void put(EntityDamageEvent.DamageCause damageCause, ChatColor chatColor) {
        colors.put(damageCause.toString(), chatColor.toString());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSkillCast(SkillPostUsageEvent event) {
        ISkill skill = event.getSkill();
        String damageType = skill.getDamageType();
        IEntity caster = event.getCaster();
        Entity entity = (Entity) caster.getEntity();

        String s = colors.get(damageType);
        if (s == null) {
            s = "";
        }

        Location location = entity.getLocation().add(0, entity.getHeight() + 0.1,0);
        Hologram hologram = HologramsAPI.createHologram(SpigotRpgPlugin.getInstance(), location);
        hologram.insertTextLine(0, s + skill.getName());
        VisibilityManager visiblityManager = hologram.getVisibilityManager();
        visiblityManager.setVisibleByDefault(true);
        holograms.put(hologram, System.currentTimeMillis() + 2000);
    }

    public void init() {

        new BukkitRunnable() {

            @Override
            public void run() {
                Iterator<Map.Entry<Hologram, Long>> iterator = holograms.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Hologram, Long> next = iterator.next();
                    Hologram key = next.getKey();
                    if (next.getValue() < System.currentTimeMillis()) {
                        key.delete();
                    }
                    key.teleport(key.getLocation().add(0.0, 0.25, 0.0));
                }
            }
        }.runTaskTimer(SpigotRpgPlugin.getInstance(), 1L, 20L);
    }

}

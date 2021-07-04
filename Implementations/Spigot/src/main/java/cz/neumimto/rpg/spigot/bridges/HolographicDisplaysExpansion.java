package cz.neumimto.rpg.spigot.bridges;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.api.utils.rng.XORShiftRnd;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.events.skill.SpigotSkillPostUsageEvent;
import cz.neumimto.rpg.spigot.utils.VectorUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HolographicDisplaysExpansion implements Listener {

    @Inject
    private SkillService skillService;

    @Inject
    private SpigotDamageService damageService;

    @Inject
    private LocalizationService localizationService;

    private static Map<Hologram, Long> holograms = new HashMap<>();
    private static Map<String, String> colors = new HashMap<>();

    private static Vector[] displayLocs;
    private static XORShiftRnd rnd = new XORShiftRnd();

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
        put(EntityDamageEvent.DamageCause.ENTITY_ATTACK, ChatColor.GRAY);
        put(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK, ChatColor.GRAY);

        displayLocs = VectorUtils.circle(new Vector[20], 2);
    }

    private static void put(EntityDamageEvent.DamageCause damageCause, ChatColor chatColor) {
        colors.put(damageCause.toString(), chatColor.toString());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onSkillCast(SpigotSkillPostUsageEvent event) {
        ISkill skill = event.getSkill();
        String damageType = skill.getDamageType();
        IEntity caster = event.getCaster();
        Entity entity = (Entity) caster.getEntity();

        String s = colors.get(damageType);
        if (s == null) {
            s = ChatColor.WHITE.toString();
        }

        Location location = entity.getLocation().add(0, entity.getHeight() + 0.1, 0).add(getLocation());
        Hologram hologram = HologramsAPI.createHologram(SpigotRpgPlugin.getInstance(), location);
        //todo in future when entitis are able to casts spells
        IActiveCharacter c = (IActiveCharacter) caster;
        PlayerSkillContext info = c.getSkillInfo(skill.getId());
        if (info == null) {
            return; //nadmin / contextless
        }
        String skillName = info.getSkillData().getSkillName();
        //ran as nadmin skill <id> we have no context to grab name from
        if (skillName == null) {
            skillName = info.getSkill().getId();
        }
        hologram.insertTextLine(0, ChatColor.BOLD + s + skillName);
        VisibilityManager visiblityManager = hologram.getVisibilityManager();
        visiblityManager.setVisibleByDefault(true);
        holograms.put(hologram, System.currentTimeMillis() + 2500L);
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
                }
            }
        }.runTaskTimer(SpigotRpgPlugin.getInstance(), 1L, 30L);
    }

    private static Vector getLocation() {
        int i = rnd.nextInt(displayLocs.length - 1);
        return displayLocs[i];
    }
}

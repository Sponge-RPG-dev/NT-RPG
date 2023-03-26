package cz.neumimto.rpg.spigot.features;

import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.localization.LocalizationService;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillService;
import cz.neumimto.rpg.common.utils.rng.XORShiftRnd;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.damage.SpigotDamageService;
import cz.neumimto.rpg.spigot.events.skill.SpigotSkillPostUsageEvent;
import cz.neumimto.rpg.spigot.utils.VectorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@ResourceLoader.ListenerClass(HologramsExpansion.SKILLCAST_HOLOGRAMS)
public class HologramsExpansion implements Listener {

    public static final String SKILLCAST_HOLOGRAMS = "skillcast_holograms";

    @Inject
    private SkillService skillService;

    @Inject
    private SpigotDamageService damageService;

    @Inject
    private LocalizationService localizationService;

    private static Map<UUID, Long> holograms = new HashMap<>();
    private static Vector[] displayLocs;
    private static XORShiftRnd rnd = new XORShiftRnd();

    static {
        displayLocs = VectorUtils.circle(new Vector[20], 2);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onSkillCast(SpigotSkillPostUsageEvent event) {
        ISkill skill = event.getSkill();
        String damageType = skill.getDamageType();
        IEntity caster = event.getCaster();
        Entity entity = (Entity) caster.getEntity();

        Location location = entity.getLocation().add(0, entity.getHeight() + 0.1, 0).add(getLocation());
        Display hologram = location.getWorld().spawn(location, Display.class);
        hologram.setViewRange(16 * 3);
        hologram.setBillboard(Display.Billboard.CENTER);

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
        hologram.customName(Component.text(skillName));
        holograms.put(hologram.getUniqueId(), System.currentTimeMillis() + 2500L);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        @NotNull Entity[] entities = event.getChunk().getEntities();
        for (Entity entity : entities) {
            if (holograms.containsKey(entity.getUniqueId())) {
                holograms.remove(entity.getUniqueId());
                entity.remove();
            }
        }
    }

    public void init() {

        new BukkitRunnable() {

            @Override
            public void run() {
                Iterator<Map.Entry<UUID, Long>> iterator = holograms.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<UUID, Long> next = iterator.next();
                    UUID key = next.getKey();
                    if (next.getValue() < System.currentTimeMillis()) {
                        Entity entity = Bukkit.getServer().getEntity(key);
                        if (entity != null) {
                            entity.remove();
                        }
                        iterator.remove();
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

package cz.neumimto.rpg.sponge.bridges;


import com.flowpowered.math.vector.Vector2d;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.api.utils.rng.XORShiftRnd;
import cz.neumimto.rpg.sponge.SpongeRpgPlugin;
import cz.neumimto.rpg.sponge.damage.SpongeDamageService;
import cz.neumimto.rpg.sponge.events.skill.SpongeSkillPostUsageEvent;
import cz.neumimto.rpg.sponge.skills.NDamageType;
import cz.neumimto.rpg.sponge.utils.math.VectorUtils;
import de.randombyte.holograms.api.HologramsService;
import de.randombyte.holograms.api.HologramsService.Hologram;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class HologramsListener {

    @Inject
    private SkillService skillService;

    @Inject
    private SpongeDamageService damageService;

    @Inject
    private LocalizationService localizationService;

    private static Map<Hologram, Long> holograms = new HashMap<>();
    private static Map<DamageType, TextColor> colors = new HashMap<>();

    private HologramsService hologramsService;

    static {
        put(NDamageType.FIRE, TextColors.RED);
        put(DamageTypes.MAGMA, TextColors.RED);

        put(DamageTypes.SUFFOCATE, TextColors.BLUE);
        put(DamageTypes.DROWN, TextColors.BLUE);
        put(NDamageType.LIGHTNING, TextColors.YELLOW);

        put(DamageTypes.MAGIC, TextColors.DARK_AQUA);

        put(DamageTypes.VOID, TextColors.DARK_PURPLE);
        put(DamageTypes.ATTACK, TextColors.GRAY);
        put(DamageTypes.SWEEPING_ATTACK, TextColors.GRAY);

        displayLocs = VectorUtils.circle(new Vector2d[20], 2);
    }

    public void init() {

        Sponge.getEventManager().registerListeners(SpongeRpgPlugin.getInstance(), this);
        hologramsService = Sponge.getServiceManager().provide(HologramsService.class).get();

        Sponge.getScheduler().createTaskBuilder()
                .intervalTicks(30)
                .execute(() -> {
                    Iterator<Map.Entry<Hologram, Long>> iterator = holograms.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Hologram, Long> next = iterator.next();
                        Hologram key = next.getKey();
                        if (next.getValue() < System.currentTimeMillis()) {
                            key.remove();
                        }
                    }
                })
                .submit(SpongeRpgPlugin.getInstance());

    }

    private static Vector2d[] displayLocs;
    private static XORShiftRnd rnd = new XORShiftRnd();


    private static void put(DamageType damageCause, TextColor tx) {
        colors.put(damageCause, tx);
    }

    private static Vector2d getLocation() {
        int i = rnd.nextInt(displayLocs.length - 1);
        return displayLocs[i];
    }


    @Listener
    public void onSkillCast(SpongeSkillPostUsageEvent event) {
        ISkill skill = event.getSkill();
        String damageTypes = skill.getDamageType();
        IEntity caster = event.getCaster();
        Living entity = (Living) caster.getEntity();

        Optional<DamageType> type = Sponge.getRegistry().getType(DamageType.class, damageTypes);
        DamageType damageType = type.get();
        TextColor s = colors.get(damageType);
        if (s == null) {
            s = TextColors.WHITE;
        }


        IActiveCharacter c = (IActiveCharacter) caster;
        PlayerSkillContext info = c.getSkillInfo(skill.getId());
        String skillName = info.getSkillData().getSkillName();
        //ran as nadmin skill <id> we have no context to grab name from
        if (skillName == null) {
            skillName = info.getSkill().getId();
        }

        Vector2d vec = getLocation();
        Location location = entity.getLocation().add(vec.getX(), 2, vec.getY());
        Optional<HologramsService.Hologram> opt = hologramsService.createHologram(location, Text.of(s, skillName));
        opt.ifPresent(o -> holograms.put(o, System.currentTimeMillis() + 2500L));
    }

}


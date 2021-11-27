package cz.neumimto.rpg.spigot.skills;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.effects.EffectBase;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.effects.IEffect;
import cz.neumimto.rpg.common.entity.IEffectConsumer;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillNodes;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.mech.DamageMechanic;
import cz.neumimto.rpg.common.skills.types.ActiveSkill;
import cz.neumimto.rpg.nms.NMSHandler;
import cz.neumimto.rpg.spigot.Resourcepack;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import de.slikey.effectlib.effect.SphereEffect;
import de.slikey.effectlib.util.DynamicLocation;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.inject.Inject;

@AutoService(ISkill.class)
@ResourceLoader.Skill("ntrpg:fire_remnant")
public class FireRemnant extends ActiveSkill<ISpigotCharacter> {

    @Inject
    private EffectService effectService;

    @Inject
    private DamageMechanic damageMechanic;

    @Inject
    private NMSHandler nmsHandler;

    @Override
    public void init() {
        super.init();
        settings.addExpression("velocity-vertical", 1);
        settings.addExpression("velocity-horizontal", 1.5f);
        settings.addExpression(SkillNodes.DURATION, "8000");
        settings.addExpression(SkillNodes.COOLDOWN, "0");
    }

    @Override
    public SkillResult cast(ISpigotCharacter character, PlayerSkillContext ctx) {
        FireRemnantEffect effect = (FireRemnantEffect) character.getEffect(FireRemnantEffect.name);
        if (effect == null) {
            Player player = character.getPlayer();
            Location loc = player.getLocation();
            Location spawnPoint = loc.clone().add(loc.getDirection().multiply(2));
            ArmorStand armorStand = Resourcepack.fireRemnant(spawnPoint);
            armorStand.setRotation(player.getEyeLocation().getYaw(), player.getEyeLocation().getPitch());


            float multiplier = (90.0f + player.getEyeLocation().getPitch()) / 50.0f;


            double velVert = ctx.getDoubleNodeValue("velocity-vertical");
            double velHor = ctx.getDoubleNodeValue("velocity-horizontal");
            velVert = Math.min(velVert, 2.0);

            Vector direction = loc.getDirection();
            Vector velocity = player.getVelocity().clone();
            velocity.setY(velVert);

            direction.setY(0).normalize().multiply(multiplier);
            velocity.multiply(new Vector(velHor, 1.0, velHor));
            armorStand.setVelocity(velocity);

            double damage = ctx.getDoubleNodeValue(SkillNodes.DAMAGE);
            long duration = ctx.getLongNodeValue(SkillNodes.DURATION);
            effectService.addEffect(new FireRemnantEffect(character, armorStand, duration, damage));
            return SkillResult.OK_NO_COOLDOWN;
        } else {

            Location location = effect.remnant.getLocation();
            Player player = character.getPlayer();
            player.teleport(location);
            effect.process();
            return SkillResult.OK;
        }
    }

    public class FireRemnantEffect extends EffectBase {

        public static final String name = "fireremnanteff";
        private final ArmorStand remnant;
        private double damage;

        private SphereEffect particles;

        public FireRemnantEffect(IEffectConsumer consumer, ArmorStand remnant, long duration,
                                 double damage) {
            super(name, consumer);
            this.remnant = remnant;
            this.damage = damage;
            setPeriod(1L);
            setDuration(duration);
            setStackable(false, null);
            particles = new SphereEffect(SpigotRpgPlugin.getEffectManager());
            particles.setDynamicTarget(new DynamicLocation(this.remnant));
            particles.infinite();
            particles.particle = Particle.DRIP_LAVA;

        }

        @Override
        public void onTick(IEffect self) {
            remnant.getWorld().spawnParticle(Particle.DRIP_LAVA, remnant.getLocation(), 10);
        }

        @Override
        public void onRemove(IEffect self) {
            remnant.remove();
            SpigotRpgPlugin.getEffectManager().done(particles);
        }

        public void process() {
            Entity entitty = (Entity) getConsumer().getEntity();
            entitty.teleport(remnant);
            if (damage > 0) {
                nmsHandler.spawnFireworkExplosion(remnant.getLocation(),
                        FireworkEffect.builder().withColor(Color.RED,
                                        Color.YELLOW,
                                        Color.fromRGB(214, 76, 45))
                                .with(FireworkEffect.Type.BURST)
                                .build(),
                        remnant.getLocation().getWorld().getPlayers());

                remnant.getLocation().getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, remnant.getLocation(), 2);
            }
            setDuration(0);
        }

        public double getDamage() {
            return damage;
        }

        public void setDamage(double damage) {
            this.damage = damage;
        }
    }
}

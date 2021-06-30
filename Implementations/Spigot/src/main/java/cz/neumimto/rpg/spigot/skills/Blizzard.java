package cz.neumimto.rpg.spigot.skills;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import de.slikey.effectlib.effect.CloudEffect;
import de.slikey.effectlib.util.RandomUtils;
import org.bukkit.Location;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import javax.inject.Singleton;

@Singleton
@AutoService(ISkill.class)
@ResourceLoader.Skill("ntrpg:blizzard")
public class Blizzard extends ActiveSkill<ISpigotCharacter> {

    @Override
    public void init() {
        super.init();
        setDamageType(EntityDamageEvent.DamageCause.FIRE.name());
        settings.addNode(SkillNodes.DAMAGE, 10);
        settings.addNode(SkillNodes.DURATION, 1.5f);
        addSkillType(SkillType.SUMMON);
        addSkillType(SkillType.PROJECTILE);
        addSkillType(SkillType.ELEMENTAL);
        addSkillType(SkillType.FIRE);
    }

    @Override
    public SkillResult cast(ISpigotCharacter character, PlayerSkillContext skillContext) {
        int duration = skillContext.getIntNodeValue(SkillNodes.DURATION);

        CloudEffect cloudEffect = new CloudEffect(SpigotRpgPlugin.getEffectManager()) {
            @Override
            public void onRun() {
                Location location = getLocation();
                location.add(0, yOffset, 0);
                for (int i = 0; i < 50; i++) {
                    Vector v = RandomUtils.getRandomCircleVector().multiply(RandomUtils.random.nextDouble() * cloudSize);
                    display(cloudParticle, location.add(v), cloudColor, 0, 7);
                    location.subtract(v);
                }
                Location l = location.add(0, .2, 0);
            }
        };
        cloudEffect.cloudSize = 15;
        cloudEffect.duration = duration;
        cloudEffect.setLocation(character.getEntity().getLocation());
        SpigotRpgPlugin.getEffectManager().start(cloudEffect);

        return SkillResult.OK;
    }
}

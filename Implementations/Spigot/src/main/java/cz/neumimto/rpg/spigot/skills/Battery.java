package cz.neumimto.rpg.spigot.skills;

import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.entity.IEntityType;
import cz.neumimto.rpg.common.resources.Resource;
import cz.neumimto.rpg.common.resources.ResourceService;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillNodes;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.tree.SkillType;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.skills.particles.CircularYIncrementingEffect;
import org.bukkit.Color;
import org.bukkit.Particle;

import javax.inject.Inject;

@AutoService(ISkill.class)
@ResourceLoader.Skill("ntrpg:battery")
public class Battery extends TargetedEntitySkill {

    @Inject
    private SpigotCharacterService characterService;

    @Override
    public void init() {
        settings.addExpression(SkillNodes.AMOUNT, "level * 2 + 10");
        addSkillType(SkillType.BUFF);
    }

    @Override
    public SkillResult castOn(IEntity target, ISpigotCharacter source, PlayerSkillContext info) {
        if (target.getType() != IEntityType.CHARACTER) {
            return SkillResult.CANCELLED;
        }
        ISpigotCharacter targetChar = (ISpigotCharacter) target;
        Resource mana = targetChar.getResource(ResourceService.mana);
        if (mana != null && mana.getType().equals(ResourceService.mana)) {
            mana.setValue(mana.getValue() + info.getDoubleNodeValue(SkillNodes.AMOUNT));

            CircularYIncrementingEffect circleEffect = new CircularYIncrementingEffect(SpigotRpgPlugin.getEffectManager());
            circleEffect.radius = 2;
            circleEffect.color = Color.fromRGB(51, 204, 255);
            circleEffect.enableRotation = true;
            circleEffect.offsetYIncrement = 0.1;
            circleEffect.particle = Particle.BLOCK_DUST;

            circleEffect.setTargetLocation(targetChar.getEntity().getLocation());
            SpigotRpgPlugin.getEffectManager().start(circleEffect);
            return SkillResult.OK;
        }
        return SkillResult.CANCELLED;
    }
}

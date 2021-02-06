package cz.neumimto.rpg.spigot.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.api.entity.IEntityType;
import cz.neumimto.rpg.api.entity.IReservable;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillNodes;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillType;
import cz.neumimto.rpg.common.entity.players.CharacterMana;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import cz.neumimto.rpg.spigot.skills.particles.CircularYIncrementingEffect;
import org.bukkit.Color;
import org.bukkit.Particle;

import javax.inject.Inject;

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
        IReservable mana = targetChar.getMana();
        if (mana instanceof CharacterMana) {
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

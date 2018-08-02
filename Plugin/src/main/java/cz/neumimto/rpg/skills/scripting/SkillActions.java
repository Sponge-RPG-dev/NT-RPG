package cz.neumimto.rpg.skills.scripting;

import cz.neumimto.rpg.IEntity;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.damage.SkillDamageSourceBuilder;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.skills.SkillNodes;
import cz.neumimto.rpg.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;

import java.util.Map;

public class SkillActions {

    public static SkillAction DAMAGE = (caster, target, context) -> {
        if (Utils.canDamage(caster, target.getEntity())) {
            SkillDamageSourceBuilder builder = new SkillDamageSourceBuilder();
            builder.fromSkill(context.getRootContext().getSkill());
            builder.setCaster(caster);
            double damage = (double) context.getRootContext().getSkillInfo().getSkillData()
                    .getSkillSettings()
                    .getLevelNodeValue(SkillNodes.DAMAGE, context.getRootContext().getSkillInfo().getTotalLevel());

            target.getEntity().damage(damage , builder.build());
        }
    };

    public static SkillAction APPLY_EFFECT = (caster, target, context) -> {
        IGlobalEffect globalEffect = NtRpgPlugin.GlobalScope.effectService.getGlobalEffect((String) context.getParam().get(SkillPipelineContext.EFFECT));
        globalEffect.construct()
    };

    public static SkillAction BROADCAST_ALL = (caster, target, context) -> {
        Text text = (Text) context.getParam().get(SkillPipelineContext.BROADCAST_ALL);
        Sponge.getServer().getOnlinePlayers().stream().forEach(player -> player.sendMessage(text));
    };

    public static SkillAction BROADCAST_NEARBY = (caster, target, context) -> {
        Text text = (Text) context.getParam().get(SkillPipelineContext.BROADCAST_ALL);
        //todo
    };
}

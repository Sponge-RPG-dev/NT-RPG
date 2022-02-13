package cz.neumimto.rpg.spigot.bridges.mmoitems;

import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.trigger.TriggerMetadata;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.PlayerStats;
import net.Indyuce.mmoitems.skill.RegisteredSkill;
import net.Indyuce.mmoitems.stat.data.AbilityData;
import org.bukkit.entity.Player;

import java.util.Set;

public class MMOItemSkill extends ActiveSkill<ISpigotCharacter> {

    private RegisteredSkill ability;

    public MMOItemSkill() {
        setDamageType(DamageType.MAGIC.name());
    }

    @Override
    public void init() {
        Set<String> modifiers = ability.getHandler().getModifiers();
        for (String modifier : modifiers) {
            settings.addExpression(modifier, ability.getDefaultModifier(modifier));
        }
    }

    @Override
    public SkillResult cast(ISpigotCharacter character, PlayerSkillContext skillContext) {
        Player player = character.getPlayer();

        Object2DoubleOpenHashMap<String> compSettings = skillContext.getCachedComputedSkillSettings();

        PlayerData playerData = PlayerData.get(player);
        PlayerStats playerStats = new PlayerStats(playerData);
        PlayerMetadata caster = playerStats.newTemporary(EquipmentSlot.MAIN_HAND);

        AbilityData ability = new AbilityData(getAbility(), TriggerType.API);

        for (Object2DoubleMap.Entry<String> e : compSettings.object2DoubleEntrySet()) {
            ability.setModifier(e.getKey(), e.getDoubleValue());
        }

        DamageMetadata damageMetadata = new DamageMetadata();
        if (skillContext.hasNode("damage")) {
            damageMetadata.add(skillContext.getDoubleNodeValue("damage"), DamageType.SKILL);
        }

        AttackMetadata attackMetadata = new AttackMetadata(damageMetadata, caster);

        var cast = ability.cast(new TriggerMetadata(caster, attackMetadata, null));

        return cast.isSuccessful(new SkillMetadata(ability, MMOPlayerData.get(player))) ? SkillResult.OK : SkillResult.CANCELLED;
    }

    public RegisteredSkill getAbility() {
        return ability;
    }

    public void setAbility(RegisteredSkill ability) {
        this.ability = ability;
    }

}

package cz.neumimto.rpg.spigot.bridges.mmoitems;

import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.stat.StatMap;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.Indyuce.mmoitems.ability.Ability;
import net.Indyuce.mmoitems.ability.AbilityMetadata;
import net.Indyuce.mmoitems.api.ItemAttackMetadata;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.PlayerStats;
import net.Indyuce.mmoitems.stat.data.AbilityData;
import org.bukkit.entity.Player;

import java.util.Set;

public class MMOItemSkill extends ActiveSkill<ISpigotCharacter> {

    private Ability ability;

    public MMOItemSkill() {
        setDamageType(DamageType.MAGIC.name());
    }

    @Override
    public void init() {
        Set<String> modifiers = ability.getModifiers();
        for (String modifier : modifiers) {
            settings.addExpression(modifier, ability.getDefaultValue(modifier));
        }
    }

    @Override
    public SkillResult cast(ISpigotCharacter character, PlayerSkillContext skillContext) {
        Player player = character.getPlayer();
        AbilityData abilityData = new AbilityData(getAbility(), Ability.CastingMode.LEFT_CLICK);

        Object2DoubleOpenHashMap<String> compSettings = skillContext.getCachedComputedSkillSettings();

        PlayerData playerData = PlayerData.get(player);
        PlayerStats playerStats = new PlayerStats(playerData);
        StatMap.CachedStatMap stats = playerStats.newTemporary(EquipmentSlot.OTHER);

        for (Object2DoubleMap.Entry<String> e : compSettings.object2DoubleEntrySet()) {
            abilityData.setModifier(e.getKey(), e.getDoubleValue());
        }

        DamageMetadata damageMetadata = new DamageMetadata();
        if (skillContext.hasNode("damage")) {
            damageMetadata.add(skillContext.getDoubleNodeValue("damage"), DamageType.SKILL);
        }
        boolean casted = this.cast(stats, new ItemAttackMetadata(damageMetadata, stats), abilityData);
        return casted ? SkillResult.OK : SkillResult.CANCELLED;
    }

    public Ability getAbility() {
        return ability;
    }

    public boolean cast(StatMap.CachedStatMap stats, ItemAttackMetadata data, AbilityData ability) {

        AbilityMetadata abilityMetadata = ability.getAbility().canBeCast(data, null, ability);
        if (abilityMetadata.isSuccessful()) {
            ability.getAbility().whenCast(data, abilityMetadata);
            return true;
        }
        return false;
    }

    public void setAbility(Ability ability) {
        this.ability = ability;
    }

}

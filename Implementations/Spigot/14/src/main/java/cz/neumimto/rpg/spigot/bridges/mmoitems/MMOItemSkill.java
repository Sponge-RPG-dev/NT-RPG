package cz.neumimto.rpg.spigot.bridges.mmoitems;

import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.mods.SkillContext;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import net.Indyuce.mmoitems.api.ItemAttackResult;
import net.Indyuce.mmoitems.api.ability.Ability;
import net.Indyuce.mmoitems.api.ability.AbilityResult;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.PlayerStats;
import net.Indyuce.mmoitems.stat.data.AbilityData;
import net.mmogroup.mmolib.api.DamageType;
import org.bukkit.entity.Player;

import java.util.Map;
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
            settings.addNode(modifier, 10, 1);
        }
    }

    @Override
    public SkillResult cast(ISpigotCharacter character, PlayerSkillContext info, SkillContext skillContext) {
        Player player = character.getPlayer();
        AbilityData abilityData = new AbilityData(getAbility(), Ability.CastingMode.LEFT_CLICK);
        PlayerData playerData = PlayerData.get(player);
        PlayerStats playerStats = new PlayerStats(playerData);
        PlayerStats.CachedStats stats = playerStats.newTemporary();

        for (Map.Entry<String, Float> e : skillContext.getSkillNodes().entrySet()) {
            abilityData.setModifier(e.getKey(), e.getValue());
        }
        boolean casted = this.cast(stats, new ItemAttackResult(true, DamageType.SKILL), abilityData);
        skillContext.next(character, info, skillContext.result(casted ? SkillResult.OK : SkillResult.CANCELLED));
    }

    public Ability getAbility() {
        return ability;
    }

    public boolean cast(PlayerStats.CachedStats stats, ItemAttackResult attack, AbilityData ability) {
        AbilityResult abilityResult = ability.getAbility().whenRan(stats, null, ability, attack);
        if (abilityResult.isSuccessful()) {
            ability.getAbility().whenCast(stats, abilityResult, attack);
            return true;
        }
        return false;
    }

    public void setAbility(Ability ability) {
        this.ability = ability;
    }

}

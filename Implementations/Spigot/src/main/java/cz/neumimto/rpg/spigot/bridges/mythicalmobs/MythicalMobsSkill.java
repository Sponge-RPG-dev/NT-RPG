package cz.neumimto.rpg.spigot.bridges.mythicalmobs;

import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.skills.Skill;
import net.Indyuce.mmoitems.api.ItemAttackResult;
import net.Indyuce.mmoitems.api.ability.AbilityResult;
import net.Indyuce.mmoitems.api.player.PlayerStats;
import net.Indyuce.mmoitems.stat.data.AbilityData;
import net.mmogroup.mmolib.api.DamageType;
import org.bukkit.entity.Player;

public class MythicalMobsSkill extends ActiveSkill<ISpigotCharacter> {

    protected MythicMobs mm;
    protected Skill mmSkill;

    public MythicalMobsSkill() {
        setDamageType(DamageType.MAGIC.name());
    }

    @Override
    public void init() {
        mm = MythicMobs.inst();
    }

    @Override
    public SkillResult cast(ISpigotCharacter character, PlayerSkillContext skillContext) {
        Player player = character.getPlayer();
        float power = (float) skillContext.getCachedComputedSkillSettings().getDouble("power");
        boolean casted = MythicMobs.inst().getAPIHelper().castSkill(player, mmSkill.getInternalName(), power);
        return casted ? SkillResult.OK : SkillResult.CANCELLED;
    }

    public void setMmSkill(Skill mmSkill) {
        this.mmSkill = mmSkill;
    }
}

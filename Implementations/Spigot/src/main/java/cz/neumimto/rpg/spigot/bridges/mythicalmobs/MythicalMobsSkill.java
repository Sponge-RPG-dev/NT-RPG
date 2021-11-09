package cz.neumimto.rpg.spigot.bridges.mythicalmobs;

import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.skills.Skill;
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

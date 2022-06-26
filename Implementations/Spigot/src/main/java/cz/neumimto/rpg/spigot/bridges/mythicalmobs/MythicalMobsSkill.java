package cz.neumimto.rpg.spigot.bridges.mythicalmobs;

import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.lib.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MythicalMobsSkill extends ActiveSkill<ISpigotCharacter> {

    protected MythicBukkit mm;
    protected Skill mmSkill;

    public MythicalMobsSkill() {
        setDamageType(DamageType.MAGIC.name());
    }

    @Override
    public void init() {
        mm = MythicBukkit.inst();
    }

    @Override
    public SkillResult cast(ISpigotCharacter character, PlayerSkillContext skillContext) {
        Player player = character.getPlayer();
        float power = (float) skillContext.getCachedComputedSkillSettings().getDouble("power");

        List<Entity> targets = new ArrayList<>();
        targets.add(player);

        boolean casted = MythicBukkit.inst().getAPIHelper().castSkill(player, mmSkill.getInternalName(), player, player.getLocation(), targets, null, power);

        return casted ? SkillResult.OK : SkillResult.CANCELLED;
    }

    public void setMmSkill(Skill mmSkill) {
        this.mmSkill = mmSkill;
    }
}

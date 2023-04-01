package cz.neumimto.rpg.spigot.bridges.mythicalmobs;

import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.types.ActiveSkill;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacter;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.lib.damage.DamageType;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MythicalMobsSkill extends ActiveSkill<SpigotCharacter> {

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
    public SkillResult cast(SpigotCharacter character, PlayerSkillContext skillContext) {
        Player player = character.getPlayer();
        float power = (float) skillContext.getCachedComputedSkillSettings().getDouble("power");

        List<Entity> targets = new ArrayList<>();
        targets.add(player);

        boolean casted = MythicBukkit.inst().getAPIHelper().castSkill(player,
                mmSkill.getInternalName(),
                player,
                player.getLocation(),
                targets,
                null,
                power,
                skillMetadata -> {
                    Object2DoubleOpenHashMap<String> sett = skillContext.getCachedComputedSkillSettings();
                    for (Object2DoubleMap.Entry<String> entry : sett.object2DoubleEntrySet()) {
                        skillMetadata.getParameters().put(entry.getKey(), String.valueOf(entry.getDoubleValue()));
                    }
                });

        return casted ? SkillResult.OK : SkillResult.CANCELLED;
    }

    public void setMmSkill(Skill mmSkill) {
        this.mmSkill = mmSkill;
    }
}

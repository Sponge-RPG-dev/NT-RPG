package cz.neumimto.rpg.spigot.skills;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.google.auto.service.AutoService;
import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.damage.DamageService;
import cz.neumimto.rpg.common.effects.EffectService;
import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillNodes;
import cz.neumimto.rpg.common.skills.SkillResult;
import cz.neumimto.rpg.common.skills.tree.SkillType;
import cz.neumimto.rpg.spigot.Resourcepack;
import cz.neumimto.rpg.spigot.effects.common.BleedingEffect;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.packetwrapper.PacketHandler;
import cz.neumimto.rpg.spigot.skills.utils.AbstractPacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
@AutoService(ISkill.class)
@ResourceLoader.Skill("ntrpg:slash")
public class Slash extends TargetedEntitySkill {

    @Inject
    private DamageService damageService;

    @Inject
    private EffectService effectService;

    @Override
    public void init() {
        super.init();
        addSkillType(SkillType.PHYSICAL);
        setDamageType("PHYSICAL");
    }

    @Override
    public SkillResult castOn(IEntity target, ISpigotCharacter source, PlayerSkillContext info) {
        double weaponDamageMult = info.getDoubleNodeValue(SkillNodes.MULTIPLIER);
        double max = info.getDoubleNodeValue(SkillNodes.MAX);

        double damage = Math.min(max, weaponDamageMult * source.getWeaponDamage());

        if (damage > 0) {
            damageService.damageEntity(target, damage);
        }

        Player entity = source.getEntity();

        List<AbstractPacket> packets = PacketHandler.amorStand(entity.getLocation(),
                Resourcepack.SLASH_01,
                EnumWrappers.ItemSlot.HEAD,
                entity.getEyeLocation().getYaw(), 750L);

        packets.add(PacketHandler.animateMainHand(entity));

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getLocation().distanceSquared(entity.getLocation()) <= 500) {
                for (AbstractPacket packet : packets) {
                    packet.sendPacket(onlinePlayer);
                }
            }
        }


        double bleedingDamage = info.getDoubleNodeValue("bleed-damage");
        int bleedingChance = info.getIntNodeValue("bleed-chance");
        if (bleedingDamage > 0 && bleedingChance > 0) {
            int i = ThreadLocalRandom.current().nextInt(100);
            if (bleedingChance > i) {
                long bleedingDuration = info.getLongNodeValue("bleed-duration");
                long bleedingPeriod = info.getLongNodeValue("bleed-period");
                BleedingEffect bleedingEffect = new BleedingEffect(target, bleedingDuration, bleedingPeriod, bleedingDamage);
                effectService.addEffect(bleedingEffect, this);
            }
        }

        return SkillResult.OK;
    }

}

package cz.neumimto.rpg.spigot.bridges;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import net.islandearth.rpgregions.rewards.DiscoveryReward;
import org.bukkit.entity.Player;

public class RpgRegionsClassExpReward extends DiscoveryReward {

    public static final String SOURCE = "RPG_REGIONS";

    private double exp;

    public RpgRegionsClassExpReward(double exp) {
        this.exp = exp;
    }

    public RpgRegionsClassExpReward() {

    }

    @Override
    public void award(Player player) {
        IActiveCharacter character = Rpg.get().getCharacterService().getCharacter(player.getUniqueId());
        if (!character.isStub()) {
            Rpg.get().getCharacterService().addExperiences(character, exp, SOURCE);
        }
    }

    @Override
    public String getName() {
        return "ClassExpReward";
    }
}

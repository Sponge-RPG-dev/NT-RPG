package cz.neumimto.rpg.spigot.bridges.rpgregions;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.api.RPGRegionsAPI;
import net.islandearth.rpgregions.gui.GuiEditable;
import net.islandearth.rpgregions.rewards.DiscoveryReward;
import org.bukkit.entity.Player;

public class RpgRegionsClassExpReward extends DiscoveryReward {

    public static final String SOURCE = "RPG_REGIONS";

    @GuiEditable("Experience")
    private double exp;

    public RpgRegionsClassExpReward(double exp) {
        super(RPGRegionsAPI.getAPI());
        this.exp = exp;
    }

    public RpgRegionsClassExpReward(IRPGRegionsAPI api) {
        super(api);
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

    @Override
    public String getPluginRequirement() {
        return "NT-RPG";
    }
}

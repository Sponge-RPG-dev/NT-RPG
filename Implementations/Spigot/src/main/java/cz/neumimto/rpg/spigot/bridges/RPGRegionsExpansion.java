package cz.neumimto.rpg.spigot.bridges;

import cz.neumimto.rpg.spigot.bridges.rpgregions.RpgRegionsClassExpReward;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.api.RPGRegionsAPI;
import net.islandearth.rpgregions.managers.registry.RPGRegionsRegistry;
import net.islandearth.rpgregions.rewards.DiscoveryReward;
import net.islandearth.rpgregions.rewards.RegionRewardRegistry;

public class RPGRegionsExpansion {
    
    public RPGRegionsExpansion() {
        IRPGRegionsAPI api = RPGRegionsAPI.getAPI();
        RPGRegionsRegistry<DiscoveryReward> registry = (RegionRewardRegistry) api.getManagers().getRegistry(RegionRewardRegistry.class);
        if (registry != null) {
            registry.register(RpgRegionsClassExpReward.class);
        }
    }
}

package cz.neumimto.rpg.spigot.bridges.luckperms;


import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.events.SpigotCharacterGainedExperiencesEvent;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class LuckpermsExpansion implements Listener {

    private ContextManager contextManager;

    @Inject
    private CharacaterCalculator characaterCalculator;

    private final List<ContextCalculator<Player>> registeredCalculators = new ArrayList<>();

    public void init() {
        this.contextManager = SpigotRpgPlugin.getLuckPerms().getContextManager();
        this.characaterCalculator.registerContexts();
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onExpGain(SpigotCharacterGainedExperiencesEvent event) {
        if (event.getExpSource() == null) {
            return;
        }

        Player player = (Player) event.getCharacter().getEntity();
        CachedMetaData metaData = SpigotRpgPlugin.getLuckPerms().getPlayerAdapter(Player.class).getMetaData(player);


        String expSource = event.getExpSource();

        double exp = metaData.getMetaValue("ntrpg.exp.mult." + expSource, Double::parseDouble).orElse(1D);
        event.setExp(exp * event.getExp());

    }
}

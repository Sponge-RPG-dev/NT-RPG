package cz.neumimto.rpg.spigot.features;


import cz.neumimto.rpg.common.ResourceLoader;
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

@ResourceLoader.ListenerClass(LuckpermsExpansion.LUCKPERMS_EXPMULT)
public class LuckpermsExpansion implements Listener {

    public static final String LUCKPERMS_EXPMULT = "luckperms_expmulat";


    public void init() {

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

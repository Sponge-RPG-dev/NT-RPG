package cz.neumimto.rpg.spigot.bridges.luckperms;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class LuckpermsExpansion {

    private ContextManager contextManager;

    @Inject
    private CharacaterCalculator characaterCalculator;
    private final List<ContextCalculator<Player>> registeredCalculators = new ArrayList<>();

    public void init() {
        LuckPerms luckPerms = Bukkit.getServer().getServicesManager().load(LuckPerms.class);
        this.contextManager = luckPerms.getContextManager();
        characaterCalculator.registerContexts();
    }

}

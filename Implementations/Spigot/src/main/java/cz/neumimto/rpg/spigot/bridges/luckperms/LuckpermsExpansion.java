package cz.neumimto.rpg.spigot.bridges.luckperms;


import cz.neumimto.rpg.spigot.events.SpigotCharacterGainedExperiencesEvent;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextManager;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeBuilder;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.metadata.NodeMetadataKey;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class LuckpermsExpansion implements Listener {

    private ContextManager contextManager;

    @Inject
    private CharacaterCalculator characaterCalculator;

    private final List<ContextCalculator<Player>> registeredCalculators = new ArrayList<>();

    //todo move elsewhere
    public static LuckPerms luckPerms;

    public void init() {
        this.luckPerms = Bukkit.getServer().getServicesManager().load(LuckPerms.class);
        this.contextManager = luckPerms.getContextManager();
        characaterCalculator.registerContexts();
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onExpGain(SpigotCharacterGainedExperiencesEvent event) {
        if (event.getExpSource() == null) {
            return;
        }

        Player player = (Player) event.getCharacter().getEntity();
        CachedMetaData metaData = luckPerms.getPlayerAdapter(Player.class).getMetaData(player);


        String expSource = event.getExpSource();

        double exp = metaData.getMetaValue("ntrpg.exp.mult." + expSource, Double::parseDouble).orElse(1D);
        event.setExp(exp * event.getExp());

        //luckPerms.getUserManager().getUser(player.getUniqueId()).transientData().add(Group.builder("ntrpg.player.").build());
    }
}

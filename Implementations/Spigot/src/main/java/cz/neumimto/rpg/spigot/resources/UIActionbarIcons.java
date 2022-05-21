package cz.neumimto.rpg.spigot.resources;

import cz.neumimto.rpg.common.resources.Resource;
import cz.neumimto.rpg.common.utils.MathUtils;
import cz.neumimto.rpg.spigot.bridges.DatapackManager;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityAirChangeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class UIActionbarIcons implements Consumer<ISpigotCharacter>, Listener {

    static Map<String, Component[]> resource = new HashMap<>();

    public static String[] resTypes;


    public static void init(ResourceGui resourceGui) {
        resTypes = new String[resourceGui.display.size()];

        int i =0;
        for (ResourceGui.Display display : resourceGui.display) {
            resTypes[i] = display.resource;
            Component[] collect = display.array.stream()
                    .map(a -> DatapackManager.instance.resolveGlyphs(null, a))
                    .toList()
                    .toArray(Component[]::new);
            resource.put(resTypes[i], collect);
            i++;
        }

    }

    @Override
    public void accept(ISpigotCharacter character) {
        Player player =  character.getEntity();

        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        if (player.getRemainingAir() != player.getMaximumAir()) {
            return;
        }

        Component c = Component.empty();
        for (String resType : resTypes) {

            Resource characterRes = character.getResource(resType);
            if (characterRes.getMaxValue() == 0) {
                continue;
            }
            double percentage = MathUtils.getPercentage(characterRes.getValue(), characterRes.getMaxValue());
            percentage = percentage > 100 ? 100 : percentage;
            percentage = percentage < 0 ? 0 : percentage;
            Component r = resource.get(characterRes.getType())[(int) Math.round(percentage / 5)];
            c = c.append(r);
        }
        player.sendActionBar(c);
    }


    @EventHandler
    public void onAirChange(EntityAirChangeEvent event) {
        Entity e = event.getEntity();
        if (e instanceof Player player) {
            if (player.getRemainingAir() != player.getMaximumAir()) {
                return;
            } else {
                player.sendActionBar(Component.empty());
            }
        }
    }
}

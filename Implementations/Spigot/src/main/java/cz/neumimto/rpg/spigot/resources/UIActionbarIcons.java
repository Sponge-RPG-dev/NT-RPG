package cz.neumimto.rpg.spigot.resources;

import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.resources.Resource;
import cz.neumimto.rpg.common.resources.ResourceService;
import cz.neumimto.rpg.common.utils.MathUtils;
import cz.neumimto.rpg.spigot.bridges.DatapackManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class UIActionbarIcons implements Runnable {

    @Inject
    private CharacterService<IActiveCharacter> characterService;

    @Inject
    private ResourceService resourceService;

    Map<String, Component[]> resource = new HashMap<>();

    public static String[] resTypes;

    public UIActionbarIcons(ResourceGui resourceGui) {
        resTypes = new String[resourceGui.display.size()];

        int i =0;
        for (ResourceGui.Display display : resourceGui.display) {
            resTypes[i] = display.resource;
            Component[] collect = display.array.stream()
                    .map(a -> DatapackManager.instance.resolveGlyphs(null, a))
                    .toList()
                    .toArray(Component[]::new);
            this.resource.put(resTypes[i], collect);
            i++;
        }
    }

    @Override
    public void run() {
        for (IActiveCharacter character : characterService.getCharacters()) {
            Player player = (Player) character.getEntity();

            Component c = Component.empty();
            for (String resType : resTypes) {

                Resource mana = character.getResource(resType);

                double percentage = MathUtils.getPercentage(mana.getValue(), mana.getMaxValue());
                percentage = percentage > 100 ? 100 : percentage;
                percentage = percentage < 0 ? 0 : percentage;
                Component r = resource.get(mana.getType())[(int) Math.round(percentage / 5)];
                c.append(r);
            }
            player.sendActionBar(c);
        }
    }

}

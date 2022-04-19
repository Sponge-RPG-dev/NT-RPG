package cz.neumimto.rpg.spigot.resources;

import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import net.kyori.adventure.text.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIActionbarIcons implements Runnable {

    @Inject
    private CharacterService<IActiveCharacter> characterService;

    Map<String, List<Component>> resource = new HashMap<>();

    @Override
    public void run() {
        for (IActiveCharacter character : characterService.getCharacters()) {

        }
    }
}

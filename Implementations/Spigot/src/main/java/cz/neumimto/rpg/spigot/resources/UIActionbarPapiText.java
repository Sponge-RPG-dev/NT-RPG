package cz.neumimto.rpg.spigot.resources;

import cz.neumimto.rpg.common.entity.players.CharacterService;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.resources.ResourceService;

import javax.inject.Inject;

public class UIActionbarPapiText implements Runnable {

    @Inject
    private CharacterService<IActiveCharacter> characterService;

    @Inject
    private ResourceService resourceService;

    public UIActionbarPapiText(ResourceGui resourceGui) {
    }

    @Override
    public void run() {

    }
}

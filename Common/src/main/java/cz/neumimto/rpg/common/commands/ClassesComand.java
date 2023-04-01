package cz.neumimto.rpg.common.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@CommandAlias("classes")
@CommandPermission("ntrpg.player.info.classes")
public class ClassesComand extends BaseCommand {

    @Inject
    private InfoCommands infoCommands;

    @Default
    @CommandCompletion("@classtypes")
    public void classes(ActiveCharacter issuer, @Optional String type) {
        infoCommands.showClassesCommand(issuer, type);
    }
}

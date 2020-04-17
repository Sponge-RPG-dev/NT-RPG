package cz.neumimto.rpg.common.commands;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;

import javax.inject.Inject;

@Subcommand("classes")
@CommandPermission("ntrpg.info.classes")
public class ClassesComand {

    @Inject
    private InfoCommands infoCommands;

    @Default
    public void classes(IActiveCharacter issuer, @Optional String type) {
        infoCommands.showClassesCommand(issuer, type);
    }
}

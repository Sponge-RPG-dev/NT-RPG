package cz.neumimto.rpg.common.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@CommandAlias("classes")
@CommandPermission("ntrpg.info.classes")
public class ClassesComand extends BaseCommand {

    @Inject
    private InfoCommands infoCommands;

    @Default
    public void classes(IActiveCharacter issuer, @Optional String type) {
        infoCommands.showClassesCommand(issuer, type);
    }
}

package cz.neumimto.rpg;

import co.aikar.commands.CommandManager;

import java.io.File;
import java.util.logging.Logger;

public interface NtRpgBootstrap {
    record Data(Object plugin, File workingDir, CommandManager commandManager, Logger logger) {
    }

    void enable(Data data);

    void disable();
}

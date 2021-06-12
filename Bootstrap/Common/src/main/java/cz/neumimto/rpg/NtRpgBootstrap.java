package cz.neumimto.rpg;

import java.io.File;
import java.util.logging.Logger;

public interface NtRpgBootstrap {
    record Data(Object plugin, File workingDir){};
    void enable(Data data);
    void disable();
}

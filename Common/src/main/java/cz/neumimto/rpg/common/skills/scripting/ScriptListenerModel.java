package cz.neumimto.rpg.common.skills.scripting;

import com.electronwill.nightconfig.core.conversion.Path;
import com.typesafe.config.Optional;

public class ScriptListenerModel {

    @Path("Id")
    public String id;

    @Optional
    @Path("Script")
    public String script;

    @Optional
    @Path("Event")
    public String event;
}

package cz.neumimto.rpg.common.configuration;

import com.electronwill.nightconfig.core.conversion.Path;

import java.util.ArrayList;
import java.util.List;

public class SkillDumpConfigurations {
    @Path("skills")
    private List<SkillDumpConfiguration> list;

    public SkillDumpConfigurations(List<SkillDumpConfiguration> list) {
        this.list = list;
    }

    public List<SkillDumpConfiguration> getList() {
        return list;
    }

}

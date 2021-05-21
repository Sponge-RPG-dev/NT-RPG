package cz.neumimto.rpg.api.skills.scripting;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.localization.Arg;
import cz.neumimto.rpg.api.skills.ISkillNodeDescription;

import javax.script.SimpleBindings;
import java.util.ArrayList;
import java.util.List;

public class ScriptedSkillNodeDescription implements ISkillNodeDescription {

    private List<String> template = new ArrayList<>();
    private String function;

    public void setTemplate(List<String> template) {
        this.template = template;
    }


    @Override
    public List<String> getDescription(IActiveCharacter character) {
        SimpleBindings simpleBindings = new SimpleBindings();
        simpleBindings.put("character", character);
        Arg arg = new Arg();
        simpleBindings.put("arg", arg);
      //  try {
            Object o = Rpg.get().getScriptEngine().executeScript(function, simpleBindings);
                    //;.eval(function, simpleBindings);
      //  } catch (ScriptException e) {
      //      Log.error("Could not build skill node description", e);
      //  }

        return Rpg.get().getLocalizationService().translateRaw(template, arg);
    }

    public void setJSFunction(String function) {
        this.function = function;
    }
}

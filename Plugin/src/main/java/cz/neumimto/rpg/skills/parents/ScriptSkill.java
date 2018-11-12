package cz.neumimto.rpg.skills.parents;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.scripting.JSLoader;
import cz.neumimto.rpg.skills.configs.ScriptSkillModel;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;

import java.io.IOException;

import javax.script.ScriptException;

public interface ScriptSkill<T> {

	default void initScript() {
		ScriptSkillModel model = getModel();
		String s = bindScriptToTemplate(model);
		try {
			JSLoader.getEngine().eval(s);
			T t = (T) JSLoader.getEngine().eval(model.getId().replaceAll(":", "") + "_executor");
			setExecutor(t);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}

	void setExecutor(T ses);

	ScriptSkillModel getModel();

	void setModel(ScriptSkillModel model);

	default String bindScriptToTemplate(ScriptSkillModel model) {
		Asset asset = Sponge.getAssetManager().getAsset(NtRpgPlugin.GlobalScope.plugin, getTemplateName()).get();
		try {
			String s = asset.readString();
			s = s.replaceAll("\\{\\{skill\\.id}}", model.getId().replaceAll(":", ""));
			s = s.replaceAll("\\{\\{userScript}}", model.getScript());
			s = fill(s);
			return s;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	String getTemplateName();

	default String fill(String asset) {
		return asset;
	}
}

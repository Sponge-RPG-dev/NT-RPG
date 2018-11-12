package cz.neumimto.rpg.skills.utils;

import java.util.ArrayList;
import java.util.List;

public class SkillLoadingErrors {

	private List<String> errors = new ArrayList<>();
	private String name;

	public SkillLoadingErrors(String name) {
		this.name = name;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void log(String k, Object... params) {
		errors.add(String.format(k, params));
	}

	public void log(String k) {
		errors.add(k);
	}
}

package cz.neumito.rpg.rest.model;

/**
 * Created by Nt on 8.5.2016.
 */
public class SkillDataRequestBean {
	private String classname;
	private String tree;
	private String skill;

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public String getTree() {
		return tree;
	}

	public void setTree(String tree) {
		this.tree = tree;
	}
}

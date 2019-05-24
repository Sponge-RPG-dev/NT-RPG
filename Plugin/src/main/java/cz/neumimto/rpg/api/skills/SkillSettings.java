/*
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package cz.neumimto.rpg.api.skills;

import cz.neumimto.rpg.api.skills.ISkillNode;
import cz.neumimto.rpg.players.attributes.Attribute;
import cz.neumimto.rpg.scripting.JsBinding;
import org.spongepowered.api.Sponge;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static cz.neumimto.rpg.api.logging.Log.error;

/**
 * Created by NeumimTo on 14.2.2015.
 */
@JsBinding(JsBinding.Type.CLASS)
public class SkillSettings {

	public static final String bonus = "_levelbonus";

	private Map<String, Float> skillSettings = new HashMap<>();
	private Map<String, String> objMap = new HashMap<>();

	public void addNode(ISkillNode n, float val, float levelbonux) {
		addNode(n.toString(), val, levelbonux);
	}

	public void addNode(String n, float val, float levelbonux) {
		addNode(n, val);
		addNode(n + bonus, levelbonux);
	}

	public void addAttributeNode(ISkillNode n, Attribute attribute, float val) {
		addAttributeNode(n.value(), attribute, val);
	}

	public void addAttributeNode(String n, Attribute attribute, float val) {
		addNode(n + "_per_" + attribute.getId(), val);
	}

	public boolean hasNode(String s) {
		return skillSettings.containsKey(s) || objMap.containsKey(s);
	}

	public Map.Entry<String, Float> getFloatNodeEntry(String entry) {
		for (Map.Entry<String, Float> stringFloatEntry : skillSettings.entrySet()) {
			if (stringFloatEntry.getKey().equalsIgnoreCase(entry)) {
				return stringFloatEntry;
			}
		}
		return null;
	}

	public boolean hasNode(ISkillNode node) {
		return hasNode(node.value());
	}

	public void addNode(String s, float val) {
		skillSettings.put(s.toLowerCase(), val);
	}

	public void addObjectNode(String k, String v) {
		objMap.put(k.toLowerCase(), v);
	}

	public String getObjectNode(ISkillNode k) {
		return getObjectNode(k.value());
	}

	public String getObjectNode(String k) {
		return objMap.get(k);
	}


	public float getNodeValue(ISkillNode n) {
		return getNodeValue(n.toString());
	}

	public float getNodeValue(String s) {
		Float aFloat = skillSettings.get(s.toLowerCase());
		if (aFloat == null) {
			error("Missing skill node " + s);
			return 0;
		}
		return aFloat;
	}

	//Use skillContext.getLevelNode
    @Deprecated
	public float getLevelNodeValue(ISkillNode n, int level) {
		return getLevelNodeValue(n.toString(), level);
	}

    @Deprecated
	public float getLevelNodeValue(String s, int level) {
		return getNodeValue(s) + level * getNodeValue(s + bonus);
	}

    @Deprecated
	public Map<String, Float> getNodes() {
		return Collections.unmodifiableMap(skillSettings);
	}

	public static Set<String> getComplexKeySuffixes() {
		Set<String> collect = Sponge.getRegistry().getAllOf(Attribute.class)
				.stream()
				.map(attribute -> "_per_" + attribute.getId())
				.collect(Collectors.toSet());
		collect.add(bonus);
		return collect;
	}

}

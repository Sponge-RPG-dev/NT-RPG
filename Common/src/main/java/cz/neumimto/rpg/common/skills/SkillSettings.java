

package cz.neumimto.rpg.common.skills;

import cz.neumimto.rpg.common.configuration.AttributeConfig;
import cz.neumimto.rpg.common.skills.scripting.JsBinding;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeumimTo on 14.2.2015.
 */
@JsBinding(JsBinding.Type.CLASS)
public class SkillSettings {

    private Map<String, String> skillSettings = new HashMap<>();

    private Map<String, String> objMap = new HashMap<>();

    private Map<AttributeConfig, AttributeSettings> attributeSettingsMap = new HashMap<>();

    /** use addExpression **/
    @Deprecated
    public void addNode(ISkillNode n, float val) {
        addExpression(n.toString(), val);
    }

    /** use addExpression **/
    @Deprecated
    public void addNode(String n, float val) {
        addExpression(n, val);
    }

    /** use addExpression **/
    @Deprecated
    public void addNode(String n, String val) {
        addExpression(n, val);
    }

    public void addExpression(String node, String expr) {
        skillSettings.put(node.toLowerCase(), expr);
    }

    public void addExpression(ISkillNode node, String expr) {
        addExpression(node.toString(), expr);
    }

    public void addExpression(String node, double expr) {
        addExpression(node, String.valueOf(expr));
    }

    public boolean hasNode(String s) {
        return skillSettings.containsKey(s) || objMap.containsKey(s);
    }

    public boolean hasNode(ISkillNode node) {
        return hasNode(node.value());
    }

    public void addObjectNode(String k, String v) {
        objMap.put(k.toLowerCase(), v);
    }

    public String getObjectNode(String k) {
        return objMap.get(k);
    }

    @Deprecated
    public Map<String, String> getNodes() {
        return Collections.unmodifiableMap(skillSettings);
    }

    public Map<AttributeConfig, AttributeSettings> getAttributeSettings() {
        return Collections.unmodifiableMap(attributeSettingsMap);
    }

    public static class AttributeSettings {
        public final String node;
        public final float value;

        public AttributeSettings(String node, float value) {
            this.node = node;
            this.value = value;
        }
    }
}

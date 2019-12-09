package cz.neumimto.rpg.common.skills;

import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.utils.DebugLevel;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;

import static cz.neumimto.rpg.api.logging.Log.info;

public class SkillConfigLoader {

    private final String id;
    private final String name;
    private Class<? extends ISkill> type;

    public SkillConfigLoader(String id, Class<? extends ISkill> type) {
        this.id = id;
        this.name = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Class<? extends ISkill> getType() {
        return type;
    }

    public ISkill build(String id) {
        info("Generating class for the skill " + id, DebugLevel.DEVELOP);
        //todo use another classloadern
        ByteBuddy byteBuddy = new ByteBuddy();
        String[] split = id.split(":");
        String name = split[split.length - 1];
        ResourceLoader rl = Rpg.get().getResourceLoader();
        Class<? extends ISkill> value = byteBuddy.subclass(type)
                .name("cz.neumimto.generated." + name + System.currentTimeMillis())
                .annotateType(AnnotationDescription.Builder.ofType(ResourceLoader.Skill.class)
                        .define("value", id)
                        .build())
                .make()
                .load(rl.getClass().getClassLoader())
                .getLoaded();
        Object o = null;
        try {
            o = rl.loadClass(value);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        ISkill o1 = (ISkill) o;
        o1.setLocalizableName(name);
        return o1;
    }

}

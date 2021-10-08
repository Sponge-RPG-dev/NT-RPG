package cz.neumimto.rpg.common.skills;

import cz.neumimto.rpg.common.ResourceLoader;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.utils.DebugLevel;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;

import static cz.neumimto.rpg.common.logging.Log.info;

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
        ResourceLoader rl = Rpg.get().getResourceLoader();

        Object o = null;
        try {
            o = rl.loadSkillClass(generateClass(id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ISkill o1 = (ISkill) o;
        return o1;
    }

    public Class<? extends ISkill> generateClass(String id) {
        info("Generating class for the skill " + id, DebugLevel.DEVELOP);
        //todo use another classloadern
        ByteBuddy byteBuddy = new ByteBuddy();
        String[] split = id.split(":");
        String name = split[split.length - 1];
        ResourceLoader rl = Rpg.get().getResourceLoader();
        return byteBuddy.subclass(type)
                .name("cz.neumimto.generated." + name + System.currentTimeMillis())
                .annotateType(AnnotationDescription.Builder.ofType(ResourceLoader.Skill.class)
                        .define("value", id)
                        .build())
                .make()
                .load(rl.getClass().getClassLoader())
                .getLoaded();
    }

}

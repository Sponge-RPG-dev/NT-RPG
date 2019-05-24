package cz.neumimto.rpg.api.skills;

import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.configuration.DebugLevel;
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
        ResourceLoader rl = NtRpgPlugin.GlobalScope.injector.getInstance(ResourceLoader.class);
        ByteBuddy byteBuddy = new ByteBuddy();
        String[] split = id.split(":");
        String name = split[split.length - 1];

        Class<? extends ISkill> value = byteBuddy.subclass(type)
                .name("cz.neumimto.generated." + name + System.currentTimeMillis())
                .annotateType(AnnotationDescription.Builder.ofType(ResourceLoader.Skill.class)
                        .define("value", id)
                        .build())
                .make()
                .load(rl.getConfigClassLoader())
                .getLoaded();
        Object o = null;
        try {
            o = rl.loadClass(value, getClass().getClassLoader());
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        ISkill o1 = (ISkill) o;
        o1.setLocalizableName(name);
        return o1;
    }

}

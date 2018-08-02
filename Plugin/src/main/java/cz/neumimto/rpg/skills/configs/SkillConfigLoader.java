package cz.neumimto.rpg.skills.configs;

import cz.neumimto.core.ioc.IoC;
import cz.neumimto.rpg.ResourceLoader;
import cz.neumimto.rpg.skills.ISkill;
import javassist.CannotCompileException;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import org.spongepowered.api.CatalogType;

public class SkillConfigLoader implements CatalogType {

    private final String id;
    private final String name;
    private Class<? extends ISkill> type;

    public SkillConfigLoader(String id, Class<? extends ISkill> type) {
        this.id = id;
        this.name = id;
        this.type = type;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public ISkill build(String id) {
        ResourceLoader rl = IoC.get().build(ResourceLoader.class);
        ByteBuddy byteBuddy = new ByteBuddy();
        String[] split = id.split(":");
        String name = split[split.length -1];

        Class<? extends ISkill> value = byteBuddy.subclass(type)
                .name(name)
                .annotateType(AnnotationDescription.Builder.ofType(ResourceLoader.Skill.class)
                        .define("value", id)
                        .build())
                .make()
                .load(rl.getConfigClassLaoder())
                .getLoaded();
        Object o = null;
        try {
            o = rl.loadClass(value);
        } catch (IllegalAccessException | CannotCompileException | InstantiationException e) {
            e.printStackTrace();
        }
        return (ISkill) o;
    }
}

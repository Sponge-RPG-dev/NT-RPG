package cz.neumimto.rpg.common;

import com.google.inject.AbstractModule;
import cz.neumimto.rpg.api.classes.ClassService;
import cz.neumimto.rpg.api.configuration.SkillTreeDao;
import cz.neumimto.rpg.api.entity.PropertyService;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.localization.LocalizationService;
import cz.neumimto.rpg.api.scripting.IScriptEngine;
import cz.neumimto.rpg.common.classes.ClassServiceImpl;
import cz.neumimto.rpg.common.configuration.SkillTreeLoaderImpl;
import cz.neumimto.rpg.common.entity.PropertyServiceImpl;
import cz.neumimto.rpg.common.exp.ExperienceDAO;
import cz.neumimto.rpg.common.localization.LocalizationServiceImpl;
import cz.neumimto.rpg.common.persistance.dao.ClassDefinitionDao;
import cz.neumimto.rpg.common.scripting.JSLoader;

import java.util.HashMap;
import java.util.Map;

public class AbstractRpgGuiceModule extends AbstractModule {

    protected <T> Map<Class<T>, Class<? extends T>> getBindings() {
        Map map = new HashMap<>();
        map.put(SkillTreeDao.class, SkillTreeLoaderImpl.class);
        map.put(PropertyService.class, PropertyServiceImpl.class);
        map.put(IScriptEngine.class, JSLoader.class);
        map.put(ClassService.class, ClassServiceImpl.class);
        map.put(ClassDefinitionDao.class, null);
        map.put(ExperienceDAO.class, null);
        map.put(Gui.class, null);
        map.put(LocalizationService.class, LocalizationServiceImpl.class);
        return map;
    }

    @Override
    protected void configure() {
        for (Map.Entry<Class<Object>, Class<?>> classClassEntry : getBindings().entrySet()) {
            Class<?> value = classClassEntry.getValue();
            Class<Object> key = classClassEntry.getKey();
            if (value == null) {
                bind(key);
            } else {
                bind(key).to(value);
            }
        }
    }
}

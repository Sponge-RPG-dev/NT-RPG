package cz.neumimto.rpg.common.scripting;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.hocon.HoconFormat;
import com.electronwill.nightconfig.json.FancyJsonWriter;
import com.electronwill.nightconfig.json.JsonFormat;
import com.google.inject.Injector;
import cz.neumimto.rpg.api.ResourceLoader;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.scripting.IRpgScriptEngine;
import cz.neumimto.rpg.api.skills.SkillService;
import cz.neumimto.rpg.api.skills.SkillsDefinition;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.api.utils.DebugLevel;
import cz.neumimto.rpg.api.utils.FileUtils;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.bytecode.ClassGenerator;
import cz.neumimto.rpg.common.skills.scripting.SkillComponent;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.BiConsumer;

import static cz.neumimto.rpg.api.logging.Log.info;

public abstract class AbstractRpgScriptEngine implements IRpgScriptEngine {

    @Inject
    protected Injector injector;

    @Inject
    protected ClassGenerator classGenerator;

    @Inject
    protected ResourceLoader resourceLoader;

    @Inject
    protected SkillService skillService;

    @Inject
    protected AssetService assetService;

    protected static Object listener;

    private Map<Class<?>, JsBinding.Type> dataToBind = new HashMap<>();

    protected Path mergeScriptFiles() {
        Path scripts_root = Paths.get(Rpg.get().getWorkingDirectory() + "/scripts");
        FileUtils.createDirectoryIfNotExists(scripts_root);
        Path path = Paths.get(scripts_root + File.separator + ".deployed.js");
        if (path.toFile().exists()) {
            path.toFile().delete();
        }

        final StringBuilder bigChunkOfCode = new StringBuilder();
        bigChunkOfCode.append(assetService.getAssetAsString("Main.js")).append(System.lineSeparator());

        bigChunkOfCode.append("//classpath:assets.nt-rpg/defaults/skills.js")
                .append(System.lineSeparator())
                .append(assetService.getAssetAsString("defaults/skills.js"))
                .append(System.lineSeparator());


        try {
            Queue<Path> directories = new PriorityQueue<>();
            directories.add(scripts_root);
            while (directories.size() != 0) {
                Files.list(directories.poll()).forEach((file -> {
                    if (file.toFile().isDirectory()) directories.add(file);
                    else if (file.toFile().getName().endsWith(".js")) {
                        bigChunkOfCode.append(System.lineSeparator()).append("// ").append(file.toFile().getName()).append(System.lineSeparator());
                        try {
                            for (String line : Files.readAllLines(file))
                                bigChunkOfCode.append(line).append(System.lineSeparator());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }));
            }

            return Files.write(path, bigChunkOfCode.toString().getBytes(), StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            throw new RuntimeException("Could not write .deployed.js ", e);
        }
    }

    protected void prepareBindings(BiConsumer<String, Object> consumer) {
        consumer.accept("Injector", injector);
        consumer.accept("Rpg", Rpg.get());
        for (Map.Entry<Class<?>, JsBinding.Type> objectTypeEntry : getDataToBind().entrySet()) {
            try {
                if (objectTypeEntry.getValue() == JsBinding.Type.CONTAINER) {
                    for (Field field : objectTypeEntry.getKey().getDeclaredFields()) {
                        field.setAccessible(true);
                        if (field.isAnnotationPresent(SkillComponent.class)) {
                            Object o = field.get(null);
                            String name = field.getName();
                            consumer.accept(name.toLowerCase(), o);
                            dumpDocumentedFunction(name.toLowerCase(), o);
                        }
                    }
                    continue;
                }
                if (objectTypeEntry.getValue() == JsBinding.Type.CLASS) {
                    consumer.accept(objectTypeEntry.getKey().getSimpleName(), objectTypeEntry.getKey());
                    dumpDocumentedJavaType(objectTypeEntry.getKey().getSimpleName(), objectTypeEntry.getKey());
                    continue;
                }
                if (objectTypeEntry.getValue() == JsBinding.Type.OBJECT) {
                    if (objectTypeEntry.getKey().isAnnotationPresent(SkillComponent.class)) {
                        consumer.accept(objectTypeEntry.getKey().getSimpleName().toLowerCase(), objectTypeEntry.getKey().newInstance());
                    } else {
                        consumer.accept(objectTypeEntry.getKey().getSimpleName(), objectTypeEntry.getKey().newInstance());
                    }
                }
            } catch (Exception e) {
                Log.error("Could not create bindings ", e);
            }
        }
    }

    private void dumpDocumentedFunction(String toLowerCase, Object o) {

    }

    private void dumpDocumentedJavaType(String simpleName, Class<?> key) {

    }

    @Override
    public void loadInternalSkills() {
        String assetAsString = assetService.getAssetAsString("defaults/skills.conf");

        HoconFormat instance = HoconFormat.instance();
        ConfigParser<CommentedConfig> parser = instance.createParser();
        CommentedConfig config = parser.parse(assetAsString);

        //URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{}, this.getClass().getClassLoader()) {
        //    @Override
        //    protected void finalize() throws Throwable {
        //        super.finalize();
        //        info("Removing URLClassloader used for defaults/skills.conf", DebugLevel.DEVELOP);
        //    }
        //};

        loadSkillDefinitionFile(config, this.getClass().getClassLoader());
    }

    @Override
    public void loadSkillDefinitionFile(ClassLoader urlClassLoader, File confFile) {
        info("Loading skills from file " + confFile.getName());
        try (FileConfig fc = FileConfig.of(confFile.getPath())) {
            fc.load();
            loadSkillDefinitionFile(fc, urlClassLoader);
        } catch (Exception e) {
            Log.error("Could not load file " + confFile, e);
        }
    }

    private void loadSkillDefinitionFile(Config config, ClassLoader urlClassLoader) {
        SkillsDefinition definition = new ObjectConverter().toObject(config, SkillsDefinition::new);
        definition.getSkills().stream()
                .map(a -> skillService.skillDefinitionToSkill(a, urlClassLoader))
                .forEach(a -> skillService.registerAdditionalCatalog(a));
    }

    protected void dumpDocumentedFunctions(List<SkillComponent> skillComponents) {
        File file = new File(Rpg.get().getWorkingDirectory(), "functions.md");
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            for (SkillComponent skillComponent : skillComponents) {
                String s = assetService.getAssetAsString("templates/function.md");
                s = s.replaceAll("\\{\\{function\\.name}}", skillComponent.value());
                s = s.replaceAll("\\{\\{function\\.usage}}", skillComponent.usage());

                StringBuilder buffer = new StringBuilder();
                for (SkillComponent.Param param : skillComponent.params()) {
                    buffer.append("    * ").append(param.value()).append("\n");
                }
                s = s.replaceAll("\\{\\{function\\.params}}", buffer.toString());
                Files.write(file.toPath(), s.getBytes(), StandardOpenOption.APPEND);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void reloadSkills() {
        Path addonDir = Paths.get(Rpg.get().getWorkingDirectory() + File.separator + "addons");

        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{}, this.getClass().getClassLoader()) {
            @Override
            protected void finalize() throws Throwable {
                super.finalize();
                info("Removing URLClassloader used for " + addonDir, DebugLevel.DEVELOP);
            }
        };

        File file1 = addonDir.toFile();
        File[] files = file1.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".conf"));
        if (files != null) {
            for (File confFile : files) {
                info("Loading file " + confFile);
                loadSkillDefinitionFile(urlClassLoader, confFile);
            }
        }

        loadInternalSkills();
    }

    @Override
    public Map<Class<?>, JsBinding.Type> getDataToBind() {
        return dataToBind;
    }


    protected static class ScriptExecutionException extends RuntimeException {

        public ScriptExecutionException(String message, Throwable cause) {
            super(message, cause);
        }

    }
}

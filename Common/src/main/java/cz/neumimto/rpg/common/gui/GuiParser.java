package cz.neumimto.rpg.common.gui;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.RpgApi;
import cz.neumimto.rpg.api.configuration.ClassTypeDefinition;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.items.ClassItem;
import cz.neumimto.rpg.api.logging.Log;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class GuiParser<T, I> {

    public Map<String, ConfigInventory<T, I>> initInventories(ClassLoader classLoader, String confName) {
        Map<String, ConfigInventory<T, I>> CACHED_MENUS = new HashMap<>();
        Config config = ConfigFactory.load(classLoader, confName);
        RpgApi api = Rpg.get();

        NashornScriptEngineFactory sFactorz = new NashornScriptEngineFactory();

        for (Config gui : config.getConfigList("gui")) {
            String guiName = gui.getString("type");


            Collection<ClassDefinition> classDefs = api.getClassService().getClasses().values();
            switch (guiName) {
                case "class_template":
                    for (ClassDefinition context : classDefs) {
                        ConfigInventory c = createCachedMenu(sFactorz, guiName, gui, context);
                        CACHED_MENUS.put(guiName + context.getName(), c);
                    }
                    break;
                case "class_types":
                    Map<String, ClassTypeDefinition> cTypes = api.getPluginConfig().CLASS_TYPES;
                    Object[] context = new Object[]{
                            cTypes,
                            (Supplier<T[]>) () -> cTypes.entrySet().stream()
                                    .map(this::classTypeButton)
                                    .collect(Collectors.toList())
                                    .toArray(initArray(cTypes.size()))
                    };
                    ConfigInventory c = createCachedMenu(
                            sFactorz, guiName, gui, context
                    );

                    CACHED_MENUS.put(guiName, c);
                    break;
                case "classes_by_type":

                    Set<String> types = api.getClassService().getClassDefinitions().stream()
                            .map(ClassDefinition::getClassType)
                            .collect(Collectors.toSet());

                    for (String type : types) {
                        Object[] context2 = new Object[]{
                                type,
                                (Supplier<T[]>) () -> api.getClassService().getClassDefinitions()
                                        .stream().filter(a -> a.getClassType().equals(type)).map(a -> toItemStack(a))
                                        .collect(Collectors.toList())
                                        .toArray(initArray(types.size()))
                        };
                        ConfigInventory c2 = createCachedMenu(
                                sFactorz, guiName, gui, context2
                        );

                        CACHED_MENUS.put(guiName + type, c2);
                    }
                    break;
                case "class_allowed_items":
                    for (ClassDefinition classDef : classDefs) {
                        Set<ClassItem> allowedArmor = classDef.getAllowedArmor();

                        Supplier<T[]> supplier = () -> allowedArmor
                                .stream().map(this::toItemStack)
                                .collect(Collectors.toList())
                                .toArray(initArray(allowedArmor.size()));

                        Object[] context3 = new Object[] {
                                classDef,
                                supplier
                        };
                        ConfigInventory c3 = createCachedMenu(
                                sFactorz, guiName, gui, context3
                        );
                        CACHED_MENUS.put(guiName + "_armor_" + classDef.getName(), c3);


                        Set<ClassItem> allowedWeapon = classDef.getWeapons();
                        supplier = () -> allowedWeapon
                                .stream().map(this::toItemStack)
                                .collect(Collectors.toList())
                                .toArray(initArray(allowedWeapon.size()));
                        context3 = new Object[] {
                                classDef,
                                supplier
                        };
                        c3 = createCachedMenu(
                                sFactorz, guiName, gui, context3
                        );
                        CACHED_MENUS.put(guiName + "_weapons_" + classDef.getName(), c3);


                    }
            }

        }
        return CACHED_MENUS;
    }

    private ConfigInventory createCachedMenu(NashornScriptEngineFactory factory,
                                             String guiName,
                                             Config gui,
                                             Object context) {
        List<String> inv = gui.getStringList("inv");
        List<String> items = gui.getStringList("items");
        String conditions = gui.getString("conditions");
        String dynamicSpace = gui.getString("dynamic_space");
        String command = gui.getString("command");
        String commandFn = "function command(context_, command_) { %cmd%; }";
        String condFn = "function validate(context_, slot_) { %cond% ; return true}";

        ScriptEngine scriptEngine = factory.getScriptEngine();
        try {
            scriptEngine.eval(commandFn.replaceAll("%cmd%", command));
            scriptEngine.eval(condFn.replaceAll("%cond%", conditions));
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        Invocable i = (Invocable) scriptEngine;
        return createCachedMenu(i, guiName, inv, dynamicSpace, items, context);
    }

    private ConfigInventory createCachedMenu(Invocable i,
                                             String guiName,
                                             List<String> inv,
                                             String dynamicSpace,
                                             List<String> items,
                                             Object context
    ) {


        Map<Character, T> itemMap = new HashMap<>();
        List<T> invContent = new ArrayList<>();
        T blank = parseItemsAndReturnBlank(i, context, itemMap, items);

        try {
            prepareInventory(inv, i, invContent, blank, context, guiName, itemMap);
        } catch (ScriptException | NoSuchMethodException e) {
            Log.error("Could not parse inventory from gui.conf " + guiName);
            e.printStackTrace();
        }
        T[] staticContent = (T[]) invContent.toArray();
        if (!(!dynamicSpace.isEmpty() && Object[].class.isAssignableFrom(context.getClass()))) {
            return new StaticInventory(staticContent, getInventorySlotProcessor());
        } else {
            Object[] c = (Object[]) context;
            return new DynamicInventory(staticContent, blank, ((Supplier<T[]>) c[1]).get(), getInventorySlotProcessor());
        }

    }

    private void prepareInventory(List<String> inv,
                                  Invocable i,
                                  List<T> invContent,
                                  T blank,
                                  Object context,
                                  String guiName,
                                  Map<Character, T> itemMap)
            throws ScriptException, NoSuchMethodException {

        for (String s : inv) {
            char[] chars = s.toCharArray();
            for (char c : chars) {
                boolean display = (boolean) i.invokeFunction("validate", context, c);
                if (display) {
                    T T = itemMap.get(c);
                    if (T == null) {
                        throw new IllegalStateException("Gui " + guiName + " missing item " + c);
                    }
                    invContent.add(T);
                } else {
                    invContent.add(blank);
                }
            }
        }

    }

    private T parseItemsAndReturnBlank(Invocable i, Object context, Map<Character, T> itemMap, List<String> items) {
        T blank = null;
        for (String item : items) {
            String[] split = item.split(",");
            String command = split[4];

            T T = itemStringToItemStack(split, () -> {
                try {
                    return (String) i.invokeFunction("command", context, command);
                } catch (ScriptException | NoSuchMethodException e) {
                    return "";
                }
            });
            if (command.equals("---")) {
                blank = T;
            }
            itemMap.put(split[0].charAt(0), T);
        }
        return blank;
    }

    private T[] initArray(int size) {
        return (T[]) new Object[size -1];
    }

    protected abstract InventorySlotProcessor getInventorySlotProcessor();

    protected abstract T classTypeButton(Map.Entry<String, ClassTypeDefinition> entry);

    protected abstract T toItemStack(ClassDefinition a);

    protected abstract T itemStringToItemStack(String[] split, Supplier<String> command);

    protected abstract T toItemStack(ClassItem a);
}
package cz.neumimto.effects.runewords;

import cz.neumimto.GroupService;
import cz.neumimto.NtRpgPlugin;
import cz.neumimto.Pair;
import cz.neumimto.configuration.PluginConfig;
import cz.neumimto.effects.EffectService;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.PostProcess;
import cz.neumimto.core.ioc.Singleton;
import org.slf4j.Logger;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cz.neumimto.utils.Utils.not;

/**
 * Created by NeumimTo on 29.10.2015.
 */
@Singleton
public class RWService {

    @Inject
    private RWDao dao;

    @Inject
    private EffectService effectService;

    @Inject
    private Logger logger;

    @Inject
    private GroupService groupService;

    private Map<String,RuneWord> runewords = new HashMap();
    private Map<String,Rune> runes = new HashMap<>();
    private final Pattern socket = Pattern.compile("\\{@\\}");
    private final Path file = Paths.get(NtRpgPlugin.workingDir,"Runes.conf");



    @PostProcess(priority = 8000)
    public void load() {
        File p = file.toFile();
        if (!p.exists()) {
            try {
                p.createNewFile();
                Files.write(p.toPath(),"Runes:{},\nRuneWords:{}".getBytes());
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (Rune rune : dao.getAllRunes(p)) {
            runes.put(rune.getName().toLowerCase(),rune);
        }
        for (RuneWordTemplate runeWord : dao.getAllRws(p)) {
            runewords.put(runeWord.getName().toLowerCase(),getRuneword(runeWord));
        }

    }

    protected RuneWord getRuneword(RuneWordTemplate template) {
        template.getRunes().stream().filter(not(runes::containsKey)).forEach(e -> logger.warn("Runeword "+template +" is not possible to create, due to missing Rune:"+e));
        template.getEffects().keySet().stream().filter(not(effectService::isGlobalEffect)).forEach(e->logger.warn("Runeword "+template +" defined non existing global effect:"+e));
        RuneWord rw = new RuneWord();
        rw.setName(template.getName());
        rw.setRunes(template.getRunes().stream()./*filter(this::existsRune).*/map(this::getRune).collect(Collectors.toList()));
        rw.setMinLevel(rw.getMinLevel());
        rw.setEffects(template.getEffects().entrySet().stream()
                .filter(l -> effectService.isGlobalEffect(l.getKey()))
                .map(a -> new Pair<>(effectService.getGlobalEffect(a.getKey()),a.getValue()))
                .collect(HashMap::new,(map, a)-> map.put(a.key,a.value),HashMap::putAll)); //wtf i just did?
        rw.setRestrictedClasses(template.getRestrictedClasses().stream()
                .filter(groupService::existsClass)
                .map(groupService::getNClass).collect(Collectors.toSet()));
        return rw;
    }

    public RuneWord getRuneword(List<Text> lore) {
        if (lore.size() <= 1) {
            return null;
        }
        Text t = lore.get(0);
        String s = Texts.toPlain(t);
        if (!s.startsWith(PluginConfig.RW_LORE_COLOR) || s.length() < 3)
            return null;
        return runewords.get(s.substring(2));
    }

    public List<Text> addRune(List<Text> lore, Rune rune) {
        if (!hasEmptySocket(lore))
            return lore;
        String s = Texts.toPlain(lore.get(1));
        s = s.replaceFirst(socket.pattern(),rune.getName()+" ");
        lore.set(1,Texts.of(s));
        return lore;
    }


    public int getSocketCount(List<Text> lore) {
        if (lore.size() < 2)
            return 0;
        String s = Texts.toPlain(lore.get(1));
        int c = 0;
        Matcher matcher = socket.matcher(s);
        while (matcher.find())
            c++;
        return c;
    }

    public boolean hasEmptySocket(List<Text> lore) {
        if (lore.size() < 2)
            return false;
        String s = Texts.toPlain(lore.get(1));
        Matcher matcher = socket.matcher(s);
        return matcher.find();
    }

    public boolean existsRune(String rune) {
        return runes.containsKey(rune.toLowerCase());
    }

    public boolean existsRuneword(String rw) {
        return runewords.containsKey(rw.toLowerCase());
    }

    public Rune getRune(String rune) {
        return runes.get(rune.toLowerCase());
    }

}

package cz.neumimto.effects.runewords;

import cz.neumimto.configuration.PluginConfig;
import cz.neumimto.effects.EffectService;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.PostProcess;
import cz.neumimto.core.ioc.Singleton;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by NeumimTo on 29.10.2015.
 */
@Singleton
public class RWService {

    @Inject
    private RWDao dao;

    @Inject
    private EffectService effectService;

    private Map<String,RuneWord> runewords = new HashMap();
    private Map<String,Rune> runes = new HashMap<>();
    Pattern socket = Pattern.compile("\\{@\\}");

    @PostProcess(priority = 8000)
    public void load() {
        for (Rune rune : dao.getAllRunes()) {
            runes.put(rune.getName(),rune);
        }
        for (RuneWord runeWord : dao.getAllRws()) {
            runewords.put(runeWord.getName(),runeWord);
        }
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
        s = s.replaceFirst("\\{@\\}",rune.getName()+" ");
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

}

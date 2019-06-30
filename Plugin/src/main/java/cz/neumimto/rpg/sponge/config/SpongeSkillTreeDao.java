package cz.neumimto.rpg.sponge.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.api.utils.Pair;
import cz.neumimto.rpg.common.configuration.SkillTreeLoaderImpl;
import cz.neumimto.rpg.sponge.gui.SkillTreeInterfaceModel;
import cz.neumimto.rpg.sponge.skills.SpongeSkillService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static cz.neumimto.rpg.api.logging.Log.error;

@Singleton
public class SpongeSkillTreeDao extends SkillTreeLoaderImpl {

    @Inject
    private SpongeSkillService spongeSkillService;

    protected void loadAsciiMaps(Config config, SkillTree skillTree) {
        try {
            List<String> asciiMap = config.getStringList("AsciiMap");
            Optional<String> max = asciiMap.stream().max(Comparator.comparingInt(String::length));
            if (max.isPresent()) {
                int length = max.get().length();
                int rows = asciiMap.size();

                short[][] array = new short[rows][length];

                int i = 0;
                int j = 0;
                StringBuilder num = new StringBuilder();
                for (String s : asciiMap) {
                    for (char c1 : s.toCharArray()) {
                        if (Character.isDigit(c1)) {
                            num.append(c1);
                            continue;
                        } else if (c1 == 'X') {
                            skillTree.setCenter(new Pair<>(i, j));
                            j++;
                            continue;
                        }
                        if (!num.toString().equals("")) {
                            array[i][j] = Short.parseShort(num.toString());
                            j++;
                        }
                        SkillTreeInterfaceModel guiModelByCharacter = spongeSkillService.getGuiModelByCharacter(c1);
                        if (guiModelByCharacter != null) {
                            array[i][j] = guiModelByCharacter.getId();
                        }
                        num = new StringBuilder();
                        j++;
                    }
                    j = 0;
                    i++;
                }
                skillTree.setSkillTreeMap(array);
            }
        } catch (ConfigException | ArrayIndexOutOfBoundsException ignored) {
            error("Could not read ascii map in the skilltree " + skillTree.getId(), ignored);
            skillTree.setSkillTreeMap(new short[][]{});
        }
    }


}

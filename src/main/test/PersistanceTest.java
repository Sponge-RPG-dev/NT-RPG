import cz.neumimto.ResourceLoader;
import cz.neumimto.ioc.IoC;
import cz.neumimto.persistance.GroupDao;
import cz.neumimto.persistance.SkillTreeDao;
import cz.neumimto.skills.SkillService;
import cz.neumimto.skills.SkillTree;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.spongepowered.api.Game;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.util.HashMap;

public class PersistanceTest {

    @Test
    public void testConfig() {
        ResourceLoader.raceDir = new File("./src/main/test/testfiles/races");
        ResourceLoader.guildsDir = new File("./src/main/test/testfiles/guilds");
        GroupDao dao = new GroupDao();
        dao.loadGuilds();
        dao.loadRaces();
        Assert.assertTrue(ResourceLoader.raceDir.listFiles().length == dao.getRaces().size());
        Assert.assertTrue(ResourceLoader.guildsDir.listFiles().length == dao.getGuilds().size());
    }



}

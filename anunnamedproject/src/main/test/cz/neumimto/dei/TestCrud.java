package cz.neumimto.dei;

import cz.neumimto.core.ioc.IoC;
import cz.neumimto.dei.entity.dao.CitizenDAO;
import cz.neumimto.dei.entity.dao.TownDAO;
import cz.neumimto.dei.entity.dao.WorldDao;
import cz.neumimto.dei.entity.database.area.ClaimedArea;
import cz.neumimto.dei.entity.database.area.TownClaim;
import cz.neumimto.dei.entity.database.player.Citizen;
import cz.neumimto.dei.entity.database.worldobject.Town;
import cz.neumimto.dei.serivce.WorldService;
import org.junit.*;

import java.util.*;

/**
 * Created by ja on 6.7.2016.
 */
public class TestCrud extends cz.neumimto.dei.Test {

    private UUID uuid = UUID.fromString("2e9f3756-8698-43b9-9df0-4283c16a8f1f");

    @BeforeClass
    public static void before() {
        IoC.get().build(DEI.class);
    }

    @org.junit.Test
    public void testCitizenDao() {
        System.out.println("testCitizenDao");
        CitizenDAO build = IoC.get().build(CitizenDAO.class);
        Citizen citizen = build.loadOrCreate(uuid);
        assert citizen != null;
    }

    @org.junit.Test
    public void testTownCreate() {
        System.out.println("testTownCreate");
        TownDAO townDAO = IoC.get().build(TownDAO.class);
        CitizenDAO build = IoC.get().build(CitizenDAO.class);
        Town town = new Town();
        TownClaim a = new TownClaim();
        a.setX(54621);
        a.setZ(45);
        a.setWorld("test");
        town.setHomeChunk(a);
        town.getClaimedAreas().add(a);
        town.setName("Test");
        town.setDestroyed(false);
        Citizen citizen = build.loadOrCreate(uuid);
        town.setCitizens(Arrays.asList(citizen));
        town.setLeader(citizen);
        townDAO.save(town);
    }

    @org.junit.Test
    public void testWorldLoading() {
        System.out.println("testWorldLoading");
        WorldService ws = IoC.get().build(WorldService.class);
        ws.loadClaimedChunks("test");
    }

    @org.junit.Test
    public void testTownClaim() {
        System.out.println("testWorldLoading");
        TownDAO townDAO = IoC.get().build(TownDAO.class);
        WorldService ws = IoC.get().build(WorldService.class);
        Town town = townDAO.getTown("Test");
        TownClaim tc = new TownClaim();
        tc.setX(new Random().nextInt(10000));
        tc.setZ(new Random().nextInt(10000));
        tc.setWorld("test");
        ws.claimChunk(town,tc);
        ws.update(town);
    }


}

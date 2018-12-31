package cz.neumimto.rpg;

import cz.neumimto.core.FindDbSchemaMigrationsEvent;
import cz.neumimto.core.FindPersistenceContextEvent;
import cz.neumimto.core.ioc.IoC;
import cz.neumimto.core.migrations.DbMigrationService;
import cz.neumimto.rpg.persistance.model.BaseCharacterAttribute;
import cz.neumimto.rpg.persistance.model.CharacterClass;
import cz.neumimto.rpg.persistance.model.CharacterSkill;
import cz.neumimto.rpg.players.CharacterBase;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.event.Listener;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by NeumimTo on 1.12.2018.
 */
public class PersistenceHandler {

    @Listener
    public void onFindDbSchemaMigrationsEvent(FindDbSchemaMigrationsEvent event) throws IOException {
        if (event.validForContext("nt-rpg")) {
            DbMigrationService dms = IoC.get().build(DbMigrationService.class);
            List<String> migrations = Arrays.asList(
                    "sql/%s/040918-init-db.sql"
            );

            for (String migration : migrations) {
                migration = migration.replaceAll("%s", dms.getDatabaseProductName().toLowerCase());
                Optional<Asset> sql = Sponge.getAssetManager().getAsset(this, migration);
                if (sql.isPresent()) {
                    dms.addMigration(sql.get().readString(Charset.forName("UTF-8")));
                } else {
                    System.err.println("You are using a database which is not officialy supported, nor tested. " +
                            "While the plugin will most likely keep working all DDL changes have to be done manually, If you want to have a simpler life  please consider switching to either mysql or postgres. " +
                            "Or in the best case submit a pr containing Database schema migrations.");
                    break;
                }
            }
        }
    }

    @Listener
    public void registerEntities(FindPersistenceContextEvent event) {
        if (event.validForContext("nt-rpg")) {
            event.getClasses().add(CharacterBase.class);
            event.getClasses().add(BaseCharacterAttribute.class);
            event.getClasses().add(CharacterSkill.class);
            event.getClasses().add(CharacterClass.class);
        }
    }
}

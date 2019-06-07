package cz.neumimto.rpg.common.persistance;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import cz.neumimto.core.FindDbSchemaMigrationsEvent;
import cz.neumimto.core.FindPersistenceContextEvent;
import cz.neumimto.core.migrations.DbMigrationService;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.persistance.model.BaseCharacterAttribute;
import cz.neumimto.rpg.api.persistance.model.CharacterClass;
import cz.neumimto.rpg.api.persistance.model.CharacterSkill;
import org.spongepowered.api.event.Listener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by NeumimTo on 1.12.2018.
 */
@Singleton
public class PersistenceHandler {

    @Inject
    private Injector injector;

    @Inject
    private AssetService assetService;

    @Listener
    public void onFindDbSchemaMigrationsEvent(FindDbSchemaMigrationsEvent event) throws IOException {
        if (event.validForContext("nt-rpg")) {
            DbMigrationService dms =  injector.getInstance(DbMigrationService.class);
            List<String> migrations = Arrays.asList(
                    "sql/%s/040918-init-db.sql",
                    "sql/%s/060119-update-2.0.0.sql"
            );

            for (String migration : migrations) {
                migration = migration.replaceAll("%s", dms.getDatabaseProductName().toLowerCase());

                try {
                    String sql = assetService.getAssetAsString(migration);
                    dms.addMigration(sql);
                } catch (Exception e) {
                    System.out.println("You are using a database which is not officialy supported, nor tested. " +
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

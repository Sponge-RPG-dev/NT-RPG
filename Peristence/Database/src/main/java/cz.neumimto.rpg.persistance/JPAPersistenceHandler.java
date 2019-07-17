package cz.neumimto.rpg.persistance;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import cz.neumimto.rpg.api.persistance.model.BaseCharacterAttribute;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.persistance.model.CharacterClass;
import cz.neumimto.rpg.api.persistance.model.CharacterSkill;
import cz.neumimto.rpg.common.assets.AssetService;
import cz.neumimto.rpg.common.persistance.dao.IPersistenceHandler;
import cz.neumimto.rpg.persistance.migrations.DbMigrationsService;
import cz.neumimto.rpg.persistance.model.JPABaseCharacterAttribute;
import cz.neumimto.rpg.persistance.model.JPACharacterBase;
import cz.neumimto.rpg.persistance.model.JPACharacterClass;
import cz.neumimto.rpg.persistance.model.JPACharacterSkill;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by NeumimTo on 1.12.2018.
 */
@Singleton
public class JPAPersistenceHandler implements IPersistenceHandler {

    @Inject
    private Injector injector;

    @Inject
    private AssetService assetService;

    public void findMigrations(DbMigrationsService dms) throws IOException {
            List<String> migrations = Arrays.asList(
                    "sql/%s/040918-init-db.sql",
                    "sql/%s/060119-update-2.0.0.sql",
                    "sql/%s/240219-fix-null-levels.sql",
                    "sql/%s/250619-attrpoints-spent.sql"
            );

            String s = dms.getDatabaseProductName().toLowerCase();
            if (s.equalsIgnoreCase("mariadb")) {
                s = "mysql";
            }
            for (String migration : migrations) {
                migration = migration.replaceAll("%s", s);

                try {
                    String sql = assetService.getAssetAsString(migration);
                    dms.addMigration(sql);
                } catch (Exception e) {
                    System.out.println("You are using a database which is not officialy supported - " + s + ", nor tested. " +
                            "While the plugin will most likely keep working all DDL changes have to be done manually, If you want to have a simpler life  please consider switching to either mysql or postgres. " +
                            "Or in the best case submit a pr containing Database schema migrations.");
                    break;
                }
            }

    }

    @Override
    public BaseCharacterAttribute createCharacterAttribute() {
        return new JPABaseCharacterAttribute();
    }

    @Override
    public CharacterClass createCharacterClass() {
        return new JPACharacterClass();
    }

    @Override
    public CharacterSkill createCharacterSkill() {
        return new JPACharacterSkill();
    }

    @Override
    public CharacterBase createCharacterBase() {
        return new JPACharacterBase();
    }
}

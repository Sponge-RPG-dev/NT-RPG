package cz.neumimto.rpg.persistence.jdbc.migrations;


import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by NeumimTo on 24.6.2018.
 */
public class DbMigrationsService {

    private List<DbMigration> migrations = new ArrayList<>();

    private Connection connection;

    private String databaseProductName;

    public void setConnection(Connection connection) throws SQLException {
        this.connection = connection;
        DatabaseMetaData metaData = connection.getMetaData();
        this.databaseProductName = metaData.getDatabaseProductName();
    }

    public String getDatabaseProductName() {
        return databaseProductName;
    }

    public void startMigration() throws SQLException, IOException {
        Statement statement = connection.createStatement();
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("sql/create-migration-table.sql");
        String s = CharStreams.toString(new InputStreamReader(resourceAsStream, Charset.forName("UTF-8")));
        statement.execute(s);
        Collections.sort(migrations);
        connection.setAutoCommit(false);
        try {
            for (DbMigration migration : migrations) {
                if (!hasRun(migration)) {
                    System.out.println("=================");
                    System.out.println(migration.getSql());
                    System.out.println("=================");
                    run(migration);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            connection.rollback();
        } finally {
            migrations = null;
        }
    }

    public void addMigration(DbMigration migration) {
        migrations.add(migration);
    }

    public void addMigration(String migration) {
        migrations.addAll(DbMigration.from(migration));
    }

    private boolean hasRun(DbMigration migration) {
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        try {
            InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("sql/check.sql");
            String s = CharStreams.toString(new InputStreamReader(resourceAsStream, Charset.forName("UTF-8")));
            preparedStatement = connection.prepareStatement(s.replaceAll("%s", migration.getId()));
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return false;
            }
            long l = resultSet.getLong("count");
            return l == 1;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void run(DbMigration migration) {
        PreparedStatement preparedStatement1 = null;
        PreparedStatement preparedStatement2 = null;
        try {
            for (String s : migration.getSql().split(";")) {
                preparedStatement1 = connection.prepareStatement(s);
                preparedStatement1.execute();
                connection.commit();
            }
            InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("sql/insert.sql");
            String s = CharStreams.toString(new InputStreamReader(resourceAsStream, Charset.forName("UTF-8")));

            preparedStatement2 = connection.prepareStatement(String.format(s, migration.getAuthor(), migration.getId(), migration.getNote()));
            preparedStatement2.execute();
            connection.commit();

        } catch (Exception e) {
            try {
                e.printStackTrace();
                connection.rollback();
                throw new RuntimeException(e);
            } catch (SQLException e1) {
                e.printStackTrace();
            }
        } finally {
            try {
                if (preparedStatement1 != null) {
                    preparedStatement1.close();
                }
                if (preparedStatement2 != null) {
                    preparedStatement2.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
    }
}

package cz.neumimto.rpg.persistence.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NamedPreparedStatement implements AutoCloseable {

    private PreparedStatement prepStmt;
    private List<String> fields = new ArrayList<>();
    private static Pattern findParametersPattern = Pattern.compile("(?<!')(:[\\w]*:)(?!')");

    public NamedPreparedStatement(Connection conn, String sql, int... params) throws SQLException {
        Matcher matcher = findParametersPattern.matcher(sql);
        while (matcher.find()) {
            String group = matcher.group();
            fields.add(group);
        }
        sql = sql.replaceAll(findParametersPattern.pattern(), "?");
        prepStmt = conn.prepareStatement(sql);
        prepStmt = conn.prepareStatement(sql, params);
    }

    public PreparedStatement getPreparedStatement() {
        return prepStmt;
    }

    public ResultSet executeQuery() throws SQLException {
        return prepStmt.executeQuery();
    }

    public void close() throws SQLException {
        prepStmt.close();
    }

    public void setInt(String name, int value) throws SQLException {
        int index = getIndex(name);
        if (index > 0) {
            prepStmt.setInt(index, value);
        }
    }

    public void setString(String name, String value) throws SQLException {
        int index = getIndex(name);
        if (index > 0) {
            prepStmt.setString(index, value);
        }
    }

    public void setLong(String name, long value) throws SQLException {
        int index = getIndex(name);
        if (index > 0) {
            prepStmt.setLong(index, value);
        }
    }

    public void setDouble(String name, double value) throws SQLException {
        int index = getIndex(name);
        if (index > 0) {
            prepStmt.setDouble(index, value);
        }
    }

    public void setBoolean(String name, boolean value) throws SQLException {
        int index = getIndex(name);
        if (index > 0) {
            prepStmt.setBoolean(index, value);
        }
    }

    private int getIndex(String name) {
        return fields.indexOf(name) + 1;
    }

    public void setDate(String name, Date date) throws SQLException {
        int index = getIndex(name);
        if (index > 0) {
            long time = date.getTime();
            java.sql.Timestamp timestamp = new java.sql.Timestamp(time);
            prepStmt.setTimestamp(index, timestamp);
        }
    }

    public long executeQueryAndGetId() throws SQLException {
        int i = getPreparedStatement().executeUpdate();
        if (i == 0) {
            throw new SQLException("More than one id created");
        }
        try (ResultSet generatedKeys = getPreparedStatement().getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        }
        throw new SQLException("Database does not support JDBC RETURN_GENERATED_KEYS");
    }
}

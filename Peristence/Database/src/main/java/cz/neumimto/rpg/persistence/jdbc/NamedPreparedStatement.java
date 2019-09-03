package cz.neumimto.rpg.persistence.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NamedPreparedStatement implements AutoCloseable {

    private PreparedStatement prepStmt;
    private List<String> fields = new ArrayList<>();

    public NamedPreparedStatement(Connection conn, String sql, int... params) throws SQLException {
        int pos;
        while ((pos = sql.indexOf(":")) != -1) {
            int end = sql.substring(pos).indexOf(":");
            if (end == -1)
                end = sql.length();
            else
                end += pos;
            fields.add(sql.substring(pos + 1, end));
            sql = sql.substring(0, pos) + "?" + sql.substring(end);
        }
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

        }
        prepStmt.setString(index, value);
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

    public void setDate(String s, Date lastReset) {

    }
}

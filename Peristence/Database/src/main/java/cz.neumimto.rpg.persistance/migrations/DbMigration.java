package cz.neumimto.rpg.persistance.migrations;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by NeumimTo on 24.6.2018.
 */
public class DbMigration implements Comparable<DbMigration> {
    private String id;
    private String author;
    private String note;
    private Date date;
    private String sql = "";

    public static synchronized List<DbMigration> from(String data) {
        Pattern compile = Pattern.compile("(?<=:).*");

        DbMigration cached = null;
        List<DbMigration> list = new ArrayList<>();

        String[] split = data.split("\n");
        for (String s : split) {
            if (s.startsWith("--@author:")) {
                validate(cached);
                Matcher matcher = compile.matcher(s);
                matcher.find();
                cached.author = matcher.group();
            } else if (s.startsWith("--@note:")) {
                validate(cached);
                Matcher matcher = compile.matcher(s);
                matcher.find();
                cached.note = matcher.group();
            } else if (s.startsWith("--@date:")) {
                validate(cached);
                Matcher matcher = compile.matcher(s);
                matcher.find();
                DateFormat df = new SimpleDateFormat("dd.MM.YYYY HH:mm");
                try {
                    cached.date = df.parse(matcher.group());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //always has to start with id
            } else if (s.startsWith("--@id:")) {
                if (cached != null) {
                    list.add(cached);
                } else {
                    cached = new DbMigration();
                    list.add(cached);
                }
                Matcher matcher = compile.matcher(s);
                matcher.find();
                cached.id = matcher.group();
            } else {
                cached.setSql(cached.sql += s);
            }
        }
        return list;
    }

    private static void validate(DbMigration cached) {
        if (cached == null) {
            throw new RuntimeException(
                    "Invalid migration format" +
                            "\nMigration has to be in the following format:" +
                            "\n--@id: - always first - unique id" +
                            "\n--@author: - mandatory - who is the author" +
                            "\n--@note: - optional - some relevant notes" +
                            "\n--@date: - mandatory - when was the migration written" +
                            "\n...sql...");
        }
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getSql() {
        return sql;
    }

    public String getNote() {
        return note;
    }

    public void setSql(String sql) {
        if (Objects.isNull(id) || Objects.isNull(date) || Objects.isNull(author)) {
            throw new RuntimeException("Invalid migration, at least one of id, date, author is missing");
        }
        this.sql = sql;
    }

    @Override
    public int compareTo(DbMigration o) {
        return date.after(o.date) ? 1 : -1;
    }
}

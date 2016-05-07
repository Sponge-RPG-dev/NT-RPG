package cz.neumito.rpg.rest;

/**
 * Created by me pro on 20.04.2016.
 */
public class Token {

    protected final String token;
    protected long time;

    public Token(String token) {
        this.token = token;
        time = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return token;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj.getClass() != Token.class)
            return false;
        return ((Token)obj).token.equals(this.token);
    }
}

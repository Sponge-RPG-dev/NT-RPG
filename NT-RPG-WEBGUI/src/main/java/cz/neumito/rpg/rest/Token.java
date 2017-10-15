package cz.neumito.rpg.rest;

import com.google.gson.annotations.SerializedName;

/**
 * Created by me pro on 20.04.2016.
 */
public class Token {

	@SerializedName("token")
	protected String token;
	protected long time;

	public Token() {
	}

	public Token(String token) {
		this.token = token;
		time = System.currentTimeMillis();
	}


	public String getToken() {
		return token;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
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
		return ((Token) obj).token.equals(this.token);
	}
}

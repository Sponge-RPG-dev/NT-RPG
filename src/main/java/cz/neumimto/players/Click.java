package cz.neumimto.players;

/**
 * Created by NeumimTo on 12.2.2015.
 */
public class Click {
    private long lastTime;
    private int times;

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }
}

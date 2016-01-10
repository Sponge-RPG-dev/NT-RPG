package cz.neumimto.utils;


public class XORShiftRnd {

    private long l;

    public XORShiftRnd() {
        this(System.currentTimeMillis());
    }

    public XORShiftRnd(long seed) {
        this.l = seed;
    }

    public int nextInt(int max) {
        int out = nextInt() % max;
        return (out < 0) ? -out : out;
    }

    public int nextInt() {
        l ^= (l << 21);
        l ^= (l >>> 35);
        l ^= (l << 4);
        return (int) l;
    }
}

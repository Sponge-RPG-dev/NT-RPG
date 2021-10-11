package cz.neumimto.rpg.common.utils.rng;

//todo move this to ntcore
public class XORShiftRnd {

    private long l;

    public XORShiftRnd() {
        this(System.currentTimeMillis());
    }

    public XORShiftRnd(long seed) {
        if (seed == 0) {
            throw new RuntimeException("The Seed of xor shift rnd can't be 0");
        }
        this.l = seed;
    }

    public int nextInt(int max) {
        int out = nextInt() % max;
        return (out < 0) ? -out : out;
    }

    public double nextDouble(double max) {
        double out = nextInt() % max;
        return (out < 0) ? -out * 0.01d : out * 0.01d;  //.00 precision is enough
    }

    //just so we dont have to typecast after
    public float nextFloat(int max) {
        int out = nextInt() % max;
        return (out < 0) ? -out * 0.01f : out * 0.01f;  //.00 precision is enough
    }

    public int nextInt() {
        l ^= (l << 21);
        l ^= (l >>> 35);
        l ^= (l << 4);
        return (int) l;
    }

    public long nextLong(long max) {
        long out = nextLong() % max;
        return (out < 0) ? -out : out;
    }

    public long nextLong() {
        l ^= (l << 21);
        l ^= (l >>> 35);
        l ^= (l << 4);
        return l;
    }


}

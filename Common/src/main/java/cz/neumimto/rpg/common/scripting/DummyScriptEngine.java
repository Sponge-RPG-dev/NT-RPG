package cz.neumimto.rpg.common.scripting;

public class DummyScriptEngine extends AbstractRpgScriptEngine{
    @Override
    public void prepareEngine() {

    }

    @Override
    public Object fn(String functionName, Object... args) {
        return null;
    }

    @Override
    public Object fn(String functionName) {
        return null;
    }

    @Override
    public <T> T eval(String expr, Class<T> t) {
        return null;
    }

    @Override
    public <T> T extract(Object o, String key, T def) {
        return null;
    }
}

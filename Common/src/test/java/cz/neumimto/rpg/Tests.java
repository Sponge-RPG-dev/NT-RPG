package cz.neumimto.rpg;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Tests {

    /* @Test
     public void testDynamicListener() throws Exception {
         Class.forName("jdk.nashorn.api.scripting.NashornScriptEngineFactory");
         ScriptEngine engine = new NashornScriptEngineFactory().getScriptEngine();
         ClassGenerator classGenerator = new SpongeClassGenerator();
         try (InputStreamReader rs = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("js/eventgen/test.js"))) {
             engine.eval(rs);
             List list = (List) engine.get("events");
             Object o = classGenerator.generateDynamicListener(list);
             Assertions.assertSame(o.getClass().getDeclaredMethods().length, 3);
         } catch (ScriptException | IOException e) {
             Assertions.fail(e.getMessage());
         }
     }
 */
    @Test
    public void k() {
        int x = -256;
        boolean is16 = (x & 0x0F) == 0;
        Assertions.assertTrue(is16);
        is16 = x == (x >> 4) << 4;
        Assertions.assertTrue(is16);
    }

}





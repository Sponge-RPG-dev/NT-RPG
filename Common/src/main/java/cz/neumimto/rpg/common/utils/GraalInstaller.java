package cz.neumimto.rpg.common.utils;

import cz.neumimto.rpg.api.Rpg;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;

public class GraalInstaller {


//https://medium.com/graalvm/graalvms-javascript-engine-on-jdk11-with-high-performance-3e79f968a819
    public static void downloadTo(Path root, Consumer<String> callback) {
        var r = (Runnable) () -> {
            String version = "21.1.0";
            String[][] files = new String[][] {
                    {"https://repo1.maven.org/maven2/org/graalvm/sdk/graal-sdk/%s/graal-sdk-%s.jar","graal-sdk-%s.jar"},
                    {"https://repo1.maven.org/maven2/org/graalvm/js/js/%s/js-%s.jar", "graal-js-%s.jar"},
                    {"https://repo1.maven.org/maven2/org/graalvm/truffle/truffle-api/%s/truffle-api-%s.jar", "graal-truffle-api-%s.jar"},
                    {"https://repo1.maven.org/maven2/org/graalvm/regex/regex/%s/regex-%s.jar", "graal-regex-%s.jar"},
                    {"https://repo1.maven.org/maven2/com/ibm/icu/icu4j/68.2/icu4j-68.2.jar", "icu4j.jar"}
            };

            try {
                for (String[] file : files) {;
                    String download = file[0].replaceAll("%s", version);
                    Path target = root.resolve(file[1].replaceAll("%s", version));
                    callback.accept("Downloading " + download + " to " + target);

                    Files.copy(new URL(download).openStream(), target, StandardCopyOption.REPLACE_EXISTING);
                    callback.accept(" Done");

                }
            } catch (Throwable e) {
                e.printStackTrace();
                callback.accept("Unable to download file");
                throw new RuntimeException("Unable to download graal binaries", e);
            }
        };

        new Thread(r).start();
    }

    public static boolean check() {
        try {
            Class.forName("org.graalvm.polyglot.Engine");
            return true;
        } catch (Throwable e) {
            return false;
        }
    }
}

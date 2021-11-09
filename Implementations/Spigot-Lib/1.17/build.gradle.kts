plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.1.14"
}

group = "cz.neumimto.rpg.nms117"
version = "1.0.0"

dependencies {
    paperDevBundle("1.17.1-R0.1-SNAPSHOT")
    compileOnly(project(":Spigot-NMS"))
    //implementation(fileTree(mapOf("dir" to "lib", "include" to listOf("*.jar"))))
}

tasks {
    build {
        dependsOn(reobfJar)
        mustRunAfter(":Spigot-NMS:build")

    }
}

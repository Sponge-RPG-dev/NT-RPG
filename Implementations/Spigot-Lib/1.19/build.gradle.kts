plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.3.3"
}

group = "cz.neumimto.rpg.nms119"
version = "1.0.0"

repositories {
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
}

dependencies {
    paperDevBundle("1.19-R0.1-SNAPSHOT")
    compileOnly(project(":Spigot-NMS"))
}

tasks {
    build {
        dependsOn(reobfJar)
        mustRunAfter(":Spigot-NMS:build")
    }
}


plugins {
    `java-library`
}

group = "cz.neumimto.rpg.nms117"
version = "1.0.0-SNAPSHOT"

repositories {
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
}
plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.7.20"
}

group = "org.qiuhua.genshinattributesbackpack"
version = "2.0.0"

repositories {
    mavenLocal()  //加载本地仓库
    mavenCentral()  //加载中央仓库
    maven {
        name = "spigotmc-repo"
        url = uri ("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }  //SpigotMC仓库
    maven {
        url = uri("https://repo.tabooproject.org/repository/releases/")
        isAllowInsecureProtocol = true
    }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")  //仅在编译时可用
    compileOnly(fileTree("src/libs"))
    compileOnly("ink.ptms.adyeshach:all:2.0.0-snapshot-25")
    compileOnly("mysql:mysql-connector-java:8.0.33")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.1.3")
    implementation("org.apache.httpcomponents.client5:httpclient5-fluent:5.1.3")
    implementation("org.apache.httpcomponents.core5:httpcore5:5.2.4")
    implementation("org.apache.httpcomponents.core5:httpcore5-h2:5.2.4")
    implementation("com.google.code.gson:gson:2.10.1")

}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<Jar>().configureEach {
    archiveFileName.set("GenshinAttributesBackpack-2.5.0.jar")
    destinationDirectory.set(File ("D:\\我的世界插件"))
}

tasks.withType<JavaCompile>{
    options.encoding = "UTF-8"
}
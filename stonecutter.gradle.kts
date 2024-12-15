plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.8-SNAPSHOT" apply false
    id("me.modmuss50.mod-publish-plugin") version "0.7.+" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}
stonecutter active "1.21.4" /* [SC] DO NOT EDIT */

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "project"
    ofTask("buildAndCollect")
}

stonecutter registerChiseled tasks.register("chiseledPublishMods", stonecutter.chiseled) {
    group = "project"
    ofTask("publishMods")
}

stonecutter configureEach {
    swap("mod_version", "\"${property("mod.version")}\";")
    const("release", property("mod.id") != "template")
    dependency("fapi", project.property("deps.fabric_api").toString())
}

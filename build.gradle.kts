import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("fabric-loom") version "1.6-SNAPSHOT"
	id("maven-publish")
	id("org.jetbrains.kotlin.jvm") version "2.0.0"
}

version = project.extra["mod_version"] as String
group = project.extra["maven_group"] as String

base {
	archivesName.set(project.extra["archives_base_name"] as String)
}

loom {
	accessWidenerPath = file("src/main/resources/joker.accesswidener")
}

repositories {
	maven(url = "https://maven.ladysnake.org/releases")
	maven(url = "https://maven.parchmentmc.org")
}

fabricApi {
	configureDataGeneration()
}

dependencies {
	minecraft("com.mojang:minecraft:${project.extra["minecraft_version"] as String}")
	//mappings(loom.officialMojangMappings())

	mappings(loom.layered {
		officialMojangMappings()
		parchment("org.parchmentmc.data:parchment-1.20.6:2024.06.02@zip")
	})

	modImplementation("net.fabricmc:fabric-loader:${project.extra["loader_version"] as String}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${project.extra["fabric_version"] as String}")
	modImplementation("net.fabricmc:fabric-language-kotlin:${project.extra["fabric_kotlin_version"] as String}")

	modImplementation("org.ladysnake.cardinal-components-api:cardinal-components-base:6.1.0")
	modImplementation("org.ladysnake.cardinal-components-api:cardinal-components-entity:6.1.0")
	include("org.ladysnake.cardinal-components-api:cardinal-components-base:6.1.0")
	include("org.ladysnake.cardinal-components-api:cardinal-components-entity:6.1.0")
}

tasks.named<ProcessResources>("processResources") {
	inputs.property("version", project.version)

	filesMatching("fabric.mod.json") {
		expand("version" to project.version)
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release.set(21)
}

tasks.withType<KotlinCompile>().configureEach {
	compilerOptions {
		jvmTarget.set(JvmTarget.JVM_21)
	}
}

java {
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

tasks.named<Jar>("jar") {
	from("LICENSE") {
		rename { "${it}_${project.name}" }
	}
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifactId = project.name
			from(components["java"])
		}
	}

	repositories {
		// Define your repositories here if any
	}
}

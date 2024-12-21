import nu.studer.gradle.jooq.JooqGenerate

plugins {
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
    id("nu.studer.jooq") version "8.2"
    id("org.liquibase.gradle") version "2.2.1"
}

group = "com.hulk"
version = "0.0.1-SNAPSHOT"

val springDocVersion = "2.3.0"
val jjwtVersion = "0.12.6"

val jdbcDriver = "org.postgresql.Driver"
val datasourceUrl = "jdbc:postgresql://localhost:5432/university-db"
val datasourceUsername = "postgres"
val datasourcePassword = "postgres"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.liquibase:liquibase-core")
    runtimeOnly("org.postgresql:postgresql")
    jooqGenerator("org.postgresql:postgresql")
    liquibaseRuntime("org.liquibase:liquibase-core:4.25.1")
    liquibaseRuntime("info.picocli:picocli:4.7.5")
    liquibaseRuntime("org.postgresql:postgresql:42.7.2")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${springDocVersion}")
    implementation("org.springframework.boot:spring-boot-starter-security")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:${jjwtVersion}")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:${jjwtVersion}")
    implementation("io.jsonwebtoken:jjwt-api:${jjwtVersion}")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
}

liquibase {
    activities.register("main") {
        this.arguments = mapOf(
            "changelogFile" to "db.changelog-master.xml",
            "url" to datasourceUrl,
            "username" to datasourceUsername,
            "password" to datasourcePassword,
            "driver" to jdbcDriver,
            "searchPath" to project.rootDir.absolutePath + "/src/main/resources/db/"
        )
    }
    runList = "main"
}

jooq {
    configurations {
        create("main") {
            jooqConfiguration.apply {
                jdbc.apply {
                    driver = jdbcDriver
                    url = datasourceUrl
                    user = datasourceUsername
                    password = datasourcePassword
                    generator.apply {
                        name = "org.jooq.codegen.KotlinGenerator"
                        database.apply {
                            name = "org.jooq.meta.postgres.PostgresDatabase"
                            inputSchema = "public"
                            excludes = "DATABASECHANGELOG|DATABASECHANGELOGLOCK"
                        }
                        generate.apply {
                            isTables = true
                            isRecords = true
                            isDaos = true
                            isPojos = true
                            isPojosAsKotlinDataClasses = true
                            isGeneratedAnnotation = true
                            isSpringAnnotations = true
                        }
                        target.apply {
                            packageName = "com.hulk.university"
                        }
                        strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                    }
                }
            }
        }
    }
}

tasks.withType<JooqGenerate> {
    dependsOn(tasks.named("update"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    dependsOn(tasks.withType<JooqGenerate>())
}

tasks.withType<Test> {
    useJUnitPlatform()
}

configurations.all {
    exclude(group = "org.slf4j", module = "slf4j-simple")
}


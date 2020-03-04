plugins {
    `java-library`
    kotlin("jvm") version "1.2.71"
    `project-report`
}

repositories {
    jcenter()
}

val slf4jVersion by extra { "1.7.25" }
val logbackVersion by extra { "1.2.3" }
val argparserVersion by extra { "2.0.3" }
val commonsCodecVersion by extra { "1.11" }
val commonsCompressVersion by extra { "1.18" }
val rxVersion by extra { "2.3.0" }
val xzVersion by extra { "1.8" }
val tikaVersion by extra { "1.19" }
val retrofitVersion by extra { "2.4.0" }

dependencies {
    compile(kotlin("stdlib"))
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.slf4j:jcl-over-slf4j:$slf4jVersion")
    implementation("org.slf4j:jul-to-slf4j:$slf4jVersion")
    implementation("org.slf4j:log4j-over-slf4j:$slf4jVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("com.xenomachina:kotlin-argparser:$argparserVersion")
    implementation("commons-codec:commons-codec:$commonsCodecVersion")
    implementation("io.reactivex.rxjava2:rxkotlin:$rxVersion")
    implementation("org.apache.commons:commons-compress:$commonsCompressVersion")
    implementation("org.tukaani:xz:$xzVersion")
    implementation("org.apache.tika:tika-core:$tikaVersion")
    implementation("org.apache.tika:tika-parsers:$tikaVersion") {
        exclude(group = "commons-logging")
        exclude(group = "org.slf4j", module = "slf4j-log4j12")
    }
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:adapter-rxjava2:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-simplexml:$retrofitVersion")

    // Use JUnit test framework
    testImplementation("junit:junit:4.12")
}


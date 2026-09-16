import com.vanniktech.maven.publish.AndroidSingleVariantLibrary

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.maven.publish)
}

android {
    namespace = "mobi.parchment"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
    }

    testOptions {
        targetSdk = libs.versions.targetSdk.get().toInt()
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    lint {
        targetSdk = libs.versions.targetSdk.get().toInt()
        abortOnError = true
        warningsAsErrors = true
        checkReleaseBuilds = true
        xmlReport = true
        htmlReport = true
    }
}

dependencies {
    compileOnly(libs.androidx.annotation)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.assertj.core)
    testImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.espresso.core)
}

tasks.withType<JavaCompile>().configureEach {
    // -options: AGP's bootclasspath is not a system-modules path. -this-escape: View constructors
    // must call the overridable AttributeSet hook. -classfile: Robolectric's jar references an
    // android.annotation type that is not on the unit-test classpath.
    val lint = mutableListOf("all", "-options", "-this-escape")
    if (name.contains("UnitTest")) lint += "-classfile"
    options.compilerArgs.addAll(listOf("-Xlint:" + lint.joinToString(","), "-Werror"))
}

tasks.withType<Test>().configureEach {
    maxHeapSize = "1g"
    jvmArgs("-XX:+ExitOnOutOfMemoryError")
    // Robolectric 4.17 reaches into these JDK internals and the module system refuses it otherwise.
    jvmArgs(
        "--add-opens=java.base/java.lang=ALL-UNNAMED",
        "--add-opens=java.base/java.util=ALL-UNNAMED",
        "--add-opens=java.base/java.io=ALL-UNNAMED",
        "--add-opens=java.base/java.net=ALL-UNNAMED",
        "--add-opens=java.base/java.security=ALL-UNNAMED",
        "--add-opens=java.base/java.text=ALL-UNNAMED",
        "--add-opens=java.base/jdk.internal.access=ALL-UNNAMED",
        "--add-opens=java.desktop/java.awt.font=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
    )
    // Robolectric URL-encodes the android-all jar path, so a home directory with a space breaks
    // its native runtime; the gitignored .robolectric/ stands in for ~ on those machines.
    val home = System.getProperty("user.home")
    if (home.contains(' ')) {
        val robolectricHome = rootProject.layout.projectDirectory.dir(".robolectric").asFile
        systemProperty("user.home", robolectricHome.absolutePath)
        doFirst { robolectricHome.mkdirs() }
    }
}

// Coordinates and POM come from the GROUP, VERSION_NAME and POM_* keys in gradle.properties.
// Signing needs the release key, which only the release workflow has; without it the artifacts
// still publish to mavenLocal, and Maven Central rejects an unsigned upload on its own.
mavenPublishing {
    configure(AndroidSingleVariantLibrary("release", sourcesJar = true, publishJavadocJar = true))
    publishToMavenCentral()
    if (providers.gradleProperty("signingInMemoryKey").isPresent) signAllPublications()
}

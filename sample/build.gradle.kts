plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "mobi.parchment.sample"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "mobi.parchment.sample"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "2.0.0-SNAPSHOT"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    lint {
        abortOnError = true
        warningsAsErrors = true
        checkReleaseBuilds = true
        xmlReport = true
        htmlReport = true
    }
}

dependencies {
    implementation(project(":library"))
    implementation(libs.picasso)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.assertj.core)
    testImplementation(libs.androidx.test.core)
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
    // Robolectric URL-encodes the android-all jar path, so a home directory with a space breaks
    // its native runtime; the gitignored .robolectric/ stands in for ~ on those machines.
    val home = System.getProperty("user.home")
    if (home.contains(' ')) {
        val robolectricHome = rootProject.layout.projectDirectory.dir(".robolectric").asFile
        systemProperty("user.home", robolectricHome.absolutePath)
        doFirst { robolectricHome.mkdirs() }
    }
}

plugins {
    alias(libs.plugins.android.library)
    `maven-publish`
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
            // Libraries ship unobfuscated; consumers shrink with consumer-rules.pro applied.
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

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
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
    // -options: javac warns that the Android bootclasspath is not a system-modules path, which is
    // inherent to AGP. -this-escape: every custom View reads its AttributeSet from the constructor
    // through an overridable hook; that is the Android pattern, not a defect.
    val lint = mutableListOf("all", "-options", "-this-escape")
    // Robolectric's jar references android.annotation.RequiresApi, which is absent from the
    // unit-test classpath; javac reports that as a classfile warning nothing here can act on.
    if (name.contains("UnitTest")) lint += "-classfile"
    options.compilerArgs.addAll(listOf("-Xlint:" + lint.joinToString(","), "-Werror"))
}

tasks.withType<Test>().configureEach {
    maxHeapSize = "1g"
    // An OOM in a test worker must kill the worker loudly, not wedge the JVM mid-run.
    jvmArgs("-XX:+ExitOnOutOfMemoryError")
    // Robolectric turns the android-all jar location into a URL and back, so a home directory
    // with a space ("C:\Users\First Last") becomes "First%20Last" and the native runtime cannot
    // load. Give the test JVM a space-free home (gitignored) so the jar cache lands somewhere
    // readable; machines without a space in the path keep the default ~/.m2 cache.
    val home = System.getProperty("user.home")
    if (home.contains(' ')) {
        val robolectricHome = rootProject.layout.projectDirectory.dir(".robolectric").asFile
        systemProperty("user.home", robolectricHome.absolutePath)
        // Robolectric writes its download lock straight into the home directory without creating it.
        doFirst { robolectricHome.mkdirs() }
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = property("GROUP").toString()
                artifactId = property("POM_ARTIFACT_ID").toString()
                version = property("VERSION_NAME").toString()

                pom {
                    name.set(property("POM_NAME").toString())
                    description.set(property("POM_DESCRIPTION").toString())
                    url.set(property("POM_URL").toString())

                    licenses {
                        license {
                            name.set(property("POM_LICENCE_NAME").toString())
                            url.set(property("POM_LICENCE_URL").toString())
                        }
                    }

                    developers {
                        developer {
                            id.set(property("POM_DEVELOPER_ID").toString())
                            name.set(property("POM_DEVELOPER_NAME").toString())
                        }
                    }

                    scm {
                        url.set(property("POM_SCM_URL").toString())
                        connection.set(property("POM_SCM_CONNECTION").toString())
                        developerConnection.set(property("POM_SCM_DEV_CONNECTION").toString())
                    }
                }
            }
        }
    }
}

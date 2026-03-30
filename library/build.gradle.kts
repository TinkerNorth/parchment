plugins {
    id("com.android.library")
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
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
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
        baseline = file("lint-baseline.xml")
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.assertj.core)
    testImplementation(libs.androidx.test.core)
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


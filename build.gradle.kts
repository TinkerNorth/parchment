plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.spotless)
}

spotless {
    java {
        target("library/src/**/*.java", "sample/src/**/*.java")
        targetExclude("**/build/**")
        googleJavaFormat(libs.versions.googleJavaFormat.get()).aosp().reflowLongStrings()
        licenseHeaderFile(rootProject.file("config/spotless/license-header.java"))
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
    format("misc") {
        target("*.md", "*.toml", "**/*.pro", ".gitignore", ".editorconfig", "gradle/libs.versions.toml")
        targetExclude("**/build/**")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

# Third-party attributions

The library has no runtime dependencies. The compile-time, test, and sample
dependencies below are resolved from Maven Central and Google Maven at the
versions pinned in `gradle/libs.versions.toml`.

## Library (compile-time only)

- **androidx.annotation** (Apache-2.0): nullability and threading annotations.
  `compileOnly`, so nothing is added to consumers' classpaths.

## Tests

- **JUnit 4** (EPL-1.0)
- **Robolectric** (MIT): JVM Android framework for the unit tests.
- **AssertJ** (Apache-2.0)
- **androidx.test** core, ext:junit, runner, rules, and Espresso (Apache-2.0):
  instrumented test runner and utilities.

## Sample app

- **Picasso** (Apache-2.0): image loading in the demo adapters.
- **Unsplash** photos: the demo images the adapters load. They are hotlinked
  from the Unsplash CDN (`images.unsplash.com`) with a `w` parameter, which is
  how the [Unsplash License](https://unsplash.com/license) asks that they be
  served, and are not part of this repository. An app that ships should bundle
  its own images rather than depend on a CDN it does not control.

## Build

- **Android Gradle Plugin** (Apache-2.0)
- **Spotless** with **google-java-format** (Apache-2.0)
- **Gradle** (Apache-2.0) and the **Foojay toolchain resolver** (Apache-2.0)

# Parchment

Horizontal/Vertical **ListView**, **GridView**, **ViewPager**, and **GridPatternView** for Android.

![Parchment Screenshot](https://i.imgur.com/2ArOltz.png)

## Overview

Parchment provides horizontal and vertical scrolling AdapterViews with snap positioning, circular scrolling, and flexible grid patterns. Choose from:

- **ListView** — horizontal or vertical scrolling list
- **GridView** — grid with wrapping height support
- **GridPatternView** — user-defined grid patterns
- **ViewPager** — page-based scrolling via ListView configuration

## Requirements

- Android 5.0+ (API 21)
- Java 17+

## Building

```bash
# Clone the repository
git clone https://github.com/EmirWeb/parchment.git
cd parchment

# Build the library
./gradlew :library:assembleDebug

# Run the sample app
./gradlew :sample:installDebug

# Run unit tests
./gradlew :library:test
```

## Project Structure

| Module | Description |
|--------|-------------|
| `library` | Core Parchment library — custom AdapterViews and layout managers |
| `sample` | Demo app showcasing ListView, GridView, GridPatternView, and ViewPager |

## Getting Started

### Step 1: Add Dependency

Add the library module to your project, or reference it as a dependency:

```kotlin
dependencies {
    implementation(project(":library"))
}
```

### Step 2: XML Layout

Add an AdapterView and choose an orientation:

```xml
<mobi.parchment.widget.adapterview.listview.ListView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:parchment="http://schemas.android.com/apk/res-auto"
    android:id="@+id/horizontal_list_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    parchment:orientation="horizontal"
    parchment:cellSpacing="10dp"
    parchment:snapPosition="center"
    parchment:snapToPosition="true"
    parchment:isCircularScroll="false"
    parchment:isViewPager="false" />
```

### Step 3: Java Code

Set your adapter:

```java
ListView listView = findViewById(R.id.horizontal_list_view);
listView.setAdapter(adapter);
```

## XML Attributes

### ListView

| Attribute | Type | Description |
|-----------|------|-------------|
| `orientation` | `horizontal` \| `vertical` | Scroll direction |
| `cellSpacing` | dimension | Space between items |
| `isCircularScroll` | boolean | Enable infinite circular scrolling |
| `snapToPosition` | boolean | Snap items to position |
| `snapPosition` | `center` \| `start` \| `end` \| `onScreen` | Where to snap |
| `selectOnSnap` | boolean | Select item on snap |
| `selectWhileScrolling` | boolean | Select items during scroll |
| `isViewPager` | boolean | Enable ViewPager-like behavior |

### GridView

| Attribute | Type | Description |
|-----------|------|-------------|
| `numberOfViewsPerCell` | integer | Views per grid cell |
| `gravity` | flags | Cell gravity (`left`, `right`, `top`, `bottom`) |

### GridPatternView

| Attribute | Type | Description |
|-----------|------|-------------|
| `ratio` | float | Grid pattern aspect ratio |

## Contributors

See [CONTRIBUTING.md](CONTRIBUTING.md)

## License

Copyright 2014 Emir Hasanbegovic

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

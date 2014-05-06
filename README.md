Parchment
===============================

[![Build Status](https://travis-ci.org/EmirWeb/parchment.png?branch=master)](https://travis-ci.org/EmirWeb/parchment)

## Horizontal/Vertical ListView, GridView, ViewPager, and GridPatternView

The Parchment library attempts to add the functionality that developers want to their AdapterViews, by providing horizontal and vertical scrolling to all of its AdapterViews. Parchment gives developers more control of layout by choosing from a simple horizontal List View, a GridView that wraps heights or a GridPatternView that lets a user specify the grid pattern.

![alt tag](https://i.imgur.com/2ArOltz.png)

## Requires
Android 2.2 +

# Getting Started

## Step 1: Set up
Include the Parchment library in your pom.xml file as follows:
```xml
<dependency>
    <groupId>mobi.parchment</groupId>
    <artifactId>parchment</artifactId>
    <version>1.6.6</version>
    <type>aar</type>
</dependency>
```

or, to your build.gradle as follows:

```java
dependencies {
    compile 'mobi.parchment:parchment:1.6.6@aar'
}
```
 

## Step 2: XML
Add one of the AdapterView classes (GridView, ListView, GridPatternView), don't forget to choose an orientation:

```xml
<mobi.parchment.widget.adapterview.listview.ListView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:parchment="http://schemas.android.com/apk/res/<YOUR PACKAGE NAME>"
    android:id="@+id/horizontal_list_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:scrollbars="< horizontal | vertical >"
    android:scrollbarAlwaysDrawHorizontalTrack="< true | false >"
    android:scrollbarAlwaysDrawVerticalTrack="< true | false >"
    android:clipToPadding="< true | false >"
    parchment:isViewPager="< true | false >"
    parchment:orientation="< horizontal | vertical >"
    parchment:cellSpacing="10dp"
    parchment:isCircularScroll="< true | false >"
    parchment:snapPosition="< center | start | end | onScreen >"
    parchment:snapToPosition="< true | false >" />
```

## Step 3: Java
Set Adapters and go:

```java
    mListView.setAdapter(mAdapter);
```



## Story Tracking Tool:

[PivotalTracker][1]


### License:

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

[1]: https://www.pivotaltracker.com/s/projects/1000984#

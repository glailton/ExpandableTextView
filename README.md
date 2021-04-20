# ExpandableTextView

[![Download](https://img.shields.io/badge/Download-1.0.1-lightgrey)](https://search.maven.org/artifact/io.github.glailton.expandabletextview/expandabletextview)
[![Preview the app](https://img.shields.io/badge/Preview-Appetize.io-orange.svg)](https://appetize.io/app/vg9evd5u6zc9bfpuj89wzd24tg)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

An Expandable TextView for Android written in
[Kotlin](https://kotlinlang.org/). The main ideia was study how create a library in Android and deploy
on [Maven Central Repository](https://s01.oss.sonatype.org).


## Table of Contents

- [Demo project](#demo-project)
- [Getting started](#getting-started)
- [Usage](#usage)
  - [Supported features](#supported-features)
  - [Extensions](#extensions)
- [Documentation](#documentation)
  - [Useful xml attributes](#useful-xml-attributes)
  - [Important notes](#important-notes)
- [Contributing](#contributing)
- [License](#license)
- [Acknowledgments](#acknowledgments)

## Demo Project

Take a look at the [demo project](app).

![Demo]()

## Getting Started

The library is included in Maven Central Repository, so just add this dependency to your module level `gradle.build`:

```kotlin
dependencies {
    implementation 'io.github.glailton.expandabletextview:expandabletextview:$LatestVersion'
}
```
Current latest version is: [![Download](https://img.shields.io/badge/Download-1.0.1-lightgrey)](https://search.maven.org/artifact/io.github.glailton.expandabletextview/expandabletextview)

## Usage

1. Define the `etv_collapsedLines` xml attribute (`setCollapsedLines(int lines)` method in Java or `collapsedLines` property in Kotlin) to set the number of lines in collapsed state.
2. Provide unique `id` so that library could restore its state after configuration change.

Then use `ExpandableTextView` just as you would use any other `TextView`.

Xml snippet:
```xml
<io.github.glailton.expandabletextview.ExpandableTextView
        android:id="@+id/expand_tv"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        expandableTextView:collapsedLines="3"
        expandableTextView:animDuration="500"
        expandableTextView:readMoreText="Leia mais"
        expandableTextView:readLessText="Leia menos"
        expandableTextView:textMode="line"
        expandableTextView:isExpanded="true"
        android:text="@string/very_long_text" />
```


### Supported features
- Setting maximum number of collapsed lines and maxim number of expanded lines via xml.

## Documentation
//TODO
Take a look at the library documentation with description of public functions and properties:

### Useful xml attributes

You can use `ExpandableTextView` in xml layouts in the same way as you would `TextView`.
The library provides following attributes in addition to the ones defined in `TextView`.

| Attribute name             | Format                                        | Description | Default |
| -------------------------|--------------------------------------------|-------------|---------|
| *etv_animationDuration* | integer >= 0 | Duration of expand/collapse animation in milliseconds. | 300 |
| *etv_collapsedLines* | integer >= 0 | Number of lines in collapsed state. Must not be greater than `etv_expandedLines`. |[`Integer.MAX_VALUE`](https://developer.android.com/reference/java/lang/Integer.html#MAX_VALUE) |
| *etv_expandedLines* | integer >= 0 | Number of lines in expanded state. Must not be less than `etv_collapsedLines`. | [`Integer.MAX_VALUE`](https://developer.android.com/reference/java/lang/Integer.html#MAX_VALUE) |

## Contributing

If you wish to send a pull request, please make sure to checkout from `main` branch and merge with `main` branch as well.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

This library was based on: [viewmore-textview](https://github.com/mike5v/viewmore-textview).

# ExpandableTextView

[![Download](https://img.shields.io/badge/Download-1.0.5-lightgrey)](https://search.maven.org/artifact/io.github.glailton.expandabletextview/expandabletextview/1.0.5/aar)
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
- [Documentation](#documentation)
- [Contributing](#contributing)
- [License](#license)
- [Acknowledgments](#acknowledgments)

## Demo Project

Take a look at the [demo project](app).

![Demo](resources/gif.gif)

## Getting Started

The library is included in Maven Central Repository, so just add this dependency to your module level `gradle.build`:

```kotlin
dependencies {
    implementation 'io.github.glailton.expandabletextview:expandabletextview:$LatestVersion'
}
```
Current latest version is: [![Download](https://img.shields.io/badge/Download-1.0.4-lightgrey)](https://search.maven.org/artifact/io.github.glailton.expandabletextview/expandabletextview)

## Usage

* collapsedLines -> number of visible lines
* isExpanded -> state of textview (default is false)
* animationDuration -> duration of the animation in ms (Will be implemented in a future version)
* readMoreText -> text to use instead of default ellipses
* readLessText -> text to use instead of default ellipses
* expandType -> type of expandable layout, could be layout or popup
* ellipsizeTextColor -> color of ellipsize text
* isUnderlined -> underline the ellipsize text

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
        app:expandType="layout"
        android:text="@string/very_long_text" />
```
or programmatically
```

        binding.expandTvProg
          .setAnimationDuration(500)
          .setReadMoreText("View More")
          .setReadLessText("View Less")
          .setCollapsedLines(3)
          .setIsExpanded(true)
          .setIsUnderlined(true)
          .setExpandType(EXPAND_TYPE_POPUP)
          .setEllipsizedTextColor(ContextCompat.getColor(this, R.color.purple_200))

        binding.expandTvProg.text =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua." +
                    "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat." +
                    "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur." +
                    "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."

        binding.expandTvProg.setOnClickListener {
            binding.expandTvProg.toggle()
            if (binding.expandTvVeryLong.isExpanded && binding.expandTvVeryLong.expandType == EXPAND_TYPE_LAYOUT)
                binding.expandTvVeryLong.toggle()
        }
```

### Supported features
- Setting maximum number of collapsed lines via xml.
- Setting the ellipsized text via xml.
- Setting the collapsed text via xml.
- Setting if the text starts expanded or collapsed.
- Setting if the expandable type is layout or popup

## Documentation
//TODO
Take a look at the library documentation with description of public functions and properties:


## Contributing

If you wish to send a pull request, please make sure to checkout from `main` branch and merge with `main` branch as well.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

This library was based on: [viewmore-textview](https://github.com/mike5v/viewmore-textview).

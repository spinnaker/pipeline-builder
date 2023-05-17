Spinnaker Pipeline Builder
==========================

A Java library to build Spinnaker pipelines and save them as JSON files.

# Build the library

```sh
$ ./gradlew build
```

This will generate two jar files in `pipeline-builder/build/libs`:
- `pipeline-builder-<version>.jar` is the jar file for the library itself.
- `pipeline-builder-<version>-sources.jar` contains the source code.

The version is defined in `gradle.properties`.

# Run the tests

```sh
$ ./gradlew check
```

If any test fails, open `pipeline-builder/build/reports/tests/test/index.html` in a browser to see the details.

# Documentation

To generate the javadocs, run `./gradlew javadoc` which should create the artifacts in `pipeline-builder/build/docs/javadoc`.

In addition to the Javadoc, see also the [tutorial and sample code](examples/README.md) in the `examples` directory,
as well as the [design principles](pipeline-builder/DESIGN.md) page for a description of the concepts
and techniques used throughout the code base.

# Loading the project in IntelliJ IDEA

In IntelliJ IDEA, use `File -> New -> Project from Existing Sources...` and select the `pipeline-builder` directory. Then select `Import project from external model` and select `Gradle`. Click `Create` and wait for the project to load.

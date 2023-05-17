# Design principles used in the pipeline-builder library

## Dependencies

The builder library makes heavy use of two Java libraries to generate pipelines, [Jackson](https://github.com/FasterXML/jackson) and [Lombok](https://projectlombok.org/).
Jackson provides an easy way to transform data classes to JSON, and Lombok uses annotations on these data classes to generate methods at compile time to make the creation of pipeline-related objects as simple as possible.

**note:** In this context, _data classes_ are class definitions that describe objects holding values. They do not perform any sort of computation on this data and their objects are generally immutable once constructed. They are similar to the new _record classes_ introduced [in JDK 16](https://openjdk.java.net/jeps/395).

This page describes some of the features of Jackson and Lombok used by the pipeline-builder library, for the purpose of explaining a few of its design choices. The examples given come directly from the library and help highlight the design principles on which it was built.

## JSON serialization with Jackson

The data classes used in this library are serialized to JSON with Jackson. Most use the default rules and serialize each field to a key with the same name, but a few other Jackson features are used.

### Renamed or generated fields

Some fields have a name that is more intuitive for the user and better describes the data contained in the field than the actual object key under which Spinnaker stores this data. For example, Slack notifications are sent to a _channel_ but Spinnaker uses `"address"` as the key for this data. We try to provide class and method names that are as intuitive as possible and specify the JSON name with `@JsonProperty` when the Java field name differs from the JSON one.

Some classes also need to store constant values in JSON fields that we don't need to expose to developers, and avoid requiring them to set these values. The `SlackNotification` class uses both techniques:

```java
public class SlackNotification extends Notification {
    @JsonProperty("address") // the actual key in which the channel name is stored
    private String channel;  // vs a name that developers will understand
    // ...

    @JsonProperty("type")
    public NotificationType getType() { // already set automatically, no need to include it in the builder
        return NotificationType.SLACK;  // enum value serialized to a custom string with @JsonValue (see below)
    }
}
```

This way developers can use `.channel("#my-channel")` and don't need to add an explicit `.type("slack")`.

### Enum serialization

The library tries to use enums wherever a field can only have a limited set of values. Simply returning a basic enum value would cause it to be serialized as the equivalent uppercase string, so these typically define a `@JsonValue` method to control how they should be serialized. `NotificationType` mentioned above is an example of an enum using this technique:

```java
public enum NotificationType {
    EMAIL,
    SLACK;

    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}
```

Jackson will serialize `NotificationType` objects by calling their `toJson` method to produce a string (the default behavior would produce the strings `"EMAIL"` and `"SLACK"`).


## Usage of Lombok's builder API

Lombok provides a `@Builder` annotation that lets developers create a fluent-style builder API for their classes. This annotation can be used on a class itself:

```java
    @Builder
    @AllArgsConstructor
    public class Stage {
        private final String type;
        private final String name;
    }
```

which auto-generates methods that let us build an object this way:
```java
Stage stage = Stage.builder()
                .type("wait")
                .name("Wait a few seconds")
                .build();
```

When used on the class itself, the builder API creates a setter method for each field defined in the class, using a single parameter of the same type as the field itself. This is particularly useful for data classes that contain lots of fields, since we no longer have to create individual setter methods and all the boilerplate that goes with it.

In our case, placing the `@Builder` annotation on the class itself is not ideal since it creates a builder API that is aware of and exposes every single field in the class as well as its actual type.

In many cases it can be advantageous to have a higher-level builder API that does not necessarily correspond one-to-one to all of the individual fields. For example, Spinnaker pipelines contain a list of Stage objects, each having a string identifier to represent its unique IDs, stored in the `refId` field. These are strings, but typically contain a number so the first stage might have `refId: "1"`, and the second would have both `refId: "2"` and a parent link with `requisiteStageRefIds: ["1"]`. 

Stages also include a few booleans that define what should happen to the pipeline execution on failure:
* "halt entire pipeline" sets `failPipeline: true`, `completeOtherBranchesThenFail: true`, `continuePipeline: false`
* "halt current branch" sets `failPipeline: false`, `completeOtherBranchesThenFail: false`, `continuePipeline: false`
* "ignore failure" sets `failPipeline: false`, `completeOtherBranchesThenFail: false`, `continuePipeline: true`

If we define our Stage class this way:
```java
@Builder
@Getter
@AllArgsConstructor
public class Stage {
    private String refId;
    @Builder.Default private List<String> requisiteRefIds = List.of();
    @Builder.Default private boolean failPipeline = true;
    @Builder.Default private boolean completeOtherBranchesThenFail = true;
    @Builder.Default private boolean continuePipeline = false;
}
```

We would then build stage objects with:
```java
Stage s1 = Stage.builder()
    .refId("1")
    .build();
Stage s2 = Stage.builder()
    .refId("2")
    .requisiteRefIds(List.of(s1.getRefId()))
    .failPipeline(false)                    // part of "ignore failure"
    .completeOtherBranchesThenFail(false)   // part of "ignore failure"
    .continuePipeline(false)                // part of "ignore failure"
    .build();
```
The `refId` and `requisiteRefIds` field names are unfortunate, so we could change them to `id` and `parentIds` and use `@JsonProperty` to control the way they are serialized:
```java
public class Stage {
    @JsonProperty("refId")
    private String id;
    
    @Builder.Default
    @JsonProperty("requisiteRefIds")
    private List<String> parentIds = List.of();

    // (same 3 booleans defined here)
}
```

Which gives us a better API for these two fields:
```java
Stage s1 = Stage.builder()
    .id("1")
    .build();
Stage s2 = Stage.builder()
    .id("2")
    .parentIds(List.of(s1.getId()))
    // (same 3 booleans set here)
    .build();
```

There are still multiple downsides to this approach:
1. We have to provide a _list_ of IDs when there's usually only one parent.
2. We need to first extract the ID from the parent stage before creating this list.
3. The fact that stages are linked together by their IDs is an implementation detail and a property of the serialization format: this is a _leaky abstraction_.
4. To select any of the "on failure" behaviors, we need to set 3 booleans.
5. Nothing prevents us from setting fewer than 3 of the failure booleans.
6. Nothing prevents us from setting 3 boolean values that do not correspond to any of the pre-defined behaviors (e.g. `true` × 3).
7. Nothing in the code that sets these 3 booleans would point a reader to the corresponding behavior, and it's hard to tell which behavior is being used without a comment.
8. Any internal field we might add to the class would be included in the builder API.

All of these taken together add up to a pretty bad experience using the builder API, but there is an alternative: instead of annotating the class, we can annotate the _constructor_. In this case, Lombok creates a builder API from the constructor parameters, which means that we have full control over the list of setter methods and their input types. This flexibility is a clear advantage, but it comes at a cost: we now need to set the fields ourselves using these parameters.

Let's consider our requirements and think of better ways to fulfill them:
1. Stages usually have one parent, can we link to a `Stage` _object_ instead of its internal ID?
2. Do we have to ask the user to generate IDs? Or can we generate them ourselves?
3. There is a limited number of failure modes that encode to these 3 booleans, can we use an enum?
4. It would be great if all of the downsides listed above could be avoided.

A `@Builder` annotation on the constructor lets us do all of that. Let's start with an enum for the failure mode:

```java
@Getter
public enum FailureMode {
    HALT_ENTIRE_PIPELINE(true, true, false),
    HALT_CURRENT_BRANCH(true, true, false),
    IGNORE_FAILURE(true, true, false),
    ;
    private final boolean failPipeline;
    private final boolean completeOtherBranchesThenFail;
    private final boolean continuePipeline;

    FailureMode(boolean failPipeline, boolean completeOtherBranchesThenFail, boolean continuePipeline) {
        this.failPipeline = failPipeline;
        this.completeOtherBranchesThenFail = completeOtherBranchesThenFail;
        this.continuePipeline = continuePipeline;
    }
}
```
Our three booleans are now represented by a more readable symbol.

We can then define our `Stage` class with constructor fields that correspond to the methods we want to include in the builder API:

```java
@Getter
@AllArgsConstructor
public class Stage {
    @JsonIgnore private final static AtomicInteger nextId = new AtomicInteger(1); // not serialized
    private final String refId;
    private final List<String> requisiteRefIds;
    private final boolean failPipeline;
    private final boolean completeOtherBranchesThenFail;
    private final boolean continuePipeline;

    @Builder
    public Stage(String id, Stage parent, String parentId, List<Stage> parents,
                 List<String> parentIds, FailureMode onFailure) {
        this.refId = id != null ? id : String.valueOf(nextId.getAndIncrement());
        this.requisiteRefIds = findRequisiteIds(parent, parentId, parents, parentIds);

        FailureMode failureMode = Objects.requireNonNullElse(onFailure, FailureMode.HALT_ENTIRE_PIPELINE);
        this.failPipeline = failureMode.isFailPipeline();
        this.completeOtherBranchesThenFail = failureMode.isCompleteOtherBranchesThenFail();
        this.continuePipeline = failureMode.isContinuePipeline();
    }

    private List<String> findRequisiteIds(final Stage parent, final String parentId,
                                          final List<Stage> parents, final List<String> parentIds) {
        if (parent != null) {
            return List.of(parent.getRefId());
        } else if (parentId != null) {
            return List.of(parentId);
        } else if (parents != null) {
            return parents.stream().map(Stage::getRefId).collect(Collectors.toList());
        } else if (parentIds != null) {
            return parentIds;
        }
        return null;
    }
}
```
We can now write:
```java
Stage s1 = Stage.builder()
    .build();
Stage s2 = Stage.builder()
    .parent(s1)
    .onFailure(FailureMode.IGNORE_FAILURE)
    .build();
```

Here's what this implementation provides:
1. `id` has a better name than `refId` and is optional.
2. The parent stage can be attached from a `Stage` object directly with `.parent(Stage)`.
3. The parent stage ID can be attached from a `String` with `.parentId(String)`.
4. Multiple parent stages can be attached from a list of `Stage` objects with `.parents(List<Stage>)`.
5. Multiple parent stage IDs can be attached from a `List<String>` with `.parentIds(List<String>)`.
6. The 3 booleans are replaced by a safer enum.
7. None of the downsides listed earlier still apply with this implementation.

### In pipeline-builder

The `pipeline-builder` library makes heavy use of the `@Builder` annotation applied on constructors. The aim is to provide a builder API that closely matches the level of abstraction at which the person writing this code is thinking, rather than simply exposing the internal fields and the structure of Spinnaker's JSON pipelines.

## Custom serialization with Jackson

In addition to Lombok builders, the library also relies on Jackson's `@JsonProperty` annotation to transform data when it is serialized rather than requiring developers to provide field values in a specific format. One example of this is the base-64 encoding of embedded assets: if we want to let users pass raw strings to the builder API, we need to encode the data that Spinnaker expects in the `reference` field within the artifact class itself. But if we encoded it in the constructor, a `.getContents()` would return encoded data despite the `.contents(String)` builder API having received a plain-text string. We use `contents` as a more appropriate name than `reference`, once again putting the emphasis on developer experience rather than naked access to internal Spinnaker data structures.

To support a plaintext `contents` field set with a builder and with a getter returning the same value as well as base-64 encoding during serialization, we annotate a getter method with `@JsonProperty`. Note how we also declare some similar methods for fields that have a constant value, without having to store these as actual fields in the class:

```java
public class EmbeddedArtifactDefinition {
    @JsonProperty @Getter private final String name;
    @JsonIgnore @Getter private final String contents;

    @Builder // this creates a builder with just these two fields
    public EmbeddedArtifactDefinition(String name, String contents) {
        this.name = name;
        this.contents = contents;
    }

    @JsonProperty("reference")
    public String getReference() {
        return Base64.getEncoder() .encodeToString(contents.getBytes(StandardCharsets.UTF_8));
    }

    @JsonProperty("type")
    public ArtifactType getType() {
        return ArtifactType.EMBEDDED_BASE64; // uses a custom serializer with @JsonValue
    }

    @JsonProperty("artifactAccount")
    public String getArtifactAccount() {
        return "embedded-artifact";
    }
}
```

# The pipeline-builder library

## Installing the library

### Gradle

```groovy
implementation(group: 'io.spinnaker', name: 'pipeline-builder', version: '0.1.+')
```

To create a separate source directory and dependency configuration for your Spinnaker pipelines in a single-project Gradle build, a new `configuration` can be added.
The below example places pipeline sources in `src/spinnaker/java` and specifies a Gradle task for executing a wrapper `spinnaker.Main` class for generating the pipelines from code.

```groovy
configurations {
    spinnaker
}

sourceSets {
    spinnaker {
        java {}
    }
}

dependencies {
    spinnakerImplementation(group: 'io.spinnaker', name: 'pipeline-builder', version: '0.1.+')
}

task generateSpinnakerPipeline(type: JavaExec) {
    classpath = sourceSets.spinnaker.runtimeClasspath
    main = 'spinnaker.Main'
}
```

### Maven

```xml
<dependency>
    <groupId>io.spinnaker</groupId>
    <artifactId>pipeline-builder</artifactId>
    <version>[0.1.0,0.2.0)</version>
</dependency>
```

## Design and advice

The design principles are described in depth in the [design document](DESIGN.md). Users of this library only need to follow the advice below, while those who want more details can read it to learn how it works.

### Always use the builders

In addition to defining classes to represent the various kinds of objects composing a Spinnaker pipeline, this library uses Lombok to provide [builder APIs](https://projectlombok.org/features/Builder) for these objects. You are _not_ expected to call the constructors since they declare parameters for every single field that can be set in the object, often with multiple options for each field. Instead, use the `.builder()` static method on the class you want to build and provide only the values you actually need.

### Singular and plural setters

Several builder classes provide both a singular and a plural setter method for the same field. This library provides both options to developers, to help them simplify their code and make it more readable. For example, stages can have multiple parents but most point to a single previous stage; similarly pipelines can have multiple triggers but many have only one. Having to pass a list of triggers with a single element adds unnecessary boilerplate, as this example shows:

```java
Pipeline pipeline = Pipeline.builder()
    .name("my test pipeline")
    .stages(List.of(stage1, stage2))
    .triggers(List.of(webhookTrigger))      // a list of triggers (plural) with just one trigger
    .build();
```

A `.trigger` method is available to handle this common use-case:

```java
Pipeline pipeline = Pipeline.builder()
    .name("my test pipeline")
    .stages(List.of(stage1, stage2))
    .trigger(webhookTrigger)      // one single trigger passed to the `.trigger` method (singular)
    .build();
```

Whenever you have a single value to set in a field that could allow multiple values, try to use the more readable singular alternative rather than building a list of one element.

### Not all fields are exposed individually

Spinnaker sometimes sets multiple values at once in the JSON document in response to a single change being made in the UI. For example, the various choices in the section titled "If stage fails" are represented by 3 separate boolean fields stored in the `Stage` object. Instead of exposing them individually with a setter method each, the library reflects the limited set of choices available in the UI by presenting these options as enum values; the booleans are still set internally but do not need to be specified manually and one by one.

## Building pipelines with the library

The top-level class representing a Spinnaker pipeline is `Pipeline`, with `JsonPipelineBuilder` being the class that generates a single Spinnaker pipeline; its stages use the `Stage` class. `Trigger` is an abstract class with `CronTrigger` and `WebhookTrigger` implementations. Notification classes all extend the abstract `Notification` class, with `EmailNotification` and `SlackNotification` both built-in.

All of these classes are meant to be instantiated via their respective builders; their constructors should never be called directly.

### Defining stages

Each `Stage` object can be defined using its builder API, and needs a few fields:

1. `.name(String)` sets the stage name.
2. The stage type is computed by `.typeClass(Class<? extends StageDefinitionBuilder>)` using the actual Orca class that implements this type of stage.
3. `.type(String)` sets the stage type using a `String`, instead of looking it up in the Orca class.
4. `.context(Map<String, Object>)` is for the stage inputs, added as a `Map`.
5. `.contextObject(Object)` also sets the stage context, using data that can be serialized as JSON object.
6. `.parentStage(Stage)` links to a single parent stage.
7. `.parentStages(List<Stage>)` links to a list of parent stages.
8. `.parentStageId(String)` links to a single parent stage using its ID (which can be accessed with `.getId()`).
9. `.parentStageIds(List<String>)` links to a list of parent stages using their IDs.
10. `.onFailure(FailureStrategy)` uses an enum to produce the 3 booleans that describe how the pipeline may continue executing if the stage fails.
11. `.failStageAfter(Duration)` sets the execution timeout.
12. `.conditionalOnExpression(String)` takes a SpEL expression to control the conditional execution of the stage.

These are the most important methods, but the builder provides even more control over all aspects of the stage.

### Stage IDs

By default, the stage builder leaves the ID as optional generates and a numeric ID using a static counter if none is provided. The stage IDs is accessible with a getter once the object is built.

### Stage context object

There are two ways to add the context data to a `Stage` builder. The simplest and most flexible uses `.context(Map<String, Object>)` to add any kind of values to the stage context, using strings for the field names:

```java
Stage pipelineStage = Stage.builder()
    .name("Run child pipeline")
    .type(StageTypes.PIPELINE)
    .context(Map.of("application", getApplication(),
        "pipeline", computePipelineIdForClass(ChildPipelineJsonBuilder.class),
        "pipelineParameters", Map.of("foo", "123"),
        "waitForCompletion", true))     // (take note of this context field)
    .build();
```

This approach has a few downsides: it requires the developer to enter all the field names correctly, the parameters might have low-level names that aren't very descriptive, and no type-checking is performed on the values. If we had named the last field `waitForPipelineCompletion` instead of `waitForCompletion`, no error would have been reported and we might only notice the mistake when the stage runs and doesn't behave as expected.

The `.contextObject(Object)` method provides a safer alternative for passing this data to the `Stage` builder. Internally, it will serialize its parameter to a JSON object and then use it as a `Map` from which the entries are added to the stage context. The library bundles pre-defined context classes for some common stage types such as "_Run Pipeline_" or "_Wait_". When instances of these classes are passed to `.contextObject`, they add type-checking at compile time, can validate their input values at runtime, and may have parameter names that can be descriptive instead of having to match exactly the Spinnaker serialization keys.

When context classes have more than one field, using a builder keeps the code more readable than a long constructor. Here is the same example as above, this time using a built-in stage context object instead of a `Map`:

```java
Stage pipelineStage = Stage.builder()
    .name("Run child pipeline")
    .type(StageTypes.PIPELINE)
    .contextObject(RunPipelineContext.builder()
        .application(getApplication())
        .pipelineId(computePipelineIdForClass(ChildPipelineJsonBuilder.class))
        .parameters(Map.of("foo", "123"))
        .waitForCompletion(true)
    .build();
```
In the example above, note the more descriptive name `pipelineId` instead of the generic `pipeline`, as well as the simpler `parameters` instead of the verbose `pipelineParameters`.

When context classes wrap a single field, they provide a static builder method named `.of<fieldname>(value)`, which also helps with readability:

```java
Stage waitStage = Stage.builder()
    .name("Wait 5 seconds")
    .type(StageTypes.WAIT)
    .contextObject(WaitContext.ofSeconds(5))
    .build();
```

### Building a Pipeline object

In a subclass of `JsonPipelineBuilder`, the `buildPipeline` method is expected to return a `Pipeline` object. Typically, `Stage` objects and other pipeline attributes such as parameters, triggers, and notifications will be created first using their own builder APIs, before creating and returning a `Pipeline` object that refers to them.
The `Pipeline` builder goes over its stages and validates that all of the parent IDs they depend on point to stages that _were_ included:

```java
Stage s1 = Stage.builder()
    .name("stage 1")
    .build();
Stage s2 = Stage.builder()
    .name("stage 2")
    .parentStage(s1)        // point to stage 1
    .build();
Pipeline p = Pipeline.builder()
    .name("my test pipeline")
    .stages(List.of(s2))    // missing stage 1! an exception is thrown.
    .build();
```
The code above will throw when it detects that `s2` points to its parent `s1` which is no included in the list of stages passed to the `Pipeline` builder.

Pipelines will sometimes need to refer to their Spinnaker app. The app name can be accessed from the `buildPipeline()` method using `String getApplication()`, provided that it was originally set using `JsonPipelineBuilder.setApplication(String)`.

### Pipeline IDs

When a pipeline is created in the UI, Spinnaker generates a new (random) unique pipeline ID. This is impractical for generated pipelines for two reasons. First, if we want to generate a new version for an existing pipeline already stored in Spinnaker we'll need to use the same ID to overwrite it. Second, pipelines that trigger other pipelines need to refer to them by ID so we can't generate new IDs for all the versions of these pipelines or the callers will also have to be updated.

For these reasons, the pipeline ID is generated deterministically based on two factors:

1. First, `JsonPipelineBuilder` has a `String getUniqueName()` method where each pipeline builder needs to return a unique string describing the pipeline.
2. Second, we need to have the ability to generate the same pipeline using the same class in two different Spinnaker apps without them conflicting on their IDs, so `JsonPipelineBuilder` also includes a `String getSalt()` method which provides a way to generate new IDs that does not only depend on the unique name. The salt value can be set with `setSalt(String)`.

The pipeline builder takes the unique name and the salt and generates a UUID for the pipeline using a deterministic algorithm with these two inputs.

If a parent pipeline needs to refer to a child pipeline in a "Run Pipeline" stage, it can compute the `pipeline` context field using the helper method `computePipelineIdForClass(Class<? extends JsonPipelineBuilder>)`:
```java
Stage runPipelineStage = Stage.builder()
    .name("Run child pipeline")
    .type(StageTypes.PIPELINE)
    .contextObject(RunPipelineContext.builder()
        .application(getApplication())    // same application
        .pipelineId(computePipelineIdForClass(ChildPipelineBuilder.class))  // computed ID of child pipeline
        .parameters(Map.of("foo", 123))
        .waitForCompletion(true)
    .build())
    .failStageAfter(Duration.of(10, ChronoUnit.MINUTES))
    .build();
```

### Notifications

Both email and Slack notifications are supported at this time. Notifications can be attached to stages or pipelines, and map an event to a String containing the message to send when this even occurs. Events are declared with the `NotificationEvent` enum, and describe cases like `STAGE_STARTING`, `STAGE_COMPLETE`, `PIPELINE_FAILED`, etc.

```java
EmailNotification emailNotification = EmailNotification.builder()
    .address("my-team-ci-results@example.com")
    .message(Map.of(
        NotificationEvent.PIPELINE_COMPLETE, Pipeline"An email sent when the pipeline succeeds",
        NotificationEvent.PIPELINE_FAILED, "An email sent when the pipeline fails"))
    .build();

SlackNotification slackNotification = SlackNotification.builder()
    .channel("my-team-ci-results") // the name of a Slack channel. Add a `#` prefix if this is a public channel.
    .message(Map.of(
        NotificationEvent.PIPELINE_COMPLETE, "A Slack message sent when the pipeline succeeds",
        NotificationEvent.PIPELINE_FAILED, "A Slack message sent when the pipeline fails"))
    .build();

Pipeline p = Pipeline.builder()
    .name("my test pipeline")
    ...
    .notifications(List.of(emailNotification, slackNotification))
    .build();
```

### Triggers

Both Webhook and CRON triggers are supported using the `WebhookTrigger` and `CronTrigger` classes, extending the `Trigger` abstract base class. They can be attached to a `Pipeline` using either `.trigger(Trigger)` or `.triggers(List<Trigger>)` (but not both):

```java
WebhookTrigger webhookTrigger = WebhookTrigger.builder()
    .enabled(true)
    .runAsUser("my-service-account")
    .source("my-endpoint") // reacts to requests sent to https://$SPINNAKER-GATE-HOST/webhooks/webhook/my-endpoint
    .payloadConstraints(Map.of(
        "json-field-1", "json-value-1",
        "json-field-2", "json-value-2"))
    .build();

CronTrigger cronTrigger = CronTrigger.builder()
    .enabled(true)
    .runAsUser("my-service-account")
    .cronExpression("0 0 9 1/1 * ? *") // every day at 9 AM
    .build();

Pipeline p = Pipeline.builder()
    .name("my test pipeline")
    .trigger(webhookTrigger) // for a single trigger
    .triggers(List.of(webhookTrigger, cronTrigger))// for multiple triggers (don't use both methods)
    .build();
```

### Parameters

Pipeline parameters are built with the `PipelineParameter` class, which comes with a straightforward builder API:
```java
PipelineParameter param = PipelineParameter.builder()
    .name("env")
    .pinned(false)
    .required(true)
    .options(List.of("Dev", "QA", "Prod"))
    .defaultValue("QA")
    .build();
```

Parameters can be added to the pipeline using either `.parameter(PipelineParameter)` or `.parameters(List<PipelineParameter>)`.


### Artifacts

Artifacts are one of most complex features of Spinnaker, and building pipelines that contain artifacts requires understanding their structure.

The library differentiates _artifacts_ from _artifact definitions_. An artifact definition describes a single object either stored in a remote location or defined in-line with the pipeline. This is actually what the [Spinnaker docs](https://spinnaker.io/reference/artifacts-with-artifactsrewrite/#the-artifact-format) call an _artifact_, with little consistency.

The `ArtifactDefinition` interface describes these objects, while its implementations provide builder APIs for the various ways that files can be accessed by Spinnaker:

1. `HelmArtifactDefinition` refers to a Helm chart.
2. `Base64ArtifactDefinition` refers to an inline file.
3. `CustomObjectArtifactDefinition` provides a generic way to build an artifact definition.

Here is an example building an artifact definition for a Helm chart:

```java
HelmArtifactDefinition helmChartDefinition = HelmArtifactDefinition.builder()
                .artifactAccount("my-helm-account")
                .name("nginx")
                .reference("helm-nginx")
                .version("0.2.4")
                .build()
```

While `ArtifactDefinition` points to an actual file, this is not how stages refer to Spinnaker artifacts. Instead, they use a data structure that _can_ contain artifact definitions, and try to _match_ these definitions to the actual artifacts passed in as input to the pipeline. This data structure is called `ExpectedArtifact` in the library, since objects of this class are the ones given to stages.

An `ExpectedArtifact` usually contains two artifact definitions: the one we try to match, and one to use by default if there's no match:

```java
ExpectedArtifact expectedArtifact = ExpectedArtifact.builder()
    .displayName("manifest.yml")
    .matchArtifact(HttpArtifactDefinition.builder() // the artifact we try to match
        .name("Manifest in Artifactory")
        .url("https://artifactory.company.com/my/project/manifest.yml")
        .artifactAccount("http-account")
        .build())
    .defaultArtifact(Base64ArtifactDefinition.builder() // the default if there's no match
        .name("inline-manifest.yml")
        .contents(getBase64EncodedYamlManifest())
        .build())
    .useDefaultArtifact(true)
    .usePriorArtifact(false)
    .build();
```

This fully-built object can then be passed to a `Stage` builder in the `.expectedArtifacts` method.

### Evaluate Variables

The "Evaluate Variables" stage uses a single `variables` field to store all of its variable definitions, each one having a `key` field for its name and a `value` field for the corresponding expression.
Defining those manually requires using verbose nested maps, and exposes these field names to the developer building a pipeline:

```java
Stage evalVariablesStage = Stage.builder()
    .name("Evaluate variables after webhook")
    .type(StageTypes.EVALUATE_VARIABLES)
    .failOnFailedExpressions(true)
    .parentStage(webhookStage)
    .context(Map.of("variables", List.of(
        Map.of("key", "statusCode", "value", "${ #stage('Webhook').context.webhook.statusCodeValue }"),
        Map.of("key", "freezeState", "value", "${ #stage('Webhook').context.webhook.body.frozen }"))))
    .build();
```
Instead of these inner `Map.of` calls, the `EvaluateVariable` class provides a wrapper without exposing the `key` and `value` field names:
```java
    .contextObject(EvalVarsContext.ofVariables(List.of(
        new EvaluateVariable("statusCode", "${ #stage('Webhook').context.webhook.statusCodeValue }"),
        new EvaluateVariable("freezeState", "${ #stage('Webhook').context.webhook.body.frozen }"))))
```

### Building a Pipeline and serializing it as JSON

To create a pipeline, start with a new class that extends `JsonPipelineBuilder`. Create all your stages, parameters, artifact definitions, triggers and notifications in the `buildPipeline` method, and use the `Pipeline` class' builder API to generate a `Pipeline` object. As mentioned above, you can also set the application name to avoid ID collisions:

```java
public class MyCustomPipelineBuilder extends PipelineBuilder {
    @Override
    public String getUniqueName() {
        return "my-pipeline";
    }

    @Override
    protected Pipeline buildPipeline() {
        Stage stage1 = Stage.builder()
            // ...
            .build();
        // more stages, parameters, triggers, etc.
        return Pipeline.builder()
            .name("My new pipeline")
            // ...
            .stages(List.of(stage1, stage2 ...))
            .parameters(...)
            .trigger(...)
            .build();
    }
}

PipelineBuilder pipelineBuilder = new MyCustomPipelineBuilder();
pipelineBuilder.setApplication("my-test-app");

Pipeline pipeline = pipelineBuilder.build();   // builds the actual Pipeline object
System.out.println(pipeline.toJson());         // serializes the object
```

### Execution time restrictions

Spinnaker now has the ability to add time-based restrictions that configure when each stage can run, with first a list of days of the week and then time ranges that apply to all of these days. It is also possible to bypass these restriction at execution time, and to introduce some jitter around the boundaries of the time ranges, to avoid a sudden load increase when many stages are suddenly unblocked.
The library provides an API that reflects the actions taken by the form's UI elements, in order to let developers port their stages as easily as possible. Even though Spinnaker adds multiple fields to the `Stage` object when these settings are serialized, the API keeps all of its time configuration in a single `ExecutionWindow` object attached with `.restrictExecutionTime()`:

```java
Stage waitStage = Stage.builder()
    .name("Stage 1: Wait")
    .type(StageTypes.WAIT)
    .contextObject(WaitContext.ofSeconds(30))
    .restrictExecutionTime(ExecutionWindow.builder()    // all fields set with a single method
        .days(ExecutionDays.WEEKDAYS)   // helper class to mimic the UI choices
        .addRandomJitter(RandomJitter.ofMinMaxSeconds(10, 60)) // static "of" builder for readability
        .skipJitterWhenManual(true)
        .withWarningWhenSkipped("skipping execution time window!")
        .timesOfDay(List.of(
            TimeRange.builder().startHour(10).startMin(30).endHour(14).endMin(45).build(),
            TimeRange.builder().startHour(18).startMin(0).endHour(20).endMin(15).build()))
        .build())
    .build();
```

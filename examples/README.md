# The pipeline-builder library, introduction and examples

To install the pipeline-builder library, follow the instructions in the [README](../README.md)
at the root of the repository.

In addition to this document, please also refer to the [DESIGN](../pipeline-builder/DESIGN.md) page,
which covers the design considerations and choices that went into the library.

## Getting started: creating your first pipeline

The pipeline-builder library is designed to be used in a fluent style, with a builder for each
stage of the pipeline and for the pipeline itself. Many of the classes it provides come with a
`builder()` method that returns a builder for that class; this is the recommended way to create
new instances of these classes. When using a builder, you can chain calls to its methods and
arrange these calls in any order you like. The main goal is to optimize for readability.

For the pipeline itself, we generally need a class extending the `JsonPipelineBuilder` abstract
class. This class provides some helper methods that structure the process of creating a pipeline.
One of these methods is `build()`, returning a `Pipeline` object. This object can then be serialized
to JSON and used in Spinnaker.

Let's start with a simple pipeline that takes two parameters `a` and `b`, starts a "wait" stage,
and ends with an "Evaluate Variables" stage computing `a + b`. We'll create a class
for our builder extending `JsonPipelineBuilder`, with stubs for the methods that we need to
implement:

```java
public class TutorialPipelineBuilder extends JsonPipelineBuilder {

    @Override
    public String getUniqueName() {

    }

    @Override
    protected Pipeline buildPipeline() {

    }
}
```

`getUniqueName` needs to return a unique string identifying this pipeline. Note that this is _not_
the pipeline name, but an internal ID that will contribute in the generation of the pipeline UUID
used to store it in the backend. You can use a descriptive string here; one way to organize these
identifiers is to keep track of them using an enum.

For this example, we can just use `"examples.tutorial"`.

`buildPipeline` is where we'll actually build the pipeline. This is where we'll use the fluent API
to create the pipeline stages, their inputs, and the pipeline itself including triggers, notifications,
and more. Most of these concepts are implemented by dedicated classes that come with builders.

### First stage

Let's start by declaring a single Stage object, using the `Stage` builder. We'll use the `wait`
stage type, and set its name to `"Wait a moment"`. We'll also need to give it a duration to wait
for, in seconds. All inputs to stages go into the stage **context**, which is a map of string keys
to arbitrary objects. In our case, the single key we need is `"waitTime"`:

```java
Stage waitStage = Stage.builder()
    .type(StageTypes.WAIT)
    .name("Wait a moment")
    .context(Map.of("waitTime", 5))
    .build();
```

### Second stage

Our second stage is an "Evaluate Variables" stage, creating a variable named `sum` with the value
computed from our parameters `a` and `b`. As above, we'll use the `Stage` builder and find its type
in the `StageTypes` class. We'll set its name to `"Compute sum"`, and give it a context with the
expression to evaluate and the variable name. Note that this kind of stage can evaluate multiple
variables, so we'll need to provide a list of variable declarations.

We'll also need to declare that it comes _after_ `waitStage`, by referencing `waitStage` as its
parent.

```java
Stage evalSumStage = Stage.builder()
    .type(StageTypes.EVALUATE_VARIABLES)
    .name("Evaluate sum")
    .parentStage(waitStage)
    .context(Map.of("variables", List.of(
        Map.of(
            "key", "sum",
            "value", "${ #toInt(parameters.a) + #toInt(parameters.b) }"))))
    .build();
```

If you are unsure of the structure to use for the `context` map, create the same type of stage in
the Spinnaker UI and click on "Edit stage as JSON" to view its structured representation. The entire
object you see in the JSON editor is the `context` map, although for this library we only use it
to declare attributes that are not part of the builder API.

### Returning the pipeline

We can now create the pipeline itself. We'll use the `Pipeline` builder, and set its name to
`"Tutorial"`. We need to declare the two parameters, give it the two stages, and call `.build()`
to get the full pipeline object.

Putting this all together, this is what our class should look like:

```java
public class TutorialPipelineBuilder extends JsonPipelineBuilder {

    @Override
    public String getUniqueName() {
        return "examples.wait-pipeline";
    }

    @Override
    protected Pipeline buildPipeline() {
            Stage waitStage = Stage.builder()
                .type(StageTypes.WAIT)
                .name("Wait a moment")
                .context(Map.of("waitTime", 5))
                .build();

            Stage evalSumStage = Stage.builder()
                .type(StageTypes.EVALUATE_VARIABLES)
                .name("Evaluate sum")
                .parentStage(waitStage)
                .context(Map.of("variables", List.of(
                    Map.of(
                        "key", "sum",
                        "value", "${ #toInt(parameters.a) + #toInt(parameters.b) }"))))
                .build();

            return Pipeline.builder()
                .parameters(List.of(
                    PipelineParameter.builder()
                        .name("a")
                        .defaultValue("17")
                        .build(),
                    PipelineParameter.builder()
                        .name("b")
                        .defaultValue("25")
                        .build()))
                .name("Tutorial")
                .stages(List.of(waitStage, evalSumStage))
                .build();
    }
}
```

### Generating the pipeline using our builder

To actually generate a pipeline object, we need to instantiate this class, set the application name,
and call `build()`:

```java
TutorialPipelineBuilder builder = new TutorialPipelineBuilder();
builder.setApplication("my-application");
Pipeline pipeline = builder.build();
String asJson = pipeline.toJson();
```

That's it! The contents of this string can be sent to Spinnaker to create a pipeline. This is what
`asJson` contains:

```json
{
  "application" : "my-application",
  "id" : "1049ea1a-874a-3d4b-8b29-e53d814df48f",
  "keepWaitingPipelines" : false,
  "limitConcurrent" : true,
  "name" : "Tutorial",
  "parameterConfig" : [ {
    "default" : "17",
    "name" : "a"
  }, {
    "default" : "25",
    "name" : "b"
  } ],
  "stages" : [ {
    "completeOtherBranchesThenFail" : false,
    "continuePipeline" : false,
    "failPipeline" : true,
    "name" : "Wait a moment",
    "refId" : "1",
    "requisiteStageRefIds" : [ ],
    "type" : "wait",
    "waitTime" : 5
  }, {
    "completeOtherBranchesThenFail" : false,
    "continuePipeline" : false,
    "failPipeline" : true,
    "name" : "Evaluate sum",
    "refId" : "2",
    "requisiteStageRefIds" : [ "1" ],
    "type" : "evaluateVariables",
    "variables" : [ {
      "key" : "sum",
      "value" : "${ #toInt(parameters.a) + #toInt(parameters.b) }"
    } ]
  } ]
}
```

Let's take note of some of the fields in this JSON document, and how they relate to the builder API.

### Stage IDs

You'll notice that the library generated `refId` values for the two stages, using `"1"` and `"2"`.
This identifier is used to structure the order of stages in a pipeline, and this is why the second
stage has `"requisiteStageRefIds"` set to `["1"]`. This means that the second stage will only run
after the first stage has completed.

You do not need to create these IDs yourself, but you can if you want to, by calling the `.id`
method on the `Stage` builder. To link the two stages, we had used the `parentStage` method on the
second stage, which takes a fully-built `Stage` object. We could also have used the `parentStageId`
method, which takes a string ID instead. Since we have a string identifier for the first stage as
soon as it is built, we could have used this to link the two instead:

```java
Stage evalSumStage = Stage.builder()
    .type(StageTypes.EVALUATE_VARIABLES)
    .name("Evaluate sum")
    .parentStageId(waitStage.getRefId())
    // ...
```

### Singular and plural methods, alternative methods

Notice also how we used the `parentStage` method above and `parentStageId` method here, both having
"singular" names. We are providing a single stage or single stage ID here, but if we wanted a stage
to have multiple parents we could use `parentStages` or `parentStageIds` instead. Many classes in
this library have both singular and plural methods or different ways to provide the same values like
here via a `Stage` object or using a string. You can use whichever one makes your code more
readable. The goal here is to avoid having overly verbose code like:

```java
.parentStageIds(
    Stream.of(stage1, stage2, stage3)
        .map(Stage::getId)
        .collect(Collectors.toList()))
```

We used `.stages()` on the `Pipeline` builder, but if we had a single stage in the pipeline we could
have used `.stage(waitStage)` for example. The same goes for parameters, notifications, and more.

## Context objects

In both stages, we used a `Map<String, Object>` value as the context. While this type is flexible
and does not require a class to structure these values, it is not very type-safe. If we make a
mistake in one of the keys for example, we would only find out when the pipeline was executed.

To avoid this we can use a context object, with the `ContextObject` interface. Classes that
implement this interface describe the structure of the context for a specific stage type, and can
use strict types for the values. An IDE will also show you which methods are available on such a
type, avoiding the need to look up (or even guess) the key names.

The library provides a few `ContextObject` classes for some common stage types, and both the wait
stage and evaluate variables stage have one. Let's use these instead and see what the code looks
like when we use them instead of maps.

To use context objects, we need to call `.contextObject()` instead of `.context()`, which is the
generic method taking a `Map`.

```java
Stage waitStage = Stage.builder()
    .type(StageTypes.WAIT)
    .name("Wait a moment")
    .contextObject(WaitContext.ofSeconds(5))
    .build();

Stage evalSumStage = Stage.builder()
    .type(StageTypes.EVALUATE_VARIABLES)
    .name("Evaluate sum")
    .parentStage(waitStage)
    .contextObject(EvalVarsContext.ofVariables(List.of(
        new EvaluateVariable("sum", "${ #toInt(parameters.a) + #toInt(parameters.b) }"))))
    .build();
```

With this approach, there is no way to provide a context object that has the wrong keys, or values
of the wrong type. An IDE will also show you the available methods on the context object, and help
you build it.

## Pipeline IDs

In our generated pipeline above, you'll notice that the `id` field was set to a UUID. While UUIDs
look random, there is actually some logic involved in generating this value. In the Spinnaker UI and
in the Spinnaker API, pipelines are identified by this ID alone and not by the combination of the
application name and the pipeline name or ID. This means that if you create a pipeline with the same
unique ID, it will overwrite the existing pipeline. This is useful if you want to update a pipeline
on the Spinnaker side after having updated the builder code for it.

Since this ID needs to be stable to provide this functionality, the library uses a hash of the
value we returned from `getUniqueName()`, combined with the application name. Adding the application
name lets you install the same generated pipeline in multiple applications without having to worry
about ID collisions and accidental overwrites.

You can therefore think of the pipeline ID as:
```java
String pipelineId = generateUUID(hash(combine(applicationName, getUniqueName()));
```

In some cases, you might also want an extra layer of indirection. This is optional, but you can
provide a "salt" to the `JsonPipelineBuilder` object which will be combined with the application
name and the unique name to generate the ID. This lets us create two pipelines with the same
contents within the same application, as long as we take care to give them different names.

In this case, the ID is generated with:

```java
String pipelineId = generateUUID(hash(combine(applicationName, salt, getUniqueName()));
```

### Referencing IDs in the "Run Pipeline" stage

Some pipelines trigger other pipelines explicitly, using the "Run Pipeline" stage. While the
Spinnaker UI lets you select the pipeline to run from a dropdown, the internal representation
requires the ID of the pipeline to run. This means that if you want to trigger a pipeline from
another, you will need provide its ID.

The library lets you compute this ID using only the builder class for the target pipeline, using
the `computePipelineIdForClass()` method.

Here's an example with a pipeline that triggers the pipeline we built above:

```java
public class ParentPipelineBuilder extends JsonPipelineBuilder {
    @Override
    public String getUniqueName() {
        return "examples.parent";
    }

    @Override
    protected Pipeline buildPipeline() {
        Stage runPipelineStage = Stage.builder()
            .type(StageTypes.PIPELINE)
            .name("Run child pipeline")
            .contextObject(RunPipelineContext.builder()
                .application(getApplication())
                .pipelineId(computePipelineIdForClass(TutorialPipelineBuilder.class))
                .build())
            .build();

        return Pipeline.builder()
            .name("Parent")
            .stage(runPipelineStage)
            .build();
    }
}
```

Let's generate this pipeline, and look at the context it generated for its "Run Pipeline" stage:

```json
{
  "application" : "my-application",
  "id" : "8e2ef73d-6518-39e9-9b03-19a490bf04ec",
  "keepWaitingPipelines" : false,
  "limitConcurrent" : true,
  "name" : "Parent",
  "stages" : [ {
    "application" : "my-application",
    "completeOtherBranchesThenFail" : false,
    "continuePipeline" : false,
    "failPipeline" : true,
    "name" : "Run child pipeline",
    "pipeline" : "1049ea1a-874a-3d4b-8b29-e53d814df48f",
    "refId" : "1",
    "requisiteStageRefIds" : [ ],
    "type" : "pipeline",
    "waitForCompletion" : true
  } ]
}
```

As we can see, the ID referenced in the stage context is `1049ea1a-874a-3d4b-8b29-e53d814df48f`,
which is indeed the same as the ID for the pipeline we generated above. If we had changed the
application or added a salt to either pipeline builder, it would have been different.

Note also the call to `getApplication()`. This is a method provided by the `JsonPipelineBuilder`,
which returns the application name that was used to generate the pipeline. This is useful if you
want to reference the application name in the pipeline, for example in a notification or by having
some behavior that depends on the application name.

## Notifications

Just like the other classes used above, notifications also supported using a fluent API. Several
classes extend the `Notification` abstract class, each with its own builder.

Notifications can be attached to a pipeline, or to a stage. We can use the same classes for both,
installing them using the `NotificationEvent` to define _when_ they should take place (e.g. at the
start or end of a pipeline or stage).

Here's an example of a pipeline with one notification when a stage starts, and another when the
pipeline ends.

```java
public class NotificationsPipelineBuilder extends JsonPipelineBuilder {

    @Override
    public String getUniqueName() {
        return "examples.notifications";
    }

    @Override
    protected Pipeline buildPipeline() {
        Stage waitStage = Stage.builder()
            .type(StageTypes.WAIT)
            .name("Wait")
            .contextObject(WaitContext.ofSeconds(5))
            .notifications(List.of(
                SlackNotification.builder()
                    .message(Map.of(NotificationEvent.STAGE_STARTING, "Starting wait stage"))
                    .channel("#spinnaker-notifications")
                    .build()))
            .build();

        return Pipeline.builder()
            .name("Notifications example")
            .stages(List.of(waitStage))
            .notifications(List.of(
                EmailNotification.builder()
                    .message(Map.of(
                        NotificationEvent.PIPELINE_COMPLETE, "Pipeline ended successfully",
                        NotificationEvent.PIPELINE_FAILED, "Pipeline ended with a failure"))
                    .address("spinnaker-notifications@example.com")
                    .build()))
            .build();
    }
}
```

## Pipeline triggers

Similarly to notifications, pipeline triggers of various types can be represented by classes that
extend the `Trigger` abstract class, each with its own builder and fluent API.

Here is an example of a pipeline that is triggered by a cron expression:

```java
public class CronTriggerPipelineBuilder extends JsonPipelineBuilder {

    @Override
    public String getUniqueName() {
        return "examples.crontrigger";
    }

    @Override
    protected Pipeline buildPipeline() {
        Stage evalVarsStage = Stage.builder()
            .type(StageTypes.EVALUATE_VARIABLES)
            .name("Show pipeline trigger")
            .contextObject(EvalVarsContext.ofVariables(List.of(
                new EvaluateVariable("pipelineTrigger", "${ execution.trigger }"))))
            .build();

        return Pipeline.builder()
            .name("Cron trigger example")
            .stages(List.of(evalVarsStage))
            .trigger(CronTrigger.builder()
                .cronExpression("0 0 0/1 1/1 * ? *")
                .enabled(true)
                .runAsUser("us-west-service-account")
                .build())
            .build();
    }
}
```

As mentioned above, note how we use `.trigger()` on the `Pipeline` builder when there's a single
trigger, instead of having to always pass in a list even if it only contains one element.

## Failure strategies

Spinnaker represents the various options for handling failures in a stage as a set of booleans,
which can be confusing. The library provides the `FailureStrategy` helper class to make this easier.
With a `Stage` builder, call `.onFailure()` and give it a `FailureStrategy` enum value to set the
corresponding booleans.

Here is a `Stage` builder that will stop the current branch and later fail the pipeline:

```java
Stage.builder()
    .type(StageTypes.WAIT)
    .name("Wait")
    .contextObject(WaitContext.ofSeconds(5))
    .onFailure(FailureStrategy.HALT_BRANCH_AND_FAIL_PIPELINE)
    .build();
```

Compare the above to this equivalent snippet where we set each boolean individually:

```java
Stage.builder()
    .type(StageTypes.WAIT)
    .name("Wait")
    .contextObject(WaitContext.ofSeconds(5))
    .failPipeline(false)
    .completeOtherBranchesThenFail(true)
    .continuePipeline(false)
    .build();
```

## Artifacts

Artifacts are one of the most complex parts of Spinnaker, so the library tries to make their use
as simple as possible by providing default values wherever possible.
As for other concepts, several interfaces and abstract classes are provided with fluent APIs to
build the various types of artifacts.

Artifacts are identified by a stable ID, which is a string that uniquely identifies the object
among others in the pipeline. Similarly to the way we computed stable pipeline IDs above, we can
compute artifact IDs using the `computeStableIdForArtifact()` method. This method takes a string
as its single parameter. This is not the artifact's name, but rather an internal identifier that can
be reused to compute the same ID to refer to the same artifact.

You can think of it as:
```java
String artifactId = generateUUID(hash(internalIdentifierForArtifact));
```

In the example below, we create an `ExpectedArtifact` that uses a base64-encoded artifact
definition, and reference it from a "Find Artifacts From Execution" stage. The actual contents of
the artifact are read from a file in the JAR's resources.

```java
public class ArtifactsPipelineBuilder extends JsonPipelineBuilder {

    @Override
    public String getUniqueName() {
        return "examples.artifacts";
    }

    @Override
    protected Pipeline buildPipeline() {
        ExpectedArtifact inlineArtifact =
            ExpectedArtifact.builder()
                .id(computeStableIdForArtifact("kubernetes-manifest")) // stable ID
                .displayName("manifest.yml")    // visible in the UI
                .useDefaultArtifact(true)       // will use the base-64 definition we provide
                .usePriorArtifact(false)
                .defaultArtifact(Base64ArtifactDefinition.builder()
                    .name("manifest.yml")
                    .id(UUID.randomUUID().toString())
                    .contents(getResourceContents(getClass().getClassLoader(), // read the file contents from the JAR's resources
                        "embeddedd-resources/manifest.yml"))
                    .build())
                .matchArtifact(Base64ArtifactDefinition.builder() // the artifact we try to match: not something that actually exists
                    .name("no-such-artifact")
                    .build())
                .build();

        Stage declareArtifactStage = Stage.builder()
            .type(StageTypes.FIND_ARTIFACT_FROM_EXECUTION)
            .name("Declare artifact containing a manifest")
            .context(Map.of("application", getApplication(),
                "pipeline", computePipelineIdForClass(this.getClass()), // self-reference
                "executionOptions", Map.of("successful", true)))
            .expectedArtifact(inlineArtifact) // expected artifact is attached here
            .build();

        return Pipeline.builder()
            .name("Artifacts demo")
            .stage(declareArtifactStage)
            .build();
    }
}
```

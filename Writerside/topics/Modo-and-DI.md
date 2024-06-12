# Modo and Dependency Injection (DI)

You can safely inject `Screen` into your DI container, but it must be removed from DI as soon as the `Screen` is removed from the hierarchy. Built-in
screen effects can be used for this purpose.

Your DI scope can be identified by `screenKey`. Here is a sample of how you can use it with <include from="snippets.topic" element-id="toothpick"/>:

```kotlin
```

{ src="ToothpickIntegrationSample.kt" }

<include from="snippets.topic" element-id="under_develop_note"/>
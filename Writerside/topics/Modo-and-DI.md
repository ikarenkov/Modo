# Modo and DI

You can safely inject `Screen` into your DI container, but it must be removed from DI as soon as `Screen` is removed from hierarchy. You can use
build-in screen effects for this purpose.

Your DI scope can be identified by `screenKey`. Here is a sample of how you can use it
with <include from="snippets.topic" element-id="toothpick"/>:

```Kotlin
```

{ src="ToothpickIntegrationSample.kt" }

<include from="snippets.topic" element-id="under_develop_note"/>
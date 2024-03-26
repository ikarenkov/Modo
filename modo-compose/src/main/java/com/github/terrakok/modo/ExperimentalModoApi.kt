package com.github.terrakok.modo

@RequiresOptIn(message = "This is an experimental Modo API.")
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY_GETTER,
)
annotation class ExperimentalModoApi
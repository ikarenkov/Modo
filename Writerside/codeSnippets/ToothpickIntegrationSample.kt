@Composable
internal fun Screen.rememberScreenScope(
    parentScope: Scope = LocalToothpickScope.current,
    moduleProvider: () -> Module = { module { } },
): Scope {
    val scope = remember {
        getOrInitScope(screenKey, parentScope, moduleProvider)
    }
    OnScreenRemoved("scope") {
        Toothpick.closeScope(scope.name)
    }
    return scope
}

private fun getOrInitScope(
    screenKey: ScreenKey,
    parentScope: Scope,
    moduleProvider: () -> Module = { module { } },
): Scope {
    val scopeName = screenKey.scopeName()
    return if (Toothpick.isScopeOpen(scopeName)) {
        Toothpick.openScope(scopeName)
    } else {
        val scope = Toothpick.openScopes(parentScope.name, scopeName)
        scope.installModules(moduleProvider.invoke())
    }
}
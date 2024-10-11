# Lifecycle testing

When you do any changes in the internal logic, that can affect the lifecycle of the screen, you should manually test correctness of the lifecycle in
certain cases:

* Simple forward, back, replace commands
* Adding and removing container screens with nested screens
    * Adding container screen with 1 screen and removing it
    * Adding container screen with multiple screens and removing it
    * Check same in `LazyList`
* Check correctness, with animation interruption. F.e. 2 fast back clicks
    * Check pause, stop. dispose - 2 fast back or replace
    * Check create, start, resume - 2 fast forward or replace
* Movable content test
* Check lifecycle order in nested screens when activity/fragment recreated, by rotating the screen. Following rules should be applied:
    * Parent screens events ON_CREATE, ON_START, ON_RESUME should be called before child screens events
    * Parent screens events ON_PAUSE, ON_STOP, ON_DESTROY should be called after child screens events

## Test cases

### Removing previous screen

1. Use `LifecycleScreenEffect` to subscribe to lifecycle updates in `ButtonsScreenContent` (remove comments from code).
2. Launch app and navigate to "Stack Actions".
3. Click "Remove previous".
4. "LifecycleScreenEffect ON_DESTROY" in logcat for previous screen.
package tests.auto_bind.into_set_with_qualifier_2.lifecycle

public abstract class AppLifecycleTask() {
    public enum class LifecycleStep {
        AppForegrounded,
        AppBackgrounded,
    }
}
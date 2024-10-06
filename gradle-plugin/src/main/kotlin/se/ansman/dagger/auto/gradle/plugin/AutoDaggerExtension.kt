package se.ansman.dagger.auto.gradle.plugin

import org.gradle.api.provider.Property

public abstract class AutoDaggerExtension {
    public abstract val enableLogging: Property<Boolean>
}
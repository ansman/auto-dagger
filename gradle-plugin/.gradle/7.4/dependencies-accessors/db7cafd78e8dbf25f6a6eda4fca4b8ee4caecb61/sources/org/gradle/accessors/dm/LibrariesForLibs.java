package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the `libs` extension.
*/
@NonNullApi
public class LibrariesForLibs extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final AndroidLibraryAccessors laccForAndroidLibraryAccessors = new AndroidLibraryAccessors(owner);
    private final AndroidxLibraryAccessors laccForAndroidxLibraryAccessors = new AndroidxLibraryAccessors(owner);
    private final AutoLibraryAccessors laccForAutoLibraryAccessors = new AutoLibraryAccessors(owner);
    private final CompileTestingLibraryAccessors laccForCompileTestingLibraryAccessors = new CompileTestingLibraryAccessors(owner);
    private final DaggerLibraryAccessors laccForDaggerLibraryAccessors = new DaggerLibraryAccessors(owner);
    private final DokkaLibraryAccessors laccForDokkaLibraryAccessors = new DokkaLibraryAccessors(owner);
    private final IncapLibraryAccessors laccForIncapLibraryAccessors = new IncapLibraryAccessors(owner);
    private final KotlinLibraryAccessors laccForKotlinLibraryAccessors = new KotlinLibraryAccessors(owner);
    private final KspLibraryAccessors laccForKspLibraryAccessors = new KspLibraryAccessors(owner);
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(providers, config);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers) {
        super(config, providers);
    }

        /**
         * Creates a dependency provider for javapoet (com.squareup:javapoet)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJavapoet() { return create("javapoet"); }

        /**
         * Creates a dependency provider for juniper (org.junit.jupiter:junit-jupiter)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJuniper() { return create("juniper"); }

        /**
         * Creates a dependency provider for junit (junit:junit)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJunit() { return create("junit"); }

        /**
         * Creates a dependency provider for okio (com.squareup.okio:okio)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getOkio() { return create("okio"); }

        /**
         * Creates a dependency provider for robolectric (org.robolectric:robolectric)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getRobolectric() { return create("robolectric"); }

        /**
         * Creates a dependency provider for testLogger (com.adarshr:gradle-test-logger-plugin)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getTestLogger() { return create("testLogger"); }

        /**
         * Creates a dependency provider for truth (com.google.truth:truth)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getTruth() { return create("truth"); }

    /**
     * Returns the group of libraries at android
     */
    public AndroidLibraryAccessors getAndroid() { return laccForAndroidLibraryAccessors; }

    /**
     * Returns the group of libraries at androidx
     */
    public AndroidxLibraryAccessors getAndroidx() { return laccForAndroidxLibraryAccessors; }

    /**
     * Returns the group of libraries at auto
     */
    public AutoLibraryAccessors getAuto() { return laccForAutoLibraryAccessors; }

    /**
     * Returns the group of libraries at compileTesting
     */
    public CompileTestingLibraryAccessors getCompileTesting() { return laccForCompileTestingLibraryAccessors; }

    /**
     * Returns the group of libraries at dagger
     */
    public DaggerLibraryAccessors getDagger() { return laccForDaggerLibraryAccessors; }

    /**
     * Returns the group of libraries at dokka
     */
    public DokkaLibraryAccessors getDokka() { return laccForDokkaLibraryAccessors; }

    /**
     * Returns the group of libraries at incap
     */
    public IncapLibraryAccessors getIncap() { return laccForIncapLibraryAccessors; }

    /**
     * Returns the group of libraries at kotlin
     */
    public KotlinLibraryAccessors getKotlin() { return laccForKotlinLibraryAccessors; }

    /**
     * Returns the group of libraries at ksp
     */
    public KspLibraryAccessors getKsp() { return laccForKspLibraryAccessors; }

    /**
     * Returns the group of versions at versions
     */
    public VersionAccessors getVersions() { return vaccForVersionAccessors; }

    /**
     * Returns the group of bundles at bundles
     */
    public BundleAccessors getBundles() { return baccForBundleAccessors; }

    /**
     * Returns the group of plugins at plugins
     */
    public PluginAccessors getPlugins() { return paccForPluginAccessors; }

    public static class AndroidLibraryAccessors extends SubDependencyFactory {

        public AndroidLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for gradlePlugin (com.android.tools.build:gradle)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getGradlePlugin() { return create("android.gradlePlugin"); }

    }

    public static class AndroidxLibraryAccessors extends SubDependencyFactory {
        private final AndroidxTestLibraryAccessors laccForAndroidxTestLibraryAccessors = new AndroidxTestLibraryAccessors(owner);

        public AndroidxLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for startup (androidx.startup:startup-runtime)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getStartup() { return create("androidx.startup"); }

        /**
         * Returns the group of libraries at androidx.test
         */
        public AndroidxTestLibraryAccessors getTest() { return laccForAndroidxTestLibraryAccessors; }

    }

    public static class AndroidxTestLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {

        public AndroidxTestLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for test (androidx.test:core-ktx)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> asProvider() { return create("androidx.test"); }

            /**
             * Creates a dependency provider for rules (androidx.test:rules)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getRules() { return create("androidx.test.rules"); }

            /**
             * Creates a dependency provider for runner (androidx.test:runner)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getRunner() { return create("androidx.test.runner"); }

    }

    public static class AutoLibraryAccessors extends SubDependencyFactory {
        private final AutoServiceLibraryAccessors laccForAutoServiceLibraryAccessors = new AutoServiceLibraryAccessors(owner);

        public AutoLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for common (com.google.auto:auto-common)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCommon() { return create("auto.common"); }

        /**
         * Returns the group of libraries at auto.service
         */
        public AutoServiceLibraryAccessors getService() { return laccForAutoServiceLibraryAccessors; }

    }

    public static class AutoServiceLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {

        public AutoServiceLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for service (com.google.auto.service:auto-service)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> asProvider() { return create("auto.service"); }

            /**
             * Creates a dependency provider for annotations (com.google.auto.service:auto-service-annotations)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getAnnotations() { return create("auto.service.annotations"); }

    }

    public static class CompileTestingLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {

        public CompileTestingLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for compileTesting (com.github.tschuchortdev:kotlin-compile-testing)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> asProvider() { return create("compileTesting"); }

            /**
             * Creates a dependency provider for ksp (com.github.tschuchortdev:kotlin-compile-testing-ksp)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getKsp() { return create("compileTesting.ksp"); }

    }

    public static class DaggerLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {
        private final DaggerHiltLibraryAccessors laccForDaggerHiltLibraryAccessors = new DaggerHiltLibraryAccessors(owner);

        public DaggerLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for dagger (com.google.dagger:dagger)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> asProvider() { return create("dagger"); }

            /**
             * Creates a dependency provider for compiler (com.google.dagger:dagger-compiler)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCompiler() { return create("dagger.compiler"); }

        /**
         * Returns the group of libraries at dagger.hilt
         */
        public DaggerHiltLibraryAccessors getHilt() { return laccForDaggerHiltLibraryAccessors; }

    }

    public static class DaggerHiltLibraryAccessors extends SubDependencyFactory {
        private final DaggerHiltAndroidLibraryAccessors laccForDaggerHiltAndroidLibraryAccessors = new DaggerHiltAndroidLibraryAccessors(owner);

        public DaggerHiltLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for compiler (com.google.dagger:hilt-compiler)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCompiler() { return create("dagger.hilt.compiler"); }

            /**
             * Creates a dependency provider for core (com.google.dagger:hilt-core)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCore() { return create("dagger.hilt.core"); }

            /**
             * Creates a dependency provider for gradlePlugin (com.google.dagger:hilt-android-gradle-plugin)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getGradlePlugin() { return create("dagger.hilt.gradlePlugin"); }

        /**
         * Returns the group of libraries at dagger.hilt.android
         */
        public DaggerHiltAndroidLibraryAccessors getAndroid() { return laccForDaggerHiltAndroidLibraryAccessors; }

    }

    public static class DaggerHiltAndroidLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {

        public DaggerHiltAndroidLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for android (com.google.dagger:hilt-android)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> asProvider() { return create("dagger.hilt.android"); }

            /**
             * Creates a dependency provider for compiler (com.google.dagger:hilt-android-compiler)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCompiler() { return create("dagger.hilt.android.compiler"); }

            /**
             * Creates a dependency provider for testing (com.google.dagger:hilt-android-testing)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getTesting() { return create("dagger.hilt.android.testing"); }

    }

    public static class DokkaLibraryAccessors extends SubDependencyFactory {

        public DokkaLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for allModulesPagePlugin (org.jetbrains.dokka:all-modules-page-plugin)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getAllModulesPagePlugin() { return create("dokka.allModulesPagePlugin"); }

            /**
             * Creates a dependency provider for gradlePlugin (org.jetbrains.dokka:dokka-gradle-plugin)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getGradlePlugin() { return create("dokka.gradlePlugin"); }

            /**
             * Creates a dependency provider for versioningPlugin (org.jetbrains.dokka:versioning-plugin)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getVersioningPlugin() { return create("dokka.versioningPlugin"); }

    }

    public static class IncapLibraryAccessors extends SubDependencyFactory implements DependencyNotationSupplier {

        public IncapLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for incap (net.ltgt.gradle.incap:incap)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> asProvider() { return create("incap"); }

            /**
             * Creates a dependency provider for compiler (net.ltgt.gradle.incap:incap-processor)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCompiler() { return create("incap.compiler"); }

    }

    public static class KotlinLibraryAccessors extends SubDependencyFactory {
        private final KotlinJvmLibraryAccessors laccForKotlinJvmLibraryAccessors = new KotlinJvmLibraryAccessors(owner);

        public KotlinLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for test (org.jetbrains.kotlin:kotlin-test)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getTest() { return create("kotlin.test"); }

        /**
         * Returns the group of libraries at kotlin.jvm
         */
        public KotlinJvmLibraryAccessors getJvm() { return laccForKotlinJvmLibraryAccessors; }

    }

    public static class KotlinJvmLibraryAccessors extends SubDependencyFactory {

        public KotlinJvmLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for gradle (org.jetbrains.kotlin:kotlin-gradle-plugin)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getGradle() { return create("kotlin.jvm.gradle"); }

    }

    public static class KspLibraryAccessors extends SubDependencyFactory {

        public KspLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for api (com.google.devtools.ksp:symbol-processing-api)
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getApi() { return create("ksp.api"); }

    }

    public static class VersionAccessors extends VersionFactory  {

        private final AndroidVersionAccessors vaccForAndroidVersionAccessors = new AndroidVersionAccessors(providers, config);
        private final AndroidxVersionAccessors vaccForAndroidxVersionAccessors = new AndroidxVersionAccessors(providers, config);
        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: autoService (1.0.1)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getAutoService() { return getVersion("autoService"); }

            /**
             * Returns the version associated to this alias: compileTesting (1.4.9)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getCompileTesting() { return getVersion("compileTesting"); }

            /**
             * Returns the version associated to this alias: dagger (2.44.1)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getDagger() { return getVersion("dagger"); }

            /**
             * Returns the version associated to this alias: incap (0.3)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getIncap() { return getVersion("incap"); }

            /**
             * Returns the version associated to this alias: kotlin (1.7.20)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getKotlin() { return getVersion("kotlin"); }

            /**
             * Returns the version associated to this alias: ksp (1.7.20-1.0.7)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getKsp() { return getVersion("ksp"); }

        /**
         * Returns the group of versions at versions.android
         */
        public AndroidVersionAccessors getAndroid() { return vaccForAndroidVersionAccessors; }

        /**
         * Returns the group of versions at versions.androidx
         */
        public AndroidxVersionAccessors getAndroidx() { return vaccForAndroidxVersionAccessors; }

    }

    public static class AndroidVersionAccessors extends VersionFactory  {

        public AndroidVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: android.compileSdk (33)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getCompileSdk() { return getVersion("android.compileSdk"); }

            /**
             * Returns the version associated to this alias: android.minSdk (14)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getMinSdk() { return getVersion("android.minSdk"); }

    }

    public static class AndroidxVersionAccessors extends VersionFactory  {

        public AndroidxVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: androidx.test (1.5.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getTest() { return getVersion("androidx.test"); }

    }

    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Creates a dependency bundle provider for androidTesting which is an aggregate for the following dependencies:
             * <ul>
             *    <li>org.jetbrains.kotlin:kotlin-test</li>
             *    <li>junit:junit</li>
             *    <li>com.google.truth:truth</li>
             *    <li>androidx.test:core-ktx</li>
             *    <li>androidx.test:runner</li>
             *    <li>androidx.test:rules</li>
             * </ul>
             * This bundle was declared in catalog libs.versions.toml
             */
            public Provider<ExternalModuleDependencyBundle> getAndroidTesting() { return createBundle("androidTesting"); }

            /**
             * Creates a dependency bundle provider for compileTesting which is an aggregate for the following dependencies:
             * <ul>
             *    <li>com.github.tschuchortdev:kotlin-compile-testing</li>
             *    <li>com.github.tschuchortdev:kotlin-compile-testing-ksp</li>
             * </ul>
             * This bundle was declared in catalog libs.versions.toml
             */
            public Provider<ExternalModuleDependencyBundle> getCompileTesting() { return createBundle("compileTesting"); }

            /**
             * Creates a dependency bundle provider for jvmTesting which is an aggregate for the following dependencies:
             * <ul>
             *    <li>org.jetbrains.kotlin:kotlin-test</li>
             *    <li>org.junit.jupiter:junit-jupiter</li>
             *    <li>com.google.truth:truth</li>
             * </ul>
             * This bundle was declared in catalog libs.versions.toml
             */
            public Provider<ExternalModuleDependencyBundle> getJvmTesting() { return createBundle("jvmTesting"); }

    }

    public static class PluginAccessors extends PluginFactory {
        private final DaggerPluginAccessors baccForDaggerPluginAccessors = new DaggerPluginAccessors(providers, config);

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Creates a plugin provider for binaryCompatibilityValidator to the plugin id 'org.jetbrains.kotlinx.binary-compatibility-validator'
             * This plugin was declared in catalog libs.versions.toml
             */
            public Provider<PluginDependency> getBinaryCompatibilityValidator() { return createPlugin("binaryCompatibilityValidator"); }

            /**
             * Creates a plugin provider for dokka to the plugin id 'org.jetbrains.dokka'
             * This plugin was declared in catalog libs.versions.toml
             */
            public Provider<PluginDependency> getDokka() { return createPlugin("dokka"); }

            /**
             * Creates a plugin provider for mkdocs to the plugin id 'ru.vyarus.mkdocs'
             * This plugin was declared in catalog libs.versions.toml
             */
            public Provider<PluginDependency> getMkdocs() { return createPlugin("mkdocs"); }

        /**
         * Returns the group of bundles at plugins.dagger
         */
        public DaggerPluginAccessors getDagger() { return baccForDaggerPluginAccessors; }

    }

    public static class DaggerPluginAccessors extends PluginFactory {

        public DaggerPluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Creates a plugin provider for dagger.hilt to the plugin id 'com.google.dagger.hilt.android'
             * This plugin was declared in catalog libs.versions.toml
             */
            public Provider<PluginDependency> getHilt() { return createPlugin("dagger.hilt"); }

    }

}

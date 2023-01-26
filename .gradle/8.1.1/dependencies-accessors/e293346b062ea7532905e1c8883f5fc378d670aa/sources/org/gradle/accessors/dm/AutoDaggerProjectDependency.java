package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.internal.artifacts.dependencies.ProjectDependencyInternal;
import org.gradle.api.internal.artifacts.DefaultProjectDependencyFactory;
import org.gradle.api.internal.artifacts.dsl.dependencies.ProjectFinder;
import org.gradle.api.internal.catalog.DelegatingProjectDependency;
import org.gradle.api.internal.catalog.TypeSafeProjectDependencyFactory;
import javax.inject.Inject;

@NonNullApi
public class AutoDaggerProjectDependency extends DelegatingProjectDependency {

    @Inject
    public AutoDaggerProjectDependency(TypeSafeProjectDependencyFactory factory, ProjectDependencyInternal delegate) {
        super(factory, delegate);
    }

    /**
     * Creates a project dependency on the project at path ":android"
     */
    public AndroidProjectDependency getAndroid() { return new AndroidProjectDependency(getFactory(), create(":android")); }

    /**
     * Creates a project dependency on the project at path ":androidx"
     */
    public AndroidxProjectDependency getAndroidx() { return new AndroidxProjectDependency(getFactory(), create(":androidx")); }

    /**
     * Creates a project dependency on the project at path ":compiler"
     */
    public CompilerProjectDependency getCompiler() { return new CompilerProjectDependency(getFactory(), create(":compiler")); }

    /**
     * Creates a project dependency on the project at path ":core"
     */
    public CoreProjectDependency getCore() { return new CoreProjectDependency(getFactory(), create(":core")); }

    /**
     * Creates a project dependency on the project at path ":retrofit"
     */
    public RetrofitProjectDependency getRetrofit() { return new RetrofitProjectDependency(getFactory(), create(":retrofit")); }

    /**
     * Creates a project dependency on the project at path ":tests"
     */
    public TestsProjectDependency getTests() { return new TestsProjectDependency(getFactory(), create(":tests")); }

}

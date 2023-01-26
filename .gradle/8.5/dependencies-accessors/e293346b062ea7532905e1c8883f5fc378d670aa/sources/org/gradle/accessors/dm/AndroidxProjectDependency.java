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
public class AndroidxProjectDependency extends DelegatingProjectDependency {

    @Inject
    public AndroidxProjectDependency(TypeSafeProjectDependencyFactory factory, ProjectDependencyInternal delegate) {
        super(factory, delegate);
    }

    /**
     * Creates a project dependency on the project at path ":androidx:room"
     */
    public Androidx_RoomProjectDependency getRoom() { return new Androidx_RoomProjectDependency(getFactory(), create(":androidx:room")); }

    /**
     * Creates a project dependency on the project at path ":androidx:viewmodel"
     */
    public Androidx_ViewmodelProjectDependency getViewmodel() { return new Androidx_ViewmodelProjectDependency(getFactory(), create(":androidx:viewmodel")); }

}

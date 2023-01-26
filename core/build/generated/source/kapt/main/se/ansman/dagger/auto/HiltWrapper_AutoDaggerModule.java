package se.ansman.dagger.auto;

import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import javax.annotation.processing.Generated;

@OriginatingElement(
    topLevelClass = AutoDaggerModule.class
)
@InstallIn(SingletonComponent.class)
@Module(
    includes = AutoDaggerModule.class
)
@Generated("dagger.hilt.processor.internal.aggregateddeps.PkgPrivateModuleGenerator")
public final class HiltWrapper_AutoDaggerModule {
}

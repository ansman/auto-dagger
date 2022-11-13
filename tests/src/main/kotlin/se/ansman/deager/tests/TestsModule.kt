package se.ansman.deager.tests

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import se.ansman.deager.Eager
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestsModule {
    @Provides
    @ExampleQualifier(
        booleanValue = true,
        byteValue = 1,
        charValue = 'n',
        shortValue = 2,
        intValue = 3,
        longValue = 4,
        floatValue = 5f,
        doubleValue = 6.0,
        stringValue = "value",
        annotationValue = Named("foo"),
        classValue = Singleton::class,
        enumValue = AnnotationRetention.BINARY,
        booleanArray = [true],
        byteArray = [1],
        charArray = ['n'],
        shortArray = [2],
        intArray = [3],
        longArray = [4],
        floatArray = [5f],
        doubleArray = [6.0],
        stringArray = ["value"],
        annotationArray = [Named("foo")],
        classArray = [Singleton::class],
        enumArray = [AnnotationRetention.BINARY],
    )
    @Eager
    @Singleton
    fun provideQualifiedThing() = QualifiedThing()
}
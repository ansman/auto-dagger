package se.ansman.dagger.auto

import javax.inject.Named
import javax.inject.Qualifier
import kotlin.reflect.KClass

@Qualifier
annotation class ExampleQualifier(
    val booleanValue: Boolean,
    val byteValue: Byte,
    val charValue: Char,
    val shortValue: Short,
    val intValue: Int,
    val longValue: Long,
    val floatValue: Float,
    val doubleValue: Double,
    val stringValue: String,
    val annotationValue: Named,
    val classValue: KClass<*>,
    val enumValue: AnnotationRetention,
    val booleanArray: BooleanArray,
    val byteArray: ByteArray,
    val charArray: CharArray,
    val shortArray: ShortArray,
    val intArray: IntArray,
    val longArray: LongArray,
    val floatArray: FloatArray,
    val doubleArray: DoubleArray,
    val stringArray: Array<String>,
    val annotationArray: Array<Named>,
    val classArray: Array<KClass<*>>,
    val enumArray: Array<AnnotationRetention>,
)

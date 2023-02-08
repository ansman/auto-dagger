package se.ansman.deager.renderers

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import se.ansman.deager.Initializable
import se.ansman.deager.applyIf
import se.ansman.deager.asClassName
import se.ansman.deager.models.EagerObject

typealias KotlinEagerObject = EagerObject<TypeName, AnnotationSpec>

class EagerObjectModuleKotlinRenderer private constructor(
    private val moduleName: ClassName,
    private val eagerObjects: List<KotlinEagerObject>
) {
    constructor(module: ClassName, eagerObjects: Iterable<KotlinEagerObject>) : this(
        moduleName = module.peerClass(module.simpleNames.joinToString(separator = "", prefix = "Eager")),
        eagerObjects = eagerObjects.toList()
    )

    constructor(eagerObject: KotlinEagerObject) : this(
        moduleName = eagerObject.moduleName(),
        eagerObjects = listOf(eagerObject)
    )

    fun render(modifier: TypeSpec.Builder.() -> Unit = {}): FileSpec {
        val bindings = mutableListOf<FunSpec>()
        val providers = mutableListOf<FunSpec>()

        for (eagerObject in eagerObjects) {
            val qualifiers = eagerObject.qualifiers.map { it.toAnnotationSpec() }
            val methodSpec = FunSpec.builder(eagerObject.method.name)
                .returns(Initializable::class)
            val parameterName = eagerObject.targetType
                .asClassName()
                .simpleName
                .replaceFirstChar(Char::lowercaseChar)
            when (eagerObject.method) {
                is EagerObject.Method.Binding -> {
                    bindings += methodSpec
                        .addModifiers(KModifier.ABSTRACT)
                        .addAnnotation(Binds::class)
                        .addAnnotation(IntoSet::class)
                        .receiver(eagerObject.targetType)
                        .addAnnotations(qualifiers.map {
                            it.toBuilder().useSiteTarget(AnnotationSpec.UseSiteTarget.RECEIVER).build()
                        })
                        .build()
                }
                is EagerObject.Method.Provider -> {
                    val parameter = ParameterSpec
                        .builder(parameterName, Lazy::class.asTypeName().parameterizedBy(eagerObject.targetType))
                        .addAnnotations(qualifiers)
                        .build()
                    providers += methodSpec
                        .addAnnotation(Provides::class)
                        .addAnnotation(IntoSet::class)
                        .addParameter(parameter)
                        .addStatement(
                            "return %N.%M(%L)",
                            parameter,
                            Initializable.Companion::class.member("asInitializable"),
                            if (eagerObject.priority == null) {
                                CodeBlock.of("")
                            } else {
                                CodeBlock.of("priority = %L", eagerObject.priority)
                            }
                        )
                        .build()
                }
            }
        }

        val typeSpec = if (bindings.isNotEmpty()) {
            TypeSpec.classBuilder(moduleName)
                .addModifiers(KModifier.ABSTRACT)
                .addFunction(FunSpec.constructorBuilder()
                    .addModifiers(KModifier.PRIVATE)
                    .build())
                .addFunctions(bindings)
                .applyIf(providers.isNotEmpty()) {
                    addType(
                        TypeSpec.companionObjectBuilder()
                            .addFunctions(providers)
                            .build()
                    )
                }
        } else {
            TypeSpec.objectBuilder(moduleName)
                .addFunctions(providers)
        }
            .addAnnotation(Module::class)
            .addModifiers(if (eagerObjects.all { it.isPublic }) KModifier.PUBLIC else KModifier.INTERNAL)
            .apply(modifier)
            .build()

        return FileSpec.builder(moduleName.packageName, moduleName.simpleName)
            .addFileComment("Code generated by Deager. Do not edit.")
            .addType(typeSpec)
            .build()
    }
}

private fun EagerObject<TypeName, *>.moduleName(): ClassName =
    ClassName(
        targetType.asClassName().packageName,
        targetType.asClassName().simpleNames.joinToString(prefix = "Eager", postfix = "Module", separator = "")
    )
package se.ansman.dagger.auto.renderers

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.codegen.OriginatingElement
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import se.ansman.dagger.auto.Initializable
import se.ansman.dagger.auto.asClassName
import se.ansman.dagger.auto.models.AutoInitializeObject
import javax.lang.model.element.Modifier

typealias JavaAutoInitializeObject = AutoInitializeObject<TypeName, AnnotationSpec>

class JavaAutoInitializeObjectRenderer private constructor(
    private val moduleName: ClassName,
    private val originatingElement: ClassName,
    private val initializeObjects: List<JavaAutoInitializeObject>
) {
    constructor(module: ClassName, initializableObjects: Iterable<JavaAutoInitializeObject>) : this(
        moduleName = module.peerClass(module.simpleNames().joinToString(separator = "", prefix = "AutoInitialize")),
        originatingElement = module.topLevelClassName(),
        initializeObjects = initializableObjects.toList()
    )

    constructor(initializeObject: JavaAutoInitializeObject) : this(
        moduleName = initializeObject.moduleName(),
        originatingElement = initializeObject.targetType.asClassName().topLevelClassName(),
        initializeObjects = listOf(initializeObject)
    )

    fun render(modifier: TypeSpec.Builder.() -> Unit = {}): JavaFile {
        val typeSpec = TypeSpec.classBuilder(moduleName)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addMethod(
                MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PRIVATE)
                    .build()
            )
            .addAnnotation(Module::class.java)
            .addAnnotation(
                AnnotationSpec.builder(InstallIn::class.java)
                    .addMember("value", "\$T.class", SingletonComponent::class.java)
                    .build()
            )
            .addAnnotation(
                AnnotationSpec.builder(OriginatingElement::class.java)
                    .addMember("topLevelClass", "\$T.class", originatingElement)
                    .build()
            )

        for (initializeObject in initializeObjects) {
            val qualifiers = initializeObject.qualifiers.map { it.toAnnotationSpec() }
            val methodSpec = MethodSpec.methodBuilder(initializeObject.method.name)
            val parameterName = initializeObject.targetType
                .asClassName()
                .simpleName()
                .replaceFirstChar(Char::lowercaseChar)
            when (initializeObject.method) {
                is AutoInitializeObject.Method.Binding -> {
                    methodSpec
                        .addModifiers(Modifier.ABSTRACT)
                        .addAnnotation(Binds::class.java)
                        .addParameter(
                            ParameterSpec
                                .builder(initializeObject.targetType, parameterName)
                                .addAnnotations(qualifiers)
                                .build()
                        )
                }
                is AutoInitializeObject.Method.Provider -> {
                    val parameter = ParameterSpec
                        .builder(
                            ParameterizedTypeName.get(ClassName.get(Lazy::class.java), initializeObject.targetType),
                            parameterName
                        )
                        .addAnnotations(qualifiers)
                        .build()
                    methodSpec
                        .addModifiers(Modifier.STATIC)
                        .addAnnotation(Provides::class.java)
                        .addParameter(parameter)
                        .addStatement(
                            "return \$T.fromLazy(\$N\$L)",
                            Initializable::class.java,
                            parameter,
                            if (initializeObject.priority == null) {
                                CodeBlock.of("")
                            } else {
                                CodeBlock.of(", /* priority */ \$L", initializeObject.priority)
                            }
                        )
                }
            }
            typeSpec.addMethod(
                methodSpec
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(IntoSet::class.java)
                    .returns(Initializable::class.java)
                    .build()
            )
        }

        return JavaFile.builder(moduleName.packageName(), typeSpec.apply(modifier).build())
            .addFileComment("Code generated by Auto Dagger. Do not edit.")
            .build()
    }
}

private fun JavaAutoInitializeObject.moduleName(): ClassName =
    ClassName.get(
        targetType.asClassName().packageName(),
        targetType.asClassName().simpleNames().joinToString(prefix = "AutoInitialize", postfix = "Module", separator = "")
    )
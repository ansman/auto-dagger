package se.ansman.dagger.auto.compiler.plugin.ir.model

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrStarProjection
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.IrTypeArgument
import org.jetbrains.kotlin.ir.types.IrTypeProjection
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.types.impl.IrStarProjectionImpl
import org.jetbrains.kotlin.ir.types.impl.makeTypeProjection
import org.jetbrains.kotlin.ir.types.typeWithArguments
import org.jetbrains.kotlin.ir.util.classIdOrFail
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.Variance
import se.ansman.dagger.auto.compiler.plugin.ir.KirEnvironment
import kotlin.reflect.KClass

fun IrType.toTypeName(): KirTypeName {
    return KirClassName(classOrFail.owner.classIdOrFail)
        .parameterizedBy(when (this) {
            is IrSimpleType -> arguments.map(::KirTypeArgument)
            else -> error("Unknown type: $this")
        })
}


sealed interface KirTypeName {
    val simpleName: String

    fun toIrType(environment: KirEnvironment): IrType
}

sealed interface KirTypeArgument {
    fun toTypeArgument(environment: KirEnvironment): IrTypeArgument
}

fun KirTypeArgument(typeArgument: IrTypeArgument): KirTypeArgument =
    when (typeArgument) {
        is IrStarProjection -> KirStar
        is IrTypeProjection -> KirSimpleTypeArgument(typeArgument.type.toTypeName(), typeArgument.variance)
        else -> error("Unknown type argument: $typeArgument")
    }

data object KirStar : KirTypeArgument {
    override fun toTypeArgument(environment: KirEnvironment): IrTypeArgument = IrStarProjectionImpl
}

data class KirSimpleTypeArgument(
    val type: KirTypeName,
    val variance: Variance = Variance.INVARIANT,
) : KirTypeArgument {
    override fun toTypeArgument(environment: KirEnvironment): IrTypeArgument =
        makeTypeProjection(type.toIrType(environment), variance)
}

data class KirParameterizedTypeName(
    val rawType: KirClassName,
    val arguments: List<KirTypeArgument>
) : KirTypeName {
    override val simpleName: String
        get() = rawType.simpleName

    override fun toIrType(environment: KirEnvironment): IrType =
        environment.context.referenceClass(rawType.classId)!!.typeWithArguments(arguments.map { it.toTypeArgument(environment) })
}

@JvmInline
value class KirClassName(
    val classId: ClassId
) : KirTypeName {
    val packageName: String get() = classId.packageFqName.asString()
    override val simpleName: String get() = classId.shortClassName.asString()
    val simpleNames: List<String> get() = classId.relativeClassName.asString().split('.')

    constructor(packageName: String, simpleName: String) : this(
        ClassId(
            FqName(packageName),
            Name.identifier(simpleName)
        )
    )

    constructor(packageName: String, simpleNames: List<String>) : this(
        ClassId(
            FqName(packageName),
            FqName.fromSegments(simpleNames),
            isLocal = false
        )
    )

    fun parameterizedBy(vararg typeArguments: KirTypeArgument): KirTypeName =
        parameterizedBy(typeArguments.asList())

    fun parameterizedBy(typeArguments: List<KirTypeArgument>): KirTypeName {
        if (typeArguments.isEmpty()) return this
        return KirParameterizedTypeName(this, typeArguments)
    }

    fun topLevelClassName(): KirClassName =
        if ('.' in classId.relativeClassName.asString()) {
            KirClassName(ClassId(classId.packageFqName, classId.relativeClassName.pathSegments().first()))
        } else {
            this
        }

    fun asClass(environment: KirEnvironment): IrClass =
        environment.renderEngine.typeLookup[classId].owner

    override fun toIrType(environment: KirEnvironment): IrType = asClass(environment).defaultType

    override fun toString(): String = "$packageName.${classId.relativeClassName.asString()}"
    fun sibling(name: String): KirClassName = KirClassName(
        packageName,
        simpleNames.dropLast(1) + name
    )

    companion object {
        fun KClass<*>.toKirClassName(): KirClassName = java.toKirClassName()
        fun Class<*>.toKirClassName(): KirClassName = KirClassName(toClassId())

        fun KClass<*>.toClassId(): ClassId = java.toClassId()

        fun Class<*>.toClassId(): ClassId {
            require(!isPrimitive) { "primitive types cannot be represented as a ClassName" }
            require(Void.TYPE != this) { "'void' type cannot be represented as a ClassName" }
            require(!isArray) { "array types cannot be represented as a ClassName" }
            val names = mutableListOf<String>()
            var c = this
            while (true) {
                names += c.simpleName
                val enclosing = c.enclosingClass ?: break
                c = enclosing
            }
            names.reverse()
            return ClassId(
                packageFqName = FqName(packageName),
                relativeClassName = FqName(names.joinToString(".")),
                isLocal = false
            )
        }

        fun bestGuess(classNameString: String): KirClassName {
            val names = mutableListOf<String>()

            // Add the package name, like "java.util.concurrent", or "" for no package.
            var p = 0
            while (p < classNameString.length && Character.isLowerCase(classNameString.codePointAt(p))) {
                p = classNameString.indexOf('.', p) + 1
                require(p != 0) { "couldn't make a guess for $classNameString" }
            }
            names += if (p != 0) classNameString.substring(0, p - 1) else ""

            // Add the class names, like "Map" and "Entry".
            for (part in classNameString.substring(p).split('.')) {
                require(part.isNotEmpty() && Character.isUpperCase(part.codePointAt(0))) {
                    "couldn't make a guess for $classNameString"
                }

                names += part
            }

            require(names.size >= 2) { "couldn't make a guess for $classNameString" }
            return KirClassName(
                ClassId(
                    FqName(names.first()),
                    FqName(names.drop(1).joinToString(".")),
                    isLocal = false
                )
            )
        }
    }
}
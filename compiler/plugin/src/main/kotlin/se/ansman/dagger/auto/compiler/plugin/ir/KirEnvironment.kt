package se.ansman.dagger.auto.compiler.plugin.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationContainer
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.IrTypeSystemContext
import org.jetbrains.kotlin.ir.types.IrTypeSystemContextImpl
import org.jetbrains.kotlin.ir.util.addChild
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.name.ClassId
import se.ansman.dagger.auto.compiler.common.TypeLookup
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerEnvironment
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerResolver
import se.ansman.dagger.auto.compiler.common.processing.RenderEngine
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirAnnotationSpec
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirClassDeclaration
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirClassName
import se.ansman.dagger.auto.compiler.plugin.ir.model.KirTypeName
import java.io.File
import kotlin.reflect.KClass

class KirEnvironment(
    override val logger: AutoDaggerKirLogger,
    val context: IrPluginContext,
    moduleFragment: IrModuleFragment,
    typeLookup: TypeLookup<ClassId, IrClassSymbol> = KirTypeLookup(context, moduleFragment),
    val renderEngine: KirRenderEngine = KirRenderEngine(context, typeLookup),
    private val dumpFilesTo: File?,
) : AutoDaggerEnvironment<IrElement, KirTypeName, KirClassName, KirAnnotationSpec, KirClass>,
    AutoDaggerResolver<IrElement, KirTypeName, KirClassName>,
    RenderEngine<IrElement, KirTypeName, KirClassName, KirAnnotationSpec> by renderEngine,
    IrPluginContext by context {
    val typeSystem: IrTypeSystemContext = IrTypeSystemContextImpl(context.irBuiltIns)
    private val typeLookup = TypeLookup { classId: ClassId ->
        KirClassDeclaration(renderEngine.typeLookup[classId].owner, this)
    }

    override val environment: KirEnvironment get() = this

    override fun write(output: KirClass) {
        val irFile = output.parent as IrFile
        irFile.addChild(output)
        dumpFilesTo
            ?.resolve(output.name.asString() + ".kt")
            ?.also { it.parentFile.mkdirs() }
            ?.writeText("package ${irFile.packageFqName.asString()}\n\n${output.dumpSrc()}")
    }

    override fun lookupType(className: KirClassName): KirClassDeclaration = typeLookup[className.classId]
    override fun lookupType(className: String) = lookupType(environment.className(className))
    override fun lookupType(className: KClass<*>) = lookupType(environment.className(className))
}

private class KirTypeLookup(
    private val context: IrPluginContext,
    private val moduleFragment: IrModuleFragment,
) : TypeLookup<ClassId, IrClassSymbol>, IrElementVisitor<IrClassSymbol?, ClassId> {
    override fun get(name: ClassId): IrClassSymbol =
        context.referenceClass(name)
            ?: moduleFragment.files.firstNotNullOfOrNull { it.findClass(name) }
            ?: throw IllegalArgumentException("Could not find class for name $name")

    override fun visitElement(element: IrElement, data: ClassId): IrClassSymbol? = null

    override fun visitClass(declaration: IrClass, data: ClassId): IrClassSymbol? {
        if (declaration.classId == data) {
            return declaration.symbol
        }
        return declaration.declarations.firstNotNullOfOrNull { it.accept(this, data) }
    }

    private fun IrDeclarationContainer.findClass(name: ClassId): IrClassSymbol? =
        declarations.firstNotNullOfOrNull { it.accept(this@KirTypeLookup, name) }
}

operator fun TypeLookup<ClassId, IrClassSymbol>.get(className: KirClassName) = get(className.classId)
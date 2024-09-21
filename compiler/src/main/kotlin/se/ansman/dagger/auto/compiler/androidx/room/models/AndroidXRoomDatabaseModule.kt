package se.ansman.dagger.auto.compiler.androidx.room.models

import se.ansman.dagger.auto.compiler.common.Processor
import se.ansman.dagger.auto.compiler.common.models.HiltModule
import se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder

data class AndroidXRoomDatabaseModule<Node, TypeName, ClassName : TypeName>(
    override val processor: Class<out Processor<*, *, *, *>>,
    override val moduleName: ClassName,
    override val installation: HiltModuleBuilder.Installation<ClassName>,
    override val originatingTopLevelClassName: ClassName,
    override val originatingElement: Node,
    val databaseType: ClassName,
    val daos: List<Dao<TypeName>>,
) : HiltModule<Node, ClassName> {
    data class Dao<TypeName>(
        val type: TypeName,
        val accessor: Accessor,
        val isPublic: Boolean,
    ) {
        sealed class Accessor {
            abstract val name: String

            data class Function(override val name: String) : Accessor()
            data class Property(override val name: String) : Accessor()
        }
    }
}

package se.ansman.dagger.auto.compiler.androidx.room.renderer;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002,\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b0\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\tJ\u001e\u0010\n\u001a\u00020\u00072\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00030\f2\u0006\u0010\r\u001a\u00020\u0006H\u0014\u00a8\u0006\u000e"}, d2 = {"Lse/ansman/dagger/auto/compiler/androidx/room/renderer/KotlinAndroidXRoomDatabaseModuleRenderer;", "Lse/ansman/dagger/auto/compiler/androidx/room/renderer/AndroidXRoomDatabaseModuleRenderer;", "Lcom/google/devtools/ksp/symbol/KSDeclaration;", "Lcom/squareup/kotlinpoet/TypeName;", "Lcom/squareup/kotlinpoet/ClassName;", "Lcom/squareup/kotlinpoet/AnnotationSpec;", "Lcom/squareup/kotlinpoet/ParameterSpec;", "Lcom/squareup/kotlinpoet/CodeBlock;", "Lcom/squareup/kotlinpoet/FileSpec;", "()V", "provideDao", "dao", "Lse/ansman/dagger/auto/compiler/androidx/room/models/AndroidXRoomDatabaseModule$Dao;", "databaseParameter", "compiler"})
public final class KotlinAndroidXRoomDatabaseModuleRenderer extends se.ansman.dagger.auto.compiler.androidx.room.renderer.AndroidXRoomDatabaseModuleRenderer<com.google.devtools.ksp.symbol.KSDeclaration, com.squareup.kotlinpoet.TypeName, com.squareup.kotlinpoet.ClassName, com.squareup.kotlinpoet.AnnotationSpec, com.squareup.kotlinpoet.ParameterSpec, com.squareup.kotlinpoet.CodeBlock, com.squareup.kotlinpoet.FileSpec> {
    @org.jetbrains.annotations.NotNull()
    public static final se.ansman.dagger.auto.compiler.androidx.room.renderer.KotlinAndroidXRoomDatabaseModuleRenderer INSTANCE = null;
    
    private KotlinAndroidXRoomDatabaseModuleRenderer() {
        super(null, null);
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    protected com.squareup.kotlinpoet.CodeBlock provideDao(@org.jetbrains.annotations.NotNull()
    se.ansman.dagger.auto.compiler.androidx.room.models.AndroidXRoomDatabaseModule.Dao<com.squareup.kotlinpoet.TypeName> dao, @org.jetbrains.annotations.NotNull()
    com.squareup.kotlinpoet.ParameterSpec databaseParameter) {
        return null;
    }
}
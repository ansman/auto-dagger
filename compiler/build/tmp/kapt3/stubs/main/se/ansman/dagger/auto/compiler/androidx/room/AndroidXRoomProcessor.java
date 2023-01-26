package se.ansman.dagger.auto.compiler.androidx.room;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u0000*\u0004\b\u0000\u0010\u0001*\u0004\b\u0001\u0010\u0002*\b\b\u0002\u0010\u0003*\u0002H\u0002*\u0004\b\u0003\u0010\u0004*\u0004\b\u0004\u0010\u00052\u001a\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u0003\u0012\u0004\u0012\u0002H\u00040\u0006BY\u0012$\u0010\u0007\u001a \u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u0003\u0012\u0004\u0012\u00028\u00040\b\u0012,\u0010\t\u001a(\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u0003\u0012\u0002\b\u0003\u0012\u0002\b\u0003\u0012\u0004\u0012\u00028\u00040\n\u00a2\u0006\u0002\u0010\u000bJ(\u0010\u0013\u001a\u00020\u00142\u001e\u0010\u0015\u001a\u001a\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u00030\u0016H\u0016J$\u0010\u0017\u001a\u00020\u0014*\u001a\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u00030\u0018H\u0002R\u001a\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rX\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R,\u0010\u0007\u001a \u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u0003\u0012\u0004\u0012\u00028\u00040\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00028\u00000\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R4\u0010\t\u001a(\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u0003\u0012\u0002\b\u0003\u0012\u0002\b\u0003\u0012\u0004\u0012\u00028\u00040\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lse/ansman/dagger/auto/compiler/androidx/room/AndroidXRoomProcessor;", "N", "TypeName", "ClassName", "AnnotationSpec", "F", "Lse/ansman/dagger/auto/compiler/common/Processor;", "environment", "Lse/ansman/dagger/auto/compiler/common/processing/AutoDaggerEnvironment;", "renderer", "Lse/ansman/dagger/auto/compiler/androidx/room/renderer/AndroidXRoomDatabaseModuleRenderer;", "(Lse/ansman/dagger/auto/compiler/common/processing/AutoDaggerEnvironment;Lse/ansman/dagger/auto/compiler/androidx/room/renderer/AndroidXRoomDatabaseModuleRenderer;)V", "annotations", "", "", "getAnnotations", "()Ljava/util/Set;", "logger", "Lse/ansman/dagger/auto/compiler/common/processing/AutoDaggerLogger;", "process", "", "resolver", "Lse/ansman/dagger/auto/compiler/common/processing/AutoDaggerResolver;", "validateDatabase", "Lse/ansman/dagger/auto/compiler/common/processing/ClassDeclaration;", "compiler"})
public final class AndroidXRoomProcessor<N extends java.lang.Object, TypeName extends java.lang.Object, ClassName extends TypeName, AnnotationSpec extends java.lang.Object, F extends java.lang.Object> implements se.ansman.dagger.auto.compiler.common.Processor<N, TypeName, ClassName, AnnotationSpec> {
    @org.jetbrains.annotations.NotNull()
    private final se.ansman.dagger.auto.compiler.common.processing.AutoDaggerEnvironment<N, TypeName, ClassName, AnnotationSpec, F> environment = null;
    @org.jetbrains.annotations.NotNull()
    private final se.ansman.dagger.auto.compiler.androidx.room.renderer.AndroidXRoomDatabaseModuleRenderer<N, TypeName, ClassName, AnnotationSpec, ?, ?, F> renderer = null;
    @org.jetbrains.annotations.NotNull()
    private final se.ansman.dagger.auto.compiler.common.processing.AutoDaggerLogger<N> logger = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Set<java.lang.String> annotations = null;
    
    public AndroidXRoomProcessor(@org.jetbrains.annotations.NotNull()
    se.ansman.dagger.auto.compiler.common.processing.AutoDaggerEnvironment<? super N, TypeName, ClassName, AnnotationSpec, F> environment, @org.jetbrains.annotations.NotNull()
    se.ansman.dagger.auto.compiler.androidx.room.renderer.AndroidXRoomDatabaseModuleRenderer<N, TypeName, ClassName, AnnotationSpec, ?, ?, F> renderer) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.util.Set<java.lang.String> getAnnotations() {
        return null;
    }
    
    @java.lang.Override()
    public void process(@org.jetbrains.annotations.NotNull()
    se.ansman.dagger.auto.compiler.common.processing.AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec> resolver) {
    }
    
    private final void validateDatabase(se.ansman.dagger.auto.compiler.common.processing.ClassDeclaration<N, TypeName, ClassName, AnnotationSpec> $this$validateDatabase) {
    }
}
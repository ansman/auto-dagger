package se.ansman.dagger.auto.compiler.replaces;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000*\u0004\b\u0000\u0010\u0001*\b\b\u0001\u0010\u0002*\u00020\u0003*\b\b\u0002\u0010\u0004*\u0002H\u0002*\u0004\b\u0003\u0010\u0005*\u0004\b\u0004\u0010\u00062\u001a\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u0004\u0012\u0004\u0012\u0002H\u00050\u0007BW\u0012$\u0010\b\u001a \u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u0003\u0012\u0004\u0012\u00028\u00040\t\u0012*\u0010\n\u001a&\u0012\u001c\u0012\u001a\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u00030\f\u0012\u0004\u0012\u00028\u00040\u000b\u00a2\u0006\u0002\u0010\rJ(\u0010\u0019\u001a\u00020\u001a2\u001e\u0010\u001b\u001a\u001a\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u00030\u001cH\u0016Jh\u0010\u001d\u001a\u00020\u001a2\u001e\u0010\u001e\u001a\u001a\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u00030\u001f2\u001e\u0010 \u001a\u001a\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u00030\u001f2\u001e\u0010\u001b\u001a\u001a\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u00030\u001cH\u0002JD\u0010!\u001a\u00020\u001a*\u001a\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u00030\u001f2\u001e\u0010 \u001a\u001a\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u00030\u001fH\u0002R\u001a\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fX\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R,\u0010\u0013\u001a \u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u0003\u0012\u0004\u0012\u00028\u00040\u0014X\u0082\u0004\u00a2\u0006\u0002\n\u0000R,\u0010\u0015\u001a \u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u0003\u0012\u0004\u0012\u00028\u00040\u0016X\u0082\u0004\u00a2\u0006\u0002\n\u0000R,\u0010\b\u001a \u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u0003\u0012\u0004\u0012\u00028\u00040\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0017\u001a\b\u0012\u0004\u0012\u00028\u00000\u0018X\u0082\u0004\u00a2\u0006\u0002\n\u0000R2\u0010\n\u001a&\u0012\u001c\u0012\u001a\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u00030\f\u0012\u0004\u0012\u00028\u00040\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\""}, d2 = {"Lse/ansman/dagger/auto/compiler/replaces/ReplacesProcessor;", "N", "TypeName", "", "ClassName", "AnnotationSpec", "F", "Lse/ansman/dagger/auto/compiler/common/Processor;", "environment", "Lse/ansman/dagger/auto/compiler/common/processing/AutoDaggerEnvironment;", "renderer", "Lse/ansman/dagger/auto/compiler/common/rendering/Renderer;", "Lse/ansman/dagger/auto/compiler/autobind/models/AutoBindObjectModule;", "(Lse/ansman/dagger/auto/compiler/common/processing/AutoDaggerEnvironment;Lse/ansman/dagger/auto/compiler/common/rendering/Renderer;)V", "annotations", "", "", "getAnnotations", "()Ljava/util/Set;", "autoBindProcessor", "Lse/ansman/dagger/auto/compiler/autobind/AutoBindProcessor;", "autoInitializeProcessor", "Lse/ansman/dagger/auto/compiler/autoinitialize/AutoInitializeProcessor;", "logger", "Lse/ansman/dagger/auto/compiler/common/processing/AutoDaggerLogger;", "process", "", "resolver", "Lse/ansman/dagger/auto/compiler/common/processing/AutoDaggerResolver;", "processAutoBind", "type", "Lse/ansman/dagger/auto/compiler/common/processing/ClassDeclaration;", "replaces", "renderAutoInitializeReplacementModule", "compiler"})
public final class ReplacesProcessor<N extends java.lang.Object, TypeName extends java.lang.Object, ClassName extends TypeName, AnnotationSpec extends java.lang.Object, F extends java.lang.Object> implements se.ansman.dagger.auto.compiler.common.Processor<N, TypeName, ClassName, AnnotationSpec> {
    @org.jetbrains.annotations.NotNull()
    private final se.ansman.dagger.auto.compiler.common.processing.AutoDaggerEnvironment<N, TypeName, ClassName, AnnotationSpec, F> environment = null;
    @org.jetbrains.annotations.NotNull()
    private final se.ansman.dagger.auto.compiler.common.rendering.Renderer<se.ansman.dagger.auto.compiler.autobind.models.AutoBindObjectModule<? extends N, TypeName, ClassName, AnnotationSpec>, F> renderer = null;
    @org.jetbrains.annotations.NotNull()
    private final se.ansman.dagger.auto.compiler.common.processing.AutoDaggerLogger<N> logger = null;
    @org.jetbrains.annotations.NotNull()
    private final se.ansman.dagger.auto.compiler.autobind.AutoBindProcessor<N, TypeName, ClassName, AnnotationSpec, F> autoBindProcessor = null;
    @org.jetbrains.annotations.NotNull()
    private final se.ansman.dagger.auto.compiler.autoinitialize.AutoInitializeProcessor<N, TypeName, ClassName, AnnotationSpec, F> autoInitializeProcessor = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Set<java.lang.String> annotations = null;
    
    public ReplacesProcessor(@org.jetbrains.annotations.NotNull()
    se.ansman.dagger.auto.compiler.common.processing.AutoDaggerEnvironment<? super N, TypeName, ClassName, AnnotationSpec, F> environment, @org.jetbrains.annotations.NotNull()
    se.ansman.dagger.auto.compiler.common.rendering.Renderer<? super se.ansman.dagger.auto.compiler.autobind.models.AutoBindObjectModule<? extends N, TypeName, ClassName, AnnotationSpec>, ? extends F> renderer) {
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
    
    private final void processAutoBind(se.ansman.dagger.auto.compiler.common.processing.ClassDeclaration<N, TypeName, ClassName, AnnotationSpec> type, se.ansman.dagger.auto.compiler.common.processing.ClassDeclaration<N, TypeName, ClassName, AnnotationSpec> replaces, se.ansman.dagger.auto.compiler.common.processing.AutoDaggerResolver<N, TypeName, ClassName, AnnotationSpec> resolver) {
    }
    
    private final void renderAutoInitializeReplacementModule(se.ansman.dagger.auto.compiler.common.processing.ClassDeclaration<N, TypeName, ClassName, AnnotationSpec> $this$renderAutoInitializeReplacementModule, se.ansman.dagger.auto.compiler.common.processing.ClassDeclaration<N, TypeName, ClassName, AnnotationSpec> replaces) {
    }
}
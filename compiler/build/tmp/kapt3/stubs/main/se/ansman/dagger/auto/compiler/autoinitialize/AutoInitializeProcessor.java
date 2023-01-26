package se.ansman.dagger.auto.compiler.autoinitialize;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000*\u0004\b\u0000\u0010\u0001*\u0004\b\u0001\u0010\u0002*\b\b\u0002\u0010\u0003*\u0002H\u0002*\u0004\b\u0003\u0010\u0004*\u0004\b\u0004\u0010\u00052\u001a\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u0003\u0012\u0004\u0012\u0002H\u00040\u0006:\u0001)BW\u0012$\u0010\u0007\u001a \u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u0003\u0012\u0004\u0012\u00028\u00040\b\u0012*\u0010\t\u001a&\u0012\u001c\u0012\u001a\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u00030\u000b\u0012\u0004\u0012\u00028\u00040\n\u00a2\u0006\u0002\u0010\fJ(\u0010\u0017\u001a\u00020\u00182\u001e\u0010\u0019\u001a\u001a\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u00030\u001aH\u0016J>\u0010\u0017\u001a\u001a\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u00030\u000b2\u001e\u0010\u001b\u001a\u001a\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u00030\u001cJP\u0010\u001d\u001a\u00020\u0018*\u001a\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u00030\u001e2*\u0010\u001f\u001a&\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u00020!\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u00030\"0 H\u0002J\'\u0010#\u001a\u00020\u0018*\u0010\u0012\f\u0012\n\u0012\u0002\b\u0003\u0012\u0002\b\u00030%0$2\u0006\u0010&\u001a\u00028\u0000H\u0002\u00a2\u0006\u0002\u0010\'J\u001e\u0010#\u001a\u00020\u0018*\u0014\u0012\u0004\u0012\u00028\u0000\u0012\u0002\b\u0003\u0012\u0002\b\u0003\u0012\u0002\b\u00030(H\u0002R\u001a\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eX\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R,\u0010\u0007\u001a \u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u0003\u0012\u0004\u0012\u00028\u00040\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00028\u00000\u0013X\u0082\u0004\u00a2\u0006\u0002\n\u0000R2\u0010\t\u001a&\u0012\u001c\u0012\u001a\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u00030\u000b\u0012\u0004\u0012\u00028\u00040\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0018\u0010\u0014\u001a\u00028\u0002*\u00028\u00028BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0015\u0010\u0016\u00a8\u0006*"}, d2 = {"Lse/ansman/dagger/auto/compiler/autoinitialize/AutoInitializeProcessor;", "N", "TypeName", "ClassName", "AnnotationSpec", "F", "Lse/ansman/dagger/auto/compiler/common/Processor;", "environment", "Lse/ansman/dagger/auto/compiler/common/processing/AutoDaggerEnvironment;", "renderer", "Lse/ansman/dagger/auto/compiler/common/rendering/Renderer;", "Lse/ansman/dagger/auto/compiler/autoinitialize/models/AutoInitializeModule;", "(Lse/ansman/dagger/auto/compiler/common/processing/AutoDaggerEnvironment;Lse/ansman/dagger/auto/compiler/common/rendering/Renderer;)V", "annotations", "", "", "getAnnotations", "()Ljava/util/Set;", "logger", "Lse/ansman/dagger/auto/compiler/common/processing/AutoDaggerLogger;", "moduleName", "getModuleName", "(Ljava/lang/Object;)Ljava/lang/Object;", "process", "", "resolver", "Lse/ansman/dagger/auto/compiler/common/processing/AutoDaggerResolver;", "type", "Lse/ansman/dagger/auto/compiler/common/processing/ClassDeclaration;", "processMethod", "Lse/ansman/dagger/auto/compiler/common/processing/ExecutableNode;", "output", "Lcom/google/common/collect/Multimap;", "Lse/ansman/dagger/auto/compiler/autoinitialize/AutoInitializeProcessor$ModuleKey;", "Lse/ansman/dagger/auto/compiler/autoinitialize/models/AutoInitializeObject;", "validateScopes", "", "Lse/ansman/dagger/auto/compiler/common/processing/AnnotationModel;", "node", "(Ljava/util/List;Ljava/lang/Object;)V", "Lse/ansman/dagger/auto/compiler/common/processing/Node;", "ModuleKey", "compiler"})
public final class AutoInitializeProcessor<N extends java.lang.Object, TypeName extends java.lang.Object, ClassName extends TypeName, AnnotationSpec extends java.lang.Object, F extends java.lang.Object> implements se.ansman.dagger.auto.compiler.common.Processor<N, TypeName, ClassName, AnnotationSpec> {
    @org.jetbrains.annotations.NotNull()
    private final se.ansman.dagger.auto.compiler.common.processing.AutoDaggerEnvironment<N, TypeName, ClassName, AnnotationSpec, F> environment = null;
    @org.jetbrains.annotations.NotNull()
    private final se.ansman.dagger.auto.compiler.common.rendering.Renderer<se.ansman.dagger.auto.compiler.autoinitialize.models.AutoInitializeModule<? extends N, TypeName, ClassName, AnnotationSpec>, F> renderer = null;
    @org.jetbrains.annotations.NotNull()
    private final se.ansman.dagger.auto.compiler.common.processing.AutoDaggerLogger<N> logger = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Set<java.lang.String> annotations = null;
    
    public AutoInitializeProcessor(@org.jetbrains.annotations.NotNull()
    se.ansman.dagger.auto.compiler.common.processing.AutoDaggerEnvironment<? super N, TypeName, ClassName, AnnotationSpec, F> environment, @org.jetbrains.annotations.NotNull()
    se.ansman.dagger.auto.compiler.common.rendering.Renderer<? super se.ansman.dagger.auto.compiler.autoinitialize.models.AutoInitializeModule<? extends N, TypeName, ClassName, AnnotationSpec>, ? extends F> renderer) {
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
    
    @org.jetbrains.annotations.NotNull()
    public final se.ansman.dagger.auto.compiler.autoinitialize.models.AutoInitializeModule<N, TypeName, ClassName, AnnotationSpec> process(@org.jetbrains.annotations.NotNull()
    se.ansman.dagger.auto.compiler.common.processing.ClassDeclaration<N, TypeName, ClassName, AnnotationSpec> type) {
        return null;
    }
    
    private final void processMethod(se.ansman.dagger.auto.compiler.common.processing.ExecutableNode<N, TypeName, ClassName, AnnotationSpec> $this$processMethod, com.google.common.collect.Multimap<se.ansman.dagger.auto.compiler.autoinitialize.AutoInitializeProcessor.ModuleKey<N, ClassName>, se.ansman.dagger.auto.compiler.autoinitialize.models.AutoInitializeObject<TypeName, AnnotationSpec>> output) {
    }
    
    private final void validateScopes(java.util.List<? extends se.ansman.dagger.auto.compiler.common.processing.AnnotationModel<?, ?>> $this$validateScopes, N node) {
    }
    
    private final void validateScopes(se.ansman.dagger.auto.compiler.common.processing.Node<N, ?, ?, ?> $this$validateScopes) {
    }
    
    private final ClassName getModuleName(ClassName $this$moduleName) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u0000*\u0006\b\u0005\u0010\u0001 \u0001*\u0004\b\u0006\u0010\u00022\u00020\u0003B\u0015\u0012\u0006\u0010\u0004\u001a\u00028\u0006\u0012\u0006\u0010\u0005\u001a\u00028\u0005\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u000b\u001a\u00028\u0006H\u00c6\u0003\u00a2\u0006\u0002\u0010\bJ\u000e\u0010\f\u001a\u00028\u0005H\u00c6\u0003\u00a2\u0006\u0002\u0010\bJ.\u0010\r\u001a\u000e\u0012\u0004\u0012\u00028\u0005\u0012\u0004\u0012\u00028\u00060\u00002\b\b\u0002\u0010\u0004\u001a\u00028\u00062\b\b\u0002\u0010\u0005\u001a\u00028\u0005H\u00c6\u0001\u00a2\u0006\u0002\u0010\u000eJ\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0003H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0015H\u00d6\u0001R\u0013\u0010\u0004\u001a\u00028\u0006\u00a2\u0006\n\n\u0002\u0010\t\u001a\u0004\b\u0007\u0010\bR\u0013\u0010\u0005\u001a\u00028\u0005\u00a2\u0006\n\n\u0002\u0010\t\u001a\u0004\b\n\u0010\b\u00a8\u0006\u0016"}, d2 = {"Lse/ansman/dagger/auto/compiler/autoinitialize/AutoInitializeProcessor$ModuleKey;", "Node", "ClassName", "", "moduleName", "originatingElement", "(Ljava/lang/Object;Ljava/lang/Object;)V", "getModuleName", "()Ljava/lang/Object;", "Ljava/lang/Object;", "getOriginatingElement", "component1", "component2", "copy", "(Ljava/lang/Object;Ljava/lang/Object;)Lse/ansman/dagger/auto/compiler/autoinitialize/AutoInitializeProcessor$ModuleKey;", "equals", "", "other", "hashCode", "", "toString", "", "compiler"})
    static final class ModuleKey<Node extends java.lang.Object, ClassName extends java.lang.Object> {
        private final ClassName moduleName = null;
        private final Node originatingElement = null;
        
        public ModuleKey(ClassName moduleName, Node originatingElement) {
            super();
        }
        
        public final ClassName getModuleName() {
            return null;
        }
        
        public final Node getOriginatingElement() {
            return null;
        }
        
        public final ClassName component1() {
            return null;
        }
        
        public final Node component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final se.ansman.dagger.auto.compiler.autoinitialize.AutoInitializeProcessor.ModuleKey<Node, ClassName> copy(ClassName moduleName, Node originatingElement) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
}
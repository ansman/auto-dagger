package se.ansman.dagger.auto.compiler.autoinitialize.renderer;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\b\b&\u0018\u0000 \u0019*\u0004\b\u0000\u0010\u0001*\u0004\b\u0001\u0010\u0002*\b\b\u0002\u0010\u0003*\u0002H\u0002*\u0004\b\u0003\u0010\u0004*\u0004\b\u0004\u0010\u0005*\u0004\b\u0005\u0010\u0006*\u0004\b\u0006\u0010\u00072&\u0012\u001c\u0012\u001a\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u0003\u0012\u0004\u0012\u0002H\u00040\t\u0012\u0004\u0012\u0002H\u00070\b:\u0001\u0019BQ\u0012\u0018\u0010\n\u001a\u0014\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u00030\u000b\u00120\u0010\f\u001a,\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u0003\u0012\u0004\u0012\u00028\u0004\u0012\u0004\u0012\u00028\u0005\u0012\u0004\u0012\u00028\u00060\r\u00a2\u0006\u0002\u0010\u000eJ\u001f\u0010\u000f\u001a\u00028\u00052\u0006\u0010\u0010\u001a\u00028\u00042\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H&\u00a2\u0006\u0002\u0010\u0013J+\u0010\u0014\u001a\u00028\u00062\u001e\u0010\u0015\u001a\u001a\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u00030\t\u00a2\u0006\u0002\u0010\u0016J\u001d\u0010\u0017\u001a\u00028\u00052\u0006\u0010\u0010\u001a\u00028\u00042\u0006\u0010\u0011\u001a\u00020\u0012H&\u00a2\u0006\u0002\u0010\u0018R8\u0010\f\u001a,\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u0003\u0012\u0004\u0012\u00028\u0004\u0012\u0004\u0012\u00028\u0005\u0012\u0004\u0012\u00028\u00060\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R \u0010\n\u001a\u0014\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u00030\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001a"}, d2 = {"Lse/ansman/dagger/auto/compiler/autoinitialize/renderer/AutoInitializeModuleRenderer;", "Node", "TypeName", "ClassName", "AnnotationSpec", "ParameterSpec", "CodeBlock", "SourceFile", "Lse/ansman/dagger/auto/compiler/common/rendering/Renderer;", "Lse/ansman/dagger/auto/compiler/autoinitialize/models/AutoInitializeModule;", "renderEngine", "Lse/ansman/dagger/auto/compiler/common/processing/RenderEngine;", "builderFactory", "Lse/ansman/dagger/auto/compiler/common/rendering/HiltModuleBuilder$Factory;", "(Lse/ansman/dagger/auto/compiler/common/processing/RenderEngine;Lse/ansman/dagger/auto/compiler/common/rendering/HiltModuleBuilder$Factory;)V", "createInitializableFromLazy", "parameter", "priority", "", "(Ljava/lang/Object;Ljava/lang/Integer;)Ljava/lang/Object;", "render", "input", "(Lse/ansman/dagger/auto/compiler/autoinitialize/models/AutoInitializeModule;)Ljava/lang/Object;", "wrapInitializable", "(Ljava/lang/Object;I)Ljava/lang/Object;", "Companion", "compiler"})
public abstract class AutoInitializeModuleRenderer<Node extends java.lang.Object, TypeName extends java.lang.Object, ClassName extends TypeName, AnnotationSpec extends java.lang.Object, ParameterSpec extends java.lang.Object, CodeBlock extends java.lang.Object, SourceFile extends java.lang.Object> implements se.ansman.dagger.auto.compiler.common.rendering.Renderer<se.ansman.dagger.auto.compiler.autoinitialize.models.AutoInitializeModule<? extends Node, TypeName, ClassName, AnnotationSpec>, SourceFile> {
    @org.jetbrains.annotations.NotNull()
    private final se.ansman.dagger.auto.compiler.common.processing.RenderEngine<TypeName, ClassName, AnnotationSpec> renderEngine = null;
    @org.jetbrains.annotations.NotNull()
    private final se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder.Factory<Node, TypeName, ClassName, AnnotationSpec, ParameterSpec, CodeBlock, SourceFile> builderFactory = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.reflect.KClass<se.ansman.dagger.auto.Initializable> initializable = null;
    @org.jetbrains.annotations.NotNull()
    public static final se.ansman.dagger.auto.compiler.autoinitialize.renderer.AutoInitializeModuleRenderer.Companion Companion = null;
    
    public AutoInitializeModuleRenderer(@org.jetbrains.annotations.NotNull()
    se.ansman.dagger.auto.compiler.common.processing.RenderEngine<TypeName, ClassName, AnnotationSpec> renderEngine, @org.jetbrains.annotations.NotNull()
    se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder.Factory<? super Node, TypeName, ClassName, AnnotationSpec, ParameterSpec, CodeBlock, SourceFile> builderFactory) {
        super();
    }
    
    @java.lang.Override()
    public final SourceFile render(@org.jetbrains.annotations.NotNull()
    se.ansman.dagger.auto.compiler.autoinitialize.models.AutoInitializeModule<? extends Node, TypeName, ClassName, AnnotationSpec> input) {
        return null;
    }
    
    public abstract CodeBlock createInitializableFromLazy(ParameterSpec parameter, @org.jetbrains.annotations.Nullable()
    java.lang.Integer priority);
    
    public abstract CodeBlock wrapInitializable(ParameterSpec parameter, int priority);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lse/ansman/dagger/auto/compiler/autoinitialize/renderer/AutoInitializeModuleRenderer$Companion;", "", "()V", "initializable", "Lkotlin/reflect/KClass;", "Lse/ansman/dagger/auto/Initializable;", "compiler"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}
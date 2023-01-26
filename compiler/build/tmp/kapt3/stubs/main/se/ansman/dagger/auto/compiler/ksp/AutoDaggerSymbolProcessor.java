package se.ansman.dagger.auto.compiler.ksp;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00072\u0006\u0010\u0010\u001a\u00020\u0011H\u0016R\u000e\u0010\u0002\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R0\u0010\u0006\u001a$\u0012 \u0012\u001e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\f0\bj\u0002`\r0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2 = {"Lse/ansman/dagger/auto/compiler/ksp/AutoDaggerSymbolProcessor;", "Lcom/google/devtools/ksp/processing/SymbolProcessor;", "environment", "Lcom/google/devtools/ksp/processing/SymbolProcessorEnvironment;", "(Lcom/google/devtools/ksp/processing/SymbolProcessorEnvironment;)V", "Lse/ansman/dagger/auto/compiler/common/ksp/processing/KspEnvironment;", "processors", "", "Lse/ansman/dagger/auto/compiler/common/Processor;", "Lcom/google/devtools/ksp/symbol/KSDeclaration;", "Lcom/squareup/kotlinpoet/TypeName;", "Lcom/squareup/kotlinpoet/ClassName;", "Lcom/squareup/kotlinpoet/AnnotationSpec;", "Lse/ansman/dagger/auto/compiler/common/ksp/KspProcessor;", "process", "Lcom/google/devtools/ksp/symbol/KSAnnotated;", "resolver", "Lcom/google/devtools/ksp/processing/Resolver;", "compiler"})
public final class AutoDaggerSymbolProcessor implements com.google.devtools.ksp.processing.SymbolProcessor {
    @org.jetbrains.annotations.NotNull()
    private final se.ansman.dagger.auto.compiler.common.ksp.processing.KspEnvironment environment = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<se.ansman.dagger.auto.compiler.common.Processor<com.google.devtools.ksp.symbol.KSDeclaration, com.squareup.kotlinpoet.TypeName, com.squareup.kotlinpoet.ClassName, com.squareup.kotlinpoet.AnnotationSpec>> processors = null;
    
    public AutoDaggerSymbolProcessor(@org.jetbrains.annotations.NotNull()
    com.google.devtools.ksp.processing.SymbolProcessorEnvironment environment) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.util.List<com.google.devtools.ksp.symbol.KSAnnotated> process(@org.jetbrains.annotations.NotNull()
    com.google.devtools.ksp.processing.Resolver resolver) {
        return null;
    }
}
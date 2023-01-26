package se.ansman.dagger.auto.compiler.kapt;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B1\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\"\u0010\u0004\u001a\u001e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0005j\u0002`\n\u00a2\u0006\u0002\u0010\u000bJ\u000e\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rH\u0016J\"\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00060\r2\u0012\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u00060\u0011H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R*\u0010\u0004\u001a\u001e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0005j\u0002`\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2 = {"Lse/ansman/dagger/auto/compiler/kapt/AutoDaggerProcessorStep;", "Lcom/google/auto/common/BasicAnnotationProcessor$Step;", "environment", "Lse/ansman/dagger/auto/compiler/common/kapt/processing/KaptEnvironment;", "processor", "Lse/ansman/dagger/auto/compiler/common/Processor;", "Ljavax/lang/model/element/Element;", "Lcom/squareup/javapoet/TypeName;", "Lcom/squareup/javapoet/ClassName;", "Lcom/squareup/javapoet/AnnotationSpec;", "Lse/ansman/dagger/auto/compiler/common/kapt/KaptProcessor;", "(Lse/ansman/dagger/auto/compiler/common/kapt/processing/KaptEnvironment;Lse/ansman/dagger/auto/compiler/common/Processor;)V", "annotations", "", "", "process", "elementsByAnnotation", "Lcom/google/common/collect/ImmutableSetMultimap;", "compiler"})
public final class AutoDaggerProcessorStep implements com.google.auto.common.BasicAnnotationProcessor.Step {
    @org.jetbrains.annotations.NotNull()
    private final se.ansman.dagger.auto.compiler.common.kapt.processing.KaptEnvironment environment = null;
    @org.jetbrains.annotations.NotNull()
    private final se.ansman.dagger.auto.compiler.common.Processor<javax.lang.model.element.Element, com.squareup.javapoet.TypeName, com.squareup.javapoet.ClassName, com.squareup.javapoet.AnnotationSpec> processor = null;
    
    public AutoDaggerProcessorStep(@org.jetbrains.annotations.NotNull()
    se.ansman.dagger.auto.compiler.common.kapt.processing.KaptEnvironment environment, @org.jetbrains.annotations.NotNull()
    se.ansman.dagger.auto.compiler.common.Processor<javax.lang.model.element.Element, com.squareup.javapoet.TypeName, com.squareup.javapoet.ClassName, com.squareup.javapoet.AnnotationSpec> processor) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.util.Set<java.lang.String> annotations() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.util.Set<javax.lang.model.element.Element> process(@org.jetbrains.annotations.NotNull()
    com.google.common.collect.ImmutableSetMultimap<java.lang.String, javax.lang.model.element.Element> elementsByAnnotation) {
        return null;
    }
}
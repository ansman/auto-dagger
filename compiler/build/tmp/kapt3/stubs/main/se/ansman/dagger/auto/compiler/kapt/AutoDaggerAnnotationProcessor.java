package se.ansman.dagger.auto.compiler.kapt;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u001c\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u0016J\b\u0010\u0006\u001a\u00020\u0007H\u0016J\u000e\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u0014\u00a8\u0006\u000b"}, d2 = {"Lse/ansman/dagger/auto/compiler/kapt/AutoDaggerAnnotationProcessor;", "Lcom/google/auto/common/BasicAnnotationProcessor;", "()V", "getSupportedOptions", "", "", "getSupportedSourceVersion", "Ljavax/lang/model/SourceVersion;", "steps", "", "Lcom/google/auto/common/BasicAnnotationProcessor$Step;", "compiler"})
@com.google.auto.service.AutoService(value = {javax.annotation.processing.Processor.class})
@net.ltgt.gradle.incap.IncrementalAnnotationProcessor(value = net.ltgt.gradle.incap.IncrementalAnnotationProcessorType.ISOLATING)
public final class AutoDaggerAnnotationProcessor extends com.google.auto.common.BasicAnnotationProcessor {
    
    public AutoDaggerAnnotationProcessor() {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public javax.lang.model.SourceVersion getSupportedSourceVersion() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.util.Set<java.lang.String> getSupportedOptions() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    protected java.lang.Iterable<com.google.auto.common.BasicAnnotationProcessor.Step> steps() {
        return null;
    }
}
package se.ansman.dagger.auto.compiler.autobind.models;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0018\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u001e\n\u0000\b\u0086\b\u0018\u0000*\u0006\b\u0000\u0010\u0001 \u0001*\u0004\b\u0001\u0010\u0002*\b\b\u0002\u0010\u0003*\u0002H\u0002*\u0004\b\u0003\u0010\u00042\u000e\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u0002H\u00030\u0005B]\u0012\u0006\u0010\u0006\u001a\u00028\u0002\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00028\u00020\b\u0012\u0006\u0010\t\u001a\u00028\u0002\u0012\u0006\u0010\n\u001a\u00028\u0000\u0012\u0006\u0010\u000b\u001a\u00028\u0002\u0012\u0006\u0010\f\u001a\u00020\r\u0012\u0006\u0010\u000e\u001a\u00020\r\u0012\u0018\u0010\u000f\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u00030\u00110\u0010\u00a2\u0006\u0002\u0010\u0012J\u000e\u0010\u001e\u001a\u00028\u0002H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0019J\u000f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00028\u00020\bH\u00c6\u0003J\u000e\u0010 \u001a\u00028\u0002H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0019J\u000e\u0010!\u001a\u00028\u0000H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0019J\u000e\u0010\"\u001a\u00028\u0002H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0019J\t\u0010#\u001a\u00020\rH\u00c6\u0003J\t\u0010$\u001a\u00020\rH\u00c6\u0003J\u001b\u0010%\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u00030\u00110\u0010H\u00c6\u0003J\u008e\u0001\u0010&\u001a\u001a\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u00030\u00002\b\b\u0002\u0010\u0006\u001a\u00028\u00022\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00028\u00020\b2\b\b\u0002\u0010\t\u001a\u00028\u00022\b\b\u0002\u0010\n\u001a\u00028\u00002\b\b\u0002\u0010\u000b\u001a\u00028\u00022\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\r2\u001a\b\u0002\u0010\u000f\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u00030\u00110\u0010H\u00c6\u0001\u00a2\u0006\u0002\u0010\'J\u0013\u0010(\u001a\u00020\r2\b\u0010)\u001a\u0004\u0018\u00010*H\u00d6\u0003J\t\u0010+\u001a\u00020,H\u00d6\u0001J\t\u0010-\u001a\u00020.H\u00d6\u0001J8\u0010/\u001a\u001a\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u0002\u0012\u0004\u0012\u00028\u00030\u00002\u0018\u00100\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u00030\u001101R#\u0010\u000f\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00028\u0001\u0012\u0004\u0012\u00028\u00030\u00110\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u001a\u0010\u0007\u001a\b\u0012\u0004\u0012\u00028\u00020\bX\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u000e\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u0017R\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u0017R\u0016\u0010\u0006\u001a\u00028\u0002X\u0096\u0004\u00a2\u0006\n\n\u0002\u0010\u001a\u001a\u0004\b\u0018\u0010\u0019R\u0016\u0010\n\u001a\u00028\u0000X\u0096\u0004\u00a2\u0006\n\n\u0002\u0010\u001a\u001a\u0004\b\u001b\u0010\u0019R\u0016\u0010\t\u001a\u00028\u0002X\u0096\u0004\u00a2\u0006\n\n\u0002\u0010\u001a\u001a\u0004\b\u001c\u0010\u0019R\u0013\u0010\u000b\u001a\u00028\u0002\u00a2\u0006\n\n\u0002\u0010\u001a\u001a\u0004\b\u001d\u0010\u0019\u00a8\u00062"}, d2 = {"Lse/ansman/dagger/auto/compiler/autobind/models/AutoBindObjectModule;", "Node", "TypeName", "ClassName", "AnnotationSpec", "Lse/ansman/dagger/auto/compiler/common/models/HiltModule;", "moduleName", "installation", "Lse/ansman/dagger/auto/compiler/common/rendering/HiltModuleBuilder$Installation;", "originatingTopLevelClassName", "originatingElement", "type", "isPublic", "", "isObject", "boundTypes", "", "Lse/ansman/dagger/auto/compiler/autobind/models/AutoBindType;", "(Ljava/lang/Object;Lse/ansman/dagger/auto/compiler/common/rendering/HiltModuleBuilder$Installation;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;ZZLjava/util/List;)V", "getBoundTypes", "()Ljava/util/List;", "getInstallation", "()Lse/ansman/dagger/auto/compiler/common/rendering/HiltModuleBuilder$Installation;", "()Z", "getModuleName", "()Ljava/lang/Object;", "Ljava/lang/Object;", "getOriginatingElement", "getOriginatingTopLevelClassName", "getType", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "(Ljava/lang/Object;Lse/ansman/dagger/auto/compiler/common/rendering/HiltModuleBuilder$Installation;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;ZZLjava/util/List;)Lse/ansman/dagger/auto/compiler/autobind/models/AutoBindObjectModule;", "equals", "other", "", "hashCode", "", "toString", "", "withTypesAdded", "types", "", "compiler"})
public final class AutoBindObjectModule<Node extends java.lang.Object, TypeName extends java.lang.Object, ClassName extends TypeName, AnnotationSpec extends java.lang.Object> implements se.ansman.dagger.auto.compiler.common.models.HiltModule<Node, ClassName> {
    private final ClassName moduleName = null;
    @org.jetbrains.annotations.NotNull()
    private final se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder.Installation<ClassName> installation = null;
    private final ClassName originatingTopLevelClassName = null;
    private final Node originatingElement = null;
    private final ClassName type = null;
    private final boolean isPublic = false;
    private final boolean isObject = false;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<se.ansman.dagger.auto.compiler.autobind.models.AutoBindType<TypeName, AnnotationSpec>> boundTypes = null;
    
    public AutoBindObjectModule(ClassName moduleName, @org.jetbrains.annotations.NotNull()
    se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder.Installation<ClassName> installation, ClassName originatingTopLevelClassName, Node originatingElement, ClassName type, boolean isPublic, boolean isObject, @org.jetbrains.annotations.NotNull()
    java.util.List<se.ansman.dagger.auto.compiler.autobind.models.AutoBindType<TypeName, AnnotationSpec>> boundTypes) {
        super();
    }
    
    @java.lang.Override()
    public ClassName getModuleName() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder.Installation<ClassName> getInstallation() {
        return null;
    }
    
    @java.lang.Override()
    public ClassName getOriginatingTopLevelClassName() {
        return null;
    }
    
    @java.lang.Override()
    public Node getOriginatingElement() {
        return null;
    }
    
    public final ClassName getType() {
        return null;
    }
    
    public final boolean isPublic() {
        return false;
    }
    
    public final boolean isObject() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<se.ansman.dagger.auto.compiler.autobind.models.AutoBindType<TypeName, AnnotationSpec>> getBoundTypes() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final se.ansman.dagger.auto.compiler.autobind.models.AutoBindObjectModule<Node, TypeName, ClassName, AnnotationSpec> withTypesAdded(@org.jetbrains.annotations.NotNull()
    java.util.Collection<se.ansman.dagger.auto.compiler.autobind.models.AutoBindType<TypeName, AnnotationSpec>> types) {
        return null;
    }
    
    public final ClassName component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder.Installation<ClassName> component2() {
        return null;
    }
    
    public final ClassName component3() {
        return null;
    }
    
    public final Node component4() {
        return null;
    }
    
    public final ClassName component5() {
        return null;
    }
    
    public final boolean component6() {
        return false;
    }
    
    public final boolean component7() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<se.ansman.dagger.auto.compiler.autobind.models.AutoBindType<TypeName, AnnotationSpec>> component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final se.ansman.dagger.auto.compiler.autobind.models.AutoBindObjectModule<Node, TypeName, ClassName, AnnotationSpec> copy(ClassName moduleName, @org.jetbrains.annotations.NotNull()
    se.ansman.dagger.auto.compiler.common.rendering.HiltModuleBuilder.Installation<ClassName> installation, ClassName originatingTopLevelClassName, Node originatingElement, ClassName type, boolean isPublic, boolean isObject, @org.jetbrains.annotations.NotNull()
    java.util.List<se.ansman.dagger.auto.compiler.autobind.models.AutoBindType<TypeName, AnnotationSpec>> boundTypes) {
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
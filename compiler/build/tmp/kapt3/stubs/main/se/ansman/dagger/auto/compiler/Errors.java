package se.ansman.dagger.auto.compiler;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u001b\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0005\n\u000b\f\r\u000eB\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0004J\u0016\u0010\u0003\u001a\u00020\u00042\u000e\u0010\u0005\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00070\u0006J\u000e\u0010\b\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\u0004\u00a8\u0006\u000f"}, d2 = {"Lse/ansman/dagger/auto/compiler/Errors;", "", "()V", "genericType", "", "annotation", "Lkotlin/reflect/KClass;", "", "invalidComponent", "component", "AndroidX", "AutoBind", "AutoInitialize", "Replaces", "Retrofit", "compiler"})
public final class Errors {
    @org.jetbrains.annotations.NotNull()
    public static final se.ansman.dagger.auto.compiler.Errors INSTANCE = null;
    
    private Errors() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String genericType(@org.jetbrains.annotations.NotNull()
    kotlin.reflect.KClass<? extends java.lang.annotation.Annotation> annotation) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String genericType(@org.jetbrains.annotations.NotNull()
    java.lang.String annotation) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String invalidComponent(@org.jetbrains.annotations.NotNull()
    java.lang.String component) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\u0003B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0004"}, d2 = {"Lse/ansman/dagger/auto/compiler/Errors$AndroidX;", "", "()V", "Room", "compiler"})
    public static final class AndroidX {
        @org.jetbrains.annotations.NotNull()
        public static final se.ansman.dagger.auto.compiler.Errors.AndroidX INSTANCE = null;
        
        private AndroidX() {
            super();
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lse/ansman/dagger/auto/compiler/Errors$AndroidX$Room;", "", "()V", "notADatabase", "", "typeMustDirectlyExtendRoomDatabase", "compiler"})
        public static final class Room {
            @org.jetbrains.annotations.NotNull()
            public static final java.lang.String notADatabase = "Types annotated @AutoProvideDao must be annotated with @Database and directly extend RoomDatabase.";
            @org.jetbrains.annotations.NotNull()
            public static final java.lang.String typeMustDirectlyExtendRoomDatabase = "Indirect inheritance of RoomDatabase is not supported right now. If this is needed, open a feature request at https://github.com/ansman/auto-dagger/issues/new and explain your use case.";
            @org.jetbrains.annotations.NotNull()
            public static final se.ansman.dagger.auto.compiler.Errors.AndroidX.Room INSTANCE = null;
            
            private Room() {
                super();
            }
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\n\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\u0013B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\b\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\nJ\u000e\u0010\u000b\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\u0004J\u000e\u0010\r\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\u0004J\u000e\u0010\u000e\u001a\u00020\u00042\u0006\u0010\u000f\u001a\u00020\u0004J\u0016\u0010\u0010\u001a\u00020\u00042\u0006\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0012\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lse/ansman/dagger/auto/compiler/Errors$AutoBind;", "", "()V", "missingBindingKey", "", "multipleBindingKeys", "multipleSuperTypes", "noSuperTypes", "invalidBindMode", "bindGenericAs", "Lse/ansman/dagger/auto/BindGenericAs;", "missingBoundType", "boundType", "missingDirectSuperType", "nonStandardScope", "scope", "parentComponent", "installIn", "inferredComponent", "BindGenericAsDefault", "compiler"})
    public static final class AutoBind {
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String missingBindingKey = "To use @AutoBindIntoMap you must also annotate the type with a map key";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String multipleBindingKeys = "Multiple map keys specified, make sure there is only a single map key";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String noSuperTypes = "There are no supertypes so there is nothing to bind. Make sure you implement an interface or extend a class to use @AutoBind.";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String multipleSuperTypes = "Multiple supertypes found. Use the `asTypes` parameter to specify which types to bind";
        @org.jetbrains.annotations.NotNull()
        public static final se.ansman.dagger.auto.compiler.Errors.AutoBind INSTANCE = null;
        
        private AutoBind() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String missingBoundType(@org.jetbrains.annotations.NotNull()
        java.lang.String boundType) {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String missingDirectSuperType(@org.jetbrains.annotations.NotNull()
        java.lang.String boundType) {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String parentComponent(@org.jetbrains.annotations.NotNull()
        java.lang.String installIn, @org.jetbrains.annotations.NotNull()
        java.lang.String inferredComponent) {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String nonStandardScope(@org.jetbrains.annotations.NotNull()
        java.lang.String scope) {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String invalidBindMode(@org.jetbrains.annotations.NotNull()
        se.ansman.dagger.auto.BindGenericAs bindGenericAs) {
            return null;
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lse/ansman/dagger/auto/compiler/Errors$AutoBind$BindGenericAsDefault;", "", "()V", "nonAbstractType", "", "nonGenericType", "compiler"})
        public static final class BindGenericAsDefault {
            @org.jetbrains.annotations.NotNull()
            public static final java.lang.String nonGenericType = "@BindGenericAs.Default can only be applied to generic types.";
            @org.jetbrains.annotations.NotNull()
            public static final java.lang.String nonAbstractType = "@BindGenericAs.Default can only be applied to interfaces and abstract/open classes.";
            @org.jetbrains.annotations.NotNull()
            public static final se.ansman.dagger.auto.compiler.Errors.AutoBind.BindGenericAsDefault INSTANCE = null;
            
            private BindGenericAsDefault() {
                super();
            }
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lse/ansman/dagger/auto/compiler/Errors$AutoInitialize;", "", "()V", "invalidAnnotatedMethod", "", "methodInNonModule", "unscopedType", "wrongScope", "compiler"})
    public static final class AutoInitialize {
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String wrongScope = "Objects annotated with @AutoInitialize must also be annotated with @Singleton";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String unscopedType = "Objects annotated with @AutoInitialize must also be annotated with @Singleton";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String methodInNonModule = "@AutoInitialize annotated methods must be declared inside a @Module annotated class, object or interface.";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String invalidAnnotatedMethod = "Annotated methods must have either the @Provides or @Binds annotation";
        @org.jetbrains.annotations.NotNull()
        public static final se.ansman.dagger.auto.compiler.Errors.AutoInitialize INSTANCE = null;
        
        private AutoInitialize() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0006\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001e\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\u0004J\u000e\u0010\t\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Lse/ansman/dagger/auto/compiler/Errors$Replaces;", "", "()V", "isAutoBindOrInitialize", "", "missingBoundType", "replacedObject", "boundType", "type", "targetIsNotAutoBind", "compiler"})
    public static final class Replaces {
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String isAutoBindOrInitialize = "Objects annotated with @Replaces cannot also be annotated with @AutoInitialize, @AutoBind, @AutoBindIntoSet, or @AutoBindIntoMap";
        @org.jetbrains.annotations.NotNull()
        public static final se.ansman.dagger.auto.compiler.Errors.Replaces INSTANCE = null;
        
        private Replaces() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String missingBoundType(@org.jetbrains.annotations.NotNull()
        java.lang.String replacedObject, @org.jetbrains.annotations.NotNull()
        java.lang.String boundType, @org.jetbrains.annotations.NotNull()
        java.lang.String type) {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String targetIsNotAutoBind(@org.jetbrains.annotations.NotNull()
        java.lang.String replacedObject) {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\n\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001e\u0010\n\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lse/ansman/dagger/auto/compiler/Errors$Retrofit;", "", "()V", "emptyService", "", "invalidServiceMethod", "nonInterface", "privateType", "propertiesNotAllowed", "scopeAndReusable", "invalidScope", "scope", "component", "neededScope", "compiler"})
    public static final class Retrofit {
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String nonInterface = "Only interfaces can be annotated with @AutoProvideService.";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String privateType = "@AutoProvideService annotated types must not be private.";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String emptyService = "@AutoProvideService annotated types must have at least one method.";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String propertiesNotAllowed = "Retrofit services cannot contain properties.";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String invalidServiceMethod = "Methods in retrofit services must be annotated with a HTTP method annotation such as @GET.";
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String scopeAndReusable = "You cannot mix a scope and @Reusable on the same type. Remove the scope or @Reusable.";
        @org.jetbrains.annotations.NotNull()
        public static final se.ansman.dagger.auto.compiler.Errors.Retrofit INSTANCE = null;
        
        private Retrofit() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String invalidScope(@org.jetbrains.annotations.NotNull()
        java.lang.String scope, @org.jetbrains.annotations.NotNull()
        java.lang.String component, @org.jetbrains.annotations.NotNull()
        java.lang.String neededScope) {
            return null;
        }
    }
}
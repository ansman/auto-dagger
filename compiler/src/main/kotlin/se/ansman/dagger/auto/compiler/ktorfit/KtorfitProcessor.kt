package se.ansman.dagger.auto.compiler.ktorfit

import se.ansman.dagger.auto.compiler.Errors
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerEnvironment
import se.ansman.dagger.auto.compiler.ktorfit.renderer.KtorfitModuleRenderer
import se.ansman.dagger.auto.compiler.retrofit.BaseApiServiceProcessor
import se.ansman.dagger.auto.ktorfit.AutoProvideService

class KtorfitProcessor<N, TypeName, ClassName : TypeName, AnnotationSpec, F>(
    environment: AutoDaggerEnvironment<N, TypeName, ClassName, AnnotationSpec, F>,
    renderer: KtorfitModuleRenderer<N, TypeName, ClassName, AnnotationSpec, *, *, F>,
) : BaseApiServiceProcessor<N, TypeName, ClassName, AnnotationSpec, F>(
    environment = environment,
    renderer = renderer,
    annotation = AutoProvideService::class,
    serviceAnnotations = setOf(
        "de.jensklingenberg.ktorfit.http.DELETE",
        "de.jensklingenberg.ktorfit.http.GET",
        "de.jensklingenberg.ktorfit.http.HEAD",
        "de.jensklingenberg.ktorfit.http.HTTP",
        "de.jensklingenberg.ktorfit.http.OPTIONS",
        "de.jensklingenberg.ktorfit.http.PATCH",
        "de.jensklingenberg.ktorfit.http.POST",
        "de.jensklingenberg.ktorfit.http.PUT",
    ),
    modulePrefix = "AutoBindKtorfit",
    logger = environment.logger.withTag("ktorfit"),
    errors = Errors.Ktorfit,
)

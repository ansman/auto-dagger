package se.ansman.dagger.auto.compiler.retrofit

import se.ansman.dagger.auto.compiler.Errors
import se.ansman.dagger.auto.compiler.common.processing.AutoDaggerEnvironment
import se.ansman.dagger.auto.compiler.retrofit.renderer.RetrofitModuleRenderer
import se.ansman.dagger.auto.retrofit.AutoProvideService

class RetrofitProcessor<N, TypeName, ClassName : TypeName, AnnotationSpec, F>(
    environment: AutoDaggerEnvironment<N, TypeName, ClassName, AnnotationSpec, F>,
    renderer: RetrofitModuleRenderer<N, TypeName, ClassName, AnnotationSpec, *, *, F>,
) : BaseApiServiceProcessor<N, TypeName, ClassName, AnnotationSpec, F>(
    environment = environment,
    renderer = renderer,
    annotation = AutoProvideService::class,
    serviceAnnotations = setOf(
        "retrofit2.http.DELETE",
        "retrofit2.http.GET",
        "retrofit2.http.HEAD",
        "retrofit2.http.HTTP",
        "retrofit2.http.OPTIONS",
        "retrofit2.http.PATCH",
        "retrofit2.http.POST",
        "retrofit2.http.PUT",
    ),
    modulePrefix = "AutoBindRetrofit",
    logger = environment.logger.withTag("retrofit"),
    errors = Errors.Retrofit,
)

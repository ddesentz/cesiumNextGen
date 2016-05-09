import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler
import services.cableconfig.cableConfigService
import services.visualization.visualizationService



fun main(args: Array<String>) {

    if (!Options.parse(args)) return

    var vertx = Vertx.vertx()

    var router = Router.router(vertx)
    router.route(Options.configRoot).handler { cableConfigService(it) }
    router.route(Options.displayRoot).handler { visualizationService(vertx, it) }
    router.route(Options.staticRoot).handler(StaticHandler.create(Options.staticFolder))

    vertx.createHttpServer()
            .requestHandler { router.accept(it) }
            .listen(Options.port)

    vertx.deployVerticle(Options.dataHandler,
                         DeploymentOptions().setWorker(true))
}

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler
import services.cableconfig.cableConfigService
import services.cablestatus.cableStatusService
import services.visualization.visualizationService


fun main(args: Array<String>) {

    if (!Options.parse(args)) return

    var vertx = Vertx.vertx()

    var router = Router.router(vertx)

    var staticHandler = StaticHandler.create().setDirectoryListing(true)

    var topics = Options.dataHandler.topics

    router.route(Options.configRoot).handler { cableConfigService(it) }
    router.route(Options.statusRoot).handler { cableStatusService(it) }
    router.route(Options.displayRoot).handler { visualizationService(vertx, it, topics) }
    router.route("/*").handler(staticHandler)

    vertx.createHttpServer()
            .requestHandler { router.accept(it) }
            .listen(Options.port)

    vertx.deployVerticle(Options.dataHandler,
                         DeploymentOptions().setWorker(true))
}

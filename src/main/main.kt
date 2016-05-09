import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler
import services.cableconfig.cableConfigService
import services.visualization.visualizationService



fun main(args: Array<String>) {

    if (!Options.parse(args)) return

    var vertx = Vertx.vertx()

    var router = Router.router(vertx)
    router.route(Options.configroot).handler { cableConfigService(it) }
    router.route(Options.displayroot).handler { visualizationService(vertx, it) }
    router.route(Options.staticroot).handler(StaticHandler.create())

    vertx.createHttpServer()
            .requestHandler { router.accept(it) }
            .listen(Options.port)

    Options.dataHandler.deploy(vertx)

}

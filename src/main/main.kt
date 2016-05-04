import datasources.dummy.DummyVerticle
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler
import services.cableconfig.cableConfigService
import services.visualization.visualizationService

val PORT = 8999
val CONFIGROOT = "/config"
val DISPLAYROOT = "/visualization/*"
val STATICROOT = "/*"

fun main(args: Array<String>) {

    var vertx = Vertx.vertx()

    var router = Router.router(vertx)
    router.route(CONFIGROOT).handler { cableConfigService(it) }
    router.route(DISPLAYROOT).handler { visualizationService(vertx, it) }
    router.route(STATICROOT).handler(StaticHandler.create())

    vertx.createHttpServer()
            .requestHandler { router.accept(it) }
            .listen(PORT)

    DummyVerticle().deploy(vertx)

}

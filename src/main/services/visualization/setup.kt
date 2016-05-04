package services.visualization

import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.sockjs.BridgeOptions
import io.vertx.ext.web.handler.sockjs.PermittedOptions
import io.vertx.ext.web.handler.sockjs.SockJSHandler
import kotlin.concurrent.thread

fun visualizationService(vertx: Vertx, route: RoutingContext) {
    var options = BridgeOptions()
            .addOutboundPermitted(PermittedOptions().setAddress("pose-ownship"))

    var handler = SockJSHandler
            .create(vertx)
            .bridge(options)

    handler.handle(route)
}
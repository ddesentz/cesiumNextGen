package services.visualization

import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.sockjs.BridgeOptions
import io.vertx.ext.web.handler.sockjs.PermittedOptions
import io.vertx.ext.web.handler.sockjs.SockJSHandler

val TOPIC_NAMES = arrayOf("pose-ownship", "pose-ownship-truth","next")

fun visualizationService(vertx: Vertx, route: RoutingContext) {
    var options = BridgeOptions()
    TOPIC_NAMES.forEach { options.addOutboundPermitted(PermittedOptions().setAddress(it)) }

    var handler = SockJSHandler
            .create(vertx)
            .bridge(options)

    handler.handle(route)
}
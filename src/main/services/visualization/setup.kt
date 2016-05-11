package services.visualization

import golem.util.logging.*
import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.sockjs.BridgeOptions
import io.vertx.ext.web.handler.sockjs.PermittedOptions
import io.vertx.ext.web.handler.sockjs.SockJSHandler

fun visualizationService(vertx: Vertx, route: RoutingContext, topicList: List<String>) {
    var options = BridgeOptions()
    ::visualizationService.log { "Giving outbound eventbus permissions for $topicList" }
    topicList.forEach { options.addOutboundPermitted(PermittedOptions().setAddress(it)) }

    var handler = SockJSHandler
            .create(vertx)
            .bridge(options)

    handler.handle(route)
}
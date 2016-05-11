package datasources.base

import golem.util.logging.*
import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

abstract class DataSourceVerticle: AbstractVerticle() {
    abstract var topics: List<String>
    protected fun listenWebSocket(topics: List<String>, listenTopic: String) {
        vertx.eventBus().consumer<JsonObject>(Options.cesiumTopic) {
            log { "Sending cesium topic list to subscribe to: $topics" }
            it.reply(JsonArray(topics))
        }
    }
}

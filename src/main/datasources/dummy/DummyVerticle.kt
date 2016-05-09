package datasources.dummy

import golem.util.logging.*
import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import org.slf4j.event.Level
import kotlin.concurrent.timer

class DummyVerticle : AbstractVerticle() {
    override fun start() {
        this.log(Level.DEBUG) { "Dummy running on thread ${Thread.currentThread().name}" }
        var movement = 0.0
        var error = 0.0
        var offset = .000001
        // Fakeout data for testing purposes
        timer(period = 1000) {
            var updateEst = JsonObject()
                    .put("pos", JsonArray(arrayListOf(0.6949654640951038,
                                                      -1.4669223582307533 + movement,
                                                      2000)))
                    .put("vel", JsonArray(arrayListOf(0.01, -0.5, 55.1)))
                    .put("rot", JsonArray(arrayListOf(66.7, -22.2, 12321.1)))
            var updateTruth = JsonObject()
                    .put("pos", JsonArray(arrayListOf(0.6949654640951038 + error + offset,
                                                      -1.4669223582307533 + movement + 2 * offset,
                                                      2000)))
                    .put("vel", JsonArray(arrayListOf(0.01, -0.5, 55.1)))
                    .put("rot", JsonArray(arrayListOf(66.7, -22.2, 12321.1)))

            movement += .00001
            error += .00000001

            this.log(Level.DEBUG) { "Dummy dumping to bus: $updateEst\n,\n$updateTruth" }

            vertx.eventBus().publish("pose-ownship", updateEst)
            vertx.eventBus().publish("pose-ownship-truth", updateTruth)
        }
    }
}

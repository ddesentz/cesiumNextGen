package datasources.dummy

import datasources.base.DeployableVerticle
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import kotlin.concurrent.timer

class DummyVerticle : DeployableVerticle() {
    // Don't need a separate thread since timer is async.
    override fun deploy(vertx: Vertx, isWorker: Boolean) {
        this.start()
        this.vertx = vertx
    }

    override fun start() {
        println("Dummy running on thread ${Thread.currentThread().name}")
        var additive = 0.0
        // Fakeout data for testing purposes
        timer(period = 1000) {
            var update = JsonObject()
                    .put("pos", JsonArray(arrayListOf(0.6949654640951038, -1.4669223582307533 + additive, 2000)))
                    .put("vel", JsonArray(arrayListOf(0.01, -0.5, 55.1)))
                    .put("rot", JsonArray(arrayListOf(66.7, -22.2, 12321.1)))

            additive += .00001

            var eb = vertx.eventBus().publish("pose-ownship", update)
        }
    }

}

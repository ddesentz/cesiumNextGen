package datasources.dummy

import datasources.base.DeployableVerticle
import golem.util.logging.*
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import org.slf4j.event.Level
import kotlin.concurrent.timer

class DummyVerticle : DeployableVerticle() {
    // Don't need a separate thread since timer is async.
    override fun deploy(vertx: Vertx, isWorker: Boolean) {
        this.start()
        this.vertx = vertx
    }

    override fun start() {
        this.log(Level.DEBUG) { "Dummy running on thread ${Thread.currentThread().name}" }
        var hstep = 0.0
        var xstep = 0.0
        var ystep = 0.0
        var lat = 39.7736667
        var long = -084.1072222
        var height = 2000
        var rotStep = 0.0
        // Fakeout data for testing purposes
        timer(period = 1000) {
            var updateEst = JsonObject()
                    .put("pos", JsonArray(arrayListOf(lat,
                             long + ystep,
                            height + hstep)))
                    .put("vel", JsonArray(arrayListOf(0.01 , -0.5, 55.1)))
                    .put("rot", JsonArray(arrayListOf(0 + rotStep,0,0)))

            var updateTruth = JsonObject()
                    .put("pos", JsonArray(arrayListOf(lat + ystep,
                            long + (2 * ystep),
                            height - hstep)))
                    .put("vel", JsonArray(arrayListOf(0.01, -0.5, 55.1)))
                    .put("rot", JsonArray(arrayListOf(0,0 + rotStep,0)))

            var updateExtra = JsonObject()
                    .put("pos", JsonArray(arrayListOf(lat - ystep,
                            long + (3 * ystep),
                            height + (1000 * hstep))))
                    .put("vel", JsonArray(arrayListOf(0.01, -0.5, 55.1)))
                    .put("rot", JsonArray(arrayListOf(0,0,0 + rotStep)))

            this.log(Level.DEBUG) { "Dummy dumping to bus: $updateEst\n,\n$updateTruth" }
            rotStep += .02
            hstep += .1
            xstep += .0001
            ystep += .01

            vertx.eventBus().publish("pose-ownship", updateEst)
            vertx.eventBus().publish("pose-ownship-truth", updateTruth)
            vertx.eventBus().publish("next", updateExtra)
        }
    }

}

package datasources.lcm

import datasources.base.DataSourceVerticle
import datasources.lcm.messages.aspn.navigationsolution
import golem.util.logging.*
import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import lcm.lcm.LCM
import org.slf4j.event.Level
import java.util.*


class LCMVerticle(var dataURI: String) : DataSourceVerticle() {
    // Lateinit shouldnt be required due to static initializer,
    // might be compiler bug
    lateinit override var topics: List<String>
    var uriType: String
    var uriAddress: String

    init {
        var out = splitURI()
        uriType = out.first
        uriAddress = out.second
        topics = out.third
    }

    override fun start() {


        when (uriType.toUpperCase()) {
            "LCMSOCKET" -> {
                listenLCMSocket(topics, uriAddress)
            }
            "LCMFILE"   -> {
                throw NotImplementedError()
            }
        }

        log { "Running on thread ${Thread.currentThread().name}" }
    }

    override fun stop() {
        var lcm = LCM.getSingleton()
        lcm.close()
    }

    /**
     * Listens to the socket defined by the LCM address [uriAddress] on the topics in
     * [topics].
     */
    private fun listenLCMSocket(topics: List<String>, uriAddress: String): LCM {
        var lcm = LCM(uriAddress)
        log { "Listening on $uriAddress" }
        topics.forEach {
            lcm.subscribe(it) { lcm, name, data ->
                log { "Received on $name" }
                var navSoln = navigationsolution(data)
                log {
                    asYaml("Message content: NavSoln",
                           "lat" to navSoln.latitude,
                           "lon" to navSoln.longitude,
                           "alt" to navSoln.altitude)
                }

                var out = navSolToJson(navSoln)
                this.log(Level.DEBUG) { "Dumping to event-bus: $it" }
                vertx.eventBus().publish(it, out)
            }
        }
        return lcm
    }

    private fun navSolToJson(navSoln: navigationsolution): JsonObject {
        return JsonObject()
                .put("pos", JsonArray(arrayListOf(navSoln.latitude,
                                                  navSoln.longitude,
                                                  navSoln.altitude)))
                .put("vel", JsonArray(navSoln.velocity.asList()))
                .put("rot", JsonArray(navSoln.rotation.asList()))

    }

    private fun splitURI(): Triple<String, String, List<String>> {
        try {
            var (uriType, uriAddress, topics) = dataURI.split(delimiters = "#",
                                                              limit = 3,
                                                              ignoreCase = true)
            return Triple(uriType, uriAddress, topics.split(","))

        } catch(e: IndexOutOfBoundsException) {
            log(Level.ERROR) { "Incorrectly formatted LCM URI received: '$dataURI'" }
            vertx.close()
            throw(IllegalArgumentException("Incorrectly formatted LCM URI received: '$dataURI'"))
        }
    }

}
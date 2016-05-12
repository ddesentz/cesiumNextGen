package datasources.lcm

import Options
import datasources.base.DataSourceVerticle
import datasources.lcm.messages.aspn.geodeticposition3d
import datasources.lcm.messages.aspn.navigationsolution
import golem.util.logging.*
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import lcm.lcm.LCM
import lcm.lcm.LCMDataInputStream
import org.slf4j.event.Level
import kotlin.collections.MutableMap

class LCMVerticle(var dataURI: String) : DataSourceVerticle() {
    // Lateinit shouldnt be required due to static initializer,
    // might be compiler bug
    lateinit override var topics: List<String>

    var topicMap: MutableMap<String, String>
    var uriType: String
    var uriAddress: String

    init {
        var out = splitURI()
        uriType = out.first
        uriAddress = out.second
        topicMap = out.third

        topics = topicMap.keys.toList()
    }

    override fun start() {
        listenLCMSocket(topics, uriAddress)
        listenWebSocket(topics, Options.cesiumTopic)
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
        log { "Listening on ${if (uriAddress == "") "<lcm default topic ()>" else uriAddress}" }
        topics.forEach {
            lcm.subscribe(it) { lcm, topicName, data ->
                log { "Received on $topicName" }
                var navSoln = lcmMessageToJson(topicName, data)

                this.log(Level.DEBUG) { "Dumping to event-bus: $it" }
                vertx.eventBus().publish(it, navSoln)
            }
        }

        return lcm
    }

    private fun lcmMessageToJson(topicName: String, dis: LCMDataInputStream): JsonObject {
        return when(topicMap[topicName]) {
            "navigationsolution"->{navSolToJson(navigationsolution(dis))}
            "geodeticposition3d"->{geoPos3DToJson(geodeticposition3d(dis))}
            else -> {throw IllegalArgumentException("Unknown message type for $topicName: ${topicMap[topicName]}")}
        }
    }

    private fun navSolToJson(msg: navigationsolution): JsonObject {
        log {
            asYaml("Message content: NavSoln",
                   "lat" to msg.latitude,
                   "lon" to msg.longitude,
                   "alt" to msg.altitude)
        }
        return JsonObject()
                .put("pos", JsonArray(arrayListOf(msg.latitude,
                                                  msg.longitude,
                                                  msg.altitude)))
                .put("vel", JsonArray(msg.velocity.asList()))
                .put("rot", JsonArray(msg.rotation.asList()))
    }

    private fun geoPos3DToJson(msg: geodeticposition3d): JsonObject {
        log {
            asYaml("Message content: NavSoln",
                   "lat" to msg.latitude,
                   "lon" to msg.longitude,
                   "alt" to msg.altitude)
        }
        return JsonObject()
                .put("pos", JsonArray(arrayListOf(msg.latitude,
                                                  msg.longitude,
                                                  msg.altitude)))

    }

    private fun splitURI(): Triple<String, String, MutableMap<String,String>> {
        try {
            var (uriType, uriAddress, topics) = dataURI.split(delimiters = "#",
                                                              limit = 3,
                                                              ignoreCase = true)
            var topicHash = mutableMapOf<String, String>()
            topics.split(",").forEach {
                var keyValPair = it.split("=")
                if (keyValPair.size==2)
                    topicHash[keyValPair[0]]=keyValPair[1]
                else
                    topicHash[keyValPair[0]]="navigationsolution"
            }
            return Triple(uriType, uriAddress, topicHash)

        } catch(e: IndexOutOfBoundsException) {
            log(Level.ERROR) { "Incorrectly formatted LCM URI received: '$dataURI'" }
            vertx.close()
            throw(IllegalArgumentException("Incorrectly formatted LCM URI received: '$dataURI'"))
        }
    }

}
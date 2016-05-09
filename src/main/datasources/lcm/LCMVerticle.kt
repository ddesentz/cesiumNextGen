package datasources.lcm

import datasources.lcm.messages.aspn.navigationsolution
import golem.util.logging.*
import io.vertx.core.AbstractVerticle
import lcm.lcm.LCM
import lcm.lcm.LCMDataInputStream
import lcm.lcm.LCMSubscriber
import org.slf4j.event.Level


class LCMVerticle(var dataURI: String) : AbstractVerticle(), LCMSubscriber {

    override fun messageReceived(lcmInstance: LCM, channel: String, dis: LCMDataInputStream) {
        this.log { "LCM receiving on thread ${Thread.currentThread().name}" }
    }

    override fun start() {

        var (uriType, uriAddress, topics) = splitURI()

        when (uriType.toUpperCase()) {
            "LCMSOCKET" -> {
                listenLCMSocket(topics, uriAddress)
            }
            "LCMFILE"   -> {
                throw NotImplementedError()
            }
        }

        log { "LCM verticle running on thread ${Thread.currentThread().name}" }
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
        log { "LCM listening on $uriAddress" }
        topics.forEach {
            lcm.subscribe(it) { lcm, name, data ->
                log { "Received on $name" }
                var navSoln = navigationsolution(data)
                log {
                    asYaml("LCMVerticle_NavSoln",
                           "lat" to navSoln.latitude,
                           "lon" to navSoln.longitude,
                           "alt" to navSoln.altitude)
                }
            }
        }
        return lcm
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
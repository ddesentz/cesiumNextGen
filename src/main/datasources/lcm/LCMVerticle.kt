package datasources.lcm

import datasources.lcm.messages.aspn.navigationsolution
import golem.util.logging.*
import io.vertx.core.AbstractVerticle
import lcm.lcm.LCM
import lcm.lcm.LCMDataInputStream
import lcm.lcm.LCMSubscriber
import org.slf4j.event.Level


class LCMVerticle(var dataURI: String) : AbstractVerticle(), LCMSubscriber {

    //todo: validation of URI

    override fun messageReceived(lcmInstance: LCM, channel: String, dis: LCMDataInputStream) {
        this.log(Level.DEBUG) { "LCM receiving on thread ${Thread.currentThread().name}" }
    }

    override fun start() {
        var lcm: LCM
        var (uriType, uriAddress, topics) = dataURI.split(delimiters = "#",
                                                          limit = 3,
                                                          ignoreCase = true)

        when (uriType.toUpperCase()) {
            "LCMSOCKET" -> {
                lcm = LCM(uriAddress)
                log { "LCM listening on $uriAddress" }
                topics.split(",").forEach {
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
            }
        }

        this.log(Level.DEBUG) { "LCM verticle running on thread ${Thread.currentThread().name}" }
    }

    override fun stop() {
        var lcm = LCM.getSingleton()
        lcm.close()
    }
}
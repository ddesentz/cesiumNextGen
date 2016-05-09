package datasources.lcm

import datasources.base.DataSourceVerticle
import golem.util.logging.*
import lcm.lcm.LCM
import lcm.lcm.LCMDataInputStream
import lcm.lcm.LCMSubscriber
import org.slf4j.event.Level


class LCMVerticle(var dataURI: String) : DataSourceVerticle(), LCMSubscriber {
    override fun messageReceived(lcmInstance: LCM, channel: String, dis: LCMDataInputStream) {
        this.log(Level.DEBUG) { "LCM receiving on thread ${Thread.currentThread().name}" }
    }

    override fun start() {
        var lcm = LCM.getSingleton()

        this.log(Level.DEBUG) { "LCM running on thread ${Thread.currentThread().name}" }
    }
}
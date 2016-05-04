package datasources.lcm

import datasources.base.DeployableVerticle
import golem.util.logging.*
import org.slf4j.event.Level


class LCMVerticle : DeployableVerticle() {
    override fun start() {
        this.log(Level.DEBUG) { "LCM running on thread ${Thread.currentThread().name}" }
    }
}
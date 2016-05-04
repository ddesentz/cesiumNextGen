package datasources.lcm

import datasources.base.DeployableVerticle
import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx


class LCMVerticle : DeployableVerticle() {
    override fun start() {
        println("LCM running on thread ${Thread.currentThread().name}")
    }
}
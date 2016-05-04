package datasources.base

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx

abstract class DeployableVerticle : AbstractVerticle() {
    open fun deploy(vertx: Vertx, isWorker: Boolean = true) {
        vertx.deployVerticle(this, DeploymentOptions().setWorker(isWorker))
    }
}

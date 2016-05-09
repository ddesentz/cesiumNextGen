package datasources.base

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx

/**
 * A verticle that can be started by calling deploy. By default, this method
 * Deploys the Verticle via vertx.
 */
abstract class DataSourceVerticle : AbstractVerticle() {
    open fun deploy(vertx: Vertx, isWorker: Boolean = true) {
        vertx.deployVerticle(this, DeploymentOptions().setWorker(isWorker))
    }
}

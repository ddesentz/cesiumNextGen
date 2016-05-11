package datasources.base

import io.vertx.core.AbstractVerticle

abstract class DataSourceVerticle: AbstractVerticle() {
    abstract var topics: List<String>

}

package datasources.hdf

import datasources.base.DeployableVerticle
import golem.util.logging.*
import org.slf4j.event.Level

class HDFVerticle : DeployableVerticle() {

    override fun start() {
        this.log(Level.DEBUG) { "HDF running on thread ${Thread.currentThread().name}" }
    }

}
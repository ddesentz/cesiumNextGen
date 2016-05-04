package datasources.hdf

import datasources.base.DeployableVerticle

class HDFVerticle : DeployableVerticle() {

    override fun start() {
        println("HDF running on thread ${Thread.currentThread().name}")
    }

}
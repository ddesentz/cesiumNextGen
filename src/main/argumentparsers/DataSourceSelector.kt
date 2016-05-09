package argumentparsers

import com.beust.jcommander.IStringConverter
import com.beust.jcommander.ParameterException
import datasources.dummy.DummyVerticle
import datasources.lcm.LCMVerticle
import io.vertx.core.AbstractVerticle

class DataSourceSelector : IStringConverter<AbstractVerticle> {
    override fun convert(value: String): AbstractVerticle {
        return value.run {
            when {
                equals("Dummy")         -> {DummyVerticle()}
                startsWith("LCMSocket") -> {LCMVerticle(this)}
                startsWith("LCMURL")    -> {LCMVerticle(this)}
                else                    -> {throw ParameterException("Unknown data source name")}
            }
        }
    }
}
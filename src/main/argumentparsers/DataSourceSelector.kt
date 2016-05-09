package argumentparsers

import com.beust.jcommander.IStringConverter
import com.beust.jcommander.ParameterException
import datasources.base.DataSourceVerticle
import datasources.dummy.DummyVerticle
import datasources.lcm.LCMVerticle

class DataSourceSelector : IStringConverter<DataSourceVerticle> {
    override fun convert(value: String): DataSourceVerticle {
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
import argumentparsers.DataSourceSelector
import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import com.beust.jcommander.ParameterException
import datasources.dummy.DummyVerticle
import golem.util.logging.*
import io.vertx.core.AbstractVerticle
import org.slf4j.event.Level

object Options {

    @Parameter(names = arrayOf("-p", "--port"),
               description = "Port to listen on")
    var port = 8999

    @Parameter(names = arrayOf("-croot", "--configroot"),
               description = "Relative URL where configuration REST service exists")
    var configroot = "/config*"

    @Parameter(names = arrayOf("-droot", "--displayroot"),
               description = "Relative URL where the visualization display service exists")
    var displayroot = "/visualization/*"

    @Parameter(names = arrayOf("-sroot", "--staticroot"),
               description = "Relative URL where static HTML pages are served from")
    var staticroot = "/*"

    @Parameter(names = arrayOf("-h", "--help"),
               help = true,
               description = "Shows this help information")
    var help = false

    @Parameter(names = arrayOf("-dsource", "--datasource"),
               converter = DataSourceSelector::class,
               description = "The data source, one of 'Dummy', 'LCMSocket:<URL>', or 'LCMFile:<filename>'")
    var dataHandler: AbstractVerticle = DummyVerticle()

    fun parse(args: Array<String>): Boolean {
        try {

            val commander = JCommander(Options, *args)
            commander.setProgramName("java -jar <jar file>")
            if (Options.help) {
                commander.usage()
                return false
            }
        } catch(e: ParameterException) {
            // Output a debug log in case this is being run in a batch.
            this.log(Level.DEBUG) {e.message ?: "Error parsing arguments: ${e.stackTrace}"}
            println("${e.message}.")
            JCommander(Options).usage()
            return false
        }
        return true
    }
}
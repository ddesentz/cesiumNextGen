import argumentparsers.DataSourceSelector
import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import com.beust.jcommander.ParameterException
import com.beust.jcommander.Parameters
import datasources.dummy.DummyVerticle
import golem.util.logging.*
import io.vertx.core.AbstractVerticle

@Parameters(separators = "= :")
object Options {

    @Parameter(names = arrayOf("-p", "--port"),
               description = "Port to listen on")
    var port = 8999

    @Parameter(names = arrayOf("-croot", "--configroot"),
               description = "Relative URL where configuration REST service exists")
    var configRoot = "/config*"

    @Parameter(names = arrayOf("-droot", "--displayroot"),
               description = "Relative URL where the visualization display service exists")
    var displayRoot = "/visualization/*"

    @Parameter(names = arrayOf("-sroot", "--staticroot"),
               description = "Relative URL where static HTML pages are served from")
    var staticRoot = "/*"

    @Parameter(names = arrayOf("-h", "--help"),
               help = true,
               description = "Shows this help information")
    var help = false

    @Parameter(names = arrayOf("-dsource", "--datasource"),
               converter = DataSourceSelector::class,
               description = "##FILLIN##") // Template filled in below due to compile time limitations

    var dataHandler: AbstractVerticle = DummyVerticle()

    @Parameter(names = arrayOf("-sfolder", "--staticfolder"),
               description = "The folder which static files are located (relative to classpath). " +
                             "Note that if the jar contains files in webroot/ that may be preferred over other " +
                             "locations, so use a different folder name for custom static files.")
    var staticFolder = "webroot"

    fun parse(args: Array<String>): Boolean {
        try {

            val commander = JCommander(Options, *args)
            if (Options.help) {
                trimmedUsage(commander)
                return false
            }
        } catch(e: ParameterException) {
            // Output a debug log in case this is being run in a batch.
            this.log { e.message ?: "Error parsing arguments: ${e.stackTrace}" }
            println("${e.message}.")
            trimmedUsage()


            return false
        }
        return true
    }

    private fun trimmedUsage(cmdr: JCommander = JCommander(Options)) {
        var output = StringBuilder()
        cmdr.setProgramName("java -jar <jar file>")
        cmdr.usage(output)
        var longDescr = """
            Specify the data source. By default, the server will
            serve fake data generated internally. To use an LCM
            source, specify a URI that is of the form:
              network sources: --datasource=LCMSocket#tcpq://my_lcm_url#topic1,topic2
              file sources:    --datasource=LCMFile#/path/to/file#topic1,topic2")
            Default: DummyVerticle
            """.replaceIndent("       ")
        println(output.replace(Regex("""\n.*##FILLIN##\n.*Default:.*"""), "\n" + longDescr))
    }
}
import argumentparsers.DataSourceSelector
import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import com.beust.jcommander.ParameterException
import com.beust.jcommander.Parameters
import datasources.base.DataSourceVerticle
import datasources.dummy.DummyVerticle
import golem.util.logging.*
import io.vertx.core.AbstractVerticle

@Parameters(separators = "= :")
object Options {

    val cesiumTopic = "cesium_topics"
    val configRoot = "/rest/config*"
    val statusRoot = "/rest/status/*"
    val displayRoot = "/services/visualize/*"

    @Parameter(names = arrayOf("-p", "--port"),
               description = "Port to listen on")
    var port = 8999

    @Parameter(names = arrayOf("-h", "--help"),
               help = true,
               description = "Shows this help information")
    var help = false

    @Parameter(names = arrayOf("-dsource", "--datasource"),
               converter = DataSourceSelector::class,
               description = "##FILLIN##") // Template filled in below due to compile time limitations

    var dataHandler: DataSourceVerticle = DummyVerticle()

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
              LCM#<lcm_url>#<topic_list>
            Where lcm_url is the URL you'd pass to the LCM library
            to connect to the bus and topic_list is a comma separated
            list of topics mapped to their message type. If lcm_url
            is empty, it will use the default LCM bus (create "LCM()").
            For example, to start a default tcp LCM bus that listens
            to topic1 and topic2 which have navsolution and
            geodeticposition3d message types respectively, we'd pass
              --datasource=LCM##topic1=navsolution,topic2=geodeticposition3d
            Default: DummyVerticle
            """.replaceIndent("       ")
        println(output.replace(Regex("""\n.*##FILLIN##\n.*Default:.*"""), "\n" + longDescr))
    }
}
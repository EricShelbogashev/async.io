import org.apache.commons.cli.*

abstract class ConfigLoader<T : Config>(
    private val appName: String,
    protected val options: Options
) {
    private val formatter = HelpFormatter()
    protected val parser: CommandLineParser = DefaultParser()

    fun load(args: Array<String>): T {
        val cmd: CommandLine = try {
            parser.parse(options, args)
        } catch (e: ParseException) {
            usage()
            throw e
        }
        return loadImpl(cmd)
    }

    abstract fun loadImpl(cmd: CommandLine): T

    protected fun usage() {
        formatter.printHelp(appName, options)
    }

    protected fun usage(message: String) {
        System.err.println(message)
        usage()
    }
}
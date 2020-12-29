package src

import com.digi.xbee.api.DigiMeshDevice
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options
import java.util.logging.Logger

// TODO: 12/26/20 Document all this
fun main(args: Array<String>) {

    val logger = Logger.getAnonymousLogger()

    logger.info("Parsing command line options...")

    val parser = DefaultParser()
    val cmd = parser.parse(getOptions(), args)

    val device = DigiMeshDevice(cmd.getOptionValue("dev"), cmd.getOptionValue("baud").toInt())
    val network = device.network

}

private fun getOptions(): Options {
    val options = Options()
    options.addOption("directory", true,"The working directory for TempleNet. For example, /home/templenet.")
    options.addRequiredOption("dev", "device", true,
        "The device port to use. On UNIX this would be /dev/ttyUSB*. On Windows it would be COM*.")
    options.addOption("baud", true,"The baud rate to use. By default, this is set to ???") // TODO: 12/21/20 whats dat baud

    return options
}

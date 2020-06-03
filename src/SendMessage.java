import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.DigiMeshNetwork;
import com.digi.xbee.api.RemoteDigiMeshDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.github.cliftonlabs.json_simple.JsonException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class SendMessage {
    public SendMessage(String message, RemoteDigiMeshDevice remoteDevice, DigiMeshNetwork network, DigiMeshDevice localDevice, String configfilesLocation){
        try {
                if (remoteDevice != null) {
                            //write message to message file
                            Process writemessagefile = Runtime.getRuntime().exec(new String[]{
                                    "bash", "-c", "echo '" + message + "' > " + configfilesLocation + "/msg"});
                            writemessagefile.waitFor();

                            //apend message to log file
                            Process appendtologfile = Runtime.getRuntime().exec(new String[]{
                                    "bash", "-c", "echo '" + localDevice.getNodeID() + ": " + message + "' >> " +
                                    configfilesLocation + "/" + remoteDevice.getNodeID()});
                            appendtologfile.waitFor();

                            //create keys from contact's generator
                            new GenKeys(1, remoteDevice.getNodeID(),configfilesLocation,configfilesLocation,configfilesLocation,configfilesLocation);

                            //Getting shared secret
                            new GetSharedSecret(remoteDevice.getNodeID(), configfilesLocation+"/"+remoteDevice.getNodeID()+"myprivatekey.pem", configfilesLocation+"/"+remoteDevice.getNodeID()+"publickey.pem");

                            //Create Encrypted message file
                            new Encrypt(configfilesLocation+"/msg",configfilesLocation+"/msg.bin",
                                    new FileToString().get(System.getProperty("user.dir")+"/sharedsecret"));
                            new DeleteSharedSecret();


                            //send pubkey
                            int i = 0;
                            int chunklength = 255;

                            //SENDING HEADER
                            String pubkeysend = new FileToString().get(configfilesLocation.toString() + "/" +
                                    remoteDevice.getNodeID() + "mypublickey.pem");
                            pubkeysend = pubkeysend.replace("\\n", "\n");
                            String CHATHeader = "-----CHAT BEGIN-----\npubkey:" + pubkeysend + "\n";
                            byte[] CHATHeaderbytebuffer = CHATHeader.getBytes();

                            while (i < CHATHeaderbytebuffer.length) {
                                localDevice.sendData(remoteDevice, Arrays.copyOfRange(CHATHeaderbytebuffer, i,
                                        i + chunklength));
                                i = i + chunklength;
                            }

                            //try to send bytes over zigbee
                            Path encmsgloc = Paths.get(configfilesLocation + "/msg.bin");
                            byte[] msgbytebuffer = Files.readAllBytes(encmsgloc);
                            if (msgbytebuffer.length < chunklength) {
                                chunklength = msgbytebuffer.length;
                            }

                            i = 0;
                            while (i < msgbytebuffer.length) {
                                localDevice.sendData(remoteDevice, Arrays.copyOfRange(msgbytebuffer, i, i + chunklength));
                                i = i + chunklength;
                            }

                            chunklength = 255;
                            //SENDING FOOTER
                            String CHATFooter = "-----CHAT END-----";
                            byte[] CHATFooterbytebuffer = CHATFooter.getBytes();
                            i = 0;
                            while (i < CHATFooterbytebuffer.length) {
                                localDevice.sendData(remoteDevice, Arrays.copyOfRange(
                                        CHATFooterbytebuffer, i, i + chunklength));
                                i = i + chunklength;
                            }
                            System.out.print("\n");


                        }
                    } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XBeeException e) {
            e.printStackTrace();
        } catch (JsonException e) {
            e.printStackTrace();
        }
    }
}

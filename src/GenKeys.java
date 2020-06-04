import com.digi.xbee.api.AbstractXBeeDevice;
import com.github.cliftonlabs.json_simple.JsonException;

import java.io.IOException;

public class GenKeys {


    public GenKeys(int state, String nodeid, String configfilesLocation, String generatorsLocation, String publickeysLocation, String privatekeysLocation) throws InterruptedException, JsonException {
        //generating keys for me (state=0) or using someone else's generator (state=1) ?
        if (state == 0) {
            System.out.println("Calculating keys...\n");
            //create generator "mygenerator.pem"
            try {
                Process generator = Runtime.getRuntime().exec(new String[]{
                        "bash", "-c", "openssl genpkey -genparam -algorithm DH -out " +
                        configfilesLocation.toString() + "/mygenerator.pem"});
                generator.waitFor();
                System.out.println("Generator key created.");
            } catch (IOException e1) {
                System.out.println("Error creating generator file.");
            }
            //create private key from generator
            try {
                Process privatekey = Runtime.getRuntime().exec(new String[]{
                        "bash", "-c", "openssl genpkey -paramfile  " +
                        configfilesLocation.toString() + "/mygenerator.pem -out " +
                        configfilesLocation.toString() + "/myprivatekey.pem"});
                privatekey.waitFor();
                System.out.println("Private key generated.");
            } catch (IOException e1) {
                System.out.println("Error creating private key file.");
            }
            //extract public key from private key
            try {
                Process publickey = Runtime.getRuntime().exec(new String[]{
                        "bash", "-c", "openssl pkey -in " +
                        configfilesLocation.toString() + "/myprivatekey.pem -pubout -out " +
                        configfilesLocation.toString() + "/mypublickey.pem"});
                publickey.waitFor();
                System.out.println("Public key generated.");
            } catch (IOException e1) {
                System.out.println("Error creating public key file.");
            }
        } else if (state == 1) {
            //System.out.println("Calculating keys...\n");
            //write generator "contactgenerator.pem"
            try {
                String cleangen = GetContact.getcontact(nodeid, configfilesLocation)[2];
                cleangen = cleangen.replace("\\n", "?");
                cleangen = cleangen.replace("\\", "");
                cleangen = cleangen.replace("?", "\\n");
                Process generator = Runtime.getRuntime().exec(new String[]{
                        "bash", "-c", "echo '" + cleangen + "' > " + generatorsLocation.toString() + "/" + nodeid +
                        "generator.pem; cat " + generatorsLocation.toString() + "/" + nodeid + "generator.pem | " +
                        "sed -i 's/\\\\n/\\'$'\\n''/g' " + generatorsLocation.toString() + "/" +
                        nodeid + "generator.pem"});

                generator.waitFor();

                //System.out.println("Contact generator key written.");
            } catch (IOException e1) {
                System.out.println("Error creating contact generator file.");
            }

            //write contact public key to file "contactpublickey.pem"
            try {
                String cleanpubkey = GetContact.getcontact(nodeid,configfilesLocation)[3];
                cleanpubkey = cleanpubkey.replace("\\n", "?");
                cleanpubkey = cleanpubkey.replace("\\", "");
                cleanpubkey = cleanpubkey.replace("?", "\\n");
                Process pubkeymake = Runtime.getRuntime().exec(new String[]{
                        "bash", "-c", "echo '" + cleanpubkey + "' > " + publickeysLocation.toString() + "/" + nodeid +
                        "publickey.pem; cat " + publickeysLocation.toString() + "/" + nodeid + "publickey.pem | sed -i" +
                        " 's/\\\\n/\\'$'\\n''/g' " + publickeysLocation.toString() + "/" + nodeid + "publickey.pem"});

                pubkeymake.waitFor();
                //System.out.println("Sending file...");
            } catch (IOException e1) {
                System.out.println("Error creating contact public key file.");
            }

            //create private key from generator
            try {
                Process privatekey = Runtime.getRuntime().exec(new String[]{
                        "bash", "-c", "openssl genpkey -paramfile " +
                        generatorsLocation.toString() + "/" + nodeid + "generator.pem -out " +
                        privatekeysLocation.toString() + "/" + nodeid + "myprivatekey.pem"});
                privatekey.waitFor();
                //System.out.println("My private key generated.");
            } catch (IOException e1) {
                System.out.println("Error creating private key file.");
            }
            //extract public key from private key
            try {
                Process publickey = Runtime.getRuntime().exec(new String[]{
                        "bash", "-c", "openssl pkey -in " +
                        privatekeysLocation.toString() + "/" + nodeid + "myprivatekey.pem -pubout -out " +
                        publickeysLocation.toString() + "/" + nodeid + "mypublickey.pem"});
                publickey.waitFor();
                //System.out.println("My public key generated.");
            } catch (IOException e1) {
                System.out.println("Error creating public key file.");
            }
        }

    }

}

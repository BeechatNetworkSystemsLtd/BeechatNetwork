import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.DigiMeshNetwork;
import com.digi.xbee.api.listeners.IDataReceiveListener;
import com.digi.xbee.api.models.XBeeMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class DataReceiveListener implements IDataReceiveListener {

    DigiMeshNetwork network;
    DigiMeshDevice localDevice;
    ConversationForm conversationForm;

    public boolean AESSignal = false;
    public String AESFilename = "receivedmessage";
    //CREATE ARRAYLIST FOR RECEIVED MESSAGE
    public ArrayList<String> messages = new ArrayList<String>();
    public String sharedsecret = null;
    public String temppubkey = null;
    public String tempgenerator = "";
    public String tempnodeid = "";
    public String tempuname = "";
    public String[][] tempcontact = new String[4][1];
    public int linecounter = 0;

    public int genkeyheaderIndex = 0;
    public int genkeyfooterIndex = 0;

    public int pubkeyheaderIndex = 0;
    public int pubkeyfooterIndex = 0;

    //TEMPORARY FILE BUFFER
    public ArrayList<byte[]> tempfilearray = new ArrayList<byte[]>();

    //TODO: 5/30/2020: get config file locations from main.java
    public String configfilesLocation = System.getProperty("user.dir");
    public String generatorsLocation = configfilesLocation;
    public String publickeysLocation = configfilesLocation;
    public String privatekeysLocation = configfilesLocation;



    /**
     * A data listener. Use this on a DigiMesh Device to listen for incoming data within range.
     * @param network network to perform discovery on.
     * @param localDevice local DigiMesh device
     * @see IDataReceiveListener
     */
    DataReceiveListener(DigiMeshNetwork network, DigiMeshDevice localDevice, ConversationForm conversationForm){
        this.network = network;
        this.localDevice = localDevice;
        this.conversationForm = conversationForm;
    }
    @Override
    public void dataReceived(XBeeMessage xBeeMessage) {

        //System.out.println(xBeeMessage.getDataString());
        messages.add(new String(xBeeMessage.getData()));

        try{
        //CATCHING DIFFERENT OPERATIONS*******************
        if (messages.size() > 0) {
// AES FILE CATCH
            if (messages.get(linecounter).contains("CHAT BEGIN")) {
                AESSignal = true;
// CONTACT CATCH
                //	NODEID CATCH
            } else if (messages.get(linecounter).contains("-BEGIN NODEID-") &&
                    messages.get(linecounter).contains("-END NODEID-") && (!AESSignal)) {
                tempnodeid = messages.get(linecounter);
                tempnodeid = tempnodeid.replace("-BEGIN NODEID-", "");
                tempnodeid = tempnodeid.replace("-END NODEID-", "");
                tempnodeid = tempnodeid.replace("--------","");
                System.out.println("Temp NODE ID:"+tempnodeid);
                tempcontact[0][0] = tempnodeid;

                //UNAME CATCH
            } else if (messages.get(linecounter).contains("-BEGIN UNAME-") &&
                    messages.get(linecounter).contains("-END UNAME-") && (!AESSignal)) {

                tempuname = messages.get(linecounter);
                tempuname = tempuname.replace("-BEGIN UNAME-", "");
                tempuname = tempuname.replace("-END UNAME-", "");
                System.out.println("Temp UNAME:"+tempuname);
                tempcontact[1][0] = tempuname;

                // GENERATOR KEY CATCH
            } else if (messages.get(linecounter).contains("-----BEGIN DH PARAMETERS-----")){
                genkeyheaderIndex = linecounter;
            } else if (messages.get(linecounter).contains("-----END DH PARAMETERS-----")){
                genkeyfooterIndex = linecounter+1;

                // 	PUBLIC KEY CATCH
            }  else if (messages.get(linecounter).contains("-----BEGIN PUBLIC KEY-----")) {
                pubkeyheaderIndex = linecounter;
                System.out.println();
                System.out.println(pubkeyheaderIndex+" *************HEADER*************");
            } else if (messages.get(linecounter).contains("-----END PUBLIC KEY-----")) {
                pubkeyfooterIndex = linecounter +1;
                System.out.println(pubkeyfooterIndex+" *************FOOTER*************");
            }

            if (genkeyfooterIndex != 0 && genkeyheaderIndex != 0){
                int distancefromHeaderandFooter = genkeyfooterIndex - genkeyheaderIndex;
                int i = 0;
                while (i < distancefromHeaderandFooter){
                    tempgenerator = tempgenerator + messages.get(genkeyheaderIndex+i);
                    i = i + 1;
                    System.out.println("****LOOPED****" + i);
                }
                System.out.println("Temp Generator:"+tempgenerator);
                tempcontact[2][0] = tempgenerator;
                genkeyheaderIndex = 0;
                genkeyfooterIndex = 0;
            }

            if (pubkeyfooterIndex != 0 && pubkeyheaderIndex != 0) {
                int distancefromHeaderandFooter = pubkeyfooterIndex - pubkeyheaderIndex;
                int i = 0;
                while (i < distancefromHeaderandFooter){
                    temppubkey = temppubkey + messages.get(pubkeyheaderIndex+i);
                    i = i + 1;
                    System.out.println("****LOOPED****" + i);
                }
                temppubkey = temppubkey.substring(temppubkey.lastIndexOf("-----BEGIN PUBLIC KEY-----"));
                System.out.println("Temp Pubkey:"+temppubkey);

                tempcontact[3][0] = temppubkey;
                pubkeyheaderIndex = 0;
                pubkeyfooterIndex = 0;

                new AddContact(tempcontact[0][0], tempcontact[1][0], tempcontact[2][0], tempcontact[3][0]);
            }

            //************CHAT***********************************
            if (AESSignal) {
                if (temppubkey != null) {
                    try {
                        String cleanpubkey = temppubkey.substring(temppubkey.indexOf
                                ("-----BEGIN PUBLIC KEY-----"));

                        cleanpubkey = cleanpubkey.replace("\\n", "?");
                        cleanpubkey = cleanpubkey.replace("\\", "");
                        cleanpubkey = cleanpubkey.replace("?", "\\n");
                        Process pubkeymake = Runtime.getRuntime().exec(new String[]{
                                "bash", "-c", "echo '" + cleanpubkey + "' > " +
                                configfilesLocation.toString() + "/contactpublickey.pem;"
                                + "cat " + configfilesLocation.toString() + "/contactpublickey.pem  | "
                                + "sed -i 's/\\\\n/\\'$'\\n''/g' " + configfilesLocation.toString() +
                                "/contactpublickey.pem"});

                        pubkeymake.waitFor();

                        //System.out.println("Contact public key written.");
                        temppubkey = null;
                    } catch (IOException e1) {
                        System.out.println("Error creating contact public key file.");
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                    }
                    //Getting shared secret

                    try {
                        new GetSharedSecret("",configfilesLocation+"/myprivatekey.pem",configfilesLocation+"/contactpublickey.pem");
                        sharedsecret = new FileToString().get(configfilesLocation+"/sharedsecret");
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }

                    //System.out.println("SHARED SECRET:"+sharedsecret);
                    //Delete sharedsecret file
                    try {
                        new DeleteSharedSecret();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } // end pubkey!=null


                if ( !(messages.get(linecounter).contains("-")) ) {
                    //Message received is NOT a keypair exchange
                    //Add the bytes to the tempfilearray
                    tempfilearray.add(xBeeMessage.getData());
                }

                if (messages.get(linecounter).contains("CHAT END")) {

                    //System.out.println("Receiving file...");

                    //								Convert arraylist to byte array
                    int i = 0;

                    byte[][] tempfile = new byte[tempfilearray.size()][0];
                    while (i < tempfile.length) {
                        tempfile[i] = tempfilearray.get(i);
                        i = i + 1;
                    }

                    //If file exists, overwrite old file, TODO maybe ask for user input?
                    File message = new File(configfilesLocation+"/msg");
                    if(message.exists() && !message.isDirectory()) {
                        try {
                            Process remold = Runtime.getRuntime().exec(new String[]{
                                    "bash", "-c", "rm -rf " + configfilesLocation + "/msg"});
                            remold.waitFor();
                        } catch (IOException | InterruptedException e1) {
                            System.out.println("Error removing old file.");
                        }
                    }

                    File encmessage = new File(configfilesLocation+"/msg.bin");
                    if(encmessage.exists() && !encmessage.isDirectory()) {
                        try {
                            Process remold = Runtime.getRuntime().exec(new String[]{
                                    "bash", "-c", "rm -rf " + configfilesLocation + "/msg.bin"});
                            remold.waitFor();
                        } catch (IOException | InterruptedException e1) {
                            System.out.println("Error removing old file.");
                        }
                    }


                    //Make new, empty file for the new data received.
                    try {
                        new CreateFile(configfilesLocation + "/msg");

                        //write byte array to file
                        try (FileOutputStream stream = new FileOutputStream(
                                configfilesLocation + "/msg.bin")) {
                            for (i = 0; i < tempfile.length; i++) {
                                for (int j = 0; j < tempfile[i].length; j++) {
                                    stream.write(tempfile[i][j]);
                                }
                            }
                        }

                    } catch (IOException e1) {
                        //	System.out.println("Error removing old file.");
                    }
                    new RemoveEndZeros(configfilesLocation+"/msg.bin");

                    //	CLEAR tempfilearray for next message
                    tempfilearray.removeAll(tempfilearray);
                    tempfile = null;

                    try {
                        new Decrypt(encmessage.getAbsolutePath(),sharedsecret);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        conversationForm.receiveMessage(xBeeMessage.getDevice().getNodeID() + ": " +
                                new FileToString().get(configfilesLocation+"/msg"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        System.out.println(xBeeMessage.getDevice().getNodeID() + ": " +
                                new FileToString().get(configfilesLocation+"/msg"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    /*
                    apend message to log file
                    Process appendtologfile = Runtime.getRuntime().exec(new String[]{
                    "bash", "-c", "echo '" + xbeeMessage.getDevice().getNodeID() + ": " +
                    file2string(downloadfilesLocation + "/msg") + "' >> " + configfilesLocation
                    + "/" + xbeeMessage.getDevice().getNodeID()});
                    appendtologfile.waitFor();
                    TODO: maybe there is a better way to append to logfiles?
                    System.out.println("END REACHED");
                    */

                    AESSignal = false;
                }


                } //end CHAT

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println(xBeeMessage.getDataString());
            linecounter = linecounter +1;
            //if (!(tempcontact[3][0].isBlank() && (tempcontact[2][0].isBlank()) &&
                    //(tempcontact[1][0].isBlank()) && (tempcontact[0][0].isBlank()))) {
                //try {
                    //new AddContact(tempcontact[0][0], tempcontact[1][0], tempcontact[2][0], tempcontact[3][0]);
                //} catch (IOException e) {
                    //System.err.println("Unable to add contact.");
                    //e.printStackTrace();
        }
    }
}
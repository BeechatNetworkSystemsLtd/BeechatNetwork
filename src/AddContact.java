import com.github.cliftonlabs.json_simple.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class AddContact {
    public AddContact(String nodeid, String uname, String generator, String pubkey) throws IOException {
        //row 0 = nodeid tempnodeid
        //row 1 = username tempuname
        //row 2 = generator tempgenerator
        //row 3 = publickey temppubkey
        String configfilesLocation = System.getProperty("user.dir");
        HashMap<String, Object> contactDetails = new HashMap<String, Object>();
        contactDetails.put("uname", uname);
        contactDetails.put("generator", generator);
        contactDetails.put("pubkey", pubkey);
        JsonObject JSONcontactDetails = new JsonObject(contactDetails);

        HashMap<String, Object> contactID = new HashMap<String, Object>();
        contactID.put(nodeid, JSONcontactDetails);
        JsonObject JSONcontactID = new JsonObject(contactID);
        try (FileWriter file = new FileWriter(configfilesLocation.toString() + "/contacts.json",true)) {
            String jsonstring = JSONcontactID.toJson();
            jsonstring = jsonstring.replace("-----END PUBLIC KEY-----\\n",
                    "-----END PUBLIC KEY-----");

            jsonstring = jsonstring.replace("-----END DH PARAMETERS-----\\n",
                    "-----END DH PARAMETERS-----");

            jsonstring = jsonstring.replace("\\u0000", "");

            file.write(jsonstring);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

import com.github.cliftonlabs.json_simple.*;

import java.io.FileReader;
import java.io.IOException;

public class GetContact {
    public static String[] getcontact(String nodeid, String configfilesLocation) throws IOException, JsonException {
        String[] finalcontact = new String[4];
        try {
            JsonObject a = (JsonObject) Jsoner.deserialize(new FileReader(
                    configfilesLocation + "/contacts.json"));
            System.out.println(a.get("XBEE 1"));



            JsonArray array = new JsonArray();
            //array.add(contactsfile);
            int i = 0;
            while (i <= array.size() - 1) {
                String currstring = array.get(i).toString();
                System.out.println(currstring);
                if (currstring.contains(nodeid)) {
                    System.out.println("CONTACT FOUND.");
                    finalcontact[0] = nodeid;
                    finalcontact[1] = currstring.substring(currstring.lastIndexOf("generator"+11));
                    finalcontact[2] = currstring.substring(currstring.lastIndexOf("uname"+7));
                    finalcontact[3] = currstring.substring(currstring.lastIndexOf("pubkey")
                            + 9, currstring.lastIndexOf("END PUBLIC KEY-----") + 19);

                } else {
                    finalcontact[0] = "ERROR 2";
                    finalcontact[1] = "ERROR 2";
                    finalcontact[2] = "ERROR 2";
                    finalcontact[3] = "ERROR 2";
                }

                i = i + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            finalcontact[0] = "ERROR 1";
            finalcontact[1] = "ERROR 1";
            finalcontact[2] = "ERROR 1";
            finalcontact[3] = "ERROR 1";
            return finalcontact;
        }
        return finalcontact;
    }

}

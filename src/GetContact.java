import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.io.FileReader;
import java.io.IOException;

public class GetContact {
    public static String[] getcontact(String nodeid, String configfilesLocation) throws IOException, JsonException {
        String[] finalcontact = new String[4];
        try {
                JsonObject obj = (JsonObject) Jsoner.deserialize(new FileReader(
                    configfilesLocation.toString() + "/contacts.json"));
            JsonArray array = new JsonArray();
            array.add(obj);
            int i = 0;
            while (i <= array.size() - 1) {
                String currstring = array.get(i).toString();
                if (currstring.contains(nodeid)) {

                    finalcontact[0] = nodeid;
                    finalcontact[1] = currstring.substring(currstring.lastIndexOf("uname\":\"")
                            + 8, currstring.lastIndexOf("\",\"generator"));
                    finalcontact[2] = currstring.substring(currstring.lastIndexOf("generator")
                            + 12, currstring.lastIndexOf("\",\"pubkey"));
                    finalcontact[3] = currstring.substring(currstring.lastIndexOf("pubkey")
                            + 9, currstring.lastIndexOf("END PUBLIC KEY-----") + 19);

                } else {
                    finalcontact[0] = "";
                    finalcontact[1] = "";
                    finalcontact[2] = "";
                    finalcontact[3] = "";
                }

                i = i + 1;
            }
        } catch (Exception e) {
            //e.printStackTrace();
            finalcontact[0] = "";
            finalcontact[1] = "";
            finalcontact[2] = "";
            finalcontact[3] = "";
            return finalcontact;
        }
        return finalcontact;
    }

}

import com.github.cliftonlabs.json_simple.*;

import java.io.FileReader;
import java.io.IOException;

public class GetContact {
    public static JsonObject getcontact(String nodeid, String configfilesLocation) throws IOException, JsonException {
        String[] finalcontact = new String[4];

        JsonObject obj = new JsonObject();
        JsonObject contacts = new JsonObject();

        //Try to read all contacts from the contacts.json file.
        try {
            obj = (JsonObject) Jsoner.deserialize(new FileReader(
                    configfilesLocation + "/contacts.json"));
            //obj = (JsonObject) obj.get("contacts");
            contacts = (JsonObject) obj.get("contacts");
        } catch (JsonException e) {
            //If there is an exception, interpret as if the contacts file has no contacts.
            System.out.println("No contacts yet.");
            //contacts.put("contacts",null);
        }
        int i = 0;
        while (i <= contacts.size() - 1) {
            if (contacts.get(nodeid) != null) {
                System.out.println("Contact found.");
                return (JsonObject) contacts.get(nodeid);
            }
            i=i+1;
        }
        System.out.println("Contact not found.");
        return null;
    }
}

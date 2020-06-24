package com.beechat.beechatnetwork;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;

import static com.github.cliftonlabs.json_simple.JsonObject.*;

public class AddContact {
    public AddContact(String nodeid, String generator, String uname, String pubkey) throws IOException, JsonException {
        // row 0 = nodeid tempnodeid
        // row 1 = username tempuname
        // row 2 = generator tempgenerator
        // row 3 = publickey temppubkey
        String configfilesLocation = System.getProperty("user.dir");
        ;
        JsonObject obj = new JsonObject();
        JsonObject contacts = new JsonObject();

        // Try to read all contacts from the contacts.json file.
        try {
            obj = (JsonObject) Jsoner.deserialize(new FileReader(configfilesLocation + "/contacts.json"));
            // obj = (JsonObject) obj.get("contacts");
            contacts = (JsonObject) obj.get("contacts");
        } catch (JsonException e) {
            // If there is an exception, interpret as if the contacts file has no contacts.
            System.out.println("No contacts yet.");
            // contacts.put("contacts",null);
        }

        JsonObject newcontact = new JsonObject();
        newcontact.put("generator", generator);
        newcontact.put("uname", uname);
        newcontact.put("pubkey", pubkey);

        // JsonObject newcontactObject = new JsonObject();
        // newcontactObject.put(nodeid,newcontact);
        contacts.put(nodeid, newcontact);

        obj.put("contacts", contacts);

        FileWriter file = new FileWriter(configfilesLocation + "/contacts.json");
        String jsonstring = obj.toJson();
        System.out.println(jsonstring);
        jsonstring = jsonstring.replace("\\u0000", "");
        file.write(jsonstring);
        file.flush();
        file.close();

        System.out.println(contacts);

        // JsonObject obj = new JsonObject();0m

        // JsonArray list = new JsonArray();
        // list.add(generator);
        // list.add(uname);
        // list.add(pubkey);

        // obj.put(nodeid, list);

        // try (FileWriter file = new FileWriter(configfilesLocation.toString() +
        // "/contacts.json",true)) {
        // String jsonstring = obj.toJson();
        // jsonstring = jsonstring.replace("\\u0000", "");
        // jsonstring = jsonstring.replace("-----END PUBLIC KEY-----\\n",
        // "-----END PUBLIC KEY-----");

        // jsonstring = jsonstring.replace("-----END DH PARAMETERS-----\\n",
        // "-----END DH PARAMETERS-----");
        // file.write(jsonstring);
        // } catch (IOException e) {
        // e.printStackTrace();
        // }

        // System.out.print(obj);

        // try {
        // JsonObject a = (JsonObject) Jsoner.deserialize(new FileReader(
        // configfilesLocation + "/contacts.json"));
        // JsonArray jsonArray = new JsonArray(Collections.singleton(obj.toJson()));
        //
        // System.out.println(jsonArray);

        // JsonObject person = new JsonObject();
        // person.put("generator", generator);
        // person.put("uname", uname);
        // person.put("pubkey", pubkey);

        // jsonArray.add(person);

        // System.out.println(jsonArray);

        // FileWriter file = new FileWriter(configfilesLocation +
        // "/contacts.json",true);
        // String jsonstring = jsonArray.toJson();
        // jsonstring = jsonstring.replace("\\u0000","");
        // file.write(jsonArray.toJson());
        // file.flush();
        // file.close();
        // } catch (IOException | JsonException e){
        // e.printStackTrace();
        // }

    }

}

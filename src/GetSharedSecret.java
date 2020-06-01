import java.io.IOException;

public class GetSharedSecret {
    public GetSharedSecret(String REMOTE_NODE_ID, String myprivatekeyPath, String publickeyPath) throws IOException, InterruptedException {

        Process sharedsecret_file = Runtime.getRuntime().exec(new String[]{
                "bash", "-c", "openssl pkeyutl -derive -inkey " + myprivatekeyPath.toString()
                + "/" + REMOTE_NODE_ID + "myprivatekey.pem -peerkey " +
                publickeyPath.toString() + "/" + REMOTE_NODE_ID +
                "publickey.pem | openssl sha3-256 | sed -e 's|(stdin)= ||' > "
                + System.getProperty("user.dir") + "/sharedsecret"});
        sharedsecret_file.waitFor();

    }
}

import java.io.IOException;

public class Encrypt {
    public  Encrypt(String filePath, String outputPath, String sharedsecret) throws InterruptedException {
        Process encwithSSP = null;
        try {
            encwithSSP = Runtime.getRuntime().exec(new String[]{
                    "bash", "-c", "openssl enc -aes-256-cbc -in " + filePath + " -out " + outputPath + ".bin -pass pass:" +
                    sharedsecret});
        } catch (IOException e) {
            e.printStackTrace();
        }
        encwithSSP.waitFor();
    }
}

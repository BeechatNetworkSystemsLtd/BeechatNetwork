import java.io.IOException;

public class DeleteSharedSecret {
    public DeleteSharedSecret() throws IOException, InterruptedException {

        Process deletesharedsecretfile = null;
        try {
            deletesharedsecretfile = Runtime.getRuntime().exec(new String[]{
                    "bash", "-c", "rm -rf " + System.getProperty("user.dir") + "/sharedsecret"});
            deletesharedsecretfile.waitFor();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}

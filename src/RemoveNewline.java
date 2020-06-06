import java.io.File;
import java.io.IOException;

public class RemoveNewline {
    public RemoveNewline(String filePath){
        File f = new File(filePath);
        if(f.exists() && !f.isDirectory()) {
            try {
                Process remzeros = Runtime.getRuntime().exec(new String[]{
                        "bash", "-c", "head -c -1 " + f.getAbsolutePath() + " > "
                        + f.getAbsolutePath() + ".stripped ; " +

                        "rm -rf " + f.getAbsolutePath() + ";" +
                        " mv "  + f.getAbsolutePath() + ".stripped " + f.getAbsolutePath()});
                remzeros.waitFor();

            } catch (IOException | InterruptedException e1) {
                System.out.println("Error removing newline character at EOF.");
            }
        }
    }
}

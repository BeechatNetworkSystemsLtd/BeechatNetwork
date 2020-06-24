package com.beechat.beechatnetwork;

import java.io.File;
import java.io.IOException;

public class RemoveEndZeros {
    public RemoveEndZeros(String filepath) {

        File f = new File(filepath);
        if (f.exists() && !f.isDirectory()) {
            try {
                Process remzeros = Runtime.getRuntime().exec(new String[] { "bash", "-c",
                        "sed '$ s/\\x00*$//' " + f.getAbsolutePath() + " > " + f.getAbsolutePath() + ".stripped ; " +

                                "rm -rf " + f.getAbsolutePath() + ";" + " mv " + f.getAbsolutePath() + ".stripped "
                                + f.getAbsolutePath() });
                remzeros.waitFor();

            } catch (IOException | InterruptedException e1) {
                System.out.println("Error removing zeros.");
            }
        }
    }
}

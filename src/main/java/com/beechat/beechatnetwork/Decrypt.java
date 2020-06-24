package com.beechat.beechatnetwork;

import java.io.IOException;

public class Decrypt {
    public Decrypt(String filepath, String sharedsecret) throws InterruptedException, IOException {
        Process decwithSSP = Runtime.getRuntime()
                .exec(new String[] { "bash", "-c", "openssl enc -d -aes-256-cbc -in " + filepath + " -out "
                        + filepath.substring(0, filepath.lastIndexOf(".bin")) + " -pass pass:" + sharedsecret });
        decwithSSP.waitFor();
    }
}

package com.beechat.beechatnetwork;

import java.io.IOException;

public class GetSharedSecret {
    public GetSharedSecret(String REMOTE_NODE_ID, String myprivatekeyPath, String publickeyPath)
            throws IOException, InterruptedException {

        Process sharedsecret_file = Runtime.getRuntime()
                .exec(new String[] { "bash", "-c",
                        "openssl pkeyutl -derive -inkey " + myprivatekeyPath.toString() + " -peerkey "
                                + publickeyPath.toString() + " | openssl sha3-256 | sed -e 's|(stdin)= ||' > "
                                + System.getProperty("user.dir") + "/sharedsecret" });
        sharedsecret_file.waitFor();

    }
}

package com.beechat.beechatnetwork;

import java.io.File;
import java.io.IOException;

public class FileExists {
    public Boolean getBoolean(String filepath) {
        File myObj = new File(filepath);
        if (myObj.exists()) {
            return true;
        } else {
            return false;
        }
    }

}

package com.beechat.beechatnetwork;

import java.io.*;

public class FileToString {
    public String get(String filePath) throws IOException {
        File source = new File(filePath);
        final DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(source)));
        final byte[] buffer = new byte[(int) source.length()];
        dis.readFully(buffer);
        dis.close();
        return new String(buffer);
    }
}

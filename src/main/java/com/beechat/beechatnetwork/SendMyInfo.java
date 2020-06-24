package com.beechat.beechatnetwork;

import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.DigiMeshNetwork;
import com.digi.xbee.api.RemoteDigiMeshDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;

import java.util.Arrays;

public class SendMyInfo {
    DigiMeshDevice localDevice;
    RemoteDigiMeshDevice remoteDevice;
    DigiMeshNetwork network;
    ConversationForm conversationForm;

    public SendMyInfo(String uname, String REMOTE_NODE_ID, String myPublicKey, String myGen,
            RemoteDigiMeshDevice remoteDevice, DigiMeshNetwork network, DigiMeshDevice localDevice) {

        this.remoteDevice = remoteDevice;
        this.network = network;
        this.localDevice = localDevice;

        if (localDevice.isOpen()) {
            localDevice.close();
        }

        try {
            localDevice.open();
            System.out.println("\nLocal XBee: " + localDevice.getNodeID());
            System.out.println("\nSending contact information to :" + REMOTE_NODE_ID);

            int chunklength = 70;

            if (localDevice.getNodeID().equals(REMOTE_NODE_ID)) {
                System.err.println("Error: the value of the REMOTE_NODE_ID constant must be "
                        + "the Node Identifier (NI) of the OTHER module.");
            } else {
                System.out.println("\nEstablishing connection with " + REMOTE_NODE_ID + "...");
                network = (DigiMeshNetwork) localDevice.getNetwork();
                remoteDevice = (RemoteDigiMeshDevice) network.discoverDevice(REMOTE_NODE_ID);

                if (remoteDevice != null) {
                    System.out.println("Connection established.\n");

                    int i = 0;

                    // SENDING NODEID
                    String mynodeid = "-----BEGIN NODEID-----" + localDevice.getNodeID() + "-----END NODEID-----";
                    byte[] bytebuffer = mynodeid.getBytes();
                    int len = bytebuffer.length;
                    while (i < len) {
                        localDevice.sendData(remoteDevice, Arrays.copyOfRange(bytebuffer, i, i + chunklength));
                        i = i + chunklength;
                    }
                    System.out.println("NODEID sent.");
                    // sending uname
                    mynodeid = "-----BEGIN UNAME-----" + uname + "-----END UNAME-----";
                    bytebuffer = mynodeid.getBytes();
                    len = bytebuffer.length;
                    i = 0;
                    while (i < len) {
                        localDevice.sendData(remoteDevice, Arrays.copyOfRange(bytebuffer, i, i + chunklength));
                        i = i + chunklength;
                    }
                    System.out.println("UNAME sent.");

                    // sending generator key
                    bytebuffer = myGen.getBytes();
                    len = bytebuffer.length;
                    i = 0;
                    while (i < len) {
                        localDevice.sendData(remoteDevice, Arrays.copyOfRange(bytebuffer, i, i + chunklength));
                        i = i + chunklength;
                    }
                    System.out.println("Generator key sent.");

                    // Sending public key
                    bytebuffer = myPublicKey.getBytes();
                    len = bytebuffer.length;
                    i = 0;
                    while (i < len) {
                        localDevice.sendData(remoteDevice, Arrays.copyOfRange(bytebuffer, i, i + chunklength));
                        i = i + chunklength;
                    }
                    System.out.println("Public key sent.");

                }
            }
        } catch (XBeeException e) {
            System.err.println("Error transmitting message: " + e.getMessage());
            localDevice.close();
            System.exit(1);
        } finally {
            System.out.println("Sent keys.");
        }

    }
}

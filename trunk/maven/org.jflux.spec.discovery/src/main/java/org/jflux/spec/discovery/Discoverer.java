/*
 * Copyright 2014 the Friendularity Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jflux.spec.discovery;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import org.jflux.api.core.util.DefaultNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runs SSDP discovery to find services.
 * @author Amy Jessica Book <jgpallack@gmail.com>
 */

public class Discoverer extends DefaultNotifier<UniqueService>
    implements Runnable {
    private final static String theSsdpAddr = "239.255.255.250";
    private final static int theSsdpPort = 1900;
    private final static Logger theLogger =
            LoggerFactory.getLogger(Discoverer.class);

    @Override
    public void run() {
        InetAddress group;
        MulticastSocket sock;
        
        try {
            group = InetAddress.getByName(theSsdpAddr);
        } catch(UnknownHostException ex) {
            theLogger.error(
                    "Unknown host " + theSsdpAddr + ": " + ex.getMessage());
            return;
        }
        
        try {
            sock = new MulticastSocket(theSsdpPort);
            sock.joinGroup(group);
        } catch(IOException ex) {
            theLogger.error("Error creating socket: " + ex.getMessage());
            return;
        }
        
        while(true) {
            boolean ourService = false;
            String serial = null;
            Map<String, String> properties = new HashMap<String, String>();
            byte[] buf = new byte[1000];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            String data;
            
            try {
                sock.receive(packet);
            } catch(IOException ex) {
                theLogger.error("Can't receive data: " + ex.getMessage());
                continue;
            }

            String ipAddress = packet.getAddress().getHostAddress();
            
            try {
                data = new String(packet.getData(), "UTF-8");
            } catch(UnsupportedEncodingException ex) {
                theLogger.error("Unsupported encoding: " + ex.getMessage());
                continue;
            }
            
            String[] lines = data.split("\n");
            
            for(String line: lines) {
                if(line.startsWith("USN")) {
                    if(line.substring(5).trim().equals("JFlux")) {
                        ourService = true;
                    } else {
                        break;
                    }
                } else if(line.startsWith("LOCATION")) {
                    String serialNumber = line.split(" ")[1];
                    String[] splitSerial = serialNumber.split(";");
                    serial =
                            splitSerial[0].replaceAll("%20", " ")
                                    .replaceAll("%25", "%");
                    
                    for(int i = 1; i < splitSerial.length; i++) {
                        String[] property = splitSerial[i].split(",");
                        String key =
                                property[0].replaceAll("%20", " ")
                                        .replaceAll("%25", "%");
                        String value =
                                property[1].replaceAll("%20", " ")
                                        .replaceAll("%25", "%");
                        properties.put(key, value);
                    }
                }
            }
            
            if(ourService && serial != null) {
                notifyListeners(
                        new UniqueService(ipAddress, serial, properties));
            }
        }
    }
}
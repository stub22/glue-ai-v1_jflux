/*
 * Copyright 2012 by The JFlux Project (www.jflux.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eit        her express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jflux.impl.transport.jms;

import java.io.ByteArrayOutputStream;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Session;
import org.jflux.api.core.Adapter;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class MessagePacker implements Adapter<ByteArrayOutputStream,BytesMessage> {
    private Session mySession;
    
    /**
     * Adapter to turn a byte stream into a proper message.
     * @param session the session to connect to
     */
    public MessagePacker(Session session){
        mySession = session;
    }
    
    /**
     * Change the session.
     * @param session the new session to connect to
     */
    public synchronized void setSession(Session session){
        mySession = session;
    }
    
    /**
     * Format the message.
     * @param a the byte stream to format into a proper message
     * @return the formatted message
     */
    @Override
    public synchronized BytesMessage adapt(ByteArrayOutputStream a) {
        if(a == null || mySession == null){
            return null;
        }
        try{
            BytesMessage msg = mySession.createBytesMessage();
            msg.writeBytes(a.toByteArray());
            return msg;
        }catch(JMSException ex){
            return null;
        }
    }
    
}

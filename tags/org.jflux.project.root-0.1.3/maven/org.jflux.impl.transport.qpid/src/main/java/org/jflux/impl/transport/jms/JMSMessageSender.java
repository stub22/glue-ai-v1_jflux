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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jflux.impl.transport.jms;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.jflux.api.core.Listener;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class JMSMessageSender implements Listener<BytesMessage> {
    private final static Logger theLogger = Logger.getLogger(JMSMessageSender.class.getName());
    private Session mySession;
    private Destination myDestination;
    private MessageProducer myProducer;

    public JMSMessageSender(Session session, Destination dest) {
        mySession = session;
        myDestination = dest;
        start();
    }
    
    public void setSession(Session session){
        stop();
        mySession = session;
        start();
    }
    
    public void setDestination(Destination dest){
        stop();
        myDestination = dest;
        start();
    }
    
    private void start(){
        try{
            if(mySession == null || myDestination == null){
                return;
            }if(myProducer != null){
                return;
            }
            myProducer = mySession.createProducer(myDestination);
        }catch(JMSException ex){
            theLogger.log(Level.WARNING, "Unable to create producer.", ex);
            return;
        }
    }
    
    private void stop(){
        try{
            if(myProducer == null){
                return;
            }
            myProducer.close();
            myProducer = null;
        }catch(JMSException ex){
            theLogger.log(Level.WARNING, "Unable to close producer.", ex);
            return;
        }
    }
    
    @Override
    public void handleEvent(BytesMessage event) {
        if(myProducer == null){
            return;
        }
        try{
            myProducer.send(event);
        }catch(JMSException ex){
            theLogger.log(Level.INFO, "Unable to send event.", ex);
        }
    }
}

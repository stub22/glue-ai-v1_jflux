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

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.jflux.api.core.node.DefaultProducerNode;
import org.jflux.api.core.util.DefaultNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Polls for JMS BytesMessages from a JMS Destination.  Records are then sent 
 * to a RecordHandler.
 * When this is started it creates a new Thread for a polling loop.
 * 
 * @author Matthew Stevenson <www.jflux.org>
 */
public class JMSMessageReceiver extends DefaultProducerNode<BytesMessage>{
    private final static Logger theLogger = LoggerFactory.getLogger(JMSMessageReceiver.class);
    private MessageConsumer myMessageConsumer;
    private Thread myPollingThread;
    
    public JMSMessageReceiver(Session session, Destination dest) 
            throws JMSException{
        super(new DefaultNotifier<BytesMessage>());
        if(session == null || dest == null){
            throw new NullPointerException();
        }
        myMessageConsumer = session.createConsumer(dest);
    }
    
    /**
     * Creates and starts an Polling Thread to fetch Records over JMS.
     */
    @Override
    public boolean start(){
        if(!super.start()){
            return false;
        }
        if(myPollingThread != null && 
                myPollingThread.isAlive()){//We are already running
            return true;
        }
        myPollingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                eventLoop();
            }
        });
        myPollingThread.start();
        return true;
    }  
    
    private void eventLoop(){
        while(getPlayState() == PlayState.RUNNING 
                || getPlayState() == PlayState.PAUSED){
            if(getPlayState() == PlayState.PAUSED){
                try{
                    Thread.sleep(10);
                }catch(InterruptedException ex){}
                continue;
            }
            try{
                BytesMessage bytesMsg = fetchMessage();
                if(bytesMsg == null){
                    continue;
                }
                getNotifier().notifyListeners(bytesMsg);
            }catch(Throwable t){
                theLogger.warn("Error in Message fetch loop.", t);
                try{
                    Thread.sleep(5);
                }catch(InterruptedException ex){}
            }
        }
    }
    
    private BytesMessage fetchMessage(){
        try{
            Message msg = myMessageConsumer.receive();
            if(msg == null){
                theLogger.info("Received Null message.");
                return null;
            }
            msg.acknowledge();
            //theLogger.info("Received JMS Message.");
            if(!(msg instanceof BytesMessage)){
                theLogger.warn("Received JMS Message not of type BytesMessage."
                        + "  Ignoring Message.");
                return null;
            }
            return (BytesMessage)msg;
        }catch(JMSException ex){
            theLogger.error("Error fetching Message.", ex);
            return null;
        }
    }
}

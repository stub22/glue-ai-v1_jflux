/*
 * Copyright 2011 Hanson Robokind LLC.
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
package org.jflux.impl.messaging.rk;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import org.apache.avro.Schema;
import org.jflux.api.messaging.rk.RecordAsyncReceiver;
import org.jflux.api.messaging.rk.RecordAsyncReceiver.RecordHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Polls for JMS BytesMessages from a JMS Destination.  Records are then sent 
 * to a RecordHandler.
 * When this is started it creates a new Thread for a polling loop.
 * 
 * @author Matthew Stevenson <www.robokind.org>
 */
public class JMSBytesRecordAsyncReceiver implements RecordAsyncReceiver<BytesMessage> {
    private final static Logger theLogger =
            LoggerFactory.getLogger(JMSBytesRecordAsyncReceiver.class);
    private Schema mySchema;
    private RecordHandler<BytesMessage> myRecordHandler;
    private MessageConsumer myMessageConsumer;
    private boolean myConsumeFlag;
    private final Object myHandlerLock = new Object();
    private Thread myPollingThread;
    private boolean myPauseFlag;
    
    /**
     * Creates a new JMSBytesRecordAsyncReceiver.
     * @param consumer JMS MessageConsumer to fetch the Record
     */
    public JMSBytesRecordAsyncReceiver(MessageConsumer consumer){
        if(consumer == null){
            throw new NullPointerException();
        }
        myMessageConsumer = consumer;
        myConsumeFlag = true;
        myPauseFlag = false;
    }
    
    @Override
    public void setRecordHandler(RecordHandler<BytesMessage> handler){
        synchronized(myHandlerLock){
            if(handler == null){
                throw new NullPointerException();
            }
            myRecordHandler = handler;
        }
    }
    
    @Override
    public void unsetRecordHandler(){
        synchronized(myHandlerLock){
            myRecordHandler = null;
        }
    }
    
    /**
     * Creates and starts an Polling Thread to fetch Records over JMS.
     * @throws IllegalStateException if RecordHandler is not set
     */
    @Override
    public void start() throws IllegalStateException{
        if(myRecordHandler == null){
            throw new IllegalStateException("RecordHandler cannot be null.");
        }
        if(myConsumeFlag && 
                myPollingThread != null && 
                myPollingThread.isAlive()){//We are already running
            if(myPauseFlag){//If we are paused, then we should resume
                resume();
            }
            return;
        }
        myPollingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                eventLoop();
            }
        });
        myConsumeFlag = true;
        myPauseFlag = false;
        myPollingThread.start();
    }  
    
    @Override
    public void pause(){
        myPauseFlag = true;
    }
    
    @Override
    public void resume(){
        myPauseFlag = false;
    }
    
    @Override
    public void stop(){
        myConsumeFlag = false;
    }
    
    private static void sleep(long msec){
        try{
            Thread.sleep(msec);
        }catch(InterruptedException ex){
            theLogger.warn("Thread sleep interrupted.", ex);
        }
    }
    
    private void eventLoop(){
        while(myConsumeFlag){
            if(myPauseFlag){
                sleep(10);
                continue;
            }
            try{
                BytesMessage bytesMsg = fetchMessage();
                if(bytesMsg == null){
                    continue;
                }
                synchronized(myHandlerLock){
                    if(myRecordHandler == null || !myConsumeFlag){
                        return;
                    }
                    myRecordHandler.handleRecord(bytesMsg);
                }
            }catch(Throwable t){
                theLogger.warn("Error in Message fetch loop.", t);
                sleep(5);
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

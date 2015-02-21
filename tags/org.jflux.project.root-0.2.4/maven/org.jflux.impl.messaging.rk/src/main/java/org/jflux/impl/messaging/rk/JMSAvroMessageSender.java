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

import java.util.logging.Logger;
import javax.jms.Destination;
import javax.jms.Session;
import org.apache.avro.generic.IndexedRecord;
import org.jflux.api.messaging.rk.DefaultMessageSender;

/**
 * DefaultMessageSender for serializing Messages to Avro Records and sending 
 * them over JMS (Qpid).
 * 
 * @param <Msg> type of Message the sender accepts
 * @param <Rec> type of Avro Record to be sent
 * @param <L> type of listener to notify when sending a Message
 * @author Matthew Stevenson <www.robokind.org>
 */
public class JMSAvroMessageSender<Msg, Rec extends IndexedRecord> extends 
        DefaultMessageSender<Msg,Rec> {
    private final static Logger theLogger = 
            Logger.getLogger(JMSAvroMessageSender.class.getName());
    private JMSBytesMessageSender myBytesSender;
    private String myDefaultContentType;
    
    /**
     * Creates a new JMSAvroMessageSender to send Records to the given 
     * destination using the given session.
     * @param session JMS Session to use for sending
     * @param destination JMS Destination to send to
     */
    public JMSAvroMessageSender(Session session, Destination destination){
        if(session == null || destination == null){
            throw new NullPointerException();
        }
        myBytesSender = new JMSBytesMessageSender();
        myBytesSender.setSession(session);
        myBytesSender.setDestination(destination);
    }
    
    /**
     * Creates a new JMSAvroMessageSender using the given sender for sending
     * Records.
     * @param sender used for sending AvroRecords
     */
    public JMSAvroMessageSender(JMSBytesMessageSender sender){
        if(sender == null){
            throw new NullPointerException();
        }
        myBytesSender = sender;
    }
    
    /**
     * Creates and opens a new JMSAvroRecordSender for sending the Avro Records.
     */
    @Override
    public void start(){
        myBytesSender.openProducer();
        JMSAvroRecordSender<Rec> recordSender = 
                new JMSAvroRecordSender<Rec>(myBytesSender);
        setRecordSender(recordSender);
    }
    
    /**
     * Closes the JMSRecordReceiver.
     */
    @Override
    public void stop(){
        if(myRecordSender == null || 
                !(myRecordSender instanceof JMSAvroRecordSender)){
            return;
        }
        myBytesSender.closeProducer();
    }
    
    public void setDefaultContentType(String contentType){
        myDefaultContentType = contentType;
    }
    
    @Override
    public void notifyListeners(Msg message){
        if(myDefaultContentType == null){
            super.notifyListeners(message);
        }else{
            sendMessage(message, myDefaultContentType);
        }
    }
    
    /**
     * Adapts the given Message to an Avro Record and sends it via JMS with the
     * given JMS header content type.
     * @param message Message to adapt and send
     * @param contentType content type to set in the JMS message header
     */
    public void sendMessage(Msg message, String contentType){
        Rec record = getRecord(message);
        if(record == null){
            theLogger.warning(
                    "Adapter returned null Record, unable to send message.");
            return;
        }
        if(myRecordSender instanceof JMSAvroRecordSender){
            ((JMSAvroRecordSender)myRecordSender).sendRecord(
                    record, contentType);
        }else{
            myRecordSender.sendRecord(record);
        }
    }
}

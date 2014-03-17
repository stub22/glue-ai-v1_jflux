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
import javax.jms.MessageConsumer;
import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.jflux.api.messaging.rk.RecordAsyncReceiver;
import org.jflux.api.messaging.rk.RecordAsyncReceiver.RecordHandler;
import org.jflux.impl.messaging.rk.common.QpidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Polls messages from a JMS Destination and attempts to deserialize them as a 
 * specific type of Avro Record.  Records are then sent to a RecordHandler.
 * When this is started it creates a new Thread for a polling loop.
 * 
 * @param <Rec> type of AvroRecord to receive
 * @author Matthew Stevenson <www.robokind.org>
 */
public class JMSAvroRecordAsyncReceiver<Rec extends IndexedRecord> 
        implements RecordAsyncReceiver<Rec> {
    private final static Logger theLogger = LoggerFactory.getLogger(JMSAvroRecordAsyncReceiver.class);
    private Class<Rec> myClass;
    private Schema mySchema;
    private RecordHandler<Rec> myRecordHandler;    
    private JMSBytesRecordAsyncReceiver myBytesReceiver;
    
    
    /**
     * Creates a new JMSAvroRecordAsyncReceiver.
     * @param clazz Class of the Avro Record being received
     * @param schema Avro Schema or the Record being received
     * @param consumer JMS MessageConsumer to fetch the Record
     */
    public JMSAvroRecordAsyncReceiver(
            Class<Rec> clazz, Schema schema, MessageConsumer consumer){
        if(clazz == null || schema == null || consumer == null){
            throw new NullPointerException();
        }
        myClass = clazz;
        mySchema = schema;
        myBytesReceiver = new JMSBytesRecordAsyncReceiver(consumer);
        myBytesReceiver.setRecordHandler(new AvroRecordHandler());
    }
    
    @Override
    public void setRecordHandler(RecordHandler<Rec> handler){
        if(handler == null){
            throw new NullPointerException();
        }
        myRecordHandler = handler;
    }
    
    @Override
    public void unsetRecordHandler(){
        myRecordHandler = null;
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
        myBytesReceiver.start();
    }  
    
    @Override
    public void pause(){
        myBytesReceiver.pause();
    }
    
    @Override
    public void resume(){
        myBytesReceiver.resume();
    }
    
    @Override
    public void stop(){
        myBytesReceiver.stop();
    }
    
    class AvroRecordHandler implements RecordHandler<BytesMessage>{

        @Override
        public void handleRecord(BytesMessage record) {
            Rec t;
            try{
                //theLogger.info("Received JMS BytesMessage.  "
                //        + "Attempting to deserialize Avro Record.");
                t = QpidUtils.unpackAvroMessage(
                        myClass, null, mySchema, record);
            }catch(Exception ex){
                theLogger.error("Error unpacking Message.", ex);
                return;
            }
            if(t == null){
                theLogger.error("Error unpacking Message. "
                        + "Received null record.");
                return;
            }
            myRecordHandler.handleRecord(t);
        }
        
    }
}

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
import org.jflux.api.messaging.rk.RecordBlockingReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Blocks and waits for a JMS BytesMessage when called.
 * @author Matthew Stevenson <www.robokind.org>
 */
public class JMSBytesRecordBlockingReceiver implements RecordBlockingReceiver<BytesMessage> {
    private final static Logger theLogger = LoggerFactory.getLogger(JMSBytesRecordBlockingReceiver.class);
    private MessageConsumer myMessageConsumer;
    /**
     * Creates a new JMSBytesRecordBlcokingReceiver which uses the given 
     * MessageConsumer.
     * @param consumer MessageConsumer to fetch JMS BytesMessages
     */
    public JMSBytesRecordBlockingReceiver(MessageConsumer consumer){
        if(consumer == null){
            throw new NullPointerException();
        }
        myMessageConsumer = consumer;
    }
    
    @Override
    public BytesMessage fetchRecord(long timeout) {
        try{
            Message msg = myMessageConsumer.receive(timeout);
            if(msg == null){
                theLogger.error("Request timed out");
                return null;
            }
            msg.acknowledge();
            if(!(msg instanceof BytesMessage)){
                theLogger.error(
                        "Bad Response, Message not instance of BytesMessage.");
                return null;
            }
            return (BytesMessage)msg;
        }catch(JMSException ex){
            theLogger.error("Error fetching BytesMessage.", ex);
            return null;
        }
    }
    
    @Override
    public int clearRecords(){
        int cleared = 0;
        try{
            Message msg;
            while((msg = myMessageConsumer.receiveNoWait()) != null){
                msg.acknowledge();
                cleared++;
            }
        }catch(JMSException ex){
            theLogger.error("Error clearing Record Receiver.", ex);
        }
        return cleared;
    }
    
}

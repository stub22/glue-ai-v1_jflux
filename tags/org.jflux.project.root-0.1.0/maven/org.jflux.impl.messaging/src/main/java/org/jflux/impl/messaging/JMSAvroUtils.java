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
package org.jflux.impl.messaging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.jflux.api.core.node.chain.ConsumerChain;
import org.jflux.api.core.node.chain.NodeChainBuilder;
import org.jflux.api.core.node.chain.ProducerChain;
import org.jflux.api.core.util.Adapter;
import org.jflux.impl.messaging.avro.AvroStreamEncoder;
import org.jflux.impl.messaging.avro.AvroStreamDecoder;
import org.jflux.impl.messaging.avro.AvroStreamEncoder.ByteOutputStreamFactory;
import org.jflux.impl.messaging.jms.JMSMessageReceiver;
import org.jflux.impl.messaging.jms.JMSMessageSender;
import org.jflux.impl.messaging.jms.MessagePacker;
import org.jflux.impl.messaging.jms.MessageUnpacker;
import org.jflux.impl.messaging.avro.PortableAdapter;
import org.jflux.impl.messaging.avro.PortableEvent;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class JMSAvroUtils {
    
    public static <T, R extends IndexedRecord> 
            ConsumerChain<T> buildEventSenderChain(
            Class<T> eventClass, Class<R> recordClass, Schema schema, 
            Adapter<T,R> eventAdapter, Session session, Destination dest, 
            Adapter<BytesMessage,BytesMessage> optionalProc)
            throws JMSException{
        return new NodeChainBuilder<T>(eventClass)
                .attach(recordClass, eventAdapter)
                .attach(ByteArrayOutputStream.class, 
                        AvroStreamEncoder.buildBinaryEncoder(recordClass, 
                                schema, new ByteOutputStreamFactory()))
                .getConsumerChain(
                        buildJMSSenderChain(session, dest, optionalProc));
    }
    
    public static <T extends PortableEvent<R>, R extends IndexedRecord> 
            ConsumerChain<T> buildPortableEventSenderChain(
            Class<T> eventClass, Class<R> recordClass, Schema schema, 
            Session session, Destination dest, 
            Adapter<BytesMessage,BytesMessage> optionalProc)
            throws Exception{
        return buildEventSenderChain(eventClass, recordClass, schema, 
                new PortableAdapter<T, R>(), session, dest, optionalProc);
    }
    
    public static <T extends IndexedRecord> 
            ConsumerChain<T> buildJMSAvroSenderChain(
            Class<T> recordClass, Schema schema, 
            Session session, Destination dest, 
            Adapter<BytesMessage,BytesMessage> optionalProc)
            throws Exception{
        return new NodeChainBuilder<T>(recordClass)
                .attach(ByteArrayOutputStream.class, 
                        AvroStreamEncoder.buildBinaryEncoder(recordClass, 
                                schema, new ByteOutputStreamFactory()))
                .getConsumerChain(
                        buildJMSSenderChain(session, dest, optionalProc));
    }
    
    public static ConsumerChain<ByteArrayOutputStream> buildJMSSenderChain(
            Session session, Destination dest, 
            Adapter<BytesMessage,BytesMessage> optionalProc)
            throws JMSException {
        NodeChainBuilder<BytesMessage> builder = 
                new NodeChainBuilder<ByteArrayOutputStream>(ByteArrayOutputStream.class)
                        .attach(BytesMessage.class, new MessagePacker(session));
        if(optionalProc != null){
            builder.attach(BytesMessage.class, optionalProc);
        }
        return builder.getConsumerChain(new JMSMessageSender(session, dest));
    }
    
    public static <E, R extends IndexedRecord> 
            ProducerChain<E> buildEventReceiverChain(
            Class<E> eventClass, Class<R> recordClass, Schema schema, 
            Adapter<R,E> recordAdapter, Session session, Destination dest) 
            throws JMSException {
        return new NodeChainBuilder<BytesMessage>(BytesMessage.class, 
                        new JMSMessageReceiver(session, dest))
                .attach(ByteArrayInputStream.class, new MessageUnpacker())
                .attach(recordClass, AvroStreamDecoder.buildBinaryDecoder(
                        ByteArrayInputStream.class, recordClass, schema))
                .attach(eventClass, recordAdapter)
                .getProducerChain();
    }
    
    public static <T extends IndexedRecord> 
            ProducerChain<T> buildJMSAvroReceiverChain(
            Class<T> recordClass, Schema schema, 
            Session session, Destination dest) 
            throws JMSException {
        return new NodeChainBuilder<BytesMessage>(BytesMessage.class, 
                        new JMSMessageReceiver(session, dest))
                .attach(ByteArrayInputStream.class, new MessageUnpacker())
                .attach(recordClass, AvroStreamDecoder.buildBinaryDecoder(
                        ByteArrayInputStream.class, recordClass, schema))
                .getProducerChain();
    }
}

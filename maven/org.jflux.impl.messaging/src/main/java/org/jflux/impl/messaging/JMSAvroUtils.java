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
import org.jflux.api.core.Adapter;
import org.jflux.api.core.node.chain.ConsumerChain;
import org.jflux.api.core.node.chain.NodeChainBuilder;
import org.jflux.api.core.node.chain.ProducerChain;
import org.jflux.impl.messaging.avro.AvroDecoder;
import org.jflux.impl.messaging.avro.AvroEncoder;
import org.jflux.impl.messaging.avro.AvroEncoder.ByteOutputStreamFactory;
import org.jflux.impl.messaging.avro.PortableEvent;
import org.jflux.impl.messaging.avro.PortableEvent.PortableAdapter;
import org.jflux.impl.messaging.jms.JMSMessageReceiver;
import org.jflux.impl.messaging.jms.JMSMessageSender;
import org.jflux.impl.messaging.jms.MessagePacker;
import org.jflux.impl.messaging.jms.MessageUnpacker;

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
        return NodeChainBuilder.build(eventClass)
                .attach(recordClass, eventAdapter)
                .attach(ByteArrayOutputStream.class, 
                        AvroEncoder.buildBinaryEncoder(recordClass, 
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
        return NodeChainBuilder.build(recordClass)
                .attach(ByteArrayOutputStream.class, 
                        AvroEncoder.buildBinaryEncoder(recordClass, 
                                schema, new ByteOutputStreamFactory()))
                .getConsumerChain(
                        buildJMSSenderChain(session, dest, optionalProc));
    }
    
    public static ConsumerChain<ByteArrayOutputStream> buildJMSSenderChain(
            Session session, Destination dest, 
            Adapter<BytesMessage,BytesMessage> optionalProc)
            throws JMSException {
        NodeChainBuilder<ByteArrayOutputStream,BytesMessage> builder = 
                NodeChainBuilder.build(ByteArrayOutputStream.class)
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
        return NodeChainBuilder.build(new JMSMessageReceiver(session, dest))
                .attach(ByteArrayInputStream.class, new MessageUnpacker())
                .attach(recordClass, AvroDecoder.buildBinaryDecoder(
                        ByteArrayInputStream.class, recordClass, schema))
                .attach(eventClass, recordAdapter)
                .getProducerChain();
    }
    
    public static <T extends IndexedRecord> 
            ProducerChain<T> buildJMSAvroReceiverChain(
            Class<T> recordClass, Schema schema, 
            Session session, Destination dest) 
            throws JMSException {
        return NodeChainBuilder.build(new JMSMessageReceiver(session, dest))
                .attach(ByteArrayInputStream.class, new MessageUnpacker())
                .attach(recordClass, AvroDecoder.buildBinaryDecoder(
                        ByteArrayInputStream.class, recordClass, schema))
                .getProducerChain();
    }
}

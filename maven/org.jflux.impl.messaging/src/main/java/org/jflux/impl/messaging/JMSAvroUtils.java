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

import java.io.ByteArrayOutputStream;
import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.Source;
import org.jflux.api.core.node.chain.ConsumerChain;
import org.jflux.api.core.node.chain.NodeChainBuilder;
import org.jflux.api.core.node.chain.ProducerChain;
import org.jflux.api.messaging.encode.EncodeRequest;
import org.jflux.api.messaging.encode.EncodeRequest.InnerAdapter;
import org.jflux.impl.messaging.encode.avro.AvroDecoder;
import org.jflux.impl.messaging.encode.avro.AvroEncoder;
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
            ConsumerChain<EncodeRequest<T,ByteArrayOutputStream>> buildEventSenderChain(
            Class<R> recordClass, Schema schema, 
            Adapter<T,R> eventAdapter, Session session, Destination dest, 
            Adapter<BytesMessage,BytesMessage> optionalProc)
            throws JMSException{
        return NodeChainBuilder.build(new InnerAdapter(eventAdapter))
                .attach(AvroEncoder.buildBinaryEncoder(recordClass, schema))
                .getConsumerChain(
                        buildJMSSenderChain(session, dest, optionalProc));
    }
    
    public static <R extends IndexedRecord> 
            ConsumerChain<EncodeRequest<R,ByteArrayOutputStream>> buildJMSAvroSenderChain(
            Class<R> recordClass, Schema schema, 
            Session session, Destination dest, 
            Adapter<BytesMessage,BytesMessage> optionalProc)
            throws Exception{
        
        return NodeChainBuilder.build(AvroEncoder.buildBinaryEncoder(
                        ByteArrayOutputStream.class, recordClass, schema))
                .getConsumerChain(
                        buildJMSSenderChain(session, dest, optionalProc));
    }
    
    public static ConsumerChain<ByteArrayOutputStream> buildJMSSenderChain(
            Session session, Destination dest, 
            Adapter<BytesMessage,BytesMessage> optionalProc)
            throws JMSException {
        if(optionalProc == null){
            return NodeChainBuilder.build(new MessagePacker(session))
                    .getConsumerChain(new JMSMessageSender(session, dest));
        }
        return NodeChainBuilder.build(new MessagePacker(session))
                .attach(optionalProc)
                .getConsumerChain(new JMSMessageSender(session, dest));
    }
    
    public static <E, R extends IndexedRecord> 
            ProducerChain<E> buildEventReceiverChain(
            Class<R> recordClass, Schema schema, 
            Adapter<R,E> recordAdapter, Session session, Destination dest) 
            throws JMSException {
        return NodeChainBuilder.build(new JMSMessageReceiver(session, dest))
                .attach(new MessageUnpacker())
                .attach(AvroDecoder.buildBinaryDecoder(recordClass, schema))
                .attach(recordAdapter)
                .getProducerChain();
    }
    
    public static <T extends IndexedRecord> 
            ProducerChain<T> buildJMSAvroReceiverChain(
            Class<T> recordClass, Schema schema, 
            Session session, Destination dest) 
            throws JMSException {
        return NodeChainBuilder.build(new JMSMessageReceiver(session, dest))
                .attach(new MessageUnpacker())
                .attach(AvroDecoder.buildBinaryDecoder(recordClass, schema))
                .getProducerChain();
    }
    
    public static <T> Adapter<T, EncodeRequest<
            T,ByteArrayOutputStream>> byteStreamRequestFactory(){
        return EncodeRequest.factory(new ByteOutputStreamFactory());
    }
    public static <T> Adapter<T,EncodeRequest<
            T,ByteArrayOutputStream>> byteStreamRequestFactory(Class<T> clazz){
        return EncodeRequest.factory(new ByteOutputStreamFactory());
    }
    
    public static class ByteOutputStreamFactory implements 
            Source<ByteArrayOutputStream>{
        @Override
        public ByteArrayOutputStream getValue() {
            return new ByteArrayOutputStream();
        }
    }
}

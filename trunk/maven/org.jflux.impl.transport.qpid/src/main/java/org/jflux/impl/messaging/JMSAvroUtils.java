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
import org.jflux.api.encode.EncodeRequest;
import org.jflux.api.encode.EncodeRequest.InnerAdapter;
import org.jflux.impl.encode.avro.AvroDecoder;
import org.jflux.impl.encode.avro.AvroEncoder;
import org.jflux.impl.transport.jms.JMSMessageReceiver;
import org.jflux.impl.transport.jms.JMSMessageSender;
import org.jflux.impl.transport.jms.MessagePacker;
import org.jflux.impl.transport.jms.MessageUnpacker;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class JMSAvroUtils {
    
    /**
     * Build a chain to send events over Qpid.
     * @param <T> the event type
     * @param <R> the record type
     * @param recordClass the record type
     * @param schema the record schema
     * @param eventAdapter the event-to-record processor
     * @param session the Qpid session
     * @param dest the Qpid destination
     * @param optionalProc an optional processor
     * @return a sender chain
     * @throws JMSException
     */
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
    
    /**
     * Build a chain to send Avro messages over Qpid.
     * @param <R> the Avro record type
     * @param recordClass the Avro record type
     * @param schema the Avro record schema
     * @param session the Qpid session
     * @param dest the Qpid destination
     * @param optionalProc an optional processor
     * @return a sender chain
     * @throws Exception
     */
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
    
    /**
     * Build a chain to send arbitrary messages.
     * @param session the Qpid session
     * @param dest the Qpid destination
     * @param optionalProc an optional processor
     * @return a sender chain
     * @throws JMSException
     */
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
    
    /**
     * Build a chain to receive events over Qpid
     * @param <E> the event type
     * @param <R> the record type
     * @param recordClass the record type
     * @param schema the record schema
     * @param recordAdapter the record-to-event processor
     * @param session the Qpid session
     * @param dest the Qpid destination
     * @return a receiver chain
     * @throws JMSException
     */
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
    
    /**
     * Build a chain to receive Avro messages over Qpid.
     * @param <T> the Avro record type
     * @param recordClass the Avro record type
     * @param schema the Avro record schema
     * @param session the Qpid session
     * @param dest the Qpid destination
     * @return a receiver chain
     * @throws JMSException
     */
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
    
    /**
     * Adapter to build a byte stream request.
     * @param <T> the class of the object to convert into a byte stream
     * @return an encode request
     */
    public static <T> Adapter<T, EncodeRequest<
            T,ByteArrayOutputStream>> byteStreamRequestFactory(){
        return EncodeRequest.factory(new ByteOutputStreamFactory());
    }
    /**
     * Adapter to build a byte stream request from a specified type.
     * @param <T> the class of the object to convert into a byte stream
     * @param clazz the class of the object to convert into a byte stream
     * @return an encode request
     */
    public static <T> Adapter<T,EncodeRequest<
            T,ByteArrayOutputStream>> byteStreamRequestFactory(Class<T> clazz){
        return EncodeRequest.factory(new ByteOutputStreamFactory());
    }
    
    /**
     * Class to build a byte stream.
     */
    public static class ByteOutputStreamFactory implements 
            Source<ByteArrayOutputStream>{
        /**
         * Builds a byte stream.
         * @return the byte stream
         */
        @Override
        public ByteArrayOutputStream getValue() {
            return new ByteArrayOutputStream();
        }
    }
}

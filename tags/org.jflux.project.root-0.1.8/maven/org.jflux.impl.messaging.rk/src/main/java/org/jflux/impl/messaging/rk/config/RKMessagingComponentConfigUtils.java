/*
 * Copyright 2012 Hanson Robokind LLC.
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
package org.jflux.impl.messaging.rk.config;

import org.jflux.impl.messaging.rk.utils.JMSAvroPolymorphicRecordBytesAdapter;
import javax.jms.BytesMessage;
import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.config.Configuration;
import org.jflux.api.messaging.rk.MessageAsyncReceiver;
import org.jflux.api.messaging.rk.MessageBlockingReceiver;
import org.jflux.api.messaging.rk.MessageSender;

import org.jflux.impl.messaging.rk.lifecycle.BytesMessageBlockingReceiverLifecycle;
import org.jflux.impl.messaging.rk.lifecycle.JMSAvroAsyncReceiverLifecycle;
import org.jflux.impl.messaging.rk.lifecycle.JMSAvroMessageSenderLifecycle;
import org.jflux.impl.messaging.rk.lifecycle.JMSAvroPolymorphicSenderLifecycle;
import static org.jflux.impl.encode.avro.SerializationConfigUtils.*;
import org.jflux.impl.services.rk.lifecycle.ServiceLifecycleProvider;

/**
 *
 * @author Matthew Stevenson
 */
public class RKMessagingComponentConfigUtils {
    public static <Msg,Rec extends IndexedRecord> 
            ServiceLifecycleProvider<MessageSender> buildAvroRemoteNotifierLifecycle(
                    String remoteNotifierId, 
                    String destinatinId,
                    String sessionId, 
                    Configuration<String> avroConfig){
        Class<Msg> messageClass = avroConfig.getPropertyValue(Class.class, CONF_MESSAGE_CLASS);
        Class<Rec> recordClass = avroConfig.getPropertyValue(Class.class, CONF_OUTPUT_CLASS);
        Adapter<Msg,Rec> encoder = avroConfig.getPropertyValue(Adapter.class, CONF_ENCODING_ADAPTER);
        String contentType = avroConfig.getPropertyValue(String.class, CONF_CONTENT_TYPE);
        
        return new JMSAvroMessageSenderLifecycle<Msg, Rec>(
                encoder, messageClass, recordClass, 
                remoteNotifierId, sessionId, destinatinId, contentType);
    }
    
    public static <Msg,Rec extends IndexedRecord> 
            ServiceLifecycleProvider<MessageAsyncReceiver> 
            buildAvroRemoteListenerLifecycle(
                    String remoteListenerId, 
                    String destinatinId, 
                    String sessionId, 
                    Configuration<String> avroConfig){
        Class<Msg> messageClass = avroConfig.getPropertyValue(Class.class, CONF_MESSAGE_CLASS);
        Class<Rec> recordClass = avroConfig.getPropertyValue(Class.class, CONF_OUTPUT_CLASS);
        Adapter<Rec,Msg> decoder = avroConfig.getPropertyValue(Adapter.class, CONF_DECODING_ADAPTER);
        Schema recordSchema = avroConfig.getPropertyValue(Schema.class, CONF_AVRO_RECORD_SCHEMA);
        
        return new JMSAvroAsyncReceiverLifecycle<Msg, Rec>(
                decoder, messageClass, recordClass, recordSchema, 
                remoteListenerId, sessionId, destinatinId);
    }
    
    public static <Msg> ServiceLifecycleProvider<MessageBlockingReceiver> 
            buildJMSRemoteSourceLifecycle(
                    String remoteSourceId, 
                    String destinatinId, 
                    String sessionId, 
                    Configuration<String> config){
        Class recordClass = 
                config.getPropertyValue(Class.class, CONF_OUTPUT_CLASS);
        if(!recordClass.isAssignableFrom(BytesMessage.class)){
            throw new UnsupportedOperationException();
        }
        Class<Msg> messageClass = 
                config.getPropertyValue(Class.class, CONF_MESSAGE_CLASS);
        Adapter<BytesMessage,Msg> decoder = 
                config.getPropertyValue(Adapter.class, CONF_DECODING_ADAPTER);
        
        return new BytesMessageBlockingReceiverLifecycle<Msg>(
                decoder, messageClass, remoteSourceId, 
                sessionId, destinatinId);
    }
    
    public static <Msg> ServiceLifecycleProvider<MessageSender> 
            buildJMSAvroPolymorphicRemoteNotifierLifecycle(
                    String remoteNotifierId, 
                    String destinatinId, 
                    String sessionId, 
                    Configuration<String> config){
        Class<Msg> messageClass = config.getPropertyValue(
                Class.class, CONF_MESSAGE_CLASS);
        JMSAvroPolymorphicRecordBytesAdapter<Msg> decoder = 
                (JMSAvroPolymorphicRecordBytesAdapter)config.getPropertyValue(
                        Adapter.class, CONF_DECODING_ADAPTER);
        
        return new JMSAvroPolymorphicSenderLifecycle<Msg>(
                decoder, messageClass, remoteNotifierId, 
                sessionId, destinatinId);
    }
}

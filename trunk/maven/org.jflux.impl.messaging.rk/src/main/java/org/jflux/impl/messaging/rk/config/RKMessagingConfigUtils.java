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

import java.util.Properties;
import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.config.Configuration;
import org.jflux.impl.encode.avro.SerializationConfigUtils;
import org.jflux.impl.services.rk.lifecycle.ManagedService;
import org.jflux.impl.services.rk.lifecycle.ServiceLifecycleProvider;
import org.jflux.impl.services.rk.lifecycle.utils.ManagedServiceFactory;
import org.jflux.impl.services.rk.lifecycle.utils.SimpleLifecycle;

/**
 *
 * @author Matthew Stevenson
 */
public class RKMessagingConfigUtils {
    public final static String JMS_CONNECTION_CONFIG = "messagingConnectionConfig";
    public final static String JMS_DESTINATION_CONFIG = "messagingDestinationConfig";
    public final static String SERIALIZATION_CONFIG = "messagingSerializationConfig";
    
    public final static String PROP_BROKER_IP = "brokerIpAddress";
    public final static String PROP_DESTINATION_NAME = "destinationName";
    public final static String PROP_DESTINATION_TYPE = "destinationType";
    
    public final static String AVRO_PREFIX = "avro";
    
    public static ManagedService registerConnectionConfig(
            String configId, Configuration<String> conf, 
            Properties props, ManagedServiceFactory factory){
        return registerConfig(JMS_CONNECTION_CONFIG, 
                configId, conf, props, factory);
    }
    
    public static ManagedService registerConnectionConfig(
            String configId, String ipAddress, String port, 
            String username, String password, String clientName, 
            String virtualHost, Properties props, 
            ManagedServiceFactory factory){
        Configuration<String> conf = ConnectionConfigUtils.buildConnectionConfig(
                ipAddress, port, username, password, clientName, virtualHost);
        props = props == null ? new Properties() : props;
        props.put(PROP_BROKER_IP, conf.getPropertyValue(
                ConnectionConfigUtils.CONF_BROKER_IP));
        return registerConnectionConfig(configId, conf, props, factory);
    }
    
    public static ManagedService registerConnectionConfig(
            String configId, String ipAddress, 
            Properties props, ManagedServiceFactory factory){
        return registerConnectionConfig(configId, ipAddress, 
                null, null, null, null, null, props, factory);             
    }
    
    public static ManagedService registerDestinationConfig(
            String configId, Configuration<String> conf, 
            Properties props, ManagedServiceFactory factory){
        return registerConfig(JMS_DESTINATION_CONFIG, 
                configId, conf, props, factory);
    }
    
    public static ManagedService registerDestinationConfig(
            String configId, String name, String type, String create, 
            String options, String nodeOptions, Properties props, 
            ManagedServiceFactory factory){
        Configuration<String> conf = ConnectionConfigUtils.buildDestinationConfig(
                name, type, create, options, nodeOptions);
        props = props == null ? new Properties() : props;
        props.put(PROP_DESTINATION_NAME, conf.getPropertyValue(
                ConnectionConfigUtils.CONF_DESTINATION_NAME));
        props.put(PROP_DESTINATION_TYPE, conf.getPropertyValue(
                ConnectionConfigUtils.CONF_DESTINATION_NODE_TYPE));
        return registerDestinationConfig(configId, conf, props, factory);
    }
    
    public static ManagedService registerDestinationConfig(
            String configId, String name, String type, 
            Properties props, ManagedServiceFactory factory){
        return registerDestinationConfig(configId, name, type, 
                ConnectionConfigUtils.CREATE_ALWAYS, 
                null, null, props, factory);
    }
    
    public static ManagedService registerQueueConfig(
            String configId, String name, 
            Properties props, ManagedServiceFactory factory){
        return registerDestinationConfig(configId, name, 
                ConnectionConfigUtils.NODE_QUEUE, 
                ConnectionConfigUtils.CREATE_ALWAYS, 
                null, null, props, factory);
    }
    
    public static ManagedService registerTopicConfig(
            String configId, String name, 
            Properties props, ManagedServiceFactory factory){
        return registerDestinationConfig(configId, name, 
                ConnectionConfigUtils.NODE_TOPIC, 
                ConnectionConfigUtils.CREATE_ALWAYS, 
                null, null, props, factory);
    }
    
    public static <Msg,Rec> ManagedService registerSerializationConfig(
            Class<Msg> messageClass, Class<Rec> recordClass,
            Adapter<Msg,Rec> encoder, Adapter<Rec,Msg> decoder,
            String contentType, Properties props, 
            ManagedServiceFactory factory){
        Configuration<String> conf = 
                SerializationConfigUtils.buildSerializationConfig(
                        messageClass, recordClass, 
                        encoder, decoder, contentType);
        return registerSerializationConfig(messageClass.toString(), conf, props, factory);
    }
    
    public static <Msg,Rec extends IndexedRecord> ManagedService registerAvroSerializationConfig(
            Class<Msg> messageClass, Class<Rec> recordClass, 
            Schema recordSchema, Adapter<Msg,Rec> encoder, 
            Adapter<Rec,Msg> decoder, String contentType, Properties props, 
            ManagedServiceFactory factory){
        Configuration<String> conf = 
                SerializationConfigUtils.buildAvroSerializationConfig(
                        messageClass, recordClass, 
                        encoder, decoder, 
                        contentType, recordSchema, 
                        SerializationConfigUtils.DEF_AVRO_ENCODING_TYPE);
        return registerSerializationConfig(messageClass.toString(), conf, props, factory);
    }
    
    public static ManagedService registerSerializationConfig(
            String configId, Configuration<String> conf, 
            Properties props, ManagedServiceFactory factory){
        props = props == null ? new Properties() : props;
        props.put(SERIALIZATION_CONFIG, configId);
        props.put(SerializationConfigUtils.CONF_MESSAGE_CLASS, 
                conf.getPropertyValue(SerializationConfigUtils.CONF_MESSAGE_CLASS).toString());
        props.put(SerializationConfigUtils.CONF_OUTPUT_CLASS, 
                conf.getPropertyValue(SerializationConfigUtils.CONF_OUTPUT_CLASS).toString());
        String contentType = conf.getPropertyValue(
                String.class, SerializationConfigUtils.CONF_CONTENT_TYPE);
        if(contentType != null){
            props.put(
                SerializationConfigUtils.CONF_CONTENT_TYPE, contentType);
        }
        return registerConfig(SERIALIZATION_CONFIG, 
                configId, conf, props, factory);
    }
    
    static ManagedService registerConfig(
            String key, String id, Configuration<String> conf, 
            Properties props, ManagedServiceFactory factory){
        if(factory == null){
            throw new NullPointerException();
        }
        ServiceLifecycleProvider lifecycle = 
                new SimpleLifecycle(conf, Configuration.class, key, id, null);
        ManagedService service = factory.createService(lifecycle, props);
        service.start();
        return service;
    }
}

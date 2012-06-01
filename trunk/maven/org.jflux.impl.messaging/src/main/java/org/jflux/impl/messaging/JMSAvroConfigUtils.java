/*
 * Copyright 2012 The JFlux Project (www.jflux.org).
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

import org.apache.avro.generic.IndexedRecord;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.config.Configuration;
import org.jflux.api.core.config.DefaultConfiguration;

/**
 *
 * @author Matthew Stevenson
 */
public class JMSAvroConfigUtils {
    public final static String JMSCONF_CONNECTION = "jmsAvroConnectionConfig";
    public final static String JMSCONF_DESTINATION = "jmsAvroDestinationConfig";
    public final static String AVROCONF_RECORD = "jmsAvroRecordConfig";
    
    public final static String CONF_MESSENGER_TYPE = "jmsavroMessengerType";
    public final static String CONF_MESSAGE_CLASS = "jmsavroMessageClass";
    public final static String CONF_SENDER_ADAPTER = "jmsavroSenderMessageRecordAdapter";
    public final static String CONF_RECEIVER_ADAPTER = "jmsAvroReceiverRecordMessageAdapter";
    public final static String CONF_CONTENT_TYPE = "jmsavroContentType";
    
    public final static int MESSAGE_SENDER = 0;
    public final static int MESSAGE_RECEIVER = 1;
    
    
    private static <M,R extends IndexedRecord> DefaultConfiguration<String> 
            buildMessengerConfig(Class<M> messageClass, 
                    Configuration<String> jmsConnectionConfig, 
                    Configuration<String> jmsDestinationConfig, 
                    Configuration<String> avroRecordConfig,
                    String contentType){
        DefaultConfiguration<String> conf = new DefaultConfiguration<String>();
        conf.addProperty(Class.class, CONF_MESSAGE_CLASS, messageClass);
        conf.addProperty(String.class, CONF_CONTENT_TYPE, contentType);
        conf.addProperty(Configuration.class, JMSCONF_CONNECTION, jmsConnectionConfig);
        conf.addProperty(Configuration.class, JMSCONF_DESTINATION, jmsDestinationConfig);
        conf.addProperty(Configuration.class, AVROCONF_RECORD, avroRecordConfig);
        return conf;
    }
    
    public static <M,R extends IndexedRecord> Configuration<String> 
            buildSenderConfig(Class<M> messageClass, 
                    Configuration<String> jmsConnectionConfig, 
                    Configuration<String> jmsDestinationConfig, 
                    Configuration<String> avroRecordConfig,
                    Adapter<M,R> senderMessageRecordAdapater,
                    String contentType){
        DefaultConfiguration<String> conf = 
                buildMessengerConfig(messageClass, jmsConnectionConfig, 
                        jmsDestinationConfig, avroRecordConfig, contentType);
        
        conf.addProperty(Integer.class, CONF_MESSENGER_TYPE, MESSAGE_SENDER);
        conf.addProperty(Adapter.class, CONF_SENDER_ADAPTER, senderMessageRecordAdapater);
        return conf;
    }
    
    public static <M,R extends IndexedRecord> Configuration<String> 
            buildReceiverConfig(Class<M> messageClass, 
                    Configuration<String> jmsConnectionConfig, 
                    Configuration<String> jmsDestinationConfig, 
                    Configuration<String> avroRecordConfig,
                    Adapter<R,M> receiverRecordMessageAdapater,
                    String contentType){
        DefaultConfiguration<String> conf = 
                buildMessengerConfig(messageClass, jmsConnectionConfig, 
                        jmsDestinationConfig, avroRecordConfig, contentType);
        
        conf.addProperty(Integer.class, CONF_MESSENGER_TYPE, MESSAGE_RECEIVER);
        conf.addProperty(Adapter.class, CONF_RECEIVER_ADAPTER, receiverRecordMessageAdapater);
        return conf;
    }
}

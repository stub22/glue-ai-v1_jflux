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
import org.jflux.api.core.config.Configuration;
import org.jflux.api.core.config.DefaultConfiguration;

/**
 *
 * @author Matthew Stevenson
 */
public class MessagingConfigUtils {
    public final static String CONFIG_JMS_CONNECTION = "messagingConnectionConfig";
    public final static String CONFIG_JMS_DESTINATION = "messagingDestinationConfig";
    public final static String CONFIG_SERIALIZATION = "messagingSerializationConfig";
    
    private static <M,R extends IndexedRecord> DefaultConfiguration<String> 
            buildBaseConfig(
                    Configuration<String> jmsConnectionConfig, 
                    Configuration<String> jmsDestinationConfig, 
                    Configuration<String> serializationConfig){
        DefaultConfiguration<String> conf = new DefaultConfiguration<String>();
        conf.addProperty(Configuration.class, CONFIG_JMS_CONNECTION, jmsConnectionConfig);
        conf.addProperty(Configuration.class, CONFIG_JMS_DESTINATION, jmsDestinationConfig);
        conf.addProperty(Configuration.class, CONFIG_SERIALIZATION, serializationConfig);
        return conf;
    }
    
    public static <M,R extends IndexedRecord> Configuration<String> 
            buildMessengerConfig(
                    Configuration<String> jmsConnectionConfig, 
                    Configuration<String> jmsDestinationConfig, 
                    Configuration<String> serializationConfig){
        return buildBaseConfig(
                jmsConnectionConfig, jmsDestinationConfig, serializationConfig);
    }
}

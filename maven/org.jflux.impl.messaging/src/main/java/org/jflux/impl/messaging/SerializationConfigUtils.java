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

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.config.Configuration;
import org.jflux.api.core.config.DefaultConfiguration;

/**
 *
 * @author Matthew Stevenson
 */
public class SerializationConfigUtils {
    public final static String CONF_MESSAGE_CLASS = "serializationMessageClass";
    public final static String CONF_RECORD_CLASS = "serializationRecordClass";
    public final static String CONF_ENCODING_ADAPTER = "serializationSenderMsgRecAdapter";
    public final static String CONF_DECODING_ADAPTER = "serializationReceiverRecMsgAdapter";
    public final static String CONF_CONTENT_TYPE = "serializationContentType";
    
    public final static String CONF_AVRO_RECORD_SCHEMA = "avroRecordSchema";
    public final static String CONF_AVRO_ENCODING_TYPE = "avroEncodingType";
    
    public final static int ENCODING_BINARY = 0;
    public final static int ENCODING_JSON = 1;
    
    public final static int DEF_AVRO_ENCODING_TYPE = ENCODING_BINARY;
    
    private static <Msg,Rec> DefaultConfiguration<String> buildBaseConfig(
            Class<Msg> messageClass, Class<Rec> recordClass,
            Adapter<Msg,Rec> encoder, Adapter<Rec,Msg> decoder,
            String contentType){
        DefaultConfiguration<String> conf = new DefaultConfiguration<String>();
        conf.addProperty(Class.class, CONF_MESSAGE_CLASS, messageClass);
        conf.addProperty(Class.class, CONF_RECORD_CLASS, recordClass);
        conf.addProperty(Adapter.class, CONF_ENCODING_ADAPTER, encoder);
        conf.addProperty(Adapter.class, CONF_DECODING_ADAPTER, decoder);
        conf.addProperty(String.class, CONF_CONTENT_TYPE, contentType);
        return conf;
    }
    
    public static <Msg,Rec> Configuration<String> buildSerializationConfig(
            Class<Msg> messageClass, Class<Rec> recordClass,
            Adapter<Msg,Rec> encoder, Adapter<Rec,Msg> decoder,
            String contentType){
        return buildBaseConfig(
                messageClass, recordClass, encoder, decoder, contentType);
    }
    
    public static <Msg,Rec extends IndexedRecord> Configuration<String> 
            buildAvroSerializationConfig(
                    Class<Msg> messageClass, Class<Rec> recordClass,
                    Adapter<Msg,Rec> encoder, Adapter<Rec,Msg> decoder,
                    String contentType, Schema recordSchema, int encoding){
        DefaultConfiguration<String> conf = buildBaseConfig(
                messageClass, recordClass, encoder, decoder, contentType);
        conf.addProperty(Schema.class, CONF_AVRO_RECORD_SCHEMA, recordSchema);
        conf.addProperty(Integer.class, CONF_AVRO_ENCODING_TYPE, encoding);
        return conf;
    }
}

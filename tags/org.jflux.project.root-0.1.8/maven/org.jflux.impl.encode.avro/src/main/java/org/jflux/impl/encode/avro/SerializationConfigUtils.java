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
package org.jflux.impl.encode.avro;

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
    /**
     * Message class key.
     */
    public final static String CONF_MESSAGE_CLASS = "serializationMessageClass";
    /**
     * Record class key.
     */
    public final static String CONF_OUTPUT_CLASS = "serializationRecordClass";
    /**
     * Content type key.
     */
    public final static String CONF_CONTENT_TYPE = "serializationContentType";
    /**
     * Encoder key.
     */
    public final static String CONF_ENCODING_ADAPTER = "serializationSenderEncoderAdapter";
    /**
     * Decoder key.
     */
    public final static String CONF_DECODING_ADAPTER = "serializationReceiverDecoderAdapter";
    
    /**
     * Schema key.
     */
    public final static String CONF_AVRO_RECORD_SCHEMA = "avroRecordSchema";
    /**
     * Encoding type key.
     */
    public final static String CONF_AVRO_ENCODING_TYPE = "avroEncodingType";
    
    /**
     * Binary encoding type.
     */
    public final static int ENCODING_BINARY = 0;
    /**
     * JSON encoding type.
     */
    public final static int ENCODING_JSON = 1;
    
    /**
     * Default encoding type (binary).
     */
    public final static int DEF_AVRO_ENCODING_TYPE = ENCODING_BINARY;
    
    private static <Msg,Rec> DefaultConfiguration<String> buildBaseConfig(
            Class<Msg> messageClass, Class<Rec> recordClass,
            Adapter<Msg,Rec> encoder, Adapter<Rec,Msg> decoder,
            String contentType){
        DefaultConfiguration<String> conf = new DefaultConfiguration<String>();
        conf.addProperty(Class.class, CONF_MESSAGE_CLASS, messageClass);
        conf.addProperty(Class.class, CONF_OUTPUT_CLASS, recordClass);
        conf.addProperty(Adapter.class, CONF_ENCODING_ADAPTER, encoder);
        conf.addProperty(Adapter.class, CONF_DECODING_ADAPTER, decoder);
        conf.addProperty(String.class, CONF_CONTENT_TYPE, contentType);
        return conf;
    }
    
    /**
     * Creates a serialization config with user-specified parameters.
     * @param <Msg> type of object that represents the data
     * @param <Rec> type of data
     * @param messageClass type of object that represents the data
     * @param recordClass type of data
     * @param encoder Adapter to encode object into raw data
     * @param decoder Adapter to decode raw data into object
     * @param contentType content type
     * @return
     */
    public static <Msg,Rec> Configuration<String> buildSerializationConfig(
            Class<Msg> messageClass, Class<Rec> recordClass,
            Adapter<Msg,Rec> encoder, Adapter<Rec,Msg> decoder,
            String contentType){
        return buildBaseConfig(
                messageClass, recordClass, encoder, decoder, contentType);
    }
    
    /**
     * Creates an Avro serialization config with user-specified parameters.
     * @param <Msg> type of object that represents the data
     * @param <Rec> type of data
     * @param messageClass type of object that represents the data
     * @param recordClass type of data
     * @param encoder Adapter to encode object into raw data
     * @param decoder Adapter to decode raw data into object
     * @param contentType content type
     * @param recordSchema the record's schema
     * @param encoding type of encoding to use
     * @return
     */
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

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
package org.jflux.impl.messaging.avro;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.jflux.api.core.Listener;
import org.jflux.api.core.config.Configuration;
import org.jflux.api.core.config.DefaultConfiguration;

/**
 *
 * @author Matthew Stevenson
 */
public class AvroConfigUtils {
    public final static String CONF_AVRO_RECORD_CLASS = "avroRecordClass";
    public final static String CONF_AVRO_RECORD_SCHEMA = "avroRecordSchema";
    public final static String CONF_AVRO_ENCODING_TYPE = "avroEncodingType";
    
    public final static int ENCODING_BINARY = 0;
    public final static int ENCODING_JSON = 1;
    
    public final static int DEF_AVRO_ENCODING_TYPE = ENCODING_BINARY;
    
    public static <T extends IndexedRecord> Configuration<String> 
            buildAvroRecordConfig(Class<T> clazz, Schema schema){
        DefaultConfiguration<String> conf = new DefaultConfiguration<String>();
        conf.addProperty(Class.class, CONF_AVRO_RECORD_CLASS, clazz);
        conf.addProperty(Schema.class, CONF_AVRO_RECORD_SCHEMA, schema);
        conf.addProperty(Integer.class, CONF_AVRO_ENCODING_TYPE, DEF_AVRO_ENCODING_TYPE);        
        return conf;
    }
    
    public static <T extends IndexedRecord> Configuration<String> 
            buildAvroRecordConfig(Class<T> clazz, Schema schema, int encoding){
        Configuration<String> conf = buildAvroRecordConfig(clazz, schema);
        Listener<Integer> setter = 
                conf.getPropertySetter(Integer.class, CONF_AVRO_ENCODING_TYPE);
        setter.handleEvent(encoding);     
        return conf;
    }
}

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
package org.jflux.impl.encode.avro;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.jflux.api.core.Adapter;
import org.jflux.api.encode.EncodeRequest;

/**
 * Encodes record objects into Avro data
 * @param <T> the type of record object to encode from
 * @param <S> the type of message stream to encode to
 * @author Matthew Stevenson <www.jflux.org>
 */
public class AvroEncoder<T extends IndexedRecord, S extends OutputStream> 
        implements Adapter<EncodeRequest<T,S>,S>{
    private final static Logger theLogger = 
            Logger.getLogger(AvroEncoder.class.getName());
    private DatumWriter<T> myWriter;
    private EncoderFactory myEncoderFactory;
    private boolean myJsonFlag;
    private Schema mySchema;
    
    /**
     * Builds an encoder to convert from a SpecificRecordBase.
     * @param <R> the type of SpecificRecordBase
     * @param <S> the type of InputStream
     * @param clazz the type of SpecificRecordBase
     * @return AvroEncoder to convert from a SpecificRecordBase
     */
    public static <R extends SpecificRecordBase, S extends OutputStream> 
            Adapter<EncodeRequest<R,S>,S> buildSpecificBinaryEncoder(Class<R> clazz){
        return new AvroEncoder<R,S>(clazz, null, false);
    }
    /**
     * Builds an encoder to convert according to a schema.
     * @param <R> the type of IndexedRecord
     * @param <S> the type of InputStream
     * @param clazz the type of IndexedRecord
     * @param schema the Schema to use to convert
     * @return AvroEncoder to convert an object into raw data via schema
     */
    public static <R extends IndexedRecord, S extends OutputStream> 
            Adapter<EncodeRequest<R,S>,S> buildBinaryEncoder(
                    Class<R> clazz, Schema schema){
        return new AvroEncoder<R,S>(clazz, schema, false);
    }
    /**
     * Used when the Stream type needs to be specified.
     * @param <R> the type of IndexedRecord
     * @param <S> the type of InputStream
     * @param streamType the type of InputStream
     * @param clazz the type of IndexedRecord 
     * @param schema the Schema to use to convert
     * @return AvroEncoder to convert an object into raw data via schema
     */
    public static <R extends IndexedRecord, S extends OutputStream> 
            Adapter<EncodeRequest<R,S>,S> buildBinaryEncoder(
                    Class<S> streamType, Class<R> clazz, Schema schema){
        return new AvroEncoder<R,S>(clazz, schema, false);
    }
    
    /**
     * Builds an AvroEncoder to convert to a JSON file
     * @param <R> the type of IndexedRecord
     * @param <S> the type of InputStream (JSON-based)
     * @param clazz the type of IndexedRecord 
     * @param schema the Schema to use to convert
     * @return AvroEncoder to convert an object into a JSON file
     */
    public static <R extends IndexedRecord, S extends OutputStream> 
            Adapter<EncodeRequest<R,S>,S> buildJsonEncoder(
                    Class<R> clazz, Schema schema){
        return new AvroEncoder<R,S>(clazz, schema, true);
    }
    
    /**
     * @return AvroEncoder to convert an object into Avro format
     * @param clazz the type of IndexedRecord 
     * @param schema the Schema to use to convert
     * @param json determines whether or not to convert to JSON
     */
    public AvroEncoder(Class<T> clazz, Schema schema, boolean json){
        if((clazz == null && schema == null) || (json && schema == null)){
            throw new NullPointerException();
        }
        myEncoderFactory = EncoderFactory.get();
        if(clazz != null && SpecificRecordBase.class.isAssignableFrom(clazz)){
            myWriter = new SpecificDatumWriter<T>(clazz);
        }else{
            myWriter = new GenericDatumWriter<T>(schema);
        }
        myJsonFlag = json;
        mySchema = schema;
    }
    
    /**
     * @return AvroEncoder to convert an object into Avro format
     * @param schema the Schema to use to convert
     * @param specific determines whether or not we use a specific encoder
     * @param json determines whether or not to convert to JSON
     */
    public AvroEncoder(Schema schema, boolean specific, boolean json){
        if(schema == null){
            throw new NullPointerException();
        }
        myEncoderFactory = EncoderFactory.get();
        myWriter = specific ? new SpecificDatumWriter<T>(schema) 
                : new GenericDatumWriter<T>(schema);
        myJsonFlag = json;
        mySchema = schema;
    }
    
    /**
     * Sets the type of record to convert.
     * @param clazz the type of record
     * @param schema the record's schema
     */
    public void setType(Class<T> clazz, Schema schema){
        if((clazz == null && schema == null) || (myJsonFlag && schema == null)){
            myWriter = null;
            return;
        }
        mySchema = schema;
        if(clazz != null && SpecificRecordBase.class.isAssignableFrom(clazz)){
            myWriter = new SpecificDatumWriter<T>(clazz);
        }else{
            myWriter = new GenericDatumWriter<T>(mySchema);
        }
    }
    
    @Override
    public S adapt(EncodeRequest<T, S> a) {
        if(a == null || a.getValue() == null || 
                a.getStream() == null || myWriter == null){
            return null;
        }
        try{
            S out = a.getStream();
            Encoder e = (myJsonFlag ? 
                    myEncoderFactory.jsonEncoder(mySchema, out) : 
                    myEncoderFactory.binaryEncoder(out, null));
            myWriter.write(a.getValue(), e);
            e.flush();
            return out;
        }catch(IOException ex){
            theLogger.log(Level.WARNING, 
                    "Error writing Avro record to OutputStream.", ex);
            return null;
        }
    }
}

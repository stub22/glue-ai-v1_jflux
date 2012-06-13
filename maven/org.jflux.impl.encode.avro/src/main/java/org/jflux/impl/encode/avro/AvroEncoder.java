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
 *
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
    
    public static <R extends SpecificRecordBase, S extends OutputStream> 
            Adapter<EncodeRequest<R,S>,S> buildSpecificBinaryEncoder(Class<R> clazz){
        return new AvroEncoder<R,S>(clazz, null, false);
    }
    public static <R extends IndexedRecord, S extends OutputStream> 
            Adapter<EncodeRequest<R,S>,S> buildBinaryEncoder(
                    Class<R> clazz, Schema schema){
        return new AvroEncoder<R,S>(clazz, schema, false);
    }
    /**
     * Used when the Stream type needs to be specified.
     * @param <R>
     * @param <S>
     * @param streamType
     * @param clazz
     * @param schema
     * @return 
     */
    public static <R extends IndexedRecord, S extends OutputStream> 
            Adapter<EncodeRequest<R,S>,S> buildBinaryEncoder(
                    Class<S> streamType, Class<R> clazz, Schema schema){
        return new AvroEncoder<R,S>(clazz, schema, false);
    }
    
    public static <R extends IndexedRecord, S extends OutputStream> 
            Adapter<EncodeRequest<R,S>,S> buildJsonEncoder(
                    Class<R> clazz, Schema schema){
        return new AvroEncoder<R,S>(clazz, schema, true);
    }
    
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
        if(myWriter == null){
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

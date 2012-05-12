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
package org.jflux.impl.messaging.avro;

import java.io.ByteArrayOutputStream;
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
import org.jflux.api.core.util.Adapter;
import org.jflux.api.core.util.Factory;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class AvroEncoder<T extends IndexedRecord, S extends OutputStream> 
        implements  Adapter<T,S>{
    private final static Logger theLogger = 
            Logger.getLogger(AvroEncoder.class.getName());
    private Factory<S> myStreamFactory;
    private DatumWriter<T> myWriter;
    private EncoderFactory myEncoderFactory;
    private boolean myJsonFlag;
    private Schema mySchema;

    public static <R extends SpecificRecordBase> 
            AvroEncoder<R,ByteArrayOutputStream> 
            buildByteStreamEncoder(Class<R> clazz){
        return new AvroEncoder<R,ByteArrayOutputStream>(
                clazz, null, new ByteOutputStreamFactory(), false);
    }
    
    public static <R extends IndexedRecord, S extends OutputStream> 
            AvroEncoder<R,S> buildBinaryEncoder(
                    Class<R> clazz, Schema schema, Factory<S> streamFact){
        return new AvroEncoder<R,S>(clazz, schema, streamFact, false);
    }
    
    public static <R extends IndexedRecord, S extends OutputStream> 
            AvroEncoder<R,S> buildJsonEncoder(
                    Class<R> clazz, Schema schema, Factory<S> streamFact){
        return new AvroEncoder<R,S>(clazz, schema, streamFact, true);
    }
    
    public AvroEncoder(Class<T> clazz, Schema schema, 
            Factory<S> streamFact, boolean json){
        if(streamFact == null 
                || (clazz == null && schema == null)
                || (json && schema == null)){
            throw new NullPointerException();
        }
        myEncoderFactory = EncoderFactory.get();
        myStreamFactory = streamFact;
        if(clazz != null && SpecificRecordBase.class.isAssignableFrom(clazz)){
            myWriter = new SpecificDatumWriter<T>(clazz);
        }else{
            myWriter = new GenericDatumWriter<T>(schema);
        }
        myJsonFlag = json;
        mySchema = schema;
    }
    
    @Override
    public S adapt(T a) {
        try{
            S out = myStreamFactory.create();
            if(out == null){
                theLogger.warning("Error encoding Avro record.  "
                        + "Unable to create OutputStream.");
                return null;
            }
            Encoder e = (myJsonFlag ? 
                    myEncoderFactory.jsonEncoder(mySchema, out) : 
                    myEncoderFactory.binaryEncoder(out, null));
            myWriter.write(a, e);
            e.flush();
            return out;
        }catch(IOException ex){
            theLogger.log(Level.WARNING, 
                    "Error writing Avro record to OutputStream.", ex);
            return null;
        }
    }
    
    public static class ByteOutputStreamFactory implements 
            Factory<ByteArrayOutputStream>{
        @Override
        public ByteArrayOutputStream create() {
            return new ByteArrayOutputStream();
        }
        
    }
}

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
package org.jflux.impl.messaging.encode.avro;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.jflux.api.core.Adapter;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class AvroDecoder<S extends InputStream, T extends IndexedRecord> 
        implements Adapter<S,T>{
    private final static Logger theLogger = 
            Logger.getLogger(AvroDecoder.class.getName());
    private DatumReader<T> myReader;
    private DecoderFactory myDecoderFactory;
    private boolean myJsonFlag;
    private Schema mySchema;
    
    public static <R extends SpecificRecordBase> 
            AvroDecoder<ByteArrayInputStream,R> 
            buildByteStreamDecoder(Class<R> outputClass){
        return new AvroDecoder<ByteArrayInputStream,R>(
                outputClass, null, false);
    }
    
    public static <S extends InputStream, R extends SpecificRecordBase> 
            AvroDecoder<S,R> buildSpecificBinaryDecoder(Class<R> outputClass){
        return new AvroDecoder<S,R>(outputClass, null, false);
    }
    
    public static <S extends InputStream, R extends IndexedRecord> 
            AvroDecoder<S,R> buildBinaryDecoder(
                    Class<R> outputClass, Schema schema){
        return new AvroDecoder<S,R>(outputClass, schema, false);
    }
    
    public static <S extends InputStream, R extends IndexedRecord> 
            AvroDecoder<S,R> buildJsonDecoder(
                    Class<S> inputClass, Class<R> outputClass, Schema schema){
        return new AvroDecoder<S,R>(outputClass, schema, true);
    }
    
    AvroDecoder(Class<T> clazz, Schema schema, boolean json){
        if(clazz == null && schema == null){
            throw new NullPointerException();
        }else if(json && schema == null){
            throw new NullPointerException();
        }  
        if(clazz != null 
                && SpecificRecordBase.class.isAssignableFrom(clazz)){
            myReader = new SpecificDatumReader<T>(clazz);
        }else{
            myReader = new GenericDatumReader<T>(schema);
        }
        myDecoderFactory = DecoderFactory.get();
        myJsonFlag = json;
        mySchema = schema;
    }
        
    @Override
    public T adapt(S a) {
        if(a == null){
            return null;
        }
        Decoder decoder;
        try{
            if(myJsonFlag){
                decoder = myDecoderFactory.jsonDecoder(mySchema, a);
            }else{
                decoder = myDecoderFactory.binaryDecoder(a, null);
            }
            return myReader.read(null, decoder);
        }catch(IOException ex){
            theLogger.log(Level.WARNING, 
                    "There was an error decoding the stream.", ex);
            return null;
        }
    }    
}

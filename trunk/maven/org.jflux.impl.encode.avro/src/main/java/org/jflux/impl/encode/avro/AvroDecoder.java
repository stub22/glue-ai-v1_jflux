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
import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.jflux.api.core.Adapter;

/**
 * Decodes Avro messages into record objects.
 * @param <S> the type of message stream to decode
 * @param <T> the type of record to decode to
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
    
    /**
     * Builds a decoder to convert from a ByteArrayInputStream to a SpecificRecordBase.
     * @param <R> the type of SpecificRecordBase
     * @param outputClass the type of SpecificRecordBase
     * @return AvroDecoder to convert ByteArrayInputStream data to an object
     */
    public static <R extends SpecificRecordBase> 
            AvroDecoder<ByteArrayInputStream,R> 
            buildByteStreamDecoder(Class<R> outputClass){
        return new AvroDecoder<ByteArrayInputStream,R>(
                outputClass, null, false);
    }
    
    /**
     * Builds a decoder to convert to a SpecificRecordBase
     * @param <S> the type of InputStream
     * @param <R> the type of SpecificRecordBase
     * @param outputClass the type of SpecificRecordBase
     * @return AvroDecoder to convert raw data to an object
     */
    public static <S extends InputStream, R extends SpecificRecordBase> 
            AvroDecoder<S,R> buildSpecificBinaryDecoder(Class<R> outputClass){
        return new AvroDecoder<S,R>(outputClass, null, false);
    }
    
    /**
     * Builds a decoder to convert according to a Schema
     * @param <S> the type of InputStream
     * @param <R> the type of IndexedRecord
     * @param outputClass the type of IndexedRecord
     * @param schema the Schema to use to convert
     * @return AvroDecoder to convert raw data to an object via schema
     */
    public static <S extends InputStream, R extends IndexedRecord> 
            AvroDecoder<S,R> buildBinaryDecoder(
                    Class<R> outputClass, Schema schema){
        return new AvroDecoder<S,R>(outputClass, schema, false);
    }
    
    /**
     * Builds a decoder to convert from a JSON file
     * @param <S> the type of InputStream (JSON-based)
     * @param <R> the type of IndexedRecord
     * @param inputClass the type of InputStream (JSON-based)
     * @param outputClass the type of IndexedRecord
     * @param schema the Schema to use to convert
     * @return AvroDecoder to convert a JSON file to an object
     */
    public static <S extends InputStream, R extends IndexedRecord> 
            AvroDecoder<S,R> buildJsonDecoder(
                    Class<S> inputClass, Class<R> outputClass, Schema schema){
        return new AvroDecoder<S,R>(outputClass, schema, true);
    }
    
    AvroDecoder(Class<T> clazz, Schema schema, boolean json){
        this(clazz, schema, json, null);
    }
    
    AvroDecoder(Class<T> clazz, Schema schema, boolean json, ClassLoader classLoader){
        if(clazz == null && schema == null){
            throw new NullPointerException();
        }else if(json && schema == null){
            throw new NullPointerException();
        }  
        if((clazz != null && SpecificRecordBase.class.isAssignableFrom(clazz))
                || (classLoader != null && schema != null)){
            myReader = buildSpecificReader(clazz, schema, classLoader);
        }
        if(myReader == null && schema != null){
            myReader = new GenericDatumReader<T>(schema);
        }
        if(myReader == null){
            throw new NullPointerException();
        }
        myDecoderFactory = DecoderFactory.get();
        myJsonFlag = json;
        mySchema = schema;
    }
    
    private SpecificDatumReader<T> buildSpecificReader(
            Class<T> clazz, Schema schema, ClassLoader classLoader){
        if(classLoader == null){
            classLoader = clazz.getClassLoader();
        }
        SpecificData data = new SpecificData(classLoader);
        if(schema == null){
            schema = data.getSchema(clazz);
            if(schema == null){
                return null;
            }
        }
        return new SpecificDatumReader<T>(schema, schema, data);
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

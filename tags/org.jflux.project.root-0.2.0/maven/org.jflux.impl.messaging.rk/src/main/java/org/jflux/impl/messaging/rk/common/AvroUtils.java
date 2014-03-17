/*
 * Copyright 2011 Hanson Robokind LLC.
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

package org.jflux.impl.messaging.rk.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public class AvroUtils {
    private final static Logger theLogger = 
            LoggerFactory.getLogger(AvroUtils.class);
    
    public static <T extends IndexedRecord> T readFromStream(
            Class<T> c, T reuse, Schema schema, InputStream in, boolean json)
            throws IOException{
        return readFromStream(c, reuse, schema, in, json, null);
    }
    public static <T extends IndexedRecord> T readFromStream(
            Class<T> c, T reuse, Schema schema, InputStream in, boolean json, ClassLoader classLoader)
            throws IOException{
        DecoderFactory dFact = DecoderFactory.get();
        DatumReader<T> r = null;
        if((c != null && SpecificRecordBase.class.isAssignableFrom(c))
                || (classLoader != null && schema != null)){
            r = buildSpecificReader(c, schema, classLoader);
        }
        if(r == null && schema != null){
            r = new GenericDatumReader<T>(schema);
        }
        Decoder wrapped = (json ? 
                dFact.jsonDecoder(schema, in) :
                dFact.binaryDecoder(in, null));
        //Decoder d = dFact.validatingDecoder(schema, wrapped);
        return r.read(reuse, wrapped);
    }
    
    private static <T> SpecificDatumReader<T> buildSpecificReader(
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
    
    public static <T extends IndexedRecord> boolean writeToStream(
            T t, Schema schema, OutputStream out, boolean json)
            throws NullPointerException, IOException{
        if(t == null){
            theLogger.error("Unable to write null record to Stream.");
            throw new NullPointerException(
                    "Unable to write null record to Stream");
        }
        DatumWriter<T> w;
        if(t instanceof SpecificRecordBase){
            w = new SpecificDatumWriter<T>(schema);
        }else{
            w = new GenericDatumWriter<T>(schema);
        }
        EncoderFactory eFact = EncoderFactory.get();
        Encoder e = (json ? 
                eFact.jsonEncoder(schema, out) : 
                eFact.binaryEncoder(out, null));
        w.write(t, e);
        e.flush();
        return true;
    }
    
    public static <T extends IndexedRecord> T readFromFile(
            Class<T> c, T reuse, Schema schema, File file, boolean json)
            throws IOException, FileNotFoundException{
        InputStream in = new FileInputStream(file);
        return readFromStream(c, reuse, schema, in, json);
    }
    
    public static <T extends IndexedRecord> boolean writeToFile(
            T t, Schema schema, File file, boolean json)
            throws IOException, FileNotFoundException{
        OutputStream out = new FileOutputStream(file);
        return writeToStream(t, schema, out, json);
    }
}

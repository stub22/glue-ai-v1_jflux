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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;

/**
 * QpidUtils contains static methods for using Avro SpecificRecords with 
 * Qpid BytesMessages.
 * 
 * @author Matthew Stevenson <www.robokind.org>
 */
public class QpidUtils {
    /**
     * Writes an Avro SpecificRecordBase to
     * @param <T>
     * @param c
     * @param t
     * @param message
     * @throws JMSException 
     */
    public static <T extends IndexedRecord> void packAvroMessage(
            T t, BytesMessage message) 
            throws IOException, JMSException{
        if(message == null){
            throw new NullPointerException();
        }
        byte[] bytes = packAvroBytes(t);
        message.writeBytes(bytes);
    }
    
    public static <T extends IndexedRecord> byte[] packAvroBytes(
            T t) throws IOException{
        if(t == null){
            throw new NullPointerException();
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        AvroUtils.writeToStream(t, t.getSchema(), out, false);
        return out.toByteArray();
    }
    
    public static <T extends IndexedRecord> T unpackAvroMessage(
            Class<T> c, T reuse, Schema schema, BytesMessage message) 
            throws JMSException, Exception{
        long len = message.getBodyLength();
        byte[] data = new byte[(int)len];
        int read = message.readBytes(data);
        if(read != len){
            String error = String.format(
                    "Could not read entire Avro message.  "
                    + "Expected %d bytes, found %d bytes.", len, read);
            throw new Exception(error);
        }
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        return AvroUtils.readFromStream(c, reuse, schema, in, false);
    }
    
    public static <T extends IndexedRecord> T unpackAvroMessage(
            Class<T> c, Schema schema, BytesMessage message) 
            throws JMSException, Exception{
        long len = message.getBodyLength();
        byte[] data = new byte[(int)len];
        int read = message.readBytes(data);
        if(read != len){
            String error = String.format(
                    "Could not read entire Avro message.  "
                    + "Expected %d bytes, found %d bytes.", len, read);
            throw new Exception(error);
        }
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        return AvroUtils.readFromStream(c, null, schema, in, false);
    }
}

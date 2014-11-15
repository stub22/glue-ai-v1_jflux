/*
 * Copyright 2012 Hanson Robokind LLC.
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
package org.jflux.impl.messaging.rk.utils;

import org.jflux.impl.messaging.rk.common.ComplexAdapter;
import org.jflux.impl.messaging.rk.common.QpidUtils;
import org.jflux.impl.messaging.rk.common.PolymorphicAdapter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.BytesMessage;
import javax.jms.Session;
import org.apache.avro.generic.IndexedRecord;
import org.apache.qpid.client.message.JMSBytesMessage;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.Source;
import org.jflux.impl.messaging.rk.BytesMessageFactory;
import org.jflux.impl.messaging.rk.common.PolymorphicAdapter.AdapterKeyMap;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public class JMSAvroPolymorphicRecordBytesAdapter<Msg> implements Adapter<Msg, BytesMessage> {
    static final Logger theLogger = 
            Logger.getLogger(JMSAvroPolymorphicRecordBytesAdapter.class.getName());
    private PolymorphicAdapter<Msg, BytesMessage> myAdapter;
    private BytesMessageFactory myBytesMessageFactory;
    
    public JMSAvroPolymorphicRecordBytesAdapter(AdapterKeyMap<Msg> keyMap){
        if(keyMap == null){
            throw new NullPointerException();
        }
        myAdapter = new PolymorphicAdapter<Msg, BytesMessage>(keyMap);
        myBytesMessageFactory = new BytesMessageFactory();
    }
    
    public void setSession(Session session){
        myBytesMessageFactory.setSession(session);
    }
    
    public <R extends IndexedRecord> void addAdapter(
            Adapter<Msg,R> adapter, String contentType){
        if(adapter == null || contentType == null){
            throw new NullPointerException();
        }
        JMSAvroRecordBytesAdapter<R> bytesAdapter = 
                new JMSAvroRecordBytesAdapter<R>(
                        myBytesMessageFactory, contentType);
        ComplexAdapter<Msg,R,BytesMessage> complexAdapter = 
                new ComplexAdapter<Msg, R, BytesMessage>(adapter, bytesAdapter);
        myAdapter.addAdapter(contentType, complexAdapter);
    }

    @Override
    public BytesMessage adapt(Msg a) {
        return myAdapter.adapt(a);
    }    
    
    public static class JMSAvroRecordBytesAdapter<A extends IndexedRecord> implements 
            Adapter<A, BytesMessage> {

        private String myContentType;
        private Source<JMSBytesMessage> myBytesMessageFactory;

        public JMSAvroRecordBytesAdapter(Source<JMSBytesMessage> messageFactory,
                String contentType){
            if(messageFactory == null){
                throw new NullPointerException();
            }
            myBytesMessageFactory = messageFactory;
            myContentType = contentType;
        }

        @Override
        public BytesMessage adapt(A a) {
            JMSBytesMessage message = myBytesMessageFactory.getValue();
            if(message == null){
                theLogger.warning("The factory failed to create a BytesMessage.");
                return null;
            }
            try{
                QpidUtils.packAvroMessage(a, message);
                if(myContentType != null){
                    message.setContentType(myContentType);
                    message.setEncoding(myContentType);
                }
                return message;
            }catch(Exception ex){
                theLogger.log(Level.WARNING, "There was an error packing the "
                        + "JMSBytesMessage.", ex);
            }
            return null;
        }
    }
}

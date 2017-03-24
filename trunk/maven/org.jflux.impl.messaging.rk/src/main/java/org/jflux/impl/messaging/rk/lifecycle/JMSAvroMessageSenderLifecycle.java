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
package org.jflux.impl.messaging.rk.lifecycle;

import java.util.Map;
import java.util.Properties;
import javax.jms.Destination;
import javax.jms.Session;
import org.apache.avro.generic.IndexedRecord;
import org.jflux.api.core.Adapter;
import org.jflux.api.messaging.rk.Constants;
import org.jflux.api.messaging.rk.MessageSender;
import org.jflux.impl.services.rk.lifecycle.AbstractLifecycleProvider;
import org.jflux.impl.services.rk.lifecycle.utils.DescriptorListBuilder;
import org.jflux.impl.messaging.rk.JMSAvroMessageSender;
import org.jflux.impl.messaging.rk.utils.ConnectionManager;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 *
 * @deprecated This class uses our old lifecycles. Use
 * {@link org.jflux.spec.messaging.MessageSenderLifecycle}.
 */
@Deprecated
public class JMSAvroMessageSenderLifecycle<Msg, Rec extends IndexedRecord>
        extends AbstractLifecycleProvider<
                MessageSender, JMSAvroMessageSender<Msg,Rec>> {
    private Adapter<Msg,Rec> myAdapter;
    private String myDefaultContentType;

    protected final static String theSession = "session";
    protected final static String theDestination = "destination";

    @Deprecated
    public JMSAvroMessageSenderLifecycle(Adapter<Msg,Rec> adapter,
            Class<Msg> messageClass, Class<Rec> recordClass,
            String senderId, String sessionId, String destinationId,
            String contentType){
        super(new DescriptorListBuilder()
                .dependency(theSession, Session.class)
                    .with(ConnectionManager.PROP_CONNECTION_ID, sessionId)
                .dependency(theDestination, Destination.class)
                    .with(ConnectionManager.PROP_DESTINATION_ID, destinationId)
                .getDescriptors());
        if(adapter == null){
            throw new NullPointerException();
        }
        myAdapter = adapter;
        myDefaultContentType = contentType;
        if(myRegistrationProperties == null){
            myRegistrationProperties = new Properties();
        }
        myRegistrationProperties.put(
                Constants.PROP_MESSAGE_TYPE, messageClass.getName());
        myRegistrationProperties.put(
                Constants.PROP_RECORD_TYPE, recordClass.getName());
        myRegistrationProperties.put(Constants.PROP_MESSAGE_SENDER_ID,
                senderId);
    }

    @Deprecated
    public JMSAvroMessageSenderLifecycle(Adapter<Msg,Rec> adapter,
            Class<Msg> messageClass, Class<Rec> recordClass,
            String senderId, String sessionId, String destinationId){
        this(adapter, messageClass, recordClass,
                senderId, sessionId, destinationId, null);
    }

    @Override
    @Deprecated
    protected JMSAvroMessageSender<Msg,Rec> create(
            Map<String, Object> dependencies) {

        Session session = (Session)dependencies.get(theSession);
        Destination dest = (Destination)dependencies.get(theDestination);
        JMSAvroMessageSender<Msg,Rec> sender =
                new JMSAvroMessageSender<Msg, Rec>(session, dest);
        if(myDefaultContentType != null){
            sender.setDefaultContentType(myDefaultContentType);
        }
        sender.setAdapter(myAdapter);
        sender.start();
        return sender;
    }

    @Override
    @Deprecated
    public synchronized void stop() {
        if(myService != null){
            myService.stop();
        }
    }

    @Override
    @Deprecated
    protected void handleChange(String dependencyId, Object service,
            Map<String, Object> dependencies) {
        if(myService == null){
            return;
        }
        myService.stop();
        if(isSatisfied()){
            myService = create(dependencies);
        }else{
            myService = null;
        }
    }

    @Override
    @Deprecated
    public Class<MessageSender> getServiceClass() {
        return MessageSender.class;
    }
}

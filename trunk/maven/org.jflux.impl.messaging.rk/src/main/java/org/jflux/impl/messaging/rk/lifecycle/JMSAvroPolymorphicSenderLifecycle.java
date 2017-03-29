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
import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.Session;
import org.jflux.api.messaging.rk.Constants;
import org.jflux.api.messaging.rk.DefaultMessageSender;
import org.jflux.api.messaging.rk.MessageSender;
import org.jflux.impl.services.rk.lifecycle.AbstractLifecycleProvider;
import org.jflux.impl.services.rk.lifecycle.utils.DescriptorListBuilder;
import org.jflux.impl.messaging.rk.JMSBytesMessageSender;
import org.jflux.impl.messaging.rk.utils.ConnectionManager;
import org.jflux.impl.messaging.rk.utils.JMSAvroPolymorphicRecordBytesAdapter;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
@Deprecated
public class JMSAvroPolymorphicSenderLifecycle<Msg>
        extends AbstractLifecycleProvider<
                MessageSender, MessageSender<Msg>> {
    private JMSAvroPolymorphicRecordBytesAdapter<Msg> myAdapter;

    protected final static String theSession = "session";
    protected final static String theDestination = "destination";

    public JMSAvroPolymorphicSenderLifecycle(
            JMSAvroPolymorphicRecordBytesAdapter<Msg> adapter,
            Class<Msg> messageClass,
            String senderId, String sessionId, String destinationId){
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
        if(myRegistrationProperties == null){
            myRegistrationProperties = new Properties();
        }
        myRegistrationProperties.put(
                Constants.PROP_MESSAGE_TYPE, messageClass.getName());
        myRegistrationProperties.put(
                Constants.PROP_RECORD_TYPE, BytesMessage.class.getName());
        myRegistrationProperties.put(Constants.PROP_MESSAGE_SENDER_ID,
                senderId);
    }

    @Override
    protected MessageSender<Msg> create(
            Map<String, Object> dependencies) {
        Session session = (Session)dependencies.get(theSession);
        Destination dest = (Destination)dependencies.get(theDestination);
        myAdapter.setSession(session);
        JMSBytesMessageSender recSender = new JMSBytesMessageSender();
        recSender.setSession(session);
        recSender.setDestination(dest);
        DefaultMessageSender<Msg,BytesMessage> sender =
                new DefaultMessageSender<Msg, BytesMessage>();
        sender.setAdapter(myAdapter);
        sender.setRecordSender(recSender);
        recSender.openProducer();
        try{
            sender.start();
        }catch(Exception ex){
            return null;
        }
        return sender;
    }

    @Override
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
    public Class<MessageSender> getServiceClass() {
        return MessageSender.class;
    }
}

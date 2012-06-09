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
package org.jflux.impl.transport.jms;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.jflux.api.core.Listener;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class JMSMessageSender implements Listener<BytesMessage> {
    private MessageProducer myProducer;

    public JMSMessageSender(Session session, Destination dest) 
            throws JMSException{
        if(session == null || dest == null){
            throw new NullPointerException();
        }
        myProducer = session.createProducer(dest);
    }
    
    @Override
    public void handleEvent(BytesMessage event) {
        if(myProducer == null){
            return;
        }
        try{
            myProducer.send(event);
        }catch(JMSException ex){}
    }
    
}

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
package org.jflux.impl.messaging.rk;

import javax.jms.JMSException;
import javax.jms.Session;
import org.apache.qpid.client.message.JMSBytesMessage;
import org.jflux.api.core.Source;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public class BytesMessageFactory implements Source<JMSBytesMessage> {
    private Session mySession;
    
    public void setSession(Session session){
        mySession = session;
    }
    
    @Override
    public JMSBytesMessage getValue() {
        if(mySession == null){
            return null;
        }
        try{
            return (JMSBytesMessage)mySession.createBytesMessage();
        }catch(JMSException ex){
            return null;
        }
    }
    
}

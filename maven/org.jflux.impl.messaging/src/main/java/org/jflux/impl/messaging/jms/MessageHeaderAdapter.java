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
package org.jflux.impl.messaging.jms;

import javax.jms.BytesMessage;
import org.apache.qpid.client.message.JMSBytesMessage;
import org.jflux.api.core.util.Adapter;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public class MessageHeaderAdapter implements Adapter<BytesMessage,BytesMessage>{
    private String myContentType;
    
    public MessageHeaderAdapter(String contentType){
        if(contentType == null){
            throw new NullPointerException();
        }
        myContentType = contentType;
    }
    
    @Override
    public BytesMessage adapt(BytesMessage a) {
        if(!(a instanceof JMSBytesMessage)){
            return a;
        }
        ((JMSBytesMessage) a).setContentType(myContentType);
        ((JMSBytesMessage)a).setEncoding(myContentType);
        return a;
    }
    
}

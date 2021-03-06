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
import org.apache.qpid.client.message.JMSBytesMessage;
import org.jflux.api.core.Adapter;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class MessageHeaderAdapter implements Adapter<BytesMessage,BytesMessage>{
    private String myContentType;
    
    /**
     * Adapter to format a message with proper content type and encoding.
     * @param contentType the content type to format
     */
    public MessageHeaderAdapter(String contentType){
        if(contentType == null){
            throw new NullPointerException();
        }
        myContentType = contentType;
    }
    
    /**
     * Change the content type.
     * @param contentType the new content type
     */
    public synchronized void setContentType(String contentType){
        myContentType = contentType;
    }
    
    /**
     * Format a message.
     * @param a the message to format
     * @return message formatted with content type and encoding
     */
    @Override
    public synchronized BytesMessage adapt(BytesMessage a) {
        if(!(a instanceof JMSBytesMessage)){
            return a;
        }
        ((JMSBytesMessage) a).setContentType(myContentType);
        ((JMSBytesMessage)a).setEncoding(myContentType);
        return a;
    }
    
}

/*
 *  Copyright 2012 by The JFlux Project (www.jflux.org).
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

import java.io.ByteArrayInputStream;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import org.jflux.api.core.util.Adapter;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class MessageUnpacker implements 
        Adapter<BytesMessage,ByteArrayInputStream> {

    @Override
    public ByteArrayInputStream adapt(BytesMessage a) {
        try{
            long len = a.getBodyLength();
            byte[] data = new byte[(int)len];
            a.readBytes(data);
            return new ByteArrayInputStream(data);
            
        }catch(JMSException ex){
            return null;
        }
    }
}

/*
 * Copyright 2012 The JFlux Project (www.jflux.org).
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
package org.jflux.api.core.event;

import java.util.Map;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.Source;

/**
 *
 * @param <S> 
 * @param <T> 
 * @author Matthew Stevenson
 */
public interface MutableHeader<S, T> extends Header<S, T> {
    /**
     *
     * @param sourceRef
     */
    public void setSourceReference(S sourceRef);
    /**
     *
     * @param timestamp
     */
    public void setTimestamp(T timestamp);
    /**
     *
     * @param eventType
     */
    public void setEventType(String eventType);
    /**
     *
     * @param headerProperties
     */
    public void setHeaderProperties(Map<String,String> headerProperties);
    
    /**
     * Creates a new MutableHeader from a given Header
     * @param <S> Header SourceReference type
     * @param <T> Header Timestamp type
     * @param <H> Output MutableHeader type
     */
    public static class HeaderChanger<S, T, H extends MutableHeader<S, T>> 
        implements Adapter<Header<S, T>, H> {
        private Source<H> myHeaderFactory;
        private Class<H> myOutputClass;
        /**
         * Creates a new HeaderChanger.
         * @param outputClass if not null, used to check if a given header is 
         * an instance of the output type to cast it rather than create a new 
         * header
         * @param headerFactory produces empty mutable headers
         */
        public HeaderChanger(Class<H> outputClass, Source<H> headerFactory){
            if(headerFactory == null){
                throw new NullPointerException();
            }
            myOutputClass = outputClass;
            myHeaderFactory = headerFactory;
        }
        
        /**
         *
         * @param a
         * @return
         */
        @Override
        public H adapt(Header<S, T> a) {
            if(myOutputClass != null && 
                    myOutputClass.isAssignableFrom(a.getClass())){
                return (H)a;
            }
            H h = myHeaderFactory.getValue();
            h.setSourceReference(a.getSourceReference());
            h.setTimestamp(a.getTimestamp());
            h.setEventType(a.getEventType());
            h.setHeaderProperties(a.getHeaderProperties());
            return h;
        }
    }
}

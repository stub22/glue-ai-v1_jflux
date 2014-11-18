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
import org.jflux.api.core.Source;

/**
 *
 * @author Matthew Stevenson
 */

/**
 * Mutable implementation of a Header
 * @author Matthew Stevenson
 * @param <SourceRef> source reference type
 * @param <Time> timestamp type
 */
public class BasicMutableHeader<SourceRef,Time> extends 
        BasicHeader<SourceRef, Time> implements MutableHeader<SourceRef, Time> {

    /**
     * Builds a BasicMutableHeader from components
     * @param sourceRef the source reference
     * @param timestamp the timestamp
     * @param eventType the event type
     * @param props Map of properties
     */
    public BasicMutableHeader(SourceRef sourceRef, Time timestamp, 
            String eventType, Map<String,String> props){
        super(sourceRef, timestamp, eventType, props);
    }

    /**
     * Set the source reference
     * @param sourceRef the source reference
     */
    @Override
    public void setSourceReference(SourceRef sourceRef) {
        mySourceRef = sourceRef;
    }

    /**
     * Set the timestamp
     * @param timestamp the timestamp
     */
    @Override
    public void setTimestamp(Time timestamp) {
        myTimestamp = timestamp;
    }

    /**
     * Set the event type
     * @param eventType the event type
     */
    @Override
    public void setEventType(String eventType) {
        myEventType = eventType;
    }

    /**
     * Set the properties
     * @param headerProperties the properties
     */
    @Override
    public void setHeaderProperties(Map<String, String> headerProperties) {
        myProperties = headerProperties;
    }
    
    /**
     * Source to generate a BasicMutableHeader on demand
     * @param <SourceRef> source reference type
     * @param <Time> timestamp type
     */
    public static class MutableHeaderSource<SourceRef,Time> implements 
            Source<MutableHeader<SourceRef,Time>> {
        private SourceRef mySourceRef;
        private Source<Time> myTimestampSource;
        private String myEventType;
        private Map<String,String> myProperties;
        
        /**
         * Builds a MutableHeaderSource from components
         * @param sourceRef the source reference
         * @param timestampSource timestamp generator
         * @param eventType the event type
         * @param props Map of properties
         */
        public MutableHeaderSource(
                SourceRef sourceRef, Source<Time> timestampSource, 
                String eventType, Map<String,String> props){
            if(timestampSource == null){
                throw new NullPointerException();
            }
            myTimestampSource = timestampSource;
            mySourceRef = sourceRef;
            myEventType = eventType;
            myProperties = props;
        }
        
        /**
         * Builds a BasicMutableHeader
         * @return the header
         */
        @Override
        public MutableHeader<SourceRef, Time> getValue() {
            return new BasicMutableHeader<SourceRef, Time>(
                    mySourceRef, myTimestampSource.getValue(), 
                    myEventType, myProperties);
        }
    }
}

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


public class BasicMutableHeader<SourceRef,Time> extends 
        BasicHeader<SourceRef, Time> implements MutableHeader<SourceRef, Time> {

    public BasicMutableHeader(SourceRef sourceRef, Time timestamp, 
            String eventType, Map<String,String> props){
        super(sourceRef, timestamp, eventType, props);
    }
    @Override
    public void setSourceReference(SourceRef sourceRef) {
        mySourceRef = sourceRef;
    }

    @Override
    public void setTimestamp(Time timestamp) {
        myTimestamp = timestamp;
    }

    @Override
    public void setEventType(String eventType) {
        myEventType = eventType;
    }

    @Override
    public void setHeaderProperties(Map<String, String> headerProperties) {
        myProperties = headerProperties;
    }
    
    public static class MutableHeaderSource<SourceRef,Time> implements 
            Source<MutableHeader<SourceRef,Time>> {
        private SourceRef mySourceRef;
        private Source<Time> myTimestampSource;
        private String myEventType;
        private Map<String,String> myProperties;
        
        public MutableHeaderSource(
                SourceRef sourceRef, Source<Time> timestampSource, 
                String eventType, Map<String,String> props){
            if(timestampSource == null){
                throw new NullPointerException();
            }
            mySourceRef = sourceRef;
            myEventType = eventType;
            myProperties = props;
        }
        
        @Override
        public MutableHeader<SourceRef, Time> getValue() {
            return new BasicMutableHeader<SourceRef, Time>(
                    mySourceRef, myTimestampSource.getValue(), 
                    myEventType, myProperties);
        }
    }
}

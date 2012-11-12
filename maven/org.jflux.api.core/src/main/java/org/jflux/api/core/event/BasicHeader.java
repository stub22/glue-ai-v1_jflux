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
package org.jflux.api.core.event;

import java.util.Map;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.Source;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class BasicHeader<SourceRef, Time> implements Header<SourceRef, Time> {
    protected SourceRef mySourceRef;
    protected Time myTimestamp;
    protected String myEventType;
    protected Map<String,String> myProperties;
    
    public BasicHeader(SourceRef sourceRef, Time timestamp, 
            String eventType, Map<String,String> props){
        mySourceRef = sourceRef;
        myTimestamp = timestamp;
        myEventType = eventType;
        myProperties = props;
    }
    
    @Override
    public SourceRef getSourceReference(){
        return mySourceRef;
    }
    
    @Override
    public Time getTimestamp(){
        return myTimestamp;
    }

    @Override
    public String getEventType() {
        return myEventType;
    }
    
    @Override
    public Map<String,String> getHeaderProperties(){
        return myProperties;
    }
    
    public static class HeaderFactory<Data,SourceRef,Time> implements 
            Adapter<Data,Header<SourceRef,Time>> {
        private Adapter<Data,SourceRef> mySourceRefAdapter;
        private Source<Time> myTimestampSource;
        private Adapter<Data,String> myEventTypeAdapter;
        private Adapter<Data,Map<String,String>> myPropertiesAdapter;
        
        public HeaderFactory(
                Adapter<Data,SourceRef> sourceRefAdapter, 
                Source<Time> timestampSource, 
                Adapter<Data,String> eventTypeAdapter, 
                Adapter<Data,Map<String,String>> propsAdapter){
            if(sourceRefAdapter == null || timestampSource == null 
                    || eventTypeAdapter == null || propsAdapter == null){
                throw new NullPointerException();
            }
            mySourceRefAdapter = sourceRefAdapter;
            myTimestampSource = timestampSource;
            myEventTypeAdapter = eventTypeAdapter;
            myPropertiesAdapter = propsAdapter;
        }

        @Override
        public Header<SourceRef, Time> adapt(Data a) {
            SourceRef ref = mySourceRefAdapter.adapt(a);
            Time time = myTimestampSource.getValue();
            String type = myEventTypeAdapter.adapt(a);
            Map<String,String> props = myPropertiesAdapter.adapt(a);
            return new BasicHeader<SourceRef, Time>(ref, time, type, props);
        }
    }
    
    public static class HeaderSource<SourceRef,Time> implements 
            Source<Header<SourceRef,Time>> {
        private SourceRef mySourceRef;
        private Source<Time> myTimestampSource;
        private String myEventType;
        private Map<String,String> myProperties;
        
        public HeaderSource(
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
        public Header<SourceRef, Time> getValue() {
            return new BasicHeader<SourceRef, Time>(
                    mySourceRef, myTimestampSource.getValue(), 
                    myEventType, myProperties);
        }
    }
}

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

import java.util.Properties;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.Source;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class BasicHeader<SourceRef, Time> implements Header<SourceRef, Time> {
    private SourceRef mySourceRef;
    private Time myTimestamp;
    private String myEventType;
    private String myEventName;
    private Properties myProperties;
    
    public BasicHeader(SourceRef sourceRef, Time timestamp, 
            String eventType, String eventName, Properties props){
        mySourceRef = sourceRef;
        myTimestamp = timestamp;
        myEventType = eventType;
        myEventName =  eventName;
        myProperties = props;
    }
    
    @Override
    public SourceRef getSourceReference(){
        return mySourceRef;
    }
    
    @Override
    public Time getTimeStamp(){
        return myTimestamp;
    }

    @Override
    public String getEventType() {
        return myEventType;
    }

    @Override
    public String getEventName() {
        return myEventName;
    }
    
    @Override
    public Properties getHeaderProperties(){
        return myProperties;
    }
    
    public static class HeaderFactory<Data,SourceRef,Time> implements 
            Adapter<Data,Header<SourceRef,Time>> {
        private Adapter<Data,SourceRef> mySourceRefAdapter;
        private Source<Time> myTimestampSource;
        private Adapter<Data,String> myEventTypeAdapter;
        private Adapter<Data,String> myEventNameAdapter;
        private Adapter<Data,Properties> myPropertiesAdapter;
        
        public HeaderFactory(
                Adapter<Data,SourceRef> sourceRefAdapter, 
                Source<Time> timestampSource, 
                Adapter<Data,String> eventTypeAdapter, 
                Adapter<Data,String> eventNameAdapter, 
                Adapter<Data,Properties> propsAdapter){
            if(sourceRefAdapter == null 
                    || timestampSource == null || eventTypeAdapter == null 
                    || eventNameAdapter == null || propsAdapter == null){
                throw new NullPointerException();
            }
            mySourceRefAdapter = sourceRefAdapter;
            myTimestampSource = timestampSource;
            myEventTypeAdapter = eventTypeAdapter;
            myEventNameAdapter = eventNameAdapter;
            myPropertiesAdapter = propsAdapter;
        }

        @Override
        public Header<SourceRef, Time> adapt(Data a) {
            SourceRef ref = mySourceRefAdapter.adapt(a);
            Time time = myTimestampSource.getValue();
            String type = myEventTypeAdapter.adapt(a);
            String name = myEventNameAdapter.adapt(a);
            Properties props = myPropertiesAdapter.adapt(a);
            return new BasicHeader<SourceRef, Time>(ref, time, type, name, props);
        }
    }
    
    public static class HeaderSource<SourceRef,Time> implements 
            Source<Header<SourceRef,Time>> {
        private SourceRef mySourceRef;
        private Source<Time> myTimestampSource;
        private String myEventType;
        private String myEventName;
        private Properties myProperties;
        
        public HeaderSource(
                SourceRef sourceRef, Source<Time> timestampSource, 
                String eventType, String eventName, Properties props){
            if(timestampSource == null){
                throw new NullPointerException();
            }
            mySourceRef = sourceRef;
            myEventType = eventType;
            myEventName = eventName;
            myProperties = props;
        }
        
        @Override
        public Header<SourceRef, Time> getValue() {
            return new BasicHeader<SourceRef, Time>(
                    mySourceRef, myTimestampSource.getValue(), 
                    myEventType, myEventName, myProperties);
        }
    }
}

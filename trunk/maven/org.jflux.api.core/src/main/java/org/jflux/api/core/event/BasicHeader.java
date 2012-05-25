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
    private Properties myProperties;
    
    public BasicHeader(
            SourceRef sourceRef, Time timestamp, 
            String eventType, Properties props){
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
    public Time getTimeStamp(){
        return myTimestamp;
    }

    @Override
    public String getEventType() {
        return myEventType;
    }
    
    @Override
    public Properties getHeaderProperties(){
        return myProperties;
    }
    
    public static class BasicHeaderFactory<Data,SourceRef,Time> implements 
            Adapter<Data,BasicHeader<SourceRef,Time>> {
        private Adapter<Data,SourceRef> mySourceRefAdapter;
        private Source<Time> myTimestampSource;
        private Adapter<Data,String> myEventTypeAdapter;
        private Adapter<Data,Properties> myPropertiesAdapter;
        
        public BasicHeaderFactory(
                Adapter<Data,SourceRef> sourceRefAdapter, 
                Source<Time> timestampSource, 
                Adapter<Data,String> eventTypeAdapter, 
                Adapter<Data,Properties> propsAdapter){
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
        public BasicHeader<SourceRef, Time> adapt(Data a) {
            SourceRef ref = mySourceRefAdapter.adapt(a);
            Time time = myTimestampSource.getValue();
            String type = myEventTypeAdapter.adapt(a);
            Properties props = myPropertiesAdapter.adapt(a);
            return new BasicHeader<SourceRef, Time>(ref, time, type, props);
        }
    }
    
    public static class BasicHeaderSource<SourceRef,Time> implements 
            Source<BasicHeader<SourceRef,Time>> {
        private SourceRef mySourceRef;
        private Source<Time> myTimestampSource;
        private String myEventType;
        private Properties myProperties;
        
        public BasicHeaderSource(
                SourceRef sourceRef, Source<Time> timestampSource, 
                String eventType, Properties props){
            if(timestampSource == null){
                throw new NullPointerException();
            }
            mySourceRef = sourceRef;
            myEventType = eventType;
            myProperties = props;
        }
        
        @Override
        public BasicHeader<SourceRef, Time> getValue() {
            return new BasicHeader<SourceRef, Time>(
                    mySourceRef, myTimestampSource.getValue(), 
                    myEventType, myProperties);
        }
    }
}

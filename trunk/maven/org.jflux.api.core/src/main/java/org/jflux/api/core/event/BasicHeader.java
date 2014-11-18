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
 * Simple implementation of a Header
 * @author Matthew Stevenson <www.jflux.org>
 * @param <SourceRef> type of source reference
 * @param <Time> type of timestamp
 */
public class BasicHeader<SourceRef, Time> implements Header<SourceRef, Time> {

    /**
     * The source reference
     */
    protected SourceRef mySourceRef;

    /**
     * The timestamp
     */
    protected Time myTimestamp;

    /**
     * The event type
     */
    protected String myEventType;

    /**
     * Key/value pairs of properties
     */
    protected Map<String,String> myProperties;
    
    /**
     * Construct a BasicHeader from a source reference, a timestamp, an event
     * type and a collection of properties
     * @param sourceRef the source reference
     * @param timestamp the timestamp
     * @param eventType the event type
     * @param props the properties
     */
    public BasicHeader(SourceRef sourceRef, Time timestamp, 
            String eventType, Map<String,String> props){
        mySourceRef = sourceRef;
        myTimestamp = timestamp;
        myEventType = eventType;
        myProperties = props;
    }
    
    /**
     * Gets the source reference
     * @return the source reference
     */
    @Override
    public SourceRef getSourceReference(){
        return mySourceRef;
    }
    
    /**
     * Gets the timestamp
     * @return the timestamp
     */
    @Override
    public Time getTimestamp(){
        return myTimestamp;
    }

    /**
     * Gets the event type
     * @return the event type
     */
    @Override
    public String getEventType() {
        return myEventType;
    }
    
    /**
     * Gets the properties
     * @return Map of properties
     */
    @Override
    public Map<String,String> getHeaderProperties(){
        return myProperties;
    }
    
    /**
     * Adapter to generate headers from raw data
     * @param <Data> type of raw data
     * @param <SourceRef> source reference type
     * @param <Time> timestamp type
     */
    public static class HeaderFactory<Data,SourceRef,Time> implements 
            Adapter<Data,Header<SourceRef,Time>> {
        private Adapter<Data,SourceRef> mySourceRefAdapter;
        private Source<Time> myTimestampSource;
        private Adapter<Data,String> myEventTypeAdapter;
        private Adapter<Data,Map<String,String>> myPropertiesAdapter;
        
        /**
         * Build a HeaderFactory from generators of source reference, timestamp,
         * event type, and properties
         * @param sourceRefAdapter Adapter to generate source reference
         * @param timestampSource Source to generate timestamp
         * @param eventTypeAdapter Adapter to generate event type
         * @param propsAdapter Adapter to generate properties
         */
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

        /**
         * Generate a BasicHeader from raw data
         * @param a the raw data
         * @return the BasicHeader
         */
        @Override
        public Header<SourceRef, Time> adapt(Data a) {
            SourceRef ref = mySourceRefAdapter.adapt(a);
            Time time = myTimestampSource.getValue();
            String type = myEventTypeAdapter.adapt(a);
            Map<String,String> props = myPropertiesAdapter.adapt(a);
            return new BasicHeader<SourceRef, Time>(ref, time, type, props);
        }
    }
    
    /**
     * Source to generate a header on demand
     * @param <SourceRef> source reference type
     * @param <Time> timestamp type
     */
    public static class HeaderSource<SourceRef,Time> implements 
            Source<Header<SourceRef,Time>> {
        private SourceRef mySourceRef;
        private Source<Time> myTimestampSource;
        private String myEventType;
        private Map<String,String> myProperties;
        
        /**
         * Builds a HeaderSource
         * @param sourceRef the source reference
         * @param timestampSource timestamp generator
         * @param eventType the event type
         * @param props Map of properties
         */
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
        
        /**
         * Builds a Header
         * @return a BasicHeader
         */
        @Override
        public Header<SourceRef, Time> getValue() {
            return new BasicHeader<SourceRef, Time>(
                    mySourceRef, myTimestampSource.getValue(), 
                    myEventType, myProperties);
        }
    }
}

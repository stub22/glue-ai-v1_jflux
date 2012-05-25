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
package org.jflux.api.registry.opt;

import org.jflux.api.core.Adapter;
import org.jflux.api.core.Source;
import org.jflux.api.core.event.BasicEvent.BasicEventFactory;
import org.jflux.api.core.event.BasicHeader.BasicHeaderSource;
import org.jflux.api.core.event.Event;
import org.jflux.api.core.event.Header;

/**
 * Returns basic event factories for creating registry events.
 * 
 * @author Matthew Stevenson
 */
public class ReferenceEventsUtil {
    public final static String REFERENCE_ADDED = "referenceAdded";
    public final static String REFERENCE_MODIFIED = "referenceModified";
    public final static String REFERENCE_REMOVED = "referenceRemoved";
        
    public static <SourceRef,Time,Ref> Adapter<
            Ref,Event<Header<SourceRef,Time>,Ref>> addedEventFactory(
                    SourceRef sourceRef, Source<Time> timestamp){
        return referenceEventFactory(sourceRef, timestamp, REFERENCE_ADDED);
    }
    public static <SourceRef,Time,Ref> Adapter<
            Ref,Event<Header<SourceRef,Time>,Ref>> modifiedEventFactory(
                    SourceRef sourceRef, Source<Time> timestamp){
        return referenceEventFactory(sourceRef, timestamp, REFERENCE_ADDED);
    }
    public static <SourceRef,Time,Ref> Adapter<
            Ref,Event<Header<SourceRef,Time>,Ref>> removedEventFactory(
                    SourceRef sourceRef, Source<Time> timestamp){
        return referenceEventFactory(sourceRef, timestamp, REFERENCE_REMOVED);
    }
    
    private static <SourceRef,Time,Ref> Adapter<
            Ref,Event<Header<SourceRef,Time>,Ref>> referenceEventFactory(
                    SourceRef sourceRef, Source<Time> timestamp, String type){                
        return new BasicEventFactory<Header<SourceRef,Time>, Ref>(
                new BasicHeaderSource(sourceRef, timestamp, type, null));
    }
}

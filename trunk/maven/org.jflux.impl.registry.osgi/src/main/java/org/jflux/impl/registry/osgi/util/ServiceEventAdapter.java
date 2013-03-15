/*
 * Copyright 2013 The JFlux Project (www.jflux.org).
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
package org.jflux.impl.registry.osgi.util;

import org.jflux.api.core.Adapter;
import org.jflux.api.core.Source;
import org.jflux.api.core.event.BasicEvent;
import org.jflux.api.core.event.Event;
import org.jflux.api.core.event.Header;
import org.jflux.api.core.event.MutableHeader;
import org.jflux.impl.registry.OSGiReference;
import org.jflux.impl.registry.OSGiRegistry;
import org.osgi.framework.ServiceEvent;

/**
 *
 * @author matt
 */
public class ServiceEventAdapter<Time> implements 
        Adapter<ServiceEvent, Event<Header<OSGiRegistry<Time>, Time>, OSGiReference>> {
    /**
     * Registered state
     */
    public final static String REGISTERED = "registered";
    /**
     * Modified state
     */
    public final static String MODIFIED = "modified";
    /**
     * Modified (end match) state
     */
    public final static String MODIFIED_ENDMATCH = "modified_endmatch";
    /**
     * Unregistering state
     */
    public final static String UNREGISTERING = "unregistering";
    
    /**
     * Wraps an OSGi ServiceEvent into a JFlux Event
     */
    private Source<MutableHeader<OSGiRegistry<Time>, Time>> myHeaderSource;

    /**
     * Creates a ServiceEventAdapter from a header source
     * @param headerSource the header source
     */
    public ServiceEventAdapter(Source<MutableHeader<OSGiRegistry<Time>, Time>> headerSource){
        if(headerSource == null){
            throw new NullPointerException();
        }
        myHeaderSource = headerSource;
    }

    /**
     * Converts an OSGi ServiceEvent into a JFlux Event
     * @param a the ServiceEvent
     * @return the Event
     */
    @Override
    public Event<Header<OSGiRegistry<Time>, Time>, OSGiReference> adapt(ServiceEvent a) {
        MutableHeader<OSGiRegistry<Time>, Time> h = myHeaderSource.getValue();
        h.setEventType(getTypeString(a.getType()));
        OSGiReference r = new OSGiReference(a.getServiceReference());
        return new BasicEvent<Header<OSGiRegistry<Time>, Time>, OSGiReference>(h, r);
    }

    private String getTypeString(int type){
        switch(type){
            case ServiceEvent.REGISTERED : return REGISTERED;
            case ServiceEvent.MODIFIED : return MODIFIED;
            case ServiceEvent.MODIFIED_ENDMATCH : return MODIFIED_ENDMATCH;
            case ServiceEvent.UNREGISTERING : return UNREGISTERING;
            default : throw new IllegalArgumentException("Invalid Service Event Type.");
        }
    }
}

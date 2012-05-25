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
package org.jflux.impl.registry.osgi.direct;

import org.jflux.api.core.Listener;
import org.jflux.api.core.Notifier;
import org.jflux.api.core.event.Event;
import org.jflux.api.core.event.Header;
import org.jflux.api.registry.Monitor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author Matthew Stevenson
 */
public class DirectMonitor<Time> implements Monitor<
        OSGiDirectRegistry<Time>, 
        Event<Header<OSGiDirectRegistry<Time>, Time>, ServiceReference>> {
    private BundleContext myContext;
    
    DirectMonitor(BundleContext context){
        if(context == null){
            throw new NullPointerException();
        }
        myContext = context;
    }

    @Override
    public Notifier<
            Event<Header<OSGiDirectRegistry<Time>, Time>, ServiceReference>> 
            getNotifier(OSGiDirectRegistry<Time> desc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Listener<OSGiDirectRegistry<Time>> releaseNotifier() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
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

import org.jflux.api.registry.Accessor;
import org.jflux.api.registry.Finder;
import org.jflux.api.registry.Monitor;
import org.jflux.api.registry.Registry;
import org.jflux.api.registry.Retriever;
import org.jflux.api.registry.opt.Descriptor;
import org.jflux.api.registry.opt.Modification;
import org.jflux.api.registry.opt.RegistrationRequest;
import org.jflux.impl.registry.osgi.direct.DirectMonitor.ServiceEventNotifier;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

/**
 * A bare OSGi registry with no JFlux wrappers.
 * @author Matthew Stevenson
 */
public class OSGiDirectRegistry<Time> implements Registry<
        BundleContext,
        Finder<Descriptor<String,String>, ServiceReference>,
        Accessor<RegistrationRequest<?, String, String>,
                ServiceRegistration,
                Modification<ServiceRegistration, String ,String>>, 
        Retriever<ServiceReference>, 
        Monitor<Descriptor<String,String>, ServiceEvent, ServiceEventNotifier>> {

    @Override
    public Finder<Descriptor<String, String>, ServiceReference> 
            getFinder(BundleContext context) {
        return new DirectFinder(context);
    }

    @Override
    public Accessor<
            RegistrationRequest<?, String, String>, 
            ServiceRegistration, 
            Modification<ServiceRegistration, String, String>> 
            getAccessor(BundleContext context) {
        return new DirectAccessor(context);
    }

    @Override
    public Retriever<ServiceReference> getRetriever(BundleContext context) {
        return new DirectRetriever(context);
    }

    @Override
    public Monitor<
            Descriptor<String,String>, ServiceEvent, ServiceEventNotifier> 
            getMonitor(BundleContext context) {
        return new DirectMonitor<Time>(context);
    }
}

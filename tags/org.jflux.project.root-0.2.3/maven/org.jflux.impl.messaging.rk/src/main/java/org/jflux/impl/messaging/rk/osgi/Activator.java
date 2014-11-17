/*
 * Copyright 2012 Hanson Robokind LLC.
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
package org.jflux.impl.messaging.rk.osgi;

import org.jflux.api.core.util.EmptyAdapter;
import org.jflux.api.messaging.rk.services.ServiceCommand;
import org.jflux.api.messaging.rk.services.ServiceCommandFactory;
import org.jflux.api.messaging.rk.services.ServiceError;
import org.jflux.impl.messaging.rk.JMSAvroServiceFacade;
import org.jflux.impl.messaging.rk.ServiceCommandRecord;
import org.jflux.impl.messaging.rk.ServiceErrorRecord;
import org.jflux.impl.messaging.rk.config.RKMessagingConfigUtils;
import org.jflux.impl.messaging.rk.services.PortableServiceCommand;
import org.jflux.impl.services.rk.lifecycle.utils.SimpleLifecycle;
import org.jflux.impl.services.rk.osgi.lifecycle.OSGiComponent;
import org.jflux.impl.services.rk.osgi.lifecycle.OSGiComponentFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 *
 * @author Matthew Stevenson
 */
public class Activator implements BundleActivator {
    
    @Override
    public void start(BundleContext context) throws Exception {
        RKMessagingConfigUtils.registerAvroSerializationConfig(
                ServiceCommand.class, 
                ServiceCommandRecord.class, 
                ServiceCommandRecord.SCHEMA$, 
                new EmptyAdapter(), 
                new EmptyAdapter(), 
                JMSAvroServiceFacade.COMMAND_MIME_TYPE, null, 
                new OSGiComponentFactory(context));
        
        RKMessagingConfigUtils.registerAvroSerializationConfig(
                ServiceError.class, 
                ServiceErrorRecord.class, 
                ServiceErrorRecord.SCHEMA$, 
                new EmptyAdapter(), 
                new EmptyAdapter(), 
                JMSAvroServiceFacade.AVRO_MIME_TYPE, null, 
                new OSGiComponentFactory(context));
        
        new OSGiComponent(context, 
                new SimpleLifecycle(new PortableServiceCommand.Factory(), 
                        ServiceCommandFactory.class)).start();
    }
    
    @Override
    public void stop(BundleContext context) throws Exception {
        //TODO add deactivation code here
    }
}

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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jflux.api.core.Listener;
import org.jflux.api.core.playable.PlayableNotifier.DefaultPlayableNotifier;
import org.jflux.api.registry.Monitor;
import org.jflux.api.registry.opt.Descriptor;
import org.jflux.impl.registry.osgi.direct.DirectMonitor.ServiceEventNotifier;
import org.jflux.impl.registry.osgi.util.OSGiRegistryUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

/**
 *
 * @author Matthew Stevenson
 */
public class DirectMonitor<Time> implements Monitor<
        Descriptor<String,String>, ServiceEvent, ServiceEventNotifier> {
    private final static Logger theLogger = Logger.getLogger(DirectMonitor.class.getName());
    private BundleContext myContext;
    
    DirectMonitor(BundleContext context){
        if(context == null){
            throw new NullPointerException();
        }
        myContext = context;
    }

    @Override
    public ServiceEventNotifier getNotifier(
            Descriptor<String,String> desc) {
        String filter = OSGiRegistryUtil.getFullFilter(desc);
        return new ServiceEventNotifier(myContext, filter);
    }

    @Override
    public Listener<ServiceEventNotifier> releaseNotifier() {
        return new Listener<ServiceEventNotifier>() {

            @Override
            public void handleEvent(ServiceEventNotifier event) {
                if(event == null){
                    return;
                }
                if(!event.stop()){
                    theLogger.warning("Unable to stop notifier");
                    return;
                }
                event.myContext = null;
            }
        };
    }
    
    public static class ServiceEventNotifier extends 
            DefaultPlayableNotifier<ServiceEvent> implements ServiceListener{
        private BundleContext myContext;
        private String myFilter;
        
        private ServiceEventNotifier(BundleContext context, String filter){
            if(context == null){
                throw new NullPointerException();
            }
            myContext = context;
            myFilter = filter;
        }
        
        @Override
        public void serviceChanged(ServiceEvent event) {
            notifyListeners(event);
        }
        
        @Override
        public boolean start(){
            if(myContext == null || !super.start()){
                return false;
            }
            return listen();
        }
        
        @Override
        public boolean pause(){
            if(myContext == null || 
                    PlayState.RUNNING != getPlayState() || !super.pause()){
                return false;
            }
            myContext.removeServiceListener(this);
            return true;
        }
        
        @Override
        public boolean resume(){
            if(myContext == null || 
                    PlayState.PAUSED != getPlayState() || !super.resume()){
                return false;
            }
            return listen();
        }
        
        @Override
        public boolean stop(){
            if(myContext == null || !super.stop()){
                return false;
            }
            myContext.removeServiceListener(this);
            return true;
        }
        
        private boolean listen(){
            try{
                myContext.addServiceListener(this, myFilter);
                return true;
            }catch(InvalidSyntaxException ex){
                theLogger.log(Level.SEVERE, 
                        "Invalid LDAP filter: " + myFilter, ex);
                return false;
            }
        }
    }
}
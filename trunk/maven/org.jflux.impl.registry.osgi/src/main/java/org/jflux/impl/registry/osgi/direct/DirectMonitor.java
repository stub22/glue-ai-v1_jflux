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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
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
 * A bare OSGi monitor with no JFlux wrappers.
 * @author Matthew Stevenson
 */
public class DirectMonitor<Time> implements Monitor<
        Descriptor<String,String>, ServiceEvent> {
    private final static Logger theLogger = Logger.getLogger(DirectMonitor.class.getName());
    private BundleContext myContext;
    private Map<Listener<ServiceEvent>,Map<Descriptor<String,String>,ServiceEventNotifier>> myListenerDescriptorMap;
    private Map<Descriptor<String,String>,ServiceEventNotifier> myDescriptorNotifierMap;
    private Map<ServiceEventNotifier,List<Descriptor<String,String>>> myNotifierUseMap;
    
    public DirectMonitor(BundleContext context){
        if(context == null){
            throw new NullPointerException();
        }
        myContext = context;
    }

    @Override
    public synchronized void addListener(Descriptor<String, String> desc, Listener<ServiceEvent> listener) {
        ServiceEventNotifier n = myDescriptorNotifierMap.get(desc);
        if(n == null){
            String filter = OSGiRegistryUtil.getFullFilter(desc);
            n = new ServiceEventNotifier(myContext, filter);
            myDescriptorNotifierMap.put(desc, n);
        }
        Map<Descriptor<String,String>,ServiceEventNotifier> m = myListenerDescriptorMap.get(listener);
        if(m == null){
            m = new HashMap<Descriptor<String, String>, ServiceEventNotifier>();
            myListenerDescriptorMap.put(listener, m);
        }
        if(m.containsKey(desc)){
            return;
        }
        m.put(desc, n);
        n.addListener(listener);
        List<Descriptor<String,String>> l = myNotifierUseMap.get(n);
        if(l == null){
            l = new ArrayList<Descriptor<String, String>>();
            myNotifierUseMap.put(n, l);
            n.start();
        }
        l.add(desc);
        
    }

    @Override
    public synchronized void removeListener(Descriptor<String, String> desc, Listener<ServiceEvent> listener) {
        Map<Descriptor<String,String>,ServiceEventNotifier> m = myListenerDescriptorMap.get(listener);
        if(m == null){
            return;
        }
        ServiceEventNotifier n = m.get(desc);
        if(n == null){
            return;
        }
        n.removeListener(listener);
        m.remove(desc);
        if(m.isEmpty()){
            myListenerDescriptorMap.remove(listener);
        }
        List<Descriptor<String,String>> l = myNotifierUseMap.get(n);
        if(l != null){
            l.remove(desc);
            if(!l.contains(desc)){
                myDescriptorNotifierMap.remove(desc);
            }
        }if(l == null || l.isEmpty()){
            myNotifierUseMap.remove(n);
            n.stop();
        }
    }

    @Override
    public synchronized void removeListener(Listener<ServiceEvent> listener) {
        Map<Descriptor<String,String>,ServiceEventNotifier> m = myListenerDescriptorMap.get(listener);
        if(m == null){
            return;
        }else if(m.isEmpty()){
            myListenerDescriptorMap.remove(listener);
            return;
        }
        Set<Descriptor<String,String>> ds = 
                new TreeSet<Descriptor<String, String>>(m.keySet());
        for(Descriptor<String,String> d : ds){
            if(d == null){
                continue;
            }
            removeListener(d, listener);
        }
    }
    
    /**
     * A notifier for service events.
     */
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
        
        /**
         * Notifies listeners when a service's state changes.
         * @param event the state change
         */
        @Override
        public void serviceChanged(ServiceEvent event) {
            notifyListeners(event);
        }
        
        /**
         * Starts the notifier
         * @return true if the notifier starts successfully
         */
        @Override
        public boolean start(){
            if(myContext == null || !super.start()){
                return false;
            }
            return listen();
        }
        
        /**
         * Pauses the notifier
         * @return true if the notifier pauses successfully
         */
        @Override
        public boolean pause(){
            if(myContext == null || 
                    PlayState.RUNNING != getPlayState() || !super.pause()){
                return false;
            }
            myContext.removeServiceListener(this);
            return true;
        }
        
        /**
         * Resumes the notifier if paused.
         * @return  true if the notifier resumes successfully
         */
        @Override
        public boolean resume(){
            if(myContext == null || 
                    PlayState.PAUSED != getPlayState() || !super.resume()){
                return false;
            }
            return listen();
        }
        
        /**
         * Stops the notifier
         * @return  true if the notifier stops successfully
         */
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
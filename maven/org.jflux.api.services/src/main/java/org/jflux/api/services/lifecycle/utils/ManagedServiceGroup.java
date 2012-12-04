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
package org.jflux.api.services.lifecycle.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.jflux.api.services.ManagedService;
import org.jflux.api.services.ServiceLifecycleProvider;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public class ManagedServiceGroup {
    
    /**
     * The property key for the group ID.
     */
    public final static String PROP_GROUP_ID = "serviceGroupId";
    
    private ManagedServiceFactory myFactory;
    private List<ServiceLifecycleProvider> myLifecycles;
    private List<ManagedService> myServices;
    private String myGroupId;
    private boolean myStartFlag;
    
    /**
     * The group's properties.
     */
    protected Properties myServiceProperties;
    
    /**
     * Creates a managed service group.
     * @param factory the managed service factory
     * @param lifecycles a list of lifecycle providers
     * @param groupId the ID of the group
     * @param registrationProperties the group's properties
     */
    public ManagedServiceGroup(
            ManagedServiceFactory factory,
            List<ServiceLifecycleProvider> lifecycles, 
            String groupId, Properties registrationProperties){
        if(factory == null || lifecycles == null || groupId == null){
            throw new NullPointerException();
        }
        myFactory = factory;
        myLifecycles = lifecycles;
        myServiceProperties = registrationProperties;
        myGroupId = groupId;
        myStartFlag = false;
        if(myServiceProperties == null){
            myServiceProperties = new Properties();
        }
        myServiceProperties.put(PROP_GROUP_ID, myGroupId);
    }
    
    /**
     * Starts the group's services.
     */
    public synchronized void start(){
        if(myStartFlag){
            return;
        }
        if(myServices == null){
            myServices = new ArrayList<ManagedService>(myLifecycles.size()); 
        }
        for(ServiceLifecycleProvider lifecycle : myLifecycles){
            Properties props = new Properties();
            props.putAll(myServiceProperties);
            ManagedService service = myFactory.createService(lifecycle, props);
            if(service != null){
                myServices.add(service);
                service.start();
            }
        }
        myStartFlag = true;
    }
    
    /**
     * Stops the group's services.
     */
    public synchronized void stop(){
        if(!myStartFlag || myServices == null || myServices.isEmpty()){
            return;
        }
        for(ManagedService service : myServices){
            service.stop();
            service.unregister();
        }
        myStartFlag = false;
    }
}

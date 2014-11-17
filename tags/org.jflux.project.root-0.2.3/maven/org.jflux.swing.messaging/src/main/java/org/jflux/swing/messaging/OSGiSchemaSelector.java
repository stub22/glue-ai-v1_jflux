/*
 * Copyright 2013 Hanson Robokind LLC.
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
package org.jflux.swing.messaging;

import java.util.ArrayList;
import java.util.List;
import org.apache.avro.Schema;
import org.jflux.api.core.Notifier;
import org.jflux.api.core.config.Configuration;
import org.jflux.api.core.util.DefaultNotifier;
import org.jflux.impl.encode.avro.SerializationConfigUtils;
import org.jflux.impl.messaging.rk.config.RKMessagingConfigUtils;
import org.jflux.impl.services.rk.osgi.OSGiUtils;
import org.jflux.impl.services.rk.osgi.ServiceClassListener;
import org.osgi.framework.BundleContext;

/**
 *
 * @author matt
 */
public class OSGiSchemaSelector extends ServiceClassListener<Configuration> {
    private List<Configuration> myConfigs;
    private Notifier myChangeNotifier;
    
    public OSGiSchemaSelector(BundleContext context){
        super(Configuration.class, context, OSGiUtils.createFilter(RKMessagingConfigUtils.SERIALIZATION_CONFIG, "*"));
        myChangeNotifier = new DefaultNotifier();
    }

    @Override
    protected synchronized void addService(Configuration t) {
        try{
            if(!t.containsKey(Schema.class, SerializationConfigUtils.CONF_AVRO_RECORD_SCHEMA)){
                return;
            }
        }catch(NullPointerException ex){
            return;
        }
        
        if(myConfigs == null) {
            myConfigs = new ArrayList<Configuration>();
        }
        
        if(!myConfigs.contains(t)){
            myConfigs.add(t);
        }
        myChangeNotifier.notifyListeners(t);
    }

    @Override
    protected synchronized void removeService(Configuration t) {
        if(myConfigs == null) {
            return;
        }
        
        if(myConfigs.contains(t)){
            myConfigs.remove(t);
        }
        myChangeNotifier.notifyListeners(t);
    }
    
    public synchronized List<Schema> getSchemas(){
        List<Schema> schemas = new ArrayList();
        
        if(myConfigs == null) {
            return schemas;
        }
        
        for(Configuration c : myConfigs){
            Schema schema = (Schema)c.getPropertyValue(Schema.class, SerializationConfigUtils.CONF_AVRO_RECORD_SCHEMA);
            if(schema == null || schemas.contains(schema)){
                continue;
            }
            schemas.add(schema);
        }
        return schemas;
    }
    
    public Notifier getChangeNotifier(){
        return myChangeNotifier;
    }
}

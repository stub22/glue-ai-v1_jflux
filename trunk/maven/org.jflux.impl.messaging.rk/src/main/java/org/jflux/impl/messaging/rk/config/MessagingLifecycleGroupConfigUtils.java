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
package org.jflux.impl.messaging.rk.config;

import org.jflux.api.core.Listener;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Arrays;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import org.jflux.api.core.config.Configuration;
import org.jflux.api.core.util.MapAdapter.MapValueAdapter;
import org.jflux.impl.services.rk.lifecycle.ServiceLifecycleProvider;
import org.jflux.impl.services.rk.lifecycle.config.GenericLifecycle.DependencyChange;
import org.jflux.impl.services.rk.lifecycle.config.RKManagedGroupConfigUtils;
import org.jflux.impl.transport.jms.ConnectionConfigUtils.ConnectionFactory;
import org.jflux.impl.transport.jms.ConnectionConfigUtils.DestinationFactory;
import org.jflux.impl.transport.jms.ConnectionConfigUtils.SessionFactory;
import org.jflux.impl.messaging.rk.utils.ConnectionManager;

import static org.jflux.impl.services.rk.lifecycle.config.RKDependencyConfigUtils.*;
import static org.jflux.impl.services.rk.lifecycle.config.RKLifecycleConfigUtils.*;
import static org.jflux.impl.services.rk.lifecycle.config.RKManagedGroupConfigUtils.*;
import static org.jflux.impl.messaging.rk.config.RKMessagingComponentConfigUtils.*;

/**
 *
 * @author Matthew Stevenson
 */
public class MessagingLifecycleGroupConfigUtils {
    private final static Logger theLogger = Logger.getLogger(MessagingLifecycleGroupConfigUtils.class.getName());
    private final static String theConnectionConfig = "connectionConfig";
    private final static String theDestinationConfig = "destinationConfig";
    private final static String theConnection = "connection";
    private final static String theSession = "session";
    private final static String theDestination = "destination";
    
    private final static String theConnectConfigIdKey = RKMessagingConfigUtils.JMS_CONNECTION_CONFIG;
    private final static String theDestinationConfigIdKey = RKMessagingConfigUtils.JMS_DESTINATION_CONFIG;
    private final static String theConnectionIdKey = ConnectionManager.PROP_CONNECTION_ID;
    private final static String theSessionIdKey = ConnectionManager.PROP_CONNECTION_ID;
    private final static String theDestinationIdKey = ConnectionManager.PROP_DESTINATION_ID;
    
    public final static String NOTIFIER_COMPONENT = "remoteNotifier";
    public final static String LISTENER_COMPONENT = "remoteListener";
    public final static String SOURCE_COMPONENT = "remoteSource";
    
    public final static int REMOTE_NOTIFIER = 0;
    public final static int REMOTE_LISTENER = 1;
    public final static int REMOTE_SOURCE = 2;
    public final static int REMOTE_POLY_NOTIFIER = 3;
    
    public final static String GROUP_PREFIX = "RKMessagingGroup";
    
    private final static String theIdFormat = "%s/" + GROUP_PREFIX + "/%s";
        
    public static Configuration<String> buildDestinationLifecycleConfig(
            String destinationConfigId, String destinationId){
        Configuration<String> depConf = buildLifecycleDependencyConfig(
                theDestinationConfig, Configuration.class, theDestinationConfigIdKey, 
                destinationConfigId,null ,
                new Listener<DependencyChange<Destination,Configuration>>(){
                    @Override public void handleEvent(
                            DependencyChange<Destination,Configuration> input) {
                        if(input.getDependency() != null){
                            input.getLifecycle().start(input.getAvailableDependencies());
                        }else{
                            input.getLifecycle().stop();
                        }
                    }});
        
        return buildGenericLifecycleConfig(new String[]{Destination.class.getName()}, 
                theDestinationIdKey, destinationId, null, Arrays.asList(depConf), 
                new MapValueAdapter(theDestinationConfig, new DestinationFactory()));
    }
    
    public static Configuration<String> buildConnectionLifecycleConfig(
            String connectionConfigId, String connectionId){
        Configuration<String> depConf = buildLifecycleDependencyConfig(
                theConnectionConfig, Configuration.class, theConnectConfigIdKey, 
                connectionConfigId, null, 
                new Listener<DependencyChange<Connection,Configuration>>(){
                    @Override public void handleEvent(
                            DependencyChange<Connection,Configuration> input) {
                        if(input.getDependency() != null){
                            input.getLifecycle().start(input.getAvailableDependencies());
                        }else{
                            if(input.getService() != null){
                                try{
                                    input.getService().close();
                                }catch(JMSException ex){
                                    theLogger.warning("Error closing connection.");
                                }
                            }
                            input.getLifecycle().stop();
                        }
                    }});
        
        return buildGenericLifecycleConfig(new String[]{Connection.class.getName()}, 
                theConnectionIdKey, connectionId, null, Arrays.asList(depConf), 
                new MapValueAdapter(theConnectionConfig, new ConnectionFactory()));
    }
    
    public static Configuration<String> buildSessionLifecycleConfig(
            String connectionId, String sessionId){
        Configuration<String> depConf = buildLifecycleDependencyConfig(
                theConnection, Connection.class, theConnectionIdKey, 
                connectionId, null, 
                new Listener<DependencyChange<Session,Connection>>(){
                    @Override public void handleEvent(
                            DependencyChange<Session,Connection> input) {
                        if(input.getDependency() != null){
                            input.getLifecycle().start(input.getAvailableDependencies());
                        }else{
                            if(input.getService() != null){
                                try{
                                    input.getService().close();
                                }catch(JMSException ex){
                                    theLogger.warning("Error closing connection.");
                                }
                            }
                            input.getLifecycle().stop();
                        }
                    }});
        
        return buildGenericLifecycleConfig(new String[]{Session.class.getName()}, 
                theSessionIdKey, sessionId, null, Arrays.asList(depConf), 
                new MapValueAdapter(theConnection, new SessionFactory()));
    }
    
    public static Configuration<String> 
            buildMessagingComponentLifecycleGroupConfig(
            String groupId, Properties groupProps,
            int componentType, String componentId, 
            Configuration<String> serializeConf,
            String destinationConfId,String connectionConfId,
            String destId, String connectionId, String sessionId){
        if(groupId == null || componentId == null
                || serializeConf == null || destinationConfId == null 
                || connectionConfId == null || destId == null 
                || connectionId == null || sessionId == null){
            throw new NullPointerException();
        }
        
        List<Configuration<String>> selfBuildLifecycles = 
                getJMSLifecycleConfigs(
                        destinationConfId, connectionConfId, 
                        destId, connectionId, sessionId);
        componentId = componentId(groupId, componentType);
        Configuration<String> componentConf = 
                buildComponentLifecycleConfig(
                        componentType, componentId, 
                        destId, sessionId, serializeConf);
        
        selfBuildLifecycles.add(componentConf);
        return buildManagedGroupConfig(groupId, groupProps, selfBuildLifecycles);
    }
    
    public static Configuration<String> 
            buildMessagingComponentLifecycleGroupConfig(
            String groupId, Properties groupProps, 
            int componentType,
            Configuration<String> serializeConf, 
            String destinationConfigId,
            String connectionConfigId){
        if(groupId == null 
                || connectionConfigId == null || destinationConfigId == null){
            throw new NullPointerException();
        }
        
        List<Configuration<String>> selfBuildLifecycles = 
                getJMSLifecycleConfigs(groupId,
                destinationConfigId, 
                connectionConfigId);
        String componentId = componentId(groupId, componentType);
        Configuration<String> componentConf = 
                buildComponentLifecycleConfig(
                        componentType, componentId, 
                        childId(groupId,theDestination), 
                        childId(groupId,theSession),
                        serializeConf);
        
        selfBuildLifecycles.add(componentConf);
        return buildManagedGroupConfig(groupId, groupProps, selfBuildLifecycles);
    }
    
    private static List<Configuration<String>>  getJMSLifecycleConfigs(
            String groupId, String destinationConfigId, String connectionConfigId){
        if(groupId == null){
            throw new NullPointerException();
        }
        return getJMSLifecycleConfigs(
                destinationConfigId, 
                connectionConfigId, 
                childId(groupId,theDestination), 
                childId(groupId,theConnection), 
                childId(groupId,theSession));
    }
    
    private static List<Configuration<String>> getJMSLifecycleConfigs(
            String destinationConfId, String connectionConfId,
            String destId, String connectionId, String sessionId){
        List<Configuration<String>> selfBuildLifecycles = new ArrayList();
        selfBuildLifecycles.add(makeSelfBuildingLifecycle(
                buildDestinationLifecycleConfig(destinationConfId, destId)));
        selfBuildLifecycles.add(makeSelfBuildingLifecycle(
                buildConnectionLifecycleConfig(connectionConfId, connectionId)));
        selfBuildLifecycles.add(makeSelfBuildingLifecycle(
                buildSessionLifecycleConfig(connectionId, sessionId)));
        
        return selfBuildLifecycles;
    }
    
    private static Configuration<String> buildComponentLifecycleConfig(
            int componentType, String componentId, 
            String destinationId, String sessionId,  
            Configuration<String> config){
        if(config == null){
            throw new NullPointerException();
        }
        ServiceLifecycleProvider lifecycle = 
                buildComponentLifecycle(
                        componentType, componentId, 
                        destinationId, sessionId, config);
        if(lifecycle == null){
            throw new NullPointerException();
        }
        return RKManagedGroupConfigUtils.makeSelfBuildingLifecycle(lifecycle);
    }
    
    private static ServiceLifecycleProvider buildComponentLifecycle(
            int componentType, String componentId, 
            String destinationId, String sessionId,  
            Configuration<String> config){
        switch(componentType){
            case REMOTE_NOTIFIER: 
                return buildAvroRemoteNotifierLifecycle(
                        componentId, destinationId, sessionId, config);
            case REMOTE_LISTENER: 
                return buildAvroRemoteListenerLifecycle(
                        componentId, destinationId, sessionId, config);
            case REMOTE_SOURCE: 
                return buildJMSRemoteSourceLifecycle(
                        componentId, destinationId, sessionId, config);
            case REMOTE_POLY_NOTIFIER: 
                return buildJMSAvroPolymorphicRemoteNotifierLifecycle(
                        componentId, destinationId, sessionId, config);
            default: throw new IllegalArgumentException(
                    "Invalid component type: " + componentType);
        }
    }
    
    public static String childId(String groupId, String part){
        return String.format(theIdFormat, groupId, part);
    }
    
    private static String componentId(String groupId, int componentType){
        switch(componentType){
            case REMOTE_NOTIFIER: return childId(groupId, NOTIFIER_COMPONENT);
            case REMOTE_LISTENER: return childId(groupId, LISTENER_COMPONENT);
            case REMOTE_SOURCE: return childId(groupId, SOURCE_COMPONENT);
            case REMOTE_POLY_NOTIFIER: return childId(groupId, NOTIFIER_COMPONENT);
            default: throw new IllegalArgumentException(
                    "Invalid component type: " + componentType);
        }
    }
}
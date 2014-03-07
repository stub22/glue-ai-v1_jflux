/*
 * Copyright 2011 Hanson Robokind LLC.
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
package org.jflux.impl.messaging.rk.utils;

import java.net.URISyntaxException;
import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import org.apache.qpid.client.AMQAnyDestination;
import org.apache.qpid.client.AMQConnectionFactory;
import org.apache.qpid.client.AMQQueue;
import org.apache.qpid.client.AMQTopic;
import org.jflux.impl.services.rk.osgi.OSGiUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for creating JMS Connections and Destinations and managing
 * them within the OSGi Service Registry.  AMQP/Qpid is used for creating the
 * connections and destinations.
 * 
 * @author Matthew Stevenson <www.robokind.org>
 */
public class ConnectionManager {
    private final static Logger theLogger = LoggerFactory.getLogger(ConnectionManager.class);
    private final static String theAMQPFormatString = "amqp://%s:%s@%s/%s?brokerlist='%s'";
    
    /**
     * Property name for a Connection's unique id within the OSGi Service 
     * Registry.
     */
    public final static String PROP_CONNECTION_ID = "connectionId";
    /**
     * Property name for a Destination's unique id within the OSGi Service 
     * Registry.
     */
    public final static String PROP_DESTINATION_ID = "destinationId";
    
    /**
     * Formats an AMQP connection URL from the given details.
     * @param username broker username
     * @param password broker password
     * @param clientName connection client name
     * @param virtualHost connection virtual host
     * @param tcpAddress broker tcp address
     * @return AMQP connection URL
     */
    public static String createAMQPConnectionURL(
            String username, String password, 
            String clientName, String virtualHost, String tcpAddress){
        return String.format(theAMQPFormatString, 
                username, password, clientName, virtualHost, tcpAddress);
    }
    
    /**
     * Create a JMS Connection from the given details.
     * @param username broker username
     * @param password broker password
     * @param clientName connection client name
     * @param virtualHost connection virtual host
     * @param tcpAddress broker tcp address
     * @return JMS Connection
     */
    public static Connection createConnection(
            String username, String password, 
            String clientName, String virtualHost, String tcpAddress){
        String amqpURL = createAMQPConnectionURL(username, password, 
                clientName, virtualHost, tcpAddress);
        return createConnection(amqpURL);
    }
    
    /**
     * Creates an JMS Connection from the given connection URL
     * @param amqpURL AMQP connection URL for the broker
     * @return JMS Connection created with the given URL
     */
    public static Connection createConnection(String amqpURL){
        try{
            String reconnectOptions = "&connectdelay='5000'&retries='2147483647'";
            amqpURL += reconnectOptions;
            ConnectionFactory cf = new AMQConnectionFactory(amqpURL);
            return cf.createConnection();
        }catch(Exception ex){
            theLogger.warn("Unable to create connection.", ex);
            return null;
        }
    }
    
    /**
     * Creates a JMS Destination from the given destination String
     * @param destinationStr destination String defining a JMS Destination
     * @return JMS Destination
     */
    public static Destination createDestination(String destinationStr){
        try{
            return new AMQAnyDestination(destinationStr);
        }catch(URISyntaxException ex){
            theLogger.warn("Unable to create destination.", ex);
            return null;
        }
    }
    
    public static Destination createQueue(String destinationStr){
        try{
            return new AMQQueue(destinationStr);
        }catch(URISyntaxException ex){
            theLogger.warn("Unable to create destination.", ex);
            return null;
        }
    }
    
    public static Destination createTopic(String destinationStr){
        try{
            return new AMQTopic(destinationStr);
        }catch(URISyntaxException ex){
            theLogger.warn("Unable to create destination.", ex);
            return null;
        }
    }
    
    /**
     * Registers a JMS Connection to OSGi Service Registry, using a unique id.
     * @param context BundleContext for OSGi
     * @param connectionId unique id for registering the connection
     * @param connection the JMS Connection to register
     * @param props optional OSGi registration properties
     * @return ServiceRegistration for the connection, null if the id is already
     * in use or some other registration error occurred
     */
    public static ServiceRegistration registerConnection(BundleContext context, 
            String connectionId, Connection connection, Properties props){
        if(context == null || connection == null){
            throw new NullPointerException();
        }
        return OSGiUtils.registerUniqueService(context, 
                Connection.class.getName(), 
                PROP_CONNECTION_ID, connectionId, 
                connection, props);
    }    
    /**
     * Registers a JMS Connection to OSGi Service Registry, using a unique id.
     * @param context BundleContext for OSGi
     * @param destinationId unique id for registering the destination
     * @param destination the JMS Destination to register
     * @param props optional OSGi registration properties
     * @return ServiceRegistration for the destination, null if the id is 
     * already in use or some other registration error occurred
     */
    public static ServiceRegistration registerDestination(BundleContext context, 
            String destinationId, Destination destination, Properties props){
        if(context == null || destination == null){
            throw new NullPointerException();
        }
        return OSGiUtils.registerUniqueService(context, 
                Destination.class.getName(), 
                PROP_DESTINATION_ID, destinationId, 
                destination, props);
    }
}

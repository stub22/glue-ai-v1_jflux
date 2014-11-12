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
package org.jflux.impl.messaging.rk.utils;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import org.jflux.impl.services.rk.osgi.OSGiUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public class ConnectionUtils {
    private final static Logger theLogger = 
            Logger.getLogger(ConnectionUtils.class.getName());
    /**
     * Indicates a JMS Queue where Message go to one consumer.
     */
    public final static int QUEUE = 0;
    /**
     * Indicates a JMS Topic where Message are broadcasted to all consumers.
     */
    public final static int TOPIC = 1;
    
    public final static String USERNAME_KEY =
            "org.jflux.impl.messaging.rk.user";
    
    public final static String PASSWORD_KEY =
            "org.jflux.impl.messaging.rk.pass";
    
    /**
     * Retrieve the configured username (or the default if no configuration).
     * @return the Qpid username
     */
    public static String getUsername() {
        String username =
                System.getProperty(USERNAME_KEY, System.getenv(USERNAME_KEY));
        return username != null ? username : "admin";
    }

    /**
     * Retrieve the configured password (or the default if no configuration).
     * @return the Qpid password
     */
    public static String getPassword() {
        String password =
                System.getProperty(PASSWORD_KEY, System.getenv(PASSWORD_KEY));
        return password != null ? password : "admin";
    }
    
    /**
     * Registers the Connection if there is not one already registered to the
     * given id.  If a connection already exists, this connection is not 
     * registered.
     * @param context BundleContext for OSGi
     * @param conId unique connection id to use
     * @param con connection to register
     * @param props optional OSGi registration properties
     * @return true if there is a connection with the given id, either 
     * pre-existing or the given connection, false if a connection is not 
     * available (one did not previously exist and registration failed)
     */
    public static ServiceRegistration ensureSession(BundleContext context, 
            String conId, Connection con, Properties props){
        if(con == null){
            throw new NullPointerException();
        }
        ensureConnection(context, conId, con, props);
        if(OSGiUtils.serviceExists(context, Session.class.getName(), 
                ConnectionManager.PROP_CONNECTION_ID, conId, props)){
            theLogger.log(Level.INFO, 
                    "Connection for Id: {0} alreaday exists.  "
                    + "No need to create new Connection.", conId);
            return null;
        }
        Session session;
        try{
            session = con.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        }catch(JMSException ex){
            theLogger.log(Level.WARNING, "Unable to create session.", ex);
            return null;
        }
        ServiceRegistration reg = OSGiUtils.registerUniqueService(context, 
                Session.class.getName(), ConnectionManager.PROP_CONNECTION_ID, 
                conId, session, props);
        if(reg == null){
            theLogger.log(Level.WARNING, 
                    "Unable to register connection with Id: {0}.", conId);
            return null;
        }
        //do something with reg
        theLogger.log(Level.INFO, 
                "Connection with Id: {0} registered successfully.", conId);
        return reg;
    }
    public static boolean ensureConnection(BundleContext context, 
            String conId, Connection con, Properties props){
        if(con == null){
            throw new NullPointerException();
        }
        if(OSGiUtils.serviceExists(context, Connection.class.getName(), 
                ConnectionManager.PROP_CONNECTION_ID, conId, props)){
            theLogger.log(Level.INFO, 
                    "Connection for Id: {0} alreaday exists.  "
                    + "No need to create new Connection.", conId);
            return true;
        }
        ServiceRegistration reg = ConnectionManager.registerConnection(
                context, conId, con, props);
        if(reg == null){
            theLogger.log(Level.WARNING, 
                    "Unable to register connection with Id: {0}.", conId);
            return false;
        }
        //do something with reg
        theLogger.log(Level.INFO, 
                "Connection with Id: {0} registered successfully.", conId);
        return true;
    }
    /**
     * Creates and registers a Destination if there is not one already 
     * registered to the given id.
     * @param context BundleContext for OSGi
     * @param destId unique destination id to use
     * @param destName destination name
     * @param type destination type, QUEUE or TOPIC
     * @param props optional OSGi registration properties
     * @return true if there is a destination with the given id, either 
     * pre-existing or newly created, false if a destination is not 
     * available (one did not previously exist and registration failed)
     */
    public static ServiceRegistration ensureDestination(BundleContext context, 
            String destId, String destName, int type, Properties props){
        if(OSGiUtils.serviceExists(context, Destination.class.getName(), 
                ConnectionManager.PROP_DESTINATION_ID, destId, props)){
            theLogger.log(Level.INFO, 
                    "Destination for Id: {0} alreaday exists.  "
                    + "No need to create new Destination.", destId);
            return null;
        }
        String destStr = buildNameString(destName, type);
        Destination dest = ConnectionManager.createDestination(destStr);
        if(dest == null){
            theLogger.log(Level.WARNING, 
                    "Unable to create destination with Id: {0}.", destId);
            return null;
        }
        ServiceRegistration reg = ConnectionManager.registerDestination(
                context, destId, dest, props);
        if(reg == null){
            theLogger.log(Level.WARNING, 
                    "Unable to register destination with Id: {0}.", destId);
            return null;
        }
        //do something with reg
        theLogger.log(Level.INFO, 
                "Destination with Id: {0} registered successfully.", destId);
        return reg;
    }
    /**
     * Makes calls to <code>ensureDestination</code> with the given parameters.
     * @param context BundleContext for OSGi
     * @param destParams parameters for calls to <code>ensureDestination</code>,
     * must be in multiples of 4 with the correct types
     */
    public static Set<ServiceRegistration> ensureDestinations(
            BundleContext context, Object...destParams){
        Set<ServiceRegistration> regs = new HashSet<ServiceRegistration>();
        int len = destParams .length;
        if(len == 0 || len % 4 != 0){
            throw new IllegalArgumentException(
                    "Wrong number of param strings.  "
                    + "Expecting id/destination/type/properties triplets.");
        }
        for(int i=0; i<len-3; i+=4){
            String id = check(String.class, destParams[i]);
            String destStr = check(String.class, destParams[i+1]);
            Integer type = check(Integer.class, destParams[i+2]);
            Properties props = check(Properties.class, destParams[i+3]);
            ServiceRegistration reg = ConnectionUtils.ensureDestination(
                    context, id, destStr, type, props);
            
            if(reg != null) {
                regs.add(reg);
            }
        }
        
        return regs;
    }
    
    private static <T> T check(Class<T> clazz, Object obj){
        if(obj == null){
            return null;
        }
        if(!clazz.isAssignableFrom(obj.getClass())){
            throw new IllegalArgumentException("Expected: " + clazz.getName() + 
                    ".  Got: " + obj.getClass());
        }
        return (T)obj;
    }
    
    private static String buildNameString(String destName, int type){
        String fullName = destName;
        if(type == QUEUE){
            fullName += "; {create: always, node: {type: queue}}";
        }else if(type == TOPIC){
            fullName += "; {create: always, node: {type: topic}}";
        }
        return fullName;
    }
}

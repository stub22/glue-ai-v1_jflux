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
package org.jflux.impl.transport.jms;

import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import org.apache.qpid.client.AMQAnyDestination;
import org.apache.qpid.client.AMQConnectionFactory;
import org.apache.qpid.client.AMQQueue;
import org.apache.qpid.client.AMQTopic;
import org.apache.qpid.url.URLSyntaxException;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.config.Configuration;
import org.jflux.api.core.config.DefaultConfiguration;

/**
 *
 * @author Matthew Stevenson
 */
public class ConnectionConfigUtils {
    private final static Logger theLogger = Logger.getLogger(ConnectionConfigUtils.class.getName());
    private final static String theAMQPFormatString = "amqp://%s:%s@%s/%s?brokerlist='%s'";
    public final static String CONF_BROKER_IP = "msgBrokerIp";
    public final static String CONF_BROKER_PORT = "msgBrokerPort";
    public final static String CONF_BROKER_USERNAME = "msgBrokerUser";
    public final static String CONF_BROKER_PASSWORD = "msgBrokerPassword";
    public final static String CONF_BROKER_CLIENT_NAME = "msgBrokerClientName";
    public final static String CONF_BROKER_VIRTUAL_HOST = "msgBrokerVirtualHost";
    
    public final static String DEF_BROKER_IP = "127.0.0.1";
    public final static String DEF_BROKER_PORT = "5672";
    public final static String DEF_BROKER_USERNAME = "admin";
    public final static String DEF_BROKER_PASSWORD = "admin";
    public final static String DEF_BROKER_CLIENT_NAME = "client1";
    public final static String DEF_BROKER_VIRTUAL_HOST = "test";
    
    public final static String CONF_DESTINATION_NAME = "msgDestinationName";
    public final static String CONF_DESTINATION_CREATE = "msgDestinationCreateMode";
    public final static String CONF_DESTINATION_NODE_TYPE = "msgDestinationNodeType";
    public final static String CONF_DESTINATION_NODE_OPTIONS = "msgDestinationNodeOpts";
    public final static String CONF_DESTINATION_OPTIONS = "msgDestinationOpts";
    
    public final static String CREATE_ALWAYS = "always";
    public final static String CREATE_NEVER = "never";
    public final static String CREATE_SENDER = "sender";
    public final static String CREATE_RECEIVER = "receiver";
    
    public final static String NODE_QUEUE = "queue";
    public final static String NODE_TOPIC = "topic";
    public final static String NODE_UNKNOWN = "unknown";
    
    public final static String DEF_DESTINATION_CREATE = CREATE_NEVER;
    public final static String DEF_DESTINATION_NODE_TYPE = NODE_UNKNOWN;
    
    
    public static Configuration<String> buildConnectionConfig(){
        DefaultConfiguration<String> conf = new DefaultConfiguration<String>();
        
        conf.addProperty(String.class, CONF_BROKER_IP, "127.0.0.1");
        conf.addProperty(String.class, CONF_BROKER_PORT, "5672");
        conf.addProperty(String.class, CONF_BROKER_USERNAME, "admin");
        conf.addProperty(String.class, CONF_BROKER_PASSWORD, "admin");
        conf.addProperty(String.class, CONF_BROKER_CLIENT_NAME, "client1");
        conf.addProperty(String.class, CONF_BROKER_VIRTUAL_HOST, "test");
        
        return conf;
    }
    
    public static Configuration<String> buildConnectionConfig(String ip){
        if(ip == null){
            throw new NullPointerException();
        }
        Configuration<String> conf = buildConnectionConfig();
        if(ip != null){
            set(conf, CONF_BROKER_IP, ip);
        }        
        return conf;
    }
    
    public static Configuration<String> buildConnectionConfig(
            String ip, String port, String username, String password, 
            String clientName, String virtualHost){
        Configuration<String> conf = buildConnectionConfig(ip);
        if(port != null){
           set(conf, CONF_BROKER_PORT, port);
        }if(username != null){
           set(conf, CONF_BROKER_USERNAME, username);
        }if(password != null){
           set(conf, CONF_BROKER_PASSWORD, password);
        }if(clientName != null){
           set(conf, CONF_BROKER_CLIENT_NAME, clientName);
        }if(virtualHost != null){
           set(conf, CONF_BROKER_VIRTUAL_HOST, virtualHost);
        }
        return conf;
    }
    
    public static Configuration<String> buildDestinationConfig(String name){
        DefaultConfiguration<String> conf = new DefaultConfiguration<String>();
        
        conf.addProperty(String.class, CONF_DESTINATION_NAME, name);
        conf.addProperty(String.class, CONF_DESTINATION_NODE_TYPE, NODE_UNKNOWN);
        conf.addProperty(String.class, CONF_DESTINATION_CREATE, DEF_DESTINATION_CREATE);
        conf.addProperty(String.class, CONF_DESTINATION_OPTIONS, null);
        conf.addProperty(String.class, CONF_DESTINATION_NODE_OPTIONS, null);
        
        return conf;
    }
    
    public static Configuration<String> buildDestinationConfig(
            String name, String type){
        Configuration<String> conf = buildDestinationConfig(name);
        if(type != null){
            set(conf, CONF_DESTINATION_NODE_TYPE, type);
        }        
        return conf;
    }
    
    public static Configuration<String> buildDestinationConfig(
            String name, String type, String create){
        Configuration<String> conf = buildDestinationConfig(name, type);
        if(create != null){
            set(conf, CONF_DESTINATION_CREATE, create);
        }     
        return conf;
    }
    
    public static Configuration<String> buildDestinationConfig(
            String name, String type, String create, 
            String options, String nodeOptions){
        Configuration<String> conf = buildDestinationConfig(name, type, create);
        if(options != null){
            set(conf, CONF_DESTINATION_OPTIONS, options);
        }
        if(nodeOptions != null){
            set(conf, CONF_DESTINATION_NODE_OPTIONS, nodeOptions);
        }
        return conf;
    }
    
    static Connection createConnection(Configuration<String> conf) 
            throws JMSException, URLSyntaxException {
        String ip = conf.getPropertyValue(String.class, CONF_BROKER_IP);
        String port = conf.getPropertyValue(String.class, CONF_BROKER_PORT);
        String username = conf.getPropertyValue(String.class, CONF_BROKER_USERNAME);
        String password = conf.getPropertyValue(String.class, CONF_BROKER_PASSWORD);
        String clientName = conf.getPropertyValue(String.class, CONF_BROKER_CLIENT_NAME);
        String virtualHost = conf.getPropertyValue(String.class, CONF_BROKER_VIRTUAL_HOST);
        String addr = "tcp://" + ip + ":" + port;
        String amqpURL = String.format(theAMQPFormatString, 
                username, password, clientName, virtualHost, addr);
        String reconnectOptions = "&connectdelay='5000'&retries='2147483647'";
        amqpURL += reconnectOptions;
        AMQConnectionFactory cf = new AMQConnectionFactory(amqpURL);
        return cf.createConnection();
    }
    
    static Destination createDestination(Configuration<String> conf) 
            throws URISyntaxException{
        String destStr = conf.getPropertyValue(String.class, CONF_DESTINATION_NAME);
        String type = conf.getPropertyValue(String.class, CONF_DESTINATION_NODE_TYPE);
        String create = conf.getPropertyValue(String.class, CONF_DESTINATION_CREATE);
        String opts = conf.getPropertyValue(String.class, CONF_DESTINATION_OPTIONS);
        String nodeOpts = conf.getPropertyValue(String.class, CONF_DESTINATION_NODE_OPTIONS);
        if(destStr == null){
            throw new NullPointerException();
        }
        if(type == null || NODE_UNKNOWN.equals(type)){
            type = null;
            create = null;
        }
        nodeOpts = addOpts(nodeOpts, "type", type);
        if(nodeOpts != null && !nodeOpts.isEmpty()){
            nodeOpts = "{" + nodeOpts + "}";
        }
        opts = addOpts(opts, "create", create);
        opts = addOpts(opts, "node", nodeOpts);
        if(opts != null && !opts.isEmpty()){
            destStr += "; {" + opts + "}";
        }
        if(NODE_QUEUE.equals(type)){
            return new AMQQueue(destStr);
        }else if(NODE_TOPIC.equals(type)){
            return new AMQTopic(destStr);
        }else{
            return new AMQAnyDestination(destStr);
        }
    }
    
    private static String addOpts(String opts, String k, String v){
        if(k == null || k.isEmpty() || v == null || v.isEmpty()){
            return opts;
        }
        String kv = k + ":" + v;
        return (opts == null || opts.isEmpty()) ? kv : opts + ", " + kv; 
    }
    
    public static class ConnectionFactory implements 
            Adapter<Configuration<String>,Connection> {
        @Override 
        public Connection adapt(Configuration<String> a) {
            try{
                Connection connection = createConnection(a);
                connection.start();
                return connection;
            }catch(Exception ex){
                theLogger.log(Level.WARNING, "Unable to start connection.", ex);
                return null;
            }
        }
    }
    
    public static class DestinationFactory implements 
            Adapter<Configuration<String>,Destination> {
        @Override 
        public Destination adapt(Configuration<String> a) {
            try{
                return createDestination(a);
            }catch(URISyntaxException ex){
                theLogger.log(Level.WARNING, 
                        "Unable to create destination.", ex);
                return null;
            }
        }
    }
    
    public static class SessionFactory implements Adapter<Connection,Session> {
        @Override 
        public Session adapt(Connection a) {
            try{
                return a.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            }catch(JMSException ex){
                theLogger.log(Level.WARNING, "Unable to create Session.", ex);
                return null;
            }
        }
    }
    
    private static void set(Configuration<String> conf, String key, String val){
        conf.getPropertySetter(String.class, key).handleEvent(val);
    }
}

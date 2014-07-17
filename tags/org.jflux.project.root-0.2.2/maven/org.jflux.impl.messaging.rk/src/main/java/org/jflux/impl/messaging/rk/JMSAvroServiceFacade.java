/*
 * Copyright 2014 the JFlux Project.
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

package org.jflux.impl.messaging.rk;


import java.io.IOException;
import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.qpid.client.message.JMSBytesMessage;
import org.apache.avro.generic.IndexedRecord;
import org.jflux.api.common.rk.playable.AbstractPlayable;
import org.jflux.api.common.rk.utils.TimeUtils;
import org.jflux.api.messaging.rk.MessageAsyncReceiver;
import org.jflux.api.messaging.rk.MessageSender;
import org.jflux.api.messaging.rk.services.RemoteServiceClient;
import org.jflux.api.messaging.rk.services.ServiceCommand;
import org.jflux.api.messaging.rk.services.ServiceCommandFactory;
import org.jflux.api.messaging.rk.services.ServiceError;
import org.jflux.impl.messaging.rk.common.QpidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A facade for controlling a RemoteServiceClient using Avro and JMS (Qpid).
 * A JMSAvroServiceFacade has a JMS MessageProducer for sending commands to the
 * RemoteServiceClient, and a JMS MessageConsumer for receiving error messages. * 
 * (Error messages are not yet completed)
 * 
 * @param <Config> type of Avro Record used to configure the service
 * @author Matthew Stevenson <www.jflux.org>
 */
public class JMSAvroServiceFacade<Config extends IndexedRecord> 
        extends AbstractPlayable implements RemoteServiceClient<Config>{
    private final static Logger theLogger = 
            LoggerFactory.getLogger(JMSAvroServiceFacade.class);
    /**
     * Custom content/mime type used in the JMS header for config Records
     */
    public final static String CONFIG_MIME_TYPE = "application/config";
    /**
     * Custom content/mime type used in the JMS header for unknown avro record
     */
    public final static String AVRO_MIME_TYPE = "application/avro";
    /**
     * Custom content/mime type used in the JMS header for service command
     */
    public final static String COMMAND_MIME_TYPE = "application/service-command";
    
    private Class<Config> myConfigClass;
    private Config myReusableConfig;
    private Session mySession;
    private MessageProducer myCommandSender;
    private MessageConsumer myErrorReceiver;
    
    /**
     * Creates a new JMSAvroServiceFacade.
     * @param configClass class of the config record
     * @param reusableConfig reusable instance of the config
     * @param session JMS session for sending Records
     * @param commandDest JMS destination for commands
     * @param errorDest JMS destination for errors
     * @throws JMSException if there is an error creating producers and 
     * consumers
     */
    public JMSAvroServiceFacade(
            Class<Config> configClass, 
            Config reusableConfig,
            Session session, 
            Destination commandDest, 
            Destination errorDest) throws JMSException{
        myConfigClass = configClass;
        myReusableConfig = reusableConfig;
        mySession = session;
        myCommandSender = session.createProducer(commandDest);
        myErrorReceiver = session.createConsumer(errorDest);
    }
    
    /**
     * Creates a new JMSAvroServiceFacade.
     * @param configClass class of the config record
     * @param reusableConfig reusable instance of the config
     * @param session JMS session for sending Records
     * @param commandSender MessagePropducer to send commands
     * @param errorReceiver MessageConsumer to receive errors
     */
    public JMSAvroServiceFacade(
            Class<Config> configClass,
            Config reusableConfig,
            Session session, 
            MessageProducer commandSender, 
            MessageConsumer errorReceiver){
        myConfigClass = configClass;
        myReusableConfig = reusableConfig;
        mySession = session;
        myCommandSender = commandSender;
        myErrorReceiver = errorReceiver;
    }
    
    /**
     * Sets the JMS Session for creating BytesMessages.
     * @param session JMS Session to use
     */
    public void setSession(Session session){
        mySession = session;
    }
    
    /**
     * Sets the MessagePropducer to send commands.
     * @param producer MessageProducer to use
     */
    public void setSender(MessageProducer producer){
        myCommandSender = producer;
    }
    
    /**
     * Sets the MessageConsumer to receive errors.
     * @param consumer MessageConsumer to set
     */
    public void setReceiver(MessageConsumer consumer){
        myErrorReceiver = consumer;
    }
    
    @Override
    public void initialize(Config config) throws IOException, JMSException, Exception {
        send(config, CONFIG_MIME_TYPE);
    }
    
    @Override
    public boolean onStart(long time) {
        return sendCommand("start");
    }

    @Override
    public boolean onPause(long time) {
        return sendCommand("pause");
    }

    @Override
    public boolean onResume(long time) {
        return sendCommand("resume");
    }
    
    @Override
    public boolean onComplete(long time){
        return sendCommand("stop");
    }

    @Override
    public boolean onStop(long time) {
        return sendCommand("stop");
    }
    
    /**
     * Sends a custom Avro Record to the service.
     * @param <T> type of Avro Record to send
     * @param t the Record to send
     * @param contentType optional content type for the JMS header
     * @throws IOException on errors packing the Record into a JMS BytesMessage
     * @throws JMSException on errors sending the Record
     */
    protected <T extends IndexedRecord> void send(
            T t, String contentType) 
            throws IOException, JMSException{
        BytesMessage message = mySession.createBytesMessage();
        ((JMSBytesMessage)message).setContentType(contentType);
        QpidUtils.packAvroMessage(t, message);
        myCommandSender.send(message);
    }
    
    /**
     * Sends a service command.
     * @param command command to send
     * @return true if successful
     */
    protected boolean sendCommand(String command){
        ServiceCommandRecord rec = new ServiceCommandRecord();
        rec.setSourceId("src");
        rec.setDestinationId("dest");
        rec.setTimestampMillisecUTC(TimeUtils.now());
        rec.setCommand(command);
        try{
            send(rec, COMMAND_MIME_TYPE);
            return true;
        }catch(Exception ex){
            theLogger.error("Error sending Start Command.", ex);
            return false;
        }
    }
    
    public Class<Config> getConfigClass(){
        return myConfigClass;
    }
    
    public Config getReusableConfig(){
        return myReusableConfig;
    }

    @Override
    public void setCommandSender(MessageSender<ServiceCommand> sender) {
    }

    @Override
    public void setConfigSender(MessageSender<Config> sender) {
    }

    @Override
    public void setErrorReceiver(MessageAsyncReceiver<ServiceError> receiver) {
    }

    @Override
    public void setCommandFactory(ServiceCommandFactory factory) {
    }

    @Override
    public String getClientId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getHostId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}

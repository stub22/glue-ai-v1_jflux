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
package org.jflux.api.messaging.rk;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.Listener;

/**
 * Default MessageSender implementation.
 * Uses an Adapter to serialize a Message to a Record.  The Record is sent with
 * a RecordSender.
 * 
 * @author Matthew Stevenson <www.robokind.org>
 * @param <Msg> type of Message capable of sending
 * @param <Rec> type of Record which is sent
 */
public class DefaultMessageSender<Msg, Rec> implements MessageSender<Msg>{    
    static final Logger theLogger = Logger.getLogger(DefaultMessageSender.class.getName());
    private List<Listener<Msg>> myListeners;
    private Adapter<Msg, Rec> myAdapter;
    /**
     * RecordSender used to send Records.  This is set using the 
     * setRecordSender method.
     */
    protected RecordSender<Rec> myRecordSender;
    /**
     * Creates an empty DefaultMessageSender.
     */
    public DefaultMessageSender(){
        myListeners = new ArrayList<Listener<Msg>>();
    }
    /**
     * Sets the underlying RecordSender to send the serialized Records.
     * @param sender RecordSender to set
     */
    public void setRecordSender(RecordSender<Rec> sender){
        myRecordSender = sender;
    }
    /**
     * Sets the Adapter for serializing Messages to Records.
     * @param adapter Adapter to set
     */
    public void setAdapter(Adapter<Msg, Rec> adapter){
        myAdapter = adapter;
    }

    @Override
    public void start() throws Exception {}

    @Override
    public void stop() {}
    
    @Override
    public void notifyListeners(Msg message){
        Rec record = getRecord(message);
        if(record == null){
            theLogger.warning(
                    "Adapter returned null Record, unable to send message.");
            return;
        }
        myRecordSender.sendRecord(record);
        fireMessageEvent(message);
    }
    
    /**
     * Adapts a Message to a Record using an Adapter
     * @param message Message to adapt
     * @return Record created from the message
     */
    protected Rec getRecord(Msg message){
        if(message == null){
            throw new NullPointerException();
        }
        if(myRecordSender == null){
            theLogger.warning("No MessageSender, unable to send message.");
            return null;
        }else if(myAdapter == null){
            theLogger.warning("No Message Adapter, unable to send message.");
            return null;
        }
        return myAdapter.adapt(message);
    }
    
    /**
     * Notifies listeners of a Message being sent.
     * @param message Message being sent
     */
    protected void fireMessageEvent(Msg message){
        for(Listener<Msg> listener : myListeners){
            listener.handleEvent(message);
        }
    }
    
    @Override
    public void addListener(Listener<Msg> listener) {
        if(listener == null){
            return;
        }
        if(!myListeners.contains(listener)){
            myListeners.add(listener);
        }
    }

    @Override
    public void removeListener(Listener<Msg> listener) {
        if(listener == null){
            return;
        }
        myListeners.remove(listener);
    }

    @Override
    public void handleEvent(Msg event) {
        notifyListeners(event);
    }
    
}

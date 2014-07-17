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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.Listener;
import org.jflux.api.messaging.rk.RecordAsyncReceiver.RecordHandler;

/**
 * Default MessageAsyncReceiver implementation.
 * Uses a RecordAdapterHandler as a RecordHandler to passes Records to an
 * Adapter for deserialization.
 * 
 * @author Matthew Stevenson <www.robokind.org>
 * @param <Msg> type of Message received
 * @param <Rec> type of underlying Record received
 */
public class DefaultMessageAsyncReceiver<Msg, Rec> 
        implements MessageAsyncReceiver<Msg> {
    private final static Logger theLogger = 
            Logger.getLogger(DefaultMessageAsyncReceiver.class.getName());
    private RecordAsyncReceiver<Rec> myRecordService;
    private Adapter<Rec,Msg> myAdapter;
    private RecordAdapterHandler myRecordHandler;
    private List<Listener<Msg>> myListeners;
    
    /**
     * Creates an empty DefaultMessageAsyncReceiver.
     */
    public DefaultMessageAsyncReceiver(){
        myListeners = new ArrayList<Listener<Msg>>();
    }
    
    /**
     * Sets the RecordAsyncReceiver used to receive Records.
     * @param service theRecordReceiver to set
     */
    public void setRecordReceiver(RecordAsyncReceiver<Rec> service){
        if(myRecordService != null){
            myRecordService.unsetRecordHandler();
            myRecordService.pause();
        }
        myRecordService = service;
        if(myRecordService != null && myRecordHandler == null){
            myRecordHandler = new RecordAdapterHandler();
        }
        myRecordService.setRecordHandler(myRecordHandler);
    }
    
    /**
     * Sets the Adapter used to convert Records to Messages.
     * @param adapter  the Adapter to set
     */
    public void setAdapter(Adapter<Rec,Msg> adapter){
        myAdapter = adapter;
    }

    @Override
    public void start() throws Exception{
        if(myRecordService == null){
            theLogger.warning("No Record PollingService, unable to start.");
            return;
        }else if(myAdapter == null){
            theLogger.warning("No Record Adapter, unable to start.");
            return;
        }
        myRecordService.start();
    }

    @Override
    public void pause() {
        if(myRecordService == null){
            theLogger.warning("No Record PollingService, unable to pause.");
            return;
        }
        myRecordService.pause();
    }

    @Override
    public void resume() {
        if(myRecordService == null){
            theLogger.warning("No Record PollingService, unable to resume.");
            return;
        }else if(myAdapter == null){
            theLogger.warning("No Record Adapter, unable to resume.");
            return;
        }
        myRecordService.resume();
    }

    @Override
    public void stop() {
        if(myRecordService == null){
            theLogger.warning("No Record PollingService to stop.");
            return;
        }
        myRecordService.stop();
    }
    /**
     * Notifies listeners of a Message
     * @param message 
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
    public void notifyListeners(Msg e) {
        fireMessageEvent(e);
    }
    
    class RecordAdapterHandler implements RecordHandler<Rec>{
        @Override
        public void handleRecord(Rec record) {
            if(record == null){
                theLogger.info("Ignoring null record.");
                return;
            }
            if(myAdapter == null){
                theLogger.log(Level.WARNING, 
                        "No Record Adapter, unable to handle record: {0}.", 
                        record.toString());
                return;
            }
            Msg message = myAdapter.adapt(record);
            if(message == null){
                theLogger.log(Level.WARNING, 
                        "Null message returned when adapting record: {0}.", 
                        record.toString());
                return;
            }
            fireMessageEvent(message);
        }        
    }
}

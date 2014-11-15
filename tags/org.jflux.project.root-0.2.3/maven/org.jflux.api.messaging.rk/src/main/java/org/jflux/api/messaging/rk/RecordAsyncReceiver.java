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

/**
 * Asynchronously receives records sent from a RecordSender.
 * 
 * @author Matthew Stevenson <www.robokind.org>
 * @param <T> type of Record which can be received
 */
public interface RecordAsyncReceiver<T>{
    /**
     * Sets the RecordHandler used to handle incoming Records.
     * @param handler the RecordHandler to set
     */
    public void setRecordHandler(RecordHandler<T> handler);
    /**
     * Removes the current RecordHandler.
     * A RecordAsyncReceiver with no RecordHandler will ignore and discard received
     * Records.
     */
    public void unsetRecordHandler();
    /**
     * Starts the PollingService.  Once called, the service begins fetching
     * Records.
     * @throws IllegalStateException if no message handler is set
     */
    public void start() throws IllegalStateException;
    /**
     * Pauses the RecordAsyncReceiver.  
     * Records may arrive, but they will nor be handled until the Receiver is 
     * resumed.
     */
    public void pause();
    /**
     * Resumes a paused RecordAsyncReceiver.
     */
    public void resume();
    /**
     * Completely stops a RecordAsyncReceiver.
     */
    public void stop();
    
    /**
     * A RecordHandler is used by the RecordAsyncReceiver to handle the Records it
     * receives.
     * @param <T> the type of Records handled 
     */
    public static interface RecordHandler<T> {
        /**
         * Handles a record.
         * @param record the Record to handle
         */
        public void handleRecord(T record);
    }
}

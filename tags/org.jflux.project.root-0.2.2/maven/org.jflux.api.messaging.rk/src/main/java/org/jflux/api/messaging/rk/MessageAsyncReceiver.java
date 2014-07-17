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

import org.jflux.api.core.Listener;
import org.jflux.api.core.Notifier;

/**
 * High-Level Message Receiver.
 * Contains a RecordAsyncReceiver for receiving Records.
 * Contains an Adapter for converting Records back to Messages.
 * Notifies Listeners of Messages Received.
 * 
 * @author Matthew Stevenson <www.robokind.org>
 * @param <Msg> type of Message received
 */
public interface MessageAsyncReceiver<Msg> extends Notifier<Msg> {    
    /**
     * Initializes and connects the MessageAsyncReceiver, and begins receiving
     * Messages.
     * @throws Exception if there is an error starting
     */
    public void start() throws Exception;
    /**
     * Pauses a MessageAsyncReceiver.  
     */
    public void pause();
    /**
     * Resumes a paused MessageAsyncReceiver.
     */
    public void resume();
    /**
     * Stops the MessageAsyncReceiver, making it unable to receive Messages.
     */
    public void stop();
    
    /**
     * Adds a Listener to be notified when a Message has been received.
     * @param listener the Listener to be notified
     */
    @Override
    public void addListener(Listener<Msg> listener);
    /**
     * Removes a Listener from being notified.
     * @param listener the Listener to remove
     */
    @Override
    public void removeListener(Listener<Msg> listener);
}

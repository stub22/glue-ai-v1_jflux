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
import org.jflux.api.core.Source;

/**
 * Synchronous message receiver.
 * Uses a RecordBlockingReceiver to wait for a Record.
 * Uses an Adapter to adapt Records to Messages. 
 * 
 * @param <Msg> type of Message to return 
 * @author Matthew Stevenson <www.robokind.org>
 */
public interface MessageBlockingReceiver<Msg> extends Source<Msg> {
    public final static int DEFAULT_TIMEOUT_LENGTH = 5000;
    /**
     * Initializes and connects the MessageAsyncReceiver, and begins receiving
     * Messages.
     * @throws Exception if there is an error starting
     */
    public void start() throws Exception;
    /**
     * Stops the MessageAsyncReceiver, making it unable to receive Messages.
     */
    public void stop();
    
    /**
     * Blocks while waiting for a Message.
     * @return Message received from a MessageSender, null if no Message 
     * received before timeout
     */    
    @Override
    public Msg getValue();
    
    public void setTimeout(long timeout);
    
    public long getTimeout();
    
    /**
     * Removes any pending messages for this receiver.  
     * Returns the number of messages cleared.
     * @return number of messages cleared
     */
    public int clearMessages();
    
    /**
     * Adds a Listener to be notified when a Message has been received.
     * @param listener the Listener to be notified
     */
    public void addMessageListener(Listener<Msg> listener);
    /**
     * Removes a Listener from being notified.
     * @param listener the Listener to remove
     */
    public void removeMessageListener(Listener<Msg> listener);
}

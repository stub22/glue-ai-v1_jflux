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
 * High-level message sender for sending API objects.
 * A typical MessageSender will serialize the Message to a Record and send it
 * using a low-level RecordSender.
 * Notifies Listeners of Messages sent.
 * 
 * @author Matthew Stevenson <www.robokind.org>
 * @param <Msg> type of message to send
 */
public interface MessageSender<Msg> extends Listener<Msg>, Notifier<Msg>{
    
    /**
     * Initializes and connects the MessageSender, making it ready to send
     * Messages.
     * @throws Exception if there is an error starting
     */
    public void start() throws Exception;
    /**
     * Stops the MessageSender, making it unable to send Messages.
     */
    public void stop();
    
    /**
     * Sends the given Message.
     * @param message the Message to send
     */
    @Override
    public void notifyListeners(Msg message);
    
    /**
     * Adds a Listener to be notified when a Message has been sent.
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

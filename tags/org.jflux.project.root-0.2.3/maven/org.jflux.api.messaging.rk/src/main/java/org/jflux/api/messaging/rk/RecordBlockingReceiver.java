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
 * Synchronously receives records sent from a RecordSender.
 * 
 * @author Matthew Stevenson <www.robokind.org>
 * @param <T> type of Record which can be received
 */
public interface RecordBlockingReceiver<T>{
    /**
     * Blocks while waiting for a Record.
     * @param timeout timeout length in milliseconds
     * @return Record received from a RecordSender
     */
    public T fetchRecord(long timeout);
    
    /**
     * Removes any pending records for this receiver.  
     * Returns the number of records cleared.
     * @return number of records cleared
     */
    public int clearRecords();
}

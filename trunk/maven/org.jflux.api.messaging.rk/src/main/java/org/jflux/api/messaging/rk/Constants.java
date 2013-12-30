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
package org.jflux.api.messaging.rk;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public class Constants {
    /**
     * Property string for identifying a Message Sender.
     */
    public final static String PROP_MESSAGE_SENDER_ID = "messageSenderId";
    /**
     * Property string for identifying a Message Receiver.
     */
    public final static String PROP_MESSAGE_RECEIVER_ID = "messageReceiverId";
    /**
     * Property string for specifying a Message type.
     */
    public final static String PROP_MESSAGE_TYPE = "messageType";
    
    /**
     * Property string for identifying a Record Sender.
     */
    public final static String PROP_RECORD_SENDER_ID = "recordSenderId";
    /**
     * Property string for identifying a Record Receiver.
     */
    public final static String PROP_RECORD_RECEIVER_ID = "recordReceiverId";
    /**
     * Property string for specifying a Record type.
     */
    public final static String PROP_RECORD_TYPE = "recordType";
    
    public final static String PROP_ENCODER_ID = "messageEncoderId";
    
    public final static String PROP_DECODER_ID = "recordDecoderId";
}

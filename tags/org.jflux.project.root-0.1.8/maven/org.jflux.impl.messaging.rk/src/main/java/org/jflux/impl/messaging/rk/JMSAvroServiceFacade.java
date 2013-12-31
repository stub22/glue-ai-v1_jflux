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

package org.jflux.impl.messaging.rk;

/**
 * A facade for controlling a RemoteServiceClient using Avro and JMS (Qpid).
 * A JMSAvroServiceFacade has a JMS MessageProducer for sending commands to the
 * RemoteServiceClient, and a JMS MessageConsumer for receiving error messages. * 
 * (Error messages are not yet completed)
 * 
 * @param <Config> type of Avro Record used to configure the service
 * @author Matthew Stevenson <www.robokind.org>
 */
public class JMSAvroServiceFacade {
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
}

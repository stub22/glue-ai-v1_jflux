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

package org.jflux.api.messaging.rk.services;

import org.jflux.api.common.rk.playable.Playable;
import org.jflux.api.messaging.rk.MessageAsyncReceiver;
import org.jflux.api.messaging.rk.MessageSender;

/**
 * RemoteServiceClient is an interface for controlling a Service through Messaging.
 * RemoteServiceClient extends Playable, offering methods to start, pause, resume, 
 * and stop the service.
 * 
 * @author Matthew Stevenson <www.jflux.org>
 * @param <Config> type of configuration used to initialize the Service.
 */
public interface RemoteServiceClient<Config> extends Playable{
    /**
     * Initializes the Service with the given config.
     * @param config the configuration object used to initialize the Service
     * @throws Exception if there is an error initializing
     */
    public void initialize(Config config) throws Exception;
    
    public void setCommandSender(MessageSender<ServiceCommand> sender);
    
    public void setConfigSender(MessageSender<Config> sender);
    
    public void setErrorReceiver(MessageAsyncReceiver<ServiceError> receiver);
    
    public void setCommandFactory(ServiceCommandFactory factory);
    
    public String getClientId();
    
    public String getHostId();
}

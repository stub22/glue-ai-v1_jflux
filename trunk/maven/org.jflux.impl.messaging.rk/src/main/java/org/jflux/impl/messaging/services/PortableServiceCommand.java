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
package org.jflux.impl.messaging.services;

import org.jflux.api.common.rk.utils.TimeUtils;
import org.jflux.api.messaging.rk.services.ServiceCommand;
import org.jflux.api.messaging.rk.services.ServiceCommandFactory;
import org.jflux.impl.messaging.rk.ServiceCommandRecord;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class PortableServiceCommand {
    public static class Factory implements ServiceCommandFactory{
        @Override
        public ServiceCommand create(
                String sourceId, String destId, String command) {
            ServiceCommandRecord record = new ServiceCommandRecord();
            record.setSourceId(sourceId);
            record.setDestinationId(destId);
            record.setTimestampMillisecUTC(TimeUtils.now());
            record.setCommand(command);
            
            return record;
        }   
    }
}

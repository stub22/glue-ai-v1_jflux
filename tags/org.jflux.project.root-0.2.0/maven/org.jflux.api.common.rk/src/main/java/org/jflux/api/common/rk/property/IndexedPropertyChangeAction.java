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


package org.jflux.api.common.rk.property;

import java.beans.IndexedPropertyChangeEvent;

/**
 * PropertyChangeAction for an indexed property
 * 
 * @author Matthew Stevenson <www.jflux.org>
 */
public abstract class IndexedPropertyChangeAction extends PropertyChangeActionBase<IndexedPropertyChangeEvent> {
    
    @Override
    public Class<IndexedPropertyChangeEvent> getEventType() {
        return IndexedPropertyChangeEvent.class;
    }

}

/*
 * Copyright 2013 The JFlux Project (www.jflux.org).
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
package org.jflux.api.registry.basic;

import org.jflux.api.registry.Reference;
import org.jflux.api.registry.RegistryEvent;

/**
 *
 * @author matt
 */
public class BasicRegistryEvent implements RegistryEvent{    
    private Reference myReference;
    private int myEventType;
    
    public BasicRegistryEvent(int type, Reference ref){
        if(ref == null){
            throw new NullPointerException();
        }
        myEventType = type;
        myReference = ref;
    }
    
    @Override
    public Reference getReference() {
        return myReference;
    }

    @Override
    public int getEventType() {
        return myEventType;
    }
}

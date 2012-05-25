/*
 *  Copyright 2012 by The JFlux Project (www.jflux.org).
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
package org.jflux.api.core.node;

import org.jflux.api.core.playable.BasicPlayable;
import org.jflux.api.core.playable.PlayableListener;
import org.jflux.api.core.Listener;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class DefaultConsumerNode<In> extends 
        BasicPlayable implements ConsumerNode<In> {
    private Listener<In> myListener;
    private Class<In> myInputClass;

    public DefaultConsumerNode(Class<In> inputClass, Listener<In> proc){
        if(inputClass == null || proc == null){
            throw new NullPointerException();
        }
        myInputClass = inputClass;
        myListener = new PlayableListener<In>(this, proc);
    }
    
    @Override
    public Listener<In> getListener() {
        return myListener;
    }

    @Override
    public Class<In> getConsumedClass() {
        return myInputClass;
    }
}

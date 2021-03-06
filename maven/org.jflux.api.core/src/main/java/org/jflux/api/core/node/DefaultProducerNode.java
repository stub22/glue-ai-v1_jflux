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
import org.jflux.api.core.playable.ConditionalNotifier;
import org.jflux.api.core.Notifier;

/**
 * Basic implementation of a ProducerNode
 * @author Matthew Stevenson <www.jflux.org>
 * @param <Out> information type
 */
public class DefaultProducerNode<Out> extends 
        BasicPlayable implements ProducerNode<Out> {
    private Notifier<Out> myNotifier;

    /**
     * Builds a DefaultProducerNode around a Notifier
     * @param notifier the Notifier
     */
    public DefaultProducerNode(Notifier<Out> notifier){
        if(notifier == null){
            throw new NullPointerException();
        }
        myNotifier = new ConditionalNotifier<Out>(this, notifier);
    }

    /**
     * Get the internal Notifier
     * @return the internal Notifier
     */
    @Override
    public Notifier<Out> getNotifier() {
        return myNotifier;
    }
}

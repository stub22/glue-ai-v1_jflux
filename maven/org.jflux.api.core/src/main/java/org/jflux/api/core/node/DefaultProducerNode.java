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

import org.jflux.api.core.util.playable.BasicPlayable;
import org.jflux.api.core.util.proc.Producer;
import org.jflux.api.core.util.Notifier;
import org.jflux.api.core.util.playable.PlayableNotifier;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public class DefaultProducerNode<Out> extends 
        BasicPlayable implements ProducerNode<Out> {
    private Notifier<Out> myNotifier;
    protected Producer<Out> myProducer;

    public DefaultProducerNode(Producer<Out> producer){
        if(producer == null){
            throw new NullPointerException();
        }
        myProducer = producer;
        myNotifier = new PlayableNotifier<Out>(this, myProducer);
    }

    @Override
    public Notifier<Out> getNotifier() {
        return myNotifier;
    }

    @Override
    public Class<Out> getProducedClass() {
        return myProducer.getOutputClass();
    }
}

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
import org.jflux.api.core.playable.ConditionalListener;
import org.jflux.api.core.playable.ConditionalNotifier;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.util.DefaultNotifier;
import org.jflux.api.core.Listener;
import org.jflux.api.core.Notifier;

/**
 *
 * @param <In> 
 * @param <Out> 
 * @author Matthew Stevenson <www.jflux.org>
 */
public class DefaultProcessorNode<In, Out> extends 
        BasicPlayable implements ProcessorNode<In, Out> {
    private Adapter<In, Out> myProcessor;
    private Listener<In> myInputListener;
    private Notifier<Out> myOutputNotifier;

    /**
     *
     * @param proc
     */
    public DefaultProcessorNode(Adapter<In,Out> proc){
        if(proc == null){
            throw new NullPointerException();
        }
        myInputListener = 
                new ConditionalListener<In>(this, new DefaultInputListener());
        myOutputNotifier = 
                new ConditionalNotifier<Out>(this, new DefaultNotifier<Out>());
        myProcessor = proc;
    }
    
    /**
     *
     * @return
     */
    @Override
    public Listener<In> getListener() {
        return myInputListener;
    }

    /**
     *
     * @return
     */
    @Override
    public Notifier<Out> getNotifier() {
        return myOutputNotifier;
    }
    
    class DefaultInputListener implements Listener<In>{
        @Override
        public void handleEvent(In event) {
            Notifier<Out> notifier = getNotifier();
            if(event == null || myProcessor == null || notifier == null){
                return;
            }
            Out out = myProcessor.adapt(event);
            if(out != null){
                notifier.notifyListeners(out);
            }
        }
    }
}

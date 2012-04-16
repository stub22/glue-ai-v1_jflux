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
import org.jflux.api.core.util.proc.Processor;
import org.jflux.api.core.util.DefaultNotifier;
import org.jflux.api.core.util.Listener;
import org.jflux.api.core.util.Notifier;
import org.jflux.api.core.util.playable.PlayableListener;
import org.jflux.api.core.util.playable.PlayableNotifier;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class DefaultProcessorNode<In, Out> extends 
        BasicPlayable implements ProcessorNode<In, Out> {
    private Processor<In, Out> myProcessor;
            
    protected Listener<In> myInputListener;
    protected Notifier<Out> myOutputNotifier;

    public DefaultProcessorNode(Processor<In,Out> proc){
        if(proc == null){
            throw new NullPointerException();
        }
        myInputListener = 
                new PlayableListener<In>(this, new DefaultInputListener());
        myOutputNotifier = 
                new PlayableNotifier<Out>(this, new DefaultNotifier<Out>());
        myProcessor = proc;
    }
    
    @Override
    public Listener<In> getListener() {
        return myInputListener;
    }

    @Override
    public Notifier<Out> getNotifier() {
        return myOutputNotifier;
    }
    @Override
    public Class<In> getConsumedClass() {
        return myProcessor.getInputClass();
    }

    @Override
    public Class<Out> getProducedClass() {
        return myProcessor.getOutputClass();
    }
    
    class DefaultInputListener implements Listener<In>{
        @Override
        public void handleEvent(In event) {
            Notifier<Out> notifier = getNotifier();
            if(event == null || myProcessor == null || notifier == null){
                return;
            }
            Out out = myProcessor.process(event);
            if(out != null){
                notifier.notifyListeners(out);
            }
        }
    }
}

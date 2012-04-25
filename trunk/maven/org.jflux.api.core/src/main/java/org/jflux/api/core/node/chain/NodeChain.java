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
package org.jflux.api.core.node.chain;

import java.util.ArrayList;
import java.util.List;
import org.jflux.api.core.node.ConsumerNode;
import org.jflux.api.core.node.Node;
import org.jflux.api.core.node.ProcessorNode;
import org.jflux.api.core.node.ProducerNode;
import org.jflux.api.core.playable.Playable;
import org.jflux.api.core.playable.PlayableGroup;
import org.jflux.api.core.util.Notifier;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class NodeChain<P,C> extends PlayableGroup implements Node{
    private ProducerNode<P> myProducer;
    private List<ProcessorNode<?,?>> myProcessors;
    private ConsumerNode<C> myConsumer;
    private List<Playable> myPlayables;
    private boolean myWiredFlag;
    
    NodeChain(List<ProcessorNode<?,?>> chain){
        if(chain == null){
            throw new NullPointerException();
        }
        build(null, chain, null);
    }
    
    NodeChain(ProducerNode<P> producer, List<ProcessorNode<?,?>> chain){
        if(producer == null || chain == null){
            throw new NullPointerException();
        }
        build(producer, chain, null);
    }
    
    NodeChain(List<ProcessorNode<?,?>> chain, ConsumerNode<C> consumer){
        if(chain == null || consumer == null){
            throw new NullPointerException();
        }
        build(null, chain, consumer);
    }
    
    NodeChain(ProducerNode<P> producer, 
            List<ProcessorNode<?,?>> chain, ConsumerNode<C> consumer){
        if(producer == null || consumer == null){
            throw new NullPointerException();
        }
        build(producer, chain, consumer);
    }
    
    private void build(
            ProducerNode<P> producer, 
            List<ProcessorNode<?,?>> chain,
            ConsumerNode<C> consumer){
        myProducer = producer;
        myProcessors = chain;
        myConsumer = consumer;
        myWiredFlag = false;
        
        int len = myProducer == null ? 0 : 1;
        len = myConsumer == null ? len : len + 1;
        len = myProcessors == null ? len : len + 1;
        
        myPlayables = new ArrayList<Playable>(len);
        Class inputClass = null;
        if(myProducer != null){
            myPlayables.add(myProducer);
            inputClass = myProducer.getProducedClass();
        }
        if(myProcessors != null){
            for(ProcessorNode<?,?> proc : myProcessors){
                if(inputClass != null && 
                        !proc.getConsumedClass().isAssignableFrom(inputClass)){
                    throw new IllegalArgumentException(
                            "Bad processor input class.");
                }
                myPlayables.add(proc);
                inputClass = proc.getProducedClass();
            }
        }
        if(myConsumer != null){
            if(inputClass != null && 
                    !myConsumer.getConsumedClass().isAssignableFrom(inputClass)){
                throw new IllegalArgumentException(
                        "Bad consumer input class.");
            }
            myPlayables.add(myConsumer);
        }
    }
    
    protected ProducerNode<P> getProducer(){
        return myProducer;
    }
    
    protected List<ProcessorNode<?,?>> getProcessorChain(){
        return myProcessors;
    }
    
    protected ConsumerNode<C> getConsumer(){
        return myConsumer;
    }
    
    @Override
    public boolean start(){
        if(!super.start()){
            return false;
        }
        if(!myWiredFlag){
            wire();
        }
        return true;
    }
    
    protected void wire(){
        Notifier n = null;
        if(myProducer != null){
            n = myProducer.getNotifier();
            if(myProducer instanceof NodeChain){
                ((NodeChain)myProducer).wire();
            }
        }
        for(ProcessorNode<?,?> proc : myProcessors){
            if(proc == null){
                continue;
            }
            n = n == null ? proc.getNotifier() : wire(n, proc);
            if(proc instanceof NodeChain){
                ((NodeChain)proc).wire();
            }
        }
        if(myConsumer != null){
            wire(n,myConsumer);
            if(myConsumer instanceof NodeChain){
                ((NodeChain)myConsumer).wire();
            }
        }
        myWiredFlag = true;
    }
    
    private <N,T> Notifier<N> wire(Notifier<T> n, ProcessorNode<T,N> proc){
        if(proc == null){
            return null;
        }
        if(n != null){
            n.addListener(proc.getListener());
        }
        return proc.getNotifier();
    }
    
    private <T> void wire(Notifier<T> n, ConsumerNode<T> consumer){
        if(n == null || consumer == null){
            return;
        }
        n.addListener(consumer.getListener());
    }

    @Override
    protected Iterable<Playable> getPlayables() {
        return myPlayables;
    }
}

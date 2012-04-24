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
    private ProcessorChain<P,C> myProcessorChain;
    private ConsumerNode<C> myConsumer;
    private List<Playable> myPlayables;
    private boolean myWiredFlag;
    
    NodeChain(ProducerNode<P> producer, ProcessorChain<P,C> chain){
        if(producer == null || chain == null){
            throw new NullPointerException();
        }
        build(producer, chain, null);
    }
    
    NodeChain(ProcessorChain<P,C> chain, ConsumerNode<C> consumer){
        if(chain == null || consumer == null){
            throw new NullPointerException();
        }
        build(null, chain, consumer);
    }
    
    NodeChain(ProducerNode<P> producer, 
            ProcessorChain<P,C> chain, ConsumerNode<C> consumer){
        if(producer == null || consumer == null){
            throw new NullPointerException();
        }
        build(producer, chain, consumer);
    }
    
    private void build(
            ProducerNode<P> producer, 
            ProcessorChain<P,C> chain,
            ConsumerNode<C> consumer){
        myProducer = producer;
        myProcessorChain = chain;
        myConsumer = consumer;
        myWiredFlag = false;
        
        int len = myProducer == null ? 0 : 1;
        len = myConsumer == null ? len : len + 1;
        len = myProcessorChain == null ? len : len + 1;
        
        myPlayables = new ArrayList<Playable>(len);
        if(myProducer != null){
            myPlayables.add(myProducer);
        }
        if(myProcessorChain != null){
            myPlayables.add(myProcessorChain);
        }
        if(myConsumer != null){
            myPlayables.add(myConsumer);
        }
    }
    
    protected ProducerNode<P> getProducer(){
        return myProducer;
    }
    
    protected ProcessorChain<P,C> getProcessorChain(){
        return myProcessorChain;
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
        Notifier n = myProducer == null ? null : myProducer.getNotifier();
        for(ProcessorNode proc : myProcessorChain.getProcessorNodes()){
            if(proc == null){
                continue;
            }
            n = n == null ? proc.getNotifier() : wire(n, proc);
        }
        if(myConsumer != null){
            wire(n,myConsumer);
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

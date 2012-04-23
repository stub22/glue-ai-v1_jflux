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
import org.jflux.api.core.node.DefaultConsumerNode;
import org.jflux.api.core.node.DefaultProcessorNode;
import org.jflux.api.core.node.DefaultProducerNode;
import org.jflux.api.core.node.ProcessorNode;
import org.jflux.api.core.node.ProducerNode;
import org.jflux.api.core.util.Adapter;
import org.jflux.api.core.util.Listener;
import org.jflux.api.core.util.Notifier;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class NodeChainBuilder<T> {
    private ProducerNode myProducer;
    private List<ProcessorNode> myProcessorList;
    private Class<T> myTailClass;
    
    public NodeChainBuilder(Class<T> inputClass){
        if(inputClass == null){
            throw new NullPointerException();
        }
        myProcessorList = new ArrayList<ProcessorNode>();
        myTailClass = inputClass;
    }
    
    public NodeChainBuilder(Class<T> outputClass, Notifier<T> producer){
        this(new DefaultProducerNode<T>(outputClass, producer));
    }
    
    public NodeChainBuilder(ProducerNode<T> producer){
        if(producer == null){
            throw new NullPointerException();
        }
        myProducer = producer;
        myTailClass = myProducer.getProducedClass();
        myProcessorList = new ArrayList<ProcessorNode>();
    }
    
    public <N> NodeChainBuilder<N> attach(
            Class<N> outputClass, Adapter<T,N> adapter){
        if(outputClass == null || adapter == null){
            throw new NullPointerException();
        }
        return attach(new DefaultProcessorNode<T, N>(
                myTailClass, outputClass, adapter));
    }
    
    public <N> NodeChainBuilder<N> attach(ProcessorNode<T,N> proc){
        if(proc == null){
            throw new NullPointerException();
        }else if(!proc.getConsumedClass().isAssignableFrom(myTailClass)){
            throw new IllegalArgumentException(
                    "Expected class: " + myTailClass 
                    + ", found class: " + proc.getConsumedClass());
        }
        myProcessorList.add(proc);
        NodeChainBuilder<N> newChainBuilder = (NodeChainBuilder<N>)this;
        newChainBuilder.myTailClass = proc.getProducedClass();
        return newChainBuilder;
    }
    
    public NodeChain attach(Listener<T> consumer){
        if(consumer == null){
            throw new NullPointerException();
        }
        return attach(new DefaultConsumerNode<T>(myTailClass, consumer));
    }
    
    public NodeChain attach(ConsumerNode<T> consumer){
        if(consumer == null){
            throw new NullPointerException();
        }else if(!consumer.getConsumedClass().isAssignableFrom(myTailClass)){
            throw new IllegalArgumentException(
                    "Expected class: " + myTailClass 
                    + ", found class: " + consumer.getConsumedClass());
        }
        if(myProducer == null){
            return getConsumerChain(consumer);
        }else if(consumer == null){
            return getProducerChain();
        }
        ProcessorChain chain = myProcessorList == null ? 
                null : new ProcessorChain(myProcessorList);
        return new NodeChain(myProducer, chain, consumer);
    }
    
    public NodeChain getNodeChain(){
        ProcessorChain chain = myProcessorList == null ? 
                null : new ProcessorChain(myProcessorList);
        return new NodeChain(myProducer, chain, null);
    }
    
    public ConsumerChain getConsumerChain(Listener<T> listener){
        return getConsumerChain(
                new DefaultConsumerNode<T>(myTailClass, listener));
    }
    
    public ConsumerChain getConsumerChain(ConsumerNode<T> node){
        return new ConsumerChain(new ProcessorChain(myProcessorList), node);
    }
    
    public ProcessorChain<?,T> getProcessorChain(){
        return new ProcessorChain(myProcessorList);
    }
    
    public ProducerChain getProducerChain(){
        return new ProducerChain(
                myProducer, new ProcessorChain(myProcessorList));
    }
}

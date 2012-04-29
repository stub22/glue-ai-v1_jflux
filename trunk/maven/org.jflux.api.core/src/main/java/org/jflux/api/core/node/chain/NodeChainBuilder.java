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
import org.jflux.api.core.node.*;
import org.jflux.api.core.util.Adapter;
import org.jflux.api.core.util.Listener;
import org.jflux.api.core.util.Notifier;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class NodeChainBuilder<H,T> {
    private ProducerNode myProducer;
    private List<ProcessorNode> myProcessorList;
    private Class<T> myTailClass;
    
    public static <C> NodeChainBuilder<C,C> build(Class<C> head){
        return new NodeChainBuilder<C, C>(head,head);
    }
    
    public static <C> NodeChainBuilder<C,C> build(
            Class<C> head, Notifier<C> producer){
        return build(new DefaultProducerNode<C>(head, producer));
    }
    
    public static <C> NodeChainBuilder<C,C> build(ProducerNode<C> producer){
        if(producer == null){
            throw new NullPointerException();
        }
        Class<C> c = producer.getProducedClass();
        NodeChainBuilder<C, C> builder = new NodeChainBuilder<C, C>(c,c);
        builder.myProducer = producer;
        return builder;
    }
    
    private NodeChainBuilder(Class<H> head, Class<T> tail){
        if(head == null || tail == null){
            throw new NullPointerException();
        }
        myProcessorList = new ArrayList<ProcessorNode>();
        myTailClass = tail;
    }
    
    public <N> NodeChainBuilder<H,N> attach(
            Class<N> outputClass, Adapter<T,N> adapter){
        if(outputClass == null || adapter == null){
            throw new NullPointerException();
        }
        return attach(new DefaultProcessorNode<T, N>(
                myTailClass, outputClass, adapter));
    }
    
    public <N> NodeChainBuilder<H,N> attach(ProcessorNode<T,N> proc){
        if(proc == null){
            throw new NullPointerException();
        }else if(!proc.getConsumedClass().isAssignableFrom(myTailClass)){
            throw new IllegalArgumentException(
                    "Expected class: " + myTailClass 
                    + ", found class: " + proc.getConsumedClass());
        }
        myProcessorList.add(proc);
        NodeChainBuilder<H,N> newChainBuilder = (NodeChainBuilder<H,N>)this;
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
        return new NodeChain(myProducer, myProcessorList, consumer);
    }
    
    public NodeChain getNodeChain(){
        return new NodeChain(myProducer, myProcessorList, null);
    }
    
    public ConsumerChain<H> getConsumerChain(Listener<T> listener){
        return getConsumerChain(
                new DefaultConsumerNode<T>(myTailClass, listener));
    }
    
    public ConsumerChain<H> getConsumerChain(ConsumerNode<T> node){
        return new ConsumerChain(myProcessorList, node);
    }
    
    public ProcessorChain<H,T> getProcessorChain(){
        return new ProcessorChain(myProcessorList);
    }
    
    public ProducerChain<T> getProducerChain(){
        return new ProducerChain(myProducer, myProcessorList);
    }
}

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
import org.jflux.api.core.Adapter;
import org.jflux.api.core.Listener;
import org.jflux.api.core.Notifier;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class NodeChainBuilder<H,T> {
    private ProducerNode myProducer;
    private List<ProcessorNode> myProcessorList;
    
    public static <H,T> NodeChainBuilder<H,T> build(Adapter<H,T> adapter){
        if(adapter == null){
            throw new NullPointerException();
        }
        return build(new DefaultProcessorNode(adapter));
    }
    
    public static <H,T> NodeChainBuilder<H,T> build(ProcessorNode<H,T> proc){
        if(proc == null){
            throw new NullPointerException();
        }
        return new NodeChainBuilder<H, H>().attach(proc);
    }
    
    public static <C> NodeChainBuilder<C,C> build(Notifier<C> producer){
        if(producer == null){
            throw new NullPointerException();
        }
        return build(new DefaultProducerNode<C>(producer));
    }
    
    public static <C> NodeChainBuilder<C,C> build(ProducerNode<C> producer){
        if(producer == null){
            throw new NullPointerException();
        }
        NodeChainBuilder<C, C> builder = new NodeChainBuilder<C, C>();
        builder.myProducer = producer;
        return builder;
    }
    
    private NodeChainBuilder(){
        myProcessorList = new ArrayList<ProcessorNode>();
    }
    
    public <N> NodeChainBuilder<H,N> attach(Adapter<T,N> adapter){
        if(adapter == null){
            throw new NullPointerException();
        }
        return attach(new DefaultProcessorNode<T, N>(adapter));
    }
    
    public <N> NodeChainBuilder<H,N> attach(ProcessorNode<T,N> proc){
        if(proc == null){
            throw new NullPointerException();
        }
        myProcessorList.add(proc);
        return (NodeChainBuilder<H,N>)this;
    }
    
    public NodeChain attach(Listener<T> consumer){
        if(consumer == null){
            throw new NullPointerException();
        }
        return attach(new DefaultConsumerNode<T>(consumer));
    }
    
    public NodeChain attach(ConsumerNode<T> consumer){
        if(consumer == null){
            throw new NullPointerException();
        }
        if(myProducer == null){
            return getConsumerChain(consumer);
        }else if(consumer == null){
            return getProducerChain();
        }
        return new NodeChain(myProducer, myProcessorList, consumer);
    }
    
    public NodeChain getNodeChain(){
        return new NodeChain(myProducer, myProcessorList);
    }
    
    public ConsumerChain<H> getConsumerChain(Listener<T> listener){
        return getConsumerChain(new DefaultConsumerNode<T>(listener));
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

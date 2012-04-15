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
import org.jflux.api.core.node.ProcessorNode;
import org.jflux.api.core.node.ProducerNode;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public class NodeChainBuilder<T> {
    private ProducerNode myProducer;
    private List<ProcessorNode> myProcessorList;
    private Class<T> myTailClass;
    
    public NodeChainBuilder(ProducerNode<T> producer){
       myProducer = producer;
       myTailClass = myProducer.getProducedClass();
       myProcessorList = new ArrayList<ProcessorNode>();
    }
    
    public <N> NodeChainBuilder<N> attach(ProcessorNode<T,N> proc){
        Class<T> tailClass = getTailClass();
        if(tailClass != null 
                && !proc.getConsumedClass().isAssignableFrom(tailClass)){
            throw new IllegalArgumentException(
                    "Expected class: " + tailClass 
                    + ", found class: " + proc.getConsumedClass());
        }
        myProcessorList.add(proc);
        NodeChainBuilder<N> newChainBuilder = (NodeChainBuilder<N>)this;
        newChainBuilder.setTailClass(proc.getProducedClass());
        return newChainBuilder;
    }
    
    public NodeChain attach(ConsumerNode<T> consumer){
        Class<T> tailClass = getTailClass();
        if(tailClass != null && 
                !consumer.getConsumedClass().isAssignableFrom(tailClass)){
            throw new IllegalArgumentException(
                    "Expected class: " + tailClass 
                    + ", found class: " + consumer.getConsumedClass());
        }
        return new NodeChain(myProducer, myProcessorList, consumer);
    }
    
    public NodeChain getNodeChain(){
        return new NodeChain(myProducer, myProcessorList, null);
    }
    
    Class<T> getTailClass(){
        return myTailClass;
    }
    
    void setTailClass(Class<T> clazz){
        myTailClass = clazz;
    }
}

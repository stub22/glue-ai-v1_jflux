/*
 * Copyright 2012 by The JFlux Project (www.jflux.org).
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

import java.util.List;
import org.jflux.api.core.node.ProcessorNode;
import org.jflux.api.core.node.ProducerNode;
import org.jflux.api.core.Notifier;

/**
 * NodeChain that acts as a single ProducerNode
 * @author Matthew Stevenson <www.jflux.org>
 * @param <T> output data type
 */
public class ProducerChain<T> extends NodeChain implements ProducerNode<T> {
    
    /**
     * Builds a ProducerChain
     * @param <P> output data type
     * @param producer initial ProducerNode
     * @param chain List of ProcessorNodes fed by the ProducerNode
     */
    public <P> ProducerChain(
            ProducerNode<P> producer, List<ProcessorNode<?,?>> chain){
        super(producer, chain);
    }
    
    /**
     * Get the underlying Notifier
     * @return the underlying Notifier
     */
    @Override
    public Notifier<T> getNotifier() {
        List<ProcessorNode> nodes = getProcessorChain();
        if(nodes != null && !nodes.isEmpty()){
            return nodes.get(nodes.size()-1).getNotifier();
        }else if(getProducer() != null){
            return getProducer().getNotifier();
        }
        return null;
    }
    
    /**
     * Get the initial Producer
     * @return the initial Producer
     */
    @Override
    public ProducerNode getProducer(){
        return super.getProducer();
    }
    
    /**
     * Get the ProcessorNodes
     * @return list of all ProcessorNodes
     */
    @Override
    public List<ProcessorNode> getProcessorChain(){
        return super.getProcessorChain();
    }
}

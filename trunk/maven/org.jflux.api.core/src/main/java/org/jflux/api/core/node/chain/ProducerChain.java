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
import org.jflux.api.core.util.Notifier;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public class ProducerChain<T> extends NodeChain implements ProducerNode<T> {
    
    public <P> ProducerChain(
            ProducerNode<P> producer, List<ProcessorNode<?,?>> chain){
        super(producer, chain);
    }
    
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

    @Override
    public Class<T> getProducedClass() {
        List<ProcessorNode> nodes = getProcessorChain();
        if(nodes != null && !nodes.isEmpty()){
            return nodes.get(nodes.size()-1).getProducedClass();
        }else if(getProducer() != null){
            return getProducer().getProducedClass();
        }
        return null;
    }
    
    @Override
    public ProducerNode getProducer(){
        return super.getProducer();
    }
    
    @Override
    public List<ProcessorNode> getProcessorChain(){
        return super.getProcessorChain();
    }
}

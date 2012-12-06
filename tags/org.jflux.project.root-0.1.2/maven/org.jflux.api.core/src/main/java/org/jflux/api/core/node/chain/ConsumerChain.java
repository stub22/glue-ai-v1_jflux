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
import org.jflux.api.core.node.ConsumerNode;
import org.jflux.api.core.node.ProcessorNode;
import org.jflux.api.core.util.Listener;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class ConsumerChain<T> extends NodeChain implements ConsumerNode<T> {
    
    public <P> ConsumerChain(
            List<ProcessorNode<?,?>> chain, ConsumerNode<P> consumer){
        super(chain, consumer);
    }

    @Override
    public Listener<T> getListener() {
        List<ProcessorNode> nodes = getProcessorChain();
        if(nodes != null && !nodes.isEmpty()){
            return nodes.get(0).getListener();
        }else if(getConsumer() != null){
            return getConsumer().getListener();
        }
        return null;
    }

    @Override
    public Class<T> getConsumedClass() {
        List<ProcessorNode> nodes = getProcessorChain();
        if(nodes != null && !nodes.isEmpty()){
            return nodes.get(0).getConsumedClass();
        }else if(getConsumer() != null){
            return getConsumer().getConsumedClass();
        }
        return null;
    }
    
    @Override
    public ConsumerNode getConsumer(){
        return super.getConsumer();
    }
    
    @Override
    public List<ProcessorNode> getProcessorChain(){
        return super.getProcessorChain();
    }
}